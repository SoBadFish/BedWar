package org.sobadfish.bedwar.variable;

import cn.nukkit.Player;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerData;
import tip.utils.Api;
import tip.utils.variables.BaseVariable;

public class TipVariable extends BaseVariable {

    public TipVariable(Player player) {
        super(player);
    }

    public static void init() {
        Api.registerVariables("Bedwar",TipVariable.class);
    }

    @Override
    public void strReplace() {
        //等级
        PlayerData data = BedWarMain.getDataManager().getData(player.getName());
        addStrReplaceString("{bd-level}",data.getLevelString());
        addStrReplaceString("{bd-exp}",data.getExpString(data.getExp())+"");
        addStrReplaceString("{bd-nextExp}",data.getExpString(data.getNextLevelExp())+"");
        addStrReplaceString("{bd-line}",data.getExpLine(10)+"");
        addStrReplaceString("{bd-per}",String.format("%.2f",data.getExpPercent() * 100)+"");
        addStrReplaceString("{bd-kill}",data.getFinalData(PlayerData.DataType.KILL)+"");
        addStrReplaceString("{bd-victory}",data.getFinalData(PlayerData.DataType.VICTORY)+"");
        addStrReplaceString("{bd-bed}",data.getFinalData(PlayerData.DataType.BED_BREAK)+"");

    }
}
