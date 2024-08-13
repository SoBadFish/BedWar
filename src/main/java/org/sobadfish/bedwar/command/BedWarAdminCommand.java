package org.sobadfish.bedwar.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.level.Position;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.RobotEntity;
import org.sobadfish.bedwar.manager.LanguageManager;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.panel.RecordPanel;
import org.sobadfish.bedwar.player.PlayerData;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.player.team.TeamInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.GameRoomCreater;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.room.floattext.FloatTextInfoConfig;
import org.sobadfish.bedwar.tools.Utils;
import org.sobadfish.bedwar.top.TopItem;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.util.LinkedHashMap;

/**
 * @author SoBadFish
 * 2022/1/3
 * 超简单的实验指令
 */
public class BedWarAdminCommand extends Command {

    private final LanguageManager language = BedWarMain.getLanguage();

    public BedWarAdminCommand(String name) {
        super(name);
        this.usageMessage = language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",BedWarMain.COMMAND_ADMIN_NAME);
        this.setPermission("op");
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
                commandSender.sendMessage(language.getLanguage("create-room-error","房间创建失败"));
            }
        }
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!commandSender.isOp()){
            BedWarMain.sendMessageToObject(language.getLanguage("command-admin-no-permission","&c你没有使用此指令的权限"),commandSender);
            return true;
        }
        String valueData = BedWarMain.COMMAND_ADMIN_NAME;
        if (strings.length > 0 && "help".equalsIgnoreCase(strings[0])) {
            commandSender.sendMessage(language.getLanguage("command-admin-help1","只需要输入/[1] 就可以了",
                    valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-help2","其他指令介绍:"));

            commandSender.sendMessage(language.getLanguage("command-admin-reload","/[1] reload 重新载入配置",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-set","/[1] set [名称] 创建一个自定义房间模板",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-tsl","/[1] tsl 读取模板的队伍数据",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-see","/[1] see 查看所有加载的房间",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-save-item","/[1] si [名称] 将手持的物品保存到配置文件中",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-close","/[1] close [名称] 关闭房间",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-start","/[1] start [名称] 强行启动游戏",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-exp","/[1] exp [玩家] [数量] <由来> 增加玩家经验",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-status","/[1] status 查看线程状态",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-end","/[1] end 停止模板预设",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-robot","/[1] robot [房间名称] [数量] 向游戏房间内增加测试机器人",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-float","/[1] float add/remove [房间名称] [名称] [文本] 在脚下设置浮空字/删除浮空字",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-cancel","/[1] cancel 终止房间创建",valueData));
            commandSender.sendMessage(language.getLanguage("command-admin-top","/[1] top add/remove [名称] [类型] [房间(可不填)] 创建/删除排行榜",valueData));
            StringBuilder v = new StringBuilder(language.getLanguage("top-type","类型: "));
            for(PlayerData.DataType type: PlayerData.DataType.values()){
                v.append(type.getName()).append(" , ");
            }
            commandSender.sendMessage(v.toString());
            return true;

        }
        if (strings.length == 0) {
            if(!commandSender.isPlayer()){
                commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
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
                        commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
                        return false;
                    }
                }else{
                    commandSender.sendMessage(language.getLanguage("set","/[1] set [名称] 创建一个自定义房间模板",valueData));
                    return false;
                }
            case "end":
                if(commandSender instanceof Player) {
                    if (create.containsKey(commandSender.getName())) {
                        create.get(commandSender.getName()).stopInit();
                    }
                }else{
                    commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
                    return false;
                }
                break;

            case "float":
                if(strings.length < 4){
                    commandSender.sendMessage(language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",BedWarMain.COMMAND_ADMIN_NAME));
                    return false;
                }
                if(commandSender instanceof Player) {
                   GameRoomConfig roomConfig = BedWarMain.getRoomManager().getRoomConfig(strings[2]);
                   if(roomConfig == null){
                       commandSender.sendMessage(language.getLanguage("room-no-exists","房间 [1] 不存在",strings[2]));
                       return false;
                   }
                   if("remove".equalsIgnoreCase(strings[1])){
                       if(roomConfig.notHasFloatText(strings[3])){
                           commandSender.sendMessage(language.getLanguage("float-no-exists","浮空字 [1] 不存在",strings[3]));
                           return false;
                       }
                       roomConfig.removeFloatText(strings[3]);
                       commandSender.sendMessage(language.getLanguage("float-delete-success","浮空字删除成功"));
                   }else{
                       if(strings.length < 5){
                           commandSender.sendMessage(language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",BedWarMain.COMMAND_ADMIN_NAME));
                           return false;
                       }
                       if(roomConfig.notHasFloatText(strings[3])){
                           roomConfig.floatTextInfoConfigs.add(new FloatTextInfoConfig(strings[3], WorldInfoConfig.positionToString(((Player) commandSender).getPosition()),strings[4]));
                           commandSender.sendMessage(language.getLanguage("float-crate-success","成功添加浮空字"));
                       }else{
                           commandSender.sendMessage(language.getLanguage("float-room-exists","房间存在 [1] 浮空字",strings[3]));
                       }
                   }

                }else{
                    commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
                    return false;
                }

                break;
            case "status":
                BedWarMain.sendMessageToObject(language.getLanguage("status-timing","&6定时任务: &a")+ ThreadManager.getScheduledSize(),commandSender);
                BedWarMain.sendMessageToObject(language.getLanguage("status-running","&6正在执行的定时任务: &a")+ ThreadManager.getScheduledActiveCount(),commandSender);
                BedWarMain.sendMessageToObject(language.getLanguage("status-thread","&6线程详情: &r")+"\n"+ThreadManager.info(),commandSender);
                BedWarMain.sendMessageToObject(language.getLanguage("status-room","&6房间状态: &a"),commandSender);
                for(GameRoomConfig config: BedWarMain.getRoomManager().getRoomConfigs()){
                    GameRoom room = BedWarMain.getRoomManager().getRoom(config.name);
                    if(room != null){
                        BedWarMain.sendMessageToObject("&a"+config.getName()+language.getLanguage("status-started"," (已启动) ")+room.getType()+" : &2"+room.getPlayerInfos().size(),commandSender);
                        StringBuilder team = new StringBuilder();

                        for(TeamInfo teamInfo: room.getTeamInfos()){
                            StringBuilder playerI = new StringBuilder();
                            for(PlayerInfo playerInfo: teamInfo.getTeamPlayers()){
                                String color;
                                switch (playerInfo.getPlayerType()){
                                    case DEATH:
                                        color = "&c";
                                        break;
                                    case WAIT:
                                        color = "&6";
                                        break;
                                    case START:
                                        color = "&a";
                                        break;
                                    default:
                                        color = "&7";
                                        break;
                                }
                                playerI.append("     - ").append(color).append(playerInfo.getName()).append(" ").append(playerInfo.getPlayerType()).append("\n");
                            }
                            team.append("   - ").append(teamInfo.getTeamConfig().getNameColor()).append(teamInfo.getTeamConfig().getName()).
                                    append("\n").append(playerI).append("\n");
                        }
                        BedWarMain.sendMessageToObject(" - &eTeam: \n"+team,commandSender);
                    }else{
                        BedWarMain.sendMessageToObject("&c"+config.getName()+language.getLanguage("status-unstarted"," (未启动) "),commandSender);


                    }
                }
                break;
            case "exp":
                if(strings.length < 3){
                    commandSender.sendMessage(language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",BedWarMain.COMMAND_ADMIN_NAME));
                    return false;
                }
                String playerName = strings[1];
                Player player = Server.getInstance().getPlayer(playerName);
                if(player != null){
                    playerName = player.getName();
                }
                String expString = strings[2];
                int exp = 0;
                try {
                    exp = Integer.parseInt(expString);
                }catch (Exception ignore){}
                String cause = language.getLanguage("exp-command-give-cause","指令给予");
                if(strings.length > 3){
                    cause = strings[3];
                }
                if(exp > 0){
                    PlayerData playerData = BedWarMain.getDataManager().getData(playerName);
                    playerData.addExp(exp,cause);
                    commandSender.sendMessage(language.getLanguage("exp-command-give","成功给予玩家 [1] [2] 点经验",
                            playerName,exp+""));
                }else{
                    commandSender.sendMessage(language.getLanguage("exp-give-error","经验必须大于0"));
                    return false;
                }
                break;

            case "tsl":
                teamShopLoad(commandSender);
                break;
            case "top":
                if(commandSender instanceof Player) {
                    if (strings.length < 3) {
                        commandSender.sendMessage(language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",BedWarMain.COMMAND_ADMIN_NAME));
                        return false;
                    }
                    String name = strings[2];


                    if ("add".equalsIgnoreCase(strings[1])) {
                        if(strings.length < 4){
                            commandSender.sendMessage(language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",BedWarMain.COMMAND_ADMIN_NAME));
                            return false;
                        }
                        PlayerData.DataType type = PlayerData.DataType.byName(strings[3]);
                        if (type == null) {
                            commandSender.sendMessage(language.getLanguage("type-unknown","未知类型"));
                            return true;
                        }
                        String room = null;
                        if (strings.length > 4) {
                            room = strings[4];
                        }
                        TopItem item = new TopItem(name,type,((Player) commandSender).getPosition(),"");
                        item.room = room;
                        if(BedWarMain.getTopManager().hasTop(name)){
                            commandSender.sendMessage(language.getLanguage("top-exists","存在名称为 [1] 的排行榜了", name));
                            return true;
                        }
                        item.setTitle(TextFormat.colorize('&',BedWarMain.getTitle()+" &a"+type.getName()+" &r排行榜"));
                        BedWarMain.getTopManager().addTopItem(item);
                        commandSender.sendMessage(language.getLanguage("top-create-success","排行榜创建成功"));
                    } else {
                        if(!BedWarMain.getTopManager().hasTop(name)){
                            commandSender.sendMessage(language.getLanguage("top-no-exists","不存在名称为 [1] 的排行榜",name));
                            return true;
                        }
                        TopItem topItem = BedWarMain.getTopManager().getTop(name);
                        if(topItem == null){
                            commandSender.sendMessage(language.getLanguage("top-no-exists","不存在名称为 [1] 的排行榜",name));
                            return true;
                        }
                        BedWarMain.getTopManager().removeTopItem(topItem);
                        commandSender.sendMessage(language.getLanguage("top-delete-success","排行榜删除成功"));

                    }
                }else{
                    commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
                    return false;
                }
                break;
            case "see":
                BedWarMain.sendMessageToObject(BedWarMain.getRoomManager().getRooms().keySet().toString(),commandSender);
                break;
            case "reload":
                BedWarMain.sendMessageToObject(language.getLanguage("reload-config-loading","正在读取配置文件"),commandSender);
                BedWarMain.getBedWarMain().loadBedWarConfig();
                BedWarMain.sendMessageToObject(language.getLanguage("reload-config-success","配置文件读取完成"),commandSender);
                break;
            case "start":
                if(strings.length > 1) {
                    String name = strings[1];

                    if(BedWarMain.getRoomManager().hasGameRoom(name)){
                        GameRoom room = BedWarMain.getRoomManager().getRoom(name);
                        if(room != null && room.getType() == GameRoom.GameType.WAIT){
                            room.loadTime =  2;
                            commandSender.sendMessage(language.getLanguage("start-room-success","成功强行启动游戏: [1]",name));
                        }else{
                            commandSender.sendMessage(language.getLanguage("start-room-error-unenable","房间不是等待状态或没有玩家在游戏房间中"));
                        }
                    }else{
                        commandSender.sendMessage(language.getLanguage("close-room-error-unenable","游戏房间未开启"));
                    }
                }else{
                    commandSender.sendMessage(language.getLanguage("close-room-error-unknown-name","请输入房间名"));
                }
                break;
            case "close":
                if(strings.length > 1) {
                    String name = strings[1];
                    if(BedWarMain.getRoomManager().hasGameRoom(name)){
                        BedWarMain.getRoomManager().disEnableRoom(name);
                        commandSender.sendMessage(language.getLanguage("close-room-success","成功关闭房间: [1]",name));
                    }else{
                        commandSender.sendMessage(language.getLanguage("close-room-error-unenable","游戏房间未开启"));
                    }
                }else{
                    commandSender.sendMessage(language.getLanguage("close-room-error-unknown-name","请输入房间名"));
                }
                break;
            case "cancel":
                create.remove(commandSender.getName());
                BedWarMain.sendMessageToObject(language.getLanguage("cancel-room-create","成功终止房间的创建，残留文件将在重启服务器后自动删除"), commandSender);
                // commandSender.sendMessage(TextFormat.colorize('&', "&d"));

                break;
            case "robot":

                if(strings.length < 3){
                    commandSender.sendMessage(language.getLanguage("command-admin-usage","/[1] help 查看指令帮助",BedWarMain.COMMAND_ADMIN_NAME));
                    return false;
                }
                String roomName = strings[1];
                GameRoomConfig roomConfig = BedWarMain.getRoomManager().getRoomConfig(roomName);
                if(roomConfig == null){
                    commandSender.sendMessage(language.getLanguage("room-no-exists","房间 [1] 不存在",strings[2]));
                    return false;
                }
                int count = Integer.parseInt(strings[2]);
                for(int i = 0; i < count; i++){
                    Position pos = roomConfig.getWorldInfo().getWaitPosition();
                    CompoundTag tag = EntityHuman.getDefaultNBT(pos);
                    Skin skin = Utils.getDefaultSkin();
                    tag.putCompound("Skin",new CompoundTag()
                            .putByteArray("Data", skin.getSkinData().data)
                            .putString("ModelId",skin.getSkinId())
                    );
                    int finalI = i;
                    RobotEntity entityHuman = new RobotEntity(pos.getChunk(), tag){
                        @Override
                        public String getName() {
                            return "robot No."+ finalI;
                        }
                    };
                    entityHuman.setNameTag("robot No."+ i);

                    entityHuman.setNameTagAlwaysVisible(true);
                    entityHuman.setNameTagVisible(true);
                    entityHuman.setSkin(skin);
                    entityHuman.spawnToAll();
                    BedWarMain.getRoomManager().joinRoom(new PlayerInfo(entityHuman), roomName);
                }
                break;
            case "record":
                if (BedWarMain.enableRecord) {
                    if (commandSender instanceof Player) {
                        RecordPanel.disPlayerMenu((Player) commandSender);
                    } else {
                        commandSender.sendMessage(language.getLanguage("do-not-console","请不要在控制台执行"));
                    }
                } else {
                    commandSender.sendMessage(language.getLanguage("record-not-enabled", "未启用录像功能"));
                }
                break;
            default:break;

        }
        return true;
    }

    private void teamShopLoad(CommandSender commandSender){
        if(!create.containsKey(commandSender.getName())){
            commandSender.sendMessage(language.getLanguage("template-reload-error","请先创建房间模板"));
            return;
        }
        GameRoomCreater creater = create.get(commandSender.getName());
        GameRoomConfig roomConfig = creater.getRoomConfig();
        if(roomConfig != null) {
            GameRoomConfig.loadTeamShopConfig(roomConfig);
            commandSender.sendMessage(language.getLanguage("template-reload-success","成功重新读取模板信息"));
        }else{
            commandSender.sendMessage(language.getLanguage("template-unknown","无模板信息"));
        }
    }
}
