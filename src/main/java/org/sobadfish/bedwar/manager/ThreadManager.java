package org.sobadfish.bedwar.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author SoBadFish
 * 2022/1/2
 */
public class ThreadManager {

    private static ExecutorService executor = Executors.newCachedThreadPool();

    public static void addThread(Runnable runnable){
        executor.execute(runnable);
    }



}
