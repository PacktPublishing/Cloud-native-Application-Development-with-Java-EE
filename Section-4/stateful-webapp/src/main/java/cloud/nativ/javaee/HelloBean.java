package cloud.nativ.javaee;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;

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
