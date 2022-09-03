package org.sobadfish.bedwar.event;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

public class PlayerLevelChangeEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    private final String playerName;

    private final int oldLevel;

    private int newLevel;

    public PlayerLevelChangeEvent(String playerName,int oldLevel,int newLevel){
        this.playerName = playerName;
        this.newLevel = newLevel;
        this.oldLevel = oldLevel;

    }

    public String getPlayerName() {
        return playerName;
    }

    public int getOldLevel() {
        return oldLevel;
    }

    public int getNewLevel() {
        return newLevel;
    }

    public void setNewLevel(int newLevel) {
        this.newLevel = newLevel;
    }
}
