package org.sobadfish.bedwar.panel.items;

import cn.nukkit.Player;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Sound;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.item.ItemIDSunName;
import org.sobadfish.bedwar.item.ItemInfo;
import org.sobadfish.bedwar.item.config.MoneyItemInfoConfig;
import org.sobadfish.bedwar.item.team.*;
import org.sobadfish.bedwar.panel.ChestInventoryPanel;
import org.sobadfish.bedwar.panel.from.ShopFrom;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.room.GameRoom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author SoBadFish
 *         2022/1/5
 */
public class TeamItem extends BasePlayPanelItemInstance {

    private int count;

    private BaseTeamEffect effect;

    private Item panelItem;

    private TeamItem(BaseTeamEffect effect, Item panelItem, String money, int count) {
        this.effect = effect;
        this.panelItem = panelItem;
        this.moneyItem = money;
        this.count = count;
    }

    private String moneyItem;

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Item getItem() {
        return panelItem.clone();
    }

    @Override
    public void onClick(ChestInventoryPanel inventory, Player player) {
        PlayerInfo info = inventory.getPlayerInfo();
        onLevelUp(info);

    }

    private void onLevelUp(PlayerInfo info) {
        Player player = (Player) info.getPlayer();
        int level = getTeamInfoLevel(info);
        GameRoom room = info.getGameRoom();
        if (level == effect.getMaxLevel() && effect.equals(new TeamTrap(1))) {
            info.sendMessage("&c当前团队存在 &r" + getItem().getCustomName());
            return;
        }
        if (level == effect.getMaxLevel()) {
            info.sendMessage("&r&e已到达最大等级");
            return;
        }
        // if(ItemInfo.getCountByInventory(room.getRoomConfig().moneyItem.get(moneyItem),
        // player.getInventory()) > count * level){

        // }
        if (level == 0) {
            level = 1;
        }
        boolean u;
        int rc = 1;
        String errorMessage = moneyItem + "不足";
        if (room.getRoomConfig().moneyItem.containsKey(moneyItem)) {
            MoneyItemInfoConfig oInfo = room.getRoomConfig().moneyItem.get(moneyItem);
            rc = (int) oInfo.getExp();
        }
        if (room.getRoomConfig().isExp()) {
            //由于扣除经验落后一个等级
            u = info.reduceExp(count * (level + 1)  * rc);
            errorMessage = "经验不足";
        } else {
            u = ItemInfo.use(room.getRoomConfig().moneyItem.get(moneyItem), player.getInventory(), count * level);
        }

        if (u) {
            info.getTeamInfo().addEffect(new TeamEffectInfo(effect));
            if (effect.equals(new TeamTrap(1))) {
                info.getTeamInfo().sendMessage(info + " &e购买了 " + getItem().getCustomName());
            } else {
                info.getTeamInfo()
                        .sendMessage(info + " &e升级了&r " + getItem().getCustomName() + " " + (getTeamInfoLevel(info)));
            }
            info.addSound(Sound.RANDOM_ORB);
        } else {
            info.sendMessage(errorMessage);
            player.getLevel().addSound(player.getPosition(), Sound.MOB_ENDERMEN_PORTAL);

        }
    }

    @Override
    public void onClickButton(Player player, ShopFrom shopFrom) {
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
        onLevelUp(info);
        shopFrom.disPlay(shopFrom.getTitle(), shopFrom.isBreak());
    }

    public static TeamItem build(String s, Map map) {
        int maxLevel = Integer.parseInt(map.get("maxLevel").toString());
        Item item = Item.fromString(map.get("disPlay").toString());
        item.setCustomName(TextFormat.colorize('&', map.get("name").toString()));
        int count = Integer.parseInt(map.get("money").toString().split("x")[1]);
        String moneyName = map.get("money").toString().split("x")[0];
        if ("enchants".equalsIgnoreCase(s)) {
            Enchantment enchantment = Enchantment.getEnchantment(Integer.parseInt(map.get("id").toString()));
            return new TeamItem(new TeamEnchant(enchantment, maxLevel), item, moneyName, count);

        } else if ("effects".equalsIgnoreCase(s)) {
            Effect effect = Effect.getEffect(Integer.parseInt(map.get("id").toString()));
            return new TeamItem(new TeamEffect(effect, maxLevel), item, moneyName, count);
        } else if("trap".equalsIgnoreCase(s)){
            return new TeamItem(new TeamTrap(maxLevel), item, moneyName, count);
        }else if("up".equalsIgnoreCase(s)){
            return new TeamItem(new TeamUp(maxLevel,getObjectArrayListToIntArray((List)map.get("times")),map.get("item").toString()), item, moneyName, count);
        }
        return null;

    }

    private static int[] getObjectArrayListToIntArray(List list){
        int[] array = new int[list.size()];
        for (int i =0;i< list.size();i++) {
            if(list.get(i) instanceof Integer){
                array[i] = (int) list.get(i);
            }
        }
        return array;
    }

    private int getTeamInfoLevel(PlayerInfo info) {
        int level = 0;
        TeamInfo info1 = info.getTeamInfo();
        if (info1 != null) {
            if (info1.getTeamEffects().contains(new TeamEffectInfo(effect))) {
                level = info1.getTeamEffects().get(info1.getTeamEffects().indexOf(new TeamEffectInfo(effect)))
                        .getLevel();
                if (level >= effect.getMaxLevel()) {
                    return effect.getMaxLevel();
                }
            }
        }
        return level;
    }

    @Override
    public Item getPanelItem(PlayerInfo info, int index) {
        Item c = getItem();
        Item i = new Item(c.getId(), c.getDamage(), c.getCount());
        i.setCustomName(c.getCustomName());
        int level = getTeamInfoLevel(info);
        level++;
        if (level > effect.getMaxLevel()) {
            level--;
        }
        if (effect.equals(new TeamTrap(1))) {
            i.setCustomName(TextFormat.colorize('&', "&r购买 " + getItem().getCustomName()));
        } else {
            i.setCustomName(TextFormat.colorize('&', "&r升级 " + getItem().getCustomName() + "" + (level)));
        }

        ArrayList<String> lore = new ArrayList<>();
        String title;
        if(info.getGameRoom().getRoomConfig().isExp()){
            int rc = 1;
            if(info.getGameRoom().getRoomConfig().moneyItem.containsKey(moneyItem)){
                MoneyItemInfoConfig oInfo = info.getGameRoom().getRoomConfig().moneyItem.get(moneyItem);
                rc = (int) oInfo.getExp();
            }
            title = "&rExp x " + count * rc * level;
        }else{
            title = "&r" + moneyItem + " x " + count * level;
        }

        if (getTeamInfoLevel(info) == effect.getMaxLevel()) {
            title = "&r&c已到达最大等级";
        }
        lore.add(TextFormat.colorize('&', title));
        i.setLore(lore.toArray(new String[0]));
        i.setNamedTag(i.getNamedTag().putInt("index", index));
        if (effect instanceof TeamEnchant) {
            i.addEnchantment(((TeamEnchant) effect).getEnchantment().setLevel(level));
        }
        return i;
    }

    @Override
    public ElementButton getGUIButton(PlayerInfo info) {
        int level = getTeamInfoLevel(info);
        level++;
        if (level > effect.getMaxLevel()) {
            level--;
        }
        String txt = "升级 " + getItem().getCustomName() + "" + (level);
        if (effect.equals(new TeamTrap(1))) {
            txt = "购买 " + getItem().getCustomName();
        }
        String title = "&r" + moneyItem + " x " + count * level;
        if(info.getGameRoom().getRoomConfig().isExp()){
            int rc = 1;
            if(info.getGameRoom().getRoomConfig().moneyItem.containsKey(moneyItem)){
                MoneyItemInfoConfig oInfo = info.getGameRoom().getRoomConfig().moneyItem.get(moneyItem);
                rc = (int) oInfo.getExp();
            }
            title = "&rExp x " + count * rc * level;
        }

        if (getTeamInfoLevel(info) == effect.getMaxLevel()) {
            title = "&c已到达最大等级";
        }
        return new ElementButton(TextFormat.colorize('&', txt + "\n&r" + title),
                new ElementButtonImageData("path",
                        ItemIDSunName.getIDByPath(getItem().getId(), getItem().getDamage())));
    }
}
