package org.sobadfish.bedwar.proxy;

import cn.nukkit.item.Item;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 由于pnx与其他版本的羊毛id不同
 * @author Sobadfish
 * @date 2023/4/1
 */
public class ItemProxy {

    public static final LinkedHashMap<String,String> DICT = new LinkedHashMap<>();

    public static void init(){
        DICT.put("35:1","-557");
        DICT.put("35:2","-565");
        DICT.put("35:3","-562");
        DICT.put("35:4","-558");
        DICT.put("35:5","-559");
        DICT.put("35:6","-566");
        DICT.put("35:7","-553");
        DICT.put("35:8","-552");
        DICT.put("35:9","-561");
        DICT.put("35:10","-564");
        DICT.put("35:11","-563");
        DICT.put("35:12","-555");
        DICT.put("35:13","-560");
        DICT.put("35:14","-556");
        DICT.put("35:15","-554");

    }

    public static String asNewWool(String id){
        for(Map.Entry<String, String> dv: DICT.entrySet()){
            if(dv.getValue().equalsIgnoreCase(id)){
                return dv.getKey();
            }

        }
        return null;
    }

    public static Item getItem(String id){
        //检查核心是否为PNX
        boolean isNewVersion = false;

        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            Field field = c.getField("CODENAME");
            isNewVersion = true;

        } catch (Exception ignored) {
        }
        if(isNewVersion && DICT.containsKey(id)){
            return Item.fromString(DICT.get(id));
        }
        return Item.fromString(id);
    }
}
