package cloud.nativ.javaee;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;

@Logged
@Interceptor
public class LoggedInterceptor {

    @AroundInvoke
    public Object logMethodEntry(InvocationContext invocationContext) throws Exception {

        String methodName = invocationContext.getMethod().getName();
        String declaringClass = invocationContext.getMethod().getDeclaringClass().getName();

        Logger logger = LoggerFactory.getLogger(invocationContext.getMethod().getDeclaringClass());
        logger.trace("Entering method {}#{}", declaringClass, methodName);

        try {
            return invocationContext.proceed();
        } finally {
            logger.trace("Leaving method {}#{}", declaringClass, methodName);
        }
    }

}
