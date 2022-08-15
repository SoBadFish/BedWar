package org.sobadfish.bedwar.thread;

import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.top.TopItemInfo;

public class TopUpdateRunnable extends ThreadManager.AbstractBedWarRunnable {
    @Override
    public GameRoom getRoom() {
        return null;
    }

    @Override
    public String getThreadName() {
        return "排行榜更新线程";
    }

    @Override
    public void run() {
        while (!isClose){
            for(TopItemInfo topItem: BedWarMain.getTopManager().topItemInfos){
                if(!BedWarMain.getTopManager().topItems.contains(topItem.topItem)){
                    topItem.floatText.toClose();
                    BedWarMain.getTopManager().topItemInfos.remove(topItem);
                    continue;
                }
                if(topItem.floatText != null ){
                    topItem.floatText.setText(topItem.topItem.getListText(topItem.floatText.player.getName()));
                }else{
                    BedWarMain.getTopManager().topItemInfos.remove(topItem);
                }

            }
        }

    }
}
