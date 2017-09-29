package com.returntrue.util;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class ThreadPoolUtil
{
    private static int CORE_POOL_SIZE = 20;
    private static int MAX_POOL_SIZE = 100;
    private static int KEEP_ALIVE_TIME = 100;
    public static int MAX_RETRY_TIME = 15000;
    public static int RETRY_INTERVAL = 10;

    private static BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(20);

    private static ThreadFactory threadFactory = new ThreadFactory()
    {
        private final AtomicInteger integer = new AtomicInteger();

        @Override
        public Thread newThread(Runnable r)
        {
            return new Thread(r, "myThreadPool thread:" + integer.getAndIncrement());
        }
    };

    private static ThreadPoolExecutor threadPool;

    static
    {
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,
                                            MAX_POOL_SIZE,
                                            KEEP_ALIVE_TIME,
                                            TimeUnit.SECONDS,
                                            workQueue,
                                            threadFactory);
    }

    public static void execute(Runnable runnable)
    {
        try
        {
            if (runnable != null)
            {
                threadPool.execute(runnable);
            }
        }
        catch (Exception e)
        {
            JLog.e(JLog.JosephWang, e.getMessage(), e);
        }
    }

    public static void shotDown()
    {
        if (threadPool != null)
        {
            threadPool.shutdown();
        }
        threadPool = new ThreadPoolExecutor(CORE_POOL_SIZE,
                                            MAX_POOL_SIZE,
                                            KEEP_ALIVE_TIME,
                                            TimeUnit.SECONDS,
                                            workQueue,
                                            threadFactory);
    }

    public static void remove(Runnable runnable)
    {
        try
        {
            if (runnable != null)
            {
                threadPool.remove(runnable);
            }
        }
        catch (Exception e)
        {
            JLog.e(JLog.JosephWang, e.getMessage(), e);
        }
    }

    public static void delay(long startTime, long delayTime)
    {
        while (System.currentTimeMillis() - startTime < delayTime)
        {
            Thread.yield();
        }
    }
}
