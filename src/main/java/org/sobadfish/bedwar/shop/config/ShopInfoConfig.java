package org.sobadfish.bedwar.shop.config;


import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.panel.items.BasePlayPanelItemInstance;
import org.sobadfish.bedwar.panel.items.DefaultItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * @author SoBadFish
 * 2022/1/2
 */

@Getter
public class ShopInfoConfig {

    /**
     * 分类
     * */
    private final ShopItemClassify classify;

    //TODO 转换为窗口

    private final ArrayList<BasePlayPanelItemInstance> shopItems;

    ShopInfoConfig(ShopItemClassify classify, ArrayList<BasePlayPanelItemInstance> shopItems){
        this.classify = classify;
        this.shopItems = shopItems;
    }

    public static ShopInfoConfig build(ShopItemClassify classify, List<Map> map){
        ArrayList<BasePlayPanelItemInstance> arrayList = new ArrayList<>();
        //TODO 构建商店信息
        for(Map<?,?> map1: map){
            DefaultItem defaultItem = DefaultItem.build(map1);
            if(defaultItem == null){
                continue;
            }
            arrayList.add(defaultItem);
        }
        BedWarMain.sendMessageToConsole("&aLoad &2"+classify.disPlayerName+" &rItem: &a"+arrayList.size()+" &r");
        return new ShopInfoConfig(classify,arrayList);
    }


    @Getter
    public static class ShopItemClassify{

        public String type;

        public String disPlayerName;

        public Item item;


        public ShopItemClassify(String type){
            this.type = type;

        }
//
        public ShopItemClassify(String type,Item item,String disPlayerName){
            this.type = type;
            this.item = item;
            this.disPlayerName = disPlayerName;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ShopItemClassify)) {
                return false;
            }

            ShopItemClassify that = (ShopItemClassify) o;

            return type != null ? type.equals(that.type) : that.type == null;
        }

        @Override
        public int hashCode() {
            return type != null ? type.hashCode() : 0;
        }

        public Item getItem() {
            Item item = this.item;
            item.setCustomName(TextFormat.colorize('&',"&r"+disPlayerName));
            return item;
        }
    }

}
