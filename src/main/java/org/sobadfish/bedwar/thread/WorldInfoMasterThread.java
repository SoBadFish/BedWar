package org.sobadfish.bedwar.thread;

import cn.nukkit.entity.Entity;
import cn.nukkit.scheduler.PluginTask;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.ShopVillage;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.world.WorldInfo;

import java.util.ArrayList;

public class WorldInfoMasterThread extends PluginTask<BedWarMain> {

    private final WorldInfo worldInfo;
    private final GameRoom room;

    public WorldInfoMasterThread(GameRoom room,WorldInfo worldInfo,BedWarMain bedWarMain) {
        super(bedWarMain);
        this.worldInfo = worldInfo;
        this.room = room;
    }

    @Override
    public void onRun(int i) {
        if (worldInfo != null){
            if(!worldInfo.onUpdate()){
                return;
            }
            for (ShopVillage shopVillage : new ArrayList<>(room.getShopInfo().getShopVillages())) {
                if (shopVillage.isClosed()) {
                    ShopVillage respawnVillage = new ShopVillage(room.getRoomConfig(), shopVillage.getInfoConfig(), shopVillage.getChunk(), Entity.getDefaultNBT(shopVillage));
                    respawnVillage.yaw = shopVillage.yaw;
                    respawnVillage.spawnToAll();
                    room.getShopInfo().getShopVillages().remove(shopVillage);
                    room.getShopInfo().getShopVillages().add(respawnVillage);
                }
            }
        }
    }
}
