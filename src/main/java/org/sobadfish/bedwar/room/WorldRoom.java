package org.sobadfish.bedwar.room;

import cn.nukkit.form.element.ElementButtonImageData;
import lombok.Data;
import lombok.Getter;
import org.sobadfish.bedwar.room.config.GameRoomConfig;

import java.util.ArrayList;

/**
 * @author SoBadFish
 * 2022/1/12
 */
@Data
public class WorldRoom {

    private String name;

    private ElementButtonImageData imageData;

    private ArrayList<GameRoomConfig> roomConfigs;

    public WorldRoom(String name,ArrayList<GameRoomConfig> roomConfigs,ElementButtonImageData imageData){
        this.name = name;
        this.roomConfigs = roomConfigs;
        this.imageData = imageData;
    }


}
