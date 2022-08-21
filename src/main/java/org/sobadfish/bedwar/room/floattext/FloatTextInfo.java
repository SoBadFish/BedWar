package org.sobadfish.bedwar.room.floattext;

import cn.nukkit.level.Position;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.BedWarFloatText;
import org.sobadfish.bedwar.item.ItemInfo;
import org.sobadfish.bedwar.item.config.MoneyItemInfoConfig;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.io.IOException;

public class FloatTextInfo {

    public FloatTextInfoConfig floatTextInfoConfig;

    public BedWarFloatText bedWarFloatText;

    public FloatTextInfo(FloatTextInfoConfig config){
        this.floatTextInfoConfig = config;
    }

    public FloatTextInfo init(){
//        if(!floatTextInfoConfig.position.getChunk().isLoaded()){
//            floatTextInfoConfig.position.getLevel().loadChunk(floatTextInfoConfig.position.getChunkX(),floatTextInfoConfig.position.getChunkZ());
//
//
//        }
        try{
            Position position = WorldInfoConfig.getPositionByString(floatTextInfoConfig.position);
            bedWarFloatText = BedWarFloatText.showFloatText(floatTextInfoConfig.name,position,"");
        }catch (Exception e){
            return null;
        }

        return this;
    }

    public boolean stringUpdate(GameRoom room){
        String text = floatTextInfoConfig.text;
        if(room == null){
            return false;
        }
        if(room.getWorldInfo() == null){
            return false;
        }
        for(ItemInfo moneyItemInfoConfig: room.getWorldInfo().getInfos()){
            MoneyItemInfoConfig config = moneyItemInfoConfig.getItemInfoConfig().getMoneyItemInfoConfig();
            text = text
                    .replace("%"+config.getName()+"%",config.getCustomName())
                    .replace("%"+config.getName()+"-time%", PlayerInfo.formatTime1((moneyItemInfoConfig.getResetTick() - moneyItemInfoConfig.getTick()))+"");
        }
        if(bedWarFloatText != null){
            bedWarFloatText.setText(text);
        }
        return true;
    }
}
