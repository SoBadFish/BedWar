package org.sobadfish.bedwar.template;

import cn.nukkit.block.BlockLadder;
import cn.nukkit.block.BlockWool;
import cn.nukkit.math.BlockFace;
import cn.nukkit.math.Vector3;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.world.BlockVector3;

import java.util.ArrayList;
import java.util.List;

/**
 * 城堡的方块模板
 * @author Sobadfish
 * @date 2024/8/26
 */
public class DefenseTowerBlockTemplate implements IBlockTemplate{



    @Override
    public List<BlockVector3> getSpawnBlocks(PlayerInfo info) {
        BlockFace pf = info.getPlayer().getHorizontalFacing();
        List<BlockVector3> blockVector3s = new ArrayList<>();
        Vector3 v3 = new Vector3();
        int height = 5;
        int y = 0;
        BlockFace[] face = new BlockFace[]{BlockFace.SOUTH,BlockFace.NORTH,BlockFace.EAST,BlockFace.WEST};
        for(;y < height;y++){
            for(BlockFace f: face){


                Vector3 ff = v3.add(0,y).getSide(f,2);
                Vector3 fleft = ff.getSide(f.rotateYCCW());
                Vector3 fright = ff.getSide(f.rotateY());
                blockVector3s.add(new BlockVector3(new BlockWool(),fleft));
                blockVector3s.add(new BlockVector3(new BlockWool(),fright));
                if(f == pf.rotateY().rotateY()){
                    if(y == 1 || y == 2){
                        //留门
                        continue;
                    }
                }
                blockVector3s.add(new BlockVector3(new BlockWool(),ff));
                if(f == pf){
                    Vector3 tz =  v3.add(0,y).getSide(f);
                    //梯子
                    blockVector3s.add(new BlockVector3(new BlockLadder(f.rotateY().rotateY().getIndex()),tz));
                }

            }
        }


        //生成平台
        for(BlockFace f: face) {
            Vector3 fn = v3.add(0, y-1).getSide(f, 2);
            Vector3 fleft = fn.getSide(f.rotateYCCW(),2);
            Vector3 fright = fn.getSide(f.rotateY(),2);
            blockVector3s.add(new BlockVector3(new BlockWool(),fleft));
            blockVector3s.add(new BlockVector3(new BlockWool(),fright));
        }
        //生成塔顶
        for(BlockFace f: face) {

            Vector3 fn = v3.add(0, y).getSide(f, 3);
            BlockFace left = f.rotateYCCW();
            BlockFace right = f.rotateY();
            blockVector3s.add(new BlockVector3(new BlockWool(),fn));
            blockVector3s.add(new BlockVector3(new BlockWool(),fn.add(0,1)));
            blockVector3s.add(new BlockVector3(new BlockWool(),fn.add(0,-1)));
            for(int i = 1;i <= 2;i++){
                Vector3 nl = fn.getSide(left,i);
                Vector3 nr = fn.getSide(right,i);
                blockVector3s.add(new BlockVector3(new BlockWool(),nl));
                blockVector3s.add(new BlockVector3(new BlockWool(),nr));
                if(i == 2){
                    blockVector3s.add(new BlockVector3(new BlockWool(),nl.add(0,1)));
                    blockVector3s.add(new BlockVector3(new BlockWool(),nr.add(0,-1)));
                    blockVector3s.add(new BlockVector3(new BlockWool(),nr.add(0,1)));
                    blockVector3s.add(new BlockVector3(new BlockWool(),nl.add(0,-1)));
                }
            }
        }


        return blockVector3s;
    }

    @Override
    public boolean autoWool() {
        return true;
    }
}
