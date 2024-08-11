package org.sobadfish.bedwar.manager;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.*;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.weather.EntityLightning;
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
import cn.nukkit.inventory.PlayerEnderChestInventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.inventory.transaction.InventoryTransaction;
import cn.nukkit.inventory.transaction.action.InventoryAction;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemColorArmor;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.command.BedWarCommand;
import org.sobadfish.bedwar.entity.*;
import org.sobadfish.bedwar.event.*;
import org.sobadfish.bedwar.item.ItemIDSunName;
import org.sobadfish.bedwar.item.button.RoomQuitItem;
import org.sobadfish.bedwar.item.button.TeamChoseItem;
import org.sobadfish.bedwar.item.config.MoneyItemInfoConfig;
import org.sobadfish.bedwar.item.nbt.DieBow;
import org.sobadfish.bedwar.item.nbt.INbtItem;
import org.sobadfish.bedwar.panel.ChestInventoryPanel;
import org.sobadfish.bedwar.panel.DisPlayWindowsFrom;
import org.sobadfish.bedwar.panel.DisPlayerPanel;
import org.sobadfish.bedwar.panel.from.BedWarFrom;
import org.sobadfish.bedwar.panel.from.ShopFrom;
import org.sobadfish.bedwar.panel.from.button.BaseIButton;
import org.sobadfish.bedwar.panel.items.BasePlayPanelItemInstance;
import org.sobadfish.bedwar.panel.items.NbtDefaultItem;
import org.sobadfish.bedwar.panel.items.PlayerItem;
import org.sobadfish.bedwar.player.PlayerData;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.GameRoom.GameType;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
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

    public static LanguageManager language = BedWarMain.getLanguage();

    public static List<GameRoomConfig> LOCK_GAME = new ArrayList<>();

    public LinkedHashMap<String,String> playerJoin = new LinkedHashMap<>();

    /**
     * 当房间丢失 但是缓存了玩家登入数据的时候
     * 这个用作最终的数据还原
     * */
    public static LinkedHashMap<String,PlayerInfo> CACHE_INFO = new LinkedHashMap<>();

    private RoomManager(Map<String, GameRoomConfig> roomConfig){
        this.roomConfig = roomConfig;
    }

    private GameRoom getGameRoomByLevel(Level level){
        for(GameRoom room : new ArrayList<>(rooms.values())){
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
        }else{
            if(player.namedTag.contains("room")){
                String roomName = player.namedTag.getString("room");
                if (!"".equalsIgnoreCase(roomName)) {
                    if (rooms.containsKey(roomName)) {
                        return rooms.get(roomName).getPlayerInfo(player);
                    }
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
                            BedWarMain.sendMessageToConsole(language.getLanguage("room-loading-success","&a加载房间 [1] 完成",roomName));
                            map.put(roomName,roomConfig);
                            //TODO 更新配置注释信息
                            File roomFile = new File(nameFile+"/room.yml");
                            Utils.addDescription(roomFile,BedWarMain.getLanguage().roomDescription,true);

                        }else{
                            BedWarMain.sendMessageToConsole(language.getLanguage("room-loading-error","&c加载房间 [1] 失败",roomName));
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
                    player.sendForceMessage(BedWarMain.getLanguage().getLanguage("room-enable-error","&c[1] 还没准备好",roomName));
                    return false;
                }
            }else{
                GameRoom room = BedWarMain.getRoomManager().getRoom(roomName);
                if(room != null){
                    if(RoomManager.LOCK_GAME.contains(room.getRoomConfig()) && room.getType() == GameType.END || room.getType() == GameType.CLOSE){
                        player.sendForceMessage(BedWarMain.getLanguage().getLanguage("room-enable-error","&c[1] 还没准备好",roomName));
                        return false;
                    }
                    if(room.getWorldInfo().getConfig().getGameWorld() == null){
                        return false;
                    }
                    if(room.getType() == GameType.END ||room.getType() == GameType.CLOSE){
                        player.sendForceMessage(BedWarMain.getLanguage().getLanguage("room-status-ending","&c[1] 结算中",roomName));

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
                        player.sendForceMessage(BedWarMain.getLanguage().getLanguage("room-ban-watch","&c该房间开始后不允许旁观"));
                    }else{

                        if(player.getGameRoom() != null && !player.isWatch()){
                            player.sendForceMessage(BedWarMain.getLanguage().getLanguage("room-ban-watch-join","&c你无法进入此房间"));
                            return false;
                        }else{
                            room.joinWatch(player);
                            return true;
                        }
                    }
                    break;
                case NO_LEVEL:
                    player.sendForceMessage(BedWarMain.getLanguage().getLanguage("room-level-resting","&c这个房间正在准备中，稍等一会吧"));
                    break;
                case NO_ONLINE:
                    break;
                case NO_JOIN:
                    player.sendForceMessage(BedWarMain.getLanguage().getLanguage("room-ban-join","&c该房间不允许加入"));
                    break;
                default:
                    //可以加入
                    return true;
            }
        } else {
            player.sendForceMessage(BedWarMain.getLanguage().getLanguage("room-absence","&c不存在 &r[1] &c房间",roomName));

        }
        return false;
    }


    private final Map<String, GameRoomConfig> roomConfig;

    public Map<String, GameRoom> getRooms() {
        return rooms;
    }

    private final Map<String, GameRoom> rooms = new LinkedHashMap<>();

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
                RoomManager.LOCK_GAME.remove(config);
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
                info.sendMessage(BedWarMain.getLanguage().getLanguage("player-auto-join-next-room","&7即将自动进行下一局"));
                RandomJoinManager.joinManager.nextJoin(info);
//                ThreadManager.addThread(new AutoJoinGameRoomRunnable(5,info,event.getRoom(),null));

            }

        }
    }

    @EventHandler
    public void onGetExp(PlayerGetExpEvent event){
        String playerName = event.getPlayerName();
        Player player = Server.getInstance().getPlayer(playerName);
        if(player != null){
//            "&b +"+event.getExp()+" 经验("+event.getCause()+")"
            player.sendMessage(TextFormat.colorize('&',BedWarMain.getLanguage().getLanguage("player-add-exp-msg",
                    "&b +[1] 经验([2])",event.getExp()+"",event.getCause())));
            PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
            PlayerData data = BedWarMain.getDataManager().getData(playerName);

            if(info == null || info.getGameRoom() == null){

                BedWarMain.sendTipMessageToObject("&l&m"+Utils.writeLine(5,"&a▁▁▁"),player);
                BedWarMain.sendTipMessageToObject("&l"+Utils.writeLine(9,"&a﹉﹉"),player);
                String line = String.format("%20s","");
                player.sendMessage(line);
                String inputTitle = BedWarMain.getLanguage().getLanguage("player-getting-level-exp-title","&b&l起床战争经验\n");
                BedWarMain.sendTipMessageToObject(Utils.getCentontString(inputTitle,30),player);
                BedWarMain.sendTipMessageToObject(Utils.getCentontString(BedWarMain.getLanguage().getLanguage("game-level-msg","&b等级")+" "+data.getLevel()+String.format("%"+inputTitle.length()+"s","")+" "+BedWarMain.getLanguage().getLanguage("game-level-msg","&b等级")+"&r "+(data.getLevel() + 1)+"\n",30),player);

                BedWarMain.sendTipMessageToObject("&7["+data.getExpLine(20)+"&7]\n",player);

                String d = String.format("%.1f",data.getExpPercent() * 100.0);
                BedWarMain.sendTipMessageToObject(Utils.getCentontString("&b"+data.getExpString(data.getExp())+" &7/ &a"+data.getExpString(data.getNextLevelExp())+" &7("+d+"％)",40)+"\n",player);
                BedWarMain.sendTipMessageToObject("&l&m"+Utils.writeLine(5,"&a▁▁▁"),player);
                BedWarMain.sendTipMessageToObject("&l"+Utils.writeLine(9,"&a﹉﹉"),player);

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
        event.getTeamInfo().sendTitle(BedWarMain.getLanguage().getLanguage("game-victory","&e&l胜利!"),5);
        String line = "■■■■■■■■■■■■■■■■■■■■■■■■■■";
        event.getRoom().sendTipMessage("&a"+line);
        event.getRoom().sendTipMessage(Utils.getCentontString(BedWarMain.getLanguage().getLanguage("game-end","&b游戏结束"),line.length()));
        event.getRoom().sendTipMessage("");
        for(PlayerInfo playerInfo: event.getTeamInfo().getInRoomPlayer()){
            event.getRoom().sendTipMessage(Utils.getCentontString(BedWarMain.getLanguage().getLanguage("game-end-info","&7   [1] 击杀：[2] 破坏床数: [3] 助攻: [4]",
                    playerInfo.getPlayer().getName(),
                    (playerInfo.getKillCount())+"",
                    playerInfo.getBedBreakCount()+"",
                    playerInfo.getAssists()+""
                    ),line.length()));
        }
        event.getRoom().sendTipMessage("&a"+line);
        for (PlayerInfo info:event.getTeamInfo().getInRoomPlayer()) {
            PlayerData data = BedWarMain.getDataManager().getData(info.getName());
            data.getRoomData(event.getRoom().getRoomConfig().name).victoryCount++;
            data.getRoomData(event.getRoom().getRoomConfig().name).gameCount++;
            event.getRoom().getRoomConfig().victoryCommand.forEach(cmd->Server.getInstance().dispatchCommand(new ConsoleCommandSender(),cmd.replace("@p",info.getName())));
        }

        event.getRoom().sendMessage(BedWarMain.getLanguage().getLanguage("game-end-team-info","&a恭喜 [1] &a 获得了胜利!",
                event.getTeamInfo().getTeamConfig().getNameColor()+event.getTeamInfo().getTeamConfig().getName()));

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


            if(info.getPlayer() instanceof Player && ((Player) info.getPlayer()).isOnline()){
                ((Player)info.getPlayer()).setFoodEnabled(false);
                if(info.getPlayer() != null) {
                    room.getRoomConfig().quitRoomCommand.forEach(cmd -> Server.getInstance().dispatchCommand(((Player) info.getPlayer()), cmd));
                }
            }
            if(info.isWatch()){
                return;
            }
            room.sendMessage(BedWarMain.getLanguage().getLanguage("player-quit-room-echo-message","&c玩家 [1] 离开了游戏",
                    event.getPlayerInfo().playerName ));
        }
    }

    /**
     * 阻止区块卸载 如果区块卸载会出现如下问题
     *
     * 1. 还原房间部分方块无法还原
     * 2. 导致后台循环报错空指针异常
     * */
    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent event){
        for(Entity entity: event.getChunk().getEntities().values()){
            if(entity instanceof BedWarFloatText){
//                event.setCancelled();
                entity.despawnFromAll();
            }
        }
    }



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
            //写入数组
            if(info.isPlayer()){
                CACHE_INFO.put(info.playerName,info);
            }


            String roomName = BedWarMain.getRoomManager().playerJoin.get(info.getPlayer().getName());
            if (roomName.equalsIgnoreCase(event.getRoom().getRoomConfig().name) && gameRoom.getPlayerInfos().contains(info)) {
                if(event.isSend()) {
                    info.sendForceMessage(BedWarMain.getLanguage().getLanguage("player-join-in-room","&c你已经在这个房间内了"));
                }
                event.setCancelled();
                return;
            }
            if (BedWarMain.getRoomManager().hasGameRoom(roomName)) {
                GameRoom room = BedWarMain.getRoomManager().getRoom(roomName);
                if (room.getType() != GameRoom.GameType.END && room.getPlayerInfos().contains(info)) {
                    if (room.getPlayerInfo(info.getPlayer()).getPlayerType() != PlayerInfo.PlayerType.WATCH ||
                            room.getPlayerInfo(info.getPlayer()).getPlayerType() != PlayerInfo.PlayerType.LEAVE) {
                        if(event.isSend()) {
                            info.sendForceMessage(BedWarMain.getLanguage().getLanguage("player-join-in-room","&c你已经在这个房间内了"));
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
                info.sendForceMessage(BedWarMain.getLanguage().getLanguage("player-join-in-room-started","&c游戏已经开始了"));
            }
            event.setCancelled();
            return;
        }
        if(gameRoom.getPlayerInfos().size() == gameRoom.getRoomConfig().getMaxPlayerSize()){
            if(event.isSend()) {
                info.sendForceMessage(BedWarMain.getLanguage().getLanguage("player-join-in-room-max","&c房间满了"));
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
                    info.sendMessage(BedWarMain.getLanguage().getLanguage("event-place-block-cancel-watch","&c观察状态下不能放置方块"));
                    event.setCancelled();
                    return;
                }
                if (item.getId() == 65 && !room.getWorldInfo().getPlaceBlock().contains(event.getBlockAgainst())) {
                    info.sendMessage(BedWarMain.getLanguage().getLanguage("event-place-ladder-in-block","&c你只能将梯子放置在玩家放置的方块上"));
                    event.setCancelled();
                    return;
                } else {
                    if (block instanceof BlockTNT) {
                        try{
                            event.setCancelled();
//                            ((BlockTNT) block).prime(40);
                            //TODO 自定义TNT
                            EntityTnt entityTnt = new EntityTnt(info.getPlayer().chunk,Entity.getDefaultNBT(block.add(0.5,0,0.5)),info,room.getRoomConfig().tntExplodeTime * 20);
                            entityTnt.spawnToAll();
                            Item i2 = item.clone();
                            i2.setCount(1);
                            event.getPlayer().getInventory().removeItem(i2);
                        }catch (Exception e){
                            event.setCancelled();
                            return;
                        }

                        return;
                    }

                }
                if (!room.worldInfo.onChangeBlock(block, true)) {
                    info.sendMessage(BedWarMain.getLanguage().getLanguage("event-place-block-cancel","&c你不能在这里放置方块"));

                    event.setCancelled();
                }else{
                    //记录队伍放置的末影箱
                    if(block instanceof BlockEnderChest){
                        info.getTeamInfo().placeEnderChest.add(block);
                    }

                    //快速搭路放置方块
                    if(room.getRoomConfig().fastPlace && info.fastPlace && info.getPlayer().getInventory().getItemInHand().getId() == 35){
                        //TODO 快速搭路代码实现
                        BlockFace face = getPlaceBlockFace(event.getBlockAgainst(),event.getBlockReplace());
                        if(face != null){
                            //多线程延时放置
                            Block c = event.getBlockAgainst();
                            if(face == BlockFace.UP && floorBlock(info.getPlayer(),c)){
                                return;
                            }
                            Block cache =  info.getPlayer().getInventory().getItemInHand().getBlock();
                            int count = Math.min(info.getPlayer().getInventory().getItemInHand().getCount(), room.getRoomConfig().fastPlaceCount);
                            Item ir = info.getPlayer().getInventory().getItemInHand().clone();
                            ir.setCount(count);
                            info.getPlayer().getInventory().removeItem(ir);
                            facePlaceBlock(face,c,cache,room,count);
                        }

                    }

                }
            }
        }

    }

    private boolean floorBlock(Position playerPos,Block floorBlock){
        return Math.abs(playerPos.getX() - floorBlock.getFloorX()) < 0.5 && Math.abs(playerPos.getZ() - floorBlock.getFloorZ()) < 0.5;
    }

    /**
     * 快速放置方块
     * @param face 方块朝向
     * @param block 方块
     * */
    public void facePlaceBlock(BlockFace face,Block last,Block block,GameRoom room,int size){
        ThreadManager.SCHEDULED.execute(() -> {
            for(int i = 1; i <= size; i++){
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Position pos = last.getSide(face,i);
                if(pos.level.getBlock(pos).getId() == 0){
                    Block place = Block.get(block.getId(),block.getDamage(),last.getSide(face,i));
                    last.level.setBlock(place,place);
                    if(room != null){
                        room.worldInfo.onChangeBlock(place, true);
                    }
                }
            }
        });
    }

    public BlockFace getPlaceBlockFace(Block placeBlock,Block targetBlock){
        for(BlockFace face: BlockFace.values()){
            if(placeBlock.getSide(face).equals(targetBlock)){
                return face;
            }
        }
        return null;
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
                        info.getGameRoom().sendFaceMessage(BedWarMain.getLanguage().getLanguage("player-speak-in-room-message-all",
                                "&l&7(全体消息)&r [1]&r >> [2]",info.toString(),msg.substring(1)));
                    }else{
                        TeamInfo teamInfo = info.getTeamInfo();
                        if(teamInfo != null){
                            if(info.isDeath()){
                                room.sendMessageOnDeath(BedWarMain.getLanguage().getLanguage("player-speak-in-room-message-death",
                                        "[1]&7(死亡) &r>> [2]",
                                        info.toString(),msg
                                ));
                            }else {
                                teamInfo.sendMessage(BedWarMain.getLanguage().getLanguage("player-speak-in-room-message-team",
                                        "[1][队伍]&7 [2] &f>>&r [3]",
                                        teamInfo.getTeamConfig().getNameColor(),
                                        info.getPlayer().getName(),
                                        msg));
//                                teamInfo.sendMessage(teamInfo.getTeamConfig().getNameColor() + "[队伍]&7 " + info.getPlayer().getName() + " &f>>&r " + msg);
                            }
                        }else{
                            room.sendMessage(BedWarMain.getLanguage().getLanguage("player-speak-in-room-message",
                                    "[1] &f>>&r [2]",
                                    info.toString(),msg));
                        }
                    }
                }
                event.setCancelled();
            }
        }
    }



    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(final BlockBreakEvent event){
        Level level = event.getBlock().level;
        Block block = event.getBlock();
        Player player = event.getPlayer();
        GameRoom room = getGameRoomByLevel(level);
        if(room != null){
            PlayerInfo info = room.getPlayerInfo(player);
            if(info == null){
                if(!player.isOp()) {
                    player.sendMessage(BedWarMain.getLanguage().getLanguage("event-break-block-cancel","你不能破坏此方块"));
                    event.setCancelled();
                }
            }else{
                if(!room.toBreakBlock(info,block)){
                    event.setCancelled();
                }else{
                    //移除记录的末影箱
                    if(block instanceof BlockEnderChest){
                        info.getTeamInfo().placeEnderChest.remove(block);
                    }

                    //出现了没有掉落物情况..修复
                    event.setDrops(event.getBlock().getDrops(Item.get(278)));
                }
                if(block instanceof BlockEnderChest || block instanceof BlockBed){
                    event.setDrops(new Item[0]);
                }
                if(info.isWatch()){
                    player.sendMessage(BedWarMain.getLanguage().getLanguage("event-break-block-cancel-watch","&c观察状态下不能破坏方块"));
                    event.setCancelled();
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
                    reset(player);
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
                            room1.quitPlayerInfo(info,true);
                        }else{
                            if(info.isWatch() || info.getTeamInfo() == null){
                                room1.joinWatch(info);
                            }else{
                                info.death(null);
                            }

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
        }else {
            for(GameRoomConfig gameRoomConfig: getRoomConfigs()){
                if(gameRoomConfig.getWorldInfo().getGameWorld() ==  player.level){
                    reset(player);
                }
            }
            if(player.getGamemode() == 3) {
                player.setGamemode(0);
            }
        }

    }

    private void reset(Player player){
        PlayerInfo info = getPlayerInfo(player);
        if(info == null){
            if(playerJoin.containsKey(player.getName())){
                if(CACHE_INFO.containsKey(player.getName())){
                    info = CACHE_INFO.get(player.getName());
                    CACHE_INFO.remove(player.getName());
                }
            }
        }
        player.setNameTag(player.getName());
        playerJoin.remove(player.getName());
        player.setHealth(player.getMaxHealth());
        player.getInventory().clearAll();
        player.getEnderChestInventory().clearAll();
        if(info != null){
            player.getInventory().setContents(info.inventory);
            player.getEnderChestInventory().setContents(info.eInventory);
            player.setGamemode(info.lastGameMode);
            player.setExperience(info.lastExp);
        }
        player.removeAllEffects();
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
        Level level = event.getTarget();
        GameRoom room = getGameRoomByLevel(level);
        if(entity instanceof EntityHuman) {
            PlayerInfo info = getPlayerInfo((EntityHuman) entity);
            if(info == null){
                info = new PlayerInfo((EntityHuman) entity);
            }
            if (room != null) {
                //不能阻止正常进入游戏
                if(info.getPlayerType() == PlayerInfo.PlayerType.WAIT){
                    if(room.equals(info.getGameRoom())){
                        return;
                    }
                }else if(room.equals(info.getGameRoom())){
                    //断线重连
                    return;
                }
                if(info.getGameRoom() != null){
                    info.getGameRoom().quitPlayerInfo(info,false);
                }
                switch (room.joinPlayerInfo(info,true)){
                    case CAN_WATCH:
                        room.joinWatch(info);
                        break;
                    case NO_LEVEL:
                    case NO_JOIN:
                        event.setCancelled();
                        BedWarMain.sendMessageToObject(BedWarMain.getLanguage().getLanguage("world-ban-join","&c你无法进入该地图"),entity);
                        if(Server.getInstance().getDefaultLevel() != null) {
                            info.getPlayer().teleport(Server.getInstance().getDefaultLevel().getSafeSpawn());
                        }else{
                            info.getPlayer().teleport(info.getPlayer().getLevel().getSafeSpawn());
                        }
                        break;
                    default:break;
                }

            }else{
                if(info.getGameRoom() != null){
                    if(info.isLeave()){
                        return;
                    }

                    if(!info.getGameRoom().getWorldInfo().getConfig().getWaitPosition().getLevel().getFolderName().equalsIgnoreCase(level.getFolderName())) {
                        info.getGameRoom().quitPlayerInfo(info, false);
                    }
                }
            }
            //清除所有的浮空字

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

        if(event.getEntity() instanceof FloatBlock){
            event.setCancelled();
        }
        if(event.getEntity() instanceof ShopVillage) {
            if(event instanceof EntityDamageByEntityEvent){
                Entity damager = ((EntityDamageByEntityEvent) event).getDamager();
                if(damager == null){
                    return;
                }
                if(damager instanceof Player) {
                    if(damager.distance(event.getEntity()) <= 4 && event.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK){
                        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo((Player) damager);
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


        if(event.getEntity() instanceof EntityHuman){
            PlayerInfo playerInfo = getPlayerInfo((EntityHuman) event.getEntity());
            //防止火球这些伤害导致重设PVP的击退
            boolean resetKnock = true;
            boolean explode = false;
            if(playerInfo != null) {
                if (playerInfo.isWatch()) {
                    playerInfo.sendForceMessage(BedWarMain.getLanguage().getLanguage("player-gamemode-3","&c你处于观察者模式"));
                    event.setCancelled();
                    return;
                }
                GameRoom room = playerInfo.getGameRoom();
                if (room.getType() == GameRoom.GameType.WAIT) {

                    event.setCancelled();
                    return;
                }
                //救援平台伤害
                if(event.getCause() == EntityDamageEvent.DamageCause.FALL){
                    if(playerInfo.getPlayer().getLevelBlock().getId() == 165){
                        event.setCancelled();
                    }
                }

                //会重复
                if (playerInfo.getPlayerType() == PlayerInfo.PlayerType.WAIT) {
                    event.setCancelled();
                    return;
                }
                if (event instanceof EntityDamageByEntityEvent) {
                    Entity dg = ((EntityDamageByEntityEvent)event).getDamager();
                    if (dg instanceof EntityFireBall) {
                        PlayerInfo target = ((EntityFireBall) dg).getMaster();
                        if(target != null){
                            if(!target.equals(playerInfo) && (target.getTeamInfo() != null
                                    && !target.getTeamInfo().equals(playerInfo.getTeamInfo()))){
                                playerInfo.setDamageByInfo(target);
                            }
                        }
                        event.setDamage(2.0F);
                        ((EntityDamageByEntityEvent)event).setKnockBack(room.getRoomConfig().fireballKnockBack);
                        resetKnock = false;
                        explode = true;
                    }
//                    if(event.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION){
//                        if(((EntityDamageByEntityEvent) event).getDamager() instanceof EntityHuman){
//                            //TODO 证明是扔火球的玩家
//                            event.setDamage(2);
//                            ((EntityDamageByEntityEvent) event).setKnockBack(room.getRoomConfig().fireballKnockBack);
//                        }
//                    }
                    if(((EntityDamageByEntityEvent) event).getDamager() instanceof EntityLightning){
                        event.setDamage(12);
                        resetKnock = false;
                        explode = true;
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
                                playerInfo1.sendTip(BedWarMain.getLanguage().getLanguage("player-attack-by-arrow",
                                        "&e目标: &c❤ [1]",String.format("%.1f", h)) );
                            }

                        }
                        if(damagers instanceof EntityFireBall){
                            event.setDamage(2);
                            explode = true;
                        }

                    }
                    resetKnock = false;
                }
                if (event instanceof EntityDamageByEntityEvent) {
                    //TODO 免受TNT爆炸伤害
//                    Entity entity = ((EntityDamageByEntityEvent) event).getDamager();
//                    if (entity instanceof EntityPrimedTNT) {
//                        event.setDamage(2);
//                    }
                    Entity entity = ((EntityDamageByEntityEvent) event).getDamager();
//                    if (entity instanceof EntityPrimedTNT) {
//                        event.setDamage(room.getRoomConfig().tntDamage);
//                        resetKnock = false;
//                        explode = true;
//                    }
                    if(entity instanceof EntityTnt){
                        PlayerInfo target = ((EntityTnt) entity).getTarget();
                        if(target != null){
                            if(!target.equals(playerInfo) && (target.getTeamInfo() != null && !target.getTeamInfo().equals(playerInfo.getTeamInfo()))){
                                playerInfo.setDamageByInfo(target);
                                event.setDamage(room.getRoomConfig().tntDamage);
                            }else{
                                event.setDamage(2f);
                            }
                            explode = true;
//                            else{
//                                event.setCancelled();
//                                return;
//                            }
                        }
                        if(room.getRoomConfig().tntKnockBack > 0){
                            ((EntityDamageByEntityEvent) event).setKnockBack(room.getRoomConfig().tntKnockBack);
                        }

                        resetKnock = false;
                    }
                    //TODO 阻止队伍PVP
                    if (entity instanceof EntityHuman) {
                        PlayerInfo damageInfo = room.getPlayerInfo((Player) entity);
                        if (damageInfo != null) {
                            if (damageInfo.isWatch()) {
                                event.setCancelled();
                                return;
                            }
                            TeamInfo t1 = playerInfo.getTeamInfo();
                            TeamInfo t2 = damageInfo.getTeamInfo();
                            if(!explode){
                                if (t1 != null && t2 != null) {
                                    if (t1.getTeamConfig().getName().equalsIgnoreCase(t2.getTeamConfig().getName())) {
                                        event.setCancelled();
                                        return;
                                    }
                                }

                            }
                            playerInfo.setDamageByInfo(damageInfo);
                        } else {
                            if(!explode){
                                event.setCancelled();
                            }

                        }
                    }
                    //击退..
                    if(!event.isCancelled()){
                        if(resetKnock){
                            if(room.getRoomConfig().knockConfig.enable && !event.isCancelled()){
                                event.getEntity().setMotion(Utils.knockBack(event.getEntity(),entity,
                                        room.getRoomConfig().knockConfig.speed,
                                        room.getRoomConfig().knockConfig.force,
                                        room.getRoomConfig().knockConfig.motionY));
                                ((EntityDamageByEntityEvent) event).setKnockBack(0f);
                            }
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
    public void onProjectileHitEvent(ProjectileHitEvent event){
        Entity entity = event.getMovingObjectPosition().entityHit;
        Entity d = event.getEntity();
        if(d instanceof EntityFireBall){
            if(entity instanceof Player){
//                if(((EntityFireBall) d).getMaster() != null && ((EntityFireBall) d).getMaster().equals(new PlayerInfo((EntityHuman) entity))){
////                    event.setCancelled();
//                }
                //照样爆炸
                ((EntityFireBall) d).explode();
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
                if(room.getType() == GameRoom.GameType.START ){
                    PlayerInfo info = room.getPlayerInfo(player);
                    if(info != null){
                        if(!info.isWatch()){
                            info.setLeave(true);
                        }

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
                        PlayerInfo playerInfo = room.getPlayerInfo(player);
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
                        if(room.getEventControl().hasResidentEvent("die")){
                            Utils.launchWitherSkull(playerInfo);
                        }
                    }
                }
            }
        }
    }


    public boolean debugFastPlace = false;

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (playerJoin.containsKey(player.getName())) {

            String roomName = playerJoin.get(player.getName());
            GameRoom room = getRoom(roomName);
            if(room == null){
                return;
            }
            PlayerInfo playerInfo = room.getPlayerInfo(player);
            if(room.getType() == GameType.START){
                //摸到 末影箱子时 替换为团队物品
                if(event.getBlock() instanceof BlockEnderChest){
                    BlockEnderChest enderChest = (BlockEnderChest) event.getBlock();
                    // 检查是哪个队伍的末影箱 如果都不在队伍内就检查 哪个距离队伍出生点最近 （10格）
                    TeamInfo enderChestMasterTeam = null;
                    for(TeamInfo teamInfo: room.getTeamInfos()){
                        if(teamInfo.placeEnderChest.contains(enderChest)){
                            enderChestMasterTeam = teamInfo;
                        }
                    }
                    if(enderChestMasterTeam == null){
                        for(TeamInfo teamInfo: room.getTeamInfos()){
                           if(teamInfo.getTeamConfig().getSpawnPosition().distance(enderChest) < 10){
                               enderChestMasterTeam = teamInfo;
                               enderChestMasterTeam.placeEnderChest.add(enderChest);
                           }
                        }
                    }

                    if(playerInfo.getTeamInfo().equals(enderChestMasterTeam)){
                        player.getEnderChestInventory().setContents(player.getEnderChestInventory().getContents());
                    }else{
                        event.setCancelled();
                    }
                }
            }

            if (event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK || event.getAction() == PlayerInteractEvent.Action.LEFT_CLICK_BLOCK) {
                //潜行左键切换模式
                if (player.isSneaking() && room.getRoomConfig().fastPlace) {

                    playerInfo.fastPlace = !playerInfo.fastPlace;

                }

            }
            if (event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_AIR || event.getAction() == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK) {
                Item item = event.getItem();
                if (event.getBlock() instanceof BlockCraftingTable || event.getBlock() instanceof BlockBed) {
                    if (BedWarMain.getRoomManager().getPlayerInfo(event.getPlayer()) != null) {
                        event.setCancelled();
                        return;
                    }
                }

                if (item.hasCompoundTag() && item.getNamedTag().getBoolean("quitItem")) {
                    event.setCancelled();
                    quitRoomItem(player, roomName, room);
                    return;
                }
                if (item.hasCompoundTag() && item.getNamedTag().getBoolean("follow")) {
                    followPlayer(room.getPlayerInfo(player), room);
                    event.setCancelled();
                    return;
                }

                if (item.hasCompoundTag() && item.getNamedTag().getBoolean("choseTeam")) {
                    event.setCancelled();
                    choseteamItem(player, room);
                    return;

                }
                if (item.hasCompoundTag() && item.getNamedTag().contains(NbtDefaultItem.TAG)) {
                    String name = item.getNamedTag().getString(NbtDefaultItem.TAG);

                    if (event.getBlock() instanceof BlockChest) {
                        if (!room.getClickChest().contains((BlockChest) event.getBlock())) {
                            room.getClickChest().add((BlockChest) event.getBlock());
                        }
                        return;
                    }
                    Item r = item.clone();
                    r.setCount(1);
                    if (room.getRoomConfig().nbtItemInfo.items.containsKey(name)) {
                        INbtItem iNbtItem = room.getRoomConfig().nbtItemInfo.items.get(name).name;
                        if (iNbtItem.onClick(r, player)) {
                            event.setCancelled();
                        }
                    }
                }


            }

        }

    }

    private boolean choseteamItem(Player player, GameRoom room) {
        if(!TeamChoseItem.clickAgain.contains(player)){
            TeamChoseItem.clickAgain.add(player);
            player.sendTip(BedWarMain.getLanguage().getLanguage("chose-click-again","请再点击一次"));
            return true;
        }
        FormWindowSimple simple = new FormWindowSimple(BedWarMain.getLanguage().getLanguage("player-chose-team","请选择队伍"),"");
        for(TeamInfo teamInfoConfig: room.getTeamInfos()){
            Item wool = teamInfoConfig.getTeamConfig().getTeamConfig().getBlockWoolColor();

            simple.addButton(new ElementButton(TextFormat.colorize('&', teamInfoConfig +" &r"+teamInfoConfig.getTeamPlayers().size()+" / "+(room.getRoomConfig().getMaxPlayerSize() / room.getTeamInfos().size())),
                    new ElementButtonImageData("path",
                            ItemIDSunName.getIDByPath(wool.getId(),wool.getDamage()))));
        }
        player.showFormWindow(simple,102);
        TeamChoseItem.clickAgain.remove(player);
        return false;
    }

    private void followPlayer(PlayerInfo info,GameRoom room){
        info.sendMessage(BedWarMain.getLanguage().getLanguage("player-click-chose-teleport-player","选择要传送的玩家"));
        if (room == null){
            return;
        }
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
        List<BaseIButton> list = new ArrayList<>();
        //手机玩家
        for(PlayerInfo i: room.getLivePlayers()){
            list.add(new BaseIButton(new PlayerItem(i).getGUIButton(info)) {
                @Override
                public void onClick(Player player) {
                    player.teleport(i.getPlayer().getLocation());
                }
            });
        }
        DisPlayWindowsFrom.disPlayerCustomMenu((Player) info.getPlayer(),BedWarMain.getLanguage().getLanguage("player-from-teleport-player-title","传送玩家"),list);

    }


    private void disPlayUI(PlayerInfo info,GameRoom room){
        //WIN10 玩家 故障，，，，
       // DisPlayerPanel playerPanel = new DisPlayerPanel();
       // playerPanel.displayPlayer(info,DisPlayerPanel.displayPlayers(room),BedWarMain.getLanguage().getLanguage("player-from-teleport-player-title","传送玩家"));

        disPlayProtect(info, room);
    }

    private boolean quitRoomItem(Player player, String roomName, GameRoom room) {
        if(!RoomQuitItem.clickAgain.contains(player)){
            RoomQuitItem.clickAgain.add(player);
            player.sendTip(BedWarMain.getLanguage().getLanguage("chose-click-again","请再点击一次"));
            return true;
        }
        RoomQuitItem.clickAgain.remove(player);
        if(room.quitPlayerInfo(room.getPlayerInfo(player),true)){
            player.sendMessage(BedWarMain.getLanguage().getLanguage("player-quit-room-success","你成功离开房间 ",roomName));
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
                if(room.getType() == GameType.WAIT){
                    event.setCancelled();
                    return;
                }
                Item item = event.getItem();
                if (item.hasCompoundTag() && (item.getNamedTag().getBoolean("quitItem") || item.hasCompoundTag()
                        || item.getNamedTag().getBoolean("choseTeam") || item.getNamedTag().getBoolean("follow"))) {
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
            return;
        }
        Player player = event.getPlayer();
        if(DisPlayWindowsFrom.CUSTOM.containsKey(player.getName())){
            BedWarFrom simple = DisPlayWindowsFrom.CUSTOM.get(player.getName());
            if(onBedWarFrom(event, player, simple)){
                return;
            }else{
                DisPlayWindowsFrom.CUSTOM.remove(player.getName());
            }
        }
        if(BedWarCommand.FROM.containsKey(player.getName())){
            BedWarFrom simple = BedWarCommand.FROM.get(player.getName());
            if(onBedWarFrom(event, player, simple)){
                return;
            }else{
                BedWarCommand.FROM.remove(player.getName());
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
                    info.sendMessage(BedWarMain.getLanguage().getLanguage("player-joined-team","&c你已经加入了 [1]",teamInfo.toString()));
                }else{
//                    info.sendMessage("&a加入了&r"+ teamInfo +" &a成功");
                    info.sendMessage((BedWarMain.getLanguage().getLanguage("player-join-team-success","&a加入&r[1] &a成功",
                            teamInfo.toString())));
                    player.getInventory().setItem(0,teamInfo.getTeamConfig().getTeamConfig().getBlockWoolColor());
                    for (Map.Entry<Integer, Item> entry : info.armor.entrySet()) {
                        Item item;
                        if(entry.getValue() instanceof ItemColorArmor){
                            ItemColorArmor colorArmor = (ItemColorArmor) entry.getValue();
                            colorArmor.setColor(teamInfo.getTeamConfig().getRgb());
                            item = colorArmor;
                        }else{
                            item = entry.getValue();
                        }
                        player.getInventory().setArmorItem(entry.getKey(), item);
                    }
                }
            }
            return;
        }

        if(DisPlayWindowsFrom.SHOP.containsKey(player.getName())){
            ShopFrom shopFrom = DisPlayWindowsFrom.SHOP.get(player.getName());
            if(shopFrom != null){
                if(shopFrom.getId() == event.getFormID()){
                    if(BedWarMain.getRoomManager().getPlayerInfo(player) != null) {
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
                            return;

                        }
                    }
                }
            }
            DisPlayWindowsFrom.SHOP.remove(player.getName());


        }
    }

    private boolean onBedWarFrom(PlayerFormRespondedEvent event, Player player, BedWarFrom simple) {
        if(simple.getId() == event.getFormID()) {
            if (event.getResponse() instanceof FormResponseSimple) {
                BaseIButton button = simple.getBaseIButtoms().get(((FormResponseSimple) event.getResponse())
                        .getClickedButtonId());
                button.onClick(player);
                return true;
            }
        }
        return false;
    }


    @EventHandler
    public void onPlayerConsumeItemEvent(PlayerItemConsumeEvent event){
        //监听玩家使用隐身药水
        if(event.getItem().getDamage() == 7 || event.getItem().getDamage() == 8){
            //没收玩家身上装备
            Player player = event.getPlayer();
            PlayerInfo playerInfo = getPlayerInfo(player);
            if(playerInfo != null) {
                GameRoom gameRoom = playerInfo.getGameRoom();
                if (gameRoom != null) {
                    if (gameRoom.getType() == GameType.START) {
                        playerInfo.invisibilityArmor();
                    }
                }
            }
        }


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

                if(inventory instanceof PlayerEnderChestInventory){
                    EntityHuman player =((PlayerEnderChestInventory) inventory).getHolder();
                    PlayerInfo playerInfo = getPlayerInfo(player);
                    if(playerInfo.isLive()){
                        //更新所有人的
                        for(Player player1: inventory.getViewers()){
                            if(player1.getName().equalsIgnoreCase(player.getName())){
                                continue;
                            }
                            player1.getEnderChestInventory().setContents(player.getEnderChestInventory().getContents());
                        }

                        playerInfo.getTeamInfo().pEnderChest.putAll(playerInfo.getPlayer().getEnderChestInventory().slots);
                    }
                }

                if(inventory instanceof PlayerInventory){
                    Item item = action.getSourceItem();

                    EntityHuman player =((PlayerInventory) inventory).getHolder();
                    //阻止其他玩家拿装备
                    if(item.hasCompoundTag() && item.getNamedTag().contains("bd_master")){
                        if(!player.getName().equalsIgnoreCase(item.getNamedTag().getString("bd_master"))){
                            event.setCancelled();
                        }
                    }
                    PlayerInfo playerInfo = getPlayerInfo(player);
                    if(playerInfo != null){
                        GameRoom gameRoom = playerInfo.getGameRoom();
                        if(gameRoom != null){
                            if(gameRoom.getType() == GameType.WAIT){
                                event.setCancelled();
                            }
                        }
                    }

                }
            }
        }
    }





    @EventHandler
    public void onWorldReloadEvent(ReloadWorldEvent event) {
        GameRoomConfig config = event.getRoomConfig();
//        Server.getInstance().getScheduler().scheduleTask(BedWarMain.getBedWarMain(), new Runnable() {
//            @Override
//            public void run() {
                Server.getInstance().loadLevel(config.getWorldInfo().getLevel());
                BedWarMain.getRoomManager().getRooms().remove(config.getName());
                RoomManager.LOCK_GAME.remove(config);
                WorldResetManager.RESET_QUEUE.remove(config.name);
                BedWarMain.sendMessageToConsole("&rRecycle Room " + config.name);
                BedWarMain.sendMessageToConsole("&rRoom " + config.name + " Recycled");

//            }
//        });

    }
}
