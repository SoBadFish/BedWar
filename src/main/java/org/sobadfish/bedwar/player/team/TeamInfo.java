package org.sobadfish.bedwar.player.team;

import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.block.BlockBed;
import cn.nukkit.level.Position;
import cn.nukkit.level.Sound;
import cn.nukkit.potion.Effect;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.event.PlayerChoseTeamEvent;
import org.sobadfish.bedwar.event.TeamDefeatEvent;
import org.sobadfish.bedwar.event.TeamVictoryEvent;
import org.sobadfish.bedwar.item.team.TeamEffectInfo;
import org.sobadfish.bedwar.item.team.TeamTrap;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.player.team.config.TeamInfoConfig;
import org.sobadfish.bedwar.room.GameRoom;

import java.util.ArrayList;

/**
 * @author SoBadFish
 * 2022/1/2
 */

public class TeamInfo {

    private TeamInfoConfig teamConfig;

    private boolean badExists = true;

    //淘汰

    private boolean stop;

    private boolean close;

    private final GameRoom room;

    private ArrayList<PlayerInfo> teamPlayers = new ArrayList<>();

    public TeamInfo(GameRoom room,TeamInfoConfig teamConfig){
        this.teamConfig = teamConfig;
        this.room = room;
    }


    private final ArrayList<TeamEffectInfo> teamEffects = new ArrayList<>();



    public ArrayList<TeamEffectInfo> getTeamEffects() {
        return teamEffects;
    }

    public boolean isClose() {
        return close;
    }

    public boolean isBadExists() {
        return badExists;
    }

    public boolean isLoading() {
        return !stop;
    }

    public void setClose(boolean close) {
        this.close = close;
    }

    public void setBadExists(boolean badExists) {
        this.badExists = badExists;
    }


    public void setStop(boolean stop) {
        this.stop = stop;
    }

    public TeamInfoConfig getTeamConfig() {
        return teamConfig;
    }

    public void setTeamConfig(TeamInfoConfig teamConfig) {
        this.teamConfig = teamConfig;
    }

    public void setTeamPlayers(ArrayList<PlayerInfo> teamPlayers) {
        this.teamPlayers = teamPlayers;
    }

    public void sendMessage(String msg){
        teamPlayers.forEach(playerInfo ->
                playerInfo.sendMessage(msg));
    }

    public void sendFaceMessage(String msg){
        teamPlayers.forEach(playerInfo ->
                playerInfo.sendForceMessage(msg));
    }

    public void addEffect(TeamEffectInfo effect){
        if(teamEffects.contains(effect)){
            TeamEffectInfo ee = teamEffects.get(teamEffects.indexOf(effect));
            ee.setLevel(ee.getLevel() + 1);
        }else{
            teamEffects.add(effect);
        }
    }



    public ArrayList<PlayerInfo> getTeamPlayers() {
        teamPlayers.removeIf((p)->p.disable);
        return teamPlayers;
    }


    public ArrayList<PlayerInfo> getInRoomPlayer(){
        ArrayList<PlayerInfo> playerInfos = new ArrayList<>();
        for(PlayerInfo playerInfo: getTeamPlayers()){
            if(playerInfo.isInRoom()){
                playerInfos.add(playerInfo);
            }
        }
        return playerInfos;
    }

    public ArrayList<PlayerInfo> getLivePlayer(){
        ArrayList<PlayerInfo> playerInfos = new ArrayList<>();
        for(PlayerInfo playerInfo: getTeamPlayers()){
            if(playerInfo.isLive()){
                playerInfos.add(playerInfo);
            }
        }
        return playerInfos;
    }


    public void echoVictory(){
        //TODO 当队伍胜利
        TeamVictoryEvent event = new TeamVictoryEvent(this,room,BedWarMain.getBedWarMain());
        Server.getInstance().getPluginManager().callEvent(event);

    }

    public void echoDefeat(){
        //TODO 当队伍失败
        TeamDefeatEvent event = new TeamDefeatEvent(this,room,BedWarMain.getBedWarMain());
        Server.getInstance().getPluginManager().callEvent(event);

    }

    public void sendActionBar(String msg){
        teamPlayers.forEach(playerInfo ->
                playerInfo.sendActionBar(msg));
    }

    public void sendTip(String msg){
        teamPlayers.forEach(playerInfo ->
                playerInfo.sendTip(msg));
    }

    public void sendTitle(String msg,int time){
        teamPlayers.forEach(playerInfo ->
                playerInfo.sendTitle(msg,time));
    }

    public void sendTitle(String msg){
        teamPlayers.forEach(playerInfo ->
                playerInfo.sendTitle(msg));
    }

    public void sendSubTitle(String msg){
        teamPlayers.forEach(playerInfo ->
                playerInfo.sendSubTitle(msg));
    }

    public void addSound(Sound sound){
        teamPlayers.forEach(playerInfo ->
                playerInfo.addSound(sound));
    }

    public void onUpdate(){
        if(close){
            return;
        }
        if(stop){
            setBadExists(false);
            breakBed();
            for(PlayerInfo info: getLivePlayer()){
                if(info.getGameRoom().getType() != GameRoom.GameType.END){
                    info.death(null);
                }

            }
            close = true;
            return;
        }
        int d = 0;
        for(PlayerInfo info: getTeamPlayers()){
            info.putEffect(teamEffects);
            if(info.getPlayerType() == PlayerInfo.PlayerType.WATCH || info.getPlayerType() == PlayerInfo.PlayerType.LEAVE){
                d++;
            }
        }
        if(teamEffects.contains(new TeamEffectInfo(new TeamTrap(1)))){
            for(PlayerInfo playerInfo: room.getPlayerInfos()){
                if( !playerInfo.isLive() || playerInfo.getPlayerType() == PlayerInfo.PlayerType.WATCH || playerInfo.getTeamInfo().equals(this) ){
                    continue;
                }
                if(playerInfo.getPlayer().distance(getTeamConfig().getBedPosition()) <= 5){
                    teamEffects.remove(new TeamEffectInfo(new TeamTrap(1)));
                    //TODO 陷阱触发
                    sendTitle(playerInfo+" &c触发了陷阱");
                    playerInfo.getPlayer().addEffect(Effect.getEffect(15).setAmplifier(1).setDuration(60));
                    playerInfo.getPlayer().addEffect(Effect.getEffect(4).setAmplifier(1).setDuration(60));
                }
            }
        }


        if(d == getTeamPlayers().size()){
            //被淘汰了
            room.sendMessage("&r团灭 > "+toString()+"&c已被淘汰!");
            echoDefeat();
            stop = true;
        }

    }
    public void placeBed(){
        //尝试修复床透明的问题
        if(!getTeamConfig().getBedPosition().getChunk().isLoaded()){
//            try {
//                getTeamConfig().getBedPosition().getChunk().load();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            getTeamConfig().getBedPosition().getLevel().loadChunk(getTeamConfig().getBedPosition().getChunkX(),getTeamConfig().getBedPosition().getChunkZ());
        }
        if(!getTeamConfig().getSpawnPosition().getChunk().isLoaded()){
            getTeamConfig().getSpawnPosition().getLevel().loadChunk(getTeamConfig().getSpawnPosition().getChunkX(),getTeamConfig().getSpawnPosition().getChunkZ());
        }

        BlockBed blockBed = new BlockBed();
        getTeamConfig().getBedPosition().getLevel().setBlock(getTeamConfig().getBedPosition(),Block.get(26,0),true,true);
        Position pos2 = getTeamConfig().getBedPosition().getSide(getTeamConfig().getBedFace());
        getTeamConfig().getBedPosition().getLevel().setBlock(pos2,Block.get(26,getTeamConfig().getBedFace().getHorizontalIndex()|8),true,true);



    }

    public void onBedBreak(PlayerInfo info){
        room.sendTitle(this+" &c床被破坏！");
        room.sendSubTitle("&7摧毁者: &r"+info);
        room.addSound(Sound.MOB_ENDERDRAGON_GROWL);

        setBadExists(false);
    }

    public void breakBed(){
        badExists = false;
        getTeamConfig().getBedPosition().getLevel().setBlock( getTeamConfig().getBedPosition(),new BlockAir());
        Position pos2 = getTeamConfig().getBedPosition().getSide(getTeamConfig().getBedFace());
        room.addSound(Sound.MOB_ENDERDRAGON_GROWL);
        getTeamConfig().getBedPosition().getLevel().setBlock(pos2,new BlockAir(),true,true);
    }

    public boolean join(PlayerInfo info){
        PlayerChoseTeamEvent event = new PlayerChoseTeamEvent(info,this,room, BedWarMain.getBedWarMain());
        Server.getInstance().getPluginManager().callEvent(event);
        if(teamPlayers.contains(info)){
            return false;
        }
        if(info.getTeamInfo() != null){
            info.getTeamInfo().quit(info);
        }
        info.setTeamInfo(this);
        teamPlayers.add(info);
        return true;
    }

    public void mjoin(PlayerInfo info){
        TeamInfo teamInfo = info.getTeamInfo();
        if(teamInfo != null){
            teamInfo.getTeamPlayers().remove(info);
        }
        info.setTeamInfo(this);
        teamPlayers.add(info);
    }

    public void quit(PlayerInfo info){
        info.setTeamInfo(null);
        teamPlayers.remove(info);
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof TeamInfo){
            return ((TeamInfo) obj).teamConfig.equals(teamConfig);
        }
        return false;
    }

    public void close(){
        close = true;
    }

    @Override
    public String toString() {
        return TextFormat.colorize('&',getTeamConfig().getNameColor()+getTeamConfig().getName());
    }
}
