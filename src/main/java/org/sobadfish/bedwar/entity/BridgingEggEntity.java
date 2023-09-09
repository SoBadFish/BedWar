package org.sobadfish.bedwar.entity;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.projectile.EntityEgg;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

/**
 * 搭桥蛋
 * @author Sobadfish
 * @date 2023/9/9
 */
public class BridgingEggEntity extends EntityEgg {


    public BridgingEggEntity(FullChunk chunk, CompoundTag nbt) {
        super(chunk, nbt);
    }

    public GameRoom gameRoom;

    public PlayerInfo playerInfo;


    @Override
    public boolean onUpdate(int currentTick) {
        if (this.closed) {
            return false;
        } else {
            if (this.age > 1200) {
                this.close();
            }
            if (isCollided){
                this.close();
            }
            placeBlock();

            return super.onUpdate(currentTick);
        }
    }

    public void placeBlock() {
        if(gameRoom.getType() == GameRoom.GameType.START){
            Server.getInstance().getScheduler().scheduleTask(() -> {
                for (int xOffset = -1; xOffset < 1; xOffset++) {
                    for (int zOffset = -1; zOffset < 1; zOffset++) {
                        Block block = playerInfo.getTeamInfo().getTeamConfig().getTeamConfig().getBlockWoolColor().clone().getBlock();
                        Vector3 vector3 = new Vector3(x + xOffset, y - 1.5f, z + zOffset);
                        if (getLevel().getBlock(vector3).getId() == 0) {

                            if(gameRoom.worldInfo.onChangeBlock(block,true)){
                                getLevel().setBlock(vector3, block);
                            }
                        }

                    }
                }

            }, true);
        }

    }






}
