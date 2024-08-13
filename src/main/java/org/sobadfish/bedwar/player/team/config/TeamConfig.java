package org.sobadfish.bedwar.player.team.config;


import cn.nukkit.item.Item;
import cn.nukkit.utils.BlockColor;
import lombok.Data;


import java.util.Map;

/**
 * @author SoBadFish
 * 2022/1/2
 */
@Data
public class TeamConfig {

    private String name;

    private String nameColor;

    private Item blockWoolColor;

    private BlockColor rgb;

    private String bedDestroy;

    private String bedNormal;

    private TeamConfig(String name, String nameColor, Item blockWoolColor, BlockColor rgb,String bedNormal, String bedDestroy){
        this.name = name;
        this.nameColor = nameColor;
        this.blockWoolColor = blockWoolColor;
        this.rgb = rgb;
        this.bedDestroy = bedDestroy;
        this.bedNormal = bedNormal;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setBedDestroy(String bedDestroy) {
        this.bedDestroy = bedDestroy;
    }

    public void setBedNormal(String bedNormal) {
        this.bedNormal = bedNormal;
    }

    public String getBedDestroy() {
        return bedDestroy;
    }

    public String getBedNormal() {
        return bedNormal;
    }

    public void setBlockWoolColor(Item blockWoolColor) {
        this.blockWoolColor = blockWoolColor;
    }

    public void setNameColor(String nameColor) {
        this.nameColor = nameColor;
    }

    public void setRgb(BlockColor rgb) {
        this.rgb = rgb;
    }



    public static TeamConfig getInstance(Map<?,?> map){
        String name = map.get("name").toString();
        String bedD = "";
        String bedN = "";

        if(map.containsKey("bed-destroy")){
            bedD =   map.get("bed-destroy").toString();
        }
        if(map.containsKey("bed-normal")){
            bedN =   map.get("bed-normal").toString();
        }

//        String bedN = map.get("bed-normal").toString();
        String nameColor = map.get("nameColor").toString();
        Map<?,?> m = (Map<?,?>) map.get("rgb");
        int r = Integer.parseInt(m.get("r").toString());
        int g = Integer.parseInt(m.get("g").toString());
        int b = Integer.parseInt(m.get("b").toString());
        return new TeamConfig(name,nameColor,Item.fromString(map.get("blockWoolColor").toString())
                ,new BlockColor(r,g,b),bedN,bedD);
    }

    @Override
    public boolean equals(Object obj){
        if(obj instanceof TeamConfig){
            return ((TeamConfig) obj).getName().equalsIgnoreCase(getName());
        }
        return false;
    }

}
