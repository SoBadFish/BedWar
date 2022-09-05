package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.tools.Utils;

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
            Utils.launchWitherSkull(playerInfo);
        }

    }

}
