package org.sobadfish.bedwar.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.scheduler.Task;
import com.google.gson.JsonObject;
import net.easecation.ghosty.LevelRecordPack;
import net.easecation.ghosty.entity.PlaybackNPC;
import net.easecation.ghosty.playback.LevelPlaybackEngine;
import net.easecation.ghosty.recording.LevelRecordEngine;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.tools.Utils;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 游戏录制/回放管理器
 * 懒得在这里加多语言了
 *
 * @author LT_Name
 */
public class RecordManager {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒");


    private HashMap<GameRoom, LevelRecordEngine> recordEngineHashMap = new HashMap<>();
    private HashMap<Player, LevelPlaybackEngine> playbackEngineHashMap = new HashMap<>();

    private final Task recordTask;

    public RecordManager() {
        this.recordTask = new Task() {
            @Override
            public void onRun(int i) {
                Iterator<Map.Entry<Player, LevelPlaybackEngine>> iterator = playbackEngineHashMap.entrySet().iterator();
                while (iterator.hasNext()) {
                    Map.Entry<Player, LevelPlaybackEngine> entry = iterator.next();
                    LevelPlaybackEngine playbackEngine = entry.getValue();
                    Player player = entry.getKey();
                    if (!player.isOnline()) {
                        playbackEngine.stopPlayback();
                    }
                    if (playbackEngine.isStopped()) {
                        Level level = playbackEngine.getLevel();
                        String levelName = level.getName();
                        Server.getInstance().unloadLevel(level, true);
                        Utils.toDelete(new File(Server.getInstance().getFilePath() + File.separator + "worlds" + File.separator + levelName));
                        BedWarMain.sendMessageToObject(levelName + " 录像播放完成，已删除回放地图", player);
                        iterator.remove();
                    }
                }
            }
        };
        Server.getInstance().getScheduler().scheduleRepeatingTask(BedWarMain.getBedWarMain(), this.recordTask, 20);
    }

    private String getRecordPath() {
        return BedWarMain.getBedWarMain().getDataFolder() + "/record/";
    }

    public void startRecord(GameRoom room) {
        if (!BedWarMain.enableRecord) {
            return;
        }
        if (recordEngineHashMap.containsKey(room)) {
            return;
        }
        LevelRecordEngine levelRecordEngine = new LevelRecordEngine(room.getWorldInfo().getConfig().getGameWorld());
        this.recordEngineHashMap.put(room, levelRecordEngine);
    }

    public void stopRecord(GameRoom room) {
        if (!BedWarMain.enableRecord) {
            return;
        }
        if (!recordEngineHashMap.containsKey(room)) {
            return;
        }
        try {
            LevelRecordEngine levelRecordEngine = recordEngineHashMap.remove(room);
            levelRecordEngine.stopRecord();
            LevelRecordPack recordPack = levelRecordEngine.toRecordPack();
            JsonObject metadata = recordPack.getMetadata();
            metadata.addProperty("roomName", room.getRoomConfig().getName());
            metadata.addProperty("roomWorld", room.getWorldInfo().getConfig().getLevel());
            String time = sdf.format(new Date());
            metadata.addProperty("time", time);
            //TODO 添加其他信息
            File file = new File(this.getRecordPath() + room.getRoomConfig().getName() + "-" + time + ".bwRecord");
            recordPack.packFile(file);
        } catch (Throwable e) {
            BedWarMain.getBedWarMain().getLogger().error("游戏录制保存失败", e);
        }
    }

    /**
     * 播放录像
     * @param file
     * @param player
     */
    public OK playRecord(File file, Player player) {
        try {
            LevelRecordPack recordPack = LevelRecordPack.unpackFile(file);
            String roomName = recordPack.getMetadata().getAsJsonPrimitive("roomName").getAsString();
            String roomWorld = recordPack.getMetadata().getAsJsonPrimitive("roomWorld").getAsString();
            String recordWorld = roomWorld + "Record#" + player.getName();
            GameRoomConfig roomConfig = BedWarMain.getRoomManager().getRoomConfig(roomName);
            if (roomConfig == null) {
                return new OK(false, "录像房间配置不存在");
            }
            WorldInfoConfig.toPathWorld(roomName, roomWorld, recordWorld, false);
            Server.getInstance().loadLevel(recordWorld);
            Level level = Server.getInstance().getLevelByName(recordWorld);
            if (level == null) {
                return new OK(false, "录像地图加载失败");
            }

            LevelPlaybackEngine playback = recordPack.createPlayback(level);
            playbackEngineHashMap.put(player, playback);

            player.teleport(Position.fromObject(roomConfig.worldInfo.getWaitPosition(), level));

            Server.getInstance().getScheduler().scheduleDelayedTask(BedWarMain.getBedWarMain(), () -> {
                //将玩家随机传送到一个回放实体附近
                List<Entity> playbackEntitys = new ArrayList<>();
                for (Entity entity : level.getEntities()) {
                    if (entity instanceof PlaybackNPC) {
                        playbackEntitys.add(entity);
                    }
                }
                player.teleport(playbackEntitys.get(ThreadLocalRandom.current().nextInt(playbackEntitys.size())).getPosition());
            }, 40); //延迟下等实体生成完成再传送

            //TODO 播放完后的处理

            return new OK(true, "开始播放录像");
        } catch (Throwable e) {
            BedWarMain.getBedWarMain().getLogger().error("游戏录像读取失败", e);
            return new OK(false, "游戏录像读取失败，请检查后台报错信息");
        }
    }


    public List<File> getRecordFileList() {
        File[] files = new File(this.getRecordPath()).listFiles();
        if (files != null) {
            ArrayList<File> list = new ArrayList<>();
            for (File file : files) {
                if (!file.isDirectory() && file.getName().endsWith(".bwRecord")) {
                    list.add(file);
                }
            }
            return list;
        }
        return null;
    }

    public LevelRecordPack getRecordPack(File file) {
        try {
            return LevelRecordPack.unpackFile(file);
        } catch (Throwable e) {
            throw new RuntimeException("游戏录制读取失败", e);
        }
    }


    public static class OK {
        private boolean isOK;
        private String message;

        public OK(boolean isOK, String message) {
            this.isOK = isOK;
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public void setOK(boolean OK) {
            isOK = OK;
        }

        public boolean isOK() {
            return isOK;
        }
    }

}
