package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.math.BlockFace;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.tools.Utils;

import java.util.LinkedHashMap;

/**
 * @author Sobadfish
 * @date 2023/11/10
 */
public class Wall implements INbtItem {
    @Override
    public String getName() {
        return "wall-item";
    }

    @Override
    public boolean onClick(Item item, Player player) {


        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
        info.sendMessage(BedWarMain.getLanguage().getLanguage("wall-use-success","&7放置守卫墙!"));

        BlockFace bf = info.getPlayer().getHorizontalFacing();
        Position pos = info.getPlayer().getLocation().add(0,1).getSide(bf,2);
        LinkedHashMap<Position,Block> blocks = new LinkedHashMap<>();
        BlockFace leftBf = bf.rotateY();
        BlockFace rightBf = bf.rotateYCCW();


        Position leftBlockEnd = pos.getSide(leftBf,2);
        Position rightBlockEnd = pos.getSide(rightBf,2);

        int minX = (int) Math.min(Math.round(leftBlockEnd.getX()), Math.round(rightBlockEnd.getX()));
        int maxX = (int) Math.max(Math.round(leftBlockEnd.getX()),Math.round(rightBlockEnd.getX()));

        int minZ = (int) Math.min(Math.round(leftBlockEnd.getZ()), Math.round(rightBlockEnd.getZ()));
        int maxZ = (int) Math.max(Math.round(leftBlockEnd.getZ()),Math.round(rightBlockEnd.getZ()));

        for(int x = minX; x <= maxX;x++){
            for(int z = minZ; z <= maxZ;z++){
                for(int y = 0;y < 3;y++){
                    blocks.put(new Position(x,pos.getFloorY()+ y,z,player.level),Block.get(24));
                }
            }
        }

        Utils.spawnBlock(player,blocks,false);
        player.getInventory().removeItem(item);

        return true;
    }

}
