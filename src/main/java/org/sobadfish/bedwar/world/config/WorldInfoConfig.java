package org.sobadfish.bedwar.world.config;


import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.item.MoneyItemInfo;
import org.sobadfish.bedwar.item.config.ItemInfoConfig;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.tools.Utils;


import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @author SoBadFish
 * 2022/1/2
 */
public class WorldInfoConfig {

    /**
     * 游戏地图
     * */
    private Level gameWorld;

    /**
     * 等待大厅坐标
     * */
    private Position waitPosition;



    /**
     * 物品生成点坐标
     * */
    private ArrayList<ItemInfoConfig> itemInfos;



    private WorldInfoConfig(Level gameWorld,
                            Position waitPosition,
                            ArrayList<ItemInfoConfig> itemInfos
                           ){
        this.gameWorld = gameWorld;
        this.waitPosition = waitPosition;
        this.itemInfos = itemInfos;


    }

    public static WorldInfoConfig createWorldConfig(Level gameWorld){
        return new WorldInfoConfig(gameWorld,null,new ArrayList<>());
    }

    public ArrayList<ItemInfoConfig> getItemInfos() {
        return itemInfos;
    }

    public Level getGameWorld() {
        return gameWorld;
    }



    public Position getWaitPosition() {
        return waitPosition;
    }

    public void setWaitPosition(Position waitPosition) {
        this.waitPosition = waitPosition;
    }

    /**
     * 如果插件内不存在地图，则从worlds文件夹中备份，反之写入worlds文件夹
     * */
    public static boolean initWorld(String roomName,String levelName){
        //插件的地图
        File nameFile = new File(BedWarMain.getBedWarMain().getDataFolder()+File.separator+"rooms"+File.separator+roomName);
        //主世界地图
        File world = new File(nameFile+File.separator+"world"+File.separator+levelName);
        if(!world.exists() && world.isDirectory()){
            if(toPathWorld(roomName, levelName)){
                BedWarMain.sendMessageToConsole("&a地图 &e"+levelName+" &a初始化完成");
            }else{
                BedWarMain.sendMessageToConsole("&c地图 &e"+levelName+" &c初始化失败,无法完成房间的加载");
                return false;
            }
        }
        if(!nameFile.exists()){
            if(toBackUpWorld(roomName, levelName)){
                BedWarMain.sendMessageToConsole("&a地图 &e"+levelName+" &a备份完成");
            }else{
                BedWarMain.sendMessageToConsole("&c地图 &e"+levelName+" &c备份失败,无法完成房间的加载");
                return false;
            }
        }

        return true;
    }

    public static boolean toBackUpWorld(String roomName,String levelName){
        File nameFile = new File(BedWarMain.getBedWarMain().getDataFolder()+File.separator+"rooms"+File.separator+roomName);
        File world = new File(nameFile+File.separator+"world"+File.separator+levelName);
        if(!world.exists()){
            world.mkdirs();
        }

        //
        return Utils.copyFiles(new File(Server.getInstance().getFilePath()+File.separator + "worlds" +File.separator+ levelName), world);
    }


    public static boolean toPathWorld(String roomName,String levelName){
        try {

            File nameFile = new File(BedWarMain.getBedWarMain().getDataFolder() + File.separator + "rooms" + File.separator + roomName);
            if (!nameFile.exists()) {
                return false;
            }
            File world = new File(nameFile + File.separator + "world" + File.separator + levelName);
            File[] files = world.listFiles();
            File f2 = new File(Server.getInstance().getFilePath() + File.separator + "worlds" + File.separator + levelName);
            if (!f2.exists()) {
                f2.mkdirs();
            }
            if (files != null && files.length > 0) {
                Server.getInstance().unloadLevel(Server.getInstance().getLevelByName(levelName),true);
                Utils.copyFiles(world, f2);
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
        //载入地图 删掉之前的地图文件
    }


    public void setGameWorld(Level gameWorld) {
        this.gameWorld = gameWorld;
    }

    public static WorldInfoConfig getInstance(String roomName, MoneyItemInfo itemInfo, Config config){
        Level gameWorld = Server.getInstance().getLevelByName(config.getString("world"));
        if(gameWorld == null){
            if(!initWorld(roomName,config.getString("world"))){
                return null;
            }
            gameWorld = Server.getInstance().getLevelByName(config.getString("world"));
        }
        Position waitPosition = getPositionByString(config.getString("waitPosition"));
        ArrayList<ItemInfoConfig> itemInfoConfigs = new ArrayList<>();
        Map mItemSpawn = (Map) config.get("itemSpawn");
        for(Object mName:mItemSpawn.keySet()){
            itemInfoConfigs.add(ItemInfoConfig.getItemInfoConfig(itemInfo,mName.toString(), (Map) mItemSpawn.get(mName)));
        }


        return new WorldInfoConfig(gameWorld,waitPosition,itemInfoConfigs);
    }

    public static String positionToString(Position position){
        return position.getFloorX() + ":"+position.getFloorY()+":"+position.getFloorZ()+":"+position.getLevel().getFolderName();
    }

    public static String locationToString(Location position){
        return position.getFloorX() + ":"+position.getFloorY()+":"+position.getFloorZ()+":"+position.getLevel().getFolderName()+":"+position.yaw;
    }

    public void setItemInfos(ArrayList<ItemInfoConfig> itemInfos) {
        this.itemInfos = itemInfos;
    }

    public static Position getPositionByString(String str){
        String[] pos = str.split(":");
        Level level = Server.getInstance().getLevelByName(pos[3]);
        if(level == null){
            if(Server.getInstance().loadLevel(pos[3])){
                level = Server.getInstance().getLevelByName(pos[3]);
            }
        }
        return new Position(
                Integer.parseInt(pos[0]),
                Integer.parseInt(pos[1]),
                Integer.parseInt(pos[2]),
                level

        );

    }
    public static Location getLocationByString(String str){
        String[] pos = str.split(":");
        Level level = Server.getInstance().getLevelByName(pos[3]);
        if(level == null){
            if(Server.getInstance().loadLevel(pos[3])){
                level = Server.getInstance().getLevelByName(pos[3]);
            }
        }
        return new Location(
                Integer.parseInt(pos[0]),
                Integer.parseInt(pos[1]),
                Integer.parseInt(pos[2]),
                Double.parseDouble(pos[4]),
                0, level

        );

    }
}
