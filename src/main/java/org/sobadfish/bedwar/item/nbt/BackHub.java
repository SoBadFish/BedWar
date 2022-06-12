package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.thread.BaseTimerRunnable;

/**
 * 快速回城
 * @author SoBadFish
 * 2022/1/6
 */
public class BackHub implements INbtItem{
    @Override
    public String getName() {
        return "快速回城";
    }

    @Override
    public boolean onClick(Item item, Player player) {
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
        info.sendMessage("10秒后传送至出生点");
        ThreadManager.addThread(new BaseTimerRunnable(10) {
            @Override
            public void onRun() {
                if(info.isDeath()){
                    this.cancel();
                }
            }

            @Override
            protected void callback() {
                PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
                info.sendMessage("已传送到出生点");
                info.getPlayer().teleport(info.getTeamInfo().getTeamConfig().getSpawnPosition());
            }
        });
        player.getInventory().removeItem(item);
        return true;
    }


}
