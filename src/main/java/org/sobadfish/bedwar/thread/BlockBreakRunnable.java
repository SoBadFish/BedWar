package org.sobadfish.bedwar.thread;


import cn.nukkit.block.BlockAir;
import cn.nukkit.level.Position;
import org.sobadfish.bedwar.room.GameRoom;

import java.util.ArrayList;

/**
 * @author SoBadFish
 * 2022/1/7
 */
public class BlockBreakRunnable extends BaseTimerRunnable {

    private ArrayList<Position> blocks;

    private GameRoom room;

    public BlockBreakRunnable(GameRoom room,ArrayList<Position> blocks, int end) {
        super(end);
        this.blocks = blocks;
        this.room = room;
    }

    @Override
    protected void callback() {
        for(Position block: blocks){
            block.getLevel().setBlock(block,new BlockAir());
            room.worldInfo.onChangeBlock(block.getLevelBlock(),false);
        }
    }
}
