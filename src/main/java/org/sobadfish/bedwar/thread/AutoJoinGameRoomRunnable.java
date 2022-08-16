package org.sobadfish.bedwar.thread;

import org.sobadfish.bedwar.manager.RandomJoinManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

public class AutoJoinGameRoomRunnable extends BaseTimerRunnable{

    private final GameRoom room;

    private final PlayerInfo playerInfo;

    private final String name;

    public AutoJoinGameRoomRunnable(int end,PlayerInfo playerInfo,GameRoom room,String name) {
        super(end);
        this.room = room;
        this.playerInfo = playerInfo;
        this.name = name;
    }

    @Override
    public GameRoom getRoom() {
        return room;
    }

    @Override
    public String getThreadName() {
        return "自动进入游戏线程";
    }

    @Override
    protected void callback() {
        if(RandomJoinManager.newInstance().join(new PlayerInfo(playerInfo.getPlayer()),name)){
            room.quitPlayerInfo(playerInfo,false);
        }

    }
}
