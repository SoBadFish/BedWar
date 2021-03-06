package org.sobadfish.bedwar.panel.from;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.panel.DisPlayWindowsFrom;
import org.sobadfish.bedwar.panel.from.button.BaseIButtom;

import java.util.ArrayList;

/**
 * @author SoBadFish
 * 2022/1/12
 */
public class BedWarFrom {

    private int id;

    private ArrayList<BaseIButtom> baseIButtoms = new ArrayList<>();

    private String title,context;
    public BedWarFrom(String title,String context,int id){
        this.title = title;
        this.context = context;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public ArrayList<BaseIButtom> getBaseIButtoms() {
        return baseIButtoms;
    }

    public void add(BaseIButtom baseIButtom){
        baseIButtoms.add(baseIButtom);
    }

    public void disPlay(Player player){
        FormWindowSimple simple = new FormWindowSimple(TextFormat.colorize('&',title),TextFormat.colorize('&', context));
        for(BaseIButtom baseIButtom: baseIButtoms){
            simple.addButton(baseIButtom.getButton());
        }
        player.showFormWindow(simple, getId());
    }

    @Override
    public String toString() {
        return id+" -> "+baseIButtoms;
    }
}
