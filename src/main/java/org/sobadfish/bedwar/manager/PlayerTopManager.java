package org.sobadfish.bedwar.manager;

import org.sobadfish.bedwar.entity.BedWarFloatText;
import org.sobadfish.bedwar.top.TopItem;
import org.sobadfish.bedwar.top.TopItemInfo;

import java.io.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PlayerTopManager extends BaseDataWriterGetterManager<TopItem>{


    public List<TopItemInfo> topItemInfos = new CopyOnWriteArrayList<>();


    public File file;

    public PlayerTopManager(List<TopItem> topItems,File file){
        super(topItems,file);
    }



    public void init(){
        for(TopItem topItem: dataList){
            BedWarFloatText floatText = BedWarFloatText.showFloatText(topItem.name,topItem.getPosition(),"");
            topItemInfos.add(new TopItemInfo(topItem,floatText));
        }

    }

    public boolean hasTop(String top){
        for(TopItem topItem: dataList){
            if(topItem.name.equals(top)){
                return true;
            }
        }
        return false;
    }

    public void removeTopItem(TopItem topItem){
        dataList.remove(topItem);
    }

    public void addTopItem(TopItem topItem){
        BedWarFloatText floatText = BedWarFloatText.showFloatText(topItem.name,topItem.getPosition(),"");
        topItemInfos.add(new TopItemInfo(topItem,floatText));
        dataList.add(topItem);
    }

    public static PlayerTopManager asFile(File file){
        return (PlayerTopManager) BaseDataWriterGetterManager.asFile(file,"top.json",TopItem[].class,PlayerTopManager.class);
    }



}
