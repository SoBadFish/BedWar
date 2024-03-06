package org.sobadfish.bedwar.manager;

import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Properties;

/**
 * 语言文件
 * @author Sobadfish
 * 2023/1/10
 */
public class LanguageManager {

    public HashMap<String, String> ini;

    public Config roomDescription;

    public String lang;


    public LanguageManager(PluginBase plugin,String lang){
        ini = new HashMap<>();
        if(!new File(plugin.getDataFolder()+"/lang").exists()){
            if(!new File(plugin.getDataFolder()+"/lang").mkdirs()){
                plugin.getLogger().error("Could not create lang");
            }
        }
        File iniFile = new File(plugin.getDataFolder()+ "/lang/"+lang+"/language.ini");
        if(!iniFile.exists()){
            plugin.saveResource("lang/"+lang+"/language.ini", "lang/"+lang+"/language.ini",false);
        }
        this.lang = lang;
        BufferedReader br;
        try {
            InputStream in = new FileInputStream(iniFile);
            br = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            Properties props = new Properties();
            props.load(br);
            for(Object s: props.keySet()){
                ini.put(s.toString(), props.getProperty(s.toString()));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //读取 room.yml注释
        InputStream resource = plugin.getResource("description/room-"+lang+".yml");
        if (resource == null) {
            resource = plugin.getResource("description/room-"+lang+".yml");
        }
        roomDescription = new Config();
        roomDescription.load(resource);


    }

    public Config getRoomDescription() {
        return roomDescription;
    }

    public String getLanguage(String key, IniValueData... values) {
        String value = "";
        return getLanguage(key,value, values);
    }

    public String getLanguage(String key,String defaultValue,String... values) {
        IniValueData[] iniValueData = new IniValueData[values.length];
        int size = 0;
        if(values.length > 0) {
            for(String s: values){
                iniValueData[size++] = new IniValueData(size, s);
            }
        }

        return getLanguage(key,defaultValue, iniValueData);
    }

    public String getLanguage(String key,String defaultValue,IniValueData[] values) {
        String value = defaultValue;
        if(ini != null && ini.containsKey(key)){
            value = ini.get(key);
        }
        if(value == null || value.length() == 0){
            value = defaultValue;
        }
        if(values.length > 0){
            for(IniValueData iv : values){
                value = value.replace("["+iv.key+"]",iv.value);
            }
        }
        value = value.replace("[n]","\n");
        return TextFormat.colorize('&',value);
    }

    public static class IniValueData{
        public int key;

        public String value;

        public IniValueData(int key, String value){
            this.key = key;
            this.value = value;
        }
    }

}
