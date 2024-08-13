package org.sobadfish.bedwar.room;


import lombok.Getter;
import org.sobadfish.bedwar.manager.RoomEventManager;
import org.sobadfish.bedwar.room.config.GameRoomEventConfig;
import org.sobadfish.bedwar.room.event.CustomEvent;
import org.sobadfish.bedwar.room.event.IEventDurationTime;
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

    @Getter
    public IGameRoomEvent lastEvent;

    public IGameRoomEvent thisEvent;

    //常驻事件
    public List<IGameRoomEvent> residentEvent = new ArrayList<>();

    /**
     * 备选事件
     * */
    public List<GameRoomEventConfig.GameRoomEventItem> eventItems = new ArrayList<>();



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
        eventItems.addAll(room.getRoomConfig().eventListConfig.getItems());

    }

    public void initAll(GameRoom room){
        for(IGameRoomEvent event: events){
            event.onCreate(room);
        }
    }

    public boolean enable;

    public void run(){
        if(enable){
            loadTime++;
            if(position < events.size()){
                IGameRoomEvent event = events.get(position);
                if(event instanceof CustomEvent){
                    if(((CustomEvent) event).isEnable){
                        IGameRoomEvent event1 = ((CustomEvent) event).nextEvent();
                        if(event1 == null ){
                            loadTime = 0;
                            lastEvent = event;
                            position++;
                            thisEvent = null;
                        }else{
                            if(loadTime >= event.getEventTime()){
                                loadTime = 0;
                                event.onStart(room);
                                ((CustomEvent) event).position++;
                                thisEvent = null;
                            }

                        }

                    }else{
                        if(loadTime >= event.getEventTime()) {
                            loadTime = 0;
                            ((CustomEvent) event).isEnable = true;
                            event.onStart(room);
                            ((CustomEvent) event).position++;
                            thisEvent = null;
                        }
                    }
                }else{
                    if(loadTime >= event.getEventTime()){
                        loadTime = 0;
                        lastEvent = event;
                        position++;
                        thisEvent = null;
                        event.onStart(room);
                    }

                }

            }else{
                loadTime = 0;
            }
            if(thisEvent != null && thisEvent instanceof IEventDurationTime){
                ((IEventDurationTime) thisEvent).update();
                if(((IEventDurationTime) thisEvent).isOutTime()){
                    thisEvent = null;
                }
            }
            for(IGameRoomEvent event: new ArrayList<>(residentEvent)){
                if(event instanceof IEventDurationTime){
                    ((IEventDurationTime) event).update();
                    if(((IEventDurationTime) event).isOutTime()){
                        residentEvent.remove(event);
                    }
                }
            }
        }
    }

    /**
     * 配置 roomEventList 内部事件
     * */
    public List<GameRoomEventConfig.GameRoomEventItem> getEventConfigList() {
        return eventItems;
    }


    public IGameRoomEvent getNextEvent() {
        if(hasEvent()){
            return events.get(position);
        }
        return null;

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

    public void addResidentEvent(IGameRoomEvent event){
        residentEvent.add(event);
    }

    public void removeResidentEvent(IGameRoomEvent event){
        residentEvent.remove(event);
    }

    public boolean hasResidentEvent(String name){
        for(IGameRoomEvent event: residentEvent){
            if(event.getEventItem().eventType.equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
}
