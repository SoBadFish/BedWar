package org.sobadfish.bedwar.item;

import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.Item;

import cn.nukkit.level.Position;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import org.sobadfish.bedwar.item.config.ItemInfoConfig;
import org.sobadfish.bedwar.item.config.MoneyItemInfoConfig;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;


/**
 * @author SoBadFish
 * 2022/1/2
 */

public class ItemInfo {

    private int tick;

    private ItemInfoConfig itemInfoConfig;


    public ItemInfo(ItemInfoConfig config){
        this.itemInfoConfig = config;
    }

    public static int getCountByInventory(MoneyItemInfoConfig info, PlayerInventory inventory){
        int count = 0;
        for(Item item: inventory.getContents().values()){
            if(item.equals(info.getItem(),true,true)){
                count+= item.getCount();
            }
        }
        return count;
    }

    public int getTick() {
        return tick;
    }

    public void setTick(int tick) {
        this.tick = tick;
    }

    public static boolean use(MoneyItemInfoConfig info, PlayerInventory inventory, int count){
        if(getCountByInventory(info,inventory) >= count){
            Item r = info.getItem().clone();
            r.setCount(count);
            inventory.removeItem(r);
            return true;
        }
        return false;
    }

    public static EntityItem getEntityItem(Item item,Position position){
        CompoundTag itemTag = NBTIO.putItemHelper(item);
        itemTag.setName("Item");
        Position position1 = position.add(0.5,0.5,0.5);
        EntityItem itemEntity = new EntityItem(position1.getLevel().getChunk((int)position1.getX() >> 4, (int)position1.getZ() >> 4, true),
                new CompoundTag().putList(new ListTag<>("Pos")
                        .add(new DoubleTag("", position1.getX()))
                        .add(new DoubleTag("", position1.getY()))
                        .add(new DoubleTag("", position1.getZ())))
                        .putShort("Health", 5)
                        .putCompound("Item", itemTag)
                        .putList((new ListTag<>("Motion"))
                                .add(new DoubleTag("", 0))
                                .add(new DoubleTag("", 0))
                                .add(new DoubleTag("",0)))
                        .putList((new ListTag<>("Rotation"))
                                .add(new FloatTag("", ThreadLocalRandom.current().nextFloat() * 360.0F))
                                .add(new FloatTag("", 0.0F)))
                        .putShort("PickupDelay", 10));
        return itemEntity;

    }

    public ItemInfoConfig getItemInfoConfig() {
        return itemInfoConfig;
    }

    public void toUpdate(){
        if(tick == itemInfoConfig.getSpawnTick()){
            tick = 0;
            itemInfoConfig.getPositions().forEach(position ->{

                ArrayList<EntityItem> entityItems = new ArrayList<>();
                for(Entity entity : position.getLevel().getChunkEntities(position.getChunkX(),position.getChunkZ()).values()){
                    if(entity instanceof EntityItem){
                        if(((EntityItem) entity).getItem().equals(getItemInfoConfig().getMoneyItemInfoConfig().getItem())){
                            entityItems.add((EntityItem) entity);
                        }
                    }
                }
                if(entityItems.size() > 20){
                    return;
                }
                Item item = getItemInfoConfig().getMoneyItemInfoConfig().getItem();
                getEntityItem(item,position).spawnToAll();

            } );
        }else{
            tick++;
        }
    }

}
