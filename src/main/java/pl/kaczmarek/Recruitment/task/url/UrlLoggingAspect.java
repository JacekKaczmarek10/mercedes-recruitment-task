package pl.kaczmarek.Recruitment.task.url;



import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Logging request and response for every controller http request.
 * In application logs developer can see all data.
 */
@Aspect
@Component
class UrlLoggingAspect {

    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    @Before("execution(* pl.kaczmarek.Recruitment.task.url.UrlController .*(..))")
    public void logRequest(JoinPoint joinPoint) {
        final var attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            final var request = attributes.getRequest();
            logger.info("Request URL: {}", request.getRequestURL().toString());
            logger.info("HTTP Method: {}", request.getMethod());
            logger.info("Arguments: {}", Arrays.toString(joinPoint.getArgs()));
        }
    }

    @AfterReturning(pointcut = "execution(* pl.kaczmarek.Recruitment.task.url.UrlController.*(..))", returning = "result")
    public void logResponse(Object result) {
        if (result instanceof ResponseEntity<?> responseEntity) {
            final var responseBody = responseEntity.getBody();
            logger.info("Response Body: {}", responseBody);
        }
    }
}