package org.sobadfish.bedwar.player.team.config;

import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.BlockColor;
import lombok.Data;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.util.LinkedHashMap;
import java.util.Map;




/**
 * @author SoBadFish
 * 2022/1/3
 */
@Data
public class TeamInfoConfig {

    private TeamConfig teamConfig;


    private BlockFace bedFace;
    /**
     * 床坐标
     * */
    private String bedPosition;

    /**
     * 出生坐标
     * */
    private String spawnPosition;

    /**
     * 商店坐标
     * */
    private LinkedHashMap<String, String> village = new LinkedHashMap<>();

    public TeamInfoConfig(TeamConfig teamConfig,String bedPosition,BlockFace bedFace,String spawnPosition){
        this.teamConfig = teamConfig;
        this.bedPosition = bedPosition;
        this.bedFace = bedFace;
        this.spawnPosition = spawnPosition;


    }

    public Position getBedPosition() {
        return WorldInfoConfig.getPositionByString(bedPosition);
    }

    public Position getSpawnPosition() {
        return WorldInfoConfig.getPositionByString(spawnPosition);
    }

    public static TeamInfoConfig getInfoByMap(TeamConfig teamConfig, Map map){
//        Position bedPosition = WorldInfoConfig.getPositionByString();
//        Position spawnPosition = WorldInfoConfig.getPositionByString(.toString());
        BlockFace face = BlockFace.valueOf(map.get("bedFace").toString().toUpperCase());
        LinkedHashMap<String,String> village = new LinkedHashMap<>();
        Map m2 = (Map) map.get("village");
        for(Object o: m2.keySet()){
            village.put(o.toString(),m2.get(o).toString());
        }
        TeamInfoConfig config = new TeamInfoConfig(teamConfig,map.get("bedPosition").toString(),face,map.get("position").toString());
        config.setVillage(village);
        return config;
    }



    public String getName(){
        return teamConfig.getName();
    }

    public String getNameColor(){
        return teamConfig.getNameColor();
    }

    public BlockColor getRgb(){
        return teamConfig.getRgb();
    }

    public LinkedHashMap<String, Object> save(){
        LinkedHashMap<String, Object> config = new LinkedHashMap<>();
        config.put("name",teamConfig.getName());
        config.put("position",spawnPosition);
        config.put("bedPosition",bedPosition);
        config.put("bedFace",bedFace.getName());
        LinkedHashMap<String, Object> vil = new LinkedHashMap<>();
        for(String s: village.keySet()){
            vil.put(s,village.get(s));
        }
        config.put("village",vil);
        return config;
    }
}
