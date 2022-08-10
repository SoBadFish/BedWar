package org.sobadfish.bedwar.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityExplosive;
import cn.nukkit.entity.projectile.EntityProjectile;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityExplosionPrimeEvent;
import cn.nukkit.level.Explosion;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.CriticalParticle;
import cn.nukkit.nbt.tag.CompoundTag;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.tools.Utils;

/**
 * 感谢MobPlugin插件开发组提供的火球算法
 * @author @Mobplugin
 * 2022/1/8
 */
public class EntityFireBall extends EntityProjectile implements EntityExplosive {
    public static final int NETWORK_ID = 85;
    protected boolean critical;
    protected boolean canExplode;
    private PlayerInfo master;

    @Override
    public int getNetworkId() {
        return 85;
    }

    @Override
    public float getWidth() {
        return 0.45F;
    }

    @Override
    public float getHeight() {
        return 0.45F;
    }

    @Override
    public float getGravity() {
        return 0.00F;
    }

    @Override
    public float getDrag() {
        return 0.01F;
    }

    public EntityFireBall(FullChunk chunk, CompoundTag nbt) {
        this(chunk, nbt, (Entity)null);
    }

    public void setMaster(PlayerInfo master) {
        this.master = master;
    }



    public EntityFireBall(FullChunk chunk, CompoundTag nbt, Entity shootingEntity) {
        this(chunk, nbt, shootingEntity, false);
    }

    public EntityFireBall(FullChunk chunk, CompoundTag nbt, Entity shootingEntity, boolean critical) {
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
            if (!this.hadCollision && this.critical) {
                this.level.addParticle(new CriticalParticle(this.add((double)(this.getWidth() / 2.0F) + Utils.rand(-100.0D, 100.0D) / 500.0D, (double)(this.getHeight() / 2.0F) + Utils.rand(-100.0D, 100.0D) / 500.0D, (double)(this.getWidth() / 2.0F) + Utils.rand(-100.0D, 100.0D) / 500.0D)));
            } else if (this.onGround) {
                this.critical = false;
            }

            if (this.age > 1200 || this.isCollided) {
                if (this.isCollided && this.canExplode) {
                    this.explode();
                }

                this.close();
            }

            return super.onUpdate(currentTick);
        }
    }

    @Override
    public boolean attack(EntityDamageEvent source) {
        if (source instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)source).getDamager() instanceof Player) {
            this.setMotion(((EntityDamageByEntityEvent)source).getDamager().getLocation().getDirectionVector());
        }

        return true;
    }

    @Override
    public void explode() {
        EntityExplosionPrimeEvent event = new EntityExplosionPrimeEvent(this, 1);
        this.server.getPluginManager().callEvent(event);
        if (!event.isCancelled()) {
            Explosion explosion = new Explosion(this, 4, master.getPlayer());

            if (event.isBlockBreaking()) {
                explosion.explodeA();
            }

            explosion.explodeB();
        }
    }

}
