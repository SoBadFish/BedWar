package org.sobadfish.bedwar.thread;

import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

/**
 * @author SoBadFish
 * 2022/1/2
 */
public class RoomLoadThread extends ThreadManager.AbstractBedWarRunnable {

    private GameRoom room;

    private String roomName = "";

    public RoomLoadThread(GameRoom room){
        this.room = room;
    }

    @Override
    public void run() {
        while (true){
            if(room.close || isClose){
                room = null;
                return;
            }
            roomName = room.getRoomConfig().name;
            room.onUpdate();
            for(PlayerInfo playerInfo:room.getPlayerInfos()){
                if(playerInfo.cancel || playerInfo.isLeave){
                    playerInfo.removeScoreBoard();
                    continue;
                }
                playerInfo.onUpdate();
            }
            if(room.loadTime > 0) {
                if(!room.getEventControl().hasEvent()){
                    room.loadTime--;
                }

            }


            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                isClose = true;
            }
        }

    }

    @Override
    public GameRoom getRoom() {
        return room;
    }

    @Override
    public String getName() {
        if(room == null){
            isClose = true;
            return "终止的"+roomName+"游戏线程";
        }
        return room.getRoomConfig().name+" 游戏线程";
    }
}
