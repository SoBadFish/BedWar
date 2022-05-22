package org.sobadfish.bedwar.event;


import cn.nukkit.plugin.Plugin;
import org.sobadfish.bedwar.room.GameRoom;

/**
 * @author SoBadFish
 * 2022/1/15
 */
public class GameRoomStartEvent extends GameRoomEvent {

    public GameRoomStartEvent(GameRoom room, Plugin plugin) {
        super(room, plugin);
    }
}
