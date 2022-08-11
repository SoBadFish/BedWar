package org.sobadfish.bedwar.room.config;

import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Sobadfish
 * 13:37
 */
public class GameRoomEventConfig {


    private final List<GameRoomEventItem> items;

    private GameRoomEventConfig(List<GameRoomEventItem> items){
        this.items = items;
    }

    public List<GameRoomEventItem> getItems() {
        return items;
    }

    public static GameRoomEventConfig getGameRoomEventConfigByFile(File file){
        Config event = new Config(file,Config.YAML);
        List<Map> lists = event.getMapList("eventLists");
        List<GameRoomEventItem> items = new ArrayList<>();
        for(Map map: lists){
            String type = "";
            String disPlay = "";
            int eventTime = 0;
            Object value = null;
            if(map.containsKey("type")){
                type = map.get("type").toString().toLowerCase();
            }
            if(map.containsKey("display")){
                disPlay = TextFormat.colorize('&',map.get("display").toString());
            }

            if(map.containsKey("eventTime")){
                eventTime = Integer.parseInt(map.get("eventTime").toString());
            }
            if(map.containsKey("value")){
                value = map.get("value");
            }
            if(type.equalsIgnoreCase("")){
                continue;
            }
            BedWarMain.sendMessageToConsole("&a装载 &r"+disPlay+"&a 完成");
            items.add(new GameRoomEventItem(type,disPlay,eventTime,value));
        }

        return new GameRoomEventConfig(items);
    }



    public static class GameRoomEventItem{
        public String eventType;

        public String display;

        public int eventTime;

        public Object value;

        private GameRoomEventItem(String type,
                                    String display,
                                    int eventTime,
                                    Object value){
            this.eventType = type;
            this.display = display;

            this.eventTime = eventTime;
            this.value = value;
        }

    }
}
