package org.sobadfish.bedwar.item.team;

import lombok.Getter;
import lombok.Setter;

/**
 * 团队的效果
 * @author SoBadFish
 * 2022/1/6
 */
public abstract class BaseTeamEffect implements Cloneable{

    @Getter
    @Setter
    private int maxLevel;

    public int level = 1;

    public BaseTeamEffect(int maxLevel){
        this.maxLevel = maxLevel;
    }


    /**
     * 必须实现判断
     * @param obj 传入对象
     * @return 是否相同
     * */
    @Override
    abstract public boolean equals(Object obj);


    @Override
    public BaseTeamEffect clone() {
        try {
            return (BaseTeamEffect) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
