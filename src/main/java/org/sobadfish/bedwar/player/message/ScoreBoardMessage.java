package org.sobadfish.bedwar.player.message;

import java.util.ArrayList;

/**
 * @author SoBadFish
 * 2022/1/3
 */

public class ScoreBoardMessage {

    private String title;

    private ArrayList<String> lore;

    public ScoreBoardMessage(String title){
        this.title = title;
    }

    public ScoreBoardMessage(String title,ArrayList<String> lore){
        this.title = title;
        this.lore = lore;
    }

    public String getTitle() {
        return title;
    }

    public ArrayList<String> getLore() {
        return lore;
    }

    public void setLore(ArrayList<String> lore) {
        this.lore = lore;
    }
}
