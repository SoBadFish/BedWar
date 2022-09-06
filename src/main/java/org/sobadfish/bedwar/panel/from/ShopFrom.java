package org.sobadfish.bedwar.panel.from;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;
import lombok.Getter;
import lombok.Setter;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.panel.DisPlayWindowsFrom;
import org.sobadfish.bedwar.panel.from.button.ShopButton;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.shop.config.ShopInfoConfig;
import org.sobadfish.bedwar.shop.item.ShopItemInfo;

import java.util.ArrayList;

/**
 * @author SoBadFish
 * 2022/1/11
 */
@Getter
@Setter
public class ShopFrom {

    private int id;

    private Player player;

    public boolean isBreak;

    private GameRoomConfig roomConfig;

    private ShopItemInfo shopItemInfo;

    private ShopFrom lastFrom = null;

    private String title;

    private ShopInfoConfig.ShopItemClassify shopItemClassify;


    private ArrayList<ShopButton> shopButtons = new ArrayList<>();

    public ShopFrom(int id,Player player,GameRoomConfig roomConfig,ShopItemInfo shopItemInfo){
        this.id = id;
        this.roomConfig = roomConfig;
        this.shopItemInfo = shopItemInfo;
        this.player = player;
    }


    public void disPlay(String title,boolean isBack){
        setTitle(title);
        setBreak(isBack);
        FormWindowSimple simple = new FormWindowSimple("商店"+"-- "+title,"");
        PlayerInfo playerInfo = BedWarMain.getRoomManager().getPlayerInfo(player);
        for(ShopButton shopButton:this.shopButtons){
            simple.addButton(shopButton.getItemInstance().getGUIButton(playerInfo));
        }
        if(isBack) {
            ElementButton button2 = new ElementButton("返回", new ElementButtonImageData("path", "textures/ui/refresh_light"));
            simple.addButton(button2);
        }
        player.showFormWindow(simple,++id);

        DisPlayWindowsFrom.SHOP.put(player.getName(),this);

    }


    @Override
    public String toString() {
        return id+"->"+shopButtons.toString();
    }
}
