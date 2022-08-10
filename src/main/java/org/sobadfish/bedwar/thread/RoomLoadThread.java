package org.sobadfish.bedwar.thread;

import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

/**
 * @author SoBadFish
 * 2022/1/2
 */
public class RoomLoadThread implements Runnable{

    private GameRoom room;

    public RoomLoadThread(GameRoom room){
        this.room = room;
    }

    @Override
    public void run() {
        while (true){
            if(room.close){
                room = null;
                return;
            }
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
            }
        }

    }
}
