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
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.GameRoom.GameType;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.entity.ShopVillage;
import org.sobadfish.bedwar.thread.BaseTimerRunnable;
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

    public LinkedHashMap<String,String> playerJoin = new LinkedHashMap<>();

    private RoomManager(Map<String, GameRoomConfig> roomConfig){
        this.roomConfig = roomConfig;
    }

    private GameRoom getGameRoomByLevel(Level level){
//        if(getPlayerInfo(player) != null){
//            return getPlayerInfo(player).getGameRoom();
//        }
//        return null;
        for(GameRoom room : rooms.values()){
            if(room.getRoomConfig().worldInfo.getGameWorld().getFolderName().equalsIgnoreCase(level.getFolderName())){
                return room;
            }
        }
        return null;
    }

    public PlayerInfo getPlayerInfo(EntityHuman player){
        //TODO ????????????????????????
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
                            BedWarMain.sendMessageToConsole("&a???????????? "+roomName+" ??????");
                            map.put(roomName,roomConfig);
                            ////????????????
                            File world = new File(nameFile+File.separator+"world"+File.separator+roomConfig.worldInfo.getGameWorld().getFolderName());
                            if(world.exists() && world.isDirectory()){

                                if(toPathWorld(roomConfig)){
                                    BedWarMain.sendMessageToConsole("&a?????? &e"+roomConfig.worldInfo.getGameWorld().getFolderName()+" &a???????????????");
                                }else{
                                    BedWarMain.sendMessageToConsole("&c?????? &e"+roomConfig.worldInfo.getGameWorld().getFolderName()+" &ac???????????????,???????????????????????????");
                                    map.remove(roomName);
                                }
                            }else{
                                if(toBackUpWorld(roomConfig)){
                                    BedWarMain.sendMessageToConsole("&a???????????? &e"+roomConfig.worldInfo.getGameWorld().getFolderName()+" &a??????");
                                }else{
                                    BedWarMain.sendMessageToConsole("&c???????????? &e"+roomConfig.worldInfo.getGameWorld().getFolderName()+" &c??????");
                                }
                            }
                        }else{
                            BedWarMain.sendMessageToConsole("&c???????????? "+roomName+" ??????");

                        }
                    }
                }
            }
        }
        return new RoomManager(map);
    }

    public static boolean toBackUpWorld(GameRoomConfig roomConfig){
        File nameFile = new File(BedWarMain.getBedWarMain().getDataFolder()+File.separator+"rooms"+File.separator+roomConfig.getName());
        File world = new File(nameFile+File.separator+"world"+File.separator+roomConfig.worldInfo.getGameWorld().getFolderName());
        if(!world.exists()){
            world.mkdirs();
        }

        //
        return Utils.copyFiles(new File(Server.getInstance().getFilePath()+File.separator + "worlds" +File.separator+ roomConfig.worldInfo.getGameWorld().getFolderName()), world);
    }


    public static boolean toPathWorld(GameRoomConfig roomConfig){
        File nameFile = new File(BedWarMain.getBedWarMain().getDataFolder()+File.separator+"rooms"+File.separator+roomConfig.getName());
        File world = new File(nameFile+File.separator+"world"+File.separator+roomConfig.worldInfo.getGameWorld().getFolderName());
        if(world.isDirectory() && world.exists()){
            File[] files = world.listFiles();
            if(files != null && files.length > 0){
//                Utils.toDelete(new File(Server.getInstance().getFilePath()+File.separator+"worlds"+File.separator+roomConfig.worldInfo.getGameWorld().getFolderName()));
                Utils.copyFiles(world,new File(Server.getInstance().getFilePath()+File.separator+"worlds"+File.separator+roomConfig.worldInfo.getGameWorld().getFolderName()));
                return Server.getInstance().loadLevel(roomConfig.worldInfo.getGameWorld().getFolderName());
            }

        }
        return false;
        //???????????? ???????????????????????????


    }

    public boolean joinRoom(PlayerInfo player,String roomName){
        if (BedWarMain.getRoomManager().hasRoom(roomName)) {
            if (!BedWarMain.getRoomManager().hasGameRoom(roomName)) {
                BedWarMain.getRoomManager().enableRoom(BedWarMain.getRoomManager().getRoomConfig(roomName));
            }
            if (!BedWarMain.getRoomManager().getRoom(roomName).joinPlayerInfo(player,true)) {
                player.sendForceMessage("&c??????????????????");
            }else{
                return true;
            }


        } else {
            player.sendForceMessage("&c????????? &r" + roomName + " &c??????");

        }
        return false;
    }

//    public void joinRobot(Player cmdPlayer,String roomName,int count){
//        Skin skin = BedWarMain.skin;
//
//        ThreadManager.addThread(() -> {
//            GameRoom room = BedWarMain.getRoomManager().getRoom(roomName);
//            for (int i = 0; i < count; i++) {
//                if (room.getType() == GameRoom.GameType.END || room.getType() == GameRoom.GameType.START) {
//                    break;
//                }
//                RobotPlayer player = new RobotPlayer("??????" + i + "????????????", cmdPlayer.chunk, Entity.getDefaultNBT(cmdPlayer.getPosition())
//                        .putCompound("Skin", new CompoundTag()
//                                .putByteArray("Data", skin.getSkinData().data)
//                                .putString("ModelId", skin.getSkinId())));
//
//                PlayerInfo playerInfo = new PlayerInfo(player);
//                player.setPlayerInfo(playerInfo);
//                player.spawnToAll();
//
//                room.joinPlayerInfo(playerInfo,true);
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }


    private Map<String, GameRoomConfig> roomConfig;

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

    public void enableRoom(GameRoomConfig config){
        rooms.put(config.getName(),GameRoom.enableRoom(config));
    }

    public GameRoomConfig getRoomConfig(String name){
        return roomConfig.getOrDefault(name,null);
    }

    public List<GameRoomConfig> getRoomConfigs(){
        return new ArrayList<>(roomConfig.values());
    }

    public GameRoom getRoom(String name){
        return rooms.getOrDefault(name,null);
    }

    public void disEnableRoom(String name){
        if(rooms.containsKey(name)){
            rooms.get(name).onDisable();

        }
    }

    @EventHandler
    public void onTeamDefeat(TeamDefeatEvent event){
        for (PlayerInfo info:event.getTeamInfo().getInRoomPlayer()) {
            event.getRoom().getRoomConfig().defeatCommand.forEach(cmd->Server.getInstance().dispatchCommand(new ConsoleCommandSender(),cmd.replace("@p",info.getName())));
            if(event.getRoom().getRoomConfig().isAutomaticNextRound){
                info.sendMessage("&75 &e???????????????????????????");
                ThreadManager.addThread(new BaseTimerRunnable(5) {
                    @Override
                    protected void callback() {
                        if(BedWarMain.getMenuRoomManager().joinRandomRoom(new PlayerInfo(info.getPlayer()),null)){
                            event.getRoom().quitPlayerInfo(info,false);
                        }
//                        quitPlayerInfo(playerInfo,false);

                    }
                });

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
        event.getTeamInfo().sendTitle("&e&l??????!",5);
        String line = "?????????????????????????????????????????????????????????????????????????????????????????????????????????";
        event.getRoom().sendTipMessage("&a"+line);
        event.getRoom().sendTipMessage(Utils.getCentontString("&b????????????",line.length()));
        event.getRoom().sendTipMessage("");
        for(PlayerInfo playerInfo: event.getTeamInfo().getInRoomPlayer()){
            event.getRoom().sendTipMessage(Utils.getCentontString("&7   "+playerInfo.getPlayer().getName()+" ????????? - "+(playerInfo.getKillCount()+playerInfo.getEndKillCount())+" ????????????: - "+playerInfo.getBedBreakCount(),line.length()));
        }
        event.getRoom().sendTipMessage("&a"+line);
        for (PlayerInfo info:event.getTeamInfo().getInRoomPlayer()) {
            event.getRoom().getRoomConfig().victoryCommand.forEach(cmd->Server.getInstance().dispatchCommand(new ConsoleCommandSender(),cmd.replace("@p",info.getName())));
        }
        ThreadManager.addThread(new BaseTimerRunnable(5) {
            @Override
            public void onRun() {
                for(PlayerInfo playerInfo: event.getTeamInfo().getLivePlayer()){
                    Utils.spawnFirework(playerInfo.getPosition());
                }
            }
            @Override
            protected void callback() {}
        });
        event.getRoom().sendMessage("&a?????? "+event.getTeamInfo().getTeamConfig().getNameColor()+event.getTeamInfo().getTeamConfig().getName()+" &a ???????????????!");

    }

    //????????????
    @EventHandler
    public void onQuitRoom(PlayerQuitRoomEvent event){
        if(event.performCommand){
            PlayerInfo info = event.getPlayerInfo();
            GameRoom room = event.getRoom();
            info.clear();
            if(info.getPlayer() instanceof Player){
                ((Player)info.getPlayer()).setFoodEnabled(false);
                room.getRoomConfig().quitRoomCommand.forEach(cmd-> Server.getInstance().dispatchCommand(((Player)info.getPlayer()),cmd));
            }

            room.sendMessage("&c?????? "+event.getPlayerInfo().getPlayer().getName()+" ???????????????");
        }
    }

    /**
     * ?????????????????? ???????????????????????????????????????
     *
     * 1. ????????????????????????????????????
     * 2. ???????????????????????????????????????
     * */
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event){
        GameRoom room = getGameRoomByLevel(event.getLevel());
        if(room != null && !room.isGc){
            event.setCancelled();

        }
    }

    @EventHandler
    public void onPlayerJoinRoom(PlayerJoinRoomEvent event){
        PlayerInfo info = event.getPlayerInfo();
        GameRoom gameRoom = event.getRoom();
        if (BedWarMain.getRoomManager().playerJoin.containsKey(info.getPlayer().getName())) {
            String roomName = BedWarMain.getRoomManager().playerJoin.get(info.getPlayer().getName());
            if (roomName.equalsIgnoreCase(event.getRoom().getRoomConfig().name) && gameRoom.getPlayerInfos().contains(info)) {
                if(event.isSend()) {
                    info.sendForceMessage("&c??????????????????????????????");
                }
                event.setCancelled();
                return;
            }
            if (BedWarMain.getRoomManager().hasGameRoom(roomName)) {
                GameRoom room = BedWarMain.getRoomManager().getRoom(roomName);
                if (room.getType() != GameRoom.GameType.END && room.getPlayerInfos().contains(info)) {
                    if (room.getPlayerInfo((Player) info.getPlayer()).getPlayerType() != PlayerInfo.PlayerType.DEATH ||
                            room.getPlayerInfo((Player) info.getPlayer()).getPlayerType() != PlayerInfo.PlayerType.LEAVE) {
                        if(event.isSend()) {
                            info.sendForceMessage("&c??????????????????????????????");
                        }
                        event.setCancelled();

                    }
                }
            }
        }
        if(gameRoom.getType() != GameRoom.GameType.WAIT){
//            if(GameType.END != gameRoom.getType()){
//                //TODO ??????????????????
//
//            }
            if(event.isSend()) {
                info.sendForceMessage("&c?????????????????????");
            }
            event.setCancelled();
            return;
        }
        if(gameRoom.getPlayerInfos().size() == gameRoom.getRoomConfig().getMaxPlayerSize()){
            if(event.isSend()) {
                info.sendForceMessage("&c????????????");
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
     * ???????????????????????????
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
            if(item.getId() == 65 && !room.getWorldInfo().getPlaceBlock().contains(event.getBlockAgainst())) {
                event.setCancelled();
                return;
            }else{
                if (block instanceof BlockTNT) {
                    event.setCancelled();
                    ((BlockTNT) block).prime(40);
                    Item i2 = item.clone();
                    i2.setCount(1);
                    event.getPlayer().getInventory().removeItem(i2);
                    return;
                }

            }
            if(!room.worldInfo.onChangeBlock(block,true)){
                PlayerInfo info = room.getPlayerInfo(event.getPlayer());
                if(info != null){
                    info.sendMessage("&c??????????????????????????????");
                }
                event.setCancelled();
            }
        }


    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(PlayerChatEvent event){
//        Player player = event.getPlayer();
//        GameRoom room = getGameRoomByLevel(player.getLevel());
        PlayerInfo info = getPlayerInfo(event.getPlayer());
        if(info != null){
            GameRoom room = info.getGameRoom();
            if(room != null){
                String msg = event.getMessage();
                if(msg.startsWith("@") || msg.startsWith("!")){
                    info.getGameRoom().sendFaceMessage("&l&7(????????????)&r "+info+"&r >> "+msg.substring(1));
                }else{
                    TeamInfo teamInfo = info.getTeamInfo();
                    if(teamInfo != null){
                        if(info.isDeath()){
                            room.sendMessageOnDeath(info+"&7(??????) &r>> "+msg);
                        }else {
                            teamInfo.sendMessage(teamInfo.getTeamConfig().getNameColor() + "[??????]&7 " + info.getPlayer().getName() + " &f>>&r " + msg);
                        }
                    }else{
                        room.sendMessage(info+" &f>>&r "+msg);
                    }
                }

                event.setCancelled();
            }
        }
    }



    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){
        Level level = event.getBlock().level;
        Block block = event.getBlock();
        Player player = event.getPlayer();
        GameRoom room = getGameRoomByLevel(level);
        if(room != null){
            PlayerInfo info = room.getPlayerInfo(player);
            if(info == null){
                if(!player.isOp()) {
                    player.sendMessage("????????????????????????");
                    event.setCancelled();
                }
            }else{
                if(block instanceof BlockBed){
                    event.setDrops(new Item[0]);
                }
                event.setCancelled(room.toBreakBlock(info,block));
            }
        }

    }






    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        //TODO ???????????? ??????
        Player player = event.getPlayer();
        if(playerJoin.containsKey(player.getName())){
            player.setFoodEnabled(false);
            player.setGamemode(2);
            String room = playerJoin.get(player.getName());
            if(hasGameRoom(room)){
                GameRoom room1 = getRoom(room);
                if(room1.getType() != GameRoom.GameType.END && !room1.close ){
                    PlayerInfo info = room1.getPlayerInfo(player);
                    if(info != null){
                        info.setPlayer(player);
                        info.setLeave(false);
                        if(room1.getType() == GameRoom.GameType.WAIT){
                            player.teleport(room1.getWorldInfo().getConfig().getWaitPosition());
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
                //TODO ????????????????????????
               reset(player);
            }
        }
    }

    private void reset(Player player){
        playerJoin.remove(player.getName());
        player.setHealth(player.getMaxHealth());
        player.getInventory().clearAll();
        player.getEnderChestInventory().clearAll();
        player.teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
    }

    @EventHandler
    public void onGameStartEvent(GameRoomStartEvent event){
        GameRoom room = event.getRoom();
        String line = "?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????";
        for(String s: room.getRoomConfig().gameStartMessage){
            room.sendTipMessage(Utils.getCentontString(s,line.length()));
        }
    }


    /**
     * ?????????????????????????????????
     * @param event ??????
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
            ((ShopVillage) event.getEntity()).onClick(info);
        }

    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBurn(BlockBurnEvent event){
        Block block = event.getBlock();
        for(GameRoomConfig gameRoomConfig: BedWarMain.getRoomManager().roomConfig.values()){
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
                //??????????????????
                if(entity instanceof EntityHuman){
                    if(room.getPlayerInfo((EntityHuman) entity) != null){
                        return;
                    }
                }

                event.setCancelled();
                BedWarMain.sendMessageToObject("&c????????????????????????",entity);
            }
        }

    }
    @EventHandler(ignoreCancelled = true)
    public void onWeatherChange(WeatherChangeEvent event){
        for(GameRoomConfig gameRoomConfig: BedWarMain.getRoomManager().roomConfig.values()){
            if(gameRoomConfig.worldInfo.getGameWorld().
                    getFolderName().equalsIgnoreCase(event.getLevel().getFolderName())){
                event.setCancelled();
                return;
            }
        }
    }



    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event){
        if(event.getEntity() instanceof ShopVillage) {
            if(event instanceof EntityDamageByEntityEvent){
                Entity entity = ((EntityDamageByEntityEvent) event).getDamager();
                if(entity instanceof Player) {
                    if(entity.distance(event.getEntity()) <= 4 && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
                        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo((Player) entity);
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
                GameRoom room = playerInfo.getGameRoom();
                if (room.getType() == GameRoom.GameType.WAIT) {
                    event.setCancelled();
                    return;
                }
//                    if (playerInfo == null) {
//                        event.setCancelled();
//                        return;
//                    }

                //?????????
                if (playerInfo.getPlayerType() == PlayerInfo.PlayerType.WAIT) {
                    event.setCancelled();
                    return;
                }
                if (event instanceof EntityDamageByEntityEvent) {
                    if (((EntityDamageByEntityEvent) event).getDamager() instanceof EntityFireBall) {
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
                                playerInfo1.sendTip("&e??????: &c???" + String.format("%.1f", h));
                            }

                        }
                    }
                }
                if (event instanceof EntityDamageByEntityEvent) {
                    //TODO ??????TNT????????????
                    Entity entity = ((EntityDamageByEntityEvent) event).getDamager();
                    if (entity instanceof EntityPrimedTNT) {
                        event.setDamage(2);
                    }
                    //TODO ????????????PVP
                    if (entity instanceof Player) {
                        PlayerInfo damageInfo = room.getPlayerInfo((Player) entity);

                        if (damageInfo != null) {
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
        //TODO ???????????? - ???????????????
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
                        player.getInventory().clearAll();
                        info.setLeave(true);
                    }
                }
            }
        }
    }

    public static final double BOW_MAX = 2.7;
    //2.7???????????????
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
            if(event.getBlock() instanceof BlockCraftingTable){
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
                        if (quitRoomItem(player, roomName, room)) {
                            event.setCancelled();
                            return;
                        }
                    }
                    if(item.hasCompoundTag() && item.getNamedTag().getBoolean("choseTeam")){
                        if (choseteamItem(player, room)) {
                            event.setCancelled();
                            return;
                        }
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
            player.sendTip("??????????????????");
            return true;
        }
        FormWindowSimple simple = new FormWindowSimple("???????????????","");
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

    private boolean quitRoomItem(Player player, String roomName, GameRoom room) {
        if(!RoomQuitItem.clickAgain.contains(player)){
            RoomQuitItem.clickAgain.add(player);
            player.sendTip("??????????????????");
            return true;
        }
        RoomQuitItem.clickAgain.remove(player);
        if(room.quitPlayerInfo(room.getPlayerInfo(player),true)){
            player.sendMessage("????????????????????? "+roomName);
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
                    if (quitRoomItem(player, roomName, room)) return;
                }
                if(item.hasCompoundTag() && item.getNamedTag().getBoolean("choseTeam")){
                    player.getInventory().setHeldItemSlot(0);
                    if (choseteamItem(player, room)) return;

                }
            }
        }
    }

    @EventHandler
    public void onFrom(PlayerFormRespondedEvent event){
        if(event.wasClosed()){
            DisPlayWindowsFrom.SHOP.remove(event.getPlayer().getName());
            BedWarCommand.FROM.remove(event.getPlayer().getName());
            return;
        }
        Player player = event.getPlayer();
        if(BedWarCommand.FROM.containsKey(player.getName())){
            BedWarFrom simple = BedWarCommand.FROM.get(player.getName());
            if(simple.getId() == event.getFormID()) {
                if (event.getResponse() instanceof FormResponseSimple) {
                    BaseIButtom button = simple.getBaseIButtoms().get(((FormResponseSimple) event.getResponse())
                            .getClickedButtonId());
                    button.onClick(player);
                }
                return;

            }else{
                BedWarCommand.FROM.remove(player.getName());
            }

        }

        int fromId = 102;
        if(event.getFormID() == fromId && event.getResponse() instanceof FormResponseSimple){
            PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
            if(info != null){
                TeamInfo teamInfo = info.getGameRoom().getTeamInfos().get(((FormResponseSimple) event.getResponse())
                        .getClickedButtonId());
                if(!teamInfo.join(info)){
                    info.sendMessage("&c?????????????????? "+teamInfo.toString());
                }else{
                    info.sendMessage("&a?????????&r"+teamInfo.toString()+" &a??????");
                }
            }
            return;
        }

        if(DisPlayWindowsFrom.SHOP.containsKey(player.getName())){
            if(BedWarMain.getRoomManager().getPlayerInfo(player) != null) {
                ShopFrom shopFrom = DisPlayWindowsFrom.SHOP.get(player.getName());
                if (event.getResponse() instanceof FormResponseSimple) {
                    if (((FormResponseSimple) event.getResponse())
                            .getClickedButtonId() == shopFrom.getShopButtons().size()) {
                        if (shopFrom.getLastFrom() != null) {
                            shopFrom.getLastFrom().disPlay(shopFrom.getLastFrom().getTitle(), false);
                        }

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

    @EventHandler
    public void onItemChange(InventoryTransactionEvent event) {
        InventoryTransaction transaction = event.getTransaction();
        for (InventoryAction action : transaction.getActions()) {
            for (Inventory inventory : transaction.getInventories()) {

                if (inventory instanceof ChestInventoryPanel) {
                    event.setCancelled();
                    Item i = action.getSourceItem();
                    if(i.hasCompoundTag() && i.getNamedTag().contains("index")){
                        int index = i.getNamedTag().getInt("index");
                        BasePlayPanelItemInstance item = ((ChestInventoryPanel) inventory).getPanel().getOrDefault(index,null);
                        Player player = ((ChestInventoryPanel) inventory).getPlayer();
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
