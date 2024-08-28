package org.sobadfish.bedwar.entity.baselib;


import cn.nukkit.Player;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import lombok.Getter;
import lombok.Setter;
import org.sobadfish.bedwar.entity.DamageEntity;


/**
 * 感谢MobPlugin插件开发组提供的AI算法
 * @author @Mobplugin
 * 2022/1/8
 */

public abstract class BaseEntity extends EntityCreature {


    //停留
    public int stayTime = 0;

    public int moveTime = 0;
    //目标
    public Vector3 target = null;
    //锁定生物
    protected EntityCreature followTarget = null;

    @Getter
    @Setter
    private boolean movement = true;

    protected int attackDelay = 0;

    private int damageDelay = 0;

    BaseEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);

    }

    @Override
    protected void initEntity() {
        super.initEntity();
        if (this.namedTag.contains("Movement")) {
            this.setMovement(this.namedTag.getBoolean("Movement"));
        }

        if (this.namedTag.contains("Age")) {
            this.age = this.namedTag.getShort("Age");
        }

    }



    public boolean isKnockback() {
        return this.attackTime > 0;
    }



    public double getSpeed() {
        //移动速度
        return 1.0f;
    }


    public void setFollowTarget(EntityCreature target) {
        this.followTarget = target;
        this.moveTime = 0;
        this.stayTime = 0;
        this.target = null;
    }



    boolean isPlayerTarget(Player player){
        return !player.closed  && player.isAlive() && (player.isSurvival() || player.isAdventure()) ;
    }



    public boolean targetOption(EntityCreature creature, double distance) {
        if (!(this instanceof DamageEntity)) {
            return true;
        } else if (creature instanceof Player ) {
            return creature.closed  || !creature.isAlive()  || !(distance <= 80.0D) ||
                    !creature.getLevel().getFolderName().equalsIgnoreCase(getLevel().getFolderName());
        }else{
            return creature.closed || !creature.isAlive() || !creature.getLevel().getFolderName().equalsIgnoreCase(getLevel().getFolderName());
        }

    }


    @Override
    public boolean entityBaseTick(int tickDiff) {
        super.entityBaseTick(tickDiff);
        if (!this.isAlive()) {
            this.close();
        }

        if ((this instanceof DamageEntity) && this.attackDelay < 1000) {
            ++this.attackDelay;
        }
        if (this instanceof DamageEntity && this.damageDelay < 1000) {
            ++this.damageDelay;
        }
        onUpdate();
        return true;
    }

    /**
     * 更新
     * */
    public abstract void onUpdate();

    /**
     * 被攻击
     * @param entity 伤害事件
     * */
    public abstract void onAttack(EntityDamageEvent entity);


    @Override
    public boolean attack(EntityDamageEvent source) {
        if (this.isKnockback() && source instanceof EntityDamageByEntityEvent && ((EntityDamageByEntityEvent)source).getDamager() instanceof Player) {
            return false;
        } else if (this.fireProof && (source.getCause()
                == EntityDamageEvent.DamageCause.FIRE || source.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK
                || source.getCause() == EntityDamageEvent.DamageCause.LAVA)) {
            return false;
        } else {
            if (source instanceof EntityDamageByEntityEvent) {
                ((EntityDamageByEntityEvent)source).setKnockBack(0.3F);
                onAttack(source);
            }
            this.target = null;
            this.stayTime = 0;
            super.attack(source);
            return true;
        }
    }

    /**攻击生物
     * @param player 生物
     * */
    abstract public void attackEntity(EntityCreature player);

    /**
     * 生物伤害
     * @return 伤害值
     * */
    abstract public float getDamage();

}
