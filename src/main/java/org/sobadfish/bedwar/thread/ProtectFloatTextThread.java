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

                if (floatText.getChunk() != null && floatText.getChunk().isLoaded()) {
                    if (floatText.isClosed()) {
                        BedWarFloatText floatText1 = new BedWarFloatText(floatText.name,floatText.chunk,floatText.namedTag);
                        floatText1.setText(floatText.text);
                        floatText1.toDisplay();
                        FloatTextManager.floatTextList.remove(floatText);
                    }
                }
            }

        }

    }
}
