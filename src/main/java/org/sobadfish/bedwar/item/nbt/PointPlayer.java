package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.room.GameRoom;

/**
 * 指向玩家
 * @author SoBadFish
 * 2022/1/6
 */
public class PointPlayer implements INbtItem{
    @Override
    public String getName() {
        return "指南针";
    }

    @Override
    public boolean onClick(Item item, Player player) {
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
        if(info == null){
            return true;
        }
        GameRoom room = info.getGameRoom();
        PlayerInfo target = null;
        for(TeamInfo teamInfo : room.getLiveTeam()){
            if(!teamInfo.equals(info.getTeamInfo())){
                target = teamInfo.getLivePlayer().get(0);
            }
        }
        if(target != null){
            info.sendTip("&a找到"+target+"\n"+"&c距离: &r"+String.format("%.2f",info.getPlayer().distance(target.getPlayer()))+" 米");

        }
        return true;

    }


}
