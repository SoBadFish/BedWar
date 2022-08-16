package org.sobadfish.bedwar.thread;

import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.tools.Utils;

import java.util.List;

public class FairworksRunnable extends BaseTimerRunnable{

    private final GameRoom room;

    private final List<PlayerInfo> playerInfos;

    public FairworksRunnable(int end, GameRoom room, List<PlayerInfo> playerInfos) {
        super(end);
        this.room = room;
        this.playerInfos = playerInfos;
    }

    @Override
    public GameRoom getRoom() {
        return room;
    }

    @Override
    public String getThreadName() {
        String color = "&a";
        if(isClose){
            color = "&7";
        }
        return color+"燃放烟花线程";
    }

    @Override
    public void onRun() {
        for(PlayerInfo playerInfo:playerInfos){
            Utils.spawnFirework(playerInfo.getPosition());
        }
        isClose = true;
    }
    @Override
    protected void callback() {}
}
