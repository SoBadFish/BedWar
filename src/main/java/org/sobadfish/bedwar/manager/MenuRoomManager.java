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

    public LinkedHashMap<String, WorldRoom> worldRoomLinkedHashMap = new LinkedHashMap<>();


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




    public LinkedHashMap<String, WorldRoom> getWorldRoomLinkedHashMap() {
        return worldRoomLinkedHashMap;
    }
}
