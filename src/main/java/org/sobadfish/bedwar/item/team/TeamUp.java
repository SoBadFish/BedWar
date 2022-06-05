package org.sobadfish.bedwar.item.team;

/**
 * @author Sobadfish
 */
public class TeamUp extends BaseTeamEffect{

    private int[] delTime;

    private String updateItem;


    public TeamUp(int maxLevel
            ,int[] delTime
            ,String updateItem) {
        super(maxLevel);
        this.delTime = delTime;
        this.updateItem = updateItem;

    }

    public String getMoneyItemInfoConfig() {
        return updateItem;
    }

    public int[] getDelTime() {
        return delTime;
    }

    public int getTimeByLevel(){
        if(delTime.length >= level ){
            return delTime[level - 1];
        }
        return delTime[delTime.length - 1];
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof TeamUp){
            return ((TeamUp) obj).updateItem.equalsIgnoreCase(updateItem);
        }
        return false;
    }
}
