package org.sobadfish.bedwar.template;

import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.world.BlockVector3;

import java.util.List;

/**
 * 方块建筑物模板
 * @author Sobadfish
 * @date 2024/8/26
 */
public interface IBlockTemplate {

    /**
     * 获取方块位置
     * @param info 放置朝向
     * @return 仅计算0,0,0
     * */
    List<BlockVector3> getSpawnBlocks(PlayerInfo info);


    /**
     * 是否自动适配羊毛
     * */
    boolean autoWool();

}
