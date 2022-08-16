package org.sobadfish.bedwar.manager;

import cn.nukkit.Player;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.WorldRoom;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.thread.JoinListRunnable;
import org.sobadfish.bedwar.tools.Utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 房间匹配队列
 * */
public class RandomJoinManager {



    public static RandomJoinManager joinManager;

    private RandomJoinManager(){}

    public static RandomJoinManager newInstance(){
        if(joinManager == null){
            joinManager = new RandomJoinManager();
        }
        return joinManager;
    }

    public List<IPlayerInfo> playerInfos = new CopyOnWriteArrayList<>();

    //将这个线程缩短为单个
    public void start(){
        ThreadManager.addThread(new JoinListRunnable(playerInfos,this));
    }


    public boolean join(PlayerInfo info,String name){
        if(info.getGameRoom() != null && info.getGameRoom().getType() != GameRoom.GameType.END){
            return false;
        }
        IPlayerInfo iPlayerInfo = new IPlayerInfo();
        iPlayerInfo.playerInfo = info;
        if(playerInfos.contains(iPlayerInfo)){
            info.sendForceMessage("&b重新开始匹配");
            iPlayerInfo = playerInfos.get(playerInfos.indexOf(iPlayerInfo));
        }

        iPlayerInfo.name = name;
        iPlayerInfo.time = new Date();
        playerInfos.add(iPlayerInfo);
        return true;
    }



    public static class IPlayerInfo{

        private PlayerInfo playerInfo;

        public String name;

        public Date time;

        public boolean cancel;

        public PlayerInfo getPlayerInfo() {
            if(playerInfo != null && playerInfo.getPlayer() instanceof Player && !playerInfo.getPlayer().closed){
                return playerInfo;
            }
            cancel = true;
            return null;
        }

        @Override
        public boolean equals(Object o) {
            if(o instanceof IPlayerInfo){
                return ((IPlayerInfo) o).playerInfo.equals(playerInfo);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(playerInfo, name, time, cancel);
        }
    }

}
