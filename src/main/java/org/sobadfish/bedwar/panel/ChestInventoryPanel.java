package org.sobadfish.bedwar.panel;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.item.Item;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import lombok.Getter;
import org.sobadfish.bedwar.panel.items.BasePlayPanelItemInstance;
import org.sobadfish.bedwar.panel.lib.DoubleChestFakeInventory;
import org.sobadfish.bedwar.player.PlayerInfo;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author BadFish
 */
public class ChestInventoryPanel extends DoubleChestFakeInventory implements InventoryHolder {

    long id;

    private final PlayerInfo player;

    public int clickSolt;

    @Getter
    private Map<Integer, BasePlayPanelItemInstance> panel = new LinkedHashMap<>();

    ChestInventoryPanel(PlayerInfo player, InventoryHolder holder, String name) {
        super(holder);
        this.player = player;
        this.setName(name);
    }

    public void setPanel(Map<Integer, BasePlayPanelItemInstance> panel){
        Map<Integer, BasePlayPanelItemInstance> m = new LinkedHashMap<>();
        LinkedHashMap<Integer,Item> map = new LinkedHashMap<>();
        for(Map.Entry<Integer,BasePlayPanelItemInstance> entry : panel.entrySet()){
            Item value = entry.getValue().getPanelItem(getPlayerInfo(),entry.getKey()).clone();
            map.put(entry.getKey(),value);
            m.put(entry.getKey(),entry.getValue());
        }
        setContents(map);
        this.panel = m;
    }

    public void update(){
        setPanel(panel);
    }

    public PlayerInfo getPlayerInfo(){
        return player;
    }

    public Player getPlayer() {
        return (Player) player.getPlayer();
    }


    @Override
    public void setName(String name) {
        super.setName(name);
    }

    @Override
    public void onSlotChange(int index, Item before, boolean send) {
        super.onSlotChange(index, before, send);
    }


    @Override
    public void onOpen(Player who) {
        super.onOpen(who);
        ContainerOpenPacket pk = new ContainerOpenPacket();
        pk.windowId = who.getWindowId(this);
        pk.entityId = id;
        pk.type = InventoryType.DOUBLE_CHEST.getNetworkType();
        who.dataPacket(pk);
    }

    @Override
    public void onClose(Player who) {
        clearAll();
        RemoveEntityPacket pk = new RemoveEntityPacket();
        pk.eid = id;
        who.dataPacket(pk);
        super.onClose(who);

    }

    @Override
    public Inventory getInventory() {
        return this;
    }



}
