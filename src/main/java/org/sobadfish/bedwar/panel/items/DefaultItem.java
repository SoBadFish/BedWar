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
import lombok.Getter;
import lombok.Setter;
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

    @Getter
    @Setter
    private Item[] item;

    @Getter
    @Setter
    public String moneyItem;



    public DefaultItem(Item[] item,String moneyItem,int count){
        this.item = item;
        this.moneyItem = moneyItem;
        this.count = count;
    }


    @Override
    public int getCount() {
        return count;
    }





    private static Item stringAsItem(String itemStr){
        int count = 1;
        String[] items = itemStr.split(":");
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
        return item;

    }

    public static DefaultItem build(Map<?,?> map){
        String name = map.get("name").toString();
        String ids = map.get("id").toString();

        //TODO 优先输出NBT物品
        if(!map.containsKey("money") || "".equalsIgnoreCase(map.get("money").toString())){
            return null;
        }
        int moneyCount = Integer.parseInt(map.get("money").toString().split("x")[1]);

        String moneyName = map.get("money").toString().split("x")[0];

        ArrayList<Enchantment> enchantments = new ArrayList<>();
        if(map.containsKey("ench")) {
            if (!"".equalsIgnoreCase(map.get("ench").toString())) {
                for (String es : map.get("ench").toString().split("-")) {
                    enchantments.add(Enchantment.get(
                                    Integer.parseInt(es.split(":")[0]))
                            .setLevel(Integer.parseInt(es.split(":")[1])));
                }
            }
        }

        ArrayList<Item> items = new ArrayList<>();
        for(String itemId: ids.split("&")){
            if(NbtItemManager.NBT_MANAGER.containsKey(itemId)){
                //TODO 构建NBT物品
                INbtItem config = NbtItemManager.NBT_MANAGER.get(itemId);
                return new NbtDefaultItem(config,moneyName,moneyCount);
            }
            Item item = stringAsItem(itemId);
            if(item == null){
                continue;
            }
            items.add(item);

        }

        if(items.isEmpty()){
            return null;
        }



        if(!"".equalsIgnoreCase(name)){
            for (Item item: items){
                item.setCustomName(TextFormat.colorize('&',"&r"+name));
            }

        }
        for(Enchantment enchantment: enchantments){
            for (Item item: items){
                item.addEnchantment(enchantment);
            }

        }
        return new DefaultItem(items.toArray(new Item[0]),moneyName,moneyCount);
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
            MoneyItemInfoConfig oInfo = room.getRoomConfig().moneyItem.getItemInfoConfigs().get(0);
            if (room.getRoomConfig().moneyItem.containsKey(moneyItem)) {
                oInfo = room.getRoomConfig().moneyItem.get(moneyItem);
            }
            boolean u = true;
            int rc = (int) oInfo.getExp();
            String errorMessage = BedWarMain.getLanguage().getLanguage("item-not-enough", "[1] 不足",
                    oInfo.getCustomName());

            Item[] iv = getItem();
            if (info.buyArmorId.contains(iv[0].getId())) {
                u = false;
                errorMessage = BedWarMain.getLanguage().getLanguage("armor-allow-buy", "已经购买过此盔甲了");
            }
            if (u) {
                if (room.getRoomConfig().isExp()) {
                    u = info.reduceExp(count * rc);
                    errorMessage = BedWarMain.getLanguage().getLanguage("exp-not-enough", "经验不足");
                } else {
                    u = ItemInfo.use(room.getRoomConfig().moneyItem.get(moneyItem), player.getInventory(), count);
                }
            }
            if (u) {
                for (Item i : iv) {
                    if (i instanceof ItemArmor) {
                        CompoundTag compoundTag = i.getNamedTag();
                        if (compoundTag == null) {
                            compoundTag = new CompoundTag();

                        }
                        compoundTag.putString("bd_master", player.getName());
                        if (room.getRoomConfig().isInventoryUnBreakable()) {
                            //无限耐久
                            compoundTag.putByte("Unbreakable", 1);
                        }

                        i.setNamedTag(compoundTag);

                        info.buyArmorId.add(i.getId());
                        if (i.isHelmet()) {
                            info.getArmor().put(0, i);
                            player.getInventory().setArmorItem(0, i);
                        } else if (i.isChestplate()) {
                            player.getInventory().setArmorItem(1, i);
                            info.getArmor().put(1, i);
                        } else if (i.isLeggings()) {
                            player.getInventory().setArmorItem(2, i);
                            info.getArmor().put(2, i);
                        } else if (i.isBoots()) {
                            player.getInventory().setArmorItem(3, i);
                            info.getArmor().put(3, i);
                        } else {
                            player.getInventory().addItem(i);
                        }
                    } else {
                        if (i.getId() == 35) {
                            int c = i.getCount();
                            Item i2 = info.getTeamInfo().getTeamConfig().getTeamConfig().getBlockWoolColor().clone();
                            i2.setCount(c);
                            player.getInventory().addItem(i2);
                        } else {
                            player.getInventory().addItem(i);
                        }
                    }
                }
            } else {
                info.sendMessage(errorMessage);
                player.getLevel().addSound(player.getPosition(), Sound.MOB_ENDERMEN_PORTAL);

            }
            player.getLevel().addSound(player.getPosition(), Sound.RANDOM_ORB);
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
        Item item = getItem()[0];
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
    public ElementButton getGuiButton(PlayerInfo info) {
        //TODO 如果语言非中文则获取其他名称
        Item it = getItem()[0];
        String itemString = it.getName();
        if("chs".equalsIgnoreCase(BedWarMain.getLanguage().lang)){
            itemString = ItemIDSunName.getNameByItem(it);
        }
        String path = ItemIDSunName.getPathByItem(it);
        if(path == null){
            path =ItemIDSunName.getPathById(it.getId());
        }
        MoneyItemInfoConfig oInfo = info.getGameRoom().getRoomConfig().moneyItem.get(moneyItem);
        String btName = TextFormat.colorize('&',  itemString+" * "+it.getCount()+"\n&rPrice: "+oInfo.getCustomName()+"&r *&a "+count);
        if(info.getGameRoom().getRoomConfig().isExp()){
            int rc = 1;
            if(info.getGameRoom().getRoomConfig().moneyItem.containsKey(moneyItem)){
                rc = (int) oInfo.getExp();
            }
            btName =TextFormat.colorize('&',  itemString+" * "+it.getCount()+"\n&rPrice: Exp * "+count * rc);
        }

        if (path == null){
            return new ElementButton(btName);
        }
        return new ElementButton(btName,new ElementButtonImageData("path",path));
    }
}
