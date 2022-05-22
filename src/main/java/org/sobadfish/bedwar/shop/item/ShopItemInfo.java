package org.sobadfish.bedwar.shop.item;


import cn.nukkit.utils.Config;
import org.sobadfish.bedwar.shop.config.ShopInfoConfig;
import org.sobadfish.bedwar.shop.config.TeamShopInfoConfig;

import java.util.ArrayList;


/**
 * @author SoBadFish
 * 2022/1/5
 */
public class ShopItemInfo {

    private String shopName;

    private ArrayList<ShopInfoConfig> shopInfoConfigs;

    private ShopItemInfo(String shopName, ArrayList<ShopInfoConfig> shopInfoConfigs){
        this.shopName = shopName;
        this.shopInfoConfigs = shopInfoConfigs;
    }

    public ShopInfoConfig getShopInfoConfigByClassify(ShopInfoConfig.ShopItemClassify classify){
        ShopInfoConfig arrayList = null;
        for(ShopInfoConfig infoConfig : shopInfoConfigs){
            if(infoConfig.getClassify() == classify){
                arrayList = infoConfig;
            }
        }
        return arrayList;
    }

    public static ShopItemInfo build(String shopName, Config config){
        ArrayList<ShopInfoConfig> shopInfoConfigs = new ArrayList<>();
        if("teamShop".equalsIgnoreCase(shopName)){
            shopInfoConfigs.add(TeamShopInfoConfig.build("effects",config.getMapList("effects")));
            shopInfoConfigs.add(TeamShopInfoConfig.build("enchants",config.getMapList("enchants")));
            shopInfoConfigs.add(TeamShopInfoConfig.build("trap",config.getMapList("trap")));
        }else{
            for(String s: config.getKeys()){
                ShopInfoConfig.ShopItemClassify classify;
                try {
                    classify = ShopInfoConfig.ShopItemClassify.valueOf(s.toUpperCase());
                }catch (IllegalArgumentException e){
                    continue;
                }
                shopInfoConfigs.add(ShopInfoConfig.build(classify,config.getMapList(s)));
            }
        }

        return new ShopItemInfo(shopName,shopInfoConfigs);
    }

    public ArrayList<ShopInfoConfig> getShopInfoConfigs() {
        return shopInfoConfigs;
    }

    public String getShopName() {
        return shopName;
    }
}
