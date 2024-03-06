package org.sobadfish.bedwar.panel.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemArmor;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.item.ItemIDSunName;
import org.sobadfish.bedwar.item.ItemInfo;
import org.sobadfish.bedwar.item.config.MoneyItemInfoConfig;
import org.sobadfish.bedwar.item.nbt.INbtItem;
import org.sobadfish.bedwar.manager.NbtItemManager;
import org.sobadfish.bedwar.panel.ChestInventoryPanel;
import org.sobadfish.bedwar.panel.from.ShopFrom;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

import java.util.ArrayList;
import java.util.Map;


/**
 * 用作商店展示
 * @author SoBadFish
 * 2022/1/2
 */
public class DefaultItem extends BasePlayPanelItemInstance {

    public static String TAG = "BED_DEFAULT_PANEL";

    public int count;

    private final Item item;

    public String moneyItem;

    public void setMoneyItem(String moneyItem) {
        this.moneyItem = moneyItem;
    }

    public String getMoneyItem() {
        return moneyItem;
    }

    public DefaultItem(Item item,String moneyItem,int count){
        this.item = item;
        this.moneyItem = moneyItem;
        this.count = count;
    }


    @Override
    public int getCount() {
        return count;
    }


//    public static DefaultItem build(Map map){
//        //TODO 根据配置去构建普通物品
//        String id = map.get("id").toString();
//        String name = TextFormat.colorize('&',map.get("name").toString());
//        ArrayList<Enchantment> enchantments = new ArrayList<>();
//        if(!"".equalsIgnoreCase(map.get("ench").toString())) {
//            for (String es : map.get("ench").toString().split("-")) {
//                enchantments.add(Enchantment.get(
//                        Integer.parseInt(es.split(":")[0]))
//                        .setLevel(Integer.parseInt(es.split(":")[1])));
//            }
//        }
//        int count = Integer.parseInt(map.get("money").toString().split("x")[1]);
//        String moneyName = map.get("money").toString().split("x")[0];
//        if(NbtItemManager.NBT_MANAGER.containsKey(id)){
//            //TODO 构建NBT物品
//            INbtItem config = NbtItemManager.NBT_MANAGER.get(id);
//            return new NbtDefaultItem(config,moneyName,count);
//        }
//        String[] ids = id.split(":");
//        int i = 0;
//        int d = 0;
//        int c = 1;
//        if(ids.length > 1){
//            i = Integer.parseInt(ids[0]);
//            d = Integer.parseInt(ids[1]);
//            if(ids.length > 2){
//                c = Integer.parseInt(ids[2]);
//            }
//        }else if(ids.length > 0){
//            i = Integer.parseInt(ids[0]);
//        }
//        Item item = Item.get(i,d,c);
//        if(!"".equalsIgnoreCase(name)){
//            item.setCustomName(name);
//        }
//        for(Enchantment enchantment: enchantments){
//            item.addEnchantment(enchantment);
//        }
//        return new DefaultItem(item,moneyName,count);
//    }

    public static DefaultItem build(Map<?,?> map){
        String ids = map.get("id").toString();
        String[] items = ids.split(":");
        String name = TextFormat.colorize('&',map.get("name").toString());
        ArrayList<Enchantment> enchantments = new ArrayList<>();
        if(!"".equalsIgnoreCase(map.get("ench").toString())) {
            for (String es : map.get("ench").toString().split("-")) {
                enchantments.add(Enchantment.get(
                                Integer.parseInt(es.split(":")[0]))
                        .setLevel(Integer.parseInt(es.split(":")[1])));
            }
        }
        if(!map.containsKey("money") || "".equalsIgnoreCase(map.get("money").toString())){
           return null;
        }
        int moneyCount = Integer.parseInt(map.get("money").toString().split("x")[1]);

        String moneyName = map.get("money").toString().split("x")[0];
        int count = 1;
        Item item;
        try{
            int id = Integer.parseInt(items[0]);
            int damage = 0;
            if(items.length > 1){
                try {
                    damage = Integer.parseInt(items[1]);
                }catch (Exception ignore){}
                if(items.length > 2){
                    try {
                        count = Integer.parseInt(items[2]);
                    }catch (Exception ignore){}
                    if(count < 0){
                        count = 1;
                    }
                }
            }
            item = Item.get(id,damage,count);
        }catch (Exception e){
            Item i = Item.fromString(items[0]);
            if(i.getId() == 0){
                if(NbtItemManager.NBT_MANAGER.containsKey(ids)){
                    //TODO 构建NBT物品
                    INbtItem config = NbtItemManager.NBT_MANAGER.get(ids);
                    return new NbtDefaultItem(config,moneyName,moneyCount);
                }
            }
            if(i.getId() == 0){
                return null;
            }
            if(items.length > 1){
                try {
                    count = Integer.parseInt(items[1]);
                }catch (Exception ignore){}
                if(count <= 0){
                    count = 1;
                }
            }
            i.setCount(count);
            item = i;
        }
        if(item.getId() == 0){
            return null;
        }
        if(!"".equalsIgnoreCase(name)){
            item.setCustomName(name);
        }
        for(Enchantment enchantment: enchantments){
            item.addEnchantment(enchantment);
        }
        return new DefaultItem(item,moneyName,moneyCount);
    }


    @Override
    public Item getItem() {
        return item;
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        PlayerInfo info = inventory.getPlayerInfo();

        givePlayerItem(info);
    }

    private void givePlayerItem(PlayerInfo info){
        if(info != null) {
            Player player = (Player) info.getPlayer();

            GameRoom room = info.getGameRoom();
            boolean u = true;
            int rc = 1;
            String errorMessage = moneyItem+"不足";
            if(room.getRoomConfig().moneyItem.containsKey(moneyItem)){
                MoneyItemInfoConfig oInfo = room.getRoomConfig().moneyItem.get(moneyItem);
                rc = (int) oInfo.getExp();
            }
            Item i = getItem();
            if(info.buyArmorId.contains(i.getId())){
                u = false;
                errorMessage = "已经购买过此盔甲了";
            }
            if(u) {
                if (room.getRoomConfig().isExp()) {
                    u = info.reduceExp(count * rc);
                    errorMessage = "经验不足";
                } else {
                    u = ItemInfo.use(room.getRoomConfig().moneyItem.get(moneyItem), player.getInventory(), count);
                }
            }
            // boolean u = ItemInfo.use(room.getRoomConfig().moneyItem.get(moneyItem), player.getInventory(), count);

            if (u) {
                if(i instanceof ItemArmor){
                    CompoundTag compoundTag = i.getNamedTag();
                    if(compoundTag == null){
                        compoundTag = new CompoundTag();

                    }
                    compoundTag.putString("bd_master",player.getName());
                    if(room.getRoomConfig().isInventoryUnBreakable()){
                        //无限耐久
                        compoundTag.putByte("Unbreakable",1);
                    }

                    i.setNamedTag(compoundTag);


                    info.buyArmorId.add(i.getId());
                    if(i.isHelmet()) {
                        info.getArmor().put(0,i);
                        player.getInventory().setArmorItem(0,i);
                    }else if(i.isChestplate()){
                        player.getInventory().setArmorItem(1,i);
                        info.getArmor().put(1,i);
                    }else if(i.isLeggings()){
                        player.getInventory().setArmorItem(2,i);
                        info.getArmor().put(2,i);
                    }else if(i .isBoots()){
                        player.getInventory().setArmorItem(3,i);
                        info.getArmor().put(3,i);
                    }else{
                        player.getInventory().addItem(i);
                    }
                }else {
                    if (i.getId() == 35) {
                        int c =i.getCount();
                        Item i2 = info.getTeamInfo().getTeamConfig().getTeamConfig().getBlockWoolColor().clone();
                        i2.setCount(c);
                        player.getInventory().addItem(i2);
                    }else {
                        player.getInventory().addItem(i);
                    }
                }
                player.getLevel().addSound(player.getPosition(),Sound.RANDOM_ORB);
            } else {
                info.sendMessage(errorMessage);
                player.getLevel().addSound(player.getPosition(),Sound.MOB_ENDERMEN_PORTAL);

            }
        }
    }

    @Override
    public void onClickButton(Player player, ShopFrom shopFrom) {
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
        givePlayerItem(info);
        shopFrom.disPlay(shopFrom.getTitle(),shopFrom.isBreak);

    }


    @Override
    public Item getPanelItem(PlayerInfo info,int index) {
        Item item = getItem().clone();
        ArrayList<String> lore = new ArrayList<>();
        if(info.getGameRoom().getRoomConfig().isExp()){
            int rc = 1;
            if(info.getGameRoom().getRoomConfig().moneyItem.containsKey(moneyItem)){
                MoneyItemInfoConfig oInfo = info.getGameRoom().getRoomConfig().moneyItem.get(moneyItem);
                rc = (int) oInfo.getExp();
            }
            lore.add(TextFormat.colorize('&',"&rExp x "+count * rc));
        }else{
            MoneyItemInfoConfig oInfo = info.getGameRoom().getRoomConfig().moneyItem.get(moneyItem);
            lore.add(TextFormat.colorize('&',"&r"+oInfo.getCustomName()+" &rx&a "+count));
        }
//
        item.setLore(lore.toArray(new String[0]));
        item.setNamedTag(item.getNamedTag().putInt("index",index));
        return item;
    }

    @Override
    public ElementButton getGUIButton(PlayerInfo info) {
        //TODO 如果语言非中文则获取其他名称
        String itemString = getItem().getName();
        if("chs".equalsIgnoreCase(BedWarMain.getLanguage().lang)){
            itemString = ItemIDSunName.getIDByName(getItem());
        }
        String path = ItemIDSunName.getIDByPath(getItem().getId(),getItem().getDamage());
        if(path == null){
            path =ItemIDSunName.getIDByPath(getItem().getId());
        }
        MoneyItemInfoConfig oInfo = info.getGameRoom().getRoomConfig().moneyItem.get(moneyItem);
        String btName = TextFormat.colorize('&',  itemString+" * "+getItem().getCount()+"\n&r价格: "+oInfo.getCustomName()+"&r *&a "+count);
        if(info.getGameRoom().getRoomConfig().isExp()){
            int rc = 1;
            if(info.getGameRoom().getRoomConfig().moneyItem.containsKey(moneyItem)){
                rc = (int) oInfo.getExp();
            }
            btName =TextFormat.colorize('&',  itemString+" * "+getItem().getCount()+"\n&r价格: Exp * "+count * rc);
        }

        if (path == null){
            return new ElementButton(btName);
        }
        return new ElementButton(btName,new ElementButtonImageData("path",path));
    }
}
