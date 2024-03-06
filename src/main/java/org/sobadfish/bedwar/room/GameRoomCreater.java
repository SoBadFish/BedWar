package org.sobadfish.bedwar.room;

import cn.nukkit.math.BlockFace;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.item.config.ItemInfoConfig;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.player.team.config.TeamInfoConfig;
import org.sobadfish.bedwar.room.config.GameRoomConfig;
import org.sobadfish.bedwar.tools.Utils;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * @author SoBadFish
 * 2022/1/13
 */
public class GameRoomCreater {

    private final PlayerInfo creator;

    private boolean isCreate;

    private boolean isRoomCreate;

    private int flag = 1;

    public int setFlag = 1;

    private String roomName = null;

    private int inflag = 0;

    private int min;

    private int itemFlag = 0;

    private GameRoomConfig roomConfig;

    private WorldInfoConfig worldInfoConfig;

    private final LinkedHashMap<String,Integer> moneyItemSize = new LinkedHashMap<>();

    /**
     * 队伍出生点
     * */
    private final LinkedHashMap<String, String> team = new LinkedHashMap<>();
    /**
     * 队伍床
     * */
    private final LinkedHashMap<String, String> teamBed = new LinkedHashMap<>();
    /**
     * 队伍床
     * */
    private final LinkedHashMap<String, BlockFace> teamBedFace = new LinkedHashMap<>();

    private final LinkedHashMap<String, String> teamShop = new LinkedHashMap<>();

    private final LinkedHashMap<String, String> teamShop2 = new LinkedHashMap<>();

    /**
     * 物品刷新点
     * */
    private final LinkedHashMap<String, ArrayList<String>> itemPosition = new LinkedHashMap<>();

    public GameRoomCreater(PlayerInfo player){
        this.creator = player;
    }



    public void onCreatePreset(String value){
        if(flag !=  1){
            creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("go-on-default-create-not-use","&c你正在进行默认创建，无法使用预设"));
            return;
        }
        switch (setFlag){
            case 1:
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-template","&2正在创建 名称为 &r[1] &2的房间模板",value));
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-run-command-min-player",
                        "&e继续执行 &r/[1] set &a[最低玩家数]&e 执行下一步操作","bd"));
                roomName = value;
                setFlag++;
                break;
            case 2:
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-setting-min-player",
                        "&2设置最低人数 [1]",value));
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-run-command-max-player",
                        "&e继续执行 &r/[1] set &2[最大玩家数]&e 执行下一步操作","bd"));
                min = Integer.parseInt(value);
                setFlag++;
                break;
            case 3:
                int max = Integer.parseInt(value);
                roomConfig = GameRoomConfig.createGameRoom(roomName,min, max);
                ArrayList<String> itemName = roomConfig.moneyItem.getNames();
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-setting-max-player",
                        "&2设置最大人数:&b [1]",value));
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-can-cancel","&a可终止预设"));
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-last-spawn-item",
                                "&e继续执行 &r/[1] set &2[数量] &e执行设置: &r[2] &e生成点数量操作","bd",itemName.get(inflag)
                ));
                isRoomCreate = true;
                setFlag++;
                break;
            case 4:
                itemName = roomConfig.moneyItem.getNames();
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-last-spawn-item-setting",
                        "&2设置 &r[1]&2生成点数量:&b [2]",
                        itemName.get(inflag),value));
                moneyItemSize.put(itemName.get(inflag),Integer.parseInt(value));
                inflag++;
                if(inflag == itemName.size()){
                    creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-default-setting-success","&a预设完成"));
                    setFlag = 1;
                    return;
                }
//                creator.sendForceMessage("&e继续执行 &r/bd set &2[数量] &e执行设置: &r"+itemName.get(inflag)+" &e生成点数量操作");
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-last-spawn-item",
                        "&e继续执行 &r/[1] set &2[数量] &e执行设置: &r[2] &e生成点数量操作","bd",itemName.get(inflag)));

                break;
            default: break;
        }

    }

    public void stopInit(){
        if(setFlag >= 4) {
            setFlag = 1;
            creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-default-setting-cancel","&c终止预设"));
        }else{
            creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-default-setting-cancel-error","&c无法终止预设"));
        }
    }

    public boolean onCreateNext(){
        if(setFlag != 1){
            creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-must-success-setting","&c请先完成预设"));
            return true;
        }
        //测试创建
        switch (flag) {
            case 1:
                if(roomConfig == null) {
                    roomConfig = GameRoomConfig.createGameRoom("Test Room", 4, 16);
                    isRoomCreate = true;
                    creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-default-success","&2成功创建一个 名字已经固定为 &r“[1]”&2的游戏房间模板 已设定最低玩家为 &b4&2 最大玩家为 &b16&r",
                            "Test Room"));
                    creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-run-command-game-world","继续执行/[1] 进行下一步 [进入游戏地图设置]","bd"));
                }else{
                    creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-default-setting-room-success","&2成功预设房间设置"));
                    creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-run-command-game-world","继续执行/[1] 进行下一步 [进入游戏地图设置]","bd"));
                }
                flag++;
                break;
            case 2:
                worldInfoConfig = WorldInfoConfig.createWorldConfig(creator.getLevel().getFolderName());
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-setting-game-world-success","&2成功设定游戏地图"));
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-run-command-game-wait","&e继续执行 &r/[1] &e进行下一步 &b[设置等待大厅]","bd"));
                flag++;
                break;
            case 3:
                worldInfoConfig.setWaitPosition(creator.getPosition());
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-setting-game-wait-success","&2成功设置等待大厅"));
//                creator.sendForceMessage("&e继续执行 &r/bd &e进行下一步 &r[&b设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(team.size()))+"商店 &21&b /&d "+roomConfig.teamCfg.size()+"&r]");
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-setting-shop",
                        "&e继续执行 &r/[1] &e进行下一步 &r[&b设置[2]商店 &21&b /&d [3]&r]","bd",
                        (new ArrayList<>(roomConfig.teamCfg.keySet()).get(team.size())),
                        roomConfig.teamCfg.size()+""
                        ));
                flag++;
                break;
            case 4:
                createShopPos();
                break;
            case 5:
                createShop2Pos();
                break;
            case 6:
                team.put(new ArrayList<>(roomConfig.teamCfg.keySet()).get(team.size()),WorldInfoConfig.positionToString(creator.getPosition()));
                int index;
                team.size();
                index = team.size() - 1;
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-setting-team-spawn",
                        "&2设置&r [1] &2出生点坐标&r [&2[2] &b/&d [3]&r]",
                        (new ArrayList<>(roomConfig.teamCfg.keySet()).get(index)),
                        team.size()+"",
                        roomConfig.getTeamCfg().size()+""

                        ));
//                creator.sendForceMessage("&2设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(index))+"出生点 &r[&2"+team.size()+"&b/&d"+roomConfig.getTeamCfg().size()+"&r]");
                if(team.size() == roomConfig.getTeamCfg().size()){

                    creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-setting-team-spawn-success",
                            "&2队伍出生点设置完成"));
                    creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-run-command-bed-location",
                            "&e继续执行 &r/[1] &e进行下一步 &r[&b设置[2]床的位置&2 1&b /&d [3]&r]",
                            "bd",
                            (new ArrayList<>(roomConfig.teamCfg.keySet()).get(0)),
                            roomConfig.teamCfg.size()+""));
//                    creator.sendForceMessage("&e继续执行 &r/bd &e进行下一步 &r[&b设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(0))+"床的位置&2 1&b /&d "+roomConfig.teamCfg.size()+"&r]");
                    flag++;
                    break;
                }
                creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-run-command-spawn",
                        "&e继续执行 &r/[1] &e进行下一步 &r[&b设置[2]出生点 &21&b /&d [3]&r]",
                        "bd",
                        (new ArrayList<>(roomConfig.teamCfg.keySet()).get(team.size())),
                        (team.size() + 1)+"" ,
                        roomConfig.getTeamCfg().size()+  ""
                        ));
                break;
            case 7:
               createBedPos();
                break;
            case 8:
               return createItemPos();

            default:
                break;
        }

        return true;

    }
    /**
     * 创建商店1坐标
     * */
    private void createShopPos(){
        teamShop.put(new ArrayList<>(roomConfig.teamCfg.keySet()).get(teamShop.size()),WorldInfoConfig.locationToString(creator.getLocation()));

        creator.sendForceMessage("&2设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(teamShop.size() - 1))+"商店 &r[&a"+teamShop.size()+"&b/&d"+roomConfig.getTeamCfg().size()+"&r]");
        if(teamShop.size() == roomConfig.getTeamCfg().size()){
            creator.sendForceMessage("&e继续执行 &r/bd &e进行下一步 &r[&b设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(0))+"团队商店 &r[&2"+(team.size() + 1)+" &b/&d "+roomConfig.getTeamCfg().size()+"&r]");
            flag++;
            return;
        }
        creator.sendForceMessage("&e继续执行 &r/bd &e进行下一步 &r[&b设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(teamShop.size()))+"商店 &r[&2"+(teamShop.size() + 1)+" &b/&d "+roomConfig.getTeamCfg().size()+"&r]");
    }
    /**
     * 创建商店2坐标
     * */
    private void createShop2Pos(){
        teamShop2.put(new ArrayList<>(roomConfig.teamCfg.keySet()).get(teamShop2.size()),WorldInfoConfig.locationToString(creator.getLocation()));
        creator.sendForceMessage("&2设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(teamShop2.size() - 1))+"团队商店 &r[&2"+teamShop2.size()+" &b/&d "+roomConfig.getTeamCfg().size()+"&r]");
        if(teamShop2.size() == roomConfig.getTeamCfg().size()){
            creator.sendForceMessage("&e继续执行 &r/bd &e进行下一步 &r[&b设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(0))+"出生点 &r[&2"+(team.size() + 1)+" &b/&d "+roomConfig.getTeamCfg().size()+"&r]");
            flag++;
            return;

        }
        creator.sendForceMessage("&e继续执行 &r/bd &e进行下一步 &r[&b设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(teamShop2.size() ))+"团队商店 &r[&2"+(teamShop2.size() + 1)+" &b/&d "+roomConfig.getTeamCfg().size()+"&r]");

    }

    /**
     * 创建床坐标
     * */
    private void createBedPos(){
        teamBed.put(new ArrayList<>(roomConfig.teamCfg.keySet()).get(teamBed.size()),WorldInfoConfig.positionToString(creator.getPosition()));
        teamBedFace.put(new ArrayList<>(roomConfig.teamCfg.keySet()).get(teamBedFace.size()), creator.getHorizontalFacing());
        creator.sendForceMessage("&2设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(teamBed.size() - 1))+"床坐标 &r[&2"+teamBed.size()+" &b/&d "+roomConfig.getTeamCfg().size()+"&r]");
        if(teamBed.size() == roomConfig.getTeamCfg().size()){

            creator.sendForceMessage("&2队伍床设置完成");
            creator.sendForceMessage("&e继续执行 &r/bd &e进行下一步 &r[&b设置 &r"+roomConfig.moneyItem.getNames().get(0)+" &2刷新点 &r"+" [&21 &b/&d "+moneyItemSize.getOrDefault(roomConfig.moneyItem.getNames().get(0),4)+"&r]");
            ArrayList<TeamInfoConfig> teamInfoConfigs = new ArrayList<>();
            for(String teamName : team.keySet()){
                TeamInfoConfig teamInfoConfig = new TeamInfoConfig(roomConfig.teamCfg.get(teamName),teamBed.get(teamName),teamBedFace.get(teamName),team.get(teamName));
                teamInfoConfig.setVillage(new LinkedHashMap<String, String>(){
                    {
                        put("defaultShop",teamShop.get(teamName));
                        put("teamShop",teamShop2.get(teamName));
                    }
                });
                teamInfoConfigs.add(teamInfoConfig);
            }
            roomConfig.setTeamConfigs(teamInfoConfigs);
            flag++;
            return;

        }
        creator.sendForceMessage("&e继续执行 &r/bd &e进行下一步 &r[&b设置"+(new ArrayList<>(roomConfig.teamCfg.keySet()).get(teamBed.size()))+"床&r [&2"+(teamBed.size() + 1)+" &b/&d "+roomConfig.getTeamCfg().size()+"&r]");
    }
    /**
     * 创建生成点坐标
     * */
    private boolean createItemPos(){
        ArrayList<String> itemName = roomConfig.moneyItem.getNames();

        String name = itemName.get(itemFlag);
        if(!itemPosition.containsKey(name)){
            itemPosition.put(name,new ArrayList<>());
        }
        ArrayList<String> positions = itemPosition.get(name);
        if(positions.size() < moneyItemSize.getOrDefault(name,4)){
            positions.add(WorldInfoConfig.positionToString(creator.getPosition()));
            creator.sendForceMessage("&2设置&r "+name+" &2生成点坐标&r [&2"+positions.size()+" &b/&d "+moneyItemSize.getOrDefault(name,4)+"&r]");
            if(positions.size() != moneyItemSize.getOrDefault(name,4)) {
                creator.sendForceMessage("&e继续执行 &r/bd &e进行下一步 &r[&b设置物品刷新点&r " + name + " [&2" + (positions.size() + 1) + " &b/&d "+moneyItemSize.getOrDefault(name,4)+"&r]");
            }else{

                if(itemName.size() > itemFlag+1){
                    creator.sendForceMessage("&2设置 &r" + name + " &2生成点坐标完成");
                    creator.sendForceMessage("&e继续执行 &r/bd &e进行下一步 &r[&b设置 &r"+ itemName.get(itemFlag + 1) + " &2刷新点 &r [&21 &b/&d "+moneyItemSize.getOrDefault(itemName.get(itemFlag + 1),4)+"&r]");
                }
                itemFlag++;
                if (itemFlag >= roomConfig.moneyItem.getNames().size()) {
                    creator.sendForceMessage("&2设置所有生成点坐标完成");
                    ArrayList<ItemInfoConfig> itemInfoConfigs = new ArrayList<>();
                    for (String s1 : itemPosition.keySet()) {
                        itemInfoConfigs.add(new ItemInfoConfig(roomConfig.moneyItem.get(s1), itemPosition.get(s1), 20));
                    }
                    worldInfoConfig.setItemInfos(itemInfoConfigs);
                    roomConfig.setWorldInfo(worldInfoConfig);
                    flag = 1;
                    isCreate = true;
                    creator.sendForceMessage("&a游戏房间创建完成 &c(重启生效配置)");

                    return false;
                }

            }
        }
        return true;
    }
    public boolean createRoom(){
        if(isCreate) {
            roomConfig.save();
            Utils.addDescription(new File(BedWarMain.getBedWarMain().getDataFolder()+"/rooms/"+roomConfig.getName()+"/room.yml"),
                    BedWarMain.getLanguage().roomDescription,true);
            creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-success","&a游戏已创建"));
            return true;
        }else{
            creator.sendForceMessage(BedWarMain.getLanguage().getLanguage("create-room-error","&c游戏未创建"));
        }
        return false;
    }


    public GameRoomConfig getRoomConfig(){
        if(isRoomCreate) {
            return roomConfig;
        }
        return null;
    }

    public PlayerInfo getCreator() {
        return creator;
    }
}
