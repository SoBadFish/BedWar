package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.template.BlockTemplateControl;
import org.sobadfish.bedwar.tools.Utils;
import org.sobadfish.bedwar.world.BlockPosition;

import java.util.List;

/**
 * 生成城堡
 * @author Sobadfish
 * @date 2024/8/26
 */
public class DefenseTowerItem implements INbtItem {
    @Override
    public String getName() {
        return "defense-tower";
    }


    @Override
    public boolean onClick(Item item, Player player) {
        Item cl = item.clone();
        cl.setCount(1);
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
        if(info != null && info.getGameRoom() != null){
            Position pos = info.getPlayer();
            List<BlockPosition> blocks = BlockTemplateControl.loadBlockTemplateByName(getName(),pos,info);
            for(TeamInfo teamInfo: info.getGameRoom().getTeamInfos()){
                if(teamInfo.getTeamConfig().getSpawnPosition().distance(pos) < 10){
                    info.sendMessage(BedWarMain.getLanguage().getLanguage("defense-tower-use-error","&c当前位置无法生成"));
                    return false;
                }
            }

            Utils.spawnBlock(info.getGameRoom(), blocks,false);
            info.sendMessage(BedWarMain.getLanguage().getLanguage("defense-tower-use-success","&a你生成了一座城堡"));
            player.getInventory().removeItem(cl);
            return true;
        }
        return false;


    }
}
