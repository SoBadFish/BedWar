package org.sobadfish.bedwar.event;

import cn.nukkit.plugin.Plugin;
import org.sobadfish.bedwar.room.GameRoom;

/**
 * @author SoBadFish
 * 2022/1/15
 */
public class GameCloseEvent extends GameRoomEvent{

    public GameCloseEvent(GameRoom room, Plugin plugin) {
        super(room, plugin);
    }
}
