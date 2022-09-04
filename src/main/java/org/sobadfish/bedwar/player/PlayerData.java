package org.sobadfish.bedwar.player;

import cn.nukkit.Server;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.event.PlayerGetExpEvent;
import org.sobadfish.bedwar.event.PlayerLevelChangeEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerData {

    public String name = "";

    public int exp;

    public int level;


    public List<RoomData> roomData = new ArrayList<>();

    public int getFinalData(DataType dataType){
        int c = 0;
        for(RoomData data: roomData){
            c += data.getInt(dataType);
        }
        return c;
    }

    public int getExp() {
        return exp;
    }

    public void addExp(int exp,String cause){
        addExp(exp,cause,true);
    }


    public void addExp(int exp,String cause,boolean event){

        this.exp += exp;
        if(this.exp >= getNextLevelExp()){
            this.exp -= getNextLevelExp();
            PlayerLevelChangeEvent event1 = new PlayerLevelChangeEvent(name,level,1);
            Server.getInstance().getPluginManager().callEvent(event1);
            if(event1.isCancelled()){
                return;
            }
            level += event1.getNewLevel();
            int nExp = this.exp - getNextLevelExp();
            if(nExp > 0){
                addExp(nExp,null,false);
            }
        }
        if(event) {
            PlayerGetExpEvent expEvent = new PlayerGetExpEvent(name, exp,cause);
            Server.getInstance().getPluginManager().callEvent(expEvent);
        }
    }

    public double getExpPercent(){
        double r = 0;
        if(this.exp > 0){
            r = (double) this.exp / (double) getNextLevelExp();
        }
        return r;
    }

    public String getExpLine(int size){
        double r = getExpPercent();
        int l = (int) (size * r);
        int other = size - l;
        StringBuilder ls = new StringBuilder();
        if(l > 0){
            for(int i = 0;i < l;i++){
                ls.append("&b■");
            }
        }
        StringBuilder others = new StringBuilder();
        if(other > 0){
            for(int i = 0;i < other;i++){
                others.append("&7■");
            }
       }
        return ls.toString()+others.toString();
    }

    public String getColorByLevel(int level){
        String[] color = new String[]{"&7","&f","&6","&b","&2","&3","&4","&d","&6","&e"};
        if(level < 100){
            return color[0];
        }else{
            return color[(level / 100) % 10];
        }

    }

    public String getLevelString(){
        String str = "✫";
        if(level > 1000){
            str = "✪";
        }
        return getColorByLevel(level)+level+str;
    }

    public String getExpString(int exp){
        double e = exp;
        e /= 1000;
        if(e < 10){
            return String.format("%.1f",e)+"k";
        }else{
            e /= 10;
            if(e < 1000){
                return String.format("%.1f",e)+"w";
            }else{
                return String.format("%.1f",e)+"bill";
            }
        }
    }

    public int getNextLevelExp(){
        double l = level;
         l+= 1;
        if(l > 100){
            l = l / 100.0;
            l = l - (int) l;
            l *= 100;
            if(l <= 0){
                l = 1;
            }
        }
       return (int)l * BedWarMain.getUpExp();
    }



    public static class RoomData{

        public String roomName = "";

        public int killCount = 0;

        public int deathCount = 0;

        public int breakCount = 0;

        public int endCont = 0;

        public int gameCount = 0;

        public int defeatCount = 0;

        public int victoryCount = 0;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            RoomData roomData = (RoomData) o;
            return Objects.equals(roomName, roomData.roomName);
        }

        @Override
        public int hashCode() {
            return Objects.hash(roomName);
        }

        public int getInt(DataType type){
            int c = 0;
            switch (type){
                case BED_BREAK:
                    c += breakCount;
                    break;
                case END_KILL:
                    c += endCont;
                    break;
                case VICTORY:
                    c += victoryCount;
                    break;
                case DEFEAT:
                    c += defeatCount;
                    break;
                case DEATH:
                    c += deathCount;
                    break;
                case KILL:
                    c += killCount;
                    break;
                case GAME:
                    c += gameCount;
                    break;

            }

            return c;


        }
    }

    public RoomData getRoomData(String room){
        RoomData roomData = new RoomData();
        roomData.roomName = room;
        if(!this.roomData.contains(roomData)){
            this.roomData.add(roomData);
        }else{
            roomData = this.roomData.get(this.roomData.indexOf(roomData));
        }
        return roomData;
    }

    public void setInfo(PlayerInfo info){
        RoomData data = getRoomData(info.getGameRoom().getRoomConfig().name);
        data.breakCount += info.bedBreakCount;
        data.deathCount += info.deathCount;
        data.killCount += info.killCount;
        data.endCont += info.endKillCount;
    }

    @Override
    public boolean equals(Object o) {
       if(o instanceof PlayerData){
           return ((PlayerData) o).name.equalsIgnoreCase(name);
       }
       return false;
    }

    public enum DataType{
        /**
         * 击杀
         * */
        KILL("击杀"),
        /**
         * 死亡
         * */
        DEATH("死亡"),
        /**
         * 胜利
         * */
        VICTORY("胜利"),
        /**
         * 失败
         * */
        DEFEAT("失败"),
        /**
         * 破坏床
         * */
        BED_BREAK("破坏床"),
        /**
         * 最终击杀
         * */
        END_KILL("最终击杀"),

        /**
         * 游戏次数
         * */
        GAME("游戏次数");

        protected String name;

        DataType(String name){
            this.name = name;
        }


        public String getName() {
            return name;
        }

        public static DataType byName(String name){
            for(DataType type: values()){
                if(type.getName().equalsIgnoreCase(name)){
                    return type;
                }
            }
            return null;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
