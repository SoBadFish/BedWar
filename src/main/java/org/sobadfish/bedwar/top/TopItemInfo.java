package org.sobadfish.bedwar.top;

import org.sobadfish.bedwar.entity.BedWarFloatText;

import java.util.Objects;

public class TopItemInfo {

    public BedWarFloatText floatText;

    public TopItem topItem;


    public TopItemInfo(TopItem topItem,BedWarFloatText floatText){
        this.floatText = floatText;
        this.topItem = topItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        TopItemInfo that = (TopItemInfo) o;
        return Objects.equals(topItem, that.topItem);
    }

}
