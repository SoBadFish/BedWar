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
            if(room.worldInfo.onChangeBlock(block.getLevelBlock(),false)){
                block.getLevel().setBlock(block,new BlockAir());
            }
        }
    }

    @Override
    public GameRoom getRoom() {
        return room;
    }

    @Override
    public String getName() {
        return "生成方块自动破坏线程";
    }
}
