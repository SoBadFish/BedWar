package org.sobadfish.bedwar.panel.items;


import cn.nukkit.item.Item;
import lombok.Getter;
import lombok.Setter;
import org.sobadfish.bedwar.item.config.NbtItemInfoConfig;
import org.sobadfish.bedwar.item.nbt.INbtItem;

/**
 * @author SoBadFish
 * 2022/1/5
 */
public class NbtDefaultItem extends DefaultItem{

    public INbtItem item;

    @Setter
    @Getter
    private NbtItemInfoConfig playerItem;


    NbtDefaultItem(INbtItem item, String moneyItem, int count) {
        super(null, moneyItem, count);
        this.item = item;
    }





    public Item[] getItem() {
        return new Item[]{playerItem.getItem()};
    }


}
