package org.sobadfish.bedwar.manager;

import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.config.GameRoomConfig;

import java.util.ArrayList;

/**
 * @author SoBadFish
 * 2022/1/15
 */
public class PlayerHasChoseRoomManager {

    private PlayerInfo playerInfo;

    public boolean cancel = false;

    private ArrayList<String> strings = new ArrayList<>();

    private ArrayList<GameRoomConfig> roomName = new ArrayList<>();

    public PlayerHasChoseRoomManager(PlayerInfo info){
        this.playerInfo = info;
    }

    public boolean hasRoomName(GameRoomConfig room){
        return roomName.contains(room);
    }

    public boolean hasRoom(String room){
        return strings.contains(room);
    }

    public void add(String room){
        strings.add(room);
    }

    public void addRoom(GameRoomConfig room){
        roomName.add(room);
    }

    public ArrayList<GameRoomConfig> getRoomName() {
        return roomName;
    }

    public ArrayList<String> getStrings() {
        return strings;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof PlayerHasChoseRoomManager){
            return ((PlayerHasChoseRoomManager) obj).playerInfo.equals(playerInfo);
        }
        return false;
    }
}
