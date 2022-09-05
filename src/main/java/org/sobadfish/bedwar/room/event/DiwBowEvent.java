package org.sobadfish.bedwar.room.event;

import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomEventConfig;

/**
 * 凋零弓
 * @author Sobadfish
 * */
public class DiwBowEvent extends IGameRoomEvent implements IEventDurationTime{

    private int loadTime = 0;

    private int outTime = -1;

    public DiwBowEvent(GameRoomEventConfig.GameRoomEventItem item) {
        super(item);
    }

    @Override
    public void onStart(GameRoom room) {
        //增加常驻事件内
        int t = -1;
        try {
            t = Integer.parseInt(item.value+"");
        }catch (Exception ignore){}
        outTime = t;
        room.getEventControl().addResidentEvent(this);
    }

    @Override
    public boolean isOutTime() {
        if(outTime == -1){
            return false;
        }

        return loadTime >= outTime;
    }

    @Override
    public void update() {
        if(outTime != -1){
            loadTime++;
        }

    }
}
