package org.sobadfish.bedwar.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.item.ItemInfo;
import org.sobadfish.bedwar.item.config.MoneyItemInfoConfig;
import org.sobadfish.bedwar.manager.FloatTextManager;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.tools.Utils;

public class BedWarFloatText extends Entity {

    public String name;

    public boolean isFinalClose;

    public String text = "";

    //如果不为null 就是房间内的浮空字 到时候需要移除
    public GameRoom room;

    public BedWarFloatText(String name,FullChunk fullChunk, CompoundTag compoundTag) {
        super(fullChunk, compoundTag);
        this.name = name;
        this.setNameTagAlwaysVisible(true);
        this.setNameTagVisible(true);
        this.setNameTag(text);
    }

    @Override
    protected float getGravity() {
        return 0;
    }


    @Override
    public int getNetworkId() {
        return 64;
    }

    @Override
    public boolean attack(EntityDamageEvent entityDamageEvent) {
        return false;
    }


    public void setText(String text) {
        this.setText(text,true);
    }

    public void setText(String text,boolean reset) {
        if(reset) {
            this.text = text;
        }
        this.setNameTag(TextFormat.colorize('&',text));
    }

    @Override
    public void close() {
        super.close();
        if(isFinalClose){
            FloatTextManager.removeFloatText(this);

        }

    }

    public void toClose(){
        isFinalClose = true;
        close();
    }

    @Override
    public void saveNBT() {
    }

    @Override
    public boolean onUpdate(int i) {
         super.onUpdate(i);
         return true;
    }


    public static BedWarFloatText showFloatText(String name,Position position, String text){
        BedWarFloatText text1;
        try {
            text1 = new BedWarFloatText(name, position.getChunk(), Entity.getDefaultNBT(position));
        }catch (Exception e){
            return null;
        }
        text1.setText(text);
        FloatTextManager.addFloatText(text1);
        text1.disPlayers();
        return text1;
    }

    /**
     * 显示给玩家，不过这个已经在
     * 写好调用了，不需要再重复调用
     * */
    public void disPlayers(){
        this.despawnFromAll();
        for(Player player: this.level.getPlayers().values()){
            if(!this.hasSpawned.containsKey(player.getLoaderId())) {
                spawnTo(player);
            }
        }
    }

    public void stringUpdate(){
        if(room == null){
            return;
        }
        if(room.getWorldInfo() == null){
            return;
        }
        String uText = text;
        for(ItemInfo moneyItemInfoConfig: room.getWorldInfo().getInfos()){
            MoneyItemInfoConfig config = moneyItemInfoConfig.getItemInfoConfig().getMoneyItemInfoConfig();
            uText = uText
                    .replace("%"+config.getName()+"%",config.getCustomName())
                    .replace("%"+config.getName()+"-time%", Utils.formatTime1(((moneyItemInfoConfig.getResetTick() - moneyItemInfoConfig.getTick())) / 20)+"");
        }
//        if(this.isClosed()){
//            FloatTextManager.removeFloatText(this);
//        }
//        检查是否存活 /
        if(!this.isAlive()){
           this.hasSpawned.clear();
        }
        this.setText(uText,false);


    }
}
