package org.sobadfish.bedwar.event;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.plugin.PluginEvent;
import cn.nukkit.plugin.Plugin;

/**
 * @author Sobadfish
 * @date 2023/7/4
 */
public class ReloadWorldEvent extends PluginEvent {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    public String world;

    public ReloadWorldEvent(Plugin plugin, String world) {
        super(plugin);
        this.world = world;
    }
}
