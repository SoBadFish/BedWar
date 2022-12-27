package org.sobadfish.bedwar.command;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.PlayerData;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;
import org.sobadfish.bedwar.room.GameRoomCreater;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.room.floattext.FloatTextInfoConfig;
import org.sobadfish.bedwar.top.TopItem;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.util.LinkedHashMap;

/**
 * @author SoBadFish
 * 2022/1/3
 * 超简单的实验指令
 */
public class BedWarAdminCommand extends Command {

    public BedWarAdminCommand(String name) {
        super(name);
        this.usageMessage = "/bd help See instructions help";
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
                commandSender.sendMessage("Room to create failure");
            }
        }
        return true;
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(!commandSender.isOp()){
            BedWarMain.sendMessageToObject("&You do not have permission to use this directive",commandSender);
            return true;
        }
        if (strings.length > 0 && "help".equalsIgnoreCase(strings[0])) {
            commandSender.sendMessage("Only need to input /bd");
            commandSender.sendMessage("Other instructions to introduce:");
            commandSender.sendMessage("/bd reload Reload Config");
            commandSender.sendMessage("/bd set [name] Create a custom room template");
            commandSender.sendMessage("/bd tsl Reads the data template data and store items");
            commandSender.sendMessage("/bd see View all loading room");
            commandSender.sendMessage("/bd close [name] Close Room");
            commandSender.sendMessage("/bd exp [player] [count] <Msg> AddExperience");
            commandSender.sendMessage("/bd status Check the thread state");
            commandSender.sendMessage("/bd end Stop templates preset");
            commandSender.sendMessage("/bd float add/remove [roomName] [name] [text] Under their feet set floating empty words/delete floating");
            commandSender.sendMessage("/bd cancel Terminate the room to create");
            commandSender.sendMessage("/bd top add/remove [name] [type] [room(Don't fill in)] create/delete ranking");
            StringBuilder v = new StringBuilder("type: ");
            for(PlayerData.DataType type: PlayerData.DataType.values()){
                v.append(type.getName()).append(" , ");
            }
            commandSender.sendMessage(v.toString());
            return true;
        }
        if (strings.length == 0) {
            if(!commandSender.isPlayer()){
                commandSender.sendMessage("Please don't in the console");
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
                        commandSender.sendMessage("Please don't in the console");
                        return false;
                    }
                }else{
                    commandSender.sendMessage(TextFormat.colorize('&',"/bd set [name] &eFor the first time to create a name for the room"));
                    return false;
                }
            case "end":
                if(commandSender instanceof Player) {
                    if (create.containsKey(commandSender.getName())) {
                        create.get(commandSender.getName()).stopInit();
                    }
                }else{
                    commandSender.sendMessage("Please don't in the console");
                    return false;
                }
                break;
            case "float":
                if(strings.length < 4){

                    commandSender.sendMessage("/bw help See the help");
                    return false;
                }
                if(commandSender instanceof Player) {
                   GameRoomConfig roomConfig = BedWarMain.getRoomManager().getRoomConfig(strings[2]);
                   if(roomConfig == null){
                       commandSender.sendMessage("Room "+strings[2]+" is null");
                       return false;
                   }
                   if("remove".equalsIgnoreCase(strings[1])){
                       if(roomConfig.notHasFloatText(strings[3])){
                           commandSender.sendMessage("Floating "+strings[3]+" is null");
                           return false;
                       }
                       roomConfig.removeFloatText(strings[3]);
                       commandSender.sendMessage("Floating delete success");

                   }else{
                       if(strings.length < 5){
                           commandSender.sendMessage("Command Error run /bw help see the help");
                           return false;
                       }
                       if(roomConfig.notHasFloatText(strings[3])){
                           roomConfig.floatTextInfoConfigs.add(new FloatTextInfoConfig(strings[3], WorldInfoConfig.positionToString(((Player) commandSender).getPosition()),strings[4]));
                           commandSender.sendMessage("Success");
                       }else{
                           commandSender.sendMessage("Room exists "+strings[3]+" Floating");
                       }
                   }

                }else{
                    commandSender.sendMessage("Please don't in the console");
                    return false;
                }

                break;
            case "status":
                BedWarMain.sendMessageToObject("&6Timing task: &a"+ ThreadManager.getScheduledSize(),commandSender);
                BedWarMain.sendMessageToObject("&6The executing task regularly: &a"+ ThreadManager.getScheduledActiveCount(),commandSender);
                BedWarMain.sendMessageToObject("&6The thread info: &r\n"+ThreadManager.info(),commandSender);
                BedWarMain.sendMessageToObject("&6Room Status: &a",commandSender);
                for(GameRoomConfig config: BedWarMain.getRoomManager().getRoomConfigs()){
                    GameRoom room = BedWarMain.getRoomManager().getRoom(config.name);
                    if(room != null){

                        BedWarMain.sendMessageToObject("&a"+config.getName()+" (Started) "+room.getType()+" : &2"+room.getPlayerInfos().size(),commandSender);
                    }else{
                        BedWarMain.sendMessageToObject("&c"+config.getName()+" (not start)",commandSender);
                    }
                }
                break;
            case "exp":
                if(strings.length < 3){
                    commandSender.sendMessage("Command Error run /bw help see the help");
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
                String cause = "Command";
                if(strings.length > 3){
                    cause = strings[3];
                }
                if(exp > 0){
                    PlayerData playerData = BedWarMain.getDataManager().getData(playerName);
                    playerData.addExp(exp,cause);
                    commandSender.sendMessage("Success give to "+playerName+" "+exp+" exp");
                }else{
                    commandSender.sendMessage("exp must be greater than zero");
                    return false;
                }
                break;
            case "tsl":
                teamShopLoad(commandSender);
                break;
            case "top":
                if(commandSender instanceof Player) {
                    if (strings.length < 3) {
                        commandSender.sendMessage("Command Error run /bw help see the help");
                        return false;
                    }
                    String name = strings[2];


                    if ("add".equalsIgnoreCase(strings[1])) {
                        if(strings.length < 4){
                            commandSender.sendMessage("Command Error run /bw help see the help");
                            return false;
                        }
                        PlayerData.DataType type = PlayerData.DataType.byName(strings[3]);
                        if (type == null) {
                            commandSender.sendMessage("Unknown type");
                            return true;
                        }
                        String room = null;
                        if (strings.length > 4) {
                            room = strings[4];
                        }
                        TopItem item = new TopItem(name,type,((Player) commandSender).getPosition(),"");
                        item.room = room;
                        if(BedWarMain.getTopManager().hasTop(name)){
                            commandSender.sendMessage("Exists "+name+" Ranking");
                            return true;
                        }
                        item.setTitle(TextFormat.colorize('&',BedWarMain.getTitle()+" &a"+type.getName()+" &r排行榜"));
                        BedWarMain.getTopManager().addTopItem(item);
                        commandSender.sendMessage("Ranking create success");
                    } else {
                        if(!BedWarMain.getTopManager().hasTop(name)){
                            commandSender.sendMessage(""+name+" is null");
                            return true;
                        }
                        TopItem topItem = BedWarMain.getTopManager().getTop(name);
                        if(topItem == null){
                            commandSender.sendMessage(""+name+" is null");
                            return true;
                        }
                        BedWarMain.getTopManager().removeTopItem(topItem);
                        commandSender.sendMessage("Ranking delete success");

                    }
                }else{
                    commandSender.sendMessage("Please don't in the console");
                    return false;
                }
                break;
            case "see":
                BedWarMain.sendMessageToObject(BedWarMain.getRoomManager().getRooms().keySet().toString(),commandSender);
                break;
            case "reload":
                BedWarMain.sendMessageToObject("loading config",commandSender);
                BedWarMain.getBedWarMain().loadBedWarConfig();
                BedWarMain.sendMessageToObject("config load success",commandSender);
                break;
            case "close":
                if(strings.length > 1) {
                    String name = strings[1];
                    if(BedWarMain.getRoomManager().hasGameRoom(name)){
                        BedWarMain.getRoomManager().disEnableRoom(name);
                        commandSender.sendMessage("close room success: "+name);
                    }else{
                        commandSender.sendMessage("Room not started");
                    }
                }else{
                    commandSender.sendMessage("place input room name");
                }
                break;
            case "cancel":
                create.remove(commandSender.getName());
                BedWarMain.sendMessageToObject("Successful end of the room to create, residual files will be automatically deleted after restart the server", commandSender);
                // commandSender.sendMessage(TextFormat.colorize('&', "&d"));

                break;

            default:break;

        }
        return true;
    }

    private void teamShopLoad(CommandSender commandSender){
        if(!create.containsKey(commandSender.getName())){
            commandSender.sendMessage("Please first create room template");
            return;
        }
        GameRoomCreater creater = create.get(commandSender.getName());
        GameRoomConfig roomConfig = creater.getRoomConfig();
        if(roomConfig != null) {
            GameRoomConfig.loadTeamShopConfig(roomConfig);
            commandSender.sendMessage("Successful reread template information");
        }else{
            commandSender.sendMessage("No template information");
        }
    }
}
