package org.sobadfish.bedwar.manager;


import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomEventConfig;
import org.sobadfish.bedwar.room.event.IGameRoomEvent;


import java.lang.reflect.InvocationTargetException;
import java.util.LinkedHashMap;

public class RoomEventManager {

    public static LinkedHashMap<String, Class<? extends IGameRoomEvent>> EVENT = new LinkedHashMap<>();

    public static void register(String name,Class<? extends IGameRoomEvent> event){
        EVENT.put(name, event);
    }




    public static IGameRoomEvent getEventByType(GameRoomEventConfig.GameRoomEventItem item, GameRoom room){
        if(EVENT.containsKey(item.eventType)){
            Class<? extends IGameRoomEvent> e = EVENT.get(item.eventType);
            try {
                IGameRoomEvent event = e.getConstructor(GameRoomEventConfig.GameRoomEventItem.class).newInstance(item);
                event.onCreate(room);
                return event;
            } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e1) {
                e1.printStackTrace();
            }
        }
        return null;

    }

}
