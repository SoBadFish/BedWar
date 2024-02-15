package org.sobadfish.bedwar.panel.from;

import cn.nukkit.Player;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.panel.from.button.BaseIButton;
import org.sobadfish.bedwar.tools.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author SoBadFish
 * 2022/1/12
 */
public class BedWarFrom {

    private final int id;

    private final static int FROM_ID = 155;

    private final static int FROM_MAX_ID = 105478;


    private List<BaseIButton> baseIButtoms = new ArrayList<>();

    private String title;

    private String context;
    public BedWarFrom(String title,String context,int id){
        this.title = title;
        this.context = context;
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContext(String context) {
        this.context = context;
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

    public static int getRId(){
        return Utils.rand(FROM_ID,FROM_MAX_ID);
    }
    public static int getRId(int min,int max){
        return Utils.rand(min,max);
    }


    @Override
    public String toString() {
        return id+" -> "+baseIButtoms;
    }
}
