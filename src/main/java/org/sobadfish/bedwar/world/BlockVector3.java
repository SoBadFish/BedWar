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

    @Override
    public String toString() {
        return "BlockVector3{" +
                "block=" + block +
                ", position=" + position +
                '}';
    }

    public Block getBlock() {
        return block;
    }

    public void setBlock(Block block) {
        this.block = block;
    }

    public BlockPosition asPosition(Position position){
        Position p2 = new Position(position.getX() + this.position.x,
                position.getY() + this.position.y,
                position.getZ() + this.position.z,position.level);
        return new BlockPosition(block,p2);
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }
}
