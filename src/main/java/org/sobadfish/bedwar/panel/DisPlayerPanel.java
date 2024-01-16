package org.sobadfish.bedwar.panel;

import cn.nukkit.entity.Entity;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import org.sobadfish.bedwar.panel.items.*;
import org.sobadfish.bedwar.panel.lib.AbstractFakeInventory;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.shop.config.ShopInfoConfig;
import org.sobadfish.bedwar.shop.item.ShopItemInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author SoBadFish
 * 2022/1/2
 */
public class DisPlayerPanel implements InventoryHolder {

    private AbstractFakeInventory inventory;


    public static Map<Integer, BasePlayPanelItemInstance> displayPlayers(GameRoom room){
        Map<Integer, BasePlayPanelItemInstance> panel = new LinkedHashMap<>();
        int index = 10;
        int count = 0;
        for(PlayerInfo info: room.getLivePlayers()){
            if(count % 7 == 0){
                count = 0;
                index += 9;
            }
            panel.put(index,new PlayerItem(info));
            count++;
            index++;
        }

        return panel;

    }



    public static Map<Integer, BasePlayPanelItemInstance> disPlayShop(GameRoomConfig gameRoom, ShopItemInfo shopItemInfo, ShopInfoConfig.ShopItemClassify chose){
        //TODO 画界面
        Map<Integer, BasePlayPanelItemInstance> panel = new LinkedHashMap<>();
        int index = 0;
        if("teamShop".equalsIgnoreCase(shopItemInfo.getShopName())){
            chose = null;
        }
        if(chose == null){
           index = 4;
           panel.put(index,new TeamPanelItem(gameRoom,shopItemInfo));
           index = 9;
           for(int i = 0;i < 9 ;i++){
                panel.put(index,new NomalItem(i == 4));
                index++;
           }
           ShopItemInfo shopInfoConfigs = gameRoom.getShops().get("teamShop");
           ArrayList<BasePlayPanelItemInstance> teamShopItems = new ArrayList<>();
           for(ShopInfoConfig teamInfo:shopInfoConfigs.getShopInfoConfigs()){
               teamShopItems.addAll(teamInfo.getShopItems());
           }
           index++;
           for (BasePlayPanelItemInstance panelItemInstance : teamShopItems) {
               panel.put(index, panelItemInstance);
               index+=2;
               if(index % 9 == 0){
                   index++;
               }
           }
            //TODO 团队商店
            return panel;
        }
        int choseIndex = -1;
        for(ShopInfoConfig.ShopItemClassify shopItemClassify : gameRoom.shopItemClassifies){
            panel.put(index,new PanelItem(gameRoom,shopItemInfo,shopItemClassify));
            if(shopItemClassify == chose){
                choseIndex = index;
            }
            index++;
        }
        index++;
//        panel.put(index++,new TeamPanelItem(gameRoom,shopItemInfo));
        //前后留空
        for(int i = 0;i < 9 ;i++){
            panel.put(index,new NomalItem(i == choseIndex));
            index++;
        }
        //前面留空
        index++;

        int newLineIndexCount = 0;


        ShopInfoConfig shopInfoConfigs = shopItemInfo.getShopInfoConfigByClassify(chose);
        if(shopInfoConfigs != null) {
            for (BasePlayPanelItemInstance panelItemInstance : shopInfoConfigs.getShopItems()) {
                if(panelItemInstance instanceof NbtDefaultItem){
                    ((NbtDefaultItem) panelItemInstance).setPlayerItem(
                            gameRoom.getNbtItemInfo()
                            .items.get(((NbtDefaultItem) panelItemInstance).item.getName()));
                }
                newLineIndexCount++;
                if(newLineIndexCount == 8){
                    newLineIndexCount = 0;
                    index+=2;
                }
                panel.put(index, panelItemInstance);
                index++;
            }
        }
        return panel;
    }




    public void displayPlayer(PlayerInfo player, Map<Integer, BasePlayPanelItemInstance> itemMap, String name){
        ChestInventoryPanel panel = new ChestInventoryPanel(player,this,name);
        panel.setPanel(itemMap);
        panel.id = ++Entity.entityCount;
        inventory = panel;
        panel.getPlayer().addWindow(panel);

    }



    @Override
    public Inventory getInventory() {
        return inventory;
    }


}
