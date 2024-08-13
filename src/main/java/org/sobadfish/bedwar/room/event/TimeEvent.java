package org.sobadfish.bedwar.room.event;

import org.sobadfish.bedwar.item.ItemInfo;
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
    public void onStart(GameRoom room) {
        String[] item = getEventItem().value.toString().split(":");
        if(item[0].equalsIgnoreCase("复活")){
            room.reSpawnTime = Integer.parseInt(item[1]);
            room.sendMessage(getEventItem().display);
            return;
        }
        for(ItemInfo itemInfo: room.getWorldInfo().getInfos()){
            MoneyItemInfoConfig moneyItemInfo = itemInfo.getItemInfoConfig().getMoneyItemInfoConfig();
            if(moneyItemInfo.getName().equalsIgnoreCase(item[0])){
                //TODO
                itemInfo.setResetTick(Integer.parseInt(item[1]));
            }
        }
        room.sendMessage(getEventItem().display);
    }




}
