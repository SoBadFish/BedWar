package org.sobadfish.bedwar.item;


import cn.nukkit.utils.Config;

import org.sobadfish.bedwar.item.config.NbtItemInfoConfig;


import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author SoBadFish
 * 2022/1/5
 */
public class NbtItemInfo {

    public LinkedHashMap<String, NbtItemInfoConfig> items;

    private NbtItemInfo(LinkedHashMap<String, NbtItemInfoConfig> items){
        this.items = items;
    }

    public static NbtItemInfo getNbtItemInfoByFile(Config config){
        LinkedHashMap<String, NbtItemInfoConfig> configLinkedHashMap = new LinkedHashMap<>();

        Map<String, Object> map = config.get("nbtItem", new LinkedHashMap<>());
        for(Map.Entry<String, Object> configValue : map.entrySet()){
            NbtItemInfoConfig config1 = NbtItemInfoConfig.build(configValue.getKey(), (Map) configValue.getValue());
            if(config1 == null){
                continue;
            }
            configLinkedHashMap.put(configValue.getKey(),config1);
        }

        return new NbtItemInfo(configLinkedHashMap);
    }
}
