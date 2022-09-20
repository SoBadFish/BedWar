package org.sobadfish.bedwar.room;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockBed;
import cn.nukkit.block.BlockChest;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.potion.Effect;
import de.theamychan.scoreboard.network.Scoreboard;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.event.*;
import org.sobadfish.bedwar.item.button.FollowItem;
import org.sobadfish.bedwar.item.button.RoomQuitItem;
import org.sobadfish.bedwar.item.button.TeamChoseItem;
import org.sobadfish.bedwar.manager.RandomJoinManager;
import org.sobadfish.bedwar.manager.RoomManager;
import org.sobadfish.bedwar.manager.WorldResetManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.player.team.config.TeamInfoConfig;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.room.floattext.FloatTextInfo;
import org.sobadfish.bedwar.room.floattext.FloatTextInfoConfig;
import org.sobadfish.bedwar.shop.ShopInfo;
import org.sobadfish.bedwar.tools.Utils;
import org.sobadfish.bedwar.world.WorldInfo;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author SoBadFish
 * 2022/1/2
 */

public class GameRoom {

    public int loadTime = -1;

    private final GameRoomConfig roomConfig;

    private final EventControl eventControl;

    private boolean hasStart;



    /**
     * 地图配置
     * */
    public WorldInfo worldInfo;

    public boolean close;

    /*
     * 是否被释放
     */
    public boolean isGc = false;

    private GameType type;

    private ShopInfo shopInfo;

    private final ArrayList<TeamInfo> teamInfos = new ArrayList<>();

    private final ArrayList<BlockChest> clickChest = new ArrayList<>();

    private final ArrayList<FloatTextInfo> floatTextInfos = new ArrayList<>();

    private final LinkedHashMap<PlayerInfo, Scoreboard> scoreboards = new LinkedHashMap<>();

    /**
     * 复活时间
     * */
    public int reSpawnTime = 5;


    private final CopyOnWriteArrayList<PlayerInfo> playerInfos = new CopyOnWriteArrayList<>();

    private GameRoom(GameRoomConfig roomConfig){
        this.roomConfig = roomConfig;
        this.worldInfo = new WorldInfo(this,roomConfig.worldInfo);
        initShopInfo();
        type = GameType.WAIT;
        for(TeamInfoConfig config: getRoomConfig().getTeamConfigs()){
            teamInfos.add(new TeamInfo(this,config));
        }
        //启动事件
        eventControl = new EventControl(this,roomConfig.eventConfig);
        eventControl.initAll(this);
        //初始化浮空字
    }

    public ArrayList<FloatTextInfo> getFloatTextInfos() {
        return floatTextInfos;
    }

    private boolean isInit = true;

    public ArrayList<BlockChest> getClickChest() {
        return clickChest;
    }

    public GameRoomConfig getRoomConfig() {
        return roomConfig;
    }

    public int getLoadTime() {
        return loadTime;
    }

    public ArrayList<TeamInfo> getTeamInfos() {
        return teamInfos;
    }

    public GameType getType() {
        return type;
    }

    public LinkedHashMap<PlayerInfo, Scoreboard> getScoreboards() {
        return scoreboards;
    }

    public ShopInfo getShopInfo() {
        return shopInfo;
    }

    public WorldInfo getWorldInfo() {
        return worldInfo;
    }

    public void setType(GameType type) {
        this.type = type;
    }

    public EventControl getEventControl() {
        return eventControl;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    /**
     * 根据名称
     * */
    private TeamInfo getTeamInfo(String name){
        for(PlayerInfo info : playerInfos){
            if(info.getTeamInfo() != null &&
                    info.getTeamInfo().getTeamConfig().getName().equalsIgnoreCase(name)){
                return info.getTeamInfo();
            }
        }
        return null;
    }


    private boolean isMax;

    /** 房间被实例化后 */
    public void onUpdate(){
        if(close){
            return;
        }
        //TODO 当房间启动后
        if(getIPlayerInfos().size() == 0 && !isInit){
            onDisable();
            return;
        }
        switch (type){
            case WAIT:
                onWait();
                break;
            case START:
                eventControl.enable = true;
                worldInfo.isStart = true;
                try {
                    onStart();
                }catch (Exception e){
                    e.printStackTrace();
                    for(PlayerInfo playerInfo: new ArrayList<>(playerInfos)){
                        playerInfo.sendForceMessage("房间出现异常 请联系服主/管理员修复");
                    }
                    onDisable();
                    return;
                }

                break;
            case END:
                //TODO 房间结束
               onEnd();
                break;
            case CLOSE:
                onDisable();
                break;
            default:break;
        }

        //移除编外人员
        for(PlayerInfo info: getInRoomPlayers()){
            if(!BedWarMain.getRoomManager().playerJoin.containsKey(info.getPlayer().getName())){
                playerInfos.remove(info);
            }
        }

    }
    private void onEnd(){
        if(loadTime == -1){
            loadTime = 10;
        }

        for(PlayerInfo playerInfo:getLivePlayers()){
            Utils.spawnFirework(playerInfo.getPosition());
        }

        if(loadTime == 0){
            type = GameType.CLOSE;

        }
    }



    private void onStart(){
        hasStart = true;
        eventControl.run();
        if(loadTime == -1 && teamAll){
            for(FloatTextInfoConfig config: roomConfig.floatTextInfoConfigs){
                FloatTextInfo info = new FloatTextInfo(config).init(this);
                if(info != null){
                    floatTextInfos.add(info);
                }
            }
            //TODO 当房间开始
            for(TeamInfo t:teamInfos){
                t.placeBed();

            }
            for(PlayerInfo i : getPlayerInfos()){
//                    i.clear();
                try {
                    i.spawn();
                }catch (Exception e){
                    i.sendForceMessage("&c出现未知原因影响导致无法正常传送 正在重新将你移动中");
                    try {
                        i.spawn();
                    }catch (Exception e1){
                        i.sendForceMessage("&c移动失败 请尝试重新进入游戏");
                        quitPlayerInfo(i,true);
                    }
                }
            }
            sendTitle("&c游戏开始");
            sendSubTitle("保护你的床");
            shopInfo.init(getRoomConfig());
            loadTime = getRoomConfig().time;
            worldInfo = new WorldInfo(this,getRoomConfig().worldInfo);
            GameRoomStartEvent event = new GameRoomStartEvent(this,BedWarMain.getBedWarMain());
            Server.getInstance().getPluginManager().callEvent(event);

        }
        if(loadTime > 0) {

            for (TeamInfo teamInfo : teamInfos) {
                teamInfo.onUpdate();
            }
            if (getLiveTeam().size() == 1) {
                TeamInfo teamInfo = getLiveTeam().get(0);
                teamInfo.echoVictory();
                type = GameType.END;
                worldInfo.setClose(true);
                loadTime = 5;
            }
        }else{
            TeamInfo successInfo = null;
            ArrayList<TeamInfo> teamInfos = getLiveTeam();
            if(teamInfos.size() > 0) {
                int pl = 0;
                for (TeamInfo info : teamInfos) {
                    if (pl == 0) {
                        pl++;
                        successInfo = info;
                        continue;
                    }

                    info.onUpdate();
                    info.setStop(true);

                }
                successInfo.echoVictory();

            }
            //TODO 当时间结束的一些逻辑
            type = GameType.END;
            worldInfo.setClose(true);
            loadTime = -1;
        }
    }

    private boolean teamAll;

    private void onWait(){
        if(getPlayerInfos().size() >= getRoomConfig().minPlayerSize){
            if(loadTime == -1){
                loadTime = getRoomConfig().waitTime;
                sendMessage("&2到达最低人数限制&e "+loadTime+" &2秒后开始游戏");

            }
        }else {
            loadTime = -1;
        }
        if(getPlayerInfos().size() == getRoomConfig().getMaxPlayerSize()){
            if(!isMax){
                isMax = true;
                loadTime = getRoomConfig().getMaxWaitTime();
            }
        }
        if(loadTime >= 1) {
            sendTip("&e距离开始还剩 &a " + loadTime + " &e秒");
            if(loadTime <= 5){
                switch (loadTime){
                    //case 5: sendTitle("&a5");break;
                    //case 4: sendTitle("&e4");break;
                    case 3: sendTitle("&63");break;
                    case 2: sendTitle("&42");break;
                    case 1: sendTitle("&41");break;
                    default:
                        sendTitle("");break;

                }
                //音效
                //addSound(Sound.RANDOM_CLICK);

            }
            if(loadTime <= 3){
                //音效
                addSound(Sound.RANDOM_TOAST);

            }
            if(loadTime == 1){
                type = GameType.START;
                loadTime = -1;
                if(allotOfAverage()){
                    teamAll = true;
                }


            }
        }else{
            sendTip("&a等待中");
        }
    }

    private void initShopInfo(){
        ArrayList<LinkedHashMap<String, String>> linkedHashMaps = new ArrayList<>();
        for(TeamInfoConfig teamInfoConfig: roomConfig.teamConfigs){
            linkedHashMaps.add(teamInfoConfig.getVillage());
        }
        shopInfo = new ShopInfo(linkedHashMaps);
    }

    /**
     * 旁观者们
     * */
    public ArrayList<PlayerInfo> getWatchPlayers(){
        ArrayList<PlayerInfo> t = new ArrayList<>();
        for(PlayerInfo playerInfo: playerInfos){
            if(playerInfo.isWatch()){
                t.add(playerInfo);
            }
        }
        return t;
    }




    /**
     * 离开游戏的玩家们
     * */
    public ArrayList<PlayerInfo> getLeavePlayers(){
        ArrayList<PlayerInfo> t = new ArrayList<>();
        for(PlayerInfo playerInfo: playerInfos){
            if(playerInfo.isLeave()){
                t.add(playerInfo);
            }
        }
        return t;
    }
    /**
     * 还在游戏内的存活玩家
     * */
    public ArrayList<PlayerInfo> getLivePlayers(){
        ArrayList<PlayerInfo> t = new ArrayList<>();
        for(PlayerInfo playerInfo: playerInfos){
            if(playerInfo.isLive()){
                t.add(playerInfo);
            }
        }
        return t;
    }
    /**
     * 还在游戏内的玩家
     * */
    public ArrayList<PlayerInfo> getInRoomPlayers(){
        ArrayList<PlayerInfo> t = new ArrayList<>();
        for(PlayerInfo playerInfo: playerInfos){
            if(playerInfo.isInRoom()){
                t.add(playerInfo);
            }
        }
        return t;
    }


    public ArrayList<TeamInfo> getLiveTeam(){
        ArrayList<TeamInfo> t = new ArrayList<>();
        for(TeamInfo teamInfo: teamInfos){
            if(teamInfo.isLoading()){
                t.add(teamInfo);
            }
        }
        return t;
    }


//

    public static GameRoom enableRoom(GameRoomConfig roomConfig){

        if(roomConfig.getWorldInfo().getGameWorld() == null){
            return null;
        }
        if(WorldResetManager.RESET_QUEUE.containsKey(roomConfig)){
            return null;
        }
        return new GameRoom(roomConfig);
    }

    public void addSound(Sound sound){
        for(PlayerInfo info: getPlayerInfos()){
            info.addSound(sound);
        }
    }

    /**
     * 全队BUFF
     * */
    public void addEffect(Effect effect){
        for(PlayerInfo info: getLivePlayers()){
            info.addEffect(effect);
        }
    }

    public boolean toBreakBlock(PlayerInfo info,Block block){
        if(worldInfo.getPlaceBlock().contains(block)){
            return worldInfo.onChangeBlock(block, false);


        }else{
            if(block instanceof BlockBed){
                return isBreadBed(info, block);
            }
        }
        return false;

    }

    public JoinType joinPlayerInfo(PlayerInfo info,boolean sendMessage){
        if(WorldResetManager.RESET_QUEUE.containsKey(roomConfig)){
            return JoinType.NO_JOIN;
        }
        if(info.getGameRoom() == null){
            if(info.getPlayer() instanceof Player) {
                if(!((Player) info.getPlayer()).isOnline()){
                    return JoinType.NO_ONLINE;
                }
            }

            if(getType() != GameType.WAIT){
                if(getType() == GameType.END || getType() == GameType.CLOSE){
                    return JoinType.NO_JOIN;
                }
                return JoinType.CAN_WATCH;
            }
            if(getWorldInfo().getConfig().getGameWorld() == null || getWorldInfo().getConfig().getGameWorld().getSafeSpawn() == null){
                return JoinType.NO_LEVEL;
            }

            PlayerJoinRoomEvent event = new PlayerJoinRoomEvent(info,this,BedWarMain.getBedWarMain());
            event.setSend(sendMessage);
            Server.getInstance().getPluginManager().callEvent(event);
            if(event.isCancelled()){
                return JoinType.NO_JOIN;
            }
            info.sendForceTitle("",1);
            info.sendForceSubTitle("");
            sendMessage(info+"&e加入了游戏 &7("+(playerInfos.size()+1)+"/"+getRoomConfig().getMaxPlayerSize()+")");
            //sendMessage(info+"&e加入了游戏 &7("+(playerInfos.size()+1)+"/"+getRoomConfig().getMaxPlayerSize()+")");
            sendMessage("&l&f"+info+"&r&e已加入"+"&7("+(playerInfos.size()+1)+"/"+getRoomConfig().getMaxPlayerSize()+")");
            info.init();
            info.getPlayer().getInventory().setItem(TeamChoseItem.getIndex(),TeamChoseItem.get());
            info.getPlayer().getInventory().setItem(RoomQuitItem.getIndex(),RoomQuitItem.get());
            info.setPlayerType(PlayerInfo.PlayerType.WAIT);
            info.setGameRoom(this);
            if(info.getPlayer() instanceof Player) {
                BedWarMain.getRoomManager().playerJoin.put(info.getPlayer().getName(),getRoomConfig().name);
            }
            playerInfos.add(info);
            info.getPlayer().teleport(getWorldInfo().getConfig().getWaitPosition());
            if(info.getPlayer() instanceof Player) {
                ((Player)info.getPlayer()).setGamemode(2);
            }
            if(isInit){
                isInit = false;
            }

        }else {
            if(info.getGameRoom().getType() != GameType.END && info.getGameRoom() == this){
                return JoinType.NO_JOIN;
            }else{
                info.getGameRoom().quitPlayerInfo(info,true);
                return JoinType.CAN_WATCH;
            }
        }
        return JoinType.CAN_JOIN;

    }

    public enum JoinType{
        NO_ONLINE,NO_JOIN,NO_LEVEL,CAN_WATCH,CAN_JOIN
    }

    /**
     * 分配玩家
     * */
    private boolean allotOfAverage(){

        int t =  (int) Math.ceil(playerInfos.size() / (double)getRoomConfig().getTeamConfigs().size());
        PlayerInfo listener;
        LinkedList<PlayerInfo> noTeam = getNoTeamPlayers();
        // TODO 检测是否一个队伍里有太多的人 拆掉多余的人
        for (TeamInfo manager: teamInfos){
            if(manager.getTeamPlayers().size() > t){
                int size = t - manager.getTeamPlayers().size();
                for(int i = 0;i < size;i++){
                    PlayerInfo info = manager.getTeamPlayers().remove(manager.getTeamPlayers().size()-1);
                    noTeam.add(info);
                }
            }
        }
        if(teamInfos.size() == 1){
            TeamInfo teamInfo = teamInfos.get(0);
            noTeam.addAll(teamInfo.getTeamPlayers());
        }
        while(noTeam.size() > 0){
            for (TeamInfo manager: teamInfos){
                if(manager.getTeamPlayers().size() == 0
                        || (manager.getTeamPlayers().size() < t )){
                    if(noTeam.size() > 0) {
                        listener = noTeam.poll();
                        manager.mjoin(listener);
                    }
                }else{
                    if(manager.getTeamPlayers().size() > t){
                        int size =  manager.getTeamPlayers().size();
                        LinkedList<PlayerInfo> playerInfos = new LinkedList<>(manager.getTeamPlayers());
                        for(int i = 0;i <size - t;i++) {
                            noTeam.add(playerInfos.pollLast());
                        }
                    }
                }
            }
        }
        return true;
    }

    public ArrayList<PlayerInfo> getKillMostPlayers(){
        ArrayList<PlayerInfo> infos = new ArrayList<>(getLivePlayers());
        infos.sort((o1, o2) -> o1.getEndKillCount() + o1.getKillCount() > o2.getEndKillCount() + o2.getKillCount()?0:1);
        return infos;
    }

    public LinkedList<PlayerInfo> getNoTeamPlayers(){
        LinkedList<PlayerInfo> noTeam = new LinkedList<>();
        for(PlayerInfo playerInfo: playerInfos){
            if(playerInfo.getTeamInfo() == null){
                noTeam.add(playerInfo);
            }
        }
        return noTeam;
    }



    /**
     * 玩家离开游戏
     * */
    public boolean quitPlayerInfo(PlayerInfo info,boolean teleport){
        if(info != null) {
            info.isLeave = true;
            if (info.getPlayer() instanceof Player) {
                if (playerInfos.contains(info)) {
                    PlayerQuitRoomEvent event = new PlayerQuitRoomEvent(info, this, BedWarMain.getBedWarMain());
                    Server.getInstance().getPluginManager().callEvent(event);
                    if(((Player) info.getPlayer()).isOnline()) {
                        if (teleport) {
                            info.getPlayer().teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
                        }
                        info.getPlayer().removeAllEffects();
                        ((Player) info.getPlayer()).setExperience(0, 0);
                    }
                    info.cancel();
                    BedWarMain.getRoomManager().playerJoin.remove(info.getPlayer().getName());
                } else {
                    BedWarMain.getRoomManager().playerJoin.remove(info.getPlayer().getName());

                }
            } else {
                info.getPlayer().close();
                playerInfos.remove(info);

            }
        }
        if (getIPlayerInfos().size() == 0) {
            onDisable();
        }
        return true;
    }


    public PlayerInfo getPlayerInfo(EntityHuman player){
        if(playerInfos.contains(new PlayerInfo(player))){
            return playerInfos.get(playerInfos.indexOf(new PlayerInfo(player)));
        }
        return null;
    }

    public CopyOnWriteArrayList<PlayerInfo> getPlayerInfos() {
        playerInfos.removeIf((p)->p.disable);
        return playerInfos;
    }

    public ArrayList<PlayerInfo> getIPlayerInfos() {
        ArrayList<PlayerInfo> p = new ArrayList<>();
        for(PlayerInfo info: playerInfos){
            if(info.getPlayer() instanceof Player){
                if(!info.isLeave()) {
                    p.add(info);
                }
            }
        }
        return p;
    }

    public void sendMessageOnWatch(String msg) {
        ArrayList<PlayerInfo> watchPlayer = new ArrayList<>();
        for(PlayerInfo info: playerInfos){
            if(info.isWatch()){
                watchPlayer.add(info);
            }
        }
        watchPlayer.forEach(dp -> dp.sendMessage(msg));
    }

    public void joinWatch(PlayerInfo info) {
        //TODO 欢迎加入观察者大家庭
        if(!playerInfos.contains(info)){


            info.init();
            info.setGameRoom(this);
            if(info.getPlayer() instanceof Player) {
                BedWarMain.getRoomManager().playerJoin.put(info.getPlayer().getName(),getRoomConfig().name);
            }
            playerInfos.add(info);

        }
        if(info.getPlayer() instanceof Player) {
            ((Player)info.getPlayer()).setGamemode(3);
        }

        info.setPlayerType(PlayerInfo.PlayerType.WATCH);
        info.getPlayer().getInventory().setItem(RoomQuitItem.getIndex(),RoomQuitItem.get());
        info.getPlayer().getInventory().setItem(FollowItem.getIndex(), FollowItem.get());
        info.getPlayer().getInventory().setHeldItemSlot(0);
        sendMessage("&7"+info+"&7 成为了旁观者 （"+getWatchPlayers().size()+"）");
        info.sendMessage("&e你可以等待游戏结束 也可以手动退出游戏房间");
        Position position = getTeamInfos().get(0).getTeamConfig().getBedPosition();
        position.add(0,64,0);
        position.level = getWorldInfo().getConfig().getGameWorld();
        info.getPlayer().teleport(position);

    }



    public enum GameType{
        /**
         * WAIT: 等待 START: 开始 END: 结束 CLOSE: 关闭
         * */
        WAIT,START,END,CLOSE
    }

    /**
     * 获取床对于的队伍
     * @param position 床坐标
     * @return 队伍
     * */
    public TeamInfo getBedPositionToTeam(Position position){
        for(TeamInfo teamInfo: teamInfos){
            if(teamInfo.getTeamConfig().getBedPosition().distance(position) < 4){
                return teamInfo;
            }
        }
        return null;

    }

    /**
     * 判断是否床被破坏
     * @param info 破坏的玩家
     * @param position 破坏的坐标
     *
     * @return 是否被破坏掉
     * */
    private boolean isBreadBed(PlayerInfo info, Position position){
        TeamInfo info1 = getBedPositionToTeam(position);
        if(info1 != null) {
            Block block = info1.getTeamConfig().getBedPosition().getLevelBlock();
            if (block instanceof BlockBed) {
                //TODO 判断一下床是否被保护的严实
                if(isProtect((BlockBed)position)){
                    info.sendMessage("&c这个床被方块包围，你至少要挖开一角");
                    return false;
                }
                if (info.getTeamInfo().getTeamConfig().equals(info1.getTeamConfig())) {
                    info.sendMessage("&c你不能破坏自己的床");
                    return false;
                } else {
                    TeamBedBreakEvent event = new TeamBedBreakEvent(info1,info,this,BedWarMain.getBedWarMain());
                    Server.getInstance().getPluginManager().callEvent(event);
                    info.bedBreakCount++;
                    sendMessage(info.toString() + " &c破坏了 " + info1.getTeamConfig().getNameColor() + info1.getTeamConfig().getName() + " &c的床!");
                    info1.onBedBreak(info);
                    return true;
                }

            }
        }
        return false;
    }

    private boolean isProtect(BlockBed block) {
        List<Block> blocks = new ArrayList<>();
        Block block2 = block.getSide(block.getBlockFace());
        for(BlockFace fence: BlockFace.values()){
            blocks.add(block.getSide(fence));
            blocks.add(block2.getSide(fence));
        }
        for(Block block1: blocks){
            if(block1.getId() == 0){
                return false;
            }
        }
        return true;

    }

    /**
     * 仅阵亡玩家观看
     * */
    public void sendMessageOnDeath(String msg){
        ArrayList<PlayerInfo> deathPlayer = new ArrayList<>();
        for(PlayerInfo info: playerInfos){
            if(info.isDeath()){
                deathPlayer.add(info);
            }
        }
        deathPlayer.forEach(dp -> dp.sendMessage(msg));
    }


    public void sendTipMessage(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendTipMessage(msg);
        }
    }

    public void sendMessage(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendMessage(msg);
        }
    }

    public void sendFaceMessage(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendForceMessage(msg);
        }
    }
    public void sendTitle(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendTitle(msg);
        }
    }
    public void sendSubTitle(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendSubTitle(msg);
        }
    }
    public void sendTip(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendTip(msg);
        }
    }

    public void sendActionBar(String msg){
        for(PlayerInfo info: getPlayerInfos()){
            info.sendActionBar(msg);
        }
    }


    /**
     * 关闭房间
     * */
    public void onDisable(){
        if(close){
            return;
        }
        close = true;
        type = GameType.CLOSE;
        if(hasStart) {
            roomConfig.save();
            GameCloseEvent event = new GameCloseEvent(this, BedWarMain.getBedWarMain());
            Server.getInstance().getPluginManager().callEvent(event);
            worldInfo.setClose(true);
            //房间结束后的执行逻辑
            if(getRoomConfig().isAutomaticNextRound){
                sendMessage("&7即将自动进行下一局");
                for(PlayerInfo playerInfo: getInRoomPlayers()){
                    RandomJoinManager.joinManager.nextJoin(playerInfo);
                }
            }
            //TODO 房间被关闭 释放一些资源
            for (PlayerInfo info : playerInfos) {
                info.clear();
                if (info.getPlayer() instanceof Player) {
                    quitPlayerInfo(info, true);
                }
            }

            //浮空字释放
            for(FloatTextInfo floatTextInfo: floatTextInfos){
                floatTextInfo.bedWarFloatText.toClose();
            }

            String level = worldInfo.getConfig().getLevel();
            Level level1 = getWorldInfo().getConfig().getGameWorld();
            for(Entity entity: new CopyOnWriteArrayList<>(level1.getEntities())){
                if(entity instanceof Player){
                    //这里出现的玩家就是没有清出地图的玩家
                    entity.teleport(Server.getInstance().getDefaultLevel().getSpawnLocation());
                    BedWarMain.getRoomManager().playerJoin.remove(entity.getName());
                    ((Player) entity).setGamemode(0);
                    entity.removeAllEffects();
                    ((Player) entity).getInventory().clearAll();
                    ((Player) entity).getEnderChestInventory().clearAll();
                    ((Player) entity).getFoodData().reset();
                    continue;
                }
                if(entity != null && !entity.isClosed()){
                    entity.close();
                }

            }
            //卸载区块就炸...
//            level1.unloadChunks();
            worldInfo.setClose(true);
            worldInfo = null;
            WorldResetManager.RESET_QUEUE.put(getRoomConfig(),level);
        }else{
            worldInfo.setClose(true);
            worldInfo = null;
            BedWarMain.getRoomManager().getRooms().remove(getRoomConfig().name);
            RoomManager.LOCK_GAME.remove(getRoomConfig());
        }

    }
}
