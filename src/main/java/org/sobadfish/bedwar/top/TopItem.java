package org.sobadfish.bedwar.top;

import cn.nukkit.level.Position;
import lombok.Getter;
import lombok.Setter;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerData;
import org.sobadfish.bedwar.world.config.WorldInfoConfig;

import java.util.ArrayList;
import java.util.List;

public class TopItem {

    public String name;

    public String topType;

    public String position;

    @Getter
    @Setter
    public String title = "";

    public String room;


    public TopItem(String name,PlayerData.DataType topType,Position position,String title){
        this.topType = topType.getName();
        this.position = WorldInfoConfig.positionToString(position);
        this.name = name;
        this.title = title;
    }

    public PlayerData.DataType getTopType() {
        return PlayerData.DataType.byName(topType);
    }

    public Position getPosition() {
        return WorldInfoConfig.getPositionByString(position);
    }



    public String getListText(){
        StringBuilder builder = new StringBuilder(getTitle()+"\n");

        List<PlayerData> dataList = new ArrayList<>(BedWarMain.getDataManager().dataList);
        dataList.sort((o1, o2) -> {
            if(room != null){
                return o2.getRoomData(room).getInt(getTopType()) - o1.getRoomData(room).getInt(getTopType());
            }else{
                return o2.getFinalData(getTopType()) - o1.getFinalData(getTopType());
            }
        });
        String topColor;
        int top = 1;
        if(!dataList.isEmpty()) {

            for (PlayerData data : dataList) {
                if (top >= 10) {
                    break;
                }
                if (top <= 3) {
                    switch (top) {
                        case 1:
                            topColor = "&l&e";
                            break;
                        case 2:
                            topColor = "&l&a";
                            break;

                        default:
                            topColor = "&l&b";
                            break;
                    }
                } else {
                    topColor = "&7";
                }
                int num;
                if (room != null) {
                    num = data.getRoomData(room).getInt(getTopType());
                } else {
                    num = data.getFinalData(getTopType());
                }
                builder.append("&7[")
                        .append(topColor)
                        .append("TOP")
                        .append(top)
                        .append("&7]&r ")
                        .append(data.getLevelString())
                        .append(" ")
                        .append(data.getName())
                        .append(" ")
                        .append("  &a")
                        .append(num)
                        .append("\n");
                top++;
            }
        }else{
            builder.append("&c暂无");
        }

        return builder.toString();
    }

    @Override
    public boolean equals(Object o) {
        if(o instanceof TopItem){
            return ((TopItem) o).name.equalsIgnoreCase(name) && ((TopItem) o).topType.equals(topType);
        }
        return false;
    }

}
