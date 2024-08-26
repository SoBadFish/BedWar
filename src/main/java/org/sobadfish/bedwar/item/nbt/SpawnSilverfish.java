package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.math.Vector3;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.BedWarEntitySnowBall;
import org.sobadfish.bedwar.entity.Silverfish;
import org.sobadfish.bedwar.player.PlayerInfo;

import java.util.ArrayList;

/**
 * 生成蠹虫
 * @author SoBadFish
 * 2022/1/6
 */
public class SpawnSilverfish implements INbtItem,IDropItem{
    @Override
    public String getName() {
        return "silverfish-item";
    }

    @Override
    public boolean onClick(Item item, Player player) {
        PlayerInfo playerInfo = BedWarMain.getRoomManager().getPlayerInfo(player);
        if(playerInfo != null){
            Item ic = item.clone();
            ic.setCount(1);
            double f = 4.2D;
            double yaw = player.yaw;
            double pitch = player.pitch;
            playerInfo.isSpawnFire = true;
            Vector3 motion = new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f, Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f);
            Location pos = new Location(player.x - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 1.5D, player.y + (double) player.getEyeHeight(), player.z + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 1.5D, yaw, pitch, player.level);
            BedWarEntitySnowBall fireBall = new BedWarEntitySnowBall(player.chunk, Entity.getDefaultNBT(pos,motion));
            fireBall.setMaster(playerInfo);
            fireBall.setUsedItem(this);

            fireBall.spawnToAll();
            player.getInventory().removeItem(ic);
        }
        return true;
    }


    @Override
    public void onRun(PlayerInfo playerInfo) {

        if(playerInfo != null) {
            ArrayList<Entity> entities = new ArrayList<>(playerInfo.getPlayer().getChunk().getEntities().values());
            int iron = 0;
            for (Entity entity : entities) {
                if (entity instanceof Silverfish) {
                    PlayerInfo playerInfo1 = ((Silverfish) entity).getMaster();
                    if (playerInfo1 != null && playerInfo1.equals(playerInfo)) {
                        iron++;
                    }
                }
            }
            if (iron > 20) {
                playerInfo.sendMessage(BedWarMain.getLanguage().getLanguage("silverfish-use-error", "&c你已经在这片区域生成了 20 只蠹虫 无法继续生成。"));
                return;
            }
            Silverfish golem = new Silverfish(playerInfo, playerInfo.getPlayer().chunk, Entity.getDefaultNBT(playerInfo.getPlayer()));
            golem.setNameTagAlwaysVisible();
            golem.setNameTagVisible();
            golem.setNameTag(TextFormat.colorize('&', BedWarMain.getLanguage().getLanguage("silverfish-named-tag", "[1]  &r的蠹虫",
                    playerInfo.getTeamInfo().getTeamConfig().getNameColor() + playerInfo.getTeamInfo().getTeamConfig().getName())));
            golem.spawnToAll();
            playerInfo.sendMessage(BedWarMain.getLanguage().getLanguage("silverfish-use-success", "&b你生成了一只蠹虫"));
        }
    }
}
