package org.sobadfish.bedwar.room.event;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.weather.EntityLightning;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomEventConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**随机劈玩家 造成 2 点伤害*/
public class LightEvent extends IGameRoomEvent implements Listener {

    public LightEvent(GameRoomEventConfig.GameRoomEventItem item) {
        super(item);
    }

    @Override
    public void onCreate(GameRoom room) {
        super.onCreate(room);
        Server.getInstance().getPluginManager().registerEvents(this, BedWarMain.getBedWarMain());

    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        if(event instanceof EntityDamageByEntityEvent){
            if(((EntityDamageByEntityEvent) event).getDamager() instanceof EntityLightning){
                event.setDamage(7);
            }
        }
    }

    @Override
    public void onStart(GameRoom room) {
        List<PlayerInfo> infos = room.getLivePlayers();
        //选中数量
        int value = 1;
        try{
            value = Integer.parseInt(getEventItem().value.toString());
        }catch (Exception ignore){}
        List<PlayerInfo> chose = new ArrayList<>();
        if(value >= infos.size()){
            chose.addAll(infos);
        }else{
            for(int i = 0;i < value;i++){
                PlayerInfo info = infos.get(new Random().nextInt(infos.size()));
                if(!chose.contains(info) && info.isLive()){
                    chose.add(info);
                }
            }
        }
        for(PlayerInfo info: chose){
            //天选之子
            EntityLightning lightning = new EntityLightning(info.getPlayer().chunk, Entity.getDefaultNBT(info.getPlayer()));
            lightning.setEffect(false);
            lightning.spawnToAll();
            info.sendTitle("&c你被选中了");
        }
        room.sendMessage("&e本次有 &a"+chose.size()+" &e名玩家被选中");

    }
}
