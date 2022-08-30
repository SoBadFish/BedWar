package org.sobadfish.bedwar.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.manager.FloatTextManager;
import org.sobadfish.bedwar.room.GameRoom;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BedWarFloatText extends Entity {

    public String name;

    public boolean isFinalClose;

    public String text = "";

    //如果不为null 就是房间内的浮空字 到时候需要移除
    public GameRoom room;

    public List<Player> player = new CopyOnWriteArrayList<>();

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
        this.text = text;
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
        return super.onUpdate(i);
    }

    public int spawnTick = 0;


    public static BedWarFloatText showFloatText(String name,Position position, String text){
        BedWarFloatText text1;
        try {
            text1 = new BedWarFloatText(name, position.getChunk(), Entity.getDefaultNBT(position));
        }catch (Exception e){
            return null;
        }
        text1.setText(text);
        FloatTextManager.addFloatText(text1);
        text1.toDisplay();
        return text1;
    }

    public void disPlayers(){
        for(Player player: player){
            if(player.getLevel() == getLevel()){
                this.player.add(player);
                spawnTo(player);
            }else{
                RemoveEntityPacket entityPacket = new RemoveEntityPacket();
                entityPacket.eid = getId();
                player.dataPacket(entityPacket);
            }
        }
    }

    private void toDisplay(){
        for(Player player: Server.getInstance().getOnlinePlayers().values()){
            if(!this.player.contains(player)) {
                if(player.getLevel() == getLevel()){
                    this.player.add(player);
                    spawnTo(player);
                }
            }

        }
    }
}
