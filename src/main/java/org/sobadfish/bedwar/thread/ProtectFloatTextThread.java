package org.sobadfish.bedwar.thread;

import org.sobadfish.bedwar.entity.BedWarFloatText;
import org.sobadfish.bedwar.manager.FloatTextManager;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.room.GameRoom;

import java.util.ArrayList;

public class ProtectFloatTextThread extends ThreadManager.AbstractBedWarRunnable {
    @Override
    public GameRoom getRoom() {
        return null;
    }

    @Override
    public String getThreadName() {
        return "浮空字守护线程  &7("+ FloatTextManager.floatTextList.size() +")";
    }

    @Override
    public void run() {
        while (!isClose) {
            for (BedWarFloatText floatText : new ArrayList<>(FloatTextManager.floatTextList)) {
                if(floatText == null){
                    continue;
                }
                if(floatText.isFinalClose){
                    FloatTextManager.removeFloatText(floatText);
                    continue;
                }
                floatText.toDisplay();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    isClose = true;
                }
            }

        }

    }
}
