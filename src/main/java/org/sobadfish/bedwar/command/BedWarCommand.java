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
import org.sobadfish.bedwar.manager.RoomManager;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.panel.DisPlayWindowsFrom;
import org.sobadfish.bedwar.panel.from.BedWarFrom;
import org.sobadfish.bedwar.panel.from.button.BaseIButtom;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.WorldRoom;
import org.sobadfish.bedwar.room.config.GameRoomConfig;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author SoBadFish&GouDan888
 * 2022/1/12
 */
public class BedWarCommand extends Command {

    public BedWarCommand(String name) {
        super(name,"起床战争游戏房间");
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
                BedWarFrom simple = new BedWarFrom("§c起床§e战争§r - 国际版",
                        "《起床战争》是一款脍炙人口小游戏玩法\n" +
                               "你需要在资源点收集资源购买道具\n" +
                               "并在保护本队床的同时让自身队伍变得更强。\n" +
                               "§e我们提供多种《起床战争》的游戏模式》\n" +
                               "§e当然,如果你不清楚游戏规则,可以试一试经典的4v4v4v4模式。", DisPlayWindowsFrom.getId(51530, 99810));
                PlayerInfo finalInfo = info;
                //simple.add(new BaseIButtom(new ElementButton("随机匹配",new ElementButtonImageData("path","textures/ui/dressing_room_skins"))) {
                simple.add(new BaseIButtom(new ElementButton("§l§5随机匹配")) {
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
                    //simple.add(new BaseIButtom(new ElementButton(TextFormat.colorize('&', wname + " &2" + size + " &r位玩家正在游玩\n&r房间数量: &a" + worldRoom.getRoomConfigs().size()), worldRoom.getImageData())) {
                    simple.add(new BaseIButtom(new ElementButton(TextFormat.colorize('&', "&c起床&e战争 &r- &l&5国际版 "+wname+" \n&r\uE105 "+size))) {
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
                                playerInfo.sendForceMessage("&a你成功离开房间: &r" + room.getRoomConfig().getName());

                                room.getRoomConfig().quitRoomCommand.forEach(cmd-> Server.getInstance().dispatchCommand(commandSender,cmd));
                            }
                        }
                        break;
                    case "join":
                        if (strings.length > 1) {
                            String name = strings[1];
                            if (BedWarMain.getRoomManager().joinRoom(playerInfo, name)) {
                                //playerInfo.sendForceMessage("&a成功加入房间: &r"+name);
                            }
                        } else {
                            playerInfo.sendForceMessage("&c请输入房间名");
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
                            commandSender.sendMessage("请在控制台执行");
                        }

                        break;
                        default:break;
                }
            }
        }else{
            commandSender.sendMessage("请不要在控制台执行");
            return false;
        }
        return true;
    }
    private void disPlayRoomsFrom(Player player,String name){
        FROM.remove(player.getName());
        //BedWarFrom simple = new BedWarFrom(BedWarMain.getTitle(), "请选择房间",DisPlayWindowsFrom.getId(51530,99810));
        BedWarFrom simple = new BedWarFrom("§c起床§e战争§r 的房间列表", "请选择房间点击进入：\n§e注意：由于房间人数及状态变动极快,部分房间可能已开始游戏,你将以观战者模式进入",DisPlayWindowsFrom.getId(51530,99810));
        WorldRoom worldRoom = BedWarMain.getMenuRoomManager().getRoom(name);
        PlayerInfo info = new PlayerInfo(player);
        //simple.add(new BaseIButtom(new ElementButton("随机匹配",new ElementButtonImageData("path","textures/ui/dressing_room_skins"))) {
        simple.add(new BaseIButtom(new ElementButton("§5随机匹配")) {
            @Override
            public void onClick(Player player) {
                RandomJoinManager.joinManager.join(info,null);

            }
        });
        for (GameRoomConfig roomConfig: worldRoom.getRoomConfigs()) {
            int size = 0;
            String type = "&l&5等待中 &r&e可加入";
            GameRoom room = BedWarMain.getRoomManager().getRoom(roomConfig.name);
            if(room != null){
                size = room.getPlayerInfos().size();
                switch (room.getType()){
                    case START:
                        type = "&l&2游戏中 &r&b可观战";
                        break;
                    case END:
                        type = "&c等待房间结束";
                        break;
                        default:break;
                }
            }

            //simple.add(new BaseIButtom(new ElementButton(TextFormat.colorize('&',roomConfig.name+" &r状态:"+type + "&r\n人数: "+size+" / " + roomConfig.getMaxPlayerSize()), worldRoom.getImageData())) {
            simple.add(new BaseIButtom(new ElementButton(TextFormat.colorize('&',type + "\n&r&4玩家数: "+size+"/" + roomConfig.getMaxPlayerSize()+"  &r&1地图： "+ roomConfig.name))) {
                @Override
                public void onClick(Player player) {
                    PlayerInfo playerInfo = new PlayerInfo(player);
                    if (!BedWarMain.getRoomManager().joinRoom(info,roomConfig.name)) {
                        playerInfo.sendForceMessage("&c无法加入房间");
                    }else{
                        playerInfo.sendForceMessage("&a你已加入 "+roomConfig.getName()+" 房间");
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
