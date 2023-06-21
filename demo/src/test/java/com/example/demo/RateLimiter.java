package com.example.demo;


import org.junit.Test;

import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class RateLimiter {
/*   Rate limiter with rate-limit aware backoff and retry.

   Write a program that enforces rate limits over a

   On the function caller side, if this results in a rate_limit_exceeded, handle that case - back-off and retry the operation after a certain time.
   No database/external calls, store everything in memory.
   Accept configuration for rate limit thresholds etc via environment variables.
*/


    //Rate limiting response as it is asked by the requirement valid response, a logical error, or a rate_limit_exceeded error.
    public class RateLimitResponse<T> {
        T response;
        String error;

        public RateLimitResponse(T t, String error) {
            this.response = t;
            this.error = error;
        }
    }

    //Workload of the task
    public abstract class Task<T> implements Callable<RateLimitResponse<T>> {

        public abstract RateLimitResponse<T> doTask() throws InterruptedException;

        public RateLimitResponse<T> call() throws Exception {
            return this.doTask();
        }
    }

    // sample null implementation
    public class MyTask<T> extends Task<T> {
        private Random random = new Random();
        private RateLimitResponse<T> result;

        @Override
        public RateLimitResponse<T> doTask() throws InterruptedException {
            return result;
        }

        private void setTypeValue(RateLimitResponse<T> value) {
            this.result = value;
        }

    }

    // RateLimiter Interface
    public interface RateLimiters<T> {

        // in impletation we will have validation as per implementation of user
        public void setLimitRateConfig(long config);

        //  The result of calling this function can either be a valid response, a logical error, or a rate_limit_exceeded error.
        public RateLimitResponse<T> limitRate(Task task) throws InterruptedException, ExecutionException;

    }


    //  My RateLimiter implemetaion
    public class MyRateLimiter<T> implements RateLimiters<T> {

        ExecutorCompletionService exec = new ExecutorCompletionService(new ThreadPoolExecutor(1, 1, 0, TimeUnit.DAYS, new LinkedBlockingQueue<>(1000)));
        AtomicInteger counter = new AtomicInteger();
        AtomicLong time = new AtomicLong(System.currentTimeMillis());

        Long rate = 0L;
        Long BACKOFF = 1000L;

        Long RESET_TIME_WINDOW_MILLIS = 8000L;

        @Override
        public void setLimitRateConfig(long rps) {
            this.rate = rps;
        }

        private void reset() {
            System.out.println("Resetting window");
            counter.set(0);
            time.set((System.currentTimeMillis() + this.time.get()) / 2);
        }

        @Override
        public RateLimitResponse<T> limitRate(Task task) throws InterruptedException, ExecutionException {
            Object t = null;
            // reset after every minute
            if (System.currentTimeMillis() - this.time.get() > RESET_TIME_WINDOW_MILLIS) {
                this.reset();
            }
            this.counter.incrementAndGet();
            if (!rateExceeded()) {
                return task.doTask();
            } else {
                System.out.println("Rate Execced");
                Thread.sleep(BACKOFF);
                return new RateLimitResponse<T>(null, null);
            }

        }

        public boolean rateExceeded() {
            System.out.println("Queue: " + this.counter.get());
            long timeLapsed = (System.currentTimeMillis() - this.time.get() + 1);
            System.out.println("Rate/Second since Last reset:" + this.counter.get() * 1000L / timeLapsed);
            return this.counter.get() * 1000L / timeLapsed > this.rate;

        }

    }

    @Test
    public void rateLimter() throws InterruptedException, ExecutionException {
        // Rate limiter of functions that return string
        MyRateLimiter<String> ratelimiter = new MyRateLimiter<>();
        long time = System.currentTimeMillis();
        ratelimiter.setLimitRateConfig(50L);
        for (int i = 0; i < 1000; i++) {
            // Task that returns string
            MyTask<String> mytask = new MyTask<>();

            ratelimiter.limitRate(mytask);
        }
        System.out.println(System.currentTimeMillis() - time);

    }
}