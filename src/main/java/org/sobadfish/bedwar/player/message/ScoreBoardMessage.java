package org.sobadfish.bedwar.player.message;

import lombok.Data;

import java.util.ArrayList;

/**
 * @author SoBadFish
 * 2022/1/3
 */
@Data
public class ScoreBoardMessage {

    private final String title;

    private ArrayList<String> lore;

    public ScoreBoardMessage(String title){
        this.title = title;
    }

    public ScoreBoardMessage(String title,ArrayList<String> lore){
        this.title = title;
        this.lore = lore;
    }


}
