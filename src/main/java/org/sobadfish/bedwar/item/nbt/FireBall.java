package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.EntityFireBall;
import org.sobadfish.bedwar.player.PlayerInfo;


/**
 * @author SoBadFish
 * 2022/1/8
 */
public class FireBall implements INbtItem{



    @Override
    public String getName() {
        return "火球";
    }

    @Override
    public boolean onClick(Item item, Player player) {
        PlayerInfo playerInfo = BedWarMain.getRoomManager().getPlayerInfo(player);
        if(playerInfo != null) {
            double f = 1.8D;
            double yaw = player.yaw;
            double pitch = player.pitch;
            Location pos = new Location(player.x - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 1.5D, player.y + (double) player.getEyeHeight(), player.z + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 1.5D, yaw, pitch, player.level);
            EntityFireBall fireBall = new EntityFireBall(player.chunk, Entity.getDefaultNBT(pos));
            fireBall.setExplode(true);
            fireBall.setMaster(playerInfo);
            fireBall.setMotion(new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f, Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f));
            fireBall.spawnToAll();
            player.getInventory().removeItem(item);
        }
        return true;
    }
}
