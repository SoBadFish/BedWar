package org.sobadfish.bedwar.shop.config;

import org.sobadfish.bedwar.panel.items.BasePlayPanelItemInstance;
import org.sobadfish.bedwar.panel.items.DefaultItem;
import org.sobadfish.bedwar.panel.items.TeamItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author SoBadFish
 * 2022/1/5
 */
public class TeamShopInfoConfig extends ShopInfoConfig{

    private TeamShopInfoConfig(ArrayList<BasePlayPanelItemInstance> shopItems) {
        super(null, shopItems);
    }

    public static TeamShopInfoConfig build(String s,List<Map> map){
        //TODO 构建团队商店
        ArrayList<BasePlayPanelItemInstance> arrayList = new ArrayList<>();
        //TODO 构建商店信息
        for(Map map1: map){
            arrayList.add(TeamItem.build(s,map1));
        }
        return new TeamShopInfoConfig(arrayList);
    }


}
