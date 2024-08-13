package org.sobadfish.bedwar.shop;




import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import lombok.Getter;
import lombok.Setter;
import org.sobadfish.bedwar.entity.ShopVillage;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 商店
 * @author SoBadFish
 * 2022/1/2
 */
@Getter
@Setter
public class ShopInfo {

    private ArrayList<LinkedHashMap<String,String>> configs;

    private ArrayList<ShopVillage> shopVillages = new ArrayList<>();

    public ShopInfo(ArrayList<LinkedHashMap<String, String>> configs){
        this.configs = configs;
    }

    public void init(GameRoomConfig gameRoomConfig){
        for(LinkedHashMap<String,String> linkedHashMap : configs){
            for (Map.Entry<String, String> entry : linkedHashMap.entrySet()) {
                Location location = WorldInfoConfig.getLocationByString(entry.getValue());
                ShopVillage shopVillage = new ShopVillage(gameRoomConfig,gameRoomConfig.shops.get(entry.getKey()), location.getChunk(), Entity.getDefaultNBT(location.add(0.5,0,0.5)));
                shopVillage.yaw = location.yaw;
                shopVillage.spawnToAll();
                shopVillages.add(shopVillage);

            }
        }

    }

}
