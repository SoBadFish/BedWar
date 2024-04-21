package org.sobadfish.bedwar.entity;

import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.data.IntEntityData;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.LevelSoundEventPacket;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.tools.Utils;


/**
 * @author Sobadfish
 * 14:11
 */
public class EntityTnt extends EntityProjectile implements EntityExplosive {

    public static final int NETWORK_ID = EntityPrimedTNT.NETWORK_ID;

    @Override
    public float getWidth() {
        return 0.98f;
    }

    @Override
    public float getLength() {
        return 0.98f;
    }

    @Override
    public int getNetworkId() {
        return NETWORK_ID;
    }

    @Override
    public float getHeight() {
        return 0.98f;
    }

    @Override
    public float getGravity() {
        return 0.08f;
    }

    @Override
    protected float getDrag() {
        return 0.02f;
    }

    @Override
    protected float getBaseOffset() {
        return 0.49f;
    }

    @Override
    public boolean canCollide() {
        return false;
    }

    protected int fuse;


    public PlayerInfo target;

    public EntityTnt(FullChunk fullChunk, CompoundTag compoundTag, PlayerInfo playerInfo, int fuse) {
        super(fullChunk, compoundTag);
        this.target = playerInfo;
        this.fuse = fuse;
        this.setNameTagVisible(true);
        this.setNameTagAlwaysVisible(true);
        resetNameTag();
        this.setDataProperty(new IntEntityData(55, this.fuse));
        this.getLevel().addLevelSoundEvent(this, 27);
    }

    @Override
    protected void initEntity() {
        super.initEntity();
        this.setDataFlag(DATA_FLAGS, DATA_FLAG_IGNITED, true);
        this.setDataProperty(new IntEntityData(DATA_FUSE_LENGTH, fuse));

        this.getLevel().addLevelSoundEvent(this, LevelSoundEventPacket.SOUND_FIZZ);
    }


    public PlayerInfo getTarget() {
        return target;
    }


    @Override
    public String getName() {
        return "TNT";
    }

    @Override
    public boolean onUpdate(int i) {
        int var2 = i - this.lastUpdate;
        resetNameTag();

        updateMovement();
        fuse -= var2;
        if (this.fuse % 5 == 0) {
            this.setDataProperty(new IntEntityData(55, this.fuse));
        }
        if (fuse <= 0) {
            if (this.level.getGameRules().getBoolean(GameRule.TNT_EXPLODES)) {
                explode();
            }
            this.close();
        }
        return super.onUpdate(i);
    }


    @Override
    public boolean attack(EntityDamageEvent source) {
        return source.getCause() == EntityDamageEvent.DamageCause.VOID && super.attack(source);
    }

    private void resetNameTag(){
        int time = 0;
        if(fuse > 0){
            time = fuse / 20;
        }
        setNameTag(Utils.formatTime1(time));



    }


    @Override
    public void explode() {
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, 4);
        server.getPluginManager().callEvent(event);
        if (event.isCancelled()) {
            return;
        }
        Explosion explosion = new Explosion(this, event.getForce(), this);
        if (event.isBlockBreaking()) {
            explosion.explodeA();
        }
        explosion.explodeB();
    }
}
