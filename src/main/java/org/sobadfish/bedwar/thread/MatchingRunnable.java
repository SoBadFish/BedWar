package org.sobadfish.bedwar.thread;

import org.sobadfish.bedwar.manager.RandomJoinManager;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

public class MatchingRunnable extends ThreadManager.AbstractBedWarRunnable {

    private final PlayerInfo info;

    private final String name;

    public MatchingRunnable(PlayerInfo info,String name){
        this.info = info;
        this.name = name;
    }

    @Override
    public GameRoom getRoom() {
        return null;
    }

    @Override
    public String getThreadName() {
        return "匹配线程";
    }

    @Override
    public void run() {
        if(RandomJoinManager.newInstance().join(info, name)){
            info.sendForceTitle("&a进入匹配队列");
        }else{
            info.sendForceTitle("&c无法进入匹配队列..");

        }
        isClose = true;

    }
}
