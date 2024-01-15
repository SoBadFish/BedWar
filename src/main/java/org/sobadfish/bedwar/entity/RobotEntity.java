package org.sobadfish.bedwar.entity;

import cn.nukkit.entity.EntityHuman;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

/**
 * 单纯可以进入游戏房间的实体
 * @author Sobadfish
 *  2023/1/13
 */
public class RobotEntity extends EntityHuman {
    public RobotEntity(FullChunk fullChunk, CompoundTag compoundTag) {
        super(fullChunk, compoundTag);
    }


    @Override
    public void addMotion(double v, double v1, double v2) {
        super.addMotion(v, v1, v2);
        this.motionX = v;
    }

    @Override
    public void saveNBT() {}
}
