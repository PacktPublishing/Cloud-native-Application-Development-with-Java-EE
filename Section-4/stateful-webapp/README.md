# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 4.4: Session replication for stateful Java webapps

Cloud native applications and microservices ought to be stateless. Unfortunately,
some Java EE APIs such as JSF or even JPA are somewhat stateful. In order to use
these in a cloud native environment they require that the state is replicated
between in the instances.

### Step 1: Infrastructure setup

We are going to start the same webapp twice, as two separate instances. Edit your
`docker-compose.yml` and add two services.

```yaml
  stateful-webapp-1:
    build:
      context: .
    image: stateful-webapp:1.0.1
    ports:
    - "18080:8080"
    networks:
    - jee8net

  stateful-webapp-2:
    image: stateful-webapp:1.0.1
    ports:
    - "28080:8080"
    networks:
    - jee8net
```

### Step 2: Make Java webapp distributable

In order for the session replication to work you need to mark it as `<distributable/>` in your `src/main/webapp/WEB-INF/web.xml`.
Create the file and add the following content.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app xmlns="http://xmlns.jcp.org/xml/ns/javaee"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://xmlns.jcp.org/xml/ns/javaee http://xmlns.jcp.org/xml/ns/javaee/web-app_4_0.xsd"
         version="4.0">

    <!-- mark webapp as distributable -->
    <distributable/>

</web-app>
```

### Step 3: Working with HttpSession in JAX-RS

This here is for demo purposes. Try not to use HttpSession in your REST endpoints if possible!
Add the following JAX-RS resource class.

```java
@RequestScoped
@Path("session")
public class SessionResource {

    @Context
    private HttpServletRequest request;

    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAttribute(@PathParam("name") String name) {
        Object payload = Optional.ofNullable(request.getSession().getAttribute(name)).orElseThrow(NotFoundException::new);
        return Response.ok(payload).build();
    }

    @POST
    @Path("/{name}")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response setAttribute(@PathParam("name") String name, String payload) {
        request.getSession().setAttribute(name, payload);
        return Response.ok().build();
    }

}
```

### Step 4: Working with stateful JSF

Add the following JSF declarations to your `web.xml` file.

```xml
    <welcome-file-list>
        <welcome-file>faces/hello.xhtml</welcome-file>
    </welcome-file-list>

    <servlet>
        <servlet-name>Faces Servlet</servlet-name>
        <servlet-class>javax.faces.webapp.FacesServlet</servlet-class>
        <load-on-startup>1</load-on-startup>
    </servlet>

    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>/faces/*</url-pattern>
    </servlet-mapping>

    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.jsf</url-pattern>
    </servlet-mapping>

    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.faces</url-pattern>
    </servlet-mapping>

    <servlet-mapping>
        <servlet-name>Faces Servlet</servlet-name>
        <url-pattern>*.xhtml</url-pattern>
    </servlet-mapping>
```

Now add the following `SessionScoped` backing bean to be used by JSF.

```java
@Named("helloBean")
@SessionScoped
public class HelloBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name = "Stateful Webapp";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
```

Finally, add the following JSF XHTML file to display and set the name in the session scoped
backing bean. Add a `hello.xhtml` file to you `src/main/webapp` folder.

````xhtml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">

<html xmlns:h="http://java.sun.com/jsf/html">
<head>
    <title>Replicated Stateful Webapp</title>
</head>

<body>
<h1>Hello #{helloBean.name}</h1>
<h:form>
    <h:inputText value="#{helloBean.name}"></h:inputText>
    <h:commandButton value="Set name" action="hello"></h:commandButton>
</h:form>
</body>
</html>
````
