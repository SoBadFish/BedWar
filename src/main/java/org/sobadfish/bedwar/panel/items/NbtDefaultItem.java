package org.sobadfish.bedwar.panel.items;


import cn.nukkit.Player;
import cn.nukkit.item.Item;
import org.sobadfish.bedwar.item.config.NbtItemInfoConfig;
import org.sobadfish.bedwar.item.nbt.INbtItem;
import org.sobadfish.bedwar.panel.ChestInventoryPanel;

/**
 * @author SoBadFish
 * 2022/1/5
 */
public class NbtDefaultItem extends DefaultItem{

    public INbtItem item;

    private NbtItemInfoConfig playerItem;


    NbtDefaultItem(INbtItem item, String moneyItem, int count) {
        super(null, moneyItem, count);
        this.item = item;
    }

    public void setPlayerItem(NbtItemInfoConfig playerItem) {
        this.playerItem = playerItem;
    }


    @Override
    public Item getItem() {
        return playerItem.getItem();
    }


}
