package org.sobadfish.bedwar.shop.config;


import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
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
            arrayList.add(DefaultItem.build(map1));
        }
        BedWarMain.sendMessageToConsole("&a加载 &r"+classify.disPlayerName+" &r物品: &a"+arrayList.size()+" &r个");
        return new ShopInfoConfig(classify,arrayList);
    }


    public ShopItemClassify getClassify() {
        return classify;
    }

    public enum ShopItemClassify{
        /**
         * 武器，盔甲，弓，方块，食物，工具，小道具，药水
         * */
        WEAPON(Item.get(276),"武器"),
        ARMOR(Item.get(311),"盔甲"),
        BOW(Item.get(261),"弓"),
        BLOCK(Item.get(24,2),"方块"),
        FOOD(Item.get(320),"食物"),
        TOOLS(Item.get(278),"工具"),
        ITEMS(Item.get(46),"小道具"),
        EFFECT(Item.get(373),"药水");
        private final Item item;
        private final String disPlayerName;

        ShopItemClassify(Item item,String disPlayerName){
            this.item = item;
            this.disPlayerName = disPlayerName;
        }

        public String getDisPlayerName() {
            return disPlayerName;
        }

        public Item getItem() {
            Item item = this.item;
            item.setCustomName(TextFormat.colorize('&',"&r"+disPlayerName));
            return item;
        }
    }

    public ArrayList<BasePlayPanelItemInstance> getShopItems() {
        return shopItems;
    }
}
