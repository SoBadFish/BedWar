package org.sobadfish.bedwar.player.team.config;


import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;


import java.util.Map;

/**
 * @author SoBadFish
 * 2022/1/2
 */

public class TeamConfig {

    private String name;

    private String nameColor;

    private Item blockWoolColor;

    private BlockColor rgb;

    private TeamConfig(String name, String nameColor, Item blockWoolColor, BlockColor rgb){
        this.name = name;
        this.nameColor = nameColor;
        this.blockWoolColor = blockWoolColor;
        this.rgb = rgb;
    }

    public String getName() {
        return name;
    }

    public Item getBlockWoolColor() {
        return blockWoolColor;
    }

    public BlockColor getRgb() {
        return rgb;
    }

    public String getNameColor() {
        return nameColor;
    }

    public static TeamConfig getInstance(Map<?,?> map){
        String name = map.get("name").toString();
        String nameColor = map.get("nameColor").toString();
        Map<?,?> m = (Map<?,?>) map.get("rgb");
        int r = Integer.parseInt(m.get("r").toString());
        int g = Integer.parseInt(m.get("g").toString());
        int b = Integer.parseInt(m.get("b").toString());
        return new TeamConfig(name,nameColor,Item.fromString(map.get("blockWoolColor").toString())
                ,new BlockColor(r,g,b));
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof TeamConfig){
            return ((TeamConfig) obj).getName().equalsIgnoreCase(getName());
        }
        return false;
    }

}
