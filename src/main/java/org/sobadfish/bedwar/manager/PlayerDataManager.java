package org.sobadfish.bedwar.manager;

import org.sobadfish.bedwar.player.PlayerData;
import org.sobadfish.bedwar.top.TopItem;

import java.io.*;
import java.util.List;

public class PlayerDataManager extends BaseDataWriterGetterManager<PlayerData>{


    public PlayerDataManager(List<PlayerData> dataList, File file) {
        super(dataList, file);
    }

    public PlayerData getData(String player){
        PlayerData data = new PlayerData();
        data.name = player;
        if(!dataList.contains(data)){
            dataList.add(data);
        }
        return dataList.get(dataList.indexOf(data));
    }

    public static PlayerDataManager asFile(File file){
        return (PlayerDataManager) BaseDataWriterGetterManager.asFile(file,"player.json", TopItem[].class,PlayerDataManager.class);
    }

}
