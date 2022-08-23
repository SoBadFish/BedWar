package org.sobadfish.bedwar.plugin;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import net.catrainbow.sakura.event.PlayerCheatEvent;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.RoomManager;
import org.sobadfish.bedwar.player.PlayerInfo;

public class OtherListener implements Listener {

    @EventHandler
    public void onPlayerCheat(PlayerCheatEvent event){
        Player player = event.player;
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
        if(info != null){
            if(info.isSpawnFire){
                event.setCancelled();
            }
        }
    }
}
