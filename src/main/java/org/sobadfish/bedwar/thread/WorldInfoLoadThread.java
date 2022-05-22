package org.sobadfish.bedwar.thread;

import org.sobadfish.bedwar.world.WorldInfo;

/**
 * @author SoBadFish
 * 2022/1/3
 */
public class WorldInfoLoadThread implements Runnable{

    private WorldInfo worldInfo;

    public WorldInfoLoadThread(WorldInfo worldInfo){
        this.worldInfo = worldInfo;
    }


    @Override
    public void run() {
        while (worldInfo != null && !worldInfo.isClose()){
            if(!worldInfo.onUpdate()){
                worldInfo = null;
                return;
            }
            try {
                Thread.sleep(1000 / 20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
