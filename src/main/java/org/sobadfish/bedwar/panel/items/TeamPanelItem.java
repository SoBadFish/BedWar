package org.sobadfish.bedwar.panel.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.item.ItemIDSunName;
import org.sobadfish.bedwar.panel.ChestInventoryPanel;
import org.sobadfish.bedwar.panel.DisPlayWindowsFrom;
import org.sobadfish.bedwar.panel.DisPlayerPanel;
import org.sobadfish.bedwar.panel.from.ShopFrom;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.shop.item.ShopItemInfo;

/**
 * @author SoBadFish
 * 2022/1/6
 */
public class TeamPanelItem extends BasePlayPanelItemInstance{

    private Item item;

    private ShopItemInfo itemInfo;

    private GameRoomConfig room;

    public TeamPanelItem(GameRoomConfig room, ShopItemInfo itemInfo){
        this.item = Item.get(426);
        this.itemInfo = itemInfo;
        this.room = room;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        inventory.setPanel(DisPlayerPanel.disPlayShop(room,itemInfo,null));
    }

    @Override
    public void onClickButton(Player player, ShopFrom shopFrom) {
        DisPlayWindowsFrom.displayTeam(player,room,true);
    }

    @Override
    public Item getPanelItem(PlayerInfo info, int index)  {
        Item item = this.item;
        item.setCustomName(TextFormat.colorize('&',"&r团队商店"));
        item.setNamedTag(item.getNamedTag().putInt("index",index));
        return item;
    }

    @Override
    public ElementButton getGUIButton(PlayerInfo info) {
        return new ElementButton("团队商店",new ElementButtonImageData("path", ItemIDSunName.getIDByPath(item.getId(),item.getDamage())));
    }
}
