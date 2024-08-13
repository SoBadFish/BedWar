package org.sobadfish.bedwar.shop.item;


import cn.nukkit.utils.Config;
import lombok.Getter;
import org.sobadfish.bedwar.shop.config.ShopInfoConfig;
import org.sobadfish.bedwar.shop.config.TeamShopInfoConfig;

import java.util.ArrayList;
import java.util.List;


/**
 * @author SoBadFish
 * 2022/1/5
 */
@Getter
public class ShopItemInfo {

    private final String shopName;

    private final ArrayList<ShopInfoConfig> shopInfoConfigs;

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

    public static ShopItemInfo build(List<ShopInfoConfig.ShopItemClassify> shopItemClassifies,String shopName, Config config){
        ArrayList<ShopInfoConfig> shopInfoConfigs = new ArrayList<>();
        if("teamShop".equalsIgnoreCase(shopName)){
            shopInfoConfigs.add(TeamShopInfoConfig.build("effects",config.getMapList("effects")));
            shopInfoConfigs.add(TeamShopInfoConfig.build("enchants",config.getMapList("enchants")));
            shopInfoConfigs.add(TeamShopInfoConfig.build("up",config.getMapList("up")));
            shopInfoConfigs.add(TeamShopInfoConfig.build("trap",config.getMapList("trap")));
        }else{
            for(String s: config.getKeys()){
                ShopInfoConfig.ShopItemClassify classify;
                try {
                    classify = shopItemClassifies.get(shopItemClassifies.indexOf(new ShopInfoConfig.ShopItemClassify(s)));
                }catch (IllegalArgumentException e){
                    continue;
                }

                shopInfoConfigs.add(ShopInfoConfig.build(classify,config.getMapList(s)));
            }
        }

        return new ShopItemInfo(shopName,shopInfoConfigs);
    }

}
