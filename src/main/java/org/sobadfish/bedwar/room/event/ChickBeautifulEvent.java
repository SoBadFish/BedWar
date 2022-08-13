package org.sobadfish.bedwar.room.event;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.passive.EntityChicken;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.event.entity.EntityDeathEvent;
import cn.nukkit.event.player.PlayerMoveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.config.GameRoomEventConfig;


import java.util.Random;

/**
 * 鸡你太美~
 * */
public class ChickBeautifulEvent extends IGameRoomEvent implements Listener {

    @Override
    public void onCreate(GameRoom room) {
        super.onCreate(room);
        Server.getInstance().getPluginManager().registerEvents(this,BedWarMain.getBedWarMain());
    }

    public ChickBeautifulEvent(GameRoomEventConfig.GameRoomEventItem item) {
        super(item);
    }

    @Override
    public void onStart(GameRoom room) {

        room.getEventControl().thisEvent = this;

    }

    @EventHandler
    public void onMove(PlayerMoveEvent event){
        Player player = event.getPlayer();
        if(room != null && room.getType() == GameRoom.GameType.START){
            if(room.getEventControl().thisEvent != null && room.getEventControl().thisEvent == this){
                PlayerInfo info = room.getPlayerInfo(player);
                if(info == null || (info.isWatch())){
                    return;
                }
                double v = (double) getEventItem().value;
                if(v < 0){
                    v *= 100;
                }
                int r = (int)v;
                if(new Random().nextInt(100) <= r){
                    //TODO 生成鸡你太美
                    Position position = player.getPosition();
                    position.add(0,2,0);
                    EntityChicken chicken = new EntityChicken(player.chunk, Entity.getDefaultNBT(player));
                    chicken.setMaxHealth(1);
                    chicken.setHealth(1);
                    chicken.addMotion(1,1,1);
                    chicken.setNameTagAlwaysVisible(true);
                    chicken.setNameTagVisible(true);
                    chicken.setNameTag(TextFormat.colorize('&',info+" - &r&e&l鸡你太美"));
                    CompoundTag tag = chicken.namedTag;
                    tag.putBoolean("bedwar",true);

                    chicken.namedTag = tag;
                    chicken.spawnToAll();

                }
            }

        }
    }
    @EventHandler
    public void onDeath(EntityDeathEvent event){
        Entity entity = event.getEntity();
        if(room != null && room.getType() == GameRoom.GameType.START) {
            if (entity.namedTag.contains("bedwar")) {
                event.setDrops(new Item[0]);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event){
        Entity entity = event.getEntity();
        if(room != null && room.getType() == GameRoom.GameType.START){
            if(entity.namedTag.contains("bedwar")){
                if(event instanceof EntityDamageByEntityEvent){
                    Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                    if(damager instanceof Player){
                        PlayerInfo info = room.getPlayerInfo((Player) damager);
                        if(info == null || (info.isWatch())){
                            return;
                        }
                        info.sendTitle("&e你干嘛~~~");
                    }
                }
            }
        }

    }
}
