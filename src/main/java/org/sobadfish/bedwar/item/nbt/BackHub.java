package org.sobadfish.bedwar.item.nbt;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Location;
import cn.nukkit.level.ParticleEffect;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.tools.Utils;

/**
 * 快速回城
 * @author SoBadFish
 * 2022/1/6
 */
public class BackHub implements INbtItem{



    @Override
    public String getName() {
        return
                BedWarMain.getLanguage().getLanguage("back-hub-item","快速回城");
    }

    @Override
    public boolean onClick(Item item, Player player) {
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
        info.sendMessage(BedWarMain.getLanguage().getLanguage("back-hub-tp-msg","5秒后传送至出生点 请不要移动"));

        ThreadManager.TIMER.execute(new Runnable() {

            public Position lastPosition;

            private double radius = 2.0; // 粒子环绕半径

            private double speed = 0.05; // 粒子运动速度

            private double angle = 0.0;

            private double ay = 0.0d;

            @Override
            public void run() {
                PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
                long useTime = System.currentTimeMillis();
                lastPosition = info.getPosition();
                while (System.currentTimeMillis() - useTime <= 5000L){
                    if(info.isDeath()){
                        return;
                    }
                    Position newLocation = info.getLocation();
                    if(Math.abs(newLocation.getX() - lastPosition.getX()) > 2 || Math.abs(newLocation.getZ() - lastPosition.getZ()) > 2){
                        info.sendMessage(BedWarMain.getLanguage().getLanguage("back-hub-wait-tp-msg","&c 传送取消!"));
                        return;
                    }
                    //读条期间有粒子很合理吧
                    info.sendTip(BedWarMain.getLanguage().getLanguage
                            ("back-hub-tp-msg","传送中...")+
                            Utils.drawLine((float) Utils.getPercent((int)
                                            (System.currentTimeMillis() - useTime),5000)
                            ,5,"&a■","&7■"));

                    Location playerLocation = player.getLocation();

                    double x = playerLocation.getX() + radius * Math.cos(angle);
                    double y = playerLocation.getY() + this.ay; // 粒子高度
                    double z = playerLocation.getZ() + radius * Math.sin(angle);

                    // 创建粒子并显示
                    player.getLevel().addParticleEffect(new Vector3(x, y, z), ParticleEffect.ENCHANTING_TABLE_PARTICLE);

                    angle += speed;
                    if (angle >= 2 * Math.PI) {
                        angle = 0;
                    }
                    ay += 0.2;
                    if(ay > 2){
                        ay = 0;
                    }

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return;
                    }
                }

                info.sendMessage(BedWarMain.getLanguage().getLanguage("back-hub-tp-success","&a已传送到出生点"));
                info.getPlayer().teleport(info.getTeamInfo().getTeamConfig().getSpawnPosition());
            }
        });
        player.getInventory().removeItem(item);
        return true;
    }



}
