package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.thread.BaseTimerRunnable;

import java.util.Timer;
import java.util.TimerTask;

/**
 * 快速回城
 * @author SoBadFish
 * 2022/1/6
 */
public class BackHub implements INbtItem{

    public Timer timer = new Timer();

    @Override
    public String getName() {
        return "快速回城";
    }

    @Override
    public boolean onClick(Item item, Player player) {
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
        info.sendMessage("5秒后传送至出生点请不要移动");

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
                info.sendMessage("已传送到出生点");
                info.getPlayer().teleport(info.getTeamInfo().getTeamConfig().getSpawnPosition());
            }
        },5000);
        player.getInventory().removeItem(item);
        return true;
    }



}
