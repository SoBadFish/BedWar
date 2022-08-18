package org.sobadfish.bedwar.room.event;

import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomEventConfig;

/**
 * @author Sobadfish
 * 13:37
 */
public abstract class IGameRoomEvent {

    public GameRoom room;

    public GameRoomEventConfig.GameRoomEventItem item;

    public IGameRoomEvent(GameRoomEventConfig.GameRoomEventItem item){
        this.item = item;
    }

    /**
     * 事件配置
     * */
    public GameRoomEventConfig.GameRoomEventItem getEventItem(){
        return item;
    }

    public int getEventTime(){
        return item.eventTime;
    }
    /**
     * 事件启动
     * */
    abstract public void onStart(GameRoom room);

    /**
     * 事件被创建
     * */
    public void onCreate(GameRoom room){
        this.room = room;
    }

    public String display(){
        return item.display;
    }



}
