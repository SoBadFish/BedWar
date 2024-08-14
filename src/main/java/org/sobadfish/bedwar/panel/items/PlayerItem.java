package org.sobadfish.bedwar.panel.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.item.ItemIDSunName;
import org.sobadfish.bedwar.panel.ChestInventoryPanel;
import org.sobadfish.bedwar.panel.from.ShopFrom;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.player.team.TeamInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Sobadfish
 */
public class PlayerItem extends BasePlayPanelItemInstance{

    private final PlayerInfo info;

    public PlayerItem(PlayerInfo info){
        this.info = info;
    }

    @Override
    public int getCount() {
        return 0;
    }


    public Item getItem() {
        return new Item(397,3);
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        player.teleport(info.getPlayer());
        inventory.close(player);
    }

    @Override
    public void onClickButton(Player player, ShopFrom shopFrom) {

    }

    @Override
    public Item getPanelItem(PlayerInfo i, int index) {
        Item item = getItem().clone();
        item.setCustomName(TextFormat.colorize('&',"&r"+info.toString()));
        //todo 这里似乎可以画个lore
        List<String> lore = new ArrayList<>();
        lore.add(TextFormat.colorize('&',"&r "));
        lore.add(TextFormat.colorize('&',"&r&7❤ &a"+this.info.getPlayer().getHealth()+" / "+this.info.getPlayer().getMaxHealth()));
        lore.add(TextFormat.colorize('&',"&r  "));
        lore.add(TextFormat.colorize('&',"&r&7☠ &a"+this.info.getKillCount()));
        lore.add(TextFormat.colorize('&',"&r   "));
        String status = "&aLive";
        if(this.info.getPlayerType() == PlayerInfo.PlayerType.DEATH){
            status = "&cRespawn";
        }
        lore.add(TextFormat.colorize('&',"&r&7Status &a"+status));
        item.setLore(lore.toArray(new String[0]));
        item.setNamedTag(item.getNamedTag().putInt("index", index));
        return item;
    }

    @Override
    public ElementButton getGuiButton(PlayerInfo info) {
        TeamInfo t = this.info.getTeamInfo();
        String img = ItemIDSunName.getPathById(14);
        if(t != null){
            Item i = t.getTeamConfig().getTeamConfig().getBlockWoolColor();
            img = ItemIDSunName.getPathByIdAndDamage(i.getId(),i.getDamage());
        }

        return new ElementButton(TextFormat.colorize('&', this.info +"\n&r❤ &c"+ this.info.getPlayer().getHealth()+" / "+ this.info.getPlayer().getMaxHealth()),new ElementButtonImageData("path",img));
    }
}
