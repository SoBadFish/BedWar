package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

import java.util.*;

/**
 * 指向玩家
 * @author SoBadFish
 * 2022/1/6
 */
public class PointPlayer implements INbtItem{
    @Override
    public String getName() {
        return "compass-item";
    }

    @Override
    public boolean onClick(Item item, Player player) {
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
        if(info == null){
            return true;
        }
        GameRoom room = info.getGameRoom();
        PlayerInfo target = null;
        LinkedHashMap<PlayerInfo,Double> dis = new LinkedHashMap<>();
        for(PlayerInfo info1: room.getLivePlayers()){
            if(!info1.getTeamInfo().equals(info.getTeamInfo())){
                dis.put(info1,player.distance(info1.getPlayer()));
            }

        }
        List<Map.Entry<PlayerInfo, Double>> list = new ArrayList<>(dis.entrySet());
        list.sort(Comparator.comparingInt(o -> o.getValue().intValue()));
        if(list.size() > 0) {
            target = list.get(0).getKey();

            if (target != null) {
                info.sendTip(BedWarMain.getLanguage().getLanguage("compass-use-message","&a找到[1]\n&c距离: &r[2] 米",
                        target.toString(),
                        String.format("%.2f", list.get(0).getValue())));

            }
        }
        return true;

    }


}
