package org.sobadfish.bedwar.room.floattext;

import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.BedWarFloatText;
import org.sobadfish.bedwar.item.ItemInfo;
import org.sobadfish.bedwar.item.config.MoneyItemInfoConfig;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

import java.io.IOException;

public class FloatTextInfo {

    public FloatTextInfoConfig floatTextInfoConfig;

    public BedWarFloatText bedWarFloatText;

    public FloatTextInfo(FloatTextInfoConfig config){
        this.floatTextInfoConfig = config;
    }

    public FloatTextInfo init(){
        if(!floatTextInfoConfig.position.getChunk().isLoaded()){
            try {
                floatTextInfoConfig.position.getChunk().load();
            } catch (IOException e) {
                BedWarMain.sendMessageToConsole("&c无法加载区块");
                return null;
            }


        }
        bedWarFloatText = BedWarFloatText.showFloatText(floatTextInfoConfig.name,floatTextInfoConfig.position,"");
        return this;
    }

    public void stringUpdate(GameRoom room){
        String text = floatTextInfoConfig.text;
        for(ItemInfo moneyItemInfoConfig: room.getWorldInfo().getInfos()){
            MoneyItemInfoConfig config = moneyItemInfoConfig.getItemInfoConfig().getMoneyItemInfoConfig();
            text = text
                    .replace("%"+config.getName()+"%",config.getCustomName())
                    .replace("%"+config.getName()+"-time%", PlayerInfo.formatTime1((moneyItemInfoConfig.getResetTick() - moneyItemInfoConfig.getTick()) / 20)+"");
        }
        if(bedWarFloatText != null){
            bedWarFloatText.setText(text);
        }
    }
}
