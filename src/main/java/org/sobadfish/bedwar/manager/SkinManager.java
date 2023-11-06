package org.sobadfish.bedwar.manager;

import cn.nukkit.entity.data.Skin;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.Utils;
import org.sobadfish.bedwar.BedWarMain;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Sobadfish
 * @date 2023/4/4
 */
public class SkinManager {

    public static LinkedHashMap<String, Skin> SKINS = new LinkedHashMap<>();


    public static Skin getSkinByName(String name) {
        if(SKINS.containsKey(name)){
            return SKINS.get(name);
        }
        return null;
    }


    /**
     * 引用LittleMonster的皮肤加载
     * @author LittleMonster开发组
     * */
    public static void init(){

        File[] files = new File(BedWarMain.getBedWarMain().getDataFolder()+"/skin").listFiles();
        if(files != null && files.length > 0){
            for(File file:files){
                String skinName = file.getName();
                if(new File(BedWarMain.getBedWarMain().getDataFolder()+"/skin/"+skinName+"/skin.png").exists()){
                    Skin skin = new Skin();
                    BufferedImage skindata = null;
                    try {
                        skindata = ImageIO.read(new File(BedWarMain.getBedWarMain().getDataFolder()+"/skin/"+skinName+"/skin.png"));
                    } catch (IOException var19) {
                        System.out.println("不存在模型");
                    }

                    if (skindata != null) {
                        skin.setSkinData(skindata);
                        skin.setSkinId(skinName);
                    }
                    //如果是4D皮肤
                    File skinJsonFile = new File(BedWarMain.getBedWarMain().getDataFolder() + "/skin/" + skinName + "/skin.json");
                    if(skinJsonFile.exists()){
                        Map<String, Object> skinJson = (new Config(BedWarMain.getBedWarMain().getDataFolder()+"/skin/"+skinName+"/skin.json", Config.JSON)).getAll();
                        String geometryName = null;

                        String formatVersion = (String) skinJson.getOrDefault("format_version", "1.10.0");
                        skin.setGeometryDataEngineVersion(formatVersion); //设置皮肤版本，主流格式有1.16.0,1.12.0(Blockbench新模型),1.10.0(Blockbench Legacy模型),1.8.0
                        switch (formatVersion){
                            case "1.16.0":
                            case "1.12.0":
                                geometryName = getGeometryName(skinJsonFile);
                                if("nullvalue".equals(geometryName)){
                                    BedWarMain.sendMessageToConsole("暂不支持" + skinName + "模型所用格式！请等待更新！");
                                }else{
                                    skin.generateSkinId(skinName);
                                    skin.setSkinResourcePatch("{\"geometry\":{\"default\":\"" + geometryName + "\"}}");
                                    skin.setGeometryName(geometryName);
                                    try {
                                        skin.setGeometryData(Utils.readFile(skinJsonFile));
                                    }catch (IOException e){
                                        return;
                                    }

                                    BedWarMain.sendMessageToConsole("模型 " + skinName + " 读取中");
                                }
                                break;
                            default:
                                BedWarMain.sendMessageToConsole("["+skinJsonFile.getName()+"] 的版本格式为："+formatVersion + "，正在尝试加载！");
                            case "1.10.0":
                            case "1.8.0":
                                for (Map.Entry<String, Object> entry : skinJson.entrySet()) {
                                    if (geometryName == null) {
                                        if (entry.getKey().startsWith("geometry")) {
                                            geometryName = entry.getKey();
                                        }
                                    }else {
                                        break;
                                    }
                                }
                                skin.generateSkinId(skinName);
                                skin.setSkinResourcePatch("{\"geometry\":{\"default\":\"" + geometryName + "\"}}");
                                skin.setGeometryName(geometryName);
                                try {
                                    skin.setGeometryData(Utils.readFile(skinJsonFile));
                                }catch (IOException e){
                                    return;
                                }
                                break;
                        }
                    }
                    BedWarMain.sendMessageToConsole(skinName+"模型读取完成");
                    SKINS.put(skinName,skin);
                }else{
                    BedWarMain.sendMessageToConsole("错误的模型名称格式 请将模型图片文件命名为 skin.png");
                }
            }
        }


    }
    private static String getGeometryName(File file) {
        Config originGeometry = new Config(file, Config.JSON);
        if (!originGeometry.getString("format_version").equals("1.12.0") && !originGeometry.getString("format_version").equals("1.16.0")) {
            return "nullvalue";
        }
        //先读取minecraft:geometry下面的项目
        List<Map<String, Object>> geometryList = (List<Map<String, Object>>) originGeometry.get("minecraft:geometry");
        //不知道为何这里改成了数组，所以按照示例文件读取第一项
        Map<String, Object> geometryMain = geometryList.get(0);
        //获取description内的所有
        Map<String, Object> descriptions = (Map<String, Object>) geometryMain.get("description");
        return (String) descriptions.getOrDefault("identifier", "geometry.unknown"); //获取identifier
    }
}
