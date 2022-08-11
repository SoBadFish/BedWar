package org.sobadfish.bedwar.room;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.blockentity.BlockEntity;
import cn.nukkit.blockentity.BlockEntityChest;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.level.Location;
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
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.player.team.config.TeamInfoConfig;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.shop.ShopInfo;
import org.sobadfish.bedwar.thread.BaseTimerRunnable;
import org.sobadfish.bedwar.thread.ProtectVillageThread;
import org.sobadfish.bedwar.thread.RoomLoadThread;
import org.sobadfish.bedwar.thread.WorldInfoLoadThread;
import org.sobadfish.bedwar.world.WorldInfo;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

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

        ThreadManager.addThread(new RoomLoadThread(this));

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
                onStart();
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
            //房间结束后的执行逻辑
            if(getRoomConfig().isAutomaticNextRound){
                sendMessage("&75 &e秒后自动进行下一局");
                for(PlayerInfo playerInfo: getInRoomPlayers()){
                    ThreadManager.addThread(new BaseTimerRunnable(5) {
                        @Override
                        protected void callback() {
                            if(RandomJoinManager.newInstance().join(new PlayerInfo(playerInfo.getPlayer()),null)){
                                quitPlayerInfo(playerInfo,false);
                            }

                        }
                    });
                }
            }

        }

        if(loadTime == 0){
            type = GameType.CLOSE;

        }
    }



    private void onStart(){
        eventControl.run();
        if(loadTime == -1 && teamAll){
            //TODO 当房间开始
            for(TeamInfo t:teamInfos){
                t.placeBed();
                for(PlayerInfo i : t.getTeamPlayers()){
                    i.clear();
                    i.spawn();
                }
            }

            shopInfo.init(getRoomConfig());
            ThreadManager.addThread(new ProtectVillageThread(this));
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
                    info.setStop(true);
                    info.onUpdate();

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
                    case 5: sendTitle("&a5");break;
                    case 4: sendTitle("&e4");break;
                    case 3: sendTitle("&63");break;
                    case 2: sendTitle("&42");break;
                    case 1: sendTitle("&41");break;
                    default:
                        sendTitle("");break;

                }
                //音效
                addSound(Sound.RANDOM_CLICK);

            }
            if(loadTime == 1){
                type = GameType.START;
                loadTime = -1;
                if(allotOfAverage()){
                    teamAll = true;
                }
                ThreadManager.addThread(new WorldInfoLoadThread(worldInfo));


            }
        }else{
            sendTip("&a等待中");
        }
    }

    private void initShopInfo(){
        ArrayList<LinkedHashMap<String, Location>> linkedHashMaps = new ArrayList<>();
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


    private final LinkedHashMap<PlayerInfo, Scoreboard> scoreboards = new LinkedHashMap<>();

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
            worldInfo.onChangeBlock(block,false);
            return false;
        }else{
            if(block instanceof BlockBed){
                return !isBreadBed(info, block);
            }
        }
        return true;

    }

    public boolean joinPlayerInfo(PlayerInfo info,boolean sendMessage){
        if(info.getGameRoom() == null){
            if(info.getPlayer() instanceof Player) {
                if(!((Player) info.getPlayer()).isOnline()){
                    return false;
                }
            }
            PlayerJoinRoomEvent event = new PlayerJoinRoomEvent(info,this,BedWarMain.getBedWarMain());
            event.setSend(sendMessage);
            Server.getInstance().getPluginManager().callEvent(event);
            if(event.isCancelled()){
                return false;
            }
            sendMessage(info+"&e加入了游戏 &7("+(playerInfos.size()+1)+"/"+getRoomConfig().getMaxPlayerSize()+")");
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
            return false;
        }
        return true;

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

            if (info.getPlayer() instanceof Player) {
                if (playerInfos.contains(info)) {

                    info.setLeave(true);


                    PlayerQuitRoomEvent event = new PlayerQuitRoomEvent(info, this, BedWarMain.getBedWarMain());
                    info.cancel();
                    if (teleport) {
                        info.getPlayer().teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
                    }

                    Server.getInstance().getPluginManager().callEvent(event);
                    ((Player) info.getPlayer()).setExperience(0, 0);
                    if (((Player) info.getPlayer()).isOnline()) {
                        BedWarMain.getRoomManager().playerJoin.remove(info.getPlayer().getName());
                    }
                } else {
                    if (((Player) info.getPlayer()).isOnline()) {
                        BedWarMain.getRoomManager().playerJoin.remove(info.getPlayer().getName());
                    }
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

            if(info.getPlayer() instanceof Player) {
                ((Player)info.getPlayer()).setGamemode(3);
            }
            info.init();
            info.setGameRoom(this);
            if(info.getPlayer() instanceof Player) {
                BedWarMain.getRoomManager().playerJoin.put(info.getPlayer().getName(),getRoomConfig().name);
            }
            playerInfos.add(info);

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
                if(isProtect((BlockBed) block)){
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
        for(BlockFace fence: BlockFace.values()){
            blocks.add(block.getSide(fence));
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


    /**
     * 关闭房间
     * */
    public void onDisable(){
        if(close){
            return;
        }
        close = true;
        GameCloseEvent event = new GameCloseEvent(this,BedWarMain.getBedWarMain());
        Server.getInstance().getPluginManager().callEvent(event);
        worldInfo.setClose(true);

        type = GameType.END;
        //TODO 房间被关闭 释放一些资源
        for(PlayerInfo info: playerInfos){
            info.clear();
            if(info.getPlayer() instanceof Player) {
                quitPlayerInfo(info,true);
            }

            if(info.getTeamInfo() != null) {
                info.getTeamInfo().breakBed();
            }
        }
        for(Block block:worldInfo.getPlaceBlock()){
            block.getLevel().setBlock(block,new BlockAir(),true,true);
        }
        for(Entity entity:worldInfo.getConfig()
                .getGameWorld()
                .getEntities()){
            if(entity instanceof Player){
                continue;
            }
            entity.close();
        }
        for(BlockEntity entity:worldInfo.getConfig()
                .getGameWorld()
                .getBlockEntities().values()){
            if(entity instanceof BlockEntityChest){
                ((BlockEntityChest) entity).getInventory().clearAll();
            }
        }
        worldInfo.getConfig().getGameWorld().doChunkGarbageCollection();
        worldInfo.getConfig().getGameWorld().unloadChunks(true);
        worldInfo = null;
        //TODO 从列表中移除
        BedWarMain.getRoomManager().getRooms().remove(roomConfig.getName());
        BedWarMain.sendMessageToConsole("&r释放房间 "+roomConfig.getName());
        if(WorldInfoConfig.toPathWorld(roomConfig.getName(),roomConfig.getWorldInfo().getGameWorld().getFolderName())){
            BedWarMain.sendMessageToConsole("&a"+roomConfig.getName()+" 地图已还原");
        }

        isGc = true;
        System.gc();
    }
}
