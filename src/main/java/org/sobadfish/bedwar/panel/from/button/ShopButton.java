package org.sobadfish.bedwar.panel.from.button;

import org.sobadfish.bedwar.panel.items.BasePlayPanelItemInstance;

/**
 * @author SoBadFish
 * 2022/1/11
 */
public class ShopButton {

    private BasePlayPanelItemInstance itemInstance;

    public ShopButton(BasePlayPanelItemInstance itemInstance){
        this.itemInstance = itemInstance;
    }

    @Override
    public String toString() {
        return itemInstance.toString();
    }

    public BasePlayPanelItemInstance getItemInstance() {
        return itemInstance;
    }
}
