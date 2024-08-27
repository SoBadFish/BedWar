package org.sobadfish.bedwar.template;

import cn.nukkit.block.BlockWool;
import cn.nukkit.level.Position;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.world.BlockPosition;
import org.sobadfish.bedwar.world.BlockVector3;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sobadfish
 * @date 2024/8/26
 */
public class BlockTemplateControl {

    private static final Map<String,Class<? extends IBlockTemplate>> TEMPLATE = new LinkedHashMap<>();

    /**
     * 注册模板
     * @param name 模板名称
     * @param block 注册的模板
     * */
    public static void registerBlockTemplate(String name, Class<? extends IBlockTemplate> block){
        if(!TEMPLATE.containsKey(name)){
            TEMPLATE.put(name, block);
        }
    }


    /**
     * 根据生成位置加载建筑物模板
     * @param name 模板名称
     * @param position 生成位置
     * @param playerInfo 放置的玩家
     * */
    public static List<BlockPosition> loadBlockTemplateByName(String name, Position position, PlayerInfo playerInfo){
        List<BlockPosition> data = new ArrayList<>();
        if(TEMPLATE.containsKey(name)){
            Class<? extends IBlockTemplate> pl = TEMPLATE.get(name);
            try {
                IBlockTemplate blockTemplate = pl.newInstance();
                List<BlockVector3> positions = blockTemplate.getSpawnBlocks(playerInfo);
                for(BlockVector3 pos : positions){
                    if(blockTemplate.autoWool()){
                        if(pos.block instanceof BlockWool){
                            if(playerInfo.getTeamInfo() != null){
                                pos.block = playerInfo.getTeamInfo().getTeamConfig()
                                        .getTeamConfig().getBlockWoolColor().getBlock();
                            }
                        }
                    }
                    data.add(pos.asPosition(position));
                }
            } catch (InstantiationException | IllegalAccessException e) {
                BedWarMain.printMessageException(e);
                return data;
            }
        }
        return data;

    }


}
