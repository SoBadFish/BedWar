package org.sobadfish.bedwar.room.event;

import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomEventConfig;

/**
 * @author Sobadfish
 * 13:37
 */
public abstract class IGameRoomEvent {

    public GameRoomEventConfig.GameRoomEventItem item;

    public IGameRoomEvent(GameRoomEventConfig.GameRoomEventItem item){
        this.item = item;
    }

    /**
     * 事件配置
     * */
    abstract public GameRoomEventConfig.GameRoomEventItem getEventItem();

    /**
     * 事件启动
     * */
    abstract public void onStart(GameRoom room);



}
