package org.sobadfish.bedwar;


import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import lombok.Getter;
import org.sobadfish.bedwar.command.BedWarAdminCommand;
import org.sobadfish.bedwar.command.BedWarCommand;
import org.sobadfish.bedwar.command.BedWarSpeakCommand;
import org.sobadfish.bedwar.entity.BridgingEggEntity;
import org.sobadfish.bedwar.entity.EntityBlueWitherSkull;
import org.sobadfish.bedwar.entity.EntityFireBall;
import org.sobadfish.bedwar.entity.IronGolem;
import org.sobadfish.bedwar.manager.*;
import org.sobadfish.bedwar.manager.data.PlayerDataManager;
import org.sobadfish.bedwar.manager.data.PlayerTopManager;
import org.sobadfish.bedwar.panel.lib.AbstractFakeInventory;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.room.event.*;
import org.sobadfish.bedwar.tools.Utils;
import org.sobadfish.bedwar.variable.BedWarVariable;
import org.sobadfish.bedwar.variable.TipVariable;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;


/**
 *   ____           ___          __
 *  |  _ \         | \ \        / /
 *  | |_) | ___  __| |\ \  /\  / /_ _ _ __
 *  |  _ < / _ \/ _` | \ \/  \/ / _` | '__|
 *  | |_) |  __/ (_| |  \  /\  / (_| | |
 *  |____/ \___|\__,_|   \/  \/ \__,_|_|
 * @author SoBadFish
 */
public class BedWarMain extends PluginBase {

    private static final List<String> LANGUAGE_LIST = new LinkedList<>();
    static {
        LANGUAGE_LIST.add("eng");
        LANGUAGE_LIST.add("chs");
    }

    private static BedWarMain bedWarMain;

    private static RoomManager roomManager;

    private static MenuRoomManager menuRoomManager;

    private static PlayerDataManager dataManager;

    private static PlayerTopManager topManager;

    @Getter
    private static RecordManager recordManager;

    public static LanguageManager language;

    public static UiType uiType;

    /**
     * 游戏指令
     * */
    public static String COMMAND_NAME = "bw";

    /**
     * 游戏管理员指令
     * */
    public static String COMMAND_ADMIN_NAME = "bd";

    /**
     * 游戏内喊话指令
     * */
    public static String COMMAND_MESSAGE_NAME = "bws";

    public static int upExp;
    public static boolean enableRecord;

    @Override
    public void onEnable() {
        bedWarMain = this;
        //  TODO 初始化文件
        //
        saveDefaultConfig();
        reloadConfig();
        initLanguage(this,getConfig().getString("language","chs"));



        checkServer();
        this.getLogger().info(TextFormat.colorize('&',"&b   ____           ___          __"));
        this.getLogger().info(TextFormat.colorize('&',"&b  |  _ \\         | \\ \\        / /"));
        this.getLogger().info(TextFormat.colorize('&',"&b  | |_) | ___  __| |\\ \\  /\\  / /_ _ _ __"));
        this.getLogger().info(TextFormat.colorize('&',"&b  |  _ < / _ \\/ _` | \\ \\/  \\/ / _` | '__|"));
        this.getLogger().info(TextFormat.colorize('&',"&b  | |_) |  __/ (_| |  \\  /\\  / (_| | |"));
        this.getLogger().info(TextFormat.colorize('&',"&b  |____/ \\___|\\__,_|   \\/  \\/ \\__,_|_|"));
        this.getLogger().info(TextFormat.colorize('&',"&b"));
        this.getLogger().info(TextFormat.colorize('&',getLanguage().getLanguage("version","&e正在加载[1] 插件 本版本为&av[2]"
                ,"BedWar",this.getDescription().getVersion())));
        this.getLogger().info(TextFormat.colorize('&',"&eAuthor:&b sobadfish &aQQ：&e1586235767"));
//        this.getLogger().info(TextFormat.colorize('&',"&c本插件为原创插件 部分源代码出处已标明原作者"));
//        "&e正在加载BedWar 起床战争插件 版本: &av"+this.getDescription().getVersion())
        sendMessageToConsole("&aChunk plugin depends");

        for(String s : this.getDescription().getSoftDepend()){
            Plugin plugin = getServer().getPluginManager().getPlugin(s);
            if(plugin == null){
                sendMessageToConsole("&c"+s+" Unload");
                continue;
            }
            try{
                Class.forName("updata.AutoData");
                updata.AutoData.defaultUpData(this, this.getFile(), "Sobadfish", "BedWar");

            }catch (Exception ignore){}

            try{
                Class.forName("com.smallaswater.npc.variable.BaseVariableV2");
                BedWarVariable.init();
            }catch (Exception ignore){}
            try{
                Class.forName("tip.utils.variables.BaseVariable");
                TipVariable.init();
            }catch (Exception ignore){}
            try{
                Class.forName("net.easecation.ghosty.GhostyPlugin");
                recordManager = new RecordManager();

            }catch (Exception ignore){}



        }
        Entity.registerEntity("FireBall", EntityFireBall.class);
        Entity.registerEntity("BridgingEgg", BridgingEggEntity.class);
        Entity.registerEntity(EntityBlueWitherSkull.class.getSimpleName(), EntityBlueWitherSkull.class);
        Entity.registerEntity(IronGolem.class.getSimpleName(), IronGolem.class);
        initSkin();
        loadBedWarConfig();

        //TODO 注册指令
        this.getServer().getCommandMap().register("badwar",new BedWarAdminCommand(COMMAND_ADMIN_NAME));
        this.getServer().getCommandMap().register("badwar",new BedWarCommand(COMMAND_NAME));
        this.getServer().getCommandMap().register("badwar",new BedWarSpeakCommand(COMMAND_MESSAGE_NAME));


        RoomEventManager.register("time", TimeEvent.class);
        RoomEventManager.register("break", BreakEvent.class);
        RoomEventManager.register("custom", CustomEvent.class);
        RoomEventManager.register("effect", EffectEvent.class);
        RoomEventManager.register("command", CommandEvent.class);
        RoomEventManager.register("chicken",ChickBeautifulEvent.class);
        RoomEventManager.register("light",LightEvent.class);
        RoomEventManager.register("die", DiwBowEvent.class);

        sendMessageToConsole("&eLoad &a"+RoomEventManager.EVENT.size()+" &eEvents");



        ThreadManager.init();



        enableRecord = this.getConfig().getBoolean("enable-Record");
        this.getLogger().info(TextFormat.colorize('&',BedWarMain.getLanguage().getLanguage("success","&a起床战争插件加载完成，祝您使用愉快")));

    }

    public static LanguageManager getLanguage() {
        return language;
    }

    public void initSkin(){
        this.getLogger().info(TextFormat.colorize('&',"&dLoading Block Skin"));
        //初始化 皮肤
        //方块列表
        String[] blockList = {"diamond","emerald","gold","iron"};
        //检测 skin 文件夹没有文件的时候初始化写入
        File skinFile = new File(BedWarMain.getBedWarMain().getDataFolder()+"/skin");

        if(!skinFile.exists()){
            if(!skinFile.mkdirs()){
                sendMessageToConsole("create skin directory failed!");
            }
            //写入皮肤文件
            for(String skinName: blockList){
                this.getLogger().info(TextFormat.colorize('&',"Load Skin: "+skinName));

                saveResource("blockskin/"+skinName+"/skin.json","/skin/"+skinName+"/skin.json",false);
                saveResource("blockskin/"+skinName+"/skin.png","/skin/"+skinName+"/skin.png",false);
            }
        }


        SkinManager.init();
        this.getLogger().info(TextFormat.colorize('&',"&aBlock Skin Init OK!"));
    }

    /**
     * 加载配置文件
    */
    public void loadBedWarConfig(){
//        saveDefaultConfig();
//        reloadConfig();
        uiType = Utils.loadUiTypeByName(getConfig().getString("shop-ui","auto"));
        upExp = getConfig().getInt("up-exp",500);
        NbtItemManager.init();
        dataManager = PlayerDataManager.asFile(new File(this.getDataFolder()+File.separator+"player.json"));
        File mainFileDir = new File(this.getDataFolder()+File.separator+"rooms");
        if(!mainFileDir.exists()){
            if(!mainFileDir.mkdirs()){
                sendMessageToConsole("&cCreate Folder rooms Failed!");
            }
        }

        roomManager = RoomManager.initGameRoomConfig(mainFileDir);
        sendMessageToConsole("&aRoom Loading OK!");
        this.getServer().getPluginManager().registerEvents(roomManager,this);
        if(getConfig().getAll().size() == 0) {
            this.saveResource("config.yml", true);
            reloadConfig();
        }
        menuRoomManager = new MenuRoomManager(getConfig());



        //初始化排行榜
        topManager = PlayerTopManager.asFile(new File(this.getDataFolder()+File.separator+"top.json"));
        if(topManager != null){
            topManager.init();
        }



    }

    public static int getUpExp(){
        return upExp;
    }

    public static PlayerTopManager getTopManager() {
        return topManager;
    }

    public static BedWarMain getBedWarMain() {
        return bedWarMain;
    }

    public static MenuRoomManager getMenuRoomManager() {
        return menuRoomManager;
    }

    public static PlayerDataManager getDataManager() {
        return dataManager;
    }

    public static void sendMessageToConsole(String msg){
        sendMessageToObject(msg,null);
    }

    public static String getTitle(){
        return TextFormat.colorize('&',bedWarMain.getConfig().getString("title"));
    }

    public static String getScoreBoardTitle(){
        return TextFormat.colorize('&',bedWarMain.getConfig().getString("scoreboard-title","&f[&a起床战争&f]"));
    }

    public static void sendTipMessageToObject(String msg,Object o){
        String message = TextFormat.colorize('&',msg);
        if(o != null){
            if(o instanceof Player){
                if(((Player) o).isOnline()) {
                    ((Player) o).sendMessage(message);
                    return;
                }
            }
        }
        bedWarMain.getLogger().info(message);

    }

    public static void initLanguage(PluginBase plugin,String lang) {
        //TODO 检查字符串是否合法

        if(!LANGUAGE_LIST.contains(lang.toLowerCase())){
            lang = LANGUAGE_LIST.get(0);
        }
        language = new LanguageManager(plugin,lang);
    }

    public static void sendMessageToObject(String msg,Object o){
        String message = TextFormat.colorize('&',getTitle()+" &b>>&r "+msg);
        sendTipMessageToObject(message,o);
    }

    public static void sendSubTitle(String msg,Player o){
        String message = TextFormat.colorize('&',msg);
        if(o != null){
            if(o.isOnline()) {
                o.setSubtitle(message);
            }
        }else{
            bedWarMain.getLogger().info(message);
        }
    }

    public static void sendTitle(String msg,int time,Player o){
        String message = TextFormat.colorize('&',msg);
        if(o != null){
            if(o.isOnline()) {
                o.sendTitle(message,null,0,time,0);
            }
        }else{
            bedWarMain.getLogger().info(message);
        }
    }

    public static void sendTip(String msg,Player o){
        String message = TextFormat.colorize('&',msg);
        if(o != null){
            if(o.isOnline()) {
                o.sendTip(message);
            }
        }else{
            bedWarMain.getLogger().info(message);
        }
    }


    public static RoomManager getRoomManager() {
        return roomManager;
    }

    public static LinkedHashMap<Position,Block> spawnBlockByPosAndSize(Position position,int size,Block block){
        LinkedHashMap<Position,Block> blocks = new LinkedHashMap<>();
        int startX = position.getFloorX() - (size / 2);
        int endX = position.getFloorX() + (size / 2);
        int startZ = position.getFloorZ() - (size / 2);
        int endZ = position.getFloorZ() + (size / 2);
        for(int x = startX;x <= endX;x++){
            for(int z = startZ;z <= endZ;z++) {
                Position position1 = new Position(x,position.getFloorY(),z,position.getLevel());
                if(position1.getLevelBlock().getId() != 0){
                    continue;
                }
                blocks.put(position1, block);
            }
        }
        return blocks;
    }

    private void checkServer(){
        boolean ver = false;
        //双核心兼容
        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            c.getField("NUKKIT_PM1E");
            ver = true;

        } catch (ClassNotFoundException | NoSuchFieldException ignore) { }
        try {
            Class<?> c = Class.forName("cn.nukkit.Nukkit");
            c.getField("NUKKIT").get(c).toString().equalsIgnoreCase("Nukkit PetteriM1 Edition");
            ver = true;
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignore) {
        }

        AbstractFakeInventory.IS_PM1E = ver;
        if(ver){
            sendMessageToConsole("&eCore Nukkit PM1E");
        }else{
            sendMessageToConsole("&eCore Nukkit");
        }
    }

    public enum UiType{
        /**
         * auto: 自动
         *
         * packet: GUI界面
         *
         * ui: 箱子界面
         * */
        AUTO,PACKET,UI
    }

    @Override
    public void onDisable() {
        if(topManager != null){
            topManager.save();
        }
        if(dataManager != null){
            dataManager.save();
        }
        if(roomManager != null){
            for (GameRoomConfig roomConfig: roomManager.getRoomConfigs()){
                roomConfig.save();
            }
        }
    }
}
