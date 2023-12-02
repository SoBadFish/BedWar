package org.sobadfish.bedwar.panel.from;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.panel.from.button.BaseIButton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SoBadFish
 * 2022/1/12
 */
public class BedWarFrom {

    private final int id;


    private List<BaseIButton> baseIButtoms = new ArrayList<>();

    private final String title;

    private final String context;
    public BedWarFrom(String title,String context,int id){
        this.title = title;
        this.context = context;
        this.id = id;
    }

    public List<BaseIButton> getBaseIButtoms() {
        return baseIButtoms;
    }

    public void setBaseIButtoms(List<BaseIButton> baseIButtoms) {
        this.baseIButtoms = baseIButtoms;
    }

    public int getId() {
        return id;
    }


    public void add(BaseIButton baseIButtom){
        baseIButtoms.add(baseIButtom);
    }

    public void disPlay(Player player){
        FormWindowSimple simple = new FormWindowSimple(TextFormat.colorize('&',title),TextFormat.colorize('&', context));
        for(BaseIButton baseIButtom: baseIButtoms){
            simple.addButton(baseIButtom.getButton());
        }
        player.showFormWindow(simple, getId());
    }

    @Override
    public String toString() {
        return id+" -> "+baseIButtoms;
    }
}
