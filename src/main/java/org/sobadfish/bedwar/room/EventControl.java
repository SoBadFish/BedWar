package org.sobadfish.bedwar.room;


import org.sobadfish.bedwar.manager.RoomEventManager;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomEventConfig;
import org.sobadfish.bedwar.room.event.IGameRoomEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件控制器
 * */
public class EventControl {

    public GameRoomEventConfig eventConfig;

    public int loadTime = 0;

    public int position = 0;

    private final GameRoom room;


    private final List<IGameRoomEvent> events = new ArrayList<>();

    public EventControl(GameRoom room,GameRoomEventConfig eventConfig){
        this.eventConfig = eventConfig;
        this.room = room;
        for(GameRoomEventConfig.GameRoomEventItem item : eventConfig.getItems()){
            IGameRoomEvent event = RoomEventManager.getEventByType(item);
            if(event != null){
                events.add(event);
            }

        }

    }

    public boolean enable;

    public void run(){
        if(enable){
            loadTime++;
            if(position < events.size()){
                IGameRoomEvent event = events.get(position);
                if(loadTime >= event.item.eventTime){
                    loadTime = 0;
                    position++;
                    event.onStart(room);
                }
            }else{
                loadTime = 0;
            }

        }
    }

    public boolean hasEvent(){
        if(room.getType() == GameRoom.GameType.START){
            return position < events.size();
        }
        return false;

    }

    public GameRoomEventConfig.GameRoomEventItem getEventConfig() {
        if(position < events.size()) {
            return  events.get(position).item;
        }
        return null;
    }
}
