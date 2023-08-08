package org.sobadfish.bedwar.manager;

import cn.nukkit.Player;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 房间匹配队列
 * @author Sobadfish
 * */
public class RandomJoinManager {



    public static RandomJoinManager joinManager;



    public static RandomJoinManager newInstance(){
        if(joinManager == null){
            joinManager = new RandomJoinManager();
        }
        return joinManager;
    }

    public List<IPlayerInfo> playerInfos = new CopyOnWriteArrayList<>();


    /**
     * 当玩家在房间内时，调用这个方法匹配房间
     * @param info 玩家
     * */
    public void nextJoin(PlayerInfo info){
        //TODO 匹配下一局 程序分配
        GameRoom gameRoom = info.getGameRoom();
        if(gameRoom != null){
            gameRoom.quitPlayerInfo(info,false);
        }

        join(info,null,true);
    }

    /**
     * 调用这个匹配房间
     * 这个是进入匹配队列
     * @param info 准备加入游戏房间的玩家
     * @param name 游戏模式的名称
     * */
    public void join(PlayerInfo info, String name){
        join(info, name,false);
    }

    /**
     * 调用这个匹配房间
     * 这个是进入匹配队列
     * @param info 准备加入游戏房间的玩家
     * @param name 游戏模式的名称
     * @param isNext 当进入房间失败后，是否传送回主大厅地图
     * */
    public void join(PlayerInfo info, String name,boolean isNext){
        if(info.getGameRoom() != null && info.getGameRoom().getType() != GameRoom.GameType.END){
            return;
        }
        info = new PlayerInfo(info.getPlayer());
        IPlayerInfo iPlayerInfo = new IPlayerInfo();
        iPlayerInfo.playerInfo = info;
        iPlayerInfo.isNext = isNext;
        if(playerInfos.contains(iPlayerInfo)){
            info.sendForceMessage("&c取消匹配");
            playerInfos.remove(iPlayerInfo);
            return;
        }

        iPlayerInfo.name = name;
        iPlayerInfo.time = System.currentTimeMillis();
        playerInfos.add(iPlayerInfo);


    }



    public static class IPlayerInfo{

        private PlayerInfo playerInfo;

        public String name;

        public Long time;

        public boolean cancel;

        public boolean isNext;

        public PlayerInfo getPlayerInfo() {
            if(playerInfo != null && playerInfo.getPlayer() instanceof Player && !playerInfo.getPlayer().closed){
                return playerInfo;
            }
            cancel = true;
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if(o == null){
                return false;
            }
            if(o instanceof IPlayerInfo){
                return ((IPlayerInfo) o).playerInfo.getName().equalsIgnoreCase(playerInfo.getName());
            }
            return false;
        }

    }

}
