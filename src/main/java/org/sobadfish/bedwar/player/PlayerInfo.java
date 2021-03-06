package org.sobadfish.bedwar.player;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.event.entity.EntityDamageByEntityEvent;
import cn.nukkit.event.entity.EntityDamageEvent;

import cn.nukkit.inventory.PlayerEnderChestInventory;
import cn.nukkit.inventory.PlayerInventory;
import cn.nukkit.item.*;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.math.BlockFace;
import cn.nukkit.utils.TextFormat;
import de.theamychan.scoreboard.api.ScoreboardAPI;
import de.theamychan.scoreboard.network.DisplaySlot;
import de.theamychan.scoreboard.network.Scoreboard;
import de.theamychan.scoreboard.network.ScoreboardDisplay;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.IronGolem;
import org.sobadfish.bedwar.event.PlayerGameDeathEvent;
import org.sobadfish.bedwar.item.button.RoomQuitItem;
import org.sobadfish.bedwar.item.team.TeamEffect;
import org.sobadfish.bedwar.item.team.TeamEffectInfo;
import org.sobadfish.bedwar.item.team.TeamEnchant;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.message.ScoreBoardMessage;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.thread.BaseTimerRunnable;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author SoBadFish
 */
public class PlayerInfo {

    private int killCount = 0;

    public int bedBreakCount = 0;

    private int endKillCount = 0;

    private EntityHuman player;

    private PlayerType playerType;

    private GameRoom gameRoom;

    private TeamInfo teamInfo;

    public boolean cancel;

    public boolean disable;

    public boolean isLeave;

    private PlayerInfo damageByInfo = null;

    public PlayerInventory inventory;


    /**
     * 
     * 封装经验金钱 省的面对其他实体报错
     */
    public int exp;

    public PlayerEnderChestInventory eInventory;

    private LinkedHashMap<Integer,Item> armor = new LinkedHashMap<Integer,Item>(){
        {
            put(0,new ItemHelmetLeather());
            put(1,new ItemChestplateLeather());
            put(2,new ItemLeggingsLeather());
            put(3,new ItemBootsLeather());
        }
    };

    public PlayerInfo(EntityHuman player){
        this.player = player;
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
         * WAIT: 等待 START: 开始 DEATH: 死亡 WAIT_DEATH:等待复活 LEAVE: 离开 WATCH 观察
         * */
        WAIT,START,DEATH,WAIT_DEATH,LEAVE,WATCH
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

    public void setDamageByInfo(PlayerInfo damageByInfo) {
        if(damageByInfo != null) {
            this.damageByInfo = damageByInfo;
        }
        ThreadManager.addThread(new BaseTimerRunnable(5) {
            @Override
            protected void callback() {
                setDamageByInfo(null);
            }
        });
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
        getPlayer().getLevel().addSound(getPlayer(),sound);
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
        sendTitle(msg,2);
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
        if (gameRoom.getScoreboards().containsKey(this)) {
            if(getPlayer() instanceof Player) {
                ScoreboardAPI.removeScorebaord((Player) getPlayer(),
                        gameRoom.getScoreboards().get(this));
                gameRoom.getScoreboards().remove(this);
            }

        }
    }

    public boolean isInRoom(){
        return !cancel && !isLeave;
    }

    public boolean isLive(){
        return !cancel && !isLeave && playerType != PlayerType.DEATH;
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
        getGameRoom().getPlayerInfos().remove(this);
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
        String teamName = "&r";
        String playerName = "&7"+player.getName();
        if(teamInfo != null){
            teamName = "&7[&r"+teamInfo.getTeamConfig().getNameColor()+teamInfo.getTeamConfig().getName()+"&7]&r";
            playerName = teamInfo.getTeamConfig().getNameColor()+" &7"+player.getName();
        }
        return teamName+playerName;
    }

    public TeamInfo getTeamInfo() {
        return teamInfo;
    }

    private int spawnTime = 0;

    private String formatTime(int s){
        int min = s / 60;
        int ss = s % 60;
        if(min > 0){
            return min+" 分 "+ss+" 秒";
        }else{
            return ss+" 秒";
        }

    }

    private ArrayList<String> getLore(boolean isWait){
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        String levelName = BedWarMain.getMenuRoomManager().getNameByRoom(gameRoom.getRoomConfig());
        if(levelName == null){
            levelName = " -- ";
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
        lore.add("&7"+format.format(new Date()));
        lore.add("游戏模式: &a"+levelName);

        lore.add(" ");
        if(isWait){
            lore.add("玩家数: &a"+gameRoom.getPlayerInfos().size()+" &r/&a "+gameRoom.getRoomConfig().getMaxPlayerSize());
            lore.add("等待中....");
            lore.add("   ");

        }else{
            for(TeamInfo teamInfo: gameRoom.getTeamInfos()){
                if(teamInfo.isBadExists() && teamInfo.isLoading()){
                    lore.add("◎ "+teamInfo.toString()+":&r    &a✔ "+(getTeamInfo().equals(teamInfo)?"&7(我)":""));
                }else if(!teamInfo.isBadExists() && teamInfo.isLoading()){
                    lore.add("◎ "+teamInfo.toString()+": &r   &c"+teamInfo.getLivePlayer().size()+" "+(getTeamInfo().equals(teamInfo)?"&7(我)":""));
                }else{
                    lore.add("◎ "+teamInfo.toString()+": &r   &c✘ "+(getTeamInfo().equals(teamInfo)?"&7(我)":""));
                }
            }
            lore.add("   ");
            lore.add("击杀数: &a"+killCount);
            lore.add("最终击杀数: &a"+endKillCount);
            lore.add("破坏床数: &a"+bedBreakCount);
            lore.add("     ");
        }
        Object obj = BedWarMain.getBedWarMain().getConfig().get("game-logo");
        if(obj instanceof List){
            for(Object s : (List)obj){
                lore.add(s.toString());
            }
        }else if(obj instanceof String){
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
        if(playerType == PlayerType.WAIT_DEATH){
            if(spawnTime >= 5){

                sendTitle("&a你复活了",1);
                sendSubTitle("");
                spawn();
                spawnTime = 0;
            }else{
                if(spawnTime == 0 && !isSendkey){
                    isSendkey = true;
                    sendTitle("&c你死了",6);
                }
                sendSubTitle((5 - spawnTime)+" 秒后复活");
                spawnTime++;
            }

        }else if(playerType == PlayerType.START){
            //TODO 游戏开始后 可以弄一些buff
            if(player instanceof Player){
                if(loadTime < 5){
                    loadTime++;
                }else{
                    ((Player) player).getFoodData().useHunger(1);
                    loadTime = 0;
                }

            }
            player.setNameTag(TextFormat.colorize('&',"&7["+teamInfo.toString()+"&7] "+teamInfo.getTeamConfig().getNameColor()+player.getName()+" \n&c❤&7"+String.format("%.1f",player.getHealth())));


        }else if(playerType == PlayerType.WAIT){
            if(getGameRoom().getRoomConfig().getWorldInfo().getWaitPosition().getY() - player.getY() > getGameRoom().getRoomConfig().callbackY){
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
        player.getInventory().clearAll();
        player.getOffhandInventory().clearAll();



        if(teamInfo.isBadExists()){
            playerType = PlayerType.WAIT_DEATH;
        }else{
            playerType = PlayerType.DEATH;
            player.getInventory().setItem(RoomQuitItem.getIndex(),RoomQuitItem.get());
        }
        PlayerGameDeathEvent event1 = new PlayerGameDeathEvent(this,getGameRoom(),BedWarMain.getBedWarMain());
        Server.getInstance().getPluginManager().callEvent(event1);
        if(getPlayer() instanceof Player) {
            ((Player) getPlayer()).setGamemode(3);
        }
        player.teleport(new Position(player.x,teamInfo.getTeamConfig().getBedPosition().y + 64,player.z));
        sendTitle("&c你死了");

        if(event != null) {
            if (event.getCause() == EntityDamageEvent.DamageCause.VOID) {
                if(damageByInfo != null){
                    gameRoom.sendMessage(this + " &e被 &r" + damageByInfo + " 推入虚空。"+(playerType==PlayerType.DEATH?"&b&l最终击杀!":""));
                    addKill(damageByInfo);
                }
                gameRoom.sendMessage(this + "&e掉入虚空");

            } else if (event instanceof EntityDamageByEntityEvent) {
                Entity entity = ((EntityDamageByEntityEvent) event).getDamager();
                if (entity instanceof Player) {
                    PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo((Player) entity);
                    String killInfo = "击杀";
                    if(event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE){
                        killInfo = "射杀";
                    }
                    if (info != null) {
                        addKill(info);
                        gameRoom.sendMessage(this + " &e被 &r" + info + " "+killInfo+"了。"+(playerType==PlayerType.DEATH?"&b&l最终击杀!":""));
                    }
                } else {
                    gameRoom.sendMessage(this + " &e被 &r" + entity.getName() + " 击败了"+(playerType==PlayerType.DEATH?"&b&l最终击杀!":""));
                }
            } else {
                if(damageByInfo != null){
                    addKill(damageByInfo);
                    gameRoom.sendMessage(this + " &e被 &r" + damageByInfo + " 击败了"+(playerType==PlayerType.DEATH?"&b&l最终击杀!":""));
                }else {
                    String deathInfo = "&e死了";
                    switch (event.getCause()){
                        case LAVA:
                            deathInfo = "&e被岩浆烧死了";
                            break;
                        case FALL:
                            deathInfo = "&e摔死了";
                            break;
                        case FIRE:
                            deathInfo = "&e被烧了";
                            break;
                        case HUNGER:
                            deathInfo = "&e饿死了";
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
                    addKill(dem);

                }

            }

        }
        if(playerType == PlayerType.DEATH && getGameRoom().getType() != GameRoom.GameType.END){
            gameRoom.sendMessage(this + " &e淘汰了");
        }
        damageByInfo = null;

    }

    private void addKill(PlayerInfo info){
        if(playerType != PlayerType.DEATH) {
            info.killCount++;
        }else{
            info.endKillCount++;
        }

    }

    public boolean  isDeath(){
        return playerType == PlayerType.DEATH || playerType == PlayerType.WAIT_DEATH || playerType == PlayerType.LEAVE;
    }

    private void leave(){
        if(player instanceof Player){
            if(((Player) player).isOnline()){
                player.setNameTag(player.getName());
                player.getInventory().clearAll();
                ((Player) player).sendExperience(0);
                if(inventory != null && eInventory != null){
                    player.getInventory().setContents(inventory.getContents());
                    player.getEnderChestInventory().setContents(eInventory.getContents());
                }
                if(getPlayer() instanceof Player) {
                    ((Player) getPlayer()).setGamemode(0);
                }
            }
        }
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
            ((Player) player).setFoodEnabled(true);
            ((Player) player).getFoodData().reset();

            if (!((Player) player).isOnline()) {
                playerType = PlayerType.LEAVE;
                return;
            }
        }

        player.getInventory().clearAll();
        this.exp = 0;
        
        player.teleport(gameRoom.getRoomConfig().worldInfo.getGameWorld().getSpawnLocation());
        player.teleport(teamInfo.getTeamConfig().getSpawnPosition());
        if (getPlayer() instanceof Player) {
            ((Player) getPlayer()).setGamemode(0);
            if(gameRoom.getRoomConfig().isExp()){
                ((Player) getPlayer()).setExperience(0,0);
            }
            
        }
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

            player.getInventory().setArmorItem(entry.getKey(), item);
        }
        player.getInventory().addItem(new ItemSwordWood());
        playerType = PlayerType.START;

    }

    public void clear(){
        if(player instanceof Player){
            if(((Player) player).isOnline()) {
                ((Player) player).setExperience(0,0);
                ((Player) player).getUIInventory().clearAll();
                player.getEnderChestInventory().clearAll();
                player.getInventory().clearAll();
                ((Player) player).getFoodData().reset();
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
                        item.getValue().addEnchantment(enchantment.setLevel(effect.getLevel()));
                        player.getInventory().setItem(item.getKey(),item.getValue());
                    }
                }

            }
        }

    }
}
