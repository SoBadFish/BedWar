package org.sobadfish.bedwar.room.config;


import cn.nukkit.utils.Config;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.item.MoneyItemInfo;
import org.sobadfish.bedwar.item.NbtItemInfo;
import org.sobadfish.bedwar.item.config.ItemInfoConfig;
import org.sobadfish.bedwar.manager.RoomEventManager;
import org.sobadfish.bedwar.player.team.config.TeamConfig;
import org.sobadfish.bedwar.player.team.config.TeamInfoConfig;
import org.sobadfish.bedwar.room.floattext.FloatTextInfoConfig;
import org.sobadfish.bedwar.shop.item.ShopItemInfo;
import org.sobadfish.bedwar.tools.Utils;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author SoBadFish
 * 2022/1/2
 */
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
    public LinkedHashMap<String,TeamConfig> teamCfg = new LinkedHashMap<>();

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
     * 获取击杀玩家的物品 (50%)
     * */
    public float killItem = 0.5f;

    /**
     * 是否允许旁观
     * */
    public boolean hasWatch = true;


    /**
     * 等待大厅拉回坐标
     * */
    public int callbackY = 17;




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

    public static GameRoomConfig createGameRoom(String name,int size,int maxSize){
        GameRoomConfig roomConfig = new GameRoomConfig(name,null,300,120,20,size,maxSize,new LinkedHashMap<>(),new ArrayList<>());
        File sdir = new File(BedWarMain.getBedWarMain().getDataFolder()+"/rooms/"+name+"/shop");
        if(!sdir.mkdirs()){
            BedWarMain.sendMessageToConsole("创建文件夹 shop 失败");
        }

        BedWarMain.getBedWarMain().saveResource("shop/defaultShop.yml","/rooms/"+name+"/shop/defaultShop.yml",false);
        BedWarMain.getBedWarMain().saveResource("shop/teamShop.yml","/rooms/"+name+"/shop/teamShop.yml",false);
        BedWarMain.getBedWarMain().saveResource("item.yml","/rooms/"+name+"/item.yml",false);
        BedWarMain.getBedWarMain().saveResource("team.yml","/rooms/"+name+"/team.yml",false);
        BedWarMain.getBedWarMain().saveResource("event.yml","/rooms/"+name+"/event.yml",false);
        BedWarMain.getBedWarMain().saveResource("roomEventList.yml","/rooms/"+name+"/roomEventList.yml",false);
        BedWarMain.getBedWarMain().saveResource("readme.txt","/rooms/"+name+"/readme.txt",false);
        loadTeamShopConfig(roomConfig);
        return roomConfig;

    }

    public static void loadTeamShopConfig(GameRoomConfig roomConfig){
        Config team = new Config(BedWarMain.getBedWarMain().getDataFolder()+"/rooms/"+roomConfig.name+"/team.yml",Config.YAML);
        LinkedHashMap<String,TeamConfig> teamConfigs = new LinkedHashMap<>();
        for(Map map : team.getMapList("team")){
            TeamConfig teamConfig = TeamConfig.getInstance(map);
            teamConfigs.put(teamConfig.getName(),teamConfig);
        }
        roomConfig.setTeamCfg(teamConfigs);
        Config item = new Config(BedWarMain.getBedWarMain().getDataFolder()+"/rooms/"+roomConfig.name+"/item.yml",Config.YAML);
        MoneyItemInfo itemInfo = MoneyItemInfo.getMoneyItemInfoByFile(item);
        roomConfig.setNbtItemInfo(NbtItemInfo.getNbtItemInfoByFile(item));

        roomConfig.setMoneyItem(itemInfo);
    }

    public boolean hasFloatText(String name){
        for(FloatTextInfoConfig config: floatTextInfoConfigs){
            if(config.name.equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    public void removeFloatText(String name){
        floatTextInfoConfigs.removeIf(config -> config.name.equalsIgnoreCase(name));
    }

    public void setNbtItemInfo(NbtItemInfo nbtItemInfo) {
        this.nbtItemInfo = nbtItemInfo;
    }

    public NbtItemInfo getNbtItemInfo() {
        return nbtItemInfo;
    }

    public String getName() {
        return name;
    }

    public int getTime() {
        return time;
    }

    public int getWaitTime() {
        return waitTime;
    }

    public ArrayList<TeamInfoConfig> getTeamConfigs() {
        return teamConfigs;
    }

    public int getMaxPlayerSize() {
        return maxPlayerSize;
    }

    public int getMaxWaitTime() {
        return maxWaitTime;
    }

    public int getMinPlayerSize() {
        return minPlayerSize;
    }

    public LinkedHashMap<String, ShopItemInfo> getShops() {
        return shops;
    }

    public WorldInfoConfig getWorldInfo() {
        return worldInfo;
    }


    public void setTime(int time) {
        this.time = time;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWaitTime(int waitTime) {
        this.waitTime = waitTime;
    }

    public void setMaxPlayerSize(int maxPlayerSize) {
        this.maxPlayerSize = maxPlayerSize;
    }

    public void setMaxWaitTime(int maxWaitTime) {
        this.maxWaitTime = maxWaitTime;
    }

    public void setMinPlayerSize(int minPlayerSize) {
        this.minPlayerSize = minPlayerSize;
    }

    public void setShops(LinkedHashMap<String, ShopItemInfo> shops) {
        this.shops = shops;
    }

    public void setTeamConfigs(ArrayList<TeamInfoConfig> teamConfigs) {
        this.teamConfigs = teamConfigs;
    }

    public void setWorldInfo(WorldInfoConfig worldInfo) {
        this.worldInfo = worldInfo;
    }

    public void setTeamCfg(LinkedHashMap<String, TeamConfig> teamCfg) {
        this.teamCfg = teamCfg;
    }

    public LinkedHashMap<String, TeamConfig> getTeamCfg() {
        return teamCfg;
    }

    public static GameRoomConfig getGameRoomConfigByFile(String name, File file){
        if(file.isDirectory()){
            try {
                Config team = new Config(file+"/team.yml",Config.YAML);
                LinkedHashMap<String,TeamConfig> teamConfigs = new LinkedHashMap<>();
                for(Map map : team.getMapList("team")){
                    TeamConfig teamConfig = TeamConfig.getInstance(map);
                    teamConfigs.put(teamConfig.getName(),teamConfig);
                }
                if(!new File(file+"/item.yml").exists()){
                    BedWarMain.getBedWarMain().saveResource("item.yml","/rooms/"+name+"/item.yml",false);

                }
                Config item = new Config(file+"/item.yml",Config.YAML);
                MoneyItemInfo itemInfo = MoneyItemInfo.getMoneyItemInfoByFile(item);
                NbtItemInfo nbtItemInfo = NbtItemInfo.getNbtItemInfoByFile(item);
                if(!new File(file+"/room.yml").exists()){
                    BedWarMain.sendMessageToConsole("&e检测到未完成房间模板");
                    Utils.toDelete(file);
                    BedWarMain.sendMessageToConsole("&a成功清除未完成的房间模板");
                    return null;
                }
                Config room = new Config(file+"/room.yml",Config.YAML);
                WorldInfoConfig worldInfoConfig = WorldInfoConfig.getInstance(name,itemInfo,room);
                if(worldInfoConfig == null){
                    BedWarMain.sendMessageToConsole("&c未成功加载 &a"+name+"&c 的游戏地图");
                    return null;
                }

                int time = room.getInt("gameTime");
                int waitTime = room.getInt("waitTime");
                int maxWaitTime = room.getInt("max-player-waitTime");
                int minPlayerSize = room.getInt("minPlayerSize");
                int maxPlayerSize =  room.getInt("maxPlayerSize");
                ArrayList<TeamInfoConfig> teamInfoConfigs = new ArrayList<>();
                for(Map map: room.getMapList("teamSpawn")){
                    teamInfoConfigs.add(TeamInfoConfig.getInfoByMap(
                            teamConfigs.get(map.get("name").toString()),map));
                }
                File shopDir = new File(file+"/shop");
                if(shopDir.isDirectory()){
                    if(!new File(file+"/shop/defaultShop.yml").exists()){
                        BedWarMain.getBedWarMain().saveResource("shop/defaultShop.yml","/rooms/"+name+"/shop/defaultShop.yml",false);

                    }
                    if(!new File(file+"/shop/teamShop.yml").exists()) {
                        BedWarMain.getBedWarMain().saveResource("shop/teamShop.yml", "/rooms/" + name + "/shop/teamShop.yml", false);
                    }
                }
                if(!new File(file+"/event.yml").exists()){
                    BedWarMain.getBedWarMain().saveResource("event.yml","/rooms/"+name+"/event.yml",false);
                }
                if(!new File(file+"/roomEventList.yml").exists()){
                    BedWarMain.getBedWarMain().saveResource("roomEventList.yml","/rooms/"+name+"/roomEventList.yml",false);
                }
                //TODO 实现商店
                LinkedHashMap<String, ShopItemInfo> shopMap = new LinkedHashMap<>();
                shopMap.put("defaultShop",ShopItemInfo.build("defaultShop",new Config(shopDir+"/defaultShop.yml",Config.YAML)));
                shopMap.put("teamShop",ShopItemInfo.build("teamShop",new Config(shopDir+"/teamShop.yml",Config.YAML)));
                BedWarMain.sendMessageToConsole("defaultShop 加载完成");
                GameRoomConfig roomConfig = new GameRoomConfig(name,worldInfoConfig,time,waitTime,maxWaitTime,minPlayerSize,maxPlayerSize,shopMap,teamInfoConfigs);
                roomConfig.setTeamCfg(teamConfigs);
                roomConfig.setMoneyItem(itemInfo);
                roomConfig.gameRoomMoney = room.getString("roomMoney","default");
                roomConfig.setNbtItemInfo(nbtItemInfo);
                roomConfig.hasWatch = room.getBoolean("hasWatch",true);
                roomConfig.killItem = (float) room.getDouble("killItem",0.5f);
                roomConfig.uiType = Utils.loadUiTypeByName(room.getString("ui","auto"));
                roomConfig.teamShopEntityId = room.getInt("entity.team",15);
                roomConfig.itemShopEntityId = room.getInt("entity.item",15);
                roomConfig.callbackY = room.getInt("callbackY",17);
                roomConfig.fireballKnockBack = (float) room.getDouble("fireballKnockBack",0.6f);
                roomConfig.banCommand = new ArrayList<>(room.getStringList("ban-command"));
                roomConfig.isAutomaticNextRound = room.getBoolean("AutomaticNextRound",true);
                roomConfig.quitRoomCommand = new ArrayList<>(room.getStringList("QuitRoom"));
                roomConfig.victoryCommand = new ArrayList<>(room.getStringList("victoryCmd"));
                roomConfig.defeatCommand = new ArrayList<>(room.getStringList("defeatCmd"));
                List<FloatTextInfoConfig> configs = new ArrayList<>();
                if(room.exists("floatSpawnPos")){
                    for(Map map: room.getMapList("floatSpawnPos")){
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
                BedWarMain.sendMessageToConsole("&e开始加载 房间主事件");
                roomConfig.eventConfig = GameRoomEventConfig.getGameRoomEventConfigByFile(new File(file+"/event.yml"));
                BedWarMain.sendMessageToConsole("&e开始加载 房间备选事件");
                roomConfig.eventListConfig = GameRoomEventConfig.getGameRoomEventConfigByFile(new File(file+"/roomEventList.yml"));
                return roomConfig;
            }catch (Exception e){
                BedWarMain.sendMessageToConsole("加载房间出错: "+e.getMessage());

                return null;

            }

        }
        return null;
    }

    public void setMoneyItem(MoneyItemInfo moneyItem) {
        this.moneyItem = moneyItem;
    }

    public boolean isExp(){
        return gameRoomMoney.equalsIgnoreCase("exp");
    }

    public static ArrayList<String> defaultGameStartMessage(){
        ArrayList<String> strings = new ArrayList<>();

        strings.add("&a■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
        strings.add("&f起床战争");
        strings.add("&e");
        strings.add("&e保护你的床并摧毁敌人的床。收集铜锭，金锭，钻石和绿宝石");
        strings.add("&e来升级，使自身和队伍变得更强");
        strings.add("&a■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■");
        return strings;
    }

    public void save(){

        Config config = new Config(BedWarMain.getBedWarMain().getDataFolder()+"/rooms/"+getName()+"/room.yml",Config.YAML);
        config.set("world",worldInfo.getGameWorld().getFolderName());
        config.set("roomMoney", gameRoomMoney);
        config.set("gameTime",time);

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
        LinkedHashMap<String, Object> itemSpawn = new LinkedHashMap<>();
        for(ItemInfoConfig itemInfoConfig: worldInfo.getItemInfos()){
            itemSpawn.put(itemInfoConfig.getMoneyItemInfoConfig().getName(),itemInfoConfig.save());
        }
        config.set("itemSpawn",itemSpawn);
        config.set("waitPosition",WorldInfoConfig.positionToString(worldInfo.getWaitPosition()));
        config.set("ban-command",banCommand);
        config.set("QuitRoom",quitRoomCommand);
        config.set("fireballKnockBack", fireballKnockBack);
        config.set("hasWatch", hasWatch);
        config.set("AutomaticNextRound",isAutomaticNextRound);
        config.set("victoryCmd",victoryCommand);
        config.set("defeatCmd",defeatCommand);
        config.set("roomStartMessage",gameStartMessage);
        List<Map<String,Object>> pos = new ArrayList<>();
        for(FloatTextInfoConfig floatTextInfoConfig: floatTextInfoConfigs){
            pos.add(floatTextInfoConfig.toConfig());
        }
        config.set("floatSpawnPos",pos);
        config.save();
    }

    @Override
    public GameRoomConfig clone() {
        try {
            return (GameRoomConfig) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
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
}
