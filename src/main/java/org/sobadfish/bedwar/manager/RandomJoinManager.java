package org.sobadfish.bedwar.manager;

import cn.nukkit.Player;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 房间匹配队列
 * */
public class RandomJoinManager {

    private Timer timer;


    public static RandomJoinManager joinManager;

    private RandomJoinManager(){
        timer = new Timer();
    }

    public static RandomJoinManager newInstance(){
        if(joinManager == null){
            joinManager = new RandomJoinManager();
        }
        return joinManager;
    }

    public List<IPlayerInfo> playerInfos = new CopyOnWriteArrayList<>();


    public void join(PlayerInfo info, String name){
        if(info.getGameRoom() != null && info.getGameRoom().getType() != GameRoom.GameType.END){
            return;
        }
        IPlayerInfo iPlayerInfo = new IPlayerInfo();
        iPlayerInfo.playerInfo = info;
        if(playerInfos.contains(iPlayerInfo)){
            info.sendForceMessage("&c取消匹配");
            playerInfos.remove(iPlayerInfo);
            return;
        }

        iPlayerInfo.name = name;
        iPlayerInfo.time = new Date();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                playerInfos.add(iPlayerInfo);
            }
        },1000);

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
