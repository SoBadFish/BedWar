package org.sobadfish.bedwar.room.event;

import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomEventConfig;

public class CommandEvent extends IGameRoomEvent{

    public CommandEvent(GameRoomEventConfig.GameRoomEventItem item) {
        super(item);
    }

    @Override
    public void onStart(GameRoom room) {
        for(PlayerInfo info: room.getLivePlayers()){
            Server.getInstance().getCommandMap().dispatch(new ConsoleCommandSender(),getEventItem().value.toString().replace("@p","'"+info.getName()+"'"));
        }
    }
}
