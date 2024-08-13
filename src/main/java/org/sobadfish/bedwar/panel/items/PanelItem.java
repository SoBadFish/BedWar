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
import org.sobadfish.bedwar.shop.config.ShopInfoConfig;
import org.sobadfish.bedwar.shop.item.ShopItemInfo;

/**
 * @author SoBadFish
 * 2022/1/5
 */
public class PanelItem extends BasePlayPanelItemInstance{

    private final ShopInfoConfig.ShopItemClassify classify;

    private final ShopItemInfo shopInfo;

    private final GameRoomConfig room;


    public PanelItem(GameRoomConfig room, ShopItemInfo shopInfo, ShopInfoConfig.ShopItemClassify classify){
        this.classify = classify;
        this.shopInfo = shopInfo;
        this.room = room;
    }

    @Override
    public int getCount() {
        return 0;
    }


    public Item getItem() {
        return classify.getItem();
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        inventory.setPanel(DisPlayerPanel.disPlayShop(room,shopInfo,classify));
    }

    @Override
    public void onClickButton(Player player, ShopFrom shopFrom) {
        //TODO 显示跳转的商店
        DisPlayWindowsFrom.disPlayChoseList(player,room,shopInfo,classify);

    }


    @Override
    public Item getPanelItem(PlayerInfo info, int index)  {
        Item i =  classify.getItem();
        i.setNamedTag(i.getNamedTag().putInt("index",index));
        return i;
    }

    @Override
    public ElementButton getGUIButton(PlayerInfo info) {
        return new ElementButton(TextFormat.colorize('&',classify.getDisPlayerName()),new ElementButtonImageData("path", ItemIDSunName.getIDByPath(classify.getItem().getId(),classify.getItem().getDamage())));
    }
}
