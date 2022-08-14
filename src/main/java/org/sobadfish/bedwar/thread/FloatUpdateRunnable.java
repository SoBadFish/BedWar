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

    private List<FloatTextInfo> floatTextInfos = new ArrayList<>();

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
        return room.getRoomConfig().name+" 浮空字更新";
    }

    @Override
    public void run() {
        //TODO 更新浮空字
        while (true){
            if(room.close){
                isClose = true;
                return;
            }
            for(FloatTextInfo floatTextInfo:floatTextInfos){
                floatTextInfo.stringUpdate(room);
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
