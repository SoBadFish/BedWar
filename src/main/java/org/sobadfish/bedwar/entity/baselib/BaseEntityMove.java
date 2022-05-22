package org.sobadfish.bedwar.entity.baselib;

import cn.nukkit.Player;
import cn.nukkit.block.*;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityCreature;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.level.particle.BubbleParticle;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.math.Vector2;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.tools.Utils;
import org.sobadfish.bedwar.player.PlayerInfo;


/**
 * 感谢MobPlugin插件开发组提供的AI算法
 * @author @Mobplugin
 * 2022/1/8
 */
public abstract class BaseEntityMove extends BaseEntity {


    private boolean canAttack = true;

    private PlayerInfo master;
    public BaseEntityMove(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public BaseEntityMove(PlayerInfo master,FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
        this.master = master;
    }

    private boolean checkJump(double dx, double dz) {
        if (this.motionY == (double)(this.getGravity() * 2.0F)) {
            return this.level.getBlock(new Vector3((double) NukkitMath.floorDouble(this.x), (double)((int)this.y), (double)NukkitMath.floorDouble(this.z))) instanceof BlockLiquid;
        } else if (this.level.getBlock(new Vector3((double)NukkitMath.floorDouble(this.x), (double)((int)(this.y + 0.8D)), (double)NukkitMath.floorDouble(this.z))) instanceof BlockLiquid) {
            this.motionY = (double)(this.getGravity() * 2.0F);
            return true;
        } else if (this.onGround && this.stayTime <= 0) {
            Block that = this.getLevel().getBlock(new Vector3((double)NukkitMath.floorDouble(this.x + dx), (double)((int)this.y), (double)NukkitMath.floorDouble(this.z + dz)));
            if (this.getDirection() == null) {
                return false;
            } else {
                Block block = that.getSide(this.getHorizontalFacing());
                if (!block.canPassThrough() && block.up().canPassThrough() && that.up(2).canPassThrough()) {
                    if (!(block instanceof BlockFence) && !(block instanceof BlockFenceGate)) {
                        if (this.motionY <= (double)(this.getGravity() * 4.0F)) {
                            this.motionY = (double)(this.getGravity() * 4.0F);
                        } else if (block instanceof BlockStairs || block instanceof BlockSlab) {
                            this.motionY = (double)(this.getGravity() * 4.0F);
                        } else if (this.motionY <= (double)(this.getGravity() * 8.0F)) {
                            this.motionY = (double)(this.getGravity() * 8.0F);
                        } else {
                            this.motionY += (double)this.getGravity() * 0.25D;
                        }
                    } else {
                        this.motionY = (double)this.getGravity();
                    }

                    return true;
                } else {
                    return false;
                }
            }
        } else {
            return false;
        }
    }

    public void setMaster(PlayerInfo master) {
        this.master = master;
    }

    public PlayerInfo getMaster() {
        return master;
    }

    @Override
    public void setFollowTarget(EntityCreature target) {
        this.setFollowTarget(target, true);
    }

    public void setFollowTarget(EntityCreature target, boolean attack) {
        super.setFollowTarget(target);
        this.canAttack = attack;
    }

    @Override
    public float getDamage() {
        return 4.0f;
    }



    private void checkTarget() {
        if (!this.isKnockback()) {
            if (this.followTarget == null || this.followTarget.closed || !this.followTarget.isAlive()
                    || this.targetOption(this.followTarget, this.distanceSquared(this.followTarget)) || this.target == null) {

                double near = 2.147483647E9D;
                double distance;
                int seeSize = 15;
                if(followTarget == null){
                    if (this.passengers.isEmpty()){
                        for (Entity entity : Utils.getAroundPlayers(this,seeSize,true)) {
                            if(entity instanceof BaseEntity){
                                continue;
                            }
                            if(entity.getNetworkId() == 19 || entity.getNetworkId() == 30){
                                continue;
                            }
                            if(entity instanceof EntityCreature) {
                                if (entity != this) {
                                    near = getAttackChunk(near, entity);
                                }
                            }

                        }
                    }
                    //随便走..
                    if (this.followTarget == null || this.followTarget.closed || !this.followTarget.isAlive() || this.targetOption(this.followTarget,
                            this.distanceSquared(this.followTarget)) || this.target == null) {
                        int x,z;
                        if (this.stayTime > 0) {
                            if (Utils.rand(1, 1000) > 50){
                                return;
                            }
                            x = Utils.rand(10, 30);
                            z = Utils.rand(10, 30);
                            this.target = this.add(Utils.rand(1,100) <= 40 ? (double)x : (double)(-x), Utils.rand(-20.0D, 20.0D) / 10.0D, Utils.rand(1,100) <= 60 ? (double)z : (double)(-z));
                        } else if (Utils.rand(1, 100) == 1) {
                            x = Utils.rand(10, 30);
                            z = Utils.rand(10, 30);
                            this.stayTime = Utils.rand(100, 200);
                            this.target = this.add(Utils.rand(1,100) <= 40 ? (double)x : (double)(-x), Utils.rand(-20.0D, 20.0D) / 10.0D, Utils.rand(1,100) <= 60 ? (double)z : (double)(-z));
                        } else if (this.moveTime <= 0 || this.target == null) {
                            x = Utils.rand(20, 100);
                            z = Utils.rand(20, 100);
                            this.stayTime = 0;
                            this.moveTime = Utils.rand(30, 200);
                            this.target = this.add(Utils.rand(1,100) <= 40 ? (double) x : (double) (-x), 0, Utils.rand(1,100) <= 60 ? (double) z : (double) (-z));
                        }
                    }

                }else{
                    distance = this.distanceSquared(followTarget);
                    if (distance > near || this.targetOption(followTarget, distance)) {
                        setFollowTarget(null,false);
                        return;
                    }
                    if(this.target == null) {
//                    near = distance;
                        this.stayTime = 0;
                        this.moveTime = 0;
                        if (this.passengers.isEmpty()) {
                            this.target = followTarget;
                        }
                    }

                }
            }
        }
    }

    private double getAttackChunk(double near, Entity entity) {
        if(entity instanceof Player ){
            PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo((Player) entity);
            if (info != null) {
                if (info.getTeamInfo().equals(master.getTeamInfo())) {
                    return near;
                }
            }
        }else{
            return near;
        }
        near = getFightEntity(near, (EntityCreature) entity);
        return near;
    }

    private double getFightEntity(double near, EntityCreature entity) {
        EntityCreature creature;
        double distance;
        creature = entity;
        distance = this.distanceSquared(creature);
        if (distance > near || this.targetOption(creature, distance)) {
            return near;
        }
        near = distance;
        this.stayTime = 0;
        this.moveTime = 0;
        this.followTarget = creature;
        if (this.passengers.isEmpty()) {
            this.target = creature;
        }

        canAttack = true;
        return near;
    }


    /**
     * 生物移除
     * */
    abstract public void onClose();

    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        } else if (!this.isAlive()) {
            onClose();
            if (++this.deadTicks >= 23) {
                this.close();
                return false;
            } else {
                return true;
            }
        } else {
            int tickDiff = currentTick - this.lastUpdate;
            this.lastUpdate = currentTick;
            this.entityBaseTick(tickDiff);

            Vector3 target = this.updateMove(tickDiff);
            if(target instanceof EntityCreature){
                if(targetOption((EntityCreature) target,distanceSquared(target))){
                    setFollowTarget(null,false);
                    return true;
                }
                if(target instanceof Player){
                    if(isPlayerTarget((Player) target)) {
                        if (target != this.followTarget || this.canAttack) {
                            if(!targetOption((EntityCreature) target,distanceSquared(target))) {
                                this.attackEntity((Player) target);
                            }else{
                                setFollowTarget(null,false);
                            }
                        }
                    }
                }else{
//                    if (target != this.followTarget || this.canAttack) {
                    if (!targetOption((EntityCreature) target, distanceSquared(target))) {
                        this.attackEntity((EntityCreature) target);
                    }else{
                        setFollowTarget(null,false);
                    }
//                    }
                }
//
            }else if (target != null && Math.pow(this.x - target.x, 2.0D) + Math.pow(this.z - target.z, 2.0D) <= 1.0D) {
                this.moveTime = 0;
            }

            return true;
        }
    }





    public Vector3 updateMove(int tickDiff) {
        if (!this.isImmobile()) {

            if (!this.isMovement()) {
                return null;
            } else {

                if (this.isKnockback()) {
                    this.move(this.motionX , this.motionY, this.motionZ);
                    this.motionY -= (double)(this.getGravity());
                    this.updateMovement();
                    return null;
                } else {

                    if (this.followTarget != null && !this.followTarget.closed && this.followTarget.isAlive() && this.target != null) {
                        double x = (this.target.x - this.x);
                        double z = (this.target.z - this.z);
                        double diff = Math.abs(x) + Math.abs(z);
                        if(diff <= 0){
                            diff = 0.1;
                        }


                        if (this.stayTime <= 0 && this.distance(this.followTarget) > ((double)this.getWidth() + 0.0D) / 2.0D + 0.05D) {
                            if (this.isInsideOfWater()) {
                                this.motionX = this.getSpeed() * 0.05D * (x / diff);
                                this.motionZ = this.getSpeed() * 0.05D * (z / diff);
                                this.level.addParticle(new BubbleParticle(this.add(Utils.rand(-2.0D, 2.0D), Utils.rand(-0.5D, 0.0D), Utils.rand(-2.0D, 2.0D))));
                            } else {
                                this.motionX = this.getSpeed() * 0.1D * (x / diff);
                                this.motionZ = this.getSpeed() * 0.1D * (z / diff);
                            }
                        } else {
                            this.motionX = 0.0D;
                            this.motionZ = 0.0D;
                        }
                        //移除了rand()
                        if ((this.passengers.isEmpty() ) && (this.stayTime <= 0 || Utils.rand())) {
                            this.yaw = Math.toDegrees(-Math.atan2(x / diff, z / diff));
                        }
                    }

                    Vector3 before = this.target;
                    this.checkTarget();
                    double x;
                    double z;
                    if (this.target != null || before != this.target) {
                        if(this.target != null) {
                            x = this.target.x - this.x;
                            z = this.target.z - this.z;
                            double diff = Math.abs(x) + Math.abs(z);
                            if(diff <= 0){
                                diff = 0.1;
                            }
                            if (this.stayTime <= 0 && this.distance(this.target) > ((double) this.getWidth() + 0.0D) / 2.0D + 0.05D) {
                                if (this.isInsideOfWater()) {
                                    this.motionX = this.getSpeed() * 0.05D * (x / diff);
                                    this.motionZ = this.getSpeed() * 0.05D * (z / diff);
                                    this.level.addParticle(new BubbleParticle(this.add(Utils.rand(-2.0D, 2.0D), Utils.rand(-0.5D, 0.0D), Utils.rand(-2.0D, 2.0D))));
                                } else {
                                    this.motionX = this.getSpeed() * 0.15D * (x / diff);
                                    this.motionZ = this.getSpeed() * 0.15D * (z / diff);
                                }
                            } else {
                                this.motionX = 0.0D;
                                this.motionZ = 0.0D;
                            }
                            if ((this.passengers.isEmpty()) && (this.stayTime <= 0 || Utils.rand())) {
                                this.yaw = Math.toDegrees(-Math.atan2(x / diff, z / diff));
                            }
                        }
                    }

                    x = this.motionX * (double)tickDiff;
                    z = this.motionZ * (double)tickDiff;
                    boolean isJump = this.checkJump(x, z);
                    if (this.stayTime > 0) {
                        this.stayTime -= tickDiff;
                        this.move(0.0D, this.motionY, 0.0D);
                    } else {
                        Vector2 be = new Vector2(this.x + x, this.z + z);
                        this.move(x, this.motionY, z);
                        Vector2 af = new Vector2(this.x, this.z);
                        if ((be.x != af.x || be.y != af.y) && !isJump) {
                            this.moveTime -= 90 * tickDiff;
                        }
                    }

                    if (!isJump) {
                        if (this.onGround) {
                            this.motionY = 0.0D;
                        } else if (this.motionY > (double)(-this.getGravity() * 4.0F)) {
                            if (!(this.level.getBlock(new Vector3((double)NukkitMath.floorDouble(this.x), (double)((int)(this.y + 0.8D)), (double)NukkitMath.floorDouble(this.z))) instanceof BlockLiquid)) {
                                this.motionY -= (double)(this.getGravity() * 1.0F);
                            }
                        } else {
                            this.motionY -= (double)(this.getGravity() * (float)tickDiff);
                        }
                    }

                    this.updateMovement();


                    return this.followTarget != null ? this.followTarget : this.target;
                }
            }
        } else {
            return null;
        }


    }


}
