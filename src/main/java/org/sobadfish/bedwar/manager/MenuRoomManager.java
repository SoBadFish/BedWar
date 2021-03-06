package org.sobadfish.bedwar.manager;

import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.utils.Config;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.WorldRoom;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.thread.BaseTimerRunnable;
import org.sobadfish.bedwar.tools.Utils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author SoBadFish
 * 2022/1/12
 */
public class MenuRoomManager {

    private LinkedHashMap<String, WorldRoom> worldRoomLinkedHashMap = new LinkedHashMap<>();


    public MenuRoomManager(Config config){
        read(config);
    }
    private void read(Config config){
        Map map = (Map) config.get("join-menu");
        for(Object os : map.keySet()){
            String oname = os.toString();
            ArrayList<GameRoomConfig> roomConfigs = new ArrayList<>();
            for(String value: config.getStringList("join-menu."+oname+".rooms")){
                if(BedWarMain.getRoomManager().hasRoom(value)){
                    roomConfigs.add(BedWarMain.getRoomManager().getRoomConfig(value));
                }
            }
            ElementButtonImageData elementButtonImageData = new ElementButtonImageData(
                    config.getString("join-menu."+oname+".buttonImg.type"),
                    config.getString("join-menu."+oname+".buttonImg.path"));

            worldRoomLinkedHashMap.put(oname,new WorldRoom(oname,roomConfigs,elementButtonImageData));
        }
    }

    public ArrayList<String> getNames(){
        return new ArrayList<>(worldRoomLinkedHashMap.keySet());
    }

    public WorldRoom getRoom(String name){
        return worldRoomLinkedHashMap.get(name);
    }

    public ArrayList<GameRoomConfig> getRoomByName(String name){
        return worldRoomLinkedHashMap.get(name).getRoomConfigs();
    }

    public String getNameByRoom(GameRoomConfig roomConfig){
        for(Map.Entry<String,WorldRoom> roomEntry : worldRoomLinkedHashMap.entrySet()){
            if(roomEntry.getValue().getRoomConfigs().contains(roomConfig)){
                return roomEntry.getKey();
            }
        }
        return null;

    }

    private ArrayList<PlayerHasChoseRoomManager> lock = new ArrayList<>();



    public synchronized boolean joinRandomRoom(PlayerInfo info,String name){
        PlayerHasChoseRoomManager roomManager = new PlayerHasChoseRoomManager(info);
        if(lock.contains(roomManager)){
            lock.remove(roomManager);
            return false;
        }else{
            lock.add(roomManager);
        }
        info.sendForceTitle("&a?????????..");
        String input = name;
        ArrayList<String> names = BedWarMain.getMenuRoomManager().getNames();
        if(name != null){
            if(!names.contains(name)){
                name = null;
            }else{
                name = names.get(names.indexOf(name));
            }
        }
        while (true) {
            if(roomManager.cancel){
                return false;
            }
            //10?????????????????????
            ThreadManager.addThread(new BaseTimerRunnable(10) {
                @Override
                protected void callback() {
                    if(!lock.contains(roomManager)){
                        roomManager.cancel = true;
                        return;
                    }
                    info.sendForceMessage("&c???????????????????????????");
                    roomManager.cancel = true;
                }
            });
            if (!lock.contains(roomManager)) {
                info.sendForceTitle("????????????!");
                return false;
            }
            if (name == null) {
                for (GameRoom gameRoom : BedWarMain.getRoomManager().getRooms().values()) {
                    if (gameRoom.getType() == GameRoom.GameType.WAIT) {
                        if (gameRoom.joinPlayerInfo(info, false)) {
                            lock.remove(roomManager);
                            return true;
                        }
                    }
                }
                if (names.size() == 0) {
                    info.sendForceMessage("&c???????????????????????????");
                    break;
                }
                name = names.get(0);
                if (names.size() > 1) {
                    name = names.get(Utils.rand(0, names.size() - 1));
                    if (roomManager.hasRoom(name)) {
                        if (roomManager.getStrings().size() == names.size()) {
                            info.sendForceMessage("&c???????????????????????????");
                            break;
                        }
                        continue;
                    } else {
                        roomManager.add(name);
                    }
                }

                WorldRoom worldRoom = BedWarMain.getMenuRoomManager().getRoom(name);
                ArrayList<GameRoomConfig> roomConfigs = worldRoom.getRoomConfigs();
                if (roomConfigs.size() > 0) {
                    GameRoomConfig roomConfig = roomConfigs.get(0);
                    if (roomConfigs.size() > 1) {
                        roomConfig = roomConfigs.get(Utils.rand(0, roomConfigs.size() - 1));
                        if (roomManager.hasRoomName(roomConfig)) {
                            if (input == null && roomManager.getRoomName().size() == roomConfigs.size()) {
                                info.sendForceMessage("&c???????????????????????????");
                                break;
                            }
                            continue;
                        } else {
                            roomManager.addRoom(roomConfig);
                        }
                    }
                    if (BedWarMain.getRoomManager().hasGameRoom(roomConfig.name)) {
                        GameRoom fg = BedWarMain.getRoomManager().getRoom(roomConfig.name);
                        if (fg.joinPlayerInfo(info, false)) {
                            lock.remove(roomManager);
                            return true;
                        }
                    } else {
                        return startGameRoom(info, roomManager, roomConfig);

                    }
                } else {
                    info.sendForceMessage("&c???????????????????????????");
                    break;
                }
            }else{
                if(BedWarMain.getMenuRoomManager().worldRoomLinkedHashMap.containsKey(name)){
                    WorldRoom worldRoom = BedWarMain.getMenuRoomManager().getRoom(name);
                    for(GameRoomConfig roomConfig: worldRoom.getRoomConfigs()){
                        GameRoom room = BedWarMain.getRoomManager().getRoom(roomConfig.name);
                        if(room != null && room.getType() == GameRoom.GameType.WAIT){
                            if (room.joinPlayerInfo(info, false)) {
                                lock.remove(roomManager);
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
                        return startGameRoom(info, roomManager, roomConfig);
                    }



                }else{
                    name = null;
                }


            }
        }
        lock.remove(roomManager);
        return false;

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

    public LinkedHashMap<String, WorldRoom> getWorldRoomLinkedHashMap() {
        return worldRoomLinkedHashMap;
    }
}
