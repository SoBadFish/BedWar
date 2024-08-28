package org.sobadfish.bedwar.entity;


import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;

import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.entity.baselib.BaseEntityMove;
import org.sobadfish.bedwar.player.PlayerInfo;



/**
 * @author SoBadFish
 * 2022/1/10
 */
public class IronGolem extends BaseEntityMove implements DamageEntity {
    public static final int NETWORK_ID = 20;

    public IronGolem(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public IronGolem(PlayerInfo master,FullChunk chunk, CompoundTag nbt) {
        super(master,chunk, nbt);
    }

    @Override
    public float getGravity() {
        return 2.0F;
    }


    @Override
    protected void initEntity() {
        this.setMaxHealth(30);
        super.initEntity();


    }

    @Override
    public String getName() {
        return TextFormat.colorize('&',getMaster().toString());
    }

    @Override
    public boolean onUpdate(int currentTick) {
        boolean b =  super.onUpdate(currentTick);
        if(b) {
            if (followTarget != null) {
                double npcx = this.x - followTarget.x;
                double npcy = this.y - followTarget.y;
                double npcz = this.z - followTarget.z;
                double yaw = Math.asin(npcx / Math.sqrt(npcx * npcx + npcz * npcz)) / 3.14D * 180.0D;
                double pitch = (double) Math.round(Math.asin(npcy / Math.sqrt(npcx * npcx + npcz * npcz + npcy * npcy)) / 3.14D * 180.0D);
                if (npcz > 0.0D) {
                    yaw = -yaw + 180.0D;
                }
                this.yaw = yaw;
                this.pitch = pitch;
            }
        }
        return b;
    }

    @Override
    public int getNetworkId() {
        return 20;
    }

    @Override
    public float getWidth() {
        return 1.4F;
    }

    @Override
    public float getHeight() {
        return 2.7F;
    }

    @Override
    public double getSpeed() {
        return 0.8D;
    }

    @Override
    public void saveNBT() {

    }

    @Override
    public void onUpdate() {}


    @Override
    public void onAttack(EntityDamageEvent entity) {
        if(entity instanceof EntityDamageByEntityEvent){
            ((EntityDamageByEntityEvent) entity).setKnockBack(0.1f);
        }
    }

    @Override
    public void attackEntity(EntityCreature player) {
        if (this.attackDelay > 23 && this.distanceSquared(player) < 4.0D) {
            this.attackDelay = 0;
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, getDamage(),0.8f));

        }
    }


    @Override
    public void onClose() {

    }
}
