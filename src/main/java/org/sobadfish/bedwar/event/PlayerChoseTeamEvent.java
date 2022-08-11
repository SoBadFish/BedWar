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
public class PlayerChoseTeamEvent extends PlayerRoomInfoEvent implements Cancellable {


    private final TeamInfo teamInfo;

    public PlayerChoseTeamEvent(PlayerInfo playerInfo,TeamInfo teamInfo,GameRoom room, Plugin plugin) {
        super(playerInfo,room, plugin);
        this.teamInfo = teamInfo;
    }



    public TeamInfo getTeamInfo() {
        return teamInfo;
    }
}
