package org.sobadfish.bedwar.manager;

import com.google.common.base.Throwables;
import com.google.gson.Gson;
import org.sobadfish.bedwar.BedWarMain;

import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * JSON数据文件读写类
  @author Sobadfish
 */
public class BaseDataWriterGetterManager<T>{

    public List<T> dataList;

    public File file;

    public BaseDataWriterGetterManager(List<T> dataList,File file){
        this.dataList = dataList;
        this.file = file;
    }

    public static <T> BaseDataWriterGetterManager<?> asFile(File file,String fileName, Class<T> tClass,Class<? extends BaseDataWriterGetterManager<?>> baseClass){
        Gson gson = new Gson();
        InputStreamReader reader = null;
        try {
            if(!file.exists()){
                BedWarMain.getBedWarMain().saveResource(fileName,false);
            }
            reader = new InputStreamReader(new FileInputStream(file));
            Object[] data = (Object[]) gson.fromJson(reader, tClass);
            Constructor<?> constructor = baseClass.getConstructor(List.class,File.class);
            if(data != null){
                return (BaseDataWriterGetterManager<?>) constructor.newInstance(new ArrayList<>(Arrays.asList(data)),file);
            }else{
                return (BaseDataWriterGetterManager<?>) constructor.newInstance(new ArrayList<>(),file);
            }


        } catch (IOException  e) {
            BedWarMain.sendMessageToConsole(BedWarMain.getLanguage().getLanguage("config-load-error","&c无法读取 [1] 配置文件"));
            e.printStackTrace();
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        } finally {
            if(reader !=null){
                try {
                    reader.close();
                } catch (IOException e) {
                    BedWarMain.sendMessageToConsole("&c"+ Throwables.getStackTraceAsString(e));
                }
            }
        }
        return null;
    }

    public void save(){
        Gson gson = new Gson();
        if(!file.exists()){
            try {
                if(!file.createNewFile()){
                    BedWarMain.sendMessageToConsole(BedWarMain.getLanguage().getLanguage("file-create-error","&c创建文件失败"));
                }
            } catch (IOException e) {
                BedWarMain.sendMessageToConsole(BedWarMain.getLanguage().getLanguage("save-player-unknown-error","未知错误 无法保存玩家数据"));
            }
        }
        try {
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
            String json = gson.toJson(dataList);
            writer.write(json,0,json.length());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
