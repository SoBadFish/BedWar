package org.sobadfish.bedwar.entity;

import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import org.sobadfish.bedwar.manager.SkinManager;

/**
 * @author Sobadfish
 * @date 2023/11/4
 */
public class FloatBlock extends EntityHuman {


    public FloatBlock(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);


    }

    @Override
    public void saveNBT() {

    }

    public static void spawnToLocation(Position pos,String skinName){
        CompoundTag tag = EntityHuman.getDefaultNBT(pos);
        Skin skin = SkinManager.getSkinByName(skinName);
        if(skin == null){
            return;
        }
        tag.putCompound("Skin",new CompoundTag()
                .putByteArray("Data", skin.getSkinData().data)
                .putString("ModelId",skin.getSkinId())
        );
        FloatBlock floatBlock = new FloatBlock(pos.getChunk(), tag);
        floatBlock.setSkin(skin);
        floatBlock.setScale(0.5f);
        floatBlock.spawnToAll();




    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.isAlive()) {

            this.yaw += 2;

        }



        return super.onUpdate(currentTick);
    }


}
