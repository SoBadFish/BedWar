package org.sobadfish.bedwar.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.LanguageManager;
import org.sobadfish.bedwar.manager.RandomJoinManager;
import org.sobadfish.bedwar.panel.DisPlayWindowsFrom;
import org.sobadfish.bedwar.panel.from.BedWarFrom;
import org.sobadfish.bedwar.panel.from.button.BaseIButton;
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

    public LanguageManager language = BedWarMain.getLanguage();

    public BedWarCommand(String name) {
        super(name,BedWarMain.getLanguage().getLanguage("command-room","游戏房间"));
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
                BedWarFrom simple = new BedWarFrom(BedWarMain.getTitle(), language.getLanguage("command-from-chose-world","请选择地图"), DisPlayWindowsFrom.getId(51530, 99810));
                PlayerInfo finalInfo = info;
                simple.add(new BaseIButton(new ElementButton(language.getLanguage("command-from-random","随机匹配"),new ElementButtonImageData("path","textures/ui/dressing_room_skins"))) {
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

                    simple.add(new BaseIButton(new ElementButton(TextFormat.colorize('&', language.getLanguage("command-button-msg","[1] &2[2] &r位玩家正在游玩\n&r房间数量: &a[3]",wname,size+"",worldRoom.getRoomConfigs().size()+"")), worldRoom.getImageData())) {
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
                                playerInfo.sendForceMessage(language.getLanguage("command-player-quit-room","&a你成功离开房间: &r[1]",room.getRoomConfig().getName()));

                                room.getRoomConfig().quitRoomCommand.forEach(cmd-> Server.getInstance().dispatchCommand(commandSender,cmd));
                            }
                        }
                        break;
                    case "join":
                        if (strings.length > 1) {
                            String name = strings[1];
                            if (BedWarMain.getRoomManager().joinRoom(playerInfo, name)) {
                                playerInfo.sendForceMessage(language.getLanguage("command-player-join-room","&a成功加入房间: &r[1]",name));
                            }
                        } else {
                            playerInfo.sendForceMessage(language.getLanguage("command-player-join-room-unknown","&c请输入房间名"));
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
                            commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
                        }

                        break;
                        default:break;
                }
            }
        }else{
            commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
            return false;
        }
        return true;
    }
    private void disPlayRoomsFrom(Player player,String name){
        FROM.remove(player.getName());
        BedWarFrom simple = new BedWarFrom(BedWarMain.getTitle(), language.getLanguage("command-from-chose-room","请选择房间"),DisPlayWindowsFrom.getId(51530,99810));
        WorldRoom worldRoom = BedWarMain.getMenuRoomManager().getRoom(name);
        PlayerInfo info = new PlayerInfo(player);
        simple.add(new BaseIButton(new ElementButton(language.getLanguage("command-from-random","随机匹配"),new ElementButtonImageData("path","textures/ui/dressing_room_skins"))) {
            @Override
            public void onClick(Player player) {
                RandomJoinManager.joinManager.join(info,null);

            }
        });
        for (GameRoomConfig roomConfig: worldRoom.getRoomConfigs()) {
            int size = 0;
            String type = language.getLanguage("room-status-unstarted","&a空闲");
            GameRoom room = BedWarMain.getRoomManager().getRoom(roomConfig.name);
            if(room != null){
                size = room.getPlayerInfos().size();
                switch (room.getType()){
                    case START:
                        type = language.getLanguage("room-status-started","&c已开始");
                        break;
                    case END:
                        type = language.getLanguage("room-status-waitend","&c等待房间结束");
                        break;
                    default:break;
                }
            }

            simple.add(new BaseIButton(new ElementButton(TextFormat.colorize('&',
                    language.getLanguage("command-from-button-title","[1] &r状态:[2] &r[n]人数: [3] / [4]",
                            roomConfig.name,type,size+"",roomConfig.getMaxPlayerSize()+"")), worldRoom.getImageData())) {
                @Override
                public void onClick(Player player) {
                    PlayerInfo playerInfo = new PlayerInfo(player);
                    if (!BedWarMain.getRoomManager().joinRoom(info,roomConfig.name)) {
                        playerInfo.sendForceMessage(language.getLanguage("command-from-join-room-error","&c无法加入房间"));
                    }else{
                        playerInfo.sendForceMessage(language.getLanguage("command-from-join-room-success","&a你已加入 [1] 房间",roomConfig.getName()));
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
