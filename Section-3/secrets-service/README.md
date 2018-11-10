# Video Instructions

For each video, there is a Git branch with a matching name that acts as a
starting point.

## Video 3.2: Handling secrets in Cloud-native Java EE microservices

Secrets are sensitive information and configuration properties and thus deserve
special treatment. They should never be version controlled, and provided dynamically
either via ENV variable or file leveraging the Kubernetes Secrets mechanism.

### Step 1: Secure usage of ENV variables

Create a `.env` file for local development, make sure to never commit this
file into version control.

```
ENV_USER_NAME=secure-env-user
ENV_USER_PASSWORD=secure-env-password
```

We running your container with Docker Compose, edit you `docker-compose.yml` and
add the envrionment variables without value.
```yaml
environment:
 - ENV_USER_NAME
 - ENV_USER_PASSWORD
```

You can now access the environment variables using MicroProfile Config API as usual.
```java
@Inject
@ConfigProperty(name = "env.user.name")
private Provider<String> envUsername;

@Inject
@ConfigProperty(name = "env.user.password")
private Provider<String> envPassword;
```

### Step 2: Secure usage of secrets files

Another popular option is to store secrets as individual files and mount these into a container.
Extend the `docker-compose.yaml` with the following:

```yaml
volumes:
  - ./src/test/resources:/secrets:ro
```

Next, we need to configure Payara to pick up the secrets files from this location. If you are using a different app
server this may vary.
```
set-config-secrets-dir --directory=/secrets
```

Now you can access the secrets files using normal MicroProfile Config API. The important bit is that the file name
needs to correspond to the properties name.
```java
@Inject
@ConfigProperty(name = "secret.user.name")
private Provider<String> secretUsername;

@Inject
@ConfigProperty(name = "secret.user.password")
private Provider<String> secretPassword;
```


### Step 3: Using Secrets in Kubernetes

First we need encode the secret user name and password in the console.

```
$ echo -n 'secret-agent' | base64
c2VjcmV0LWFnZW50
$ echo -n 'jamesbond007' | base64
amFtZXNib25kMDA3
```

Next, we create a Kubernetes YAML definition of a Secret in

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: super-secret
type: Opaque
data:
  secret.user.name: c2VjcmV0LWFnZW50
  secret.user.password: amFtZXNib25kMDA3
```

You now have two options to use the Secret in Kubernetes: mount the secrets as ENV variables (see Step 1) or mount the
secrets as files (see Step 2). To use these, create a file `src/main/kubernetes/secrets-service-deployment.yaml`.

```yaml
apiVersion: extensions/v1beta1
kind: Deployment
metadata:
  labels:
    io.kompose.service: secrets-service
  name: secrets-service
spec:
  replicas: 1
  template:
    metadata:
      labels:
        io.kompose.service: secrets-service
    spec:
      containers:
      - image: secrets-service:1.0.1
        name: secrets-service
        ports:
        - containerPort: 8080
        # injecting secrets as ENV variables
        env:
        - name: ENV_USER_NAME
          valueFrom:
            secretKeyRef:
              name: super-secret
              key: secret.user.name
        - name: ENV_USER_PASSWORD
          valueFrom:
            secretKeyRef:
              name: super-secret
              key: secret.user.password
        # injecting secrets as files in a directory
        volumeMounts:
        - mountPath: /secrets
          name: super-secret
          readOnly: true
      restartPolicy: Always
      volumes:
      - name: super-secret
        secret:
          secretName: super-secret
```
