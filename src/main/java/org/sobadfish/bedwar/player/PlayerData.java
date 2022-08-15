package org.sobadfish.bedwar.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerData {

    public String name = "";

    public List<RoomData> roomData = new ArrayList<>();

    public int getFinalData(DataType dataType){
        int c = 0;
        for(RoomData data: roomData){
            c += data.getInt(dataType);
        }
        return c;
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
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
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
