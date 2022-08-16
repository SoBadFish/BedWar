package org.sobadfish.bedwar.thread;

import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.PlayerHasChoseRoomManager;
import org.sobadfish.bedwar.manager.RandomJoinManager;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.WorldRoom;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.tools.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class JoinListRunnable extends ThreadManager.AbstractBedWarRunnable{

    private final RandomJoinManager manager;

    private final List<PlayerHasChoseRoomManager> lock = new CopyOnWriteArrayList<>();


    public JoinListRunnable(RandomJoinManager manager){
        this.manager = manager;
    }

    @Override
    public GameRoom getRoom() {
        return null;
    }

    @Override
    public String getThreadName() {
        String color = "&a";
        if(isClose){
            color = "&7";
        }
        return color+"匹配玩家队列线程";
    }

    @Override
    public void run() {
        while (true){
            manager.playerInfos.removeIf(info -> info.cancel || !joinRandomRoom(info));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                isClose = true;
                break;
            }

        }
    }
    public synchronized boolean joinRandomRoom(RandomJoinManager.IPlayerInfo i){
        if(i == null){
            return false;
        }
        PlayerInfo info = i.getPlayerInfo();
        if(info == null){
            return false;
        }
        PlayerHasChoseRoomManager roomManager = new PlayerHasChoseRoomManager(i);
        if(lock.contains(roomManager)){
            roomManager = lock.get(lock.indexOf(roomManager));
        }else{
            lock.add(roomManager);
        }
        if(roomManager.cancel){
            return false;
        }
        info.sendForceTitle("&6匹配中",2);
        info.sendSubTitle(PlayerInfo.formatTime((int)(System.currentTimeMillis() - i.time.getTime()) / 1000));

        String input = i.name;
        ArrayList<String> names = BedWarMain.getMenuRoomManager().getNames();
        if(i.name != null){
            if(!names.contains(i.name)){
                input = null;
            }else{
                input = names.get(names.indexOf(i.name));
            }
        }
        if(roomManager.cancel){
            info.sendForceTitle("匹配终止!");
            return false;
        }
        if(System.currentTimeMillis() -  i.time.getTime() > 60 * 1000){
            //一分钟未找到
            info.sendForceMessage("&c暂时没有合适的房间");
            roomManager.cancel = true;
            return false;


        }

        if (!lock.contains(roomManager)) {
            return false;
        }
        if (i.name == null) {
            for (GameRoom gameRoom : BedWarMain.getRoomManager().getRooms().values()) {
                if (gameRoom.getType() == GameRoom.GameType.WAIT) {
                    if (gameRoom.joinPlayerInfo(info, false)) {
                        i.cancel = true;
                        lock.remove(roomManager);
                        return false;
                    }
                }
            }
            if (names.size() == 0) {
                info.sendForceMessage("&c暂时没有合适的房间");
                i.cancel = true;
                return false;
            }
            i.name = names.get(0);
            if (names.size() > 1) {
                i.name = names.get(Utils.rand(0, names.size() - 1));
                if (roomManager.hasRoom(i.name)) {
                    if (roomManager.getStrings().size() == names.size()) {
                        i.cancel = true;
                        return true;
                    }
                } else {
                    roomManager.add(i.name);
                }
            }

            WorldRoom worldRoom = BedWarMain.getMenuRoomManager().getRoom(i.name);
            ArrayList<GameRoomConfig> roomConfigs = worldRoom.getRoomConfigs();
            if (roomConfigs.size() > 0) {
                GameRoomConfig roomConfig = roomConfigs.get(0);
                if (roomConfigs.size() > 1) {
                    roomConfig = roomConfigs.get(Utils.rand(0, roomConfigs.size() - 1));
                    if (roomManager.hasRoomName(roomConfig)) {
                        if (input == null && roomManager.getRoomName().size() == roomConfigs.size()) {
                            return true;
                        }
                    } else {
                        roomManager.addRoom(roomConfig);
                    }
                }
                if (BedWarMain.getRoomManager().hasGameRoom(roomConfig.name)) {
                    GameRoom fg = BedWarMain.getRoomManager().getRoom(roomConfig.name);
                    if (fg.joinPlayerInfo(info, false)) {
                        info.sendForceTitle("&a匹配完成");
                        i.cancel = true;
                        lock.remove(roomManager);

                        return false;
                    }
                } else {
                    return !startGameRoom(info, roomManager, roomConfig);

                }
            }
        }else{
            if(BedWarMain.getMenuRoomManager().worldRoomLinkedHashMap.containsKey(i.name)){
                WorldRoom worldRoom = BedWarMain.getMenuRoomManager().getRoom(i.name);
                for(GameRoomConfig roomConfig: worldRoom.getRoomConfigs()){
                    GameRoom room = BedWarMain.getRoomManager().getRoom(roomConfig.name);
                    if(room != null && room.getType() == GameRoom.GameType.WAIT){
                        if (room.joinPlayerInfo(info, false)) {
                            lock.remove(roomManager);
                            info.sendForceTitle("&a匹配完成");
                            i.cancel = true;
                            return false;
                        }
                    }else if(room != null){
                        roomManager.addRoom(roomConfig);
                    }
                }
                for(GameRoomConfig roomConfig: worldRoom.getRoomConfigs()){
                    if(roomManager.hasRoomName(roomConfig)){
                        continue;
                    }
                    if(startGameRoom(info, roomManager, roomConfig)){
                        info.sendForceTitle("&a匹配完成");
                        i.cancel = true;
                        return false;
                    }
                }

            }else{
                i.name = null;
            }
        }
        return true;
    }

    private boolean startGameRoom(PlayerInfo info, PlayerHasChoseRoomManager roomManager, GameRoomConfig roomConfig) {
        BedWarMain.getRoomManager().enableRoom(BedWarMain.getRoomManager().getRoomConfig(roomConfig.name));
        if (BedWarMain.getRoomManager().getRoom(roomConfig.name).joinPlayerInfo(info, true)) {
            lock.remove(roomManager);
            return true;
        } else {
            lock.remove(roomManager);
            return false;
        }
    }


}
