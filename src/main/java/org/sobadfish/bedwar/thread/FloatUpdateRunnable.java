package org.sobadfish.bedwar.thread;

import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.floattext.FloatTextInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sobadfish
 * 16:24
 */
public class FloatUpdateRunnable extends ThreadManager.AbstractBedWarRunnable {

    private List<FloatTextInfo> floatTextInfos;

    private GameRoom room;

    public FloatUpdateRunnable(GameRoom room,List<FloatTextInfo> floatTextInfos){
        this.floatTextInfos = floatTextInfos;
        this.room = room;
    }

    @Override
    public GameRoom getRoom() {
        return room;
    }

    @Override
    public String getThreadName() {
        String color = "&a";
        if(isClose){
            color = "&7";
        }
        return color+room.getRoomConfig().name+" 浮空字更新";
    }

    @Override
    public void run() {
        //TODO 更新浮空字
        while (true){
            if(room == null){
                isClose = true;
                return;
            }
            if(room.close){
                isClose = true;
                return;
            }
            for(FloatTextInfo floatTextInfo:floatTextInfos){
                if(!floatTextInfo.stringUpdate(room)){
                    isClose = true;
                    break;
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                isClose = true;
                break;
            }
        }
    }
}
