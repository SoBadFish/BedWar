package org.sobadfish.bedwar.thread;

import cn.nukkit.Server;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.BedWarFloatText;
import org.sobadfish.bedwar.event.ReloadWorldEvent;
import org.sobadfish.bedwar.manager.FloatTextManager;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.manager.WorldResetManager;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.util.Map;

/**
 * @author Sobadfish
 */
public class PluginMasterRunnable extends ThreadManager.AbstractBedWarRunnable {

    private long loadTime = 0;

    //浮空字一秒更新一次会跳
    private int update = 0;


    @Override
    public GameRoom getRoom() {
        return null;
    }

    @Override
    public String getThreadName() {
        String color = "&a";
        if(isClose){
            color = "&7";
        }
        StringBuilder s = new StringBuilder(color + "Plugin Master  Floating Text &7(" +
                FloatTextManager.floatTextList.size() + ") &a" + loadTime + " ms\n");
        for(BedWarFloatText floatText:FloatTextManager.floatTextList){
            s.append("&r   - ").append(floatText.name).append(" = ").append(floatText.getNameTag()).append(" &7pos=(")
                    .append(floatText.getFloorX()).append(":")
                    .append(floatText.getFloorY()).append(":")
                    .append(floatText.getFloorZ()).append(":")
                    .append(floatText.getLevel().getFolderName()).append(")\n");
        }

        return s.toString();
    }

    @Override
    public void run() {
        long t1 = System.currentTimeMillis();
        update ++;
        try {
            if (isClose) {
                ThreadManager.cancel(this);
            }

            if (BedWarMain.getBedWarMain().isDisabled()) {
                isClose = true;
                return;
            }

            for (BedWarFloatText floatText : FloatTextManager.floatTextList) {
                if (floatText == null) {
                    continue;
                }

                if (floatText.isFinalClose) {
                    FloatTextManager.removeFloatText(floatText);
                    continue;
                }

                if (update > 120) {
                    //每120秒刷新一下...
                    floatText.disPlayers();

                }
                floatText.stringUpdate();
            }
            if(update > 120){
                update = 0;
            }
            try {
                for(Map.Entry<String,String> map: WorldResetManager.RESET_QUEUE.entrySet()){
                    if (WorldInfoConfig.toPathWorld(map.getKey(), map.getValue(),false)) {
                        BedWarMain.sendMessageToConsole("&a" + map.getKey() + " Reset!");
                    }
                    Server.getInstance().getPluginManager().callEvent(new ReloadWorldEvent(BedWarMain.getBedWarMain(), BedWarMain.getRoomManager().getRoomConfig(map.getKey())));

                }

            } catch (Exception e) {
                BedWarMain.sendMessageToConsole("&c释放房间出现了一个小问题，导致无法正常释放,已将这个房间暂时锁定");
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        loadTime = System.currentTimeMillis() - t1;
    }


}
