package org.sobadfish.bedwar.manager;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.BedWarFloatText;
import org.sobadfish.bedwar.top.TopItem;
import org.sobadfish.bedwar.top.TopItemInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerTopManager {

    public List<TopItem> topItems;

    public List<TopItemInfo> topItemInfos = new CopyOnWriteArrayList<>();


    public File file;

    public PlayerTopManager(List<TopItem> topItems,File file){
        this.topItems = topItems;
        this.file = file;
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
            String json = gson.toJson(topItems);
            writer.write(json,0,json.length());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void init(){
        for(TopItem topItem: topItems){
            BedWarFloatText floatText = BedWarFloatText.showFloatText(topItem.name,topItem.getPosition(),"");
            topItemInfos.add(new TopItemInfo(topItem,floatText));
        }

    }

    public boolean hasTop(String top){
        for(TopItem topItem: topItems){
            if(topItem.name.equals(top)){
                return true;
            }
        }
        return false;
    }

    public void removeTopItem(TopItem topItem){
        topItems.remove(topItem);
    }

    public void addTopItem(TopItem topItem){
        BedWarFloatText floatText = BedWarFloatText.showFloatText(topItem.name,topItem.getPosition(),"");
        topItemInfos.add(new TopItemInfo(topItem,floatText));
        topItems.add(topItem);
    }

    public static PlayerTopManager asFile(File file){
        Gson gson = new Gson();
        InputStreamReader reader = null;
        try {
            if(!file.exists()){
                BedWarMain.getBedWarMain().saveResource("top.json",false);
            }
            reader = new InputStreamReader(new FileInputStream(file));
            TopItem[] data = gson.fromJson(reader,TopItem[].class);
            return new PlayerTopManager(new ArrayList<>(Arrays.asList(data)),file);
        } catch (IOException e) {
            BedWarMain.sendMessageToConsole("&c无法读取 "+file.getName()+" 配置文件");
            e.printStackTrace();
        }finally {
            if(reader !=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    BedWarMain.sendMessageToConsole("&c"+ Throwables.getStackTraceAsString(e));
                }
            }
        }
        return null;

    }



}
