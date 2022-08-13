package org.sobadfish.bedwar.manager;

import java.util.concurrent.*;

/**
 * @author SoBadFish
 * 2022/1/2
 */
public class ThreadManager {

    /**
     * 工具类，构造方法私有化
     */
    private ThreadManager() {
        super();
    }

    // 线程池核心线程数
    private final static Integer COREPOOLSIZE = 5;
    // 最大线程数
    private final static Integer MAXIMUMPOOLSIZE = 10;
    // 空闲线程存活时间
    private final static Integer KEEPALIVETIME = 3 * 60;
    // 线程等待队列
    private static BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(10);
    // 线程池对象
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(COREPOOLSIZE, MAXIMUMPOOLSIZE,
            KEEPALIVETIME, TimeUnit.SECONDS, queue, new ThreadPoolExecutor.AbortPolicy());

    /**
     * 向线程池提交一个任务,返回线程结果
     *
     */
    public static Future<?> submit(Callable<?> r) {
        return threadPool.submit(r);
    }

    /**
     * 向线程池提交一个任务，不关心处理结果
     *
     */
    public static void execute(Runnable r) {
        threadPool.execute(r);
    }

    /**
     * 停止任务
     */
    public static void shutdown() {
        threadPool.shutdown();
    }

    /**
     * 获取当前线程池线程数量
     */
    public static int getSize() {
        return threadPool.getPoolSize();
    }

    /**
     * 获取当前活动的线程数量
     */
    public static int getActiveCount() {
        return threadPool.getActiveCount();
    }


    public static void addThread(Runnable runnable){
        execute(runnable);
    }


}
