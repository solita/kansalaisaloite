package fi.om.initiative.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Resource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Aspect
public class TaskExecutorAspect {

    private final Logger log = LoggerFactory.getLogger(TaskExecutorAspect.class);
    private final AtomicInteger queueLength = new AtomicInteger();
    
    @Resource ExecutorService executorService;
    
    public TaskExecutorAspect() {}
    
    public TaskExecutorAspect(ExecutorService executorService) {
        this.executorService = executorService;
    }
    
    public int getQueueLength() {
        return queueLength.get();
    }

    @Around("execution(public void *(..)) and (@within(fi.om.initiative.util.Task) or @annotation(fi.om.initiative.util.Task))")
    public void executeTask(final ProceedingJoinPoint pjp) {
        queueLength.incrementAndGet(); // Increment queue when submitting task.
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    pjp.proceed();
                } catch (Throwable t) {
                    log.error(t.getMessage(), t);
                }
                queueLength.decrementAndGet(); // Decrement queue when task done.
            }
        });
    }
}
