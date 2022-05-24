package org.sobadfish.bedwar.event;

import cn.nukkit.plugin.Plugin;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.room.GameRoom;

/**
 * @author SoBadFish
 * 2022/5/24
 */
public class TeamDefeatEvent extends GameRoomEvent {

    private TeamInfo teamInfo;

    public TeamDefeatEvent(TeamInfo teamInfo, GameRoom room, Plugin plugin) {
        super(room, plugin);
        this.teamInfo = teamInfo;
    }

    public TeamInfo getTeamInfo() {
        return teamInfo;
    }
}