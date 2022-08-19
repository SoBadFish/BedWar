package org.sobadfish.bedwar.thread;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.BedWarFloatText;
import org.sobadfish.bedwar.entity.ShopVillage;
import org.sobadfish.bedwar.manager.*;
import org.sobadfish.bedwar.manager.data.WorldResetManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.WorldRoom;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.room.floattext.FloatTextInfo;
import org.sobadfish.bedwar.tools.Utils;
import org.sobadfish.bedwar.top.TopItemInfo;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

public class PluginMasterRunnable extends ThreadManager.AbstractBedWarRunnable {

    public long loadTime = 0;

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
        return color+"插件主进程  浮空字数量&7("+ FloatTextManager.floatTextList.size() +") &a"+loadTime+" ms";
    }

    @Override
    public void run() {
        if(isClose){
            ThreadManager.cancel(this);
        }
        long t1 = System.currentTimeMillis();
        if(BedWarMain.getBedWarMain().isDisabled()){
            isClose = true;
            return;
        }
        for (Player player : new ArrayList<>(Server.getInstance().getOnlinePlayers().values())) {
            for (BedWarFloatText floatText : new ArrayList<>(FloatTextManager.floatTextList)) {
                if (floatText == null) {
                    continue;
                }
                if (floatText.isFinalClose) {
                    FloatTextManager.removeFloatText(floatText);
                    continue;
                }
                if(floatText.player.contains(player)){
                    if(!player.getLevel().getFolderName().equalsIgnoreCase(floatText.getPosition().getLevel().getFolderName()) || !player.isOnline()) {
                        if (!floatText.closed) {
                            floatText.close();
                        }
                        floatText.player.remove(player);
                    }
                }
                if(player.getLevel() == floatText.getPosition().getLevel()){
                    floatText.player.add(player);
                }

                floatText.disPlayers();

            }

        }
        worldReset();

        RandomJoinManager.newInstance().playerInfos.removeIf(info -> info.cancel || !joinRandomRoom(info));
        loadTime = System.currentTimeMillis() - t1;
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
        info.sendForceTitle("&6匹配中",4);
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
                    if (gameRoom.joinPlayerInfo(info, false) == GameRoom.JoinType.CAN_JOIN) {
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
                i.name = names.get(Utils.rand(0, names.size()));
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
                    if (fg.joinPlayerInfo(info, false) == GameRoom.JoinType.CAN_JOIN) {
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
                        if (room.joinPlayerInfo(info, false) == GameRoom.JoinType.CAN_JOIN) {
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

    public void worldReset() {
        List<GameRoomConfig> bufferQueue = new ArrayList<>();
        try {
            for(Map.Entry<GameRoomConfig,String> map: WorldResetManager.RESET_QUEUE.entrySet()){
                if (WorldInfoConfig.toPathWorld(map.getKey().getName(), map.getValue())) {
                    BedWarMain.sendMessageToConsole("&a" + map.getKey().getName() + " 地图已还原");
                }
                Server.getInstance().loadLevel(map.getValue());
                BedWarMain.sendMessageToConsole("&r释放房间 " + map.getKey().getName());
                BedWarMain.sendMessageToConsole("&r房间 " + map.getKey().getName() + " 已回收");
                bufferQueue.add(map.getKey());
            }
            //TODO 从列表中移除
            for(GameRoomConfig config: bufferQueue){
                BedWarMain.getRoomManager().getRooms().remove(config.getName());
                RoomManager.LOCK_GAME.remove(config);
                WorldResetManager.RESET_QUEUE.remove(config);
            }
        } catch (Exception e) {
            BedWarMain.sendMessageToConsole("&c释放房间出现了一个小问题，导致无法正常释放,已将这个房间暂时锁定");
        }

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
