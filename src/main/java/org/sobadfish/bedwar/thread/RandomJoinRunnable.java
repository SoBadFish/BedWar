package org.sobadfish.bedwar.thread;

import cn.nukkit.Player;
import cn.nukkit.Server;
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

/**
 * @author Sobadfish
 * 21:57
 */
public class RandomJoinRunnable extends ThreadManager.AbstractBedWarRunnable {
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
        return color+"匹配玩家线程 &7("+ RandomJoinManager.newInstance().playerInfos.size()+")";
    }

    @Override
    public void run() {
        try {
            int size = RandomJoinManager.newInstance().playerInfos.size();
            List<RandomJoinManager.IPlayerInfo> copy = new ArrayList<>(RandomJoinManager.newInstance().playerInfos);
            for (int i = 0; i < size; i++) {
                RandomJoinManager.IPlayerInfo iPlayerInfo = copy.get(i);
                if (joinRandomRoom(iPlayerInfo)) {
                    RandomJoinManager.newInstance().playerInfos.remove(iPlayerInfo);
                }
            }
            for(RandomJoinManager.IPlayerInfo info: RandomJoinManager.newInstance().playerInfos){
                if(info.cancel){
                    RandomJoinManager.newInstance().playerInfos.remove(info);
                    continue;
                }
                info.getPlayerInfo().sendSubTitle(Utils.formatTime1((int)((System.currentTimeMillis() - info.time ))));
                if(!joinRandomRoom(info)){
                    if(info.isNext){
                        if(info.getPlayerInfo().getPlayer() instanceof Player) {
                            info.getPlayerInfo().clear();
                            info.getPlayerInfo().getPlayer().teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
                        }
                    }
                    RandomJoinManager.newInstance().playerInfos.remove(info);

                }
            }
            RandomJoinManager.newInstance().playerInfos.removeIf(info -> info.cancel || !joinRandomRoom(info));

        }catch (Exception e){
            BedWarMain.printMessageException(e);
        }
    }
    public boolean joinRandomRoom(RandomJoinManager.IPlayerInfo i){
        if(i == null){
            return true;
        }
        PlayerInfo info = i.getPlayerInfo();
        if(info == null){
            return true;
        }
        PlayerHasChoseRoomManager roomManager = new PlayerHasChoseRoomManager(i);
        if(lock.contains(roomManager)){
            roomManager = lock.get(lock.indexOf(roomManager));
        }else{
            lock.add(roomManager);
        }
        if(roomManager.cancel){
            return true;
        }
        info.sendForceTitle(BedWarMain.getLanguage().getLanguage("matching-loading","&6匹配中"),100);
        info.sendSubTitle(Utils.formatTime1((int)(System.currentTimeMillis() - i.time) / 1000));

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
            info.sendForceTitle(BedWarMain.getLanguage().getLanguage("matching-disable","匹配终止!"));
            return true;
        }
        if(System.currentTimeMillis() -  i.time > (60 * 1000)){
            //一分钟未找到
            info.sendForceMessage(BedWarMain.getLanguage().getLanguage("matching-error-room","&c暂时没有合适的房间"));
            roomManager.cancel = true;
            return true;


        }

        if (!lock.contains(roomManager)) {
            return true;
        }
        if (i.name == null) {
            for (GameRoom gameRoom : BedWarMain.getRoomManager().getRooms().values()) {
                if (gameRoom.getType() == GameRoom.GameType.WAIT) {
                    if (gameRoom.joinPlayerInfo(info, true) == GameRoom.JoinType.CAN_JOIN) {
                        i.cancel = true;
                        lock.remove(roomManager);
                        return true;
                    }
                }
            }
            if (names.isEmpty()) {
                info.sendForceMessage(BedWarMain.getLanguage().getLanguage("matching-error-room","&c暂时没有合适的房间"));
                i.cancel = true;
                return true;
            }
            i.name = names.get(0);
            if (names.size() > 1) {
                int r = Utils.rand(0, names.size());
                if(r >= names.size()){
                    r = names.size() - 1;
                }
                i.name = names.get(r);
                if (roomManager.hasRoom(i.name)) {
                    if (roomManager.getStrings().size() == names.size()) {
                        return false;
                    }
                } else {
                    roomManager.add(i.name);
                }
            }

            WorldRoom worldRoom = BedWarMain.getMenuRoomManager().getRoom(i.name);
            ArrayList<GameRoomConfig> roomConfigs = worldRoom.getRoomConfigs();
            if (!roomConfigs.isEmpty()) {
                GameRoomConfig roomConfig = roomConfigs.get(0);
                if (roomConfigs.size() > 1) {
                    roomConfig = roomConfigs.get(Utils.rand(0, roomConfigs.size() - 1));
                    if (roomManager.hasRoomName(roomConfig)) {
                        if (input == null && roomManager.getRoomName().size() == roomConfigs.size()) {
                            return false;
                        }
                    } else {
                        roomManager.addRoom(roomConfig);
                    }
                }
                if (BedWarMain.getRoomManager().hasGameRoom(roomConfig.name)) {
                    GameRoom fg = BedWarMain.getRoomManager().getRoom(roomConfig.name);
                    if (fg.joinPlayerInfo(info, false) == GameRoom.JoinType.CAN_JOIN) {
                        info.sendForceTitle(BedWarMain.getLanguage().getLanguage("matching-finish","&a匹配完成"),100);
                        i.cancel = true;
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
                        if (room.joinPlayerInfo(info, false) == GameRoom.JoinType.CAN_JOIN) {
                            lock.remove(roomManager);
                            info.sendForceTitle(BedWarMain.getLanguage().getLanguage("matching-finish","&a匹配完成"),100);
                            i.cancel = true;
                            return true;
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
                        info.sendForceTitle(BedWarMain.getLanguage().getLanguage("matching-finish","&a匹配完成"),100);
                        i.cancel = true;
                        return true;
                    }
                }

            }else{
                i.name = null;
            }
        }
        return false;
    }




    private final List<PlayerHasChoseRoomManager> lock = new CopyOnWriteArrayList<>();

    private boolean startGameRoom(PlayerInfo info, PlayerHasChoseRoomManager roomManager, GameRoomConfig roomConfig) {

        if(BedWarMain.getRoomManager().enableRoom(BedWarMain.getRoomManager().getRoomConfig(roomConfig.name))){
            if (BedWarMain.getRoomManager().getRoom(roomConfig.name).joinPlayerInfo(info, true) == GameRoom.JoinType.CAN_JOIN) {
                lock.remove(roomManager);
                return true;
            } else {
                lock.remove(roomManager);
                return false;
            }
        }
        return false;

    }
}
