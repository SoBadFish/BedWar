package org.sobadfish.bedwar.player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlayerData {

    public String name;

    public List<RoomData> roomData = new ArrayList<>();

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
    }

    public RoomData getRoomData(String room){
        RoomData roomData = new RoomData();
        roomData.roomName = room;
        if(!this.roomData.contains(roomData)){
            this.roomData.add(roomData);
        }
        return this.roomData.get(this.roomData.indexOf(roomData));
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
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PlayerData data = (PlayerData) o;
        return Objects.equals(name, data.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}
