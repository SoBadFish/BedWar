package org.sobadfish.bedwar.thread;

import cn.nukkit.Player;
import cn.nukkit.Server;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.BedWarFloatText;
import org.sobadfish.bedwar.manager.*;
import org.sobadfish.bedwar.manager.WorldResetManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.WorldRoom;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.tools.Utils;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class PluginMasterRunnable extends ThreadManager.AbstractBedWarRunnable {

    public long loadTime = 0;

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
        return color+"插件主进程  浮空字数量&7("+ FloatTextManager.floatTextList.size() +") &a"+loadTime+" ms";
    }

    @Override
    public void run() {
        if(isClose){
            ThreadManager.cancel(this);
        }
        long t1 = System.currentTimeMillis();
        if(BedWarMain.getBedWarMain().isDisabled()){
            isClose = true;
            return;
        }
        for (Player player : new ArrayList<>(Server.getInstance().getOnlinePlayers().values())) {
            for (BedWarFloatText floatText : new ArrayList<>(FloatTextManager.floatTextList)) {
                if (floatText == null) {
                    continue;
                }
                if (floatText.isFinalClose) {
                    FloatTextManager.removeFloatText(floatText);
                    continue;
                }
                if(floatText.player.contains(player)){
                    if(!player.getLevel().getFolderName().equalsIgnoreCase(floatText.getPosition().getLevel().getFolderName()) || !player.isOnline()) {
                        if (!floatText.closed) {
                            floatText.close();
                        }
                        floatText.player.remove(player);
                    }
                }
                if(player.getLevel() == floatText.getPosition().getLevel()){
                    floatText.player.add(player);
                }

                floatText.disPlayers();

            }

        }
        worldReset();

        loadTime = System.currentTimeMillis() - t1;
    }

    public void worldReset() {
        List<GameRoomConfig> bufferQueue = new ArrayList<>();
        try {
            for(Map.Entry<GameRoomConfig,String> map: WorldResetManager.RESET_QUEUE.entrySet()){
                if (WorldInfoConfig.toPathWorld(map.getKey().getName(), map.getValue())) {
                    BedWarMain.sendMessageToConsole("&a" + map.getKey().getName() + " 地图已还原");
                }
                Server.getInstance().loadLevel(map.getValue());
                BedWarMain.sendMessageToConsole("&r释放房间 " + map.getKey().getName());
                BedWarMain.sendMessageToConsole("&r房间 " + map.getKey().getName() + " 已回收");
                bufferQueue.add(map.getKey());
            }
            //TODO 从列表中移除
            for(GameRoomConfig config: bufferQueue){
                BedWarMain.getRoomManager().getRooms().remove(config.getName());
                RoomManager.LOCK_GAME.remove(config);
                WorldResetManager.RESET_QUEUE.remove(config);
            }
        } catch (Exception e) {
            BedWarMain.sendMessageToConsole("&c释放房间出现了一个小问题，导致无法正常释放,已将这个房间暂时锁定");
        }

    }
}
