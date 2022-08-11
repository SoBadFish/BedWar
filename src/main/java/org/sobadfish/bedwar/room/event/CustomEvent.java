package org.sobadfish.bedwar.room.event;

import org.sobadfish.bedwar.manager.RoomEventManager;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomEventConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CustomEvent extends IGameRoomEvent{

    public int position = 0;

    public EventRunType runType;

    public IGameRoomEvent random;

    public boolean isRun;

    private final List<IGameRoomEvent> gameRoomEvents = new ArrayList<>();

    @Override
    public void onCreate(GameRoom room) {
        super.onCreate(room);
        //TODO 这不得解析一下
        init();

    }

    public void init(){
        String[] type = getEventItem().value.toString().split(":");
        for(EventRunType runType: EventRunType.values()){
            if(runType.name().toLowerCase().equalsIgnoreCase(type[0])){
                this.runType = runType;
                break;
            }
        }
        if(type.length > 1){
            String[] v = type[1].split(",");
            for(String obj: v){
                String[] v2 = obj.split("-");
                if(v2.length > 1){
                    int pos = Integer.parseInt(v2[0]);
                    int pos1 = Integer.parseInt(v2[1]);
                    if(pos > pos1){
                        continue;
                    }
                    for(int i = pos;i < pos1;i++){
                        if(room.getEventControl().eventItems.size() > i){
                            IGameRoomEvent event = RoomEventManager.getEventByType(room.getEventControl().eventItems.get(i),room);
                            if(event != null){
                                gameRoomEvents.add(event);
                            }
                        }
                    }
                }else{
                    int i = Integer.parseInt(v2[0]);
                    if(room.getEventControl().eventItems.size() > i){
                        IGameRoomEvent event = RoomEventManager.getEventByType(room.getEventControl().eventItems.get(i),room);
                        if(event != null){
                            gameRoomEvents.add(event);
                        }
                    }
                }
            }
        }
    }

    public enum EventRunType{
        WHILE,RANDOM,FOREACH
    }


    public CustomEvent(GameRoomEventConfig.GameRoomEventItem item) {
        super(item);

    }


    public boolean isEnable;


    public IGameRoomEvent nextEvent(){
        IGameRoomEvent event = null;
        switch (runType){
            case WHILE:
                if(position >= gameRoomEvents.size()){
                    position = 0;
                }
                event = gameRoomEvents.get(position);
                break;
            case RANDOM:
                if(!isRun){
                    if(random == null){
                        random = gameRoomEvents.get(new Random().nextInt(gameRoomEvents.size() - 1));
                    }
                    event = random;
                }

                break;
            case FOREACH:
                if(position < gameRoomEvents.size()){
                    event = gameRoomEvents.get(position);
                }
                break;
        }
        return event;

    }

    @Override
    public void onStart(GameRoom room) {
        IGameRoomEvent event = nextEvent();
        if(runType == EventRunType.RANDOM){
            isRun = true;
        }
        if(event != null){
            event.onStart(room);
        }
    }

    @Override
    public String display() {
        return getEventItem().display+"-"+super.display();
    }
}
