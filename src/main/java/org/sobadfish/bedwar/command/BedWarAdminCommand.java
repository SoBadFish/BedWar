package org.sobadfish.bedwar.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.item.Item;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.item.ItemInfo;
import org.sobadfish.bedwar.manager.RoomManager;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.GameRoomCreater;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.room.floattext.FloatTextInfoConfig;

import java.util.LinkedHashMap;

/**
 * @author SoBadFish
 * 2022/1/3
 * 超简单的实验指令
 */
public class BedWarAdminCommand extends Command {

    public BedWarAdminCommand(String name) {
        super(name);
    }

    private final LinkedHashMap<String, GameRoomCreater> create = new LinkedHashMap<>();

    private boolean createSetRoom(CommandSender commandSender,String value){
        GameRoomCreater creater;
        if (create.containsKey(commandSender.getName())) {
            creater = create.get(commandSender.getName());
        } else {
            creater = new GameRoomCreater(new PlayerInfo((Player) commandSender));
            create.put(commandSender.getName(), creater);
        }
        creater.onCreatePreset(value);
        return true;
    }

    private boolean createRoom(CommandSender commandSender){
        GameRoomCreater creater;
        if(create.containsKey(commandSender.getName())){
            creater = create.get(commandSender.getName());
        }else{
            creater = new GameRoomCreater(new PlayerInfo((Player) commandSender));
            create.put(commandSender.getName(),creater);
        }
        if(!creater.onCreateNext()){
            if(!creater.createRoom()){
                commandSender.sendMessage("房间创建失败");
            }
        }
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!commandSender.isOp()){
            return true;
        }
        if (strings.length > 0 && "help".equalsIgnoreCase(strings[0])) {
            commandSender.sendMessage("只需要输入/bd 就可以了");
            commandSender.sendMessage("其他指令介绍:");
            commandSender.sendMessage("/bd reload 重新载入配置");
            commandSender.sendMessage("/bd set [名称] 创建一个自定义房间模板");
            commandSender.sendMessage("/bd tsl 读取模板的队伍数据与商店物品数据");
            commandSender.sendMessage("/bd see 查看所有加载的房间");
            commandSender.sendMessage("/bd close [名称] 关闭房间");
            commandSender.sendMessage("/bd status 查看线程状态");
            commandSender.sendMessage("/bd end 停止模板预设");
            commandSender.sendMessage("/bd float add/remove [房间名称] [名称] [文本] 在脚下设置浮空字/删除浮空字");
            commandSender.sendMessage("/bd cancel 终止房间创建");

            return true;
        }
        if (strings.length == 0) {
            if(!commandSender.isPlayer()){
                commandSender.sendMessage("请不要在控制台执行");
                return false;
            }
            return createRoom(commandSender);
        }
        switch (strings[0]){
            case "set":
                if(strings.length > 1) {
                    if (commandSender instanceof Player) {
                        return createSetRoom(commandSender, strings[1]);
                    } else {
                        commandSender.sendMessage("请不要在控制台执行");
                        return false;
                    }
                }else{
                    commandSender.sendMessage(TextFormat.colorize('&',"/bd set [内容] &e首次创建为房间名称"));
                    return false;
                }
            case "end":
                if(commandSender instanceof Player) {
                    if (create.containsKey(commandSender.getName())) {
                        create.get(commandSender.getName()).stopInit();
                    }
                }else{
                    commandSender.sendMessage("请不要在控制台执行");
                    return false;
                }
                break;
            case "float":
                if(strings.length < 5){
                    commandSender.sendMessage("指令参数错误 执行/bw help 查看帮助");
                    return false;
                }
                if(commandSender instanceof Player) {
                   GameRoomConfig roomConfig = BedWarMain.getRoomManager().getRoomConfig(strings[2]);
                   if(roomConfig == null){
                       commandSender.sendMessage("房间 "+strings[2]+" 不存在");
                       return false;
                   }
                   if(strings[1].equalsIgnoreCase("remove")){
                       if(!roomConfig.hasFloatText(strings[3])){
                           commandSender.sendMessage("浮空字 "+strings[3]+" 不存在");
                           return false;
                       }
                       roomConfig.removeFloatText(strings[3]);
                       commandSender.sendMessage("浮空字删除成功");

                   }else{
                       if(!roomConfig.hasFloatText(strings[3])){
                           roomConfig.floatTextInfoConfigs.add(new FloatTextInfoConfig(strings[3],((Player) commandSender).getPosition(),strings[4]));
                           commandSender.sendMessage("成功添加浮空字");
                       }else{
                           commandSender.sendMessage("房间存在 "+strings[3]+"的浮空字");
                       }
                   }

                }else{
                    commandSender.sendMessage("请不要在控制台执行");
                    return false;
                }

                break;
            case "status":
                BedWarMain.sendMessageToObject("&6活跃线程: &a"+ ThreadManager.getActiveCount(),commandSender);
                BedWarMain.sendMessageToObject("&6线程数量: &a"+ ThreadManager.getSize(),commandSender);
                BedWarMain.sendMessageToObject("&6线程详情: &r\n"+ThreadManager.info(),commandSender);
                BedWarMain.sendMessageToObject("&6房间状态: &a",commandSender);
                for(GameRoomConfig config: BedWarMain.getRoomManager().getRoomConfigs()){
                    GameRoom room = BedWarMain.getRoomManager().getRoom(config.name);
                    if(room != null){

                        BedWarMain.sendMessageToObject("&a"+config.getName()+" (已启动) "+room.getType()+" : &2"+room.getPlayerInfos().size(),commandSender);
                    }else{
                        BedWarMain.sendMessageToObject("&c"+config.getName()+" (未启动)",commandSender);
                    }
                }
                break;
            case "tsl":
                teamShopLoad(commandSender);
                break;
            case "see":
                BedWarMain.sendMessageToObject(BedWarMain.getRoomManager().getRooms().keySet().toString(),commandSender);
                break;
            case "reload":
                BedWarMain.sendMessageToObject("正在读取配置文件中",commandSender);
                BedWarMain.getBedWarMain().loadBedWarConfig();
                BedWarMain.sendMessageToObject("配置文件读取完成",commandSender);
                break;
            case "close":
                if(strings.length > 1) {
                    String name = strings[1];
                    if(BedWarMain.getRoomManager().hasGameRoom(name)){
                        BedWarMain.getRoomManager().disEnableRoom(name);
                        commandSender.sendMessage("成功关闭房间: "+name);
                    }else{
                        commandSender.sendMessage("游戏房间未开启");
                    }
                }else{
                    commandSender.sendMessage("请输入房间名");
                }
                break;
            case "cancel":
                create.remove(commandSender.getName());
                BedWarMain.sendMessageToObject("成功终止房间的创建，残留文件将在重启服务器后自动删除", commandSender);
                // commandSender.sendMessage(TextFormat.colorize('&', "&d"));

                break;

            default:break;

        }
        return true;
    }

    private void teamShopLoad(CommandSender commandSender){
        if(!create.containsKey(commandSender.getName())){
            commandSender.sendMessage("请先创建房间模板");
            return;
        }
        GameRoomCreater creater = create.get(commandSender.getName());
        GameRoomConfig roomConfig = creater.getRoomConfig();
        if(roomConfig != null) {
            GameRoomConfig.loadTeamShopConfig(roomConfig);
            commandSender.sendMessage("成功重新读取模板信息");
        }else{
            commandSender.sendMessage("无模板信息");
        }
    }
}
