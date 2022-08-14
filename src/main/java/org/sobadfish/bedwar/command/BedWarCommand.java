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
 * @author SoBadFish
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
                BedWarFrom simple = new BedWarFrom(BedWarMain.getTitle(), "请选择地图", DisPlayWindowsFrom.getId(51530, 99810));
                simple.add(new BaseIButtom(new ElementButton("随机匹配",new ElementButtonImageData("path","textures/ui/dressing_room_skins"))) {
                    @Override
                    public void onClick(Player player) {
                        ThreadManager.addThread(new MatchingRunnable(info,null));
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
                                playerInfo.sendForceMessage("&a成功加入房间: &r"+name);
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

                            PlayerInfo info = new PlayerInfo((Player)commandSender);
                            String finalName = name;
                            ThreadManager.addThread(new MatchingRunnable(info,finalName));
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
        BedWarFrom simple = new BedWarFrom(BedWarMain.getTitle(), "请选择房间",DisPlayWindowsFrom.getId(51530,99810));
        WorldRoom worldRoom = BedWarMain.getMenuRoomManager().getRoom(name);
        PlayerInfo info = new PlayerInfo(player);
        simple.add(new BaseIButtom(new ElementButton("随机匹配",new ElementButtonImageData("path","textures/ui/dressing_room_skins"))) {
            @Override
            public void onClick(Player player) {
                ThreadManager.addThread(new MatchingRunnable(info,name));

            }
        });
        for (GameRoomConfig roomConfig: worldRoom.getRoomConfigs()) {
            int size = 0;
            String type = "&a空闲";
            GameRoom room = BedWarMain.getRoomManager().getRoom(roomConfig.name);
            if(room != null){
                size = room.getPlayerInfos().size();
                switch (room.getType()){
                    case START:
                        type = "&c已开始";
                        break;
                    case END:
                        type = "&c等待房间结束";
                        break;
                        default:break;
                }
            }

            simple.add(new BaseIButtom(new ElementButton(TextFormat.colorize('&',roomConfig.name+" &r状态:"+type + "&r\n人数: "+size+" / " + roomConfig.getMaxPlayerSize()), worldRoom.getImageData())) {
                @Override
                public void onClick(Player player) {
                    PlayerInfo playerInfo = new PlayerInfo(player);
                    if (BedWarMain.getRoomManager().hasRoom(roomConfig.name)) {
                        if (!BedWarMain.getRoomManager().hasGameRoom(roomConfig.name)) {
                            BedWarMain.getRoomManager().enableRoom(BedWarMain.getRoomManager().getRoomConfig(roomConfig.name));
                        }
                        if (!BedWarMain.getRoomManager().getRoom(roomConfig.name).joinPlayerInfo(playerInfo,true)) {
                            playerInfo.sendForceMessage("&c无法加入房间");
                        }else{
                            playerInfo.sendForceMessage("&a你已加入 "+roomConfig.getName()+" 房间");
                        }
                    } else {
                        playerInfo.sendForceMessage("不存在" + roomConfig.name + "房间");
                    }
                    FROM.remove(player.getName());

                }
            });
        }
        simple.disPlay(player);
        FROM.put(player.getName(),simple);
    }

    private class MatchingRunnable extends ThreadManager.AbstractBedWarRunnable {

        private PlayerInfo info;
        private String name;

        public MatchingRunnable(PlayerInfo info,String name){
            this.info = info;
            this.name = name;
        }

        @Override
        public GameRoom getRoom() {
            return null;
        }

        @Override
        public String getThreadName() {
            return "匹配线程";
        }

        @Override
        public void run() {
            if(RandomJoinManager.newInstance().join(info, name)){
                info.sendForceTitle("&a进入匹配队列");
            }else{
                info.sendForceTitle("&c无法进入匹配队列..");

            }
            isClose = true;

        }
    }


}
