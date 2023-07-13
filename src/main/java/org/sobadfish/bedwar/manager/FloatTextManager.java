package org.sobadfish.bedwar.manager;

import org.sobadfish.bedwar.entity.BedWarFloatText;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class FloatTextManager {

    public static List<BedWarFloatText> floatTextList = new CopyOnWriteArrayList<>();


    /**
     * 请不要用这里的方法直接添加浮空字
     * 使用{@link BedWarFloatText}中 showFloatText 的方法生成浮空字
     * */
    public static void addFloatText(BedWarFloatText floatText){
        floatTextList.add(floatText);
    }


    /**
     * 请不要用这里的方法直接删除浮空字
     * 使用{@link BedWarFloatText}中的方法toClose()方法浮空字
     * */
    public static void removeFloatText(BedWarFloatText floatText) {
        floatTextList.remove(floatText);
    }
}
