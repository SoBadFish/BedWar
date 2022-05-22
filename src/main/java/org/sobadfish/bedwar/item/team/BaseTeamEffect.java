package org.sobadfish.bedwar.item.team;

/**
 * 团队的效果
 * @author SoBadFish
 * 2022/1/6
 */
public abstract class BaseTeamEffect implements Cloneable{

    private int maxLevel;

    public int level = 1;

    public BaseTeamEffect(int maxLevel){
        this.maxLevel = maxLevel;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    /**
     * 必须实现判断
     * @param obj 传入对象
     * @return 是否相同
     * */
    @Override
    abstract public boolean equals(Object obj);

    public void setLevel(int level) {
        this.level = level;
    }



}
