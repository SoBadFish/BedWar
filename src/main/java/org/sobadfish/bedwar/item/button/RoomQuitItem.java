package org.sobadfish.bedwar.item.button;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;

import java.util.ArrayList;

/**
 * @author SoBadFish
 * 2022/1/3
 */
public class RoomQuitItem {


    public static ArrayList<Player> clickAgain = new ArrayList<>();


    private final ArrayList<Player> clickAgainTeam = new ArrayList<>();

    public static int getIndex(){
        return 8;
    }

    public static Item get(){
        Item item = Item.get(324);
        item.addEnchantment(Enchantment.get(9));
        CompoundTag tag = item.getNamedTag();
        tag.putBoolean("quitItem",true);
        item.setNamedTag(tag);


        item.setCustomName(TextFormat.colorize('&',BedWarMain.getLanguage().getLanguage("player-click-quit-room"
                ,"&r&l&e点我退出游戏")));
        return item;

    }
}
