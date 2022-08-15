package org.sobadfish.bedwar.manager;

import org.sobadfish.bedwar.entity.BedWarFloatText;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FloatTextManager {

    public static List<BedWarFloatText> floatTextList = new CopyOnWriteArrayList<>();


    public static void addFloatText(BedWarFloatText floatText){
        floatTextList.add(floatText);
    }




    public static void removeFloatText(BedWarFloatText floatText) {
        floatTextList.remove(floatText);
    }
}
