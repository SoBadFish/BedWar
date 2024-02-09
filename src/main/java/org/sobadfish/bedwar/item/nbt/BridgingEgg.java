package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.item.Item;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.BridgingEggEntity;
import org.sobadfish.bedwar.player.PlayerInfo;

/**
 * @author Sobadfish
 * @date 2023/9/9
 */
public class BridgingEgg implements INbtItem{
    @Override
    public String getName() {
        return BedWarMain.getLanguage().getLanguage("egg-item","搭桥蛋");
    }

    @Override
    public boolean onClick(Item item, Player player) {
        PlayerInfo playerInfo = BedWarMain.getRoomManager().getPlayerInfo(player);
        if(playerInfo.getGameRoom() == null){
            return false;
        }

        Entity master = playerInfo.getPlayer();
        Vector3 directionVector = master.getDirectionVector();
        Vector3 playerPos = master.getPosition();
        CompoundTag nbt = (new CompoundTag()).putList((new ListTag("Pos"))
                        .add(new DoubleTag("", playerPos.x))
                        .add(new DoubleTag("", playerPos.y  + master.getEyeHeight()))
                        .add(new DoubleTag("", playerPos.z)))
                .putList((new ListTag("Motion")).add(new DoubleTag("", directionVector.x))
                        .add(new DoubleTag("", directionVector.y))
                        .add(new DoubleTag("", directionVector.z)))
                .putList((new ListTag("Rotation"))
                        .add(new FloatTag("", (float)master.yaw)).add(new FloatTag("", (float)master.pitch)));

        BridgingEggEntity bridgingEgg = new BridgingEggEntity(master.getLevel().getChunk(master.getFloorX() >> 4, master.getFloorZ() >> 4),nbt);
        bridgingEgg.setMotion(bridgingEgg.getMotion().multiply(1.5f));
        bridgingEgg.shootingEntity = master;
        bridgingEgg.gameRoom = playerInfo.getGameRoom();
        bridgingEgg.playerInfo = playerInfo;
        bridgingEgg.spawnToAll();

        player.getInventory().removeItem(item);
        return true;
    }
}
