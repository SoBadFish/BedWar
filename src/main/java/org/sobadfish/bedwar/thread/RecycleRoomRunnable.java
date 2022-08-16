package org.sobadfish.bedwar.thread;

import cn.nukkit.Server;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.RoomManager;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

public class RecycleRoomRunnable extends ThreadManager.AbstractBedWarRunnable {

    private final GameRoom room;

    private final String roomName;

    public RecycleRoomRunnable(GameRoom room){
        this.room = room;
        this.roomName = room.getRoomConfig().getName();
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
        return color+"回收 "+roomName+" 线程";
    }

    @Override
    public void run() {
        BedWarMain.sendMessageToConsole("&r释放房间 "+room.getRoomConfig().getName());
        if(WorldInfoConfig.toPathWorld(room.getRoomConfig().getName(),room.getRoomConfig().getWorldInfo().getGameWorld().getFolderName())){
            Server.getInstance().loadLevel(room.getRoomConfig().getWorldInfo().getGameWorld().getFolderName());
            BedWarMain.sendMessageToConsole("&a"+room.getRoomConfig().getName()+" 地图已还原");
        }
        room.isGc = true;
        RoomManager.LOCK_GAME.remove(room.getRoomConfig());
        BedWarMain.getRoomManager().getRooms().remove(room.getRoomConfig().getName());


    }
}
