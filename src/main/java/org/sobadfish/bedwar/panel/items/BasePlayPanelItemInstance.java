package org.sobadfish.bedwar.panel.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.item.Item;
import org.sobadfish.bedwar.panel.ChestInventoryPanel;
import org.sobadfish.bedwar.panel.from.ShopFrom;
import org.sobadfish.bedwar.player.PlayerInfo;

/**
 * @author SoBadFish
 * 2022/1/2
 */
public abstract class BasePlayPanelItemInstance {

    
    /**
     * 消费数量
     * @return 数量
     * */
    public abstract int getCount();
//    /**
//     * 游戏内物品
//     * @return 物品
//     * */
//    public abstract Item getItem();
    /**
     * 当玩家触发
     *
     * @param inventory 商店
     * @param player 玩家
     *
     * */
    public abstract void onClick(ChestInventoryPanel inventory, Player player);
    /**
     * 当玩家触发GUI的button
     *
     * @param player 玩家
     * @param shopFrom 商店GUI
     *
     * */
    public abstract void onClickButton(Player player, ShopFrom shopFrom);

    /**
     * 展示物品
     * @param index 位置
     * @param info 玩家信息
     * @return 物品
     *
     * */
    public abstract Item getPanelItem(PlayerInfo info,int index);

    /**
     * GUI按键button
     * @param info 玩家信息
     * @return 物品
     *
     * */
    public abstract ElementButton getGUIButton(PlayerInfo info);


    @Override
    public String toString() {
        return " count: "+getCount();
    }
}
