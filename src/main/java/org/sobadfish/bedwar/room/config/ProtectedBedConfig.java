package org.sobadfish.bedwar.room.config;

import cn.nukkit.block.Block;
import org.sobadfish.bedwar.BedWarMain;

import java.util.*;

/**
 * 床保护配置
 * @author Sobadfish
 * @date 2024/8/19
 */
public class ProtectedBedConfig {


    public boolean enable;

    public List<String> blocks;

    public List<Block> loadingBlocks;



    public ProtectedBedConfig() {
        this.enable = false;
        this.blocks = Arrays.asList("5","35","20");
        loadConfig();

    }

    public Map<String, Object> saveConfig(){
        Map<String, Object> config = new LinkedHashMap<>();
        config.put("enable",enable);
        config.put("blockList",blocks);
        return config;
    }

    /**
     * 加载方块字符串为方块
    * */
    public void loadConfig(){
        loadingBlocks = new ArrayList<>();
        for (String str: blocks){
            String[] eq = str.split(":");
            try {
                int id = Integer.parseInt(eq[0]);
                int meta = 0;
                if(eq.length > 1){
                    meta = Integer.parseInt(eq[1]);
                }
                Block block = Block.get(id,meta);
                loadingBlocks.add(block);
            }catch (Exception e){
                BedWarMain.printMessageException(e);
            }

        }
    }
}
