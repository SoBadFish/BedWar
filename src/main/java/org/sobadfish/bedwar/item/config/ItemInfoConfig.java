package org.sobadfish.bedwar.item.config;

import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.item.MoneyItemInfo;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 货币
 * @author SoBadFish
 * 2022/1/2
 */

public class ItemInfoConfig {

    private final MoneyItemInfoConfig moneyItemInfoConfig;

    private int spawnTick;

    private ArrayList<Position> positions;


    public ItemInfoConfig(MoneyItemInfoConfig moneyItemInfoConfig,ArrayList<Position> positions,int spawnTick){
        this.moneyItemInfoConfig = moneyItemInfoConfig;
        this.positions = positions;
        this.spawnTick = spawnTick;
    }

    public ArrayList<Position> getPositions() {
        return positions;
    }

    public int getSpawnTick() {
        return spawnTick;
    }

    public MoneyItemInfoConfig getMoneyItemInfoConfig() {
        return moneyItemInfoConfig;
    }

    public static ItemInfoConfig getItemInfoConfig(MoneyItemInfo moneyItemInfo, String name, Map map){
        int spawnTick = Integer.parseInt(map.get("spawnTick").toString());
        Object oList = map.get("position");
        if(oList instanceof List){
            return new ItemInfoConfig(moneyItemInfo.get(name),getPositionByList((List) oList),spawnTick);
        }
        return null;
    }

    public void setPositions(ArrayList<Position> positions) {
        this.positions = positions;
    }

    public void setSpawnTick(int spawnTick) {
        this.spawnTick = spawnTick;
    }

    private static ArrayList<Position> getPositionByList(List list){
        ArrayList<Position> positions = new ArrayList<>();
        for(Object ostr : list){
            positions.add(WorldInfoConfig.getPositionByString(ostr.toString()));
        }
        return positions;
    }



    public LinkedHashMap<String, Object> save(){
        LinkedHashMap<String, Object> config = new LinkedHashMap<>();
        ArrayList<String> pos = new ArrayList<>();
        for(Position position: positions){
            pos.add(WorldInfoConfig.positionToString(position));
        }
        config.put("spawnTick",spawnTick);
        config.put("position",pos);
        return config;
    }

}
