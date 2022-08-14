package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
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
        info.sendMessage("10秒后传送至出生点请不要移动");

        ThreadManager.addThread(new GoBackRunnable(player, player.getPosition(), 10));
        player.getInventory().removeItem(item);
        return true;
    }

    private static class GoBackRunnable extends BaseTimerRunnable{

        private final Position lastPos;

        private final Player player;

        public GoBackRunnable(Player player,Position lastPos,int end) {
            super(end);
            this.lastPos = lastPos;
            this.player = player;
        }

        @Override
        public void onRun() {
            PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
            if(lastPos.getFloorX() != player.getFloorX() && lastPos.getFloorZ() != player.getFloorZ()){
                this.cancel();
            }
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

        @Override
        public GameRoom getRoom() {
            return null;
        }

        @Override
        public String getThreadName() {
            return "道具返回出生点线程";
        }
    }


}
