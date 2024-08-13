package org.sobadfish.bedwar.world;

import cn.nukkit.block.Block;
import lombok.Data;
import org.sobadfish.bedwar.item.ItemInfo;
import org.sobadfish.bedwar.item.config.ItemInfoConfig;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.util.ArrayList;

/**
 * @author SoBadFish
 * 2022/1/2
 */
@Data
public class WorldInfo {

    private GameRoom room;

    private ArrayList<ItemInfo> infos = new ArrayList<>();

    private boolean isClose;

    public boolean isStart;

    private WorldInfoConfig config;

    private ArrayList<Block> placeBlock = new ArrayList<>();

    public WorldInfo(GameRoom room,WorldInfoConfig config){
        this.config = config;
        this.room = room;
        for(ItemInfoConfig config1:config.getItemInfos()){
            infos.add(new ItemInfo(config1));
        }
    }

    public boolean onChangeBlock(Block block,boolean isPlace){
        for(TeamInfo info:room.getTeamInfos()){
            if(block.distance(info.getTeamConfig().getSpawnPosition()) < 3){
                return false;
            }
        }
        if(isPlace){
            placeBlock.add(block);
        }else{
            placeBlock.remove(block);
        }
        return true;
    }


    public void setClose(boolean close) {
        isClose = close;
    }

    public boolean isClose() {
        return isClose;
    }



    /**
     * 定时任务
     * */
    public boolean onUpdate(){
        if(isStart) {
            if (room.close || isClose) {
                isStart = false;
                infos.clear();
                return false;
            }
            for (ItemInfo itemInfo : infos) {
                try {
                    itemInfo.toUpdate(room);
                } catch (Exception e) {
                    return false;
                }

            }
        }
        return true;

    }

}
