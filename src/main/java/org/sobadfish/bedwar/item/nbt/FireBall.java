package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import net.catrainbow.sakura.SakuraAPIAB;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.EntityFireBall;
import org.sobadfish.bedwar.player.PlayerInfo;

import java.util.LinkedHashMap;


/**
 * todo 加个冷却机制
 * @author SoBadFish
 * 2022/1/8
 */
public class FireBall implements INbtItem{


    private final LinkedHashMap<PlayerInfo,Long> clickTime = new LinkedHashMap<>();


    @Override
    public String getName() {
        return "fire-ball-item";
    }

    @Override
    public boolean onClick(Item item, Player player) {
        PlayerInfo playerInfo = BedWarMain.getRoomManager().getPlayerInfo(player);
        if(playerInfo != null) {
            try {
                Class<?> c = Class.forName("net.catrainbow.sakura.SakuraAPI");
                SakuraAPIAB sakuraAPI = (SakuraAPIAB) c.newInstance();
//                            net.catrainbow.sakura.SakuraAPIAB sakuraAPI = new net.catrainbow.sakura.SakuraAPI();
                sakuraAPI.addBypassTime((Player) playerInfo.getPlayer(), "KillAura", 2);
            } catch (Throwable ignore) {
            }
            if(clickTime.containsKey(playerInfo) && System.currentTimeMillis() - clickTime.get(playerInfo) < 500){
                playerInfo.sendMessage(BedWarMain.getLanguage().getLanguage("fire-boll-use-message"
                        ,"&c使用太频繁了 请过一会再试吧"));
                return true;
            }else{
                clickTime.put(playerInfo,System.currentTimeMillis());
            }



            Item ic = item.clone();
            ic.setCount(1);
            double f = 2.2D;
            double yaw = player.yaw;
            double pitch = player.pitch;
            playerInfo.isSpawnFire = true;
            Vector3 motion = new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f, Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f);
            Location pos = new Location(player.x - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 1.5D, player.y + (double) player.getEyeHeight(), player.z + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 1.5D, yaw, pitch, player.level);
            EntityFireBall fireBall = new EntityFireBall(player.chunk, Entity.getDefaultNBT(pos,motion));
            fireBall.setExplode(true);
            fireBall.setMaster(playerInfo);

            fireBall.spawnToAll();
            player.getInventory().removeItem(ic);
            playerInfo.isSpawnFire = false;
        }
        return true;
    }
}
