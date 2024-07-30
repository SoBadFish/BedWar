package org.sobadfish.bedwar.player;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.network.protocol.DataPacket;
import cn.nukkit.network.protocol.MobArmorEquipmentPacket;
import cn.nukkit.network.protocol.OnScreenTextureAnimationPacket;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.IronGolem;
import org.sobadfish.bedwar.event.PlayerGameDeathEvent;
import org.sobadfish.bedwar.item.ItemInfo;
import org.sobadfish.bedwar.item.config.ItemInfoConfig;
import org.sobadfish.bedwar.item.config.MoneyItemInfoConfig;
import org.sobadfish.bedwar.item.team.TeamEffect;
import org.sobadfish.bedwar.item.team.TeamEffectInfo;
import org.sobadfish.bedwar.item.team.TeamEnchant;
import org.sobadfish.bedwar.manager.RoomManager;
import org.sobadfish.bedwar.player.message.ScoreBoardMessage;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.event.IGameRoomEvent;
import org.sobadfish.bedwar.tools.Utils;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author SoBadFish
 */
public class PlayerInfo {

    public int killCount = 0;

    public int bedBreakCount = 0;

    public int endKillCount = 0;

    public int deathCount = 0;

    public int updateTime = 0;

    public int assists = 0;

    private EntityHuman player;

    private PlayerType playerType;

    private GameRoom gameRoom;

    private TeamInfo teamInfo;

    public boolean cancel;

    public boolean disable;

    public boolean isLeave;

    private PlayerInfo damageByInfo = null;

    public LinkedHashMap<PlayerInfo,Long> assistsPlayers = new LinkedHashMap<>();

    public List<Integer> buyArmorId = new ArrayList<>();

    public Map<Integer,Item> inventory;

    public int damageTime = 0;

    public boolean isSpawnFire = false;

    public String playerName;

    public boolean isInvisibility = false;

    public int lastGameMode = 0;

    public int lastExp = 0;

    public boolean fastPlace = true;


    /**
     *
     * 封装经验金钱 省的面对其他实体报错
     */
    public int exp;

    public Map<Integer,Item> eInventory;


    public LinkedHashMap<Integer,Item> armor = new LinkedHashMap<Integer,Item>(){
        {
            put(0,new ItemHelmetLeather());
            put(1,new ItemChestplateLeather());
            put(2,new ItemLeggingsLeather());
            put(3,new ItemBootsLeather());
        }
    };

    public PlayerInfo(EntityHuman player){
        this.player = player;
        this.playerName = player.getName();
    }


    public void setPlayer(EntityHuman player){
        this.player = player;
    }


    public EntityHuman getPlayer() {
        return player;

    }

    public void setLeave(boolean leave) {
        isLeave = leave;
        if(leave){
            playerType = PlayerType.LEAVE;

        }else{
            if(getGameRoom().getType() == GameRoom.GameType.WAIT){
                playerType = PlayerType.WAIT;
            }
            if(getGameRoom().getType() == GameRoom.GameType.START){
                playerType = PlayerType.START;
            }
        }
    }

    public boolean isLeave() {
        return isLeave;
    }

    public void setGameRoom(GameRoom gameRoom) {
        this.gameRoom = gameRoom;
    }

    public void setPlayerType(PlayerType playerType) {
        this.playerType = playerType;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public GameRoom getGameRoom() {
        return gameRoom;
    }



    public enum PlayerType{
        /**
         * WAIT: 等待 START: 开始 DEATH: 死亡(等待复活)  LEAVE: 离开 WATCH 观察(真正的死亡)
         * */
        WAIT,START,DEATH,LEAVE,WATCH
    }

    public Level getLevel(){
        return player.getLevel();
    }

    public Location getLocation(){
        return player.getLocation();
    }

    public BlockFace getHorizontalFacing(){
        return player.getHorizontalFacing();
    }

    public String getName(){
        return player.getName();
    }

    public Position getPosition(){
        return player.getPosition();
    }

    public void setTeamInfo(TeamInfo teamInfo) {
        this.teamInfo = teamInfo;
    }

    /**
     * 发送强制信息
     * */
    public void sendForceMessage(String msg){
        BedWarMain.sendMessageToObject(msg, getPlayer());
    }

    /**
     * 发送强制信息
     * */
    public void sendForceTitle(String msg){
       if(player instanceof Player){
           ((Player) player).sendTitle(TextFormat.colorize('&',msg));
       }
    }

    /**
     * 发送强制信息
     * */
    public void sendForceTitle(String msg,int time){
        if(player instanceof Player){
            ((Player) player).sendTitle(TextFormat.colorize('&',msg),null,0,time,0);
        }
    }

    /**
     * 发送强制信息
     * */
    public void sendForceSubTitle(String msg){
        if(player instanceof Player){
            ((Player) player).setSubtitle(TextFormat.colorize('&',msg));
        }
    }


    public int formatSecond(int seconds){
        return seconds * 20;
    }


    public void setDamageByInfo(PlayerInfo damageByInfo) {
        if(damageByInfo != null) {

            this.damageByInfo = damageByInfo;
            damageTime = 5;
            assistsPlayers.put(damageByInfo,System.currentTimeMillis());
            //现身
            if(isInvisibility){
                visibilityArmor();
            }
            //攻击者也得现身
            if(damageByInfo.isInvisibility){
                damageByInfo.visibilityArmor();
            }
            getPlayer().removeEffect(14);
        }
    }

    public PlayerInfo getDamageByInfo() {
        return damageByInfo;
    }

    public int getKillCount() {
        return killCount;
    }

    public int getBedBreakCount() {
        return bedBreakCount;
    }

    public int getEndKillCount() {
        return endKillCount;
    }

    public LinkedHashMap<Integer, Item> getArmor() {
        return armor;
    }

    public int getLoadTime() {
        return loadTime;
    }

    public int getSpawnTime() {
        return spawnTime;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    public void setKillCount(int killCount) {
        this.killCount = killCount;
    }

    public void setAssists(int assists) {
        this.assists = assists;
    }

    public int getAssists() {
        return assists;
    }

    public void setArmor(LinkedHashMap<Integer, Item> armor) {
        this.armor = armor;
    }

    public void setBedBreakCount(int bedBreakCount) {
        this.bedBreakCount = bedBreakCount;
    }

    public void setEndKillCount(int endKillCount) {
        this.endKillCount = endKillCount;
    }

    public void setLoadTime(int loadTime) {
        this.loadTime = loadTime;
    }

    public void setSpawnTime(int spawnTime) {
        this.spawnTime = spawnTime;
    }

    /**
     * 发送无前缀信息
     * */
    public void sendTipMessage(String msg){
        if(getGameRoom() != null) {
            if (cancel || isLeave  || getGameRoom().getType() == GameRoom.GameType.END) {
                return;
            }
            BedWarMain.sendTipMessageToObject(msg, getPlayer());
        }
    }
    /**
     * 发送信息
     * */
    public void sendMessage(String msg){
        if(getGameRoom() != null) {
            if (cancel || isLeave  || getGameRoom().getType() == GameRoom.GameType.END) {
                return;
            }
            BedWarMain.sendMessageToObject(msg, getPlayer());
        }
    }
    /**
     * 发送音效
     * */
    public void addSound(Sound sound){
        if(cancel || isLeave){
            return;
        }
        addSound(sound,1f,1f);
    }
    /**
     * 发送音效
     * */
    public void addSound(Sound sound,float v1,float v2){
        if(cancel || isLeave){
            return;
        }
        if(getPlayer() instanceof Player){
            getPlayer().getLevel().addSound(getPlayer(),sound,v1,v2,(Player)getPlayer());
        }

    }
    /**
     * 发送音效
     * */
    public void addSound(cn.nukkit.level.sound.Sound sound){
        if(cancel || isLeave){
            return;
        }
        if(getPlayer() instanceof Player){
            getPlayer().getLevel().addSound(sound,(Player) getPlayer());
        }

    }


    /**
     * 增加效果
     * */
    public void addEffect(Effect effect) {
        if(cancel || isLeave){
            return;
        }
        getPlayer().addEffect(effect);
    }

    /**
     * 发送信息
     * */
    public void sendTitle(String msg,int time){
        if(cancel || isLeave){
            return;
        }
        if(getPlayer() instanceof Player) {
            BedWarMain.sendTitle(msg, time * 20, (Player) getPlayer());
        }
    }
    /**
     * 发送信息
     * */
    public void sendTitle(String msg){
        sendTitle(msg,1);
    }
    /**
     * 发送信息
     * */
    public void sendSubTitle(String msg){
        if(cancel || isLeave ){
            return;
        }
        if(getPlayer() instanceof Player) {
            BedWarMain.sendSubTitle(msg, ((Player) getPlayer()));
        }
    }

    /**
     * 扣除经验值
     * @param exp 经验
     * @return 是否扣除成功
     */
    public boolean reduceExp(int exp){
        if(this.exp >= exp){
            this.exp -= exp;
            if(player instanceof Player){
                ((Player) player).setExperience(0, this.exp);
            }
            return true;
        }
        return false;
    }

    /**
     * 增加经验
     * @param exp 经验值
     */
    public void addExp(int exp){
          //至少还可以屏蔽非法经验
        this.exp += exp;
        addSound(Sound.RANDOM_ORB);
        if(player instanceof Player){
            ((Player) player).setExperience(0,this.exp);
        }

    }

    public void removeScoreBoard(){
        if(gameRoom != null) {
            if (gameRoom.getScoreboards().containsKey(this)) {
                if (getPlayer() instanceof Player) {
                    ScoreboardAPI.removeScorebaord((Player) getPlayer(),
                            gameRoom.getScoreboards().get(this));
                    gameRoom.getScoreboards().remove(this);
                }

            }
        }
    }

    //玩家盔甲隐身
    public void invisibilityArmor(){
        isInvisibility = true;
        MobArmorEquipmentPacket mobArmorEquipmentPacket = new MobArmorEquipmentPacket();
        mobArmorEquipmentPacket.eid = player.getId();
        for (int i = 0; i < 4; i++) {
            mobArmorEquipmentPacket.slots[i] = Item.get(0);
        }
        sendPacketToOtherPlayer(mobArmorEquipmentPacket);

    }
    //玩家盔甲现身
    public void visibilityArmor(){
        isInvisibility = false;
        MobArmorEquipmentPacket mobArmorEquipmentPacket = new MobArmorEquipmentPacket();
        mobArmorEquipmentPacket.eid = player.getId();
        for (int i = 0; i < 4; i++) {
            mobArmorEquipmentPacket.slots[i] = player.getInventory().getArmorContents()[i];
        }
        sendPacketToOtherPlayer(mobArmorEquipmentPacket);

    }

    public void sendPacketToOtherPlayer(DataPacket dataPacket){
        List<Player> sendPlayers = new ArrayList<>();
        if(getGameRoom() != null && getGameRoom().getType() == GameRoom.GameType.START){
            for(PlayerInfo playerInfo: getGameRoom().getPlayerInfos()){
                if(playerInfo.player instanceof Player){
                    if(!playerInfo.player.equals(player)) {
                        sendPlayers.add((Player) playerInfo.player);
                    }
                }

            }
            Server.broadcastPacket(sendPlayers.toArray(new Player[0]),dataPacket);
        }
    }

    public boolean isInRoom(){
        return !cancel && !isLeave;
    }

    public boolean isLive(){
        return !cancel && !isLeave && playerType != PlayerType.WATCH;
    }

    public void sendScore(ScoreBoardMessage message){
        if(getPlayer() instanceof Player) {
            if (message == null || getPlayer() == null || !((Player) getPlayer()).isOnline()) {
                removeScoreBoard();
                return;
            }
            if (((Player) getPlayer()).isOnline()) {
                try {
                    Scoreboard scoreboard = ScoreboardAPI.createScoreboard();
                    String title = message.getTitle();
                    ScoreboardDisplay scoreboardDisplay = scoreboard.addDisplay(DisplaySlot.SIDEBAR,
                            "dumy", TextFormat.colorize('&', title));

                    ArrayList<String> list = message.getLore();
                    for (int line = 0; line < list.size(); line++) {
                        String s = list.get(line);

                        scoreboardDisplay.addLine(TextFormat.colorize('&', s), line);
                    }
                    try {
                        gameRoom.getScoreboards().get(this).hideFor((Player) player);
                    } catch (Exception ignored) {
                    }
                    scoreboard.showFor((Player) player);
                    gameRoom.getScoreboards().put(this, scoreboard);
                } catch (Exception ignored) {
                }
            }
        }

    }

    /**
     * 发送信息
     * */
    public void sendActionBar(String msg){
        if(cancel || isLeave){
            return;
        }
        if(getPlayer() instanceof Player) {
            ((Player) getPlayer()).sendActionBar(TextFormat.colorize('&',msg));
        }
    }

    /**
     * 发送信息
     * */
    public void sendTip(String msg){
        if(cancel || isLeave){
            return;
        }
        if(getPlayer() instanceof Player) {
            BedWarMain.sendTip(msg, (Player) getPlayer());
        }
    }

    /**
     * 取消
     * */
    public void cancel(){
        leave();
        cancel = true;
        disable = true;
        setGameRoom(null);
    }



    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PlayerInfo){
            return ((PlayerInfo) obj).getPlayer().getName().equalsIgnoreCase(getPlayer().getName());
        }
        return false;
    }
    @Override
    public String toString(){
        PlayerData data = BedWarMain.getDataManager().getData(getName());
        String teamName = "&r";
        String playerName = "&7"+player.getName();
        if(teamInfo != null && !isWatch()){
            teamName = "&7[&r"+teamInfo.getTeamConfig().getNameColor()+teamInfo.getTeamConfig().getName()+"&7]&r";
            playerName = teamInfo.getTeamConfig().getNameColor()+" &7"+player.getName();
        }else if(isWatch()){
            teamName = "&7[旁观] ";
        }

        return "&7["+data.getLevelString()+"&7]&r "+teamName+playerName;
    }

    public TeamInfo getTeamInfo() {
        return teamInfo;
    }

    private int spawnTime = 0;



    private ArrayList<String> getLore(boolean isWait){
        ArrayList<String> lore = new ArrayList<>();
        String levelName = BedWarMain.getMenuRoomManager().getNameByRoom(gameRoom.getRoomConfig());
        if(levelName == null){
            levelName = " -- ";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        lore.add("&7"+format.format(new Date()));
        lore.add(BedWarMain.getLanguage().getLanguage("scoreboard-line-game-world",
                "游戏模式: &a[1]",levelName));

        lore.add(" ");
        if(isWait){
            //玩家等待时的计分板显示内容
            lore.add(BedWarMain.getLanguage().getLanguage("scoreboard-line-wait-player",
                    "玩家数: &a[1] &r/&a [2]",
                    gameRoom.getPlayerInfos().size()+"",
                    gameRoom.getRoomConfig().getMaxPlayerSize()+""));
//            lore.add("玩家数: &a"+gameRoom.getPlayerInfos().size()+" &r/&a "+gameRoom.getRoomConfig().getMaxPlayerSize());
            lore.add(BedWarMain.getLanguage().getLanguage("scoreboard-line-waiting","等待中...."));
            lore.add("   ");

        }else{
            IGameRoomEvent event = getGameRoom().getEventControl().getNextEvent();
            if(event != null){
                lore.add(event.display()+" &a"+ Utils.formatTime1((event.getEventTime() - getGameRoom().getEventControl().loadTime)));
                lore.add("    ");
            }else{

                lore.add(BedWarMain.getLanguage().getLanguage("scoreboard-line-game-end","游戏结束: &a[1]",
                        Utils.formatTime(getGameRoom().loadTime)
                ));
            }

            for(TeamInfo teamInfo: gameRoom.getTeamInfos()){
                String me = "";
                if(getTeamInfo() != null && getTeamInfo().equals(teamInfo)){
                    me = BedWarMain.getLanguage().getLanguage("scoreboard-line-myself","&7(我)");
                }
                if(teamInfo.isBadExists() && teamInfo.isLoading()){

                    lore.add(BedWarMain.getLanguage().getLanguage("scoreboard-line-team-info-exits-bed",
                            "◎ [1]: &r    &a[2] &r[3] {me}",teamInfo.toString(),
                            teamInfo.getTeamConfig().getTeamConfig().getBedNormal(),
                            teamInfo.getLivePlayer().size()+""
                            ).replace("{me}",me));
                }else if(!teamInfo.isBadExists() && teamInfo.isLoading()){

                    lore.add(BedWarMain.getLanguage().getLanguage("scoreboard-line-team-info-no-bed",
                            "◎ [1]: &r    &a[2] &r[3] {me}",teamInfo.toString(),
                            teamInfo.getTeamConfig().getTeamConfig().getBedNormal(),
                            teamInfo.getLivePlayer().size()+""
                    ).replace("{me}",me));
                }else{

                    lore.add(BedWarMain.getLanguage().getLanguage("scoreboard-line-team-info-disuse",
                            "◎ [1]: &r    &a[2] &r[3] {me}",teamInfo.toString(),
                            teamInfo.getTeamConfig().getTeamConfig().getBedDestroy(),
                            teamInfo.getLivePlayer().size()+""
                    ).replace("{me}",me));
                }
            }
            lore.add("      ");
            lore.add(BedWarMain.getLanguage().getLanguage("scoreboard-line-kill","击杀数: &a[1]",killCount+""));
            lore.add(BedWarMain.getLanguage().getLanguage("scoreboard-line-assists","助攻数: &a[1]",assists+""));
//            lore.add("破坏床数: &a"+bedBreakCount);
            lore.add(BedWarMain.getLanguage().getLanguage("scoreboard-line-break-bed","破坏床: &a[1]",bedBreakCount+""));

            lore.add("        ");
        }
        Object obj = BedWarMain.getBedWarMain().getConfig().get("game-logo");
        if(obj instanceof List){
            for(Object s : (List<?>)obj){
                lore.add(s.toString());
            }
        }else{
            lore.add(BedWarMain.getBedWarMain().getConfig().getString("game-logo","&l&cT&6o&eC&ar&ba&9f&dt"));
        }
        return lore;
    }
    private int loadTime = 0;

    private boolean isSendkey = false;

    /**
     * 定时任务
     * */
    public void onUpdate(){
        if(gameRoom == null ||  gameRoom.getType() == GameRoom.GameType.END){
            return;
        }


        updateTime++;
        if(isWatch()){
            sendActionBar(BedWarMain.getLanguage().getLanguage("player-is-watch","&c你当前是一名观察者 &b/bw quit 离开房间"));
            if(player instanceof Player){
                if(((Player) player).getGamemode() != 3){
                    ((Player) player).setGamemode(3);
                }
                if(gameRoom != null && gameRoom.getType() == GameRoom.GameType.START && Server.getInstance().isLevelLoaded(getGameRoom().worldInfo.getConfig().getLevel())){
                    if(player.getLevel() != gameRoom.getWorldInfo().getConfig().getGameWorld()){
                        Position position = gameRoom.getTeamInfos().get(0).getTeamConfig().getBedPosition();
                        position.add(0,64,0);
                        position.level = gameRoom.getWorldInfo().getConfig().getGameWorld();
                        player.teleport(position);
                    }
                }

            }
        }else{
            if(gameRoom.getType() == GameRoom.GameType.START) {
                if (gameRoom.getRoomConfig().minutesExp > 0) {
                    if (updateTime >= 60) {
                        //每 60s 增加25经验
                        PlayerData data = BedWarMain.getDataManager().getData(getName());
                        data.addExp(gameRoom.getRoomConfig().minutesExp, BedWarMain.getLanguage().getLanguage("player-exp-time-award","时长奖励"));
                        updateTime = 0;
                    }
                }
            }
        }
        if(damageTime > 0){
            damageTime--;
        }else{
            damageByInfo = null;
        }
        if(damageByInfo != null){
            sendTip(damageByInfo+"  &a"+damageByInfo.getPlayer().getHealth()+" / "+damageByInfo.getPlayer().getMaxHealth());
        }
        //助攻间隔
        LinkedHashMap<PlayerInfo,Long> ass = new LinkedHashMap<>(assistsPlayers);
        for(Map.Entry<PlayerInfo,Long> entry: ass.entrySet()){
            if(System.currentTimeMillis() - entry.getValue() > 3000){
                assistsPlayers.remove(entry.getKey());
            }
        }
        //TODO 玩家身上隐身效果主动消失后 还原装备
        if(!player.hasEffect(14) && isInvisibility){
            //现身
           visibilityArmor();
        }else if(player.hasEffect(14) && isInvisibility){
            //隐身没收玩家身上的盔甲
            invisibilityArmor();
        }
        if(playerType == PlayerType.DEATH){
            if(spawnTime >= 5){

                sendTitle(BedWarMain.getLanguage().getLanguage("player-respawn-info","&a你复活了"),1);
                sendSubTitle("");
                spawn();
                spawnTime = 0;
            }else{
                if(spawnTime == 0 && !isSendkey){
                    isSendkey = true;
                    if(gameRoom != null) {
                        sendTitle(BedWarMain.getLanguage().getLanguage("player-death-info-title","&c你死了"), gameRoom.reSpawnTime);
                    }
                }
                if(gameRoom != null) {
                    sendSubTitle(BedWarMain.getLanguage().getLanguage("player-death-respawn-info-title",
                            "[1] 秒后复活",(gameRoom.reSpawnTime - spawnTime)+""));
                }
                spawnTime++;
            }

        }else if(playerType == PlayerType.START){
            //TODO 游戏开始后 可以弄一些buff
            if(player instanceof Player){
                if(loadTime < 5){
                    loadTime++;
                }else{
                    if(gameRoom.getRoomConfig().enableFood) {
                        ((Player) player).getFoodData().useHunger(1);
                    }
                    loadTime = 0;
                }

            }
//            "&7["+teamInfo.toString()+"&7] "+teamInfo.getTeamConfig().getNameColor()+player.getName()+" \n&c❤&7"+String.format("%.1f",player.getHealth())
            //TODO 自定义头部
            player.setNameTag(
                    TextFormat.colorize('&',gameRoom.getRoomConfig().customNamedTag
                            .replace("{team}",teamInfo.toString())
                            .replace("{color}",teamInfo.getTeamConfig().getNameColor())
                            .replace("{name}",player.getName())
                            .replace("{health}",String.format("%.1f",player.getHealth()))
                            .replace("{maxhealth}",player.getMaxHealth()+""))
            );


        }else if(playerType == PlayerType.WAIT){
            if(getGameRoom().getRoomConfig().getWorldInfo().getWaitPosition().getY() - player.getY() > getGameRoom().getRoomConfig().callbackY){
                if(getGameRoom().getRoomConfig().getWorldInfo().getWaitPosition() == null){
                    if(getGameRoom() != null){
                        getGameRoom().quitPlayerInfo(this,true);
                        sendMessage("&c房间出现了错误 （未识别到等待大厅）已将你送回出生点");
                    }
                    return;
                }
                player.teleport(getGameRoom().getRoomConfig().getWorldInfo().getWaitPosition());
            }
        }
        try{
            Class.forName("de.theamychan.scoreboard.api.ScoreboardAPI");
        }catch (Exception e){
            return;
        }
        if(playerType != PlayerType.LEAVE){
            //TODO 计分板的一些内容

            ScoreBoardMessage boardMessage = new ScoreBoardMessage(BedWarMain.getScoreBoardTitle());
            boardMessage.setLore(getLore(playerType == PlayerType.WAIT));
            sendScore(boardMessage);
        }else{
            sendScore(null);
        }
        //TODO 快速搭路提示
        if(getGameRoom().getRoomConfig().fastPlace && player.getInventory().getItemInHand().getId() == 35){
            sendTip("&e快速搭路模式: "+(fastPlace?"&a开启":"&c关闭"));
        }
    }

    @Override
    public int hashCode() {
        return player.hashCode();
    }

    public void death(EntityDamageEvent event){

        player.setHealth(player.getMaxHealth());
        if(player instanceof Player){
            ((Player) player).removeAllWindows();
            ((Player) player).getUIInventory().clearAll();
        }
        //死亡检测一下是否为最后存活
        if(gameRoom.getLivePlayers().size() == 1){
            //免疫死亡
            spawn();
            return;
        }

        PlayerGameDeathEvent event1 = new PlayerGameDeathEvent(this,getGameRoom(),BedWarMain.getBedWarMain());
        Server.getInstance().getPluginManager().callEvent(event1);
        if(getPlayer() instanceof Player) {
            ((Player) getPlayer()).setGamemode(3);
        }else{
            getPlayer().despawnFromAll();
        }

        player.removeAllEffects();
        if(getGameRoom().getWorldInfo().getConfig().getGameWorld() == null){
            return;
        }
        player.teleport(getGameRoom().worldInfo.getConfig().getGameWorld().getSafeSpawn());
        player.teleport(new Position(player.x,teamInfo.getTeamConfig().getBedPosition().y + 64,player.z,getLevel()));
        sendTitle(BedWarMain.getLanguage().getLanguage("player-death-info-title","&c你死了"));
        deathCount++;
        boolean end = !teamInfo.isBadExists();

        if(event != null) {
            OnScreenTextureAnimationPacket packet = new OnScreenTextureAnimationPacket();
            packet.effectId = getGameRoom().getRoomConfig().deathIcon;
            if(getPlayer() instanceof Player) {
                ((Player)getPlayer()).dataPacket(packet);
            }

            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if(damageByInfo != null){
                    gameRoom.sendMessage(BedWarMain.getLanguage().getLanguage("player-death-by-player-void",
                            "[1] &e被 &r[2] 推入虚空。",
                            this.toString(),
                            damageByInfo.toString()
                            )+" "+(end?BedWarMain.getLanguage().getLanguage("player-death-by-player-kill-final",
                            "&b&l最终击杀!"):""));
//                    gameRoom.sendMessage(this + " &e被 &r" + damageByInfo + " 推入虚空。"+(end?" &b&l最终击杀!":""));
                    addKill(damageByInfo,end);
                }
                gameRoom.sendMessage(BedWarMain.getLanguage().getLanguage("player-death-by-void", "&e掉入虚空",this.toString()));

            } else if (event instanceof EntityDamageByEntityEvent) {
                Entity entity = ((EntityDamageByEntityEvent) event).getDamager();
                if (entity instanceof EntityHuman) {
                    PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo((Player) entity);
                    String killInfo = BedWarMain.getLanguage().getLanguage("death-by-damage","击杀");
                    if(event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE){
                        killInfo = BedWarMain.getLanguage().getLanguage("death-by-arrow","射杀");
                    }
                    if (info != null) {
                        addKill(info,end);
                        gameRoom.sendMessage(BedWarMain.getLanguage().getLanguage("player-kill-player-info",
                                "[1] &e被 &r[2] [3]了。",
                                this.toString(),
                                info.toString(),
                                killInfo
                                )+" "+(end?BedWarMain.getLanguage().getLanguage("player-death-by-player-kill-final",
                                "&b&l最终击杀!"):""));
                    }
                } else {
                    gameRoom.sendMessage(this + " &e被 &r" + entity.getName() + " 击败了"+" "+(end?BedWarMain.getLanguage().getLanguage("player-death-by-player-kill-final",
                            "&b&l最终击杀!"):""));
                }
            } else {
                if(damageByInfo != null){
                    addKill(damageByInfo,end);
                    gameRoom.sendMessage(
                            BedWarMain.getLanguage().getLanguage("player-death-by-player-kill",
                                    "[1] &e被 &r[2] 击败了",
                                    this.toString(),
                                    damageByInfo.toString()
                                    )+" "+(end?BedWarMain.getLanguage().getLanguage("player-death-by-player-kill-final",
                            "&b&l最终击杀!"):""));
                }else {
                    String deathInfo = BedWarMain.getLanguage().getLanguage("player-death-info-unknown","&e死了");
                    switch (event.getCause()){
                        case LAVA:
                            deathInfo =  BedWarMain.getLanguage().getLanguage("player-death-info-lava","&e被岩浆烧死了");
                            break;
                        case FALL:
                            deathInfo = BedWarMain.getLanguage().getLanguage("player-death-info-fall","&e摔死了");
                            break;
                        case FIRE:
                            deathInfo =BedWarMain.getLanguage().getLanguage("player-death-info-fire","&e被烧了");
                            break;
                        case HUNGER:
                            deathInfo = BedWarMain.getLanguage().getLanguage("player-death-info-hunger","&e饿死了");
                            break;
                            default:break;
                    }
                    gameRoom.sendMessage(this +deathInfo);
                }
            }
            if(player.getLastDamageCause() instanceof EntityDamageByEntityEvent){
                Entity last = ((EntityDamageByEntityEvent) player.getLastDamageCause()).getDamager();
                if(last instanceof IronGolem){
                    PlayerInfo dem = ((IronGolem) last).getMaster();
                    addKill(dem,end);

                }

            }



        }
        if(end && getGameRoom().getType() != GameRoom.GameType.END){
            gameRoom.sendMessage(BedWarMain.getLanguage().getLanguage("player-end", "[1] &e淘汰了",this.toString()));
        }
        if(teamInfo.isBadExists()){
            playerType = PlayerType.DEATH;
        }else{
            //TODO 死亡后的观察模式
            playerType = PlayerType.WATCH;

        }
        damageByInfo = null;
        player.getInventory().clearAll();
        player.getOffhandInventory().clearAll();
        if(playerType == PlayerType.WATCH){
            getGameRoom().joinWatch(this);
        }



    }

    /**
     * 玩家数据初始化
     * */
    public void init(){
        if(BedWarMain.getBedWarMain().getConfig().getBoolean("save-playerInventory",true)){
            inventory = getPlayer().getInventory().getContents();
            eInventory = getPlayer().getEnderChestInventory().getContents();
        }
        getPlayer().setHealth(getPlayer().getMaxHealth());
        if(getPlayer() instanceof Player) {
            ((Player)getPlayer()).getFoodData().reset();
            ((Player) getPlayer()).removeAllWindows();
            ((Player) getPlayer()).setExperience(0,0);
            //记录信息
            lastGameMode = ((Player) getPlayer()).getGamemode();
            //记录经验值
            lastExp = ((Player) getPlayer()).getExperience();

        }
        getPlayer().getInventory().clearAll();
        getPlayer().getEnderChestInventory().clearAll();

        //TODO 给玩家物品
        getPlayer().getInventory().setHeldItemSlot(0);
    }

    private void addKill(PlayerInfo info,boolean end){
        if(!end) {
            info.killCount++;
        }else{
            info.endKillCount++;
            info.killCount++;
        }
        if(!info.getPlayer().closed){
            if(isInRoom()){
                if(getGameRoom().getRoomConfig().isExp()){
                    int e = (int) (exp * getGameRoom().getRoomConfig().killItem);
                    if(e > 0){
                        info.addExp(e);
                        info.sendMessage("&a +"+e+" Exp");
                    }
                }else{
                    for(ItemInfoConfig config: getGameRoom().getRoomConfig().getWorldInfo().getItemInfos()){
                        MoneyItemInfoConfig moneyItemInfoConfig = config.getMoneyItemInfoConfig();
                        double c = ItemInfo.getCountByInventory(moneyItemInfoConfig,player.getInventory());
                        double g = 0;
                        if(c > 0){
                            g = (c * getGameRoom().getRoomConfig().getKillItem());
                        }
                        if(g > 0){
                            Item item = moneyItemInfoConfig.getItem();
                            item.setCount((int)g);
                            info.getPlayer().getInventory().addItem(item);
                            String name = moneyItemInfoConfig.getCustomName();
                            String color = name.substring(0,2);
                            info.sendMessage(color + " + "+((int)g)+" "+name);
                        }
                    }
                }
            }
        }
        //助攻累计
        for(PlayerInfo playerInfo: assistsPlayers.keySet()){
            if(playerInfo.equals(info)){
                continue;
            }
            playerInfo.assists++;
        }
    }

    public boolean isPlayer(){
        return player instanceof Player;
    }


    public boolean isWatch(){
        return  playerType == PlayerType.WATCH;
    }

    public boolean  isDeath(){
        return  playerType == PlayerType.DEATH;
    }

    private void leave(){
        clear();
        if(getTeamInfo() != null){
            getTeamInfo().quit(this);
        }
        sendScore(null);
        setLeave(true);
        playerType = PlayerType.LEAVE;
    }

    public void spawn() {
        if(isSendkey){
            isSendkey = false;
        }
        if (player instanceof Player) {
            ((Player) player).setFoodEnabled(gameRoom.getRoomConfig().enableFood);

            ((Player) player).getFoodData().reset();

            if (!((Player) player).isOnline()) {
                playerType = PlayerType.LEAVE;
                return;
            }
        }else{
            if(!player.isAlive()){
                player.respawnToAll();

            }
        }

        player.getInventory().clearAll();
        this.exp = 0;
        isInvisibility = false;


        boolean teleport;
        try {
            teleport = player.teleport(teamInfo.getTeamConfig().getSpawnPosition());
        }catch (Exception e){
            e.printStackTrace();
            teleport = false;
        }
        if(!teleport && player instanceof Player) {
            throw new NullPointerException("无法将玩家传送到队伍出生点");

        }
        if (getPlayer() instanceof Player) {
            ((Player) getPlayer()).setGamemode(0);
            if(gameRoom.getRoomConfig().isExp()){
                ((Player) getPlayer()).setExperience(0,0);
            }

        }
        putPlayerArmor();
        player.getInventory().addItem(new ItemSwordWood());
        playerType = PlayerType.START;

    }



    public void putPlayerArmor(){
        //TODO 初始装备
        for (Map.Entry<Integer, Item> entry : armor.entrySet()) {
            Item item;
            if(entry.getValue() instanceof ItemColorArmor){
                ItemColorArmor colorArmor = (ItemColorArmor) entry.getValue();
                colorArmor.setColor(getTeamInfo().getTeamConfig().getRgb());
                item = colorArmor;
            }else{
                item = entry.getValue();
            }
            CompoundTag compoundTag = item.getNamedTag();
            if(compoundTag == null){
                compoundTag = new CompoundTag();
            }
            if(gameRoom != null){
                if(gameRoom.getRoomConfig().isInventoryUnBreakable()){
                    //无限耐久
                    compoundTag.putByte("Unbreakable",1);
                    item.setNamedTag(compoundTag);
                }

            }


            player.getInventory().setArmorItem(entry.getKey(), item);
        }
    }

    public void clear(){
        if(player instanceof Player){
            if(((Player) player).isOnline()){
                player.setNameTag(player.getName());
                player.getInventory().clearAll();
                player.getEnderChestInventory().clearAll();
                player.removeAllEffects();
                ((Player) player).getFoodData().reset();
                player.setHealth(player.getMaxHealth());
                ((Player) player).setExperience(lastExp);
                if(inventory != null && eInventory != null){
                    player.getInventory().setContents(inventory);
                    player.getEnderChestInventory().setContents(eInventory);
                }
                if(getPlayer() instanceof Player) {
                    ((Player) getPlayer()).setGamemode(lastGameMode);
                }

                //清空缓存
                RoomManager.CACHE_INFO.remove(playerName);
            }
        }
    }

    public void putEffect(ArrayList<TeamEffectInfo> effects){
        if(player instanceof Player && !((Player) player).isOnline()){
            return;
        }
        for(TeamEffectInfo effect : effects){
            if(effect.getEffect() instanceof TeamEffect){
                player.addEffect(((TeamEffect) effect.getEffect()).getEffect().setDuration(80).setAmplifier(effect.getLevel()));
            }
            if(effect.getEffect() instanceof TeamEnchant){
                for(Map.Entry<Integer,Item> item:player.getInventory().getContents().entrySet()){
                    Enchantment enchantment = ((TeamEnchant) effect.getEffect()).getEnchantment();
                    if(enchantment.canEnchant(item.getValue())){
                        if(item.getValue().hasEnchantment(enchantment.getId())){
                            if(item.getValue().getEnchantment(enchantment.getId()).getLevel() >= effect.getLevel()){
                                continue;
                            }
                        }
                        //TODO 修复附魔高一级的BUG
                        item.getValue().addEnchantment(enchantment.setLevel(effect.getLevel() ));
                        player.getInventory().setItem(item.getKey(),item.getValue());
                    }
                }

            }
        }

    }
}
