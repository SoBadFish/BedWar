package org.sobadfish.bedwar.room.floattext;

import cn.nukkit.level.Position;
import org.sobadfish.bedwar.entity.BedWarFloatText;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

public class FloatTextInfo {

    public FloatTextInfoConfig floatTextInfoConfig;

    public BedWarFloatText bedWarFloatText;

    public FloatTextInfo(FloatTextInfoConfig config){
        this.floatTextInfoConfig = config;
    }

    public FloatTextInfo init(GameRoom room){
        try{
            Position position = WorldInfoConfig.getPositionByString(floatTextInfoConfig.position);
            bedWarFloatText = BedWarFloatText.showFloatText(floatTextInfoConfig.name,position,floatTextInfoConfig.text);
            if(bedWarFloatText != null){
                bedWarFloatText.room = room;
            }

        }catch (Exception e){
            return null;
        }

        return this;
    }


}
