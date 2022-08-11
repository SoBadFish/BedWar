package org.sobadfish.bedwar.event;

import cn.nukkit.event.Cancellable;
import cn.nukkit.plugin.Plugin;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.room.GameRoom;

/**
 * @author SoBadFish
 * 2022/1/15
 */
public class TeamBedBreakEvent extends GameRoomEvent {

    private final TeamInfo teamInfo;

    private final PlayerInfo breaker;

    public TeamBedBreakEvent(TeamInfo teamInfo,PlayerInfo breaker,GameRoom room, Plugin plugin) {
        super(room, plugin);
        this.teamInfo = teamInfo;
        this.breaker = breaker;
    }

    public TeamInfo getTeamInfo() {
        return teamInfo;
    }

    public PlayerInfo getBreaker() {
        return breaker;
    }
}
