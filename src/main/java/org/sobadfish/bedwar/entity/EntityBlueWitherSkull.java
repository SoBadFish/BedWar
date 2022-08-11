package org.sobadfish.bedwar.entity;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.event.entity.ExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.GameRule;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.SmokeParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.potion.Effect;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.tools.Utils;

/**
 * 感谢MobPlugin插件开发组提供的凋零头炸弹算法
 * @author @Mobplugin
 * 2022/1/8
 */
public class EntityBlueWitherSkull extends EntityProjectile implements EntityExplosive {

    public static final int NETWORK_ID = 89;

    protected boolean critical;

    protected boolean canExplode;

    private PlayerInfo master;

    @Override
    public int getNetworkId() {
        return 89;
    }

    @Override
    public float getWidth() {
        return 0.25F;
    }

    @Override
    public float getLength() {
        return 0.25F;
    }

    @Override
    public float getHeight() {
        return 0.25F;
    }

    @Override
    public float getGravity() {
        return 0.01F;
    }

    @Override
    public float getDrag() {
        return 0.01F;
    }

    public EntityBlueWitherSkull(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, (Entity)null);
    }

    public EntityBlueWitherSkull(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        this(chunk, nbt, shootingEntity, false);
    }

    public EntityBlueWitherSkull(FullChunk chunk, CompoundTag nbt, Entity shootingEntity, boolean critical) {
        super(chunk, nbt, shootingEntity);
        this.canExplode = false;
        this.critical = critical;
    }

    public boolean isExplode() {
        return this.canExplode;
    }

    public void setExplode(boolean bool) {
        this.canExplode = bool;
    }

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        } else {
            if (this.age <= 1200 && !this.hadCollision) {
                this.level.addParticle(new SmokeParticle(this.add((double)(this.getWidth() / 2.0F) + Utils.rand(-100.0D, 100.0D) / 500.0D, (double)(this.getHeight() / 2.0F) + Utils.rand(-100.0D, 100.0D) / 500.0D, (double)(this.getWidth() / 2.0F) + Utils.rand(-100.0D, 100.0D) / 500.0D)));
            } else {
                if (this.canExplode) {
                    this.explode();
                }

                this.close();
            }

            return super.onUpdate(currentTick);
        }
    }

    public void setMaster(PlayerInfo master) {
        this.master = master;
    }

    public PlayerInfo getMaster() {
        return master;
    }

    @Override
    public void onCollideWithEntity(Entity entity) {
        super.onCollideWithEntity(entity);
        entity.addEffect(Effect.getEffect(20).setAmplifier(1).setDuration(140));
    }

    @Override
    public void explode() {
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, 4.0D);
        this.server.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            Explosion explosion = new Explosion(this, event.getForce(), master.getPlayer());
            if (event.isBlockBreaking()) {
                explosion.explodeA();
            }

            explosion.explodeB();
        }
    }

}
