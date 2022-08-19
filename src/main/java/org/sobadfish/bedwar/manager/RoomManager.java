package org.sobadfish.bedwar.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.item.EntityPrimedTNT;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockBurnEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.*;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.event.inventory.InventoryTransactionEvent;
import cn.nukkit.event.level.ChunkUnloadEvent;
import cn.nukkit.event.level.WeatherChangeEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.form.window.FormWindowSimple;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.command.BedWarCommand;
import org.sobadfish.bedwar.entity.EntityFireBall;
import org.sobadfish.bedwar.event.*;
import org.sobadfish.bedwar.item.ItemIDSunName;
import org.sobadfish.bedwar.item.button.RoomQuitItem;
import org.sobadfish.bedwar.item.button.TeamChoseItem;
import org.sobadfish.bedwar.item.config.MoneyItemInfoConfig;
import org.sobadfish.bedwar.item.nbt.DieBow;
import org.sobadfish.bedwar.item.nbt.INbtItem;
import org.sobadfish.bedwar.panel.ChestInventoryPanel;
import org.sobadfish.bedwar.panel.DisPlayWindowsFrom;
import org.sobadfish.bedwar.panel.from.BedWarFrom;
import org.sobadfish.bedwar.panel.from.ShopFrom;
import org.sobadfish.bedwar.panel.from.button.BaseIButtom;
import org.sobadfish.bedwar.panel.items.BasePlayPanelItemInstance;
import org.sobadfish.bedwar.panel.items.NbtDefaultItem;
import org.sobadfish.bedwar.panel.items.PlayerItem;
import org.sobadfish.bedwar.player.PlayerData;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.GameRoom.GameType;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.entity.ShopVillage;
import org.sobadfish.bedwar.thread.FairworksRunnable;
import org.sobadfish.bedwar.tools.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author SoBadFish
 * 2022/1/2
 */
public class RoomManager implements Listener {

    public static List<GameRoomConfig> LOCK_GAME = new ArrayList<>();

    public LinkedHashMap<String,String> playerJoin = new LinkedHashMap<>();

    private RoomManager(Map<String, GameRoomConfig> roomConfig){
        this.roomConfig = roomConfig;
    }

    private GameRoom getGameRoomByLevel(Level level){
        for(GameRoom room : rooms.values()){
            if(room.getRoomConfig().worldInfo.getGameWorld() == null){
                continue;
            }
            if(room.getRoomConfig().worldInfo.getGameWorld().getFolderName().equalsIgnoreCase(level.getFolderName())){
                return room;
            }
        }
        return null;
    }

    public PlayerInfo getPlayerInfo(EntityHuman player){
        //TODO 获取游戏中的玩家
        if(playerJoin.containsKey(player.getName())) {
            String roomName = playerJoin.get(player.getName());
            if (!"".equalsIgnoreCase(roomName)) {
                if (rooms.containsKey(roomName)) {
                    return rooms.get(roomName).getPlayerInfo(player);
                }
            }
        }
        return null;
    }


    public static RoomManager initGameRoomConfig(File file){
        Map<String, GameRoomConfig> map = new LinkedHashMap<>();
        if(file.isDirectory()){
            File[] dirNameList = file.listFiles();
            if(dirNameList != null && dirNameList.length > 0) {
                for (File nameFile : dirNameList) {
                    if(nameFile.isDirectory()){
                        String roomName = nameFile.getName();
                        GameRoomConfig roomConfig = GameRoomConfig.getGameRoomConfigByFile(roomName,nameFile);
                        if(roomConfig != null){
                            BedWarMain.sendMessageToConsole("&a加载房间 "+roomName+" 完成");
                            map.put(roomName,roomConfig);

                        }else{
                            BedWarMain.sendMessageToConsole("&c加载房间 "+roomName+" 失败");

                        }
                    }
                }
            }
        }
        return new RoomManager(map);
    }



    public boolean joinRoom(PlayerInfo player,String roomName){
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player.getPlayer());
        if(info != null){
            player = info;
        }

        if (BedWarMain.getRoomManager().hasRoom(roomName)) {
            if (!BedWarMain.getRoomManager().hasGameRoom(roomName)) {
                if(!BedWarMain.getRoomManager().enableRoom(BedWarMain.getRoomManager().getRoomConfig(roomName))){
                    player.sendForceMessage("&c" + roomName + " 还没准备好");
                    return false;
                }
            }else{
                GameRoom room = BedWarMain.getRoomManager().getRoom(roomName);
                if(room != null){
                    if(RoomManager.LOCK_GAME.contains(room.getRoomConfig()) && room.getType() == GameType.END || room.getType() == GameType.CLOSE){
                        player.sendForceMessage("&c" + roomName + " 还没准备好");
                        return false;
                    }
                    if(room.getWorldInfo().getConfig().getGameWorld() == null){
                        return false;
                    }
                    if(room.getType() == GameType.END){
                        player.sendForceMessage("&c" + roomName + " 结算中");
                        return false;
                    }
                }
            }

            GameRoom room = BedWarMain.getRoomManager().getRoom(roomName);
            if(room == null){
                return false;
            }
            switch (room.joinPlayerInfo(player,true)){
                case CAN_WATCH:
                    if(!room.getRoomConfig().hasWatch){
                        player.sendForceMessage("&c该房间开始后不允许旁观");
                    }else{

                        if(player.getGameRoom() != null){
                            player.sendForceMessage("&c你无法进入此房间");
                        }else{
                            room.joinWatch(player);
                            return true;
                        }
                    }
                    break;
                case NO_LEVEL:
                    player.sendForceMessage("&c这个房间正在准备中，稍等一会吧");
                    break;
                case NO_ONLINE:
                    break;
                case NO_JOIN:
                    player.sendForceMessage("&c该房间不允许加入");
                    break;
                default:
                    //可以加入
                    return true;
            }
        } else {
            player.sendForceMessage("&c不存在 &r" + roomName + " &c房间");

        }
        return false;
    }


    private final Map<String, GameRoomConfig> roomConfig;

    public Map<String, GameRoom> getRooms() {
        return rooms;
    }

    private Map<String, GameRoom> rooms = new LinkedHashMap<>();

    public boolean hasRoom(String room){
        return roomConfig.containsKey(room);
    }

    public boolean hasGameRoom(String room){
        return rooms.containsKey(room);
    }

    public boolean enableRoom(GameRoomConfig config){
        if(config.getWorldInfo().getGameWorld() == null){
            return false;
        }
        if(!RoomManager.LOCK_GAME.contains(config)){
            RoomManager.LOCK_GAME.add(config);

            GameRoom room = GameRoom.enableRoom(config);
            if(room == null){
                return false;
            }
            rooms.put(config.getName(),room);
            return true;
        }else{

            return false;
        }

    }

    public GameRoomConfig getRoomConfig(String name){
        return roomConfig.getOrDefault(name,null);
    }

    public List<GameRoomConfig> getRoomConfigs(){
        return new ArrayList<>(roomConfig.values());
    }

    public GameRoom getRoom(String name){
        GameRoom room = rooms.getOrDefault(name,null);
        if(room == null || room.worldInfo == null){
            return null;
        }

        if(room.getWorldInfo().getConfig().getGameWorld() == null){
            return null;
        }
        return room;
    }

    public void disEnableRoom(String name){
        if(rooms.containsKey(name)){
            rooms.get(name).onDisable();

        }
    }

    @EventHandler
    public void onTeamDefeat(TeamDefeatEvent event){

        final GameRoom room = event.getRoom();
        for (PlayerInfo info:event.getTeamInfo().getInRoomPlayer()) {
            PlayerData data = BedWarMain.getDataManager().getData(info.getName());
            data.getRoomData(event.getRoom().getRoomConfig().name).defeatCount++;
            data.getRoomData(event.getRoom().getRoomConfig().name).gameCount++;
            room.getRoomConfig().defeatCommand.forEach(cmd->Server.getInstance().dispatchCommand(new ConsoleCommandSender(),cmd.replace("@p",info.getName())));
            if(event.getRoom().getRoomConfig().isAutomaticNextRound){
                info.sendMessage("&7即将自动进行下一局");
                RandomJoinManager.joinManager.join(info,null);
//                ThreadManager.addThread(new AutoJoinGameRoomRunnable(5,info,event.getRoom(),null));

            }

        }
    }

    @EventHandler
    public void onExecuteCommand(PlayerCommandPreprocessEvent event){
        PlayerInfo info = getPlayerInfo(event.getPlayer());
        if(info != null){
            GameRoom room = info.getGameRoom();
            if(room != null) {
                for(String cmd: room.getRoomConfig().banCommand){
                    if(event.getMessage().contains(cmd)){
                        event.setCancelled();
                    }
                }
            }
        }

    }


    @EventHandler
    public void onTeamVictory(TeamVictoryEvent event){
        event.getTeamInfo().sendTitle("&e&l胜利!",5);
        String line = "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■";
        event.getRoom().sendTipMessage("&a"+line);
        event.getRoom().sendTipMessage(Utils.getCentontString("&b游戏结束",line.length()));
        event.getRoom().sendTipMessage("");
        for(PlayerInfo playerInfo: event.getTeamInfo().getInRoomPlayer()){
            event.getRoom().sendTipMessage(Utils.getCentontString("&7   "+playerInfo.getPlayer().getName()+" 击杀： - "+(playerInfo.getKillCount()+playerInfo.getEndKillCount())+" 破坏床数: - "+playerInfo.getBedBreakCount(),line.length()));
        }
        event.getRoom().sendTipMessage("&a"+line);
        for (PlayerInfo info:event.getTeamInfo().getInRoomPlayer()) {
            PlayerData data = BedWarMain.getDataManager().getData(info.getName());
            data.getRoomData(event.getRoom().getRoomConfig().name).victoryCount++;
            data.getRoomData(event.getRoom().getRoomConfig().name).gameCount++;
            event.getRoom().getRoomConfig().victoryCommand.forEach(cmd->Server.getInstance().dispatchCommand(new ConsoleCommandSender(),cmd.replace("@p",info.getName())));
        }
        TeamInfo teamInfo = event.getTeamInfo();
        if(teamInfo != null){
            ThreadManager.addScheduled(new FairworksRunnable(5,teamInfo.getLivePlayer()));
        }

        event.getRoom().sendMessage("&a恭喜 "+event.getTeamInfo().getTeamConfig().getNameColor()+event.getTeamInfo().getTeamConfig().getName()+" &a 获得了胜利!");

    }

    //事件响应
    @EventHandler
    public void onQuitRoom(PlayerQuitRoomEvent event){
        if(event.performCommand){
            PlayerInfo info = event.getPlayerInfo();
            PlayerData data = BedWarMain.getDataManager().getData(info.getName());
            data.setInfo(info);
            GameRoom room = event.getRoom();
            info.clear();
            if(info.getPlayer() instanceof Player){
                ((Player)info.getPlayer()).setFoodEnabled(false);
                room.getRoomConfig().quitRoomCommand.forEach(cmd-> Server.getInstance().dispatchCommand(((Player)info.getPlayer()),cmd));
            }
            if(info.isWatch()){
                return;
            }
            room.sendMessage("&c玩家 "+event.getPlayerInfo().getPlayer().getName()+" 离开了游戏");
        }
    }

//    /**
//     * 阻止区块卸载 如果区块卸载会出现如下问题
//     *
//     * 1. 还原房间部分方块无法还原
//     * 2. 导致后台循环报错空指针异常
//     * */
//    @EventHandler
//    public void onChunkUnload(ChunkUnloadEvent event){
//        GameRoom room = getGameRoomByLevel(event.getLevel());
//        if(room != null && !room.close){
//            event.setCancelled();
//
//        }
//    }



    @EventHandler
    public void onRoomClose(GameCloseEvent event){
        //TODO 写入
        event.getRoom().getRoomConfig().save();

    }

    @EventHandler
    public void onPlayerJoinRoom(PlayerJoinRoomEvent event){
        PlayerInfo info = event.getPlayerInfo();
        GameRoom gameRoom = event.getRoom();
        if (BedWarMain.getRoomManager().playerJoin.containsKey(info.getPlayer().getName())) {
            String roomName = BedWarMain.getRoomManager().playerJoin.get(info.getPlayer().getName());
            if (roomName.equalsIgnoreCase(event.getRoom().getRoomConfig().name) && gameRoom.getPlayerInfos().contains(info)) {
                if(event.isSend()) {
                    info.sendForceMessage("&c你已经在这个房间内了");
                }
                event.setCancelled();
                return;
            }
            if (BedWarMain.getRoomManager().hasGameRoom(roomName)) {
                GameRoom room = BedWarMain.getRoomManager().getRoom(roomName);
                if (room.getType() != GameRoom.GameType.END && room.getPlayerInfos().contains(info)) {
                    if (room.getPlayerInfo((Player) info.getPlayer()).getPlayerType() != PlayerInfo.PlayerType.WATCH ||
                            room.getPlayerInfo((Player) info.getPlayer()).getPlayerType() != PlayerInfo.PlayerType.LEAVE) {
                        if(event.isSend()) {
                            info.sendForceMessage("&c你已经在游戏房间内了");
                        }
                        event.setCancelled();

                    }
                }
            }
        }
        if(gameRoom.getType() != GameRoom.GameType.WAIT){
            if(GameType.END != gameRoom.getType()){
                //TODO 或许还能旁观
                if(gameRoom.getRoomConfig().hasWatch){
                    event.setCancelled();
                    return;
                }

            }
            if(event.isSend()) {
                info.sendForceMessage("&c游戏已经开始了");
            }
            event.setCancelled();
            return;
        }
        if(gameRoom.getPlayerInfos().size() == gameRoom.getRoomConfig().getMaxPlayerSize()){
            if(event.isSend()) {
                info.sendForceMessage("&c房间满了");
            }
            event.setCancelled();
        }
        if(info.getPlayer() instanceof Player) {
            ((Player) info.getPlayer()).setFoodEnabled(false);
            ((Player) info.getPlayer()).setGamemode(2);
        }

    }
    @EventHandler
    public void onCraft(CraftItemEvent event){
        Player player = event.getPlayer();
        GameRoom room = getGameRoomByLevel(player.getLevel());
        if(room != null) {
            PlayerInfo info = room.getPlayerInfo(player);
            if (info != null) {
                event.setCancelled();
            }
        }
    }



    /**
     * 游戏地图的爆炸保护
     * */

    @EventHandler
    public void onEntityExplodeEvent(EntityExplodeEvent event){
        Level level = event.getPosition().getLevel();
        GameRoom room = getGameRoomByLevel(level);
        if(room != null) {
            ArrayList<Block> blocks = new ArrayList<>(event.getBlockList());
            for (Block block : event.getBlockList()) {
                if (!room.worldInfo.getPlaceBlock().contains(block)) {
                    blocks.remove(block);

                }else{
                    room.worldInfo.getPlaceBlock().remove(block);
                }
            }
            event.setBlockList(blocks);
        }
    }
    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event){
        Level level = event.getBlock().level;

        Block block = event.getBlock();
        Item item = event.getItem();
        if(item.hasCompoundTag() && (item.getNamedTag().contains("quitItem")
                || item.getNamedTag().contains("choseTeam"))){
            event.setCancelled();
            return;
        }
        GameRoom room = getGameRoomByLevel(level);
        if(room != null){
            PlayerInfo info = room.getPlayerInfo(event.getPlayer());
            if(info != null) {
                if (info.isWatch()) {
                    info.sendMessage("&c观察状态下不能放置方块");
                    event.setCancelled();
                    return;
                }
                if (item.getId() == 65 && !room.getWorldInfo().getPlaceBlock().contains(event.getBlockAgainst())) {
                    info.sendMessage("&c你只能将梯子放置在玩家放置的方块上");
                    event.setCancelled();
                    return;
                } else {
                    if (block instanceof BlockTNT) {
                        event.setCancelled();
                        ((BlockTNT) block).prime(40);
                        Item i2 = item.clone();
                        i2.setCount(1);
                        event.getPlayer().getInventory().removeItem(i2);
                        return;
                    }

                }
                if (!room.worldInfo.onChangeBlock(block, true)) {
                    info.sendMessage("&c你不能在这里放置方块");

                    event.setCancelled();
                }
            }
        }


    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(PlayerChatEvent event){
        PlayerInfo info = getPlayerInfo(event.getPlayer());
        if(info != null){
            GameRoom room = info.getGameRoom();
            if(room != null){
                if(info.isWatch()){
                    room.sendMessageOnWatch(info+" &r>> "+event.getMessage());
                }else{
                    String msg = event.getMessage();
                    if(msg.startsWith("@") || msg.startsWith("!")){
                        info.getGameRoom().sendFaceMessage("&l&7(全体消息)&r "+info+"&r >> "+msg.substring(1));
                    }else{
                        TeamInfo teamInfo = info.getTeamInfo();
                        if(teamInfo != null){
                            if(info.isDeath()){
                                room.sendMessageOnDeath(info+"&7(死亡) &r>> "+msg);
                            }else {
                                teamInfo.sendMessage(teamInfo.getTeamConfig().getNameColor() + "[队伍]&7 " + info.getPlayer().getName() + " &f>>&r " + msg);
                            }
                        }else{
                            room.sendMessage(info+" &f>>&r "+msg);
                        }
                    }

                }

                event.setCancelled();
            }
        }
    }



    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event){
        Level level = event.getBlock().level;
        Block block = event.getBlock();
        Player player = event.getPlayer();
        GameRoom room = getGameRoomByLevel(level);
        if(room != null){
            PlayerInfo info = room.getPlayerInfo(player);
            if(info == null){
                if(!player.isOp()) {
                    player.sendMessage("你不能破坏此方块");
                    event.setCancelled();
                }
            }else{
                if(block instanceof BlockEnderChest){
                    event.setDrops(new Item[0]);
                }
                if(info.isWatch()){
                    player.sendMessage("&c观察状态下不能破坏方块");
                    event.setCancelled();
                    return;
                }
                if(block instanceof BlockBed){
                    event.setDrops(new Item[0]);
                }

                if(!room.toBreakBlock(info,block)){
                    event.setCancelled();
                }else{
                    //防止一些无敌方块
                    event.getBlock().getLevel().setBlock(event.getBlock(),Block.get(0),true);
                }

            }
        }

    }






    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        //TODO 断线重连 上线
        Player player = event.getPlayer();
        if(playerJoin.containsKey(player.getName())){
            player.setFoodEnabled(false);
            player.setGamemode(2);
            String room = playerJoin.get(player.getName());
            if(hasGameRoom(room)){
                GameRoom room1 = getRoom(room);
                if(room1 == null){
                    playerJoin.remove(player.getName());
                    player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
                    return;
                }
                if(room1.getType() != GameRoom.GameType.END && !room1.close ){
                    PlayerInfo info = room1.getPlayerInfo(player);
                    if(info != null){
                        info.setPlayer(player);
                        info.setLeave(false);
                        if(room1.getType() == GameRoom.GameType.WAIT){
                            if(room1.worldInfo.getConfig().getGameWorld() != null){
                                player.teleport(room1.worldInfo.getConfig().getGameWorld().getSafeSpawn());
                                player.teleport(room1.getWorldInfo().getConfig().getWaitPosition());
                            }

                        }else{

                            info.death(null);
                        }
                    }else{
                        reset(player);
                    }

                }else{
                    reset(player);
                }
            }else{
                //TODO 无房间回到出生点
               reset(player);
            }
        }else if(player.getGamemode() == 3){
            player.setGamemode(0);
        }

    }

    private void reset(Player player){
        playerJoin.remove(player.getName());
        player.setHealth(player.getMaxHealth());
        player.getInventory().clearAll();
        player.removeAllEffects();
        player.setGamemode(0);
        player.getEnderChestInventory().clearAll();
        player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
    }

    @EventHandler
    public void onGameStartEvent(GameRoomStartEvent event){
        GameRoom room = event.getRoom();
        String line = "■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■■";
        for(String s: room.getRoomConfig().gameStartMessage){
            room.sendTipMessage(Utils.getCentontString(s,line.length()));
        }
    }


    /**
     * 将拾取的物品转换为经验
     * @param event 事件
     */
    @EventHandler
    public void onItemPickUp(InventoryPickupItemEvent event){
        Inventory inventory = event.getInventory();
        if(inventory instanceof PlayerInventory){
            EntityHuman entityHuman = (EntityHuman) inventory.getHolder();
            if(entityHuman instanceof Player){
                Player player = (Player) entityHuman;
                GameRoom room = getGameRoomByLevel(entityHuman.level);
                double exp = 0.0;
                if(room != null && room.getType() == GameType.START){
                    PlayerInfo playerInfo = room.getPlayerInfo(player);
                    if(playerInfo != null){
                        if(room.getRoomConfig().isExp()){
                            for (MoneyItemInfoConfig config : room.getRoomConfig().moneyItem.getItemInfoConfigs()) {
                                if(config.getItem().equals(event.getItem().getItem(), true, true)){
                                    exp += event.getItem().getItem().count * config.getExp();
                                    break;
                                }
                            }
                            if(exp > 0){
                                event.setCancelled();
                                event.getItem().close();
                                playerInfo.addExp((int)Math.floor(exp));
                            }
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityClick(PlayerInteractEntityEvent event){
        Player player = event.getPlayer();
        if(event.getEntity() instanceof ShopVillage){
            event.setCancelled();
            PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
            if(info.isWatch()){
                return;
            }
            ((ShopVillage) event.getEntity()).onClick(info);
        }


    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event){
        Block block = event.getBlock();
        for(GameRoomConfig gameRoomConfig: BedWarMain.getRoomManager().roomConfig.values()){
            if(gameRoomConfig.worldInfo.getGameWorld() == null){
                continue;
            }
            if(gameRoomConfig.worldInfo.getGameWorld().
                    getFolderName().equalsIgnoreCase(block.getLevel().getFolderName())){
                event.setCancelled();
                return;
            }
        }

    }

    @EventHandler
    public void onLevelTransfer(EntityLevelChangeEvent event){
        Entity entity = event.getEntity();
        GameRoom room = getGameRoomByLevel(event.getTarget());
        if(room != null){
            if(room.getType() == GameRoom.GameType.START){
                //防止错杀玩家
                if(entity instanceof Player){
                    PlayerInfo info = room.getPlayerInfo((Player) entity);
                    if(info != null){
                        return;
                    }
                }

                event.setCancelled();
                BedWarMain.sendMessageToObject("&c你无法进入该地图",entity);
            }
        }

    }
    @EventHandler(ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event){
        for(GameRoomConfig gameRoomConfig: BedWarMain.getRoomManager().roomConfig.values()){
            if(gameRoomConfig.getWorldInfo().getGameWorld() != null){
                if(gameRoomConfig.worldInfo.getGameWorld().
                        getFolderName().equalsIgnoreCase(event.getLevel().getFolderName())){
                    event.setCancelled();
                    return;
                }
            }

        }
    }



    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof ShopVillage) {
            if(event instanceof EntityDamageByEntityEvent){
                Entity entity = ((EntityDamageByEntityEvent) event).getDamager();
                if(entity == null){
                    return;
                }
                if(entity instanceof Player) {
                    if(entity.distance(event.getEntity()) <= 4 && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
                        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo((Player) entity);
                        if (info.isWatch()) {
                            event.setCancelled();
                            return;
                        }
                        ((ShopVillage) event.getEntity()).onClick(info);
                    }

                }
            }
            event.setCancelled();
            return;
        }
        if(event.getEntity() instanceof Player){
            PlayerInfo playerInfo = getPlayerInfo((EntityHuman) event.getEntity());
            if(playerInfo != null) {
                if (playerInfo.isWatch()) {
                    playerInfo.sendForceMessage("&c你处于观察者模式");
                    event.setCancelled();
                    return;
                }
                GameRoom room = playerInfo.getGameRoom();
                if (room.getType() == GameRoom.GameType.WAIT) {

                    event.setCancelled();
                    return;
                }
                //会重复
                if (playerInfo.getPlayerType() == PlayerInfo.PlayerType.WAIT) {
                    event.setCancelled();
                    return;
                }
                if (event instanceof EntityDamageByEntityEvent) {
                    if (((EntityDamageByEntityEvent) event).getDamager() instanceof EntityFireBall) {
                        event.setDamage(2);
                        ((EntityDamageByEntityEvent) event).setKnockBack(room.getRoomConfig().fireballKnockBack);
                    }
                }
                if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE) {
                    if (event instanceof EntityDamageByEntityEvent) {
                        Entity damagers = (((EntityDamageByEntityEvent) event).getDamager());
                        if (damagers instanceof Player) {
                            PlayerInfo playerInfo1 = BedWarMain.getRoomManager().getPlayerInfo((Player) damagers);
                            if (playerInfo1 != null) {
                                playerInfo1.addSound(Sound.RANDOM_ORB);
                                double h = event.getEntity().getHealth() - event.getFinalDamage();
                                if (h < 0) {
                                    h = 0;
                                }
                                playerInfo1.sendTip("&e目标: &c❤" + String.format("%.1f", h));
                            }

                        }
                    }
                }
                if (event instanceof EntityDamageByEntityEvent) {
                    //TODO 免受TNT爆炸伤害
                    Entity entity = ((EntityDamageByEntityEvent) event).getDamager();
                    if (entity instanceof EntityPrimedTNT) {
                        event.setDamage(2);
                    }
                    //TODO 阻止队伍PVP
                    if (entity instanceof Player) {
                        PlayerInfo damageInfo = room.getPlayerInfo((Player) entity);
                        if (damageInfo != null) {
                            if (damageInfo.isWatch()) {
                                event.setCancelled();
                                return;
                            }
                            TeamInfo t1 = playerInfo.getTeamInfo();
                            TeamInfo t2 = damageInfo.getTeamInfo();
                            if (t1 != null && t2 != null) {
                                if (t1.getTeamConfig().getName().equalsIgnoreCase(t2.getTeamConfig().getName())) {
                                    event.setCancelled();
                                    return;
                                }
                            }
                            playerInfo.setDamageByInfo(damageInfo);
                        } else {
                            event.setCancelled();
                        }
                    }

                }
                if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                    event.setCancelled();
                    playerInfo.death(event);
                }
                if (event.getFinalDamage() + 1 > playerInfo.getPlayer().getHealth()) {
                    event.setCancelled();
                    playerInfo.death(event);
                    for (EntityDamageEvent.DamageModifier modifier : EntityDamageEvent.DamageModifier.values()) {
                        event.setDamage(0, modifier);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event){
        //TODO 断线重连 - 离线状态下
        Player player = event.getPlayer();
        if(playerJoin.containsKey(player.getName())){
            String roomName = playerJoin.get(player.getName());
            GameRoom room = getRoom(roomName);
            if(room != null){
                if(room.getType() != GameRoom.GameType.START ){
                    PlayerInfo info = room.getPlayerInfo(player);
                    if(info != null){
                        room.quitPlayerInfo(info,true);
                    }

                }else{
                    PlayerInfo info = room.getPlayerInfo(player);
                    if(info != null){
                        if(info.isWatch()){
                            room.quitPlayerInfo(info,true);
                            return;
                        }
                        player.getInventory().clearAll();
                        info.setLeave(true);
                    }
                }
            }
        }
    }

    public static final double BOW_MAX = 2.7;
    //2.7就可以触发
    @EventHandler
    public void onBow(EntityShootBowEvent event){
        if(event.getForce() >= BOW_MAX) {
            Entity entity = event.getEntity();
            if (entity instanceof Player) {
                Player player = (Player) entity;
                if (playerJoin.containsKey(player.getName())) {
                    String roomName = playerJoin.get(player.getName());
                    GameRoom room = getRoom(roomName);
                    if (room != null) {
                        Item item = event.getBow();
                        if (item.hasCompoundTag() && item.getNamedTag().contains(NbtDefaultItem.TAG)) {
                            String name = item.getNamedTag().getString(NbtDefaultItem.TAG);
                            if (room.getRoomConfig().nbtItemInfo.items.containsKey(name)) {
                                INbtItem iNbtItem = room.getRoomConfig().nbtItemInfo.items.get(name).name;
                                if(iNbtItem instanceof DieBow){
                                    event.getProjectile().close();
                                    ((DieBow) iNbtItem).onSend(player);
                                }
                            }
                        }
                    }
                }
            }
        }
    }



    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
            Item item = event.getItem();
            if(event.getBlock() instanceof BlockCraftingTable || event.getBlock() instanceof BlockBed){
                if(BedWarMain.getRoomManager().getPlayerInfo(event.getPlayer()) != null){
                    event.setCancelled();
                    return;
                }
            }
            if (playerJoin.containsKey(player.getName())) {
                String roomName = playerJoin.get(player.getName());
                GameRoom room = getRoom(roomName);
                if (room != null) {
                    if(item.hasCompoundTag() && item.getNamedTag().getBoolean("quitItem")){
                        event.setCancelled();
                        quitRoomItem(player, roomName, room);
                        return;
                    }
                    if(item.hasCompoundTag() && item.getNamedTag().getBoolean("follow")){
                        followPlayer(room.getPlayerInfo(player),room);
                        event.setCancelled();
                        return;
                    }

                    if(item.hasCompoundTag() && item.getNamedTag().getBoolean("choseTeam")){
                        event.setCancelled();
                        choseteamItem(player, room);
                        return;

                    }
                    if (item.hasCompoundTag() && item.getNamedTag().contains(NbtDefaultItem.TAG)) {
                        String name = item.getNamedTag().getString(NbtDefaultItem.TAG);

                        if(event.getBlock() instanceof BlockChest){
                            if(!room.getClickChest().contains((BlockChest) event.getBlock())) {
                                room.getClickChest().add((BlockChest) event.getBlock());
                            }
                            return;
                        }
                        Item r = item.clone();
                        r.setCount(1);
                        if (room.getRoomConfig().nbtItemInfo.items.containsKey(name)) {
                            INbtItem iNbtItem = room.getRoomConfig().nbtItemInfo.items.get(name).name;
                            if(iNbtItem.onClick(r, player)){
                                event.setCancelled();
                            }
                        }
                    }

                }
            }
        }

    }

    private boolean choseteamItem(Player player, GameRoom room) {
        if(!TeamChoseItem.clickAgain.contains(player)){
            TeamChoseItem.clickAgain.add(player);
            player.sendTip("请再点击一次");
            return true;
        }
        FormWindowSimple simple = new FormWindowSimple("请选择队伍","");
        for(TeamInfo teamInfoConfig: room.getTeamInfos()){
            Item wool = teamInfoConfig.getTeamConfig().getTeamConfig().getBlockWoolColor();
            simple.addButton(new ElementButton(TextFormat.colorize('&',teamInfoConfig.toString()+" &r"+teamInfoConfig.getTeamPlayers().size()+" / "+(room.getRoomConfig().getMaxPlayerSize() / room.getTeamInfos().size())),
                    new ElementButtonImageData("path",
                            ItemIDSunName.getIDByPath(wool.getId(),wool.getDamage()))));
        }
        player.showFormWindow(simple,102);
        TeamChoseItem.clickAgain.remove(player);
        return false;
    }

    private void followPlayer(PlayerInfo info,GameRoom room){
        info.sendMessage("选择要传送的玩家");
        switch(room.getRoomConfig().uiType){
            case UI:
                disPlayUI(info, room);
                break;
            case AUTO:
                if (((Player) info.getPlayer()).getLoginChainData().getDeviceOS() == 7){
                    disPlayUI(info, room);
                }else{
                    disPlayProtect(info, room);
                }
                break;
            case PACKET:
                disPlayProtect(info, room);
                break;
                default:break;
        }

    }

    private void disPlayProtect(PlayerInfo info,GameRoom room){
        List<BaseIButtom> list = new ArrayList<>();
        //手机玩家
        for(PlayerInfo i: room.getLivePlayers()){
            list.add(new BaseIButtom(new PlayerItem(i).getGUIButton(info)) {
                @Override
                public void onClick(Player player) {
                    player.teleport(i.getPlayer().getLocation());
                }
            });
        }
        DisPlayWindowsFrom.disPlayerCustomMenu((Player) info.getPlayer(),"传送玩家",list);

    }


    private void disPlayUI(PlayerInfo info,GameRoom room){
        //WIN10 玩家 故障，，，，
//        DisPlayerPanel playerPanel = new DisPlayerPanel();
//        playerPanel.displayPlayer(info,DisPlayerPanel.displayPlayers(room),"传送玩家");

        disPlayProtect(info, room);
    }

    private boolean quitRoomItem(Player player, String roomName, GameRoom room) {
        if(!RoomQuitItem.clickAgain.contains(player)){
            RoomQuitItem.clickAgain.add(player);
            player.sendTip("请再点击一次");
            return true;
        }
        RoomQuitItem.clickAgain.remove(player);
        if(room.quitPlayerInfo(room.getPlayerInfo(player),true)){
            player.sendMessage("你成功离开房间 "+roomName);
        }
        return false;
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if(playerJoin.containsKey(player.getName())) {
            String roomName = playerJoin.get(player.getName());
            GameRoom room = getRoom(roomName);
            if (room != null) {
                Item item = event.getItem();
                if (item.hasCompoundTag() && item.getNamedTag().getBoolean("quitItem") || item.hasCompoundTag() && item.getNamedTag().getBoolean("choseTeam")) {
                    event.setCancelled();
                }
            }
        }
    }

    @EventHandler
    public void onPlayerItemHeldEvent(PlayerItemHeldEvent event){
        Player player = event.getPlayer();
        if(playerJoin.containsKey(player.getName())){
            String roomName = playerJoin.get(player.getName());
            GameRoom room = getRoom(roomName);
            if(room != null){
                Item item = event.getItem();
                if(item.hasCompoundTag() && item.getNamedTag().getBoolean("quitItem")){
                    player.getInventory().setHeldItemSlot(0);
                    if (quitRoomItem(player, roomName, room)) {
                        return;
                    }
                }
                if(item.hasCompoundTag() && item.getNamedTag().getBoolean("choseTeam")){
                    player.getInventory().setHeldItemSlot(0);
                    choseteamItem(player, room);


                }
                if(item.hasCompoundTag() && item.getNamedTag().getBoolean("follow")){
                    followPlayer(room.getPlayerInfo(player),room);
                    player.getInventory().setHeldItemSlot(0);
                }
            }
        }
    }

    @EventHandler
    public void onFrom(PlayerFormRespondedEvent event){
        if(event.wasClosed()){
            DisPlayWindowsFrom.SHOP.remove(event.getPlayer().getName());
            BedWarCommand.FROM.remove(event.getPlayer().getName());
            DisPlayWindowsFrom.CUSTOM.remove(event.getPlayer().getName());
            return;
        }
        Player player = event.getPlayer();
        if(DisPlayWindowsFrom.CUSTOM.containsKey(player.getName())){
            BedWarFrom simple = DisPlayWindowsFrom.CUSTOM.get(player.getName());
            if (onBedWarFrom(event, player, simple)) {
                return;
            }

        }
        if(BedWarCommand.FROM.containsKey(player.getName())){
            BedWarFrom simple = BedWarCommand.FROM.get(player.getName());
            if (onBedWarFrom(event, player, simple)) {
                return;
            }

        }

        int fromId = 102;
        if(event.getFormID() == fromId && event.getResponse() instanceof FormResponseSimple){
            PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
            if(info != null){
                if(info.getGameRoom() == null || info.getGameRoom().getType() == GameType.START){
                    return;
                }
                TeamInfo teamInfo = info.getGameRoom().getTeamInfos().get(((FormResponseSimple) event.getResponse())
                        .getClickedButtonId());
                if(!teamInfo.join(info)){
                    info.sendMessage("&c你已经加入了 "+teamInfo.toString());
                }else{
                    info.sendMessage("&a加入了&r"+teamInfo.toString()+" &a成功");
                }
            }
            return;
        }

        if(DisPlayWindowsFrom.SHOP.containsKey(player.getName())){
            if(BedWarMain.getRoomManager().getPlayerInfo(player) != null) {
                ShopFrom shopFrom = DisPlayWindowsFrom.SHOP.get(player.getName());
                if(shopFrom == null){
                    DisPlayWindowsFrom.SHOP.remove(player.getName());
                    return;
                }
                if (event.getResponse() instanceof FormResponseSimple) {
                    if (((FormResponseSimple) event.getResponse())
                            .getClickedButtonId() == shopFrom.getShopButtons().size()) {
                        if (shopFrom.getLastFrom() != null) {
                            shopFrom.getLastFrom().disPlay(shopFrom.getLastFrom().getTitle(), false);
                        }

                        return;
                    }
                    if(((FormResponseSimple) event.getResponse()).getClickedButtonId() >= shopFrom.getShopButtons().size()){
                        return;
                    }
                    shopFrom.getShopButtons().get(((FormResponseSimple) event.getResponse())
                            .getClickedButtonId()).getItemInstance().onClickButton(player, shopFrom);

                }
            }else{
                DisPlayWindowsFrom.SHOP.remove(player.getName());
            }
        }
    }

    private boolean onBedWarFrom(PlayerFormRespondedEvent event, Player player, BedWarFrom simple) {
        if(simple.getId() == event.getFormID()) {
            if (event.getResponse() instanceof FormResponseSimple) {
                BaseIButtom button = simple.getBaseIButtoms().get(((FormResponseSimple) event.getResponse())
                        .getClickedButtonId());
                button.onClick(player);
            }
            return true;

        }else{
            BedWarCommand.FROM.remove(player.getName());
        }
        return false;
    }

    @EventHandler
    public void onItemChange(InventoryTransactionEvent event) {

        InventoryTransaction transaction = event.getTransaction();
        for (InventoryAction action : transaction.getActions()) {
            for (Inventory inventory : transaction.getInventories()) {
                if (inventory instanceof ChestInventoryPanel) {
                    Player player = ((ChestInventoryPanel) inventory).getPlayer();
                    event.setCancelled();
                    Item i = action.getSourceItem();
                    if(i.hasCompoundTag() && i.getNamedTag().contains("index")){
                        int index = i.getNamedTag().getInt("index");
                        BasePlayPanelItemInstance item = ((ChestInventoryPanel) inventory).getPanel().getOrDefault(index,null);

                        if(item != null){
                            ((ChestInventoryPanel) inventory).clickSolt = index;
                            item.onClick((ChestInventoryPanel) inventory,player);
                            ((ChestInventoryPanel) inventory).update();
                        }
                    }

                }
            }
        }
    }
}
