package org.sobadfish.bedwar.item;

import cn.nukkit.utils.Config;
import org.sobadfish.bedwar.item.config.MoneyItemInfoConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author SoBadFish
 * 2022/1/3
 */
public class MoneyItemInfo {
    private final LinkedHashMap<String, MoneyItemInfoConfig> items;

    private MoneyItemInfo(LinkedHashMap<String, MoneyItemInfoConfig> items){
        this.items = items;
    }

    public static MoneyItemInfo getMoneyItemInfoByFile(Config config){
        LinkedHashMap<String, MoneyItemInfoConfig> configLinkedHashMap = new LinkedHashMap<>();
        for(Map<?,?> map:config.getMapList("money")){
            configLinkedHashMap.put(map.get("name").toString(),MoneyItemInfoConfig.getInstance(map));
        }
        return new MoneyItemInfo(configLinkedHashMap);
    }

    public ArrayList<MoneyItemInfoConfig> getItemInfoConfigs(){
        return new ArrayList<>(items.values());
    }

    public ArrayList<String> getNames(){
        return new ArrayList<>(items.keySet());
    }

    public MoneyItemInfoConfig get(String key){
        return items.get(key);
    }

    @Override
    public String toString() {
        return "MoneyItemInfo{" +
                "items=" + items +
                '}';
    }

    public boolean containsKey(String key){
        return items.containsKey(key);
    }

}
