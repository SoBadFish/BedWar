package org.sobadfish.bedwar.world;

import cn.nukkit.block.Block;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;

/**
 * @author Sobadfish
 * @date 2024/8/19
 */
public class BlockVector3 {

    public Block block;

    public Vector3 position;


    public BlockVector3(Block block, Vector3 position){
        this.block = block;
        this.position = position;
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public BlockPosition asPosition(Position position){
        position.add(this.position.x, this.position.y, this.position.z);
        return new BlockPosition(block,position);
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }
}
