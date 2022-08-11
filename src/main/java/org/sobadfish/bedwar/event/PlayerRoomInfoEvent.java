package org.sobadfish.bedwar.event;

import cn.nukkit.plugin.Plugin;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

/**
 * @author SoBadFish
 * 2022/1/15
 */
public class PlayerRoomInfoEvent extends GameRoomEvent{

    private final PlayerInfo playerInfo;

    public PlayerRoomInfoEvent(PlayerInfo playerInfo, GameRoom room, Plugin plugin) {
        super(room, plugin);
        this.playerInfo = playerInfo;
    }

    public PlayerInfo getPlayerInfo() {
        return playerInfo;
    }
}
