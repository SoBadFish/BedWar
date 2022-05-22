package org.sobadfish.bedwar.panel.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import org.sobadfish.bedwar.panel.ChestInventoryPanel;
import org.sobadfish.bedwar.panel.from.ShopFrom;
import org.sobadfish.bedwar.player.PlayerInfo;


/**
 * @author SoBadFish
 * 2022/1/5
 */
public class NomalItem extends BasePlayPanelItemInstance{

    private Item item;


    public NomalItem(boolean isUse){
        this.item = Item.get(160,13);
        if(isUse){
            this.item = Item.get(160,14);
        }
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

    }

    @Override
    public void onClickButton(Player player, ShopFrom shopFrom) {

    }


    @Override
    public Item getPanelItem(PlayerInfo info, int index)  {
        return item;
    }

    @Override
    public ElementButton getGUIButton(PlayerInfo info) {
        return null;
    }
}
