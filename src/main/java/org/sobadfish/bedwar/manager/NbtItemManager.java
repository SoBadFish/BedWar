package org.sobadfish.bedwar.manager;


import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.item.nbt.*;
import org.sobadfish.bedwar.panel.items.NbtDefaultItem;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SoBadFish
 * 2022/1/5
 */
public class NbtItemManager {

    public static LinkedHashMap<String, INbtItem> NBT_MANAGER = new LinkedHashMap<>();

    public static void init(){
        NBT_MANAGER.put("简易平台",new Platform());
        NBT_MANAGER.put("快速回城",new BackHub());
        NBT_MANAGER.put("线",new Line());
        NBT_MANAGER.put("指南针",new PointPlayer());
        NBT_MANAGER.put("护卫",new SpawnMob());
        NBT_MANAGER.put("火球",new FireBall());
        NBT_MANAGER.put("凋零弓",new DieBow());

    }

    public static Item build(String name, Map map){
        ArrayList<String> lore = new ArrayList<>();
        for(Object o:(List)map.get("lore")){
            lore.add(TextFormat.colorize('&',o.toString()));
        }

        Item item = Item.fromString(map.get("item").toString());
        item.setLore(lore.toArray(new String[0]));
        String cn = map.get("customName").toString();
        if(!"".equalsIgnoreCase(cn)){
            item.setCustomName(TextFormat.colorize('&',cn));
        }
        CompoundTag tag = item.getNamedTag();
        tag.putString(NbtDefaultItem.TAG,name);
        item.setNamedTag(tag);
        return item;

    }
}
