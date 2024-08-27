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
 * @author Sobadfish
 * @date 2024/8/26
 */
public class Silverfish extends BaseEntityMove {
    public static final int NETWORK_ID = 39;


    public Silverfish(PlayerInfo master, FullChunk chunk, CompoundTag nbt) {
        super(master,chunk, nbt);
    }

    @Override
    public int getNetworkId() {
        return 39;
    }

    @Override
    public float getWidth() {
        return 0.4F;
    }

    @Override
    public float getHeight() {
        return 0.3F;
    }

    @Override
    public double getSpeed() {
        return 1.4D;
    }

    @Override
    public void onUpdate() {

    }

    @Override
    public void initEntity() {
        this.setMaxHealth(8);
        super.initEntity();

    }

    @Override
    public String getName() {
        return TextFormat.colorize('&',getMaster().toString());
    }


    public Silverfish(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    @Override
    public boolean onUpdate(int currentTick)  {
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
    public float getDamage() {
        return 1.0f;
    }

    @Override
    public void attackEntity(EntityCreature player) {
        if (this.attackDelay > 23 && this.distanceSquared(player) < 8.0D) {
            this.attackDelay = 0;
            player.attack(new EntityDamageByEntityEvent(this, player, EntityDamageEvent.DamageCause.ENTITY_ATTACK, getDamage(),0.2f));

        }
    }


    @Override
    public void onAttack(EntityDamageEvent entity) {

    }



    @Override
    public void onClose() {

    }


}
