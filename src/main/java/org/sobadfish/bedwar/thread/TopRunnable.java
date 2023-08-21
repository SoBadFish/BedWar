package org.sobadfish.bedwar.thread;

import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.top.TopItemInfo;

public class TopRunnable extends ThreadManager.AbstractBedWarRunnable {

    @Override
    public GameRoom getRoom() {
        return null;
    }

    public long time = 0;

    @Override
    public String getThreadName() {
        String color = "&a";
        if(isClose){
            color = "&7";
        }
        return color+"排行榜更新 &7("+BedWarMain.getTopManager().topItemInfos.size()+") &a"+time+" ms";
    }

    @Override
    public void run() {
        try {
            long t1 = System.currentTimeMillis();
            if (isClose) {
                ThreadManager.cancel(this);
                return;
            }

            if (BedWarMain.getBedWarMain().isDisabled()) {
                isClose = true;
                return;
            }
            for (TopItemInfo topItem : BedWarMain.getTopManager().topItemInfos) {
                if (!BedWarMain.getTopManager().dataList.contains(topItem.topItem)) {
                    topItem.floatText.toClose();
                    BedWarMain.getTopManager().topItemInfos.remove(topItem);
                    continue;
                }
                if (topItem.floatText != null) {
                    if (topItem.floatText.player == null) {
                        continue;
                    }
                    topItem.floatText.setText(topItem.topItem.getListText());
                } else {
                    BedWarMain.getTopManager().topItemInfos.remove(topItem);
                }
            }
            time = System.currentTimeMillis() - t1;
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
