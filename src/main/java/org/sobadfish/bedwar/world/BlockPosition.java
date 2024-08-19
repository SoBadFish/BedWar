package org.sobadfish.bedwar.world;

import cn.nukkit.block.Block;
import cn.nukkit.level.Position;

/**
 * @author Sobadfish
 * @date 2024/8/19
 */
public class BlockPosition {

    public Block block;

    public Position position;

    public BlockPosition(Block block,Position position){
        this.block = block;
        this.position = position;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
