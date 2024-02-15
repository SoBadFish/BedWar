package org.sobadfish.bedwar.panel;

import cn.lanink.gamecore.form.windows.AdvancedFormWindowCustom;
import cn.lanink.gamecore.form.windows.AdvancedFormWindowSimple;
import cn.nukkit.Player;
import com.google.gson.JsonObject;
import net.easecation.ghosty.LevelRecordPack;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.manager.RecordManager;

import java.io.File;
import java.util.List;

/**
 * 录像管理面板
 *
 * @author LT_Name
 */
public class RecordPanel {

    private static final String PLUGIN_NAME = "&l&cB&6e&ed&aW&ba&9r";

    public static void disPlayerMenu(Player player) {

        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(PLUGIN_NAME + " - 录像管理", "请选择您要进行的操作");
        simple.addButton("查看录像列表", RecordPanel::disRecordList);
        simple.addButton("查找录像", RecordPanel::disRecordSearch);
        simple.showToPlayer(player);
    }

    public static void disRecordList(Player player) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(PLUGIN_NAME + " - 录像列表", "请选择要操作的录像");
        List<File> list = BedWarMain.getRecordManager().getRecordFileList();
        if (list != null && !list.isEmpty()) {
            for (File file : list) {
                simple.addButton(file.getName().split("\\.")[0], cp -> disRecordInfo(player, file));
            }
        } else {
            simple.setContent("哎呀！还没有录像文件呢！快让玩家们玩一局吧！");
        }
        simple.addButton("返回", RecordPanel::disPlayerMenu);
        simple.showToPlayer(player);
    }

    public static void disRecordSearch(Player player) {
        AdvancedFormWindowCustom custom = new AdvancedFormWindowCustom(PLUGIN_NAME + " - 录像查找");

        //TODO

        custom.showToPlayer(player);
    }

    public static void disRecordInfo(Player player, File file) {
        AdvancedFormWindowSimple simple = new AdvancedFormWindowSimple(PLUGIN_NAME + " - 录像详细信息");
        LevelRecordPack recordPack = BedWarMain.getRecordManager().getRecordPack(file);
        JsonObject metadata = recordPack.getMetadata();
        simple.setContent("录像名称：" + file.getName() +
                "\n录像房间：" + metadata.getAsJsonPrimitive("roomName") +
                "\n录像地图：" + metadata.getAsJsonPrimitive("roomWorld") +
                "\n录制完成时间：" + metadata.getAsJsonPrimitive("time"));
        simple.addButton("播放录像", cp -> {
            RecordManager.OK ok = BedWarMain.getRecordManager().playRecord(file, player);
            if (ok.isOK()) {
                player.sendMessage("开始播放录像：" + file.getName());
            } else {
                player.sendMessage("播放录像失败！原因：" + ok.getMessage());
            }
        });
        simple.addButton("返回", cp -> disRecordList(player));
        simple.showToPlayer(player);
    }

}
