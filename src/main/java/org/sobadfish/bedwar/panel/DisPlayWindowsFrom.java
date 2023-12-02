package org.sobadfish.bedwar.panel;

import cn.nukkit.Player;
import org.sobadfish.bedwar.panel.from.BedWarFrom;
import org.sobadfish.bedwar.panel.from.button.BaseIButton;
import org.sobadfish.bedwar.tools.Utils;
import org.sobadfish.bedwar.panel.from.ShopFrom;
import org.sobadfish.bedwar.panel.from.button.ShopButton;
import org.sobadfish.bedwar.panel.items.BasePlayPanelItemInstance;
import org.sobadfish.bedwar.panel.items.NbtDefaultItem;
import org.sobadfish.bedwar.panel.items.PanelItem;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.shop.config.ShopInfoConfig;
import org.sobadfish.bedwar.shop.item.ShopItemInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * @author SoBadFish
 * 2022/1/11
 */
public class DisPlayWindowsFrom {

    public static int FROM_ID = 155;

    public static int FROM_MAX_ID = 105478;

    public static LinkedHashMap<String, ShopFrom> SHOP = new LinkedHashMap<>();

    public static LinkedHashMap<String, BedWarFrom> CUSTOM = new LinkedHashMap<>();

    public static void disPlayerCustomMenu(Player player, String tag, List<BaseIButton> from){
        BedWarFrom bedWarFrom = new BedWarFrom(tag,"",getId());
        bedWarFrom.setBaseIButtoms(from);
        CUSTOM.put(player.getName(), bedWarFrom);
        bedWarFrom.disPlay(player);
    }


    public static void disPlayMenu(Player player, GameRoomConfig room, ShopItemInfo shopItemInfo){

        ShopFrom shopFrom = new ShopFrom(getId(),player,room,shopItemInfo);
        ArrayList<ShopButton> shopButtons = new ArrayList<>();
        if("teamShop".equalsIgnoreCase(shopItemInfo.getShopName())){
            displayTeam(player, room,false);
        }else{
            for(ShopInfoConfig shopInfoConfig: shopItemInfo.getShopInfoConfigs()){
                shopButtons.add(new ShopButton(new PanelItem(room,shopItemInfo,shopInfoConfig.getClassify())));
            }
//            shopButtons.add(new ShopButton(new TeamPanelItem(room,shopItemInfo)));
            shopFrom.setShopButtons(shopButtons);
            shopFrom.disPlay("主页",false);

        }

    }

    public static int getId(){
        return Utils.rand(FROM_ID,FROM_MAX_ID);
    }
    public static int getId(int min,int max){
        return Utils.rand(min,max);
    }



    public static void disPlayChoseList(Player player, GameRoomConfig roomConfig, ShopItemInfo shopItemInfo, ShopInfoConfig.ShopItemClassify classify){
        ShopFrom shopFrom = new ShopFrom(getId(),player,roomConfig,shopItemInfo);
        if(SHOP.containsKey(player.getName())){
            shopFrom.setLastFrom(SHOP.get(player.getName()));
        }
        ArrayList<ShopButton> shopButtons = new ArrayList<>();
        ShopInfoConfig shopInfoConfig = shopItemInfo.getShopInfoConfigByClassify(classify);
        for(BasePlayPanelItemInstance panelItemInstance: shopInfoConfig.getShopItems()){
            if(panelItemInstance instanceof NbtDefaultItem){
                ((NbtDefaultItem) panelItemInstance).setPlayerItem(
                        roomConfig.getNbtItemInfo()
                                .items.get(((NbtDefaultItem) panelItemInstance).item.getName()));
            }
            shopButtons.add(new ShopButton(panelItemInstance));
        }
        shopFrom.setShopButtons(shopButtons);
        shopFrom.setShopItemClassify(classify);
        shopFrom.disPlay(classify.getDisPlayerName(),true);
    }

    public static void displayTeam(Player player, GameRoomConfig roomConfig,boolean isBack){
        int fromId3 = getId();
        ArrayList<ShopButton> shopButtons = new ArrayList<>();
        ShopItemInfo shopInfoConfigs = roomConfig.getShops().get("teamShop");
        ShopFrom shopFrom = new ShopFrom(fromId3,player,roomConfig,shopInfoConfigs);
        if(isBack){
            if(SHOP.containsKey(player.getName())){
                shopFrom.setLastFrom(SHOP.get(player.getName()));
            }
        }
        ArrayList<BasePlayPanelItemInstance> teamShopItems = new ArrayList<>();
        for(ShopInfoConfig teamInfo:shopInfoConfigs.getShopInfoConfigs()){
            teamShopItems.addAll(teamInfo.getShopItems());
        }
        for(BasePlayPanelItemInstance basePlayPanelItemInstance:teamShopItems){
            shopButtons.add(new ShopButton(basePlayPanelItemInstance));
        }

        shopFrom.setShopButtons(shopButtons);
        shopFrom.disPlay("团队商店",isBack);
    }
}
