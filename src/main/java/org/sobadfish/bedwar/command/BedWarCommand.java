package org.sobadfish.bedwar.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.RandomJoinManager;
import org.sobadfish.bedwar.panel.DisPlayWindowsFrom;
import org.sobadfish.bedwar.panel.from.BedWarFrom;
import org.sobadfish.bedwar.panel.from.button.BaseIButtom;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.WorldRoom;
import org.sobadfish.bedwar.room.config.GameRoomConfig;

import java.util.LinkedHashMap;

/**
 * @author SoBadFish
 * 2022/1/12
 */
public class BedWarCommand extends Command {

    public BedWarCommand(String name) {
        super(name,"BedWar GameRoom");
    }

    public static LinkedHashMap<String, BedWarFrom> FROM = new LinkedHashMap<>();

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player) {
            if(strings.length == 0) {
                PlayerInfo info = new PlayerInfo((Player)commandSender);
                PlayerInfo i = BedWarMain.getRoomManager().getPlayerInfo((Player) commandSender);
                if(i != null){
                    info = i;
                }
                BedWarFrom simple = new BedWarFrom(BedWarMain.getTitle(), "Place chose world", DisPlayWindowsFrom.getId(51530, 99810));
                PlayerInfo finalInfo = info;
                simple.add(new BaseIButtom(new ElementButton("Auto matching",new ElementButtonImageData("path","textures/ui/dressing_room_skins"))) {
                    @Override
                    public void onClick(Player player) {
                        RandomJoinManager.joinManager.join(finalInfo,null);
                    }
                });
                for (String wname : BedWarMain.getMenuRoomManager().getNames()) {
                    WorldRoom worldRoom = BedWarMain.getMenuRoomManager().getRoom(wname);
                    int size = 0;
                    for (GameRoomConfig roomConfig : worldRoom.getRoomConfigs()) {
                        GameRoom room = BedWarMain.getRoomManager().getRoom(roomConfig.name);
                        if (room != null) {
                            size += room.getPlayerInfos().size();
                        }
                    }
                    simple.add(new BaseIButtom(new ElementButton(TextFormat.colorize('&', wname + " &2" + size + " &r位玩家正在游玩\n&r房间数量: &a" + worldRoom.getRoomConfigs().size()), worldRoom.getImageData())) {
                        @Override
                        public void onClick(Player player) {
                            disPlayRoomsFrom(player, wname);
                        }
                    });
                }
                simple.disPlay((Player) commandSender);
                FROM.put(commandSender.getName(), simple);
            }else{
                PlayerInfo playerInfo = new PlayerInfo((Player) commandSender);
                PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo((Player) commandSender);
                if(info != null){
                    playerInfo = info;
                }
                switch (strings[0]){
                    case "quit":
                        PlayerInfo player = BedWarMain.getRoomManager().getPlayerInfo((Player) commandSender);
                        if (player != null) {
                            GameRoom room = player.getGameRoom();
                            if (room.quitPlayerInfo(player,true)) {
                                playerInfo.sendForceMessage("&aYou leave the room success: &r" + room.getRoomConfig().getName());

                                room.getRoomConfig().quitRoomCommand.forEach(cmd-> Server.getInstance().dispatchCommand(commandSender,cmd));
                            }
                        }
                        break;
                    case "join":
                        if (strings.length > 1) {
                            String name = strings[1];
                            if (BedWarMain.getRoomManager().joinRoom(playerInfo, name)) {
                                playerInfo.sendForceMessage("&aSuccess to join the room: &r"+name);
                            }
                        } else {
                            playerInfo.sendForceMessage("&cPlease enter the room");
                        }
                        break;
                    case "rjoin":
                    String name = null;
                        if(commandSender.isPlayer()){
                            if(strings.length > 1){
                                name = strings[1];
                            }
                            if(name != null){
                                if("".equals(name.trim())){
                                    name = null;
                                }
                            }

                            info = new PlayerInfo((Player)commandSender);
                            PlayerInfo i = BedWarMain.getRoomManager().getPlayerInfo((Player) commandSender);
                            if(i != null){
                                info = i;
                            }
                            String finalName = name;
                            RandomJoinManager.joinManager.join(info,finalName);

                        }else{
                            commandSender.sendMessage("Please don't in the console");
                        }

                        break;
                        default:break;
                }
            }
        }else{
            commandSender.sendMessage("Please don't in the console");
            return false;
        }
        return true;
    }
    private void disPlayRoomsFrom(Player player,String name){
        FROM.remove(player.getName());
        BedWarFrom simple = new BedWarFrom(BedWarMain.getTitle(), "Place chose room",DisPlayWindowsFrom.getId(51530,99810));
        WorldRoom worldRoom = BedWarMain.getMenuRoomManager().getRoom(name);
        PlayerInfo info = new PlayerInfo(player);
        simple.add(new BaseIButtom(new ElementButton("Auto matching",new ElementButtonImageData("path","textures/ui/dressing_room_skins"))) {
            @Override
            public void onClick(Player player) {
                RandomJoinManager.joinManager.join(info,null);

            }
        });
        for (GameRoomConfig roomConfig: worldRoom.getRoomConfigs()) {
            int size = 0;
            String type = "&a wait";
            GameRoom room = BedWarMain.getRoomManager().getRoom(roomConfig.name);
            if(room != null){
                size = room.getPlayerInfos().size();
                switch (room.getType()){
                    case START:
                        type = "&c started";
                        break;
                    case END:
                        type = "&c wait end";
                        break;
                        default:break;
                }
            }

            simple.add(new BaseIButtom(new ElementButton(TextFormat.colorize('&',roomConfig.name+" &r状态:"+type + "&r\n人数: "+size+" / " + roomConfig.getMaxPlayerSize()), worldRoom.getImageData())) {
                @Override
                public void onClick(Player player) {
                    PlayerInfo playerInfo = new PlayerInfo(player);
                    if (!BedWarMain.getRoomManager().joinRoom(info,roomConfig.name)) {
                        playerInfo.sendForceMessage("&cUnable to join the room");
                    }else{
                        playerInfo.sendForceMessage("&aYou joined "+roomConfig.getName()+" room");
                    }
//                    if (BedWarMain.getRoomManager().hasRoom(roomConfig.name)) {
                    FROM.remove(player.getName());

                }
            });
        }
        simple.disPlay(player);
        FROM.put(player.getName(),simple);
    }



}
