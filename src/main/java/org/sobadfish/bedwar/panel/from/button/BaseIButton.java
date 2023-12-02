package org.sobadfish.bedwar.panel.from.button;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;

/**
 * @author SoBadFish
 * 2022/1/12
 */
public abstract class BaseIButton {

    private final ElementButton button;

    public BaseIButton(ElementButton button) {
        this.button = button;
    }

    public ElementButton getButton() {
        return button;
    }

    /**
     * 按键被点击
     * @param player 玩家
     * */
    abstract public void onClick(Player player);

    @Override
    public String toString() {
        return button.getText()+" - img["+button.getImage().getType()+">"+button.getImage().getData()+"]";
    }
}
