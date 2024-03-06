package org.sobadfish.bedwar.panel.from;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.window.FormWindowSimple;
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


    public boolean isBreak() {
        return isBreak;
    }

    public int getId() {
        return id;
    }

    public ArrayList<ShopButton> getShopButtons() {
        return shopButtons;
    }

    public GameRoomConfig getRoomConfig() {
        return roomConfig;
    }

    public Player getPlayer() {
        return player;
    }

    public ShopFrom getLastFrom() {
        return lastFrom;
    }

    public ShopItemInfo getShopItemInfo() {
        return shopItemInfo;
    }

    public String getTitle() {
        return title;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void setBreak(boolean aBreak) {
        isBreak = aBreak;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLastFrom(ShopFrom lastFrom) {
        this.lastFrom = lastFrom;
    }

    public void setRoomConfig(GameRoomConfig roomConfig) {
        this.roomConfig = roomConfig;
    }

    public void setShopItemInfo(ShopItemInfo shopItemInfo) {
        this.shopItemInfo = shopItemInfo;
    }

    public void setShopButtons(ArrayList<ShopButton> shopButtons) {
        this.shopButtons = shopButtons;
    }

    public void setShopItemClassify(ShopInfoConfig.ShopItemClassify shopItemClassify) {
        this.shopItemClassify = shopItemClassify;
    }

    public void setTitle(String title) {
        this.title = title;
    }


    public ShopInfoConfig.ShopItemClassify getShopItemClassify() {
        return shopItemClassify;
    }

    public void disPlay(String title, boolean isBack){
        setTitle(title);
        setBreak(isBack);
        FormWindowSimple simple = new FormWindowSimple(title,"");
        PlayerInfo playerInfo = BedWarMain.getRoomManager().getPlayerInfo(player);
        for(ShopButton shopButton:this.shopButtons){
            simple.addButton(shopButton.getItemInstance().getGUIButton(playerInfo));
        }
        if(isBack) {
            ElementButton button2 = new ElementButton("Back", new ElementButtonImageData("path", "textures/ui/refresh_light"));
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
