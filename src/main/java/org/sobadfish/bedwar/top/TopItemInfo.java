package org.sobadfish.bedwar.top;

import org.sobadfish.bedwar.entity.BedWarFloatText;

public class TopItemInfo {

    public BedWarFloatText floatText;

    public TopItem topItem;


    public TopItemInfo(TopItem topItem,BedWarFloatText floatText){
        this.floatText = floatText;
        this.topItem = topItem;
    }


}
