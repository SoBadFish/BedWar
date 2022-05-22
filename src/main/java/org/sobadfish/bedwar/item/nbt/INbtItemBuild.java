package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.item.Item;

import java.util.Map;

/**
 * @author SoBadFish
 * 2022/5/21
 */
public interface INbtItemBuild {

    /**
     * 将物品数据编译为Item类
     * Map 为配置文件内容
     * */
    Item build(Map map);
}
