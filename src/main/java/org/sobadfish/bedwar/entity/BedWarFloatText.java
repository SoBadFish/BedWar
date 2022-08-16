package org.sobadfish.bedwar.entity;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.level.Position;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.manager.FloatTextManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class BedWarFloatText extends Entity {

    public String name;

    public boolean isFinalClose;

    public String text = "";

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

    public int spawnTick = 0;


    public static BedWarFloatText showFloatText(String name,Position position, String text){
        if(!position.getChunk().isLoaded()){
            position.getLevel().loadChunk(position.getChunkX(),position.getChunkZ());
        }
        BedWarFloatText text1 = new BedWarFloatText(name,position.getChunk(),Entity.getDefaultNBT(position));
        text1.setText(text);

        FloatTextManager.addFloatText(text1);
        text1.toDisplay();
        return text1;
    }

    public void disPlayers(){
        for(Player player: player){
            spawnTo(player);

        }
    }

    private void toDisplay(){
        for(Player player: Server.getInstance().getOnlinePlayers().values()){
            this.player.add(player);
            spawnTo(player);

        }
    }
}
