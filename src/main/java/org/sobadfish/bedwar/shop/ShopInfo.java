package org.sobadfish.bedwar.shop;




import cn.nukkit.entity.Entity;
import cn.nukkit.level.Location;
import org.sobadfish.bedwar.entity.ShopVillage;
import org.sobadfish.bedwar.room.config.GameRoomConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 商店
 * @author SoBadFish
 * 2022/1/2
 */
public class ShopInfo {

    private ArrayList<LinkedHashMap<String,Location>> configs;

    private ArrayList<ShopVillage> shopVillages = new ArrayList<>();

    public ShopInfo(ArrayList<LinkedHashMap<String, Location>> configs){
        this.configs = configs;
    }

    public void init(GameRoomConfig gameRoomConfig){
        for(LinkedHashMap<String,Location> linkedHashMap : configs){
            for (Map.Entry<String, Location> entry : linkedHashMap.entrySet()) {
                ShopVillage shopVillage = new ShopVillage(gameRoomConfig,gameRoomConfig.shops.get(entry.getKey()), entry.getValue().getChunk(), Entity.getDefaultNBT(entry.getValue().add(0.5,0,0.5)));
                shopVillage.yaw = entry.getValue().yaw;
                shopVillage.spawnToAll();
                shopVillages.add(shopVillage);

            }
        }

    }

    public ArrayList<ShopVillage> getShopVillages() {
        return shopVillages;
    }
}
