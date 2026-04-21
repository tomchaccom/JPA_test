package test.jpa.study.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Aspect
@Component
public class TimeTraceAop {

    private static final Logger log = LoggerFactory.getLogger(TimeTraceAop.class);

    // 서비스 계층의 모든 메소드를 타겟으로 설정합니다.
    @Around("execution(* test.jpa.study.service..*(..))")
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        // 어떤 클래스의 어떤 메소드가 실행되는지 로깅
        String methodName = joinPoint.getSignature().toShortString();
        log.info("==== START: {} ====", methodName);

        try {
            // 실제 메소드(타겟) 실행
            return joinPoint.proceed();
        } finally {
            stopWatch.stop();
            // N+1 문제 등으로 인한 시간 지연 등을 확인할 수 있도록 실행 시간 로깅
            log.info("==== END: {} ({}ms) ====", methodName, stopWatch.getTotalTimeMillis());
        }
    }
}
