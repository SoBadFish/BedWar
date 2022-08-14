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

        ThreadManager.addThread(new GoBackRunnable(info, player.getPosition(), 10));
        player.getInventory().removeItem(item);
        return true;
    }

    private static class GoBackRunnable extends BaseTimerRunnable{

        private final Position lastPos;

        private final PlayerInfo player;

        GoBackRunnable(PlayerInfo player, Position lastPos, int end) {
            super(end);
            this.lastPos = lastPos;
            this.player = player;
        }

        @Override
        public void onRun() {
            if(lastPos.getFloorX() != player.getPlayer().getFloorX() && lastPos.getFloorZ() != player.getPlayer().getFloorZ()){
                this.cancel();
            }
            if(player.isDeath()){
                this.cancel();
            }
        }

        @Override
        protected void callback() {

            player.sendMessage("已传送到出生点");
            player.getPlayer().teleport(player.getTeamInfo().getTeamConfig().getSpawnPosition());
        }

        @Override
        public GameRoom getRoom() {
            return null;
        }

        @Override
        public String getName() {
            return "道具返回出生点线程";
        }
    }


}
