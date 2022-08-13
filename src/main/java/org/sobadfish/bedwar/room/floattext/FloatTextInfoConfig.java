package org.sobadfish.bedwar.room.floattext;

import cn.nukkit.level.Position;
import org.sobadfish.bedwar.world.WorldInfo;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.util.LinkedHashMap;
import java.util.Map;

public class FloatTextInfoConfig {

    public String name;

    public Position position;

    public String text;

    public FloatTextInfoConfig(String name,Position position,String text){
        this.name = name;
        this.position = position;
        this.text = text;
    }


    public static FloatTextInfoConfig build(Map map){
        String name = "";
        String pos = "";
        String text = "";
        if(map.containsKey("name")){
            name = map.get("name").toString();
        }
        if(map.containsKey("position")){
            pos = map.get("position").toString();
        }
        if(map.containsKey("text")){
            text = map.get("text").toString();
        }
        if(pos.equalsIgnoreCase("") || name.equalsIgnoreCase("")){
            return null;
        }
        Position position = WorldInfoConfig.getPositionByString(pos);
        return new FloatTextInfoConfig(name,position,text);
    }

    public Map<String,Object> toConfig(){
        Map<String,Object> conf = new LinkedHashMap<>();
        conf.put("name", name);
        conf.put("position", WorldInfoConfig.positionToString(position));
        conf.put("text", text);
        return conf;
    }
}
