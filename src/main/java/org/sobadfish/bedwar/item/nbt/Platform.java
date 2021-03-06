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
 * 生成一个平台
 * @author SoBadFish
 * 2022/1/5
 */
public class Platform implements INbtItem {


    @Override
    public String getName() {
        return "简易平台";
    }

    @Override
    public boolean onClick(Item item, Player player) {
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);

        Position pos = info.getPlayer().add(0,-10);
        LinkedHashMap<Position,Block> spawn = BedWarMain.spawnBlockByPosAndSize(pos,3,Block.get(165));
        spawnBlock(item, player, spawn);
        info.sendMessage("&a已生成平台");
        return true;
    }

    static void spawnBlock(Item item, Player player, LinkedHashMap<Position, Block> spawn) {
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
        if(info == null){
            return;
        }
        for(Map.Entry<Position,Block> block: spawn.entrySet()){
            if(block.getKey().getLevelBlock().getId() == 0) {
                if(info.getGameRoom().worldInfo.onChangeBlock(block.getKey().getLevelBlock(),true)){
                    block.getKey().getLevel().setBlock(block.getKey(), block.getValue(), true, true);
                }
            }

        }
        ThreadManager.addThread(new BlockBreakRunnable(info.getGameRoom(),new ArrayList<>(spawn.keySet()),10));
        player.getInventory().removeItem(item);
    }


}
