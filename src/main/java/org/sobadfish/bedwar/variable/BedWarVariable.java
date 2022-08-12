package org.sobadfish.bedwar.variable;

import cn.nukkit.Player;
import com.smallaswater.npc.data.RsNpcConfig;
import com.smallaswater.npc.variable.BaseVariableV2;
import com.smallaswater.npc.variable.VariableManage;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomConfig;

public class BedWarVariable extends BaseVariableV2 {


    public static void init() {
        VariableManage.addVariableV2("bedwar",BedWarVariable.class);
    }

    @Override
    public void onUpdate(Player player, RsNpcConfig rsNpcConfig) {
       initVariable();
    }


    private void initVariable(){
        for(GameRoomConfig roomConfig: BedWarMain.getRoomManager().getRoomConfigs()){
            GameRoom room = BedWarMain.getRoomManager().getRoom(roomConfig.name);
            int p = 0;
            int mp = roomConfig.getMaxPlayerSize();
            String status = "&a等待中";
            if(room != null){
                switch (room.getType()){
                    case START:
                        status = "&c游戏中";
                        break;
                    case END:
                    case CLOSE:
                        status = "&e结算中";
                        break;
                    default:break;
                }
            }
            addVariable("%"+roomConfig.getName()+"-player%",p+"");
            addVariable("%"+roomConfig.getName()+"-maxplayer%",mp+"");
            addVariable("%"+roomConfig.getName()+"-status%",status);
        }
    }
}
