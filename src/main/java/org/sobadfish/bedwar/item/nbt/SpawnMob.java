package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.IronGolem;
import org.sobadfish.bedwar.player.PlayerInfo;

import java.util.ArrayList;

/**
 * 生成铁傀儡
 * @author SoBadFish
 * 2022/1/6
 */
public class SpawnMob implements INbtItem{
    @Override
    public String getName() {
        return "护卫";
    }

    @Override
    public boolean onClick(Item item, Player player) {
        PlayerInfo playerInfo = BedWarMain.getRoomManager().getPlayerInfo(player);
        if(playerInfo != null){
            ArrayList<Entity> entities = new ArrayList<>(player.getChunk().getEntities().values());
            int iron = 0;
            for(Entity entity: entities){
                if(entity instanceof IronGolem){
                    PlayerInfo playerInfo1 = ((IronGolem) entity).getMaster();
                    if(playerInfo1 != null && playerInfo1.equals(playerInfo)){
                        iron++;
                    }
                }
            }
            if(iron > 10){
                playerInfo.sendMessage("&c你已经在这片区域生成了 10 只铁傀儡 无法继续生成。");
                return true;
            }
            IronGolem golem = new IronGolem(playerInfo,player.chunk, Entity.getDefaultNBT(player));
            golem.setNameTagAlwaysVisible();
            golem.setNameTagVisible();
            golem.setNameTag(TextFormat.colorize('&',playerInfo.getTeamInfo().getTeamConfig().getNameColor()+playerInfo.getTeamInfo().getTeamConfig().getName()+" 的护卫"));
            golem.spawnToAll();
            playerInfo.sendMessage("&b你生成了一个护卫");
        }
        player.getInventory().removeItem(item);
        return true;
    }


}
