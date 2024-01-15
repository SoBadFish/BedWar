package org.sobadfish.bedwar.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.panel.DisPlayWindowsFrom;
import org.sobadfish.bedwar.panel.DisPlayerPanel;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.shop.item.ShopItemInfo;

/**
 * @author SoBadFish
 * 2022/1/2
 */
public class ShopVillage extends EntityCreature {


    private ShopItemInfo infoConfig;

    private GameRoomConfig room;

    private int entityId;
    @Override
    public float getWidth() {
        return  0.6F;
    }

    @Override
    public float getHeight() {
        return  1.95F;
    }

    public ShopVillage(FullChunk chunk, CompoundTag nbt){
        super(chunk, nbt);

    }


    public ShopVillage(GameRoomConfig room, ShopItemInfo infoConfig, FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.infoConfig = infoConfig;
        this.room = room;
        this.setNameTagAlwaysVisible();
        this.setNameTagVisible();
        String str = BedWarMain.getLanguage().getLanguage("team-title","&b团队商店\n&e点击购买");
        entityId = room.teamShopEntityId;
        if("defaultShop".equalsIgnoreCase(infoConfig.getShopName())){
            str = BedWarMain.getLanguage().getLanguage("shop-title","&b道具商店\n&e点击购买");
            entityId = room.itemShopEntityId;
        }
        this.setNameTag(TextFormat.colorize('&',str));
    }



    public ShopItemInfo getInfoConfig() {
        return infoConfig;
    }

    @Override
    public int getNetworkId() {
        return entityId;
    }

    public void onClick(PlayerInfo player){
        //TODO 展示给玩家窗口
        if(player.getPlayer() instanceof Player) {
            BedWarMain.UiType finalUiType;
            if(BedWarMain.uiType != BedWarMain.UiType.AUTO){
                finalUiType = BedWarMain.uiType;
            }else{
                finalUiType = room.uiType;
            }
            switch (finalUiType){
                case PACKET:
                    DisPlayWindowsFrom.disPlayMenu((Player) player.getPlayer(),room,infoConfig);
                    break;
                case UI:
                    DisPlayerPanel disPlayerPanel = new DisPlayerPanel();
                    disPlayerPanel.displayPlayer(player, DisPlayerPanel.disPlayShop(room, infoConfig, room.shopItemClassifies.get(0)), BedWarMain.getLanguage().getLanguage("shop-name","商店"));
                    break;
                default:
                    if (((Player) player.getPlayer()).getLoginChainData().getDeviceOS() == 7) {
                        DisPlayerPanel dis = new DisPlayerPanel();
                        dis.displayPlayer(player, DisPlayerPanel.disPlayShop(room, infoConfig,  room.shopItemClassifies.get(0)),  BedWarMain.getLanguage().getLanguage("shop-name","商店"));
                    }else{
                        DisPlayWindowsFrom.disPlayMenu((Player) player.getPlayer(),room,infoConfig);
                    }
                    break;
            }

        }

    }

    @Override
    public void saveNBT() {

    }

    @Override
    public boolean onInteract(Player player, Item item) {
        return true;
    }
}
