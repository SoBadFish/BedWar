package org.sobadfish.bedwar.room.config;


import cn.nukkit.utils.Config;
import lombok.Data;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.item.MoneyItemInfo;
import org.sobadfish.bedwar.item.NbtItemInfo;
import org.sobadfish.bedwar.item.config.ItemInfoConfig;
import org.sobadfish.bedwar.manager.SkinManager;
import org.sobadfish.bedwar.player.team.config.TeamConfig;
import org.sobadfish.bedwar.player.team.config.TeamInfoConfig;
import org.sobadfish.bedwar.room.floattext.FloatTextInfoConfig;
import org.sobadfish.bedwar.shop.config.ShopInfoConfig;
import org.sobadfish.bedwar.shop.item.ShopItemInfo;
import org.sobadfish.bedwar.tools.Utils;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.io.File;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author SoBadFish
 * 2022/1/2
 */
@Data
public class GameRoomConfig implements Cloneable{
    /**
     * 房间名
     * */
    public String name;

    /**
     * 地图配置
     * */
    public WorldInfoConfig worldInfo;

    /**
     * 游戏时长
     * */
    public int time;
    /**
     * 等待时长
     * */
    public int waitTime;
    /**
     * 满人等待时长
     * */
    private int maxWaitTime;

    /**
     * 最低人数
     * */
    public int minPlayerSize;

    /**
     * 最大人数
     * */
    private int maxPlayerSize;

    /**
     * 商店
     * */
    public LinkedHashMap<String, ShopItemInfo> shops;

    /**
     * 队伍数据信息
     * */
    public List<TeamConfig> teamCfg = new LinkedList<>();

    /**
     * 队伍
     * */
    public ArrayList<TeamInfoConfig> teamConfigs;

    /**
     * 基础货币
     * */
    public MoneyItemInfo moneyItem;

    /**
     * 特殊物品
     * */
    public NbtItemInfo nbtItemInfo;

    /**
     * UI界面
     * */
    public BedWarMain.UiType uiType;

    /**
     * 自定义火球击退距离
     * */
    public float fireballKnockBack = 0.6f;

    /**
     * 自定义TNT击退距离
     * */
    public float tntKnockBack = 0.6f;

    /**
     * 获取击杀玩家的物品 (50%)
     * */
    public float killItem = 0.5f;

    /**
     * 是否允许旁观
     * */
    public boolean hasWatch = true;
    /**
     * 是否开启物品均分
     * */
    public boolean enableItemEqual = false;

    /**
     * 显示物品名称
     * */
    public boolean displayItemName = false;

    /**
     * 是否启用饥饿
     * */
    public boolean enableFood = false;

    /**
     * 快速搭路
     * */
    public boolean fastPlace = false;

    /**
     * 快速搭路放置数量
     * */
    public int fastPlaceCount = 5;


    /**
     * 等待大厅拉回坐标
     * */
    public int callbackY = 17;

    /**
     * 击退设置
     * */
    public KnockConfig knockConfig = new KnockConfig();

    /**
     * 玩家身上的装备无限耐久
     * */
    public boolean inventoryUnBreakable = false;




    /**
     * 房间游戏货币类型
     * 目前只有default 和 exp
     *
     */
    public String gameRoomMoney = "default";

    /**
     * 团队商店NPC的生物ID
     * */
    public int teamShopEntityId = 15;


    /**
     * 道具商店NPC的生物ID
     */
    public int itemShopEntityId = 15;

    /**
     * 每分钟获得的经验
     */
    public int minutesExp = 25;

    /**
     * 死亡提示的图标
     */
    public int deathIcon = 20;

    /**
     * 禁用指令
     * */
    public ArrayList<String> banCommand = new ArrayList<>();

    /**
     * 退出房间执行指令
     * */
    public ArrayList<String> quitRoomCommand = new ArrayList<>();

    /**
     * 是否开始下一局
     * */
    public boolean isAutomaticNextRound = true;

    /**
     * 玩家胜利执行命令
     * */
    public ArrayList<String> victoryCommand = new ArrayList<>();

    /**
     * 玩家失败执行命令
     * */
    public ArrayList<String> defeatCommand = new ArrayList<>();

    /**
     * 游戏开始的一些介绍
     * */
    public ArrayList<String> gameStartMessage = new ArrayList<>();

    /**
     * 游戏的事件
     * */
    public GameRoomEventConfig eventConfig;

    /**
     * 游戏备选事件列表
     * */
    public GameRoomEventConfig eventListConfig;

    /**
     * 游戏浮空字
     * */
    public List<FloatTextInfoConfig> floatTextInfoConfigs = new CopyOnWriteArrayList<>();

    /**
     * 浮空方块
     * */
    public LinkedHashMap<String, String> floatBlockConfig = new LinkedHashMap<>();

    /**
     * 自定义的一些文本
     * */
    public boolean enableCustomTag = true;


    public boolean enableAutoDisplayFloat = true;

    public String autoDisplayFloat;


    public String customNamedTag;


    /**
     * TNT伤害
     * */
    public int tntDamage = 8;

    /**
     * TNT爆炸时间
     * */
    public int tntExplodeTime = 5;

    /**
     * 商店类型
     * */
    public List<ShopInfoConfig.ShopItemClassify> shopItemClassifies;


    private GameRoomConfig(String name,
                           WorldInfoConfig worldInfo,
                           int time,
                           int waitTime,
                           int maxWaitTime,
                           int minPlayerSize,
                           int maxPlayerSize,
                           LinkedHashMap<String, ShopItemInfo> shops,
                           ArrayList<TeamInfoConfig> teamConfigs){
        this.name = name;
        this.worldInfo = worldInfo;
        this.time = time;
        this.waitTime = waitTime;
        this.maxWaitTime = maxWaitTime;
        this.minPlayerSize = minPlayerSize;
        this.maxPlayerSize = maxPlayerSize;
        this.shops = shops;
        this.teamConfigs = teamConfigs;

    }



    public List<String> getTeamNames(){
        List<String> names = new ArrayList<String>();
        for (TeamConfig teamConfig : teamCfg) {
            names.add(teamConfig.getName());

        }
        return names;
    }


    public static GameRoomConfig createGameRoom(String name, int size, int maxSize){
        GameRoomConfig roomConfig = new GameRoomConfig(name,null,300,120,20,size,maxSize,new LinkedHashMap<>(),new ArrayList<>());
        File sdir = new File(BedWarMain.getBedWarMain().getDataFolder()+"/rooms/"+name+"/shop");
        if(!sdir.mkdirs()){
            BedWarMain.sendMessageToConsole("创建文件夹 shop 失败");
        }

        BedWarMain.getBedWarMain().saveResource("shop/"+BedWarMain.getLanguage().lang+"/shopClassify.yml","/rooms/"+name+ "/shop/"+BedWarMain.getLanguage().lang+"/shopClassify.yml",false);
        BedWarMain.getBedWarMain().saveResource("shop/"+BedWarMain.getLanguage().lang+"/defaultShop.yml","/rooms/"+name+ "/shop/"+BedWarMain.getLanguage().lang+"/defaultShop.yml",false);
        BedWarMain.getBedWarMain().saveResource("shop/"+BedWarMain.getLanguage().lang+"/teamShop.yml","/rooms/"+name+ "/shop/"+BedWarMain.getLanguage().lang+"/teamShop.yml",false);
        BedWarMain.getBedWarMain().saveResource("items/"+BedWarMain.getLanguage().lang+"/item.yml","/rooms/"+name+"/"+BedWarMain.getLanguage().lang+"/item.yml",false);
        BedWarMain.getBedWarMain().saveResource("team/"+BedWarMain.getLanguage().lang+"/team.yml","/rooms/"+name+"/"+BedWarMain.getLanguage().lang+"/team.yml",false);
        BedWarMain.getBedWarMain().saveResource("event.yml","/rooms/"+name+"/event.yml",false);
        BedWarMain.getBedWarMain().saveResource("event/"+BedWarMain.getLanguage().lang+"/roomEventList.yml","/rooms/"+name+"/"+BedWarMain.getLanguage().lang+"/roomEventList.yml",false);
        BedWarMain.getBedWarMain().saveResource("readme.txt","/rooms/"+name+"/readme.txt",false);
        loadTeamShopConfig(roomConfig);
        return roomConfig;

    }

    public static void loadTeamShopConfig(GameRoomConfig roomConfig){

        Config team = new Config(BedWarMain.getBedWarMain().getDataFolder()+"/rooms/"+roomConfig.name+"/"+BedWarMain.getLanguage().lang+"/team.yml",Config.YAML);
        List<TeamConfig> teamConfigs = new LinkedList<>();
        for(Map<?,?> map : team.getMapList("team")){
            TeamConfig teamConfig = TeamConfig.getInstance(map);
            teamConfigs.add(teamConfig);
        }
        roomConfig.setTeamCfg(teamConfigs);
        Config item = new Config(BedWarMain.getBedWarMain().getDataFolder()+"/rooms/"+roomConfig.name+"/"+BedWarMain.getLanguage().lang+"/item.yml",Config.YAML);
        MoneyItemInfo itemInfo = MoneyItemInfo.getMoneyItemInfoByFile(item);
        roomConfig.setNbtItemInfo(NbtItemInfo.getNbtItemInfoByFile(item));

        roomConfig.setMoneyItem(itemInfo);
    }

    public boolean notHasFloatText(String name){
        for(FloatTextInfoConfig config: floatTextInfoConfigs){
            if(config.name.equalsIgnoreCase(name)){
                return false;
            }
        }
        return true;
    }

    public void removeFloatText(String name){
        floatTextInfoConfigs.removeIf(config -> config.name.equalsIgnoreCase(name));
    }




    public static GameRoomConfig getGameRoomConfigByFile(String name, File file){
        if(file.isDirectory()){
            try {
                if(!new File(file+"/"+BedWarMain.getLanguage().lang+"/team.yml").exists()){
                    BedWarMain.getBedWarMain().saveResource("team/"+BedWarMain.getLanguage().lang+"/team.yml","/rooms/"+name+"/"+BedWarMain.getLanguage().lang+"/team.yml",false);

                }
                Config team = new Config(file+"/"+BedWarMain.getLanguage().lang+"/team.yml",Config.YAML);
                List<TeamConfig> teamConfigs = new LinkedList<>();
                for(Map<?,?> map : team.getMapList("team")){
                    TeamConfig teamConfig = TeamConfig.getInstance(map);
                    teamConfigs.add(teamConfig);
                }
                if(!new File(file+"/"+BedWarMain.getLanguage().lang+"/item.yml").exists()){
                    BedWarMain.getBedWarMain().saveResource("items/"+BedWarMain.getLanguage().lang+"/item.yml","/rooms/"+name+"/"+BedWarMain.getLanguage().lang+"/item.yml",false);

                }
                Config item = new Config(file+"/"+BedWarMain.getLanguage().lang+"/item.yml",Config.YAML);
                MoneyItemInfo itemInfo = MoneyItemInfo.getMoneyItemInfoByFile(item);
                NbtItemInfo nbtItemInfo = NbtItemInfo.getNbtItemInfoByFile(item);
                if(!new File(file+"/room.yml").exists()){
                    BedWarMain.sendMessageToConsole("&eUnfinished room template detected");
                    Utils.toDelete(file);
                    BedWarMain.sendMessageToConsole("&aSuccessfully cleared unfinished room templates");
                    return null;
                }
                Config room = new Config(file+"/room.yml",Config.YAML);
                WorldInfoConfig worldInfoConfig = WorldInfoConfig.getInstance(name,itemInfo,room);
                if(worldInfoConfig == null){
                    BedWarMain.sendMessageToConsole("&cUnsuccessfully loaded game map for "+name);
                    return null;
                }

                int time = room.getInt("gameTime");
                int waitTime = room.getInt("waitTime");
                int maxWaitTime = room.getInt("max-player-waitTime");
                int minPlayerSize = room.getInt("minPlayerSize");
                int maxPlayerSize =  room.getInt("maxPlayerSize");
                ArrayList<TeamInfoConfig> teamInfoConfigs = new ArrayList<>();
                for(int i = 0;i < Math.min(teamConfigs.size(),room.getMapList("teamSpawn").size());i++) {
                    teamInfoConfigs.add(
                            TeamInfoConfig.getInfoByMap(teamConfigs.get(i) ,room.getMapList("teamSpawn").get(i)));
                }

                File shopDir = new File(file+"/shop");
                if(!shopDir.exists()){
                    if(!shopDir.mkdirs()){
                        throw new RuntimeException("Unable to create shop folder");
                    }
                }
                if(shopDir.isDirectory()){
                    if(!new File(file+ "/shop/"+BedWarMain.getLanguage().lang+"/defaultShop.yml").exists()){
                        BedWarMain.getBedWarMain().saveResource("shop/"+BedWarMain.getLanguage().lang+"/defaultShop.yml","/rooms/"+name+ "/shop/"+BedWarMain.getLanguage().lang+"/defaultShop.yml",false);

                    }
                    if(!new File(file+ "/shop/"+BedWarMain.getLanguage().lang+"/teamShop.yml").exists()) {
                        BedWarMain.getBedWarMain().saveResource("shop/"+BedWarMain.getLanguage().lang+"/teamShop.yml", "/rooms/" + name + "/shop/"+BedWarMain.getLanguage().lang+"/teamShop.yml", false);
                    }
                    if(!new File(file+ "/shop/"+BedWarMain.getLanguage().lang+"/shopClassify.yml").exists()) {
                        BedWarMain.getBedWarMain().saveResource("shop/"+BedWarMain.getLanguage().lang+"/shopClassify.yml", "/rooms/" + name + "/shop/"+BedWarMain.getLanguage().lang+"/shopClassify.yml", false);
                    }
                }
                if(!new File(file+"/event.yml").exists()){
                    BedWarMain.getBedWarMain().saveResource("event.yml","/rooms/"+name+"/event.yml",false);
                }
                if(!new File(file+"/"+BedWarMain.getLanguage().lang+"/roomEventList.yml").exists()){
                    BedWarMain.getBedWarMain().saveResource("event/"+BedWarMain.getLanguage().lang+"/roomEventList.yml","/rooms/"+name+"/"+BedWarMain.getLanguage().lang+"/roomEventList.yml",false);
                }
                //TODO 实现商店
                //先加载配置项

                Config classShop = new Config(shopDir+"/"+BedWarMain.getLanguage().lang+"/shopClassify.yml",Config.YAML);
                List<ShopInfoConfig.ShopItemClassify> shopItemClassifies = new ArrayList<>();
                for(Map.Entry<String,Object> entry: classShop.getAll().entrySet()){
                    Object obj = entry.getValue();
                    if(obj instanceof Map){
                        Map<?,?> omap = (Map<?,?>) obj;
                        shopItemClassifies.add(new ShopInfoConfig.ShopItemClassify(entry.getKey(),
                                Utils.formatItemByString(omap.get("item").toString()),
                                omap.get("name").toString()

                        ));
                    }

                }


                LinkedHashMap<String, ShopItemInfo> shopMap = new LinkedHashMap<>();
                shopMap.put("defaultShop",ShopItemInfo.build(shopItemClassifies,"defaultShop",new Config(shopDir+"/"+BedWarMain.getLanguage().lang+"/defaultShop.yml",Config.YAML)));
                shopMap.put("teamShop",ShopItemInfo.build(null,"teamShop",new Config(shopDir+"/"+BedWarMain.getLanguage().lang+"/teamShop.yml",Config.YAML)));
                BedWarMain.sendMessageToConsole("defaultShop load OK");
                GameRoomConfig roomConfig = new GameRoomConfig(name,worldInfoConfig,time,waitTime,maxWaitTime,minPlayerSize,maxPlayerSize,shopMap,teamInfoConfigs);
                roomConfig.setTeamCfg(teamConfigs);
                roomConfig.setMoneyItem(itemInfo);
                roomConfig.knockConfig.enable = room.getBoolean("kb-setting.enable",false);
                roomConfig.knockConfig.force = (float) room.getDouble("kb-setting.force",0.4f);
                roomConfig.knockConfig.speed = (float) room.getDouble("kb-setting.speed",0.5f);
                roomConfig.knockConfig.motionY = (float) room.getDouble("kb-setting.motionY",0.1f);
                roomConfig.shopItemClassifies = shopItemClassifies;
                roomConfig.gameRoomMoney = room.getString("roomMoney","default");
                roomConfig.setNbtItemInfo(nbtItemInfo);
                roomConfig.hasWatch = room.getBoolean("hasWatch",true);
                roomConfig.killItem = (float) room.getDouble("killItem",0.5f);
                roomConfig.uiType = Utils.loadUiTypeByName(room.getString("ui","auto"));
                roomConfig.teamShopEntityId = room.getInt("entity.team",15);
                roomConfig.itemShopEntityId = room.getInt("entity.item",15);
                roomConfig.callbackY = room.getInt("callbackY",17);
                roomConfig.inventoryUnBreakable = room.getBoolean("inventory-unbreakable",true);
                roomConfig.fireballKnockBack = (float) room.getDouble("fireballKnockBack",0.6f);
                roomConfig.tntKnockBack = (float) room.getDouble("tntKnockBack",0.6f);
                roomConfig.banCommand = new ArrayList<>(room.getStringList("ban-command"));
                roomConfig.isAutomaticNextRound = room.getBoolean("AutomaticNextRound",true);
                roomConfig.quitRoomCommand = new ArrayList<>(room.getStringList("QuitRoom"));
                roomConfig.victoryCommand = new ArrayList<>(room.getStringList("victoryCmd"));
                roomConfig.defeatCommand = new ArrayList<>(room.getStringList("defeatCmd"));
                roomConfig.minutesExp = room.getInt("minutesExp",25);
                roomConfig.enableItemEqual = room.getBoolean("enable-item-equal",false);
                roomConfig.displayItemName = room.getBoolean("display-item-name",false);
                roomConfig.deathIcon = room.getInt("deathIcon",20);
                roomConfig.enableFood = room.getBoolean("enable-food",false);
                roomConfig.fastPlace = room.getBoolean("fast-place",false);
                roomConfig.fastPlaceCount = room.getInt("fast-place-count",5);
                roomConfig.tntDamage = room.getInt("tntDamage",8);
                roomConfig.tntExplodeTime = room.getInt("tntExplodeTime",5);
                roomConfig.enableCustomTag = room.getBoolean("custom.namedtag.enable",true);
                roomConfig.customNamedTag = room.getString("custom.namedtag.text","&7[{team}&7] {color}{name} \n&c❤&7 {health}");
                LinkedHashMap<String,String> floatBlocks = new LinkedHashMap<>();
                roomConfig.enableAutoDisplayFloat = room.getBoolean("auto-display-floattext.enable",true);
                roomConfig.autoDisplayFloat = room.getString("auto-display-floattext.text","{item} &r生成点\n剩余&a {time} &r秒");
                List<FloatTextInfoConfig> configs = new ArrayList<>();
                //初始化浮空字方块
                Object omap = room.get("display-floatBlock");
                if(omap instanceof Map){
                    BedWarMain.sendMessageToConsole("&eLoading float blocks info");
                    Map<?,?> map = (Map<?,?>)omap;
                    for(Map.Entry<?,?> entry : map.entrySet()){
                        BedWarMain.sendMessageToConsole("&eLoading "+entry.getKey().toString()+" float block");
                        if(itemInfo.containsKey(entry.getKey().toString()) && SkinManager.SKINS.containsKey(entry.getValue().toString().toLowerCase())){
//                            floatBlocks.put(entry.getKey().toString(), entry.getValue().toString().toLowerCase());
                            int iv = itemInfo.indexOf(entry.getKey().toString());
                            floatBlocks.put(iv+"", entry.getValue().toString().toLowerCase());

                            BedWarMain.sendMessageToConsole("&eWrite "+entry.getKey().toString()+" float block..");
                        }else{
                            if(SkinManager.SKINS.containsKey(entry.getValue().toString().toLowerCase())){
                                try{
                                    int id = Integer.parseInt(entry.getKey().toString());
                                    floatBlocks.put(id+"", entry.getValue().toString().toLowerCase());
                                    BedWarMain.sendMessageToConsole("&eWrite "+entry.getKey().toString()+" float block..");
                                }catch (Exception ignore){
                                    BedWarMain.sendMessageToConsole("&cRead "+entry.getKey().toString()+" float block！");
                                }
                            }else{
                                BedWarMain.sendMessageToConsole("&cRead "+entry.getKey().toString()+" float block！");
                            }



                        }
                    }
                    if(floatBlocks.size() == 0){
                        BedWarMain.sendMessageToConsole("&cNo float block information was read");
                    }




                }
                roomConfig.floatBlockConfig = floatBlocks;

                if(room.exists("floatSpawnPos")){
                    for(Map<?,?> map: room.getMapList("floatSpawnPos")){
                        FloatTextInfoConfig config = FloatTextInfoConfig.build(map);
                        if(config != null){
                            configs.add(config);
                        }
                    }
                    roomConfig.floatTextInfoConfigs = configs;
                }
                if(room.exists("roomStartMessage")){
                    roomConfig.gameStartMessage = new ArrayList<>(room.getStringList("roomStartMessage"));
                }else{
                    roomConfig.gameStartMessage = defaultGameStartMessage();
                }
                BedWarMain.sendMessageToConsole("&eLoading Room Events");
                roomConfig.eventConfig = GameRoomEventConfig.getGameRoomEventConfigByFile(new File(file+"/event.yml"));
                BedWarMain.sendMessageToConsole("&eLoading Room spare Events");
                roomConfig.eventListConfig = GameRoomEventConfig.getGameRoomEventConfigByFile(new File(file+"/"+BedWarMain.getLanguage().lang+"/roomEventList.yml"));
                return roomConfig;
            }catch (Exception e){
                BedWarMain.printMessageException(e);

                return null;

            }

        }
        return null;
    }


    public boolean isExp(){
        return "exp".equalsIgnoreCase(gameRoomMoney);
    }

    public static ArrayList<String> defaultGameStartMessage(){
        ArrayList<String> strings = new ArrayList<>();

        strings.add("&a■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
        strings.add("&f起床战争");
        strings.add("&e");
        strings.add("&e保护你的床并摧毁敌人的床。收集铜锭，金锭，钻石和绿宝石");
        strings.add("&e来升级，使自身和队伍变得更强");
        strings.add("&a■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
        return strings;
    }

    public void save(){

        Config config = new Config(BedWarMain.getBedWarMain().getDataFolder()+"/rooms/"+getName()+"/room.yml",Config.YAML);
        config.set("world",worldInfo.getLevel());
        config.set("roomMoney", gameRoomMoney);
        config.set("gameTime",time);
        config.set("enable-food",enableFood);
        config.set("callbackY",callbackY);
        config.set("entity.team",teamShopEntityId);
        config.set("entity.item",itemShopEntityId);
        config.set("waitTime",waitTime);
        config.set("max-player-waitTime",maxWaitTime);
        config.set("minPlayerSize",minPlayerSize);
        config.set("maxPlayerSize",maxPlayerSize);
        ArrayList<LinkedHashMap<String, Object>> teamSpawn = new ArrayList<>();
        for(TeamInfoConfig infoConfig: teamConfigs){
            teamSpawn.add(infoConfig.save());
        }
        config.set("teamSpawn",teamSpawn);
        List<Object> itemSpawn = new ArrayList<>();
//        LinkedHashMap<String, Object> itemSpawn = new LinkedHashMap<>();
        for(ItemInfoConfig itemInfoConfig: worldInfo.getItemInfos()){
            itemSpawn.add(itemInfoConfig.save());
//            itemSpawn.put(itemInfoConfig.getMoneyItemInfoConfig().getName(),itemInfoConfig.save());
        }
        config.set("fast-place",fastPlace);
        config.set("fast-place-count",fastPlaceCount);
        config.set("itemSpawn",itemSpawn);
        config.set("enable-item-Equal",enableItemEqual);
        config.set("display-item-name",displayItemName);
        config.set("waitPosition",WorldInfoConfig.positionToString(worldInfo.getWaitPosition()));
        config.set("ban-command",banCommand);
        config.set("QuitRoom",quitRoomCommand);
        if(uiType != null){
            config.set("ui",uiType.name().toLowerCase());
        }
        config.set("fireballKnockBack", fireballKnockBack);
        config.set("tntKnockBack", tntKnockBack);
        config.set("hasWatch", hasWatch);
        config.set("AutomaticNextRound",isAutomaticNextRound);
        config.set("defeatCmd",defeatCommand);
        config.set("victoryCmd",victoryCommand);
        config.set("minutesExp",minutesExp);
        config.set("deathIcon",deathIcon);
        config.set("roomStartMessage",gameStartMessage);
        config.set("custom.namedtag.enable",enableCustomTag);
        config.set("custom.namedtag.text",customNamedTag);
        List<Map<String,Object>> pos = new ArrayList<>();
        for(FloatTextInfoConfig floatTextInfoConfig: floatTextInfoConfigs){
            pos.add(floatTextInfoConfig.toConfig());
        }
        config.set("display-floatBlock",floatBlockConfig);
        config.set("floatSpawnPos",pos);
        config.set("auto-display-floattext.enable",enableAutoDisplayFloat);
        config.set("auto-display-floattext.text",autoDisplayFloat);
        config.set("kb-setting",knockConfig.saveConfig());
        

        config.save();
        //写入注释...
        File roomFile = new File(BedWarMain.getBedWarMain().getDataFolder()+"/rooms/"+getName()+"/room.yml");
        Utils.addDescription(roomFile,BedWarMain.getLanguage().roomDescription,true);


    }

    @Override
    public GameRoomConfig clone() {
        try {
            return (GameRoomConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            BedWarMain.printMessageException(e);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof GameRoomConfig){
            return name.equalsIgnoreCase(((GameRoomConfig) obj).name);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
