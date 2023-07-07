package org.sobadfish.bedwar.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.plugin.PluginEvent;
import cn.nukkit.plugin.Plugin;
import org.sobadfish.bedwar.room.config.GameRoomConfig;

/**
 * @author Sobadfish
 * @date 2023/7/4
 */
public class ReloadWorldEvent extends PluginEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    public GameRoomConfig roomConfig;

    public ReloadWorldEvent(Plugin plugin, GameRoomConfig roomConfig) {
        super(plugin);
        this.roomConfig = roomConfig;
    }

    public GameRoomConfig getRoomConfig() {
        return roomConfig;
    }
}
