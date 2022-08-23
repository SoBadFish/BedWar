package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

import java.util.*;
import java.util.concurrent.TimeUnit;


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
        Timer timer = new Timer();
        try{
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    for(Position block: new ArrayList<>(spawn.keySet())){
                        if(info.getGameRoom() == null || info.getGameRoom().getType() != GameRoom.GameType.START){
                            return;
                        }
                        if(info.getGameRoom().worldInfo.onChangeBlock(block.getLevelBlock(),false)){
                            block.getLevel().setBlock(block,new BlockAir());
                        }
                    }
                }
            },5000);
        }catch (Exception ignore){

        }


        player.getInventory().removeItem(item);
    }


}
