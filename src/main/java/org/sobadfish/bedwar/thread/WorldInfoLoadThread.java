package org.sobadfish.bedwar.thread;

import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.world.WorldInfo;

/**
 * @author SoBadFish
 * 2022/1/3
 */
public class WorldInfoLoadThread extends ThreadManager.AbstractBedWarRunnable {

    private String level = "";

    private WorldInfo worldInfo;

    public WorldInfoLoadThread(WorldInfo worldInfo){
        this.worldInfo = worldInfo;
    }


    @Override
    public void run() {
        while (worldInfo != null && !worldInfo.isClose()){
            try{
                level = worldInfo.getConfig().getGameWorld().getFolderName();
                if(isClose){
                    return;
                }
                if(!worldInfo.onUpdate()){
                    worldInfo = null;
                    isClose = true;
                    return;
                }
                try {
                    Thread.sleep(1000 / 20);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isClose = true;
                }
            }catch (Exception e){
                isClose = true;
            }

        }
        isClose = true;
    }

    @Override
    public GameRoom getRoom() {
        if(worldInfo == null){
            return null;
        }
        return worldInfo.getRoom();
    }

    @Override
    public String getThreadName() {
        if(worldInfo == null){
            isClose = true;
            return "终止 "+level+" 地图线程";
        }
        return worldInfo.getConfig().getGameWorld().getFolderName()+" 地图线程";
    }
}
