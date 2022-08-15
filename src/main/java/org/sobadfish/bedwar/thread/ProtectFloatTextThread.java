package org.sobadfish.bedwar.thread;

import cn.nukkit.Player;
import cn.nukkit.Server;
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
            for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                for (BedWarFloatText floatText : new ArrayList<>(FloatTextManager.floatTextList)) {
                    if (floatText == null) {
                        continue;
                    }
                    if (floatText.isFinalClose) {
                        FloatTextManager.removeFloatText(floatText);
                        continue;
                    }
                    if(floatText.player.contains(player)){
                        if(player.getLevel() != floatText.getPosition().getLevel()) {
                            if (!floatText.closed) {
                                floatText.close();
                            }
                            floatText.player.remove(player);
                        }
                    }
                    if(player.getLevel() == floatText.getPosition().getLevel()){
                        floatText.player.add(player);
                    }
                    floatText.player.removeIf(p->!p.isOnline());

                    floatText.disPlayers();
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
}
