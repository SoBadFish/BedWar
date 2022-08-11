package org.sobadfish.bedwar.item.config;

import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.item.nbt.INbtItem;
import org.sobadfish.bedwar.item.nbt.INbtItemBuild;
import org.sobadfish.bedwar.manager.NbtItemManager;
import org.sobadfish.bedwar.panel.items.NbtDefaultItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author SoBadFish
 * 2022/1/5
 */
public class NbtItemInfoConfig {

    public INbtItem name;

    public Item item;


    private NbtItemInfoConfig(INbtItem name, Item item){
        this.name = name;
        this.item = item;
    }

    public static NbtItemInfoConfig build(String name, Map map){
        if(NbtItemManager.NBT_MANAGER.containsKey(name)){
            INbtItem iNbtItem = NbtItemManager.NBT_MANAGER.get(name);
            if(iNbtItem == null){
                return null;
            }
            if(iNbtItem instanceof INbtItemBuild){
                return new NbtItemInfoConfig(iNbtItem,((INbtItemBuild) iNbtItem).build(map));
            }else{
                return new NbtItemInfoConfig(iNbtItem,NbtItemManager.build(name,map));
            }

        }
        return null;
    }


    public Item getItem() {
        return item;
    }
}
