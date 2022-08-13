package org.sobadfish.bedwar.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;

public class BedWarFloatText extends Entity {

    public String text = "";

    private BedWarFloatText(FullChunk fullChunk, CompoundTag compoundTag) {
        super(fullChunk, compoundTag);
        this.setNameTagAlwaysVisible(true);
        this.setNameTagVisible(true);
        this.setNameTag(text);
    }

    @Override
    protected float getGravity() {
        return 0;
    }

    @Override
    public int getNetworkId() {
        return 64;
    }

    @Override
    public boolean attack(EntityDamageEvent entityDamageEvent) {
        return false;
    }


    public void setText(String text) {
        this.text = text;
        this.setNameTag(TextFormat.colorize('&',text));
    }

    public static BedWarFloatText showFloatText(Position position,String text){
        BedWarFloatText text1 = new BedWarFloatText(position.getChunk(),Entity.getDefaultNBT(position));
        text1.setText(text);
        text1.spawnToAll();
        return text1;
    }
}
