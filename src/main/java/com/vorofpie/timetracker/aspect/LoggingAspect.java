package com.vorofpie.timetracker.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Aspect for logging execution of service layer methods.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * Log method entry with arguments.
     *
     * @param joinPoint the join point
     */
    @Before("execution(* com.vorofpie.timetracker.service.*.*(..))")
    public void logBefore(JoinPoint joinPoint) {
        log.info("Entering method: {} with arguments: {}",
                joinPoint.getSignature(),
                Arrays.toString(joinPoint.getArgs())
        );
    }

    /**
     * Log method exit with result.
     *
     * @param joinPoint the join point
     * @param result the result returned by the method
     */
    @AfterReturning(
            pointcut = "execution(* com.vorofpie.timetracker.service.*.*(..))",
            returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("Exiting method: {} with result: {}",
                joinPoint.getSignature(),
                result
        );
    }

    /**
     * Log exceptions thrown by methods.
     *
     * @param joinPoint the join point
     * @param exception the exception thrown by the method
     */
    @AfterThrowing(
            pointcut = "execution(* com.vorofpie.timetracker.service.*.*(..))",
            throwing = "exception"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        log.error("Exception in method: {} with message: {}",
                joinPoint.getSignature(),
                exception.getMessage()
        );
    }
}
