package org.sobadfish.bedwar.manager;

import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.WorldRoom;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.tools.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 房间匹配队列
 * */
public class RandomJoinManager {

    private final List<PlayerHasChoseRoomManager> lock = new CopyOnWriteArrayList<>();

    public static RandomJoinManager joinManager;

    private RandomJoinManager(){}

    public static RandomJoinManager newInstance(){
        if(joinManager == null){
            joinManager = new RandomJoinManager();
        }
        return joinManager;
    }

    public List<IPlayerInfo> playerInfos = new CopyOnWriteArrayList<>();

    //将这个线程缩短为单个
    public void start(){
        ThreadManager.addThread(() -> {
            while (true){
                playerInfos.removeIf(info -> info.cancel || !joinRandomRoom(info));
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    break;
                }

            }
        });
    }

    public boolean join(PlayerInfo info,String name){
        if(info.getGameRoom() != null && info.getGameRoom().getType() != GameRoom.GameType.END){
            return false;
        }
        IPlayerInfo iPlayerInfo = new IPlayerInfo();
        iPlayerInfo.playerInfo = info;
        if(playerInfos.contains(iPlayerInfo)){
            info.sendForceMessage("&b重新开始匹配");
            iPlayerInfo = playerInfos.get(playerInfos.indexOf(iPlayerInfo));
        }

        iPlayerInfo.name = name;
        iPlayerInfo.time = new Date();
        playerInfos.add(iPlayerInfo);
        return true;
    }

    private synchronized boolean joinRandomRoom(IPlayerInfo i){
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
        info.sendSubTitle(info.formatTime((int)(new Date().getTime() - i.time.getTime()) / 1000));

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
        if(new Date().getTime() -  i.time.getTime() > 60 * 1000){
            //一分钟未找到
            if(!lock.contains(roomManager)){
                roomManager.cancel = true;
                return false;
            }
            info.sendForceMessage("&c暂时没有合适的房间");
            roomManager.cancel = true;

        }

        if (!lock.contains(roomManager)) {
            return false;
        }
        if (i.name == null) {
            for (GameRoom gameRoom : BedWarMain.getRoomManager().getRooms().values()) {
                if (gameRoom.getType() == GameRoom.GameType.WAIT) {
                    if (gameRoom.joinPlayerInfo(info, false)) {
                        lock.remove(roomManager);
                        return true;
                    }
                }
            }
            if (names.size() == 0) {
                info.sendForceMessage("&c暂时没有合适的房间");
            }
            i.name = names.get(0);
            if (names.size() > 1) {
                i.name = names.get(Utils.rand(0, names.size() - 1));
                if (roomManager.hasRoom(i.name)) {
                    if (roomManager.getStrings().size() == names.size()) {
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
                        lock.remove(roomManager);
                        return true;
                    }
                } else {
                    return startGameRoom(info, roomManager, roomConfig);

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


    public static class IPlayerInfo{

        private PlayerInfo playerInfo;

        public String name;

        public Date time;

        public boolean cancel;

        public PlayerInfo getPlayerInfo() {
            if(playerInfo != null && playerInfo.getPlayer().isPlayer && !playerInfo.getPlayer().closed){
                return playerInfo;
            }
            cancel = true;
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof IPlayerInfo){
                return ((IPlayerInfo) o).playerInfo.equals(playerInfo);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(playerInfo, name, time, cancel);
        }
    }

}
