package org.sobadfish.bedwar.thread;

import cn.nukkit.entity.Entity;
import org.sobadfish.bedwar.entity.ShopVillage;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.room.GameRoom;

import java.util.ArrayList;

/**
 * 商店NPC保护线程
 * @author SoBadFish
 * 2022/1/10
 */
public class ProtectVillageThread extends ThreadManager.AbstractBedWarRunnable {

    private GameRoom room;

    public ProtectVillageThread(GameRoom room){
        this.room = room;
    }
    @Override
    public void run() {
        while (!room.close || isClose) {

            for (ShopVillage shopVillage : new ArrayList<>(room.getShopInfo().getShopVillages())) {
                if (shopVillage.getChunk() != null && shopVillage.getChunk().isLoaded()) {
                    if (shopVillage.isClosed()) {
                        ShopVillage respawnVillage = new ShopVillage(room.getRoomConfig(), shopVillage.getInfoConfig(), shopVillage.getChunk(), Entity.getDefaultNBT(shopVillage));
                        respawnVillage.yaw = shopVillage.yaw;
                        respawnVillage.spawnToAll();
                        room.getShopInfo().getShopVillages().remove(shopVillage);
                        room.getShopInfo().getShopVillages().add(respawnVillage);
                    }
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
                isClose = true;
            }
        }

    }

    @Override
    public GameRoom getRoom() {
        return room;
    }

    @Override
    public String getName() {
        return "商店NPC保护线程";
    }
}
