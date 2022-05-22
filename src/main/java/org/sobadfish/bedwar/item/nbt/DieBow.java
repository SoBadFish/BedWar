package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBow;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.EntityBlueWitherSkull;
import org.sobadfish.bedwar.entity.EntityFireBall;
import org.sobadfish.bedwar.player.PlayerInfo;

import java.util.Map;

/**
 * 凋零弓
 * @author Sobadfish
 * 2022/5/21
 */
public class DieBow implements INbtItem{


    @Override
    public String getName() {
        return "凋零弓";
    }



    @Override
    public boolean onClick(Item item, Player player) {
        return false;
    }

    public void onSend(Player player){
        PlayerInfo playerInfo = BedWarMain.getRoomManager().getPlayerInfo(player);
        if(playerInfo != null) {
            double f = 1.2D;
            double yaw = player.yaw;
            double pitch = player.pitch;
            Location pos = new Location(player.x - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 1.5D, player.y + (double) player.getEyeHeight(), player.z + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 1.5D, yaw, pitch, player.level);
            EntityBlueWitherSkull fireBall = new EntityBlueWitherSkull(player.chunk, Entity.getDefaultNBT(pos));
            fireBall.setExplode(true);
            fireBall.setMaster(playerInfo);
            fireBall.setMotion(new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f, Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f));
            fireBall.spawnToAll();
        }

    }



}
