package org.sobadfish.bedwar.variable;

import cn.nukkit.Player;
import com.smallaswater.npc.data.RsNpcConfig;
import com.smallaswater.npc.variable.BaseVariableV2;
import com.smallaswater.npc.variable.VariableManage;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.WorldRoom;
import org.sobadfish.bedwar.room.config.GameRoomConfig;

import java.util.Map;

public class BedWarVariable extends BaseVariableV2 {


    public static void init() {
        VariableManage.addVariableV2("bedwar", BedWarVariable.class);
    }

    @Override
    public void onUpdate(Player player, RsNpcConfig rsNpcConfig) {
        initVariable();
    }


    private void initVariable(){

        for(GameRoomConfig roomConfig: BedWarMain.getRoomManager().getRoomConfigs()){
            addRoomVariable(roomConfig);
        }
        for(Map.Entry<String, WorldRoom> worldRoomEntry: BedWarMain.getMenuRoomManager().getWorldRoomLinkedHashMap().entrySet()){
            WorldRoom worldRoom = worldRoomEntry.getValue();
            int p = 0;
            for(GameRoomConfig roomConfig: worldRoom.getRoomConfigs()){
                GameRoom room = BedWarMain.getRoomManager().getRoom(roomConfig.name);
                if(room != null){
                    p+= room.getPlayerInfos().size();
                }
            }
            addVariable("%bd-"+worldRoom.getName()+"-player%",p+"");

        }
        int game = 0;
        for(GameRoom gameRoom: BedWarMain.getRoomManager().getRooms().values()){
            game += gameRoom.getPlayerInfos().size();
        }
        addVariable("%bd-all-player%",game+"");

    }

    public void addRoomVariable(GameRoomConfig roomConfig){
        int p = 0;
        int mp = roomConfig.getMaxPlayerSize();
        String status = "&a等待中";
        GameRoom room = BedWarMain.getRoomManager().getRoom(roomConfig.name);
        if(room != null){
            p = room.getPlayerInfos().size();
            switch (room.getType()){
                case START:
                    status = "&c游戏中";
                    break;
                case END:
                case CLOSE:
                    status =  "&e结算中";
                    break;
                default:break;
            }
        }
        addVariable("%bd-"+roomConfig.getName()+"-player%",p+"");
        addVariable("%bd-"+roomConfig.getName()+"-maxplayer%",mp+"");
        addVariable("%bd-"+roomConfig.getName()+"-status%",status);
    }
}
