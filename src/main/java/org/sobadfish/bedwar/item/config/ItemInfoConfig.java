package org.sobadfish.bedwar.item.config;

import cn.nukkit.level.Position;
import lombok.Getter;
import lombok.Setter;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.item.MoneyItemInfo;
import org.sobadfish.bedwar.tools.Utils;
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

    @Getter
    private final MoneyItemInfoConfig moneyItemInfoConfig;

    @Getter
    @Setter
    private int spawnTick;

    private final ArrayList<String> positions;


    public ItemInfoConfig(MoneyItemInfoConfig moneyItemInfoConfig,ArrayList<String> positions,int spawnTick){
        this.moneyItemInfoConfig = moneyItemInfoConfig;
        this.positions = positions;
        this.spawnTick = spawnTick;
    }

    public ArrayList<Position> getPositions() {
        ArrayList<Position> list = new ArrayList<>();
        for(String sl: positions){
            list.add(WorldInfoConfig.getPositionByString(sl));
        }
        return list;
    }



    public static ItemInfoConfig getItemInfoConfig(MoneyItemInfo moneyItemInfo, String name, Map<?,?> map){
        int spawnTick = Utils.formatSecond(3);
        try {
            spawnTick = Integer.parseInt(map.get("spawnTick").toString());
        }catch (Exception e){
            BedWarMain.sendMessageToConsole("&c由于一个未知问题导致 spawnTick 无法转为 int 。传入的参数 "+map.get("spawnTIck").toString());

        }
        if(spawnTick <= 5){
            //太快的话会影响服务器效率
            spawnTick = 10;

        }

        Object oList = map.get("position");
        if(oList instanceof List){
            return new ItemInfoConfig(moneyItemInfo.get(name),getPositionByList((List<?>) oList),spawnTick);
        }
        return null;
    }




    private static ArrayList<String> getPositionByList(List<?> list){
        ArrayList<String> positions = new ArrayList<>();
        for(Object ostr : list){
            positions.add(ostr.toString());
        }
        return positions;
    }



    public LinkedHashMap<String, Object> save(){
        LinkedHashMap<String, Object> config = new LinkedHashMap<>();

        config.put("spawnTick",spawnTick);
        config.put("position",positions);
        return config;
    }

}
