package org.sobadfish.bedwar.world.config;


import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.utils.Config;
import org.sobadfish.bedwar.item.MoneyItemInfo;
import org.sobadfish.bedwar.item.config.ItemInfoConfig;


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

    public static WorldInfoConfig getInstance(MoneyItemInfo itemInfo, Config config){
        Level gameWorld = Server.getInstance().getLevelByName(config.getString("world"));
        if(gameWorld == null){
            Server.getInstance().loadLevel(config.getString("world"));
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
        return new Position(
                Integer.parseInt(pos[0]),
                Integer.parseInt(pos[1]),
                Integer.parseInt(pos[2]),
                Server.getInstance().getLevelByName(pos[3])

        );

    }
    public static Location getLocationByString(String str){
        String[] pos = str.split(":");
        return new Location(
                Integer.parseInt(pos[0]),
                Integer.parseInt(pos[1]),
                Integer.parseInt(pos[2]),
                Double.parseDouble(pos[4]),
                0, Server.getInstance().getLevelByName(pos[3])

        );

    }
}
