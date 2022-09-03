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
        addStrReplaceString("%bd-level%",data.getLevelString());
        addStrReplaceString("%bd-exp%",data.getExp()+"");
        addStrReplaceString("%bd-nextExp%",data.getNextLevelExp()+"");
        addStrReplaceString("%bd-line%",data.getExpLine(6)+"");
        addStrReplaceString("%bd-per%",String.format("%.2f",data.getExpPercent() * 100)+"");


    }
}
