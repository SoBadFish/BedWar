package org.sobadfish.bedwar.manager;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerData;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PlayerDataManager {

    private File file;

    public List<PlayerData> dataList;

    private PlayerDataManager(List<PlayerData> playerData,File file){
        this.file = file;
        this.dataList = playerData;
    }

    public PlayerData getData(String player){
        PlayerData data = new PlayerData();
        data.name = player;
        if(!dataList.contains(data)){
            dataList.add(data);
        }
        return dataList.get(dataList.indexOf(data));
    }


    public void save(){
        Gson gson = new Gson();
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                BedWarMain.sendMessageToConsole("未知错误 无法保存玩家数据");
            }
        }
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            String json = gson.toJson(dataList);
            writer.write(json,0,json.length());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static PlayerDataManager asFile(File file) {

        Gson gson = new Gson();
        InputStreamReader reader = null;
        try {
            if(!file.exists()){
                BedWarMain.getBedWarMain().saveResource("player.json",false);
            }
            reader = new InputStreamReader(new FileInputStream(file));
            PlayerData[] data = gson.fromJson(reader,PlayerData[].class);
            return new PlayerDataManager(new ArrayList<>(Arrays.asList(data)),file);
        } catch (IOException e) {
            BedWarMain.sendMessageToConsole("&c无法读取 "+file.getName()+" 配置文件");
            e.printStackTrace();
        }finally {
            if(reader !=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    BedWarMain.sendMessageToConsole("&c"+Throwables.getStackTraceAsString(e));
                }
            }
        }
        return null;
    }
}
