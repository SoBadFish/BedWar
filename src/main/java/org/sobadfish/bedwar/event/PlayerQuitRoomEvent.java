package org.sobadfish.bedwar.event;

import cn.nukkit.plugin.Plugin;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

/**
 * @author SoBadFish
 * 2022/1/15
 */
public class PlayerQuitRoomEvent extends PlayerRoomInfoEvent {

    public boolean performCommand = true;

    public PlayerQuitRoomEvent(PlayerInfo playerInfo, GameRoom room, Plugin plugin) {
        super(playerInfo, room, plugin);
    }

    public void setPerformCommand(boolean performCommand) {
        this.performCommand = performCommand;
    }

    public boolean isPerformCommand() {
        return performCommand;
    }
}
