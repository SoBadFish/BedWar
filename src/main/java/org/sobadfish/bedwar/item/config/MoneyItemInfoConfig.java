package org.sobadfish.bedwar.item.config;

import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;


import java.util.Map;

/**
 * @author SoBadFish
 * 2022/1/3
 */
@Data
public class MoneyItemInfoConfig {

    private String name;


    private String customName;

    private Item item;

    private double exp;

    private MoneyItemInfoConfig(String name, String customName, Item item){
        this.name = name;
        this.customName = customName;
        this.item = item;
    }



    public Item getItem() {
        Item item = new Item(this.item.getId(),this.item.getDamage());
        if(this.item.getNamedTag() != null){
            item.setNamedTag(this.item.getNamedTag());
        }
        item.setCount(1);
        item.setCustomName(TextFormat.colorize('&',"&r"+getCustomName()));
        return item;
    }


    public static MoneyItemInfoConfig getInstance(Map<?,?> map){
        String name = map.containsKey("name")?map.get("name").toString():null;
        String item = map.containsKey("item")?map.get("item").toString():null;
        String customName = map.containsKey("customName")?map.get("customName").toString():name;
        if(name == null || item == null){
            return null;
        }
        Item item1 = Item.fromString(item);
        MoneyItemInfoConfig iconfig = new MoneyItemInfoConfig(name,customName,item1);
        iconfig.exp = map.containsKey("exp")?Double.parseDouble(map.get("exp").toString()):1.0;
        return iconfig;
    }

    @Override
    public String toString() {
        return "{item: "+item.getId()+":"+item.getDamage()+" name: "+name+" customName: "+customName+"}";
    }
}
