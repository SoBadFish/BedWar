package org.sobadfish.bedwar;


import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.command.BedWarCommand;
import org.sobadfish.bedwar.command.BedWarAdminCommand;
import org.sobadfish.bedwar.command.BedWarSpeakCommand;
import org.sobadfish.bedwar.entity.EntityBlueWitherSkull;
import org.sobadfish.bedwar.entity.EntityFireBall;
import org.sobadfish.bedwar.entity.IronGolem;
import org.sobadfish.bedwar.manager.MenuRoomManager;
import org.sobadfish.bedwar.manager.NbtItemManager;
import org.sobadfish.bedwar.manager.RoomManager;
import org.sobadfish.bedwar.panel.lib.AbstractFakeInventory;
import org.sobadfish.bedwar.tools.Utils;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * @author SoBadFish
 */
public class BedWarMain extends PluginBase {

    private static BedWarMain bedWarMain;

    private static RoomManager roomManager;

    private static MenuRoomManager menuRoomManager;


    public static UiType uiType;

    @Override
    public void onEnable() {
        bedWarMain = this;
        //  TODO 初始化文件
        checkServer();

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
        File mainFileDir = new File(this.getDataFolder()+"/rooms");
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
    }

    public static BedWarMain getBedWarMain() {
        return bedWarMain;
    }

    public static MenuRoomManager getMenuRoomManager() {
        return menuRoomManager;
    }



    public static void sendMessageToConsole(String msg){
        sendMessageToObject(msg,null);
    }

    public static String getTitle(){
        return TextFormat.colorize('&',bedWarMain.getConfig().getString("title"));
    }

    public static void sendTipMessageToObject(String msg,Object o){
        String message = TextFormat.colorize('&',msg);
        if(o != null){
            if(o instanceof Player){
                if(((Player) o).isOnline()) {
                    ((Player) o).sendMessage(message);
                }
            }
        }else{
            bedWarMain.getLogger().info(message);
        }
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
                o.sendTitle(message,null,5,time,5);
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
}
