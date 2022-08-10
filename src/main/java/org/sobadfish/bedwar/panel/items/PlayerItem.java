package org.sobadfish.bedwar.panel.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.panel.ChestInventoryPanel;
import org.sobadfish.bedwar.panel.from.ShopFrom;
import org.sobadfish.bedwar.player.PlayerInfo;

import java.util.ArrayList;
import java.util.List;

public class PlayerItem extends BasePlayPanelItemInstance{

    private final PlayerInfo info;

    public PlayerItem(PlayerInfo info){
        this.info = info;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Item getItem() {

        return null;
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        if(info.isLive()){
            player.teleport(info.getPlayer());
        }
    }

    @Override
    public void onClickButton(Player player, ShopFrom shopFrom) {

    }

    @Override
    public Item getPanelItem(PlayerInfo i, int index) {
        CompoundTag tag = new CompoundTag();
        tag.putString("player",info.getName());
        Item item = new Item(397,3);
        item.setNamedTag(tag);
        item.setCustomName(TextFormat.colorize('&',"&r"+info.toString()));
        //todo 这里似乎可以画个lore
        List<String> lore = new ArrayList<>();
        lore.add(TextFormat.colorize('&',"&r "));
        lore.add(TextFormat.colorize('&',"&r&7血量 &a"+this.info.getPlayer().getHealth()+" / "+this.info.getPlayer().getMaxHealth()));
        lore.add(TextFormat.colorize('&',"&r  "));
        lore.add(TextFormat.colorize('&',"&r&7击杀 &a"+this.info.getKillCount()));
        lore.add(TextFormat.colorize('&',"&r   "));
        String status = "&a存活";
        if(this.info.getPlayerType() == PlayerInfo.PlayerType.DEATH){
            status = "&c复活中";
        }
        lore.add(TextFormat.colorize('&',"&r&7状态 &a"+status));
        item.setLore(lore.toArray(new String[0]));

        return item;
    }

    @Override
    public ElementButton getGUIButton(PlayerInfo info) {

        return new ElementButton(TextFormat.colorize('&', this.info.toString()+"\n&r生命 &c"+ this.info.getPlayer().getHealth()+" / "+ this.info.getPlayer().getMaxHealth()),new ElementButtonImageData("path","textures/ui/friends"));
    }
}
