package org.sobadfish.bedwar.room.event;

import org.sobadfish.bedwar.item.ItemInfo;
import org.sobadfish.bedwar.item.MoneyItemInfo;
import org.sobadfish.bedwar.item.config.MoneyItemInfoConfig;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomEventConfig;

/**
 * 资源点升级 (说白了就是减少时间)
 * */
public class TimeEvent extends IGameRoomEvent{


    public TimeEvent(GameRoomEventConfig.GameRoomEventItem item) {
        super(item);
    }

    @Override
    public GameRoomEventConfig.GameRoomEventItem getEventItem() {
        return item;
    }

    @Override
    public void onStart(GameRoom room) {
        String item = getEventItem().item;
        for(ItemInfo itemInfo: room.getWorldInfo().getInfos()){
            MoneyItemInfoConfig moneyItemInfo = itemInfo.getItemInfoConfig().getMoneyItemInfoConfig();
            if(moneyItemInfo.getName().equalsIgnoreCase(item)){
                //TODO
                itemInfo.setResetTick((int)getEventItem().value);
            }
        }
        room.sendMessage(getEventItem().display);
    }




}
