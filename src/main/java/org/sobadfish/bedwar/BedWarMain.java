package org.sobadfish.bedwar;


import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.command.BedWarCommand;
import org.sobadfish.bedwar.command.BedWarAdminCommand;
import org.sobadfish.bedwar.command.BedWarSpeakCommand;
import org.sobadfish.bedwar.entity.EntityBlueWitherSkull;
import org.sobadfish.bedwar.entity.EntityFireBall;
import org.sobadfish.bedwar.entity.IronGolem;
import org.sobadfish.bedwar.manager.*;
import org.sobadfish.bedwar.manager.data.PlayerDataManager;
import org.sobadfish.bedwar.manager.data.PlayerTopManager;
import org.sobadfish.bedwar.panel.lib.AbstractFakeInventory;
import org.sobadfish.bedwar.room.event.*;
import org.sobadfish.bedwar.tools.Utils;
import org.sobadfish.bedwar.variable.BedWarVariable;

import java.io.File;
import java.util.LinkedHashMap;

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

    private static BedWarMain bedWarMain;

    private static RoomManager roomManager;

    private static MenuRoomManager menuRoomManager;

    private static PlayerDataManager dataManager;

    private static PlayerTopManager topManager;

    public static UiType uiType;

    @Override
    public void onEnable() {
        bedWarMain = this;
        //  TODO 初始化文件
        checkServer();
        this.getLogger().info(TextFormat.colorize('&',"&b   ____           ___          __"));
        this.getLogger().info(TextFormat.colorize('&',"&b  |  _ \\         | \\ \\        / /"));
        this.getLogger().info(TextFormat.colorize('&',"&b  | |_) | ___  __| |\\ \\  /\\  / /_ _ _ __"));
        this.getLogger().info(TextFormat.colorize('&',"&b  |  _ < / _ \\/ _` | \\ \\/  \\/ / _` | '__|"));
        this.getLogger().info(TextFormat.colorize('&',"&b  | |_) |  __/ (_| |  \\  /\\  / (_| | |"));
        this.getLogger().info(TextFormat.colorize('&',"&b  |____/ \\___|\\__,_|   \\/  \\/ \\__,_|_|"));
        this.getLogger().info(TextFormat.colorize('&',"&b"));
        this.getLogger().info(TextFormat.colorize('&',"&e正在加载BedWar 起床战争插件 本版本为&av"+this.getDescription().getVersion()+"&e 开源版本"));
        this.getLogger().info(TextFormat.colorize('&',"&c插件作者:&b sobadfish(某吃瓜咸鱼) &aQQ：&e1586235767"));
        this.getLogger().info(TextFormat.colorize('&',"&c本插件为原创插件 部分源代码出处已标明原作者"));
        Entity.registerEntity("FireBall", EntityFireBall.class);
        Entity.registerEntity(EntityBlueWitherSkull.class.getSimpleName(), EntityBlueWitherSkull.class);
        Entity.registerEntity(IronGolem.class.getSimpleName(), IronGolem.class);
        loadBedWarConfig();
        //TODO 注册指令
        this.getServer().getCommandMap().register("badwar",new BedWarAdminCommand("bd"));
        this.getServer().getCommandMap().register("badwar",new BedWarCommand("bw"));
        this.getServer().getCommandMap().register("badwar",new BedWarSpeakCommand("bws"));
        RandomJoinManager.newInstance().start();

        RoomEventManager.register("time", TimeEvent.class);
        RoomEventManager.register("break", BreakEvent.class);
        RoomEventManager.register("custom", CustomEvent.class);
        RoomEventManager.register("effect", EffectEvent.class);
        RoomEventManager.register("command", CommandEvent.class);
        RoomEventManager.register("chicken",ChickBeautifulEvent.class);
        RoomEventManager.register("light",LightEvent.class);

        sendMessageToConsole("&e当前内置 &a"+RoomEventManager.EVENT.size()+" &e个事件");

        sendMessageToConsole("&a正在检查相应的依赖");
        for(String s : this.getDescription().getSoftDepend()){
            Plugin plugin = getServer().getPluginManager().getPlugin(s);
            if(plugin == null){
                sendMessageToConsole("&c"+s+" 插件未加载，部分功能可能无法实现");
                continue;
            }
            sendMessageToConsole("&a检测到 "+s+" 插件");
            if(s.equalsIgnoreCase("RsNPC") || s.equalsIgnoreCase("RsNPCX")){
                sendMessageToConsole("&7正在对接 "+s+" 插件");
                BedWarVariable.init();
                sendMessageToConsole("&a对接 "+s+" 插件完成");
                break;
            }
        }
        ThreadManager.init();
        this.getLogger().info(TextFormat.colorize('&',"&a起床战争插件加载完成，祝您使用愉快"));

    }

    /**
     * 加载配置文件
    */
    public void loadBedWarConfig(){
        saveDefaultConfig();
        reloadConfig();
        uiType = Utils.loadUiTypeByName(getConfig().getString("shop-ui","auto"));
        NbtItemManager.init();
//        saveResource("skin.png");
        File mainFileDir = new File(this.getDataFolder()+File.separator+"rooms");
        if(!mainFileDir.exists()){
            if(!mainFileDir.mkdirs()){
                sendMessageToConsole("&c创建文件夹 rooms失败");
            }
        }


        roomManager = RoomManager.initGameRoomConfig(mainFileDir);

        sendMessageToConsole("&a房间数据全部加载完成");
        this.getServer().getPluginManager().registerEvents(roomManager,this);
        if(getConfig().getAll().size() == 0) {
            this.saveResource("config.yml", true);
            reloadConfig();
        }
        menuRoomManager = new MenuRoomManager(getConfig());


        dataManager = PlayerDataManager.asFile(new File(this.getDataFolder()+File.separator+"player.json"));
        //初始化排行榜
        topManager = PlayerTopManager.asFile(new File(this.getDataFolder()+File.separator+"top.json"));
        if(topManager != null){
            topManager.init();
        }


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
            Class c = Class.forName("cn.nukkit.Nukkit");
            c.getField("NUKKIT_PM1E");
            ver = true;

        } catch (ClassNotFoundException | NoSuchFieldException ignore) { }
        try {
            Class c = Class.forName("cn.nukkit.Nukkit");
            c.getField("NUKKIT").get(c).toString().equalsIgnoreCase("Nukkit PetteriM1 Edition");
            ver = true;
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException ignore) {
        }

        AbstractFakeInventory.IS_PM1E = ver;
        if(ver){
            sendMessageToConsole("&e当前核心为 Nukkit PM1E");
        }else{
            sendMessageToConsole("&e当前核心为 Nukkit");
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
    }
}
