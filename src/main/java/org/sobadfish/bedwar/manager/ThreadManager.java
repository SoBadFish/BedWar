package org.sobadfish.bedwar.manager;

import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.room.GameRoom;

import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;

/**
 * @author SoBadFish
 * 2022/1/2
 */
public class ThreadManager {


    public static final List<AbstractBedWarRunnable> RUNNABLES = new CopyOnWriteArrayList<>();
    /**
     * 工具类，构造方法私有化
     */
    public ThreadManager() {
        ThreadManager.execute(new AbstractBedWarRunnable() {
            @Override
            public GameRoom getRoom() {
                return null;
            }

            @Override
            public String getThreadName() {
                return "线程检测";
            }

            @Override
            public void run() {
                for(;;) {
                    if(isClose){
                        return;
                    }
                    for (AbstractBedWarRunnable runnable : RUNNABLES) {
                        if (runnable.isClose) {
                            threadPool.remove(runnable);
                            RUNNABLES.remove(runnable);
                        }
                    }
                }
            }
        });
    }

    // 线程池核心线程数
    private final static Integer COREPOOLSIZE = 1;
    // 最大线程数
    private final static Integer MAXIMUMPOOLSIZE = Integer.MAX_VALUE;
    // 空闲线程存活时间
    private final static Integer KEEPALIVETIME = 5;

    // 线程池对象
    private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(COREPOOLSIZE, MAXIMUMPOOLSIZE,
            KEEPALIVETIME, TimeUnit.SECONDS,  new SynchronousQueue<Runnable>() ,new ThreadPoolExecutor.AbortPolicy());



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
    private static void execute(AbstractBedWarRunnable r) {

        RUNNABLES.add(r);
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




    public static void addThread(AbstractBedWarRunnable runnable){
        execute(runnable);
    }

    public static String info() {
        StringBuilder builder = new StringBuilder();
        Map<String, List<AbstractBedWarRunnable>> map = getRunnables();
        for(Map.Entry<String, List<AbstractBedWarRunnable>> me : map.entrySet()){
            builder.append("&r").append(me.getKey()).append("\n").append(listToString(me.getValue()));
        }
        String s = builder.toString();
        if("".equalsIgnoreCase(s)){
            return "null";
        }
        return s;
    }

    private static String listToString(List<AbstractBedWarRunnable> runnables){
        StringBuilder s = new StringBuilder();
        for(AbstractBedWarRunnable runnable: runnables){
            s.append("  &r- ").append(runnable.isClose ? "&7" : "&a").append(runnable.getThreadName()).append("\n");
        }
        return s.toString();
    }

    private static Map<String,List<AbstractBedWarRunnable>> getRunnables(){
        LinkedHashMap<String, List<AbstractBedWarRunnable>> threadList = new LinkedHashMap<>();

        for(AbstractBedWarRunnable workerValue: RUNNABLES) {
            GameRoom room = ((AbstractBedWarRunnable) workerValue).getRoom();
            if (room != null) {
                if (!threadList.containsKey(room.getRoomConfig().name)) {
                    threadList.put(room.getRoomConfig().name, new ArrayList<>());
                }
                List<AbstractBedWarRunnable> runnables = threadList.get(room.getRoomConfig().name);
                runnables.add((AbstractBedWarRunnable) workerValue);
                threadList.put(room.getRoomConfig().name, runnables);
            } else {
                String name = "Unknown";
                if (!threadList.containsKey(name)) {
                    threadList.put(name, new ArrayList<>());
                }
                List<AbstractBedWarRunnable> runnables = threadList.get(name);
                runnables.add((AbstractBedWarRunnable) workerValue);
                threadList.put(name, runnables);
            }
        }
        return threadList;
    }


    public abstract static class AbstractBedWarRunnable implements Runnable{

        public boolean isClose;

        abstract public GameRoom getRoom();

        abstract public String getThreadName();

        public boolean isClose() {
            return isClose;
        }
    }



}
