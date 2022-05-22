package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.thread.BlockBreakRunnable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * 生成蜘蛛网
 * @author SoBadFish
 * 2022/1/6
 */
public class Line implements INbtItem{
    @Override
    public String getName() {
        return "线";
    }

    @Override
    public boolean onClick(Item item, Player player) {
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);

        Position pos = info.getPlayer().getSide(info.getPlayer().getHorizontalFacing(),2);
        LinkedHashMap<Position,Block> spawn = BedWarMain.spawnBlockByPosAndSize(pos,3,Block.get(30));
        Platform.spawnBlock(item, player, spawn);
        info.sendMessage("&a已生成蜘蛛网");
        player.getInventory().removeItem(item);
        return true;
    }


}
