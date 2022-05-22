package org.sobadfish.bedwar.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.plugin.PluginEvent;
import cn.nukkit.plugin.Plugin;
import org.sobadfish.bedwar.room.GameRoom;

/**
 * @author SoBadFish
 * 2022/1/15
 */
public class GameRoomEvent extends PluginEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    private GameRoom room;

    public GameRoomEvent(GameRoom room,
                         Plugin plugin) {
        super(plugin);
        this.room = room;
    }

    public GameRoom getRoom() {
        return room;
    }
}
