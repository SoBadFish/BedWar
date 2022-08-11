package org.sobadfish.bedwar.room.event;

import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomEventConfig;

public class BreakEvent extends IGameRoomEvent{


    public BreakEvent(GameRoomEventConfig.GameRoomEventItem item) {
        super(item);
    }


    @Override
    public void onStart(GameRoom room) {
        for(TeamInfo teamInfo: room.getTeamInfos()){
            teamInfo.breakBed();
        }
        room.sendMessage(getEventItem().display);
    }



}
