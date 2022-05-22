package org.sobadfish.bedwar.thread;

import cn.nukkit.block.Block;
import org.sobadfish.bedwar.world.WorldInfo;

/**
 * @author SoBadFish
 * 2022/1/12
 */
public class AddBlockRunnable implements Runnable{

    private Block block;

    private WorldInfo worldInfo;

    public AddBlockRunnable(Block block,WorldInfo worldInfo){
        this.block = block;
        this.worldInfo = worldInfo;
    }
    @Override
    public void run() {
        worldInfo.onChangeBlock(block,true);
    }
}
