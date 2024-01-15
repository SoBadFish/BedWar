package org.sobadfish.bedwar.tools;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.block.BlockAir;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Level;
import cn.nukkit.level.Location;
import cn.nukkit.level.Position;
import cn.nukkit.math.Vector3;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.DyeColor;
import cn.nukkit.utils.TextFormat;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.entity.EntityBlueWitherSkull;
import org.sobadfish.bedwar.entity.baselib.BaseEntity;
import org.sobadfish.bedwar.manager.ThreadManager;
import org.sobadfish.bedwar.player.PlayerInfo;
import org.sobadfish.bedwar.room.GameRoom;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 感谢MobPlugin插件开发组提供的AI算法
 *
 * @author @Mobplugin
 * 2022/1/8
 */
public class Utils {

    private static final SplittableRandom RANDOM = new SplittableRandom(System.currentTimeMillis());

    public static int rand(int min, int max) {
        return min == max ? max : RANDOM.nextInt(max + 1 - min) + min;
    }

    public static double rand(double min, double max) {
        return min == max ? max : min + Math.random() * (max - min);
    }

    public static float rand(float min, float max) {
        return min == max ? max : min + (float) Math.random() * (max - min);
    }

    public static boolean rand() {
        return RANDOM.nextBoolean();
    }

    public static double calLinearFunction(Vector3 pos1, Vector3 pos2, double element, int type) {
        if (pos1.getFloorY() != pos2.getFloorY()) {
            return 1.7976931348623157E308D;
        } else if (pos1.getX() == pos2.getX()) {
            return type == 1 ? pos1.getX() : 1.7976931348623157E308D;
        } else if (pos1.getZ() == pos2.getZ()) {
            return type == 0 ? pos1.getZ() : 1.7976931348623157E308D;
        } else {
            return type == 0 ? (element - pos1.getX()) * (pos1.getZ() - pos2.getZ()) / (pos1.getX() - pos2.getX()) + pos1.getZ() : (element - pos1.getZ()) * (pos1.getX() - pos2.getX()) / (pos1.getZ() - pos2.getZ()) + pos1.getX();
        }
    }

    public static ArrayList<Player> getAroundOfPlayers(Position player, int size) {
        ArrayList<Player> players = new ArrayList<>();
        for (Entity entity : getAroundPlayers(player, size, false)) {
            players.add((Player) entity);
        }
        return players;
    }

    public static LinkedList<Entity> getAroundPlayers(Position player, int size, boolean isEntity) {
        LinkedList<Entity> explodePlayer = new LinkedList<>();
        for (Entity player1 : player.level.getEntities()) {

            if (player1.x < player.x + size && player1.x > player.x - size && player1.z < player.z + size && player1.z > player.z - size && player1.y < player.y + size && player1.y > player.y - size) {
                if (!isEntity && player instanceof Player && ((Player) player).getGamemode() != 3) {
                    explodePlayer.add(player1);
                } else if (isEntity && !(player1 instanceof BaseEntity)) {
                    explodePlayer.add(player1);
                }

            }
        }
        return explodePlayer;
    }

    public static BedWarMain.UiType loadUiTypeByName(String name) {
        BedWarMain.UiType type = BedWarMain.UiType.valueOf(name.toUpperCase());
        if (type == null) {
            type = BedWarMain.UiType.AUTO;
        }
        return type;
    }

    public static void toDelete(File file) {
        File[] files = file.listFiles();
        if (files != null) {
            for (File file1 : files) {
                if (file1.isDirectory()) {
                    toDelete(file1);
                } else {
                    file1.delete();
                }
            }
        }
        file.delete();

    }

    /**
     * 放烟花
     */
    public static void spawnFirework(Position position) {

        Level level = position.getLevel();
        ItemFirework item = new ItemFirework();
        CompoundTag tag = new CompoundTag();
        Random random = new Random();
        CompoundTag ex = new CompoundTag();
        ex.putByteArray("FireworkColor", new byte[]{
                (byte) DyeColor.values()[random.nextInt(ItemFirework.FireworkExplosion.ExplosionType.values().length)].getDyeData()
        });
        ex.putByteArray("FireworkFade", new byte[0]);
        ex.putBoolean("FireworkFlicker", random.nextBoolean());
        ex.putBoolean("FireworkTrail", random.nextBoolean());
        ex.putByte("FireworkType", ItemFirework.FireworkExplosion.ExplosionType.values()
                [random.nextInt(ItemFirework.FireworkExplosion.ExplosionType.values().length)].ordinal());
        tag.putCompound("Fireworks", (new CompoundTag("Fireworks")).putList(new ListTag<CompoundTag>("Explosions").add(ex)).putByte("Flight", 1));
        item.setNamedTag(tag);
        CompoundTag nbt = new CompoundTag();
        nbt.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("", position.x + 0.5D))
                .add(new DoubleTag("", position.y + 0.5D))
                .add(new DoubleTag("", position.z + 0.5D))
        );
        nbt.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("", 0.0D))
                .add(new DoubleTag("", 0.0D))
                .add(new DoubleTag("", 0.0D))
        );
        nbt.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("", 0.0F))
                .add(new FloatTag("", 0.0F))

        );
        nbt.putCompound("FireworkItem", NBTIO.putItemHelper(item));
        EntityFirework entity = new EntityFirework(level.getChunk((int) position.x >> 4, (int) position.z >> 4), nbt);
        entity.spawnToAll();
    }

    private static final String KEY_HEADER = "header";

    private static final String KEY_FOOTER = "footer";

    /**
     * 配置文件动态增加注释
     * @author https://github.com/MemoriesOfTime/MemoriesOfTime-GameCore
     * @param file 配置文件
     * @param description 注释文件
     * @param clearOriginalComment 清除原始选项
     * */

    public static void addDescription(File file, Config description, boolean clearOriginalComment) {
        if (!file.exists()) {
            return;
        }

        StringBuilder result = new StringBuilder();

        //添加头部
        if (description.exists(KEY_HEADER) && !description.getString(KEY_HEADER).trim().isEmpty()) {
            for (String header : description.getString(KEY_HEADER).trim().split("\n")) {
                result.append("# ").append(header).append(System.lineSeparator());
            }
            result.append(System.lineSeparator());
        }

        //添加内容
        StringBuilder keyBuilder = new StringBuilder();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(Files.newInputStream(file.toPath()), StandardCharsets.UTF_8))) {
            String line;
            LinkedList<String[]> path = new LinkedList<>();
            Pattern pattern = Pattern.compile("^( *)([a-zA-Z0-9\u4e00-\u9fa5_-]+):"); //u4e00-u9fa5 中文
            int lastIdent = 0;
            String[] last = null;
            int blankLine = 0;
            //逐行读取并添加介绍
            while ((line = in.readLine()) != null) {
                if (clearOriginalComment && line.trim().startsWith("#")) {
                    continue;
                }

                if (line.trim().isEmpty()) {
                    if (blankLine > 1) {
                        continue;
                    }
                    blankLine++;
                }

                Matcher matcher = pattern.matcher(line);
                if (!matcher.find()) {
                    result.append(line).append(System.lineSeparator());
                    continue;
                }

                String current = matcher.group(2);
                String ident = matcher.group(1);
                int newIdent = ident.length();

                //返回上一级
                if (newIdent < lastIdent) {
                    int reduced = lastIdent - newIdent; //需要移除的空格数量
                    int i = 0;
                    while (i < reduced && !path.isEmpty()) {
                        if (path.pollLast()[1].length() == newIdent) { //返回到同级后 就不需要再移除上一级了
                            break;
                        }
                        i++;
                    }
                    lastIdent = lastIdent - reduced;
                }
                //进入下一级
                if (newIdent > lastIdent) {
                    path.add(last);
                    lastIdent = newIdent;
                }
                last = new String[]{current, ident};

                keyBuilder.setLength(0);
                for (String[] part : path) {
                    keyBuilder.append('.').append(part[0]);
                }
                keyBuilder.append('.').append(current);
                String key = keyBuilder.substring(1); //忽略最开始的 . 号
                if (description.exists(key) && !description.getString(key).trim().isEmpty()) {
                    String[] comments = description.getString(key).trim().split("\n");
                    for (String comment : comments) {
                        result.append(ident).append("# ").append(comment).append(System.lineSeparator());
                    }
                }

                result.append(line).append(System.lineSeparator());
                blankLine = 0;
            }

            //添加尾部
            if (description.exists(KEY_FOOTER) && !description.getString(KEY_FOOTER).trim().isEmpty()) {
                while (blankLine < 2) {
                    result.append(System.lineSeparator());
                    blankLine++;
                }
                for (String footer : description.getString(KEY_FOOTER).trim().split("\n")) {
                    result.append("# ").append(footer).append(System.lineSeparator());
                }
            }

            cn.nukkit.utils.Utils.writeFile(file, result.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static String writeLine(int size,String line){
        StringBuilder s = new StringBuilder();
        for(int i = 0;i< size;i++){
            s.append(line);
        }
        return s.toString();
    }

    public static String getCentontString(String input,int lineWidth){
        input = input.replace(' ','$');
        return justify(input,lineWidth,'c').replace('$',' ');
    }
    /**
     * 字符串居中算法
     *
     * @param input 输入的字符串
     * @param lineWidth 一共多少行
     * @param just 对齐方法 l: 左对齐 c: 居中 r右对齐
     *
     * @return 对齐的字符串
    * */
    public static String justify(String input, int lineWidth, char just) {
        StringBuilder sb = new StringBuilder("");
        char[] inputText = input.toCharArray();
        ArrayList<String> words = new ArrayList<>();
        for (int i = 0; i < inputText.length; i++) {
            if (inputText[i] != ' ' && inputText[i] != '\n') {
                sb.append(inputText[i]);
            } else {
                inputText[i] = '\n';
                words.add(sb.toString());
                //clear content
                sb = new StringBuilder("");
            }
        }
        //add last word because the last char is not space/'\n'.
        words.add(sb.toString());
        for (String s : words) {
            if (s.length() >= lineWidth) {
                lineWidth = s.length();
            }
        }
        char[] output = null;
        StringBuilder sb2 = new StringBuilder("");
        StringBuilder line;
        for (String word : words) {
            line = new StringBuilder();
            for (int i = 0; i < lineWidth; i++) {
                line.append(" ");
            }
            switch (just) {
                case 'l':
                    line.replace(0, word.length(), word);
                    break;
                case 'r':
                    line.replace(lineWidth - word.length(), lineWidth, word);
                    break;
                case 'c':
                    //all the spaces' length
                    int rest = lineWidth - word.length();
                    int begin = 0;
                    if (rest % 2 != 0) {
                        begin = (rest / 2) + 1;
                    } else {
                        begin = rest / 2;
                    }
                    line.replace(begin, begin + word.length(), word);
                    break;
                    default:break;
            }

            line.append('\n');
            sb2.append(line);
        }
        return sb2.toString();

    }

    /**
     * 复制文件
     * */
    public static boolean copyFiles(File old,File target){

        int load = 1;
        File[] files = old.listFiles();
        if(files != null){
            for (File value : files) {
                BedWarMain.sendMessageToConsole("复制地图中 ... "+((load / (float)files.length) * 100) +"%");
                load++;
                if (value.isFile()) {
                    // 复制文件
                    try {
                        File file1 = new File(target +File.separator + value.getName());
                        if(!file1.exists()){
                            try{
                                file1.createNewFile();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                        copyByChannelToChannel(value, file1);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (value.isDirectory()) {
                    // 复制目录
                    String sourceDir = old + File.separator + value.getName();
                    String targetDir = target+ File.separator + value.getName();
                    try {
                        copyDirectiory(sourceDir, targetDir);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
        return true;
    }

    /**
     * 通过channel到channel直接传输
     * @param source 源文件
     * @param target 目标文件
     * @throws IOException 异常
     */
    public static void copyByChannelToChannel(File source,File target) throws IOException {

        RandomAccessFile sourceFile = new RandomAccessFile(source, "r");
        FileChannel sourceChannel = sourceFile.getChannel();

        if (!target.isFile()) {
            if (!target.createNewFile()) {
                sourceChannel.close();
                sourceFile.close();
                return;
            }
        }
        RandomAccessFile destFile = new RandomAccessFile(target, "rw");
        FileChannel destChannel = destFile.getChannel();
        long leftSize = sourceChannel.size();
        long position = 0;
        while (leftSize > 0) {
            long writeSize = sourceChannel.transferTo(position, leftSize, destChannel);
            position += writeSize;
            leftSize -= writeSize;
        }
        sourceChannel.close();
        sourceFile.close();
        destChannel.close();
        destFile.close();
    }


    /**复制文件夹   */
    private static void copyDirectiory(String sourceDir, String targetDir)
            throws IOException {
        // 新建目标目录
        File file = new File(targetDir);
        if(!file.exists()) {
            if (!file.mkdirs()) {
                Server.getInstance().getLogger().error("新建" + targetDir + "失败");
            }
        }
        // 获取源文件夹当前下的文件或目录
        File[] files = (new File(sourceDir)).listFiles();
        if(files != null){
            for (File value : files) {
                if (value.isFile()) {
                    // 源文件
                    // 目标文件
                    File targetFile = new
                            File(new File(targetDir).getAbsolutePath()
                            + File.separator + value.getName());
                    copyByChannelToChannel(value, targetFile);

                }
                if (value.isDirectory()) {
                    // 准备复制的源文件夹
                    String dir1 = sourceDir + File.separator + value.getName();
                    // 准备复制的目标文件夹
                    String dir2 = targetDir + File.separator + value.getName();
                    copyDirectiory(dir1, dir2);
                }
            }
        }

    }


    /**
     * 画一条进度条
     * ■■■■□□□□□□
     * @param progress 进度（百分比）
     * @param size 总长度
     * @param hasDataChar 自定义有数据图案 ■
     * @param noDataChar 自定义无数据图案 □
     * @return 画出来的线
     * */
    public static String drawLine(float progress,int size,String hasDataChar,String noDataChar){
        int l = (int) (size * progress);
        int other = size - l;
        StringBuilder ls = new StringBuilder();
        if(l > 0){
            for(int i = 0;i < l;i++){
                ls.append(hasDataChar);
            }
        }
        StringBuilder others = new StringBuilder();
        if(other > 0){
            for(int i = 0;i < other;i++){
                others.append(noDataChar);
            }
        }
        return TextFormat.colorize('&',ls +others.toString());
    }

    /**
     * 获取百分比
     * 保留两位有效数字
     * @param n 当前值
     * @param max 最大值
     * @return 计算出的百分比
     * */
    public static double getPercent(int n,int max){
        double r = 0;
        if(n > 0){
            r = (double) n / (double) max;
        }
        return Double.parseDouble(String.format("%.2f",r));
    }


    public static void launchWitherSkull(PlayerInfo playerInfo){
        Entity player = playerInfo.getPlayer();
        double f = 1.2D;
        double yaw = player.yaw;
        double pitch = player.pitch;
        Location pos = new Location(player.x - Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 1.5D, player.y + (double) player.getEyeHeight(), player.z + Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * 1.5D, yaw, pitch, player.level);
        EntityBlueWitherSkull fireBall = new EntityBlueWitherSkull(player.chunk, Entity.getDefaultNBT(pos));
        fireBall.setExplode(true);
        fireBall.setMaster(playerInfo);
        fireBall.setMotion(new Vector3(-Math.sin(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f, -Math.sin(Math.toRadians(pitch)) * f * f, Math.cos(Math.toRadians(yaw)) * Math.cos(Math.toRadians(pitch)) * f * f));
        fireBall.spawnToAll();
    }


    /**
     * 在游戏地图生成方块
     * @param player 玩家
     * @param spawn 生成的方块与位置
     * @param canRemove 是否被移除
     * */
    public static void spawnBlock(Player player, LinkedHashMap<Position, Block> spawn,boolean canRemove) {
        PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo(player);
        if(info == null){
            return;
        }
        for(Map.Entry<Position,Block> block: spawn.entrySet()){
            if(block.getKey().getLevelBlock().getId() == 0) {
                if(info.getGameRoom().worldInfo.onChangeBlock(block.getKey().getLevelBlock(),true)){
                    block.getKey().getLevel().setBlock(block.getKey(), block.getValue(), true, true);
                }
            }

        }
        if(canRemove){
            ThreadManager.SCHEDULED.schedule(() -> {
                for(Position block: new ArrayList<>(spawn.keySet())){
                    if(info.getGameRoom() == null || info.getGameRoom().getType() != GameRoom.GameType.START){
                        return;
                    }
                    if(info.getGameRoom().worldInfo.onChangeBlock(block.getLevelBlock(),false)){
                        block.getLevel().setBlock(block,new BlockAir());
                    }
                }
            },5, TimeUnit.SECONDS);
        }


//        player.getInventory().removeItem(item);
    }

    public static int formatSecond(int i) {
        return i * 20;
    }

    public static String formatTime(int s){
        int min = s / 60;
        int ss = s % 60;

        if(min > 0){
            return min+" 分 "+ss+" 秒";
        }else{
            return ss+" 秒";
        }

    }



    public static String formatTime1(int s){

        int min = s / 60;
        int ss = s % 60;
        String mi = min+"";
        String sss = ss+"";
        if(min < 10){
            mi = "0"+mi;
        }
        if(ss < 10){
            sss = "0"+ss;
        }
        if(min > 0){

            return mi+":"+sss;
        }else{
            return "00:"+sss+"";
        }

    }
}
