package org.sobadfish.bedwar.item;


import cn.nukkit.item.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Winfxk
 * @author Anders233
 *
 */
public enum ItemIDSunName {
    /**
     * 石头
     */
    STONE("石头", 1, 0, "textures/blocks/stone.png"),
    /**
     * 花岗岩
     */
    STONE_GRANITE("花岗岩", 1, 1, "textures/blocks/stone_granite.png"),
    /**
     * 磨制花岗岩
     */
    STONE_GRANITE_SMOOTH("磨制花岗岩", 1, 2, "textures/blocks/stone_granite_smooth.png"),
    /**
     * 闪长岩
     */
    STONE_DIORITE("闪长岩", 1, 3, "textures/blocks/stone_diorite.png"),
    /**
     * 磨制闪长岩
     */
    STONE_DIORITE_SMOOTH("磨制闪长岩", 1, 4, "textures/blocks/stone_diorite_smooth.png"),
    /**
     * 安山岩
     */
    STONE_ANDESITE("安山岩", 1, 5, "textures/blocks/stone_andesite.png"),
    /**
     * 磨制安山岩
     */
    STONE_ANDESITE_SMOOTH("磨制安山岩", 1, 6, "textures/blocks/stone_andesite_smooth.png"),
    /**
     * 草方块
     */
    GRASS("草方块", 2, 0, "textures/blocks/grass_side_carried.png"),
    /**
     * 泥土
     */
    DIRT("泥土", 3, 0, "textures/blocks/dirt.png"),
    /**
     * 圆石
     */
    COBBLESTONE("圆石", 4, 0, "textures/blocks/cobblestone.png"),
    /**
     * 橡树木板
     */
    PLANKS("橡树木板", 5, 0, "textures/blocks/planks_oak.png"),
    /**
     * 云杉木板
     */
    PLANKS_SPRUCE("云杉木板", 5, 1, "textures/blocks/planks_spruce.png"),
    /**
     * 桦木板
     */
    PLANKS_BIRCH("桦木板", 5, 2, "textures/blocks/planks_birch.png"),
    /**
     * 丛林树木板
     */
    PLANKS_JUNGLE("丛林树木板", 5, 3, "textures/blocks/planks_jungle.png"),
    /**
     * 金合欢木板
     */
    PLANKS_ACACIA("金合欢木板", 5, 4, "textures/blocks/planks_acacia.png"),
    /**
     * 深色橡木木板
     */
    PLANKS_BIG_OAK("深色橡木木板", 5, 5, "textures/blocks/planks_big_oak.png"),
    /**
     * 橡树苗
     */
    SAPLING("橡树苗", 6, 0, "textures/blocks/sapling_oak.png"),
    /**
     * 云杉树苗
     */
    SAPLING_SPRUCE("云杉树苗", 6, 1, "textures/blocks/sapling_spruce.png"),
    /**
     * 桦树苗
     */
    SAPLING_BIRCH("桦树苗", 6, 2, "textures/blocks/sapling_birch.png"),
    /**
     * 丛林树苗
     */
    SAPLING_JUNGLE("丛林树苗", 6, 3, "textures/blocks/sapling_jungle.png"),
    /**
     * 金合欢树苗
     */
    SAPLING_ACACIA("金合欢树苗", 6, 4, "textures/blocks/sapling_acacia.png"),
    /**
     * 深色橡树苗
     */
    SAPLING_ROOFED_OAK("深色橡树苗", 6, 5, "textures/blocks/sapling_roofed_oak.png"),
    /**
     * 基岩
     */
    BEDROCK("基岩", 7, 0, "textures/blocks/bedrock.png"),
    /**
     * 流动的水
     */
    FLOWING_WATER("流动的水", 8, 0, "textures/blocks/water_placeholder.png"),
    /**
     * 水
     */
    WATER("水", 9, 0, "textures/blocks/water_placeholder.png"),
    /**
     * 流动的岩浆
     */
    FLOWING_LAVA("流动的岩浆", 10, 0, "textures/blocks/lava_placeholder.png"),
    /**
     * 岩浆
     */
    LAVA("岩浆", 11, 0, "textures/blocks/lava_placeholder.png"),
    /**
     * 沙子
     */
    SAND("沙子", 12, 0, "textures/blocks/sand.png"),
    /**
     * 红沙
     */
    RED_SAND("红沙", 12, 1, "textures/blocks/red_sand.png"),
    /**
     * 砾石
     */
    GRAVEL("砾石", 13, 0, "textures/blocks/gravel.png"),
    /**
     * 金矿石
     */
    GOLD_ORE("金矿石", 14, 0, "textures/blocks/gold_ore.png"),
    /**
     * 铁矿石
     */
    IRON_ORE("铁矿石", 15, 0, "textures/blocks/iron_ore.png"),
    /**
     * 煤矿石
     */
    COAL_ORE("煤矿石", 16, 0, "textures/blocks/coal_ore.png"),
    /**
     * 橡木
     */
    LOG("橡木", 17, 0, "textures/blocks/log_oak.png"),
    /**
     * 云杉木
     */
    LOG_SPRUCE("云杉木", 17, 1, "textures/blocks/log_spruce.png"),
    /**
     * 桦木
     */
    LOG_BIRCH("桦木", 17, 2, "textures/blocks/log_birch.png"),
    /**
     * 丛林木
     */
    LOG_JUNGLE("丛林木", 17, 3, "textures/blocks/log_jungle.png"),
    /**
     * 橡树叶
     */
    LEAVES("橡树叶", 18, 0, "textures/blocks/leaves_oak_carried.tga"),
    /**
     * 云杉叶
     */
    LEAVES_SPRUCE_CARRIED("云杉叶", 18, 1, "textures/blocks/leaves_spruce_carried.tga"),
    /**
     * 桦树叶
     */
    LEAVES_BIRCH_CARRIED("桦树叶", 18, 2, "textures/blocks/leaves_birch_carried.tga"),
    /**
     * 丛林树叶
     */
    LEAVES_JUNGLE_CARRIED("丛林树叶", 18, 3, "textures/blocks/leaves_jungle_carried.tga"),
    /**
     * 干海绵
     */
    SPONGE("干海绵", 19, 0, "textures/blocks/sponge.png"),
    /**
     * 湿海绵
     */
    SPONGE_WET("湿海绵", 19, 1, "textures/blocks/sponge_wet.png"),
    /**
     * 玻璃
     */
    GLASS("玻璃", 20, 0, "textures/blocks/glass.png"),
    /**
     * 青金石矿
     */
    LAPIS_ORE("青金石矿", 21, 0, "textures/blocks/lapis_ore.png"),
    /**
     * 青金石块
     */
    LAPIS_BLOCK("青金石块", 22, 0, "textures/blocks/lapis_block.png"),
    /**
     * 发射器
     */
    DISPENSER("发射器", 23, 0, "textures/blocks/dispenser_front_horizontal.png"),
    /**
     * 沙石
     */
    SANDSTONE("沙石", 24, 0, "textures/blocks/sandstone_normal.png"),
    /**
     * 錾制沙石
     */
    SANDSTONE_CARVED("錾制沙石", 24, 1, "textures/blocks/sandstone_carved.png"),
    /**
     * 光滑沙石
     */
    SANDSTONE_SMOOTH("光滑沙石", 24, 2, "textures/blocks/sandstone_smooth.png"),
    /**
     * 音符盒
     */
    NOTEBLOCK("音符盒", 25, 0, "textures/blocks/noteblock.png"),
    /**
     * 方块床
     */
    BED_BLOCK("方块床", 26, 0, "textures/blocks/bed_head_top.png"),
    /**
     * 动力铁轨
     */
    GOLDEN_RAIL("动力铁轨", 27, 0, "textures/blocks/rail_golden.png"),
    /**
     * 探测铁轨
     */
    DETECTOR_RAIL("探测铁轨", 28, 0, "textures/blocks/rail_detector.png"),
    /**
     * 粘性活塞
     */
    STICKY_PISTON("粘性活塞", 29, 0, "textures/blocks/piston_top_sticky.png"),
    /**
     * 蜘蛛网
     */
    WEB("蜘蛛网", 30, 0, "textures/blocks/web.png"),
    /**
     * 高草
     */
    TALLGRASS("高草", 31, 0, "textures/blocks/deadbush.png"),
    /**
     * 草
     */
    TALLGRASS_CARRIED("草", 31, 1, "textures/blocks/tallgrass_carried.tga"),
    /**
     * 蕨
     */
    FERN_CARRIED("蕨", 31, 2, "textures/blocks/fern_carried.tga"),
    /**
     * 枯死的灌木
     */
    DEADBUSH("枯死的灌木", 32, 0, "textures/blocks/deadbush.png"),
    /**
     * 活塞
     */
    PISTON("活塞", 33, 0, "textures/blocks/piston_top_normal.png"),
    /**
     * 活塞臂
     */
    PISTON_HEAD("活塞臂", 34, 0, "textures/blocks/piston_top_normal.png"),
    /**
     * 白色羊毛
     */
    WOOL("白色羊毛", 35, 0, "textures/blocks/wool_colored_white.png"),
    /**
     * 橙色羊毛
     */
    WOOL_COLORED_ORANGE("橙色羊毛", 35, 1, "textures/blocks/wool_colored_orange.png"),
    /**
     * 品红色羊毛
     */
    WOOL_COLORED_MAGENTA("品红色羊毛", 35, 2, "textures/blocks/wool_colored_magenta.png"),
    /**
     * 淡蓝色羊毛
     */
    WOOL_COLORED_LIGHT_BLUE("淡蓝色羊毛", 35, 3, "textures/blocks/wool_colored_light_blue.png"),
    /**
     * 黄色羊毛
     */
    WOOL_COLORED_YELLOW("黄色羊毛", 35, 4, "textures/blocks/wool_colored_yellow.png"),
    /**
     * 黄绿色羊毛
     */
    WOOL_COLORED_LIME("黄绿色羊毛", 35, 5, "textures/blocks/wool_colored_lime.png"),
    /**
     * 粉红色羊毛
     */
    WOOL_COLORED_PINK("粉红色羊毛", 35, 6, "textures/blocks/wool_colored_pink.png"),
    /**
     * 灰色羊毛
     */
    WOOL_COLORED_GRAY("灰色羊毛", 35, 7, "textures/blocks/wool_colored_gray.png"),
    /**
     * 淡灰色羊毛
     */
    WOOL_COLORED_SILVER("淡灰色羊毛", 35, 8, "textures/blocks/wool_colored_silver.png"),
    /**
     * 青色羊毛
     */
    WOOL_COLORED_CYAN("青色羊毛", 35, 9, "textures/blocks/wool_colored_cyan.png"),
    /**
     * 紫色羊毛
     */
    WOOL_COLORED_PURPLE("紫色羊毛", 35, 10, "textures/blocks/wool_colored_purple.png"),
    /**
     * 蓝色羊毛
     */
    WOOL_COLORED_BLUE("蓝色羊毛", 35, 11, "textures/blocks/wool_colored_blue.png"),
    /**
     * 棕色羊毛
     */
    WOOL_COLORED_BROWN("棕色羊毛", 35, 12, "textures/blocks/wool_colored_brown.png"),
    /**
     * 绿色羊毛
     */
    WOOL_COLORED_GREEN("绿色羊毛", 35, 13, "textures/blocks/wool_colored_green.png"),
    /**
     * 红色羊毛
     */
    WOOL_COLORED_RED("红色羊毛", 35, 14, "textures/blocks/wool_colored_red.png"),
    /**
     * 黑色羊毛
     */
    WOOL_COLORED_BLACK("黑色羊毛", 35, 15, "textures/blocks/wool_colored_black.png"),
    /**
     * 黄花
     */
    YELLOW_FLOWER("黄花", 37, 0, "textures/blocks/glazed_terracotta_yellow.png"),
    /**
     * 罂粟
     */
    RED_FLOWER("罂粟", 38, 0, "textures/blocks/flower_rose.png"),
    /**
     * 蓝色的兰花
     */
    FLOWER_BLUE_ORCHID("蓝色的兰花", 38, 1, "textures/blocks/flower_blue_orchid.png"),
    /**
     * 绒球葱
     */
    FLOWER_ALLIUM("绒球葱", 38, 2, "textures/blocks/flower_allium.png"),
    /**
     * 茜草花
     */
    FLOWER_HOUSTONIA("茜草花", 38, 3, "textures/blocks/flower_houstonia.png"),
    /**
     * 红色郁金香
     */
    FLOWER_TULIP_RED("红色郁金香", 38, 4, "textures/blocks/flower_tulip_red.png"),
    /**
     * 橙色郁金香
     */
    FLOWER_TULIP_ORANGE("橙色郁金香", 38, 5, "textures/blocks/flower_tulip_orange.png"),
    /**
     * 白色郁金香
     */
    FLOWER_TULIP_WHITE("白色郁金香", 38, 6, "textures/blocks/flower_tulip_white.png"),
    /**
     * 粉色郁金香
     */
    FLOWER_TULIP_PINK("粉色郁金香", 38, 7, "textures/blocks/flower_tulip_pink.png"),
    /**
     * 滨菊
     */
    FLOWER_OXEYE_DAISY("滨菊", 38, 8, "textures/blocks/flower_oxeye_daisy.png"),
    /**
     * 棕色蘑菇
     */
    BROWN_MUSHROOM("棕色蘑菇", 39, 0, "textures/blocks/mushroom_brown.png"),
    /**
     * 红色蘑菇
     */
    RED_MUSHROOM("红色蘑菇", 40, 0, "textures/blocks/mushroom_red.png"),
    /**
     * 黄金块
     */
    GOLD_BLOCK("黄金块", 41, 0, "textures/blocks/gold_block.png"),
    /**
     * 铁块
     */
    IRON_BLOCK("铁块", 42, 0, "textures/blocks/iron_block.png"),
    /**
     * 双石台阶
     */
    DOUBLE_STONE_SLAB("双石台阶", 43, 0, "textures/blocks/stone_slab_side.png"),
    /**
     * 双沙石台阶
     */
    SANDSTONE_BOTTOM("双沙石台阶", 43, 1, "textures/blocks/sandstone_bottom.png"),
    /**
     * 双橡木台阶
     */
    PLANKS_OAK("双橡木台阶", 43, 2, "textures/blocks/planks_oak.png"),
    /**
     * 双圆石台阶
     */
    DOUBLE_PEBBLE_STEPS("双圆石台阶", 43, 3, "textures/blocks/cobblestone.png"),
    /**
     * 双砖台阶
     */
    DOUBLE_BRICK_STEPS("双砖台阶", 43, 4, "textures/blocks/brick.png"),
    /**
     * 双石砖台阶
     */
    DOUBLE_STONE_BRICK_STEPS("双石砖台阶", 43, 5, "textures/blocks/stonebrick.png"),
    /**
     * 双石英台阶
     */
    DOUBLE_QUARTZ_STEPS("双石英台阶", 43, 6, "textures/blocks/nether_brick.png"),
    /**
     * 双地狱砖台阶
     */
    DOUBLE_HELL_BRICK_STEPS("双地狱砖台阶", 43, 7, "textures/blocks/quartz_block_top.png"),
    /**
     * 石台阶
     */
    STONE_SLAB("石台阶", 44, 0, "textures/blocks/stone_slab_top.png"),
    /**
     * 沙石台阶
     */
    SANDSTONE_TOP("沙石台阶", 44, 1, "textures/blocks/sandstone_top.png"),
    /**
     * 圆石台阶
     */
    COBBLESTONE_STEPS("圆石台阶", 44, 3, "textures/blocks/cobblestone.png"),
    /**
     * 砖台阶
     */
    BRICK_STEPS("砖台阶", 44, 4, "textures/blocks/brick.png"),
    /**
     * 石砖台阶
     */
    STONEBRICK_STEPS("石砖台阶", 44, 5, "textures/blocks/stonebrick.png"),
    /**
     * 石英台阶
     */
    NETHER_BRICK_STEPS("石英台阶", 44, 6, "textures/blocks/nether_brick.png"),
    /**
     * 地狱砖台阶
     */
    QUARTZ_BLOCK_TOP_STEPS("地狱砖台阶", 44, 7, "textures/blocks/quartz_block_top.png"),
    /**
     * 砖
     */
    BRICK("砖", 336, 0, "textures/blocks/brick.png"),
    /**
     * TNT
     */
    TNT("TNT", 46, 0, "textures/blocks/tnt_side.png"),
    /**
     * 书架
     */
    BOOKSHELF("书架", 47, 0, "textures/blocks/bookshelf.png"),
    /**
     * 苔石
     */
    MOSSY_COBBLESTONE("苔石", 48, 0, "textures/blocks/cobblestone_mossy.png"),
    /**
     * 黑曜石
     */
    OBSIDIAN("黑曜石", 49, 0, "textures/blocks/obsidian.png"),
    /**
     * 火把
     */
    TORCH("火把", 50, 0, "textures/blocks/torch_on.png"),
    /**
     * 火
     */
    FIRE("火", 51, 0, "textures/blocks/fire_0_placeholder.png"),
    /**
     * 刷怪笼
     */
    MOB_SPAWNER("刷怪笼", 52, 0, "textures/blocks/mob_spawner.png"),
    /**
     * 橡木阶梯
     */
    OAK_STAIRS("橡木阶梯", 53, 0, "textures/blocks/planks_oak.png"),
    /**
     * 箱子
     */
    CHEST("箱子", 54, 0, "textures/blocks/chest_front.png"),
    /**
     * 红石粉
     */
    REDSTONE_WIRE("红石粉", 55, 0, "textures/blocks/redstone_dust_line.png"),
    /**
     * 钻石矿
     */
    DIAMOND_ORE("钻石矿", 56, 0, "textures/blocks/diamond_ore.png"),
    /**
     * 钻石块
     */
    DIAMOND_BLOCK("钻石块", 57, 0, "textures/blocks/diamond_block.png"),
    /**
     * 工作台
     */
    CRAFTING_TABLE("工作台", 58, 0, "textures/blocks/crafting_table_top.png"),
    /**
     * 农田
     */
    FARMLAND("农田", 60, 0, "textures/blocks/farmland_dry.png"),
    /**
     * 熔炉
     */
    FURNACE("熔炉", 61, 0, "textures/blocks/furnace_front_off.png"),
    /**
     * 梯子
     */
    LADDER("梯子", 65, 0, "textures/blocks/ladder.png"),
    /**
     * 铁轨
     */
    RAIL("铁轨", 66, 0, "textures/blocks/rail_normal.png"),
    /**
     * 圆石阶梯
     */
    STONE_STAIRS("圆石阶梯", 67, 0, "textures/blocks/cobblestone.png"),
    /**
     * 拉杆
     */
    LEVER("拉杆", 69, 0, "textures/blocks/lever.png"),
    /**
     * 石质压力板
     */
    STONE_PRESSURE_PLATE("石质压力板", 70, 0, "textures/blocks/stone.png"),
    /**
     * 木质压力板
     */
    WOODEN_PRESSURE_PLATE("木质压力板", 72, 0, "textures/blocks/planks_oak.png"),
    /**
     * 红石矿
     */
    REDSTONE_ORE("红石矿", 73, 0, "textures/blocks/redstone_ore.png"),
    /**
     * 发光的红石矿
     */
    LIT_REDSTONE_ORE("发光的红石矿", 74, 0, "textures/blocks/redstone_ore.png"),
    /**
     * 红石火把
     */
    UNLIT_REDSTONE_TORCH("红石火把", 75, 0, "textures/blocks/redstone_torch_off.png"),
    /**
     * 石质按钮
     */
    STONE_BUTTON("石质按钮", 77, 0, "textures/blocks/stone.png"),
    /**
     * 顶层雪
     */
    SNOW_LAYER("顶层雪", 78, 0, "textures/blocks/snow.png"),
    /**
     * 冰
     */
    ICE("冰", 79, 0, "textures/blocks/ice.png"),
    /**
     * 雪
     */
    SNOW("雪", 80, 0, "textures/blocks/snow.png"),
    /**
     * 仙人掌
     */
    CACTUS("仙人掌", 81, 0, "textures/blocks/cactus_side.tga"),
    /**
     * 粘土
     */
    CLAY("粘土", 82, 0, "textures/blocks/clay.png"),
    /**
     * 音乐盒
     */
    JUKEBOX("音乐盒", 84, 0, "textures/blocks/jukebox_top.png"),
    /**
     * 橡木围墙
     */
    FENCE("橡木围墙", 85, 0, "textures/blocks/planks_oak.png"),
    /**
     * 南瓜
     */
    PUMPKIN("南瓜", 86, 0, "textures/blocks/pumpkin_face_off.png"),
    /**
     * 地狱岩
     */
    NETHERRACK("地狱岩", 87, 0, "textures/blocks/netherrack.png"),
    /**
     * 灵魂沙
     */
    SOUL_SAND("灵魂沙", 88, 0, "textures/blocks/soul_sand.png"),
    /**
     * 萤石
     */
    GLOWSTONE("萤石", 89, 0, "textures/blocks/glowstone.png"),
    /**
     * 传送门
     */
    PORTAL("传送门", 90, 0, "textures/blocks/portal_placeholder.png"),
    /**
     * 南瓜灯
     */
    LIT_PUMPKIN("南瓜灯", 91, 0, "textures/blocks/pumpkin_face_on.png"),
    /**
     * 隐形基岩
     */
    STAINED_GLASS("隐形基岩", 95, 0, "textures/blocks/glass_white.png"),
    /**
     * 木质陷阱门
     */
    TRAPDOOR("木质陷阱门", 96, 0, "textures/blocks/trapdoor.png"),
    /**
     * 石头刷怪蛋
     */
    MONSTER_EGG("石头刷怪蛋", 97, 0, "textures/blocks/stone.png"),
    /**
     * 圆石刷怪蛋
     */
    COBBLESTONE_EGG("圆石刷怪蛋", 97, 1, "textures/blocks/Cobblestone.png"),
    /**
     * 石砖刷怪蛋
     */
    STONEBRICK("石砖刷怪蛋", 97, 2, "textures/blocks/stonebrick.png"),
    /**
     * 苔石砖
     */
    STONEBRICK_MOSSY("苔石砖", 98, 1, "textures/blocks/stonebrick_mossy.png"),
    /**
     * 裂石砖
     */
    STONEBRICK_CRACKED("裂石砖", 98, 2, "textures/blocks/stonebrick_cracked.png"),
    /**
     * 錾制石砖
     */
    STONEBRICK_CARVED("錾制石砖", 98, 3, "textures/blocks/stonebrick_carved.png"),
    /**
     * 棕色蘑菇块
     */
    BROWN_MUSHROOM_BLOCK("棕色蘑菇块", 99, 0, "textures/blocks/mushroom_block_skin_brown.png"),
    /**
     * 红色蘑菇块
     */
    RED_MUSHROOM_BLOCK("红色蘑菇块", 100, 0, "textures/blocks/mushroom_block_skin_red.png"),
    /**
     * 铁栏杆
     */
    IRON_BARS("铁栏杆", 101, 0, "textures/blocks/iron_bars.png"),
    /**
     * 玻璃板
     */
    GLASS_PANE("玻璃板", 102, 0, "textures/blocks/glass_pane_top.png"),
    /**
     * 南瓜梗
     */
    PUMPKIN_STEM("南瓜梗", 104, 0, "textures/blocks/pumpkin_stem_disconnected.png"),
    /**
     * 藤蔓
     */
    VINE("藤蔓", 106, 0, "textures/blocks/vine_carried.png"),
    /**
     * 橡木围墙大门
     */
    FENCE_GATE("橡木围墙大门", 107, 0, "textures/blocks/planks_oak.png"),
    /**
     * 砖块阶梯
     */
    BRICK_STAIRS("砖块阶梯", 108, 0, "textures/blocks/brick.png"),
    /**
     * 石砖阶梯
     */
    STONE_BRICK_STAIRS("石砖阶梯", 109, 0, "textures/blocks/stonebrick.png"),
    /**
     * 菌丝
     */
    MYCELIUM("菌丝", 110, 0, "textures/blocks/mycelium_side.png"),
    /**
     * 睡莲
     */
    WATERLILY("睡莲", 111, 0, "textures/blocks/carried_waterlily.png"),
    /**
     * 地狱砖
     */
    NETHERBRICK("地狱砖", 405, 0, "textures/blocks/nether_brick.png"),
    /**
     * 地狱砖围墙
     */
    NETHER_BRICK_FENCE("地狱砖围墙", 113, 0, "textures/blocks/nether_brick.png"),
    /**
     * 地狱砖阶梯
     */
    NETHER_BRICK_STAIRS("地狱砖阶梯", 114, 0, "textures/blocks/nether_brick.png"),
    /**
     * 附魔台
     */
    ENCHANTING_TABLE("附魔台", 116, 0, "textures/blocks/enchanting_table_side.png"),
    /**
     * 酿造台
     */
    BREWING_STAND("酿造台", 379, 0, "textures/blocks/brewing_stand.png"),
    /**
     * 炼药锅
     */
    CAULDRON("炼药锅", 380, 0, "textures/blocks/cauldron_side.png"),
    /**
     * 末地门
     */
    END_PORTAL("末地门", 119, 0, "textures/blocks/end_portal.png"),
    /**
     * 末地传送门
     */
    END_PORTAL_FRAME("末地传送门", 120, 0, "textures/blocks/end_portal.png"),
    /**
     * 末地石
     */
    END_STONE("末地石", 121, 0, "textures/blocks/end_stone.png"),
    /**
     * 龙蛋
     */
    DRAGON_EGG("龙蛋", 122, 0, "textures/blocks/dragon_egg.png"),
    /**
     * 红石灯
     */
    REDSTONE_LAMP("红石灯", 123, 0, "textures/blocks/redstone_lamp_off.png"),
    /**
     * 沙石阶梯
     */
    SANDSTONE_STAIRS("沙石阶梯", 128, 0, "textures/blocks/sandstone_bottom.png"),
    /**
     * 绿宝石矿石
     */
    EMERALD_ORE("绿宝石矿石", 129, 0, "textures/blocks/emerald_ore.png"),
    /**
     * 末影箱
     */
    ENDER_CHEST("末影箱", 130, 0, "textures/blocks/ender_chest_front.png"),
    /**
     * 拌线钩
     */
    TRIPWIRE_HOOK("拌线钩", 131, 0, "textures/blocks/trip_wire_source.png"),
    /**
     * 拌线
     */
    TRIPWIRE("拌线", 132, 0, "textures/blocks/trip_wire.png"),
    /**
     * 绿宝石块
     */
    EMERALD_BLOCK("绿宝石块", 133, 0, "textures/blocks/emerald_block.png"),
    /**
     * 云杉木阶梯
     */
    SPRUCE_STAIRS("云杉木阶梯", 134, 0, "textures/blocks/planks_spruce.png"),
    /**
     * 桦木阶梯
     */
    BIRCH_STAIRS("桦木阶梯", 135, 0, "textures/blocks/planks_birch.png"),
    /**
     * 丛林木阶梯
     */
    JUNGLE_STAIRS("丛林木阶梯", 136, 0, "textures/blocks/planks_jungle.png"),
    /**
     * 命令方块
     */
    COMMAND_BLOCK("命令方块", 137, 0, "textures/blocks/command_block.png"),
    /**
     * 信标
     */
    BEACON("信标", 138, 0, "textures/blocks/beacon.png"),
    /**
     * 圆石墙
     */
    COBBLESTONE_WALL("圆石墙", 139, 0, "textures/blocks/cobblestone.png"),
    /**
     * 苔石墙
     */
    MOSS_COBBLESTONE_WALL("苔石墙", 139, 1, "textures/blocks/cobblestone_mossy.png"),
    /**
     * 土豆
     */
    POTATOES("土豆", 142, 0, "textures/blocks/potatoes_stage_3.png"),
    /**
     * 木质按钮
     */
    WOODEN_BUTTON("木质按钮", 143, 0, "textures/blocks/planks_oak.png"),
    /**
     * 铁砧
     */
    ANVIL("铁砧", 145, 0, "textures/blocks/anvil_top_damaged_0.png"),
    /**
     * 陷阱箱
     */
    TRAPPED_CHEST("陷阱箱", 146, 0, "textures/blocks/trapped_chest_front.png"),
    /**
     * 重力压力板(轻型)
     */
    LIGHT_WEIGHTED_PRESSURE_PLATE("重力压力板(轻型)", 147, 0, "textures/blocks/gold_block.png"),
    /**
     * 重力压力板(重型)
     */
    HEAVY_WEIGHTED_PRESSURE_PLATE("重力压力板(重型)", 148, 0, "textures/blocks/iron_block.png"),
    /**
     * 阳光传感器
     */
    DAYLIGHT_DETECTOR_INVERTED("阳光传感器", 178, 0, "textures/blocks/daylight_detector_inverted_top.png"),
    /**
     * 红石块
     */
    REDSTONE_BLOCK("红石块", 152, 0, "textures/blocks/redstone_block.png"),
    /**
     * 地狱石英矿石
     */
    QUARTZ_ORE("地狱石英矿石", 153, 0, "textures/blocks/quartz_ore.png"),
    /**
     * 漏斗
     */
    HOPPER("漏斗", 154, 0, "textures/blocks/hopper_top.png"),
    /**
     * 石英块
     */
    QUARTZ_BLOCK("石英块", 155, 0, "textures/blocks/quartz_block_top.png"),
    /**
     * 竖纹石英块
     */
    VERTICAL_GRAIN_QUARTZ_BLOCK("竖纹石英块", 155, 1, "textures/blocks/quartz_block_lines.png"),
    /**
     * 錾制石英块
     */
    QUARTZ_BLOCK_CHISELED("錾制石英块", 155, 2, "textures/blocks/quartz_block_chiseled_top.png"),
    /**
     * 石英阶梯
     */
    QUARTZ_STAIRS("石英阶梯", 156, 0, "textures/blocks/quartz_block_top.png"),
    /**
     * 橡木台阶
     */
    OAK_WOOD_STAIRS("橡木台阶", 158, 0, "textures/blocks/planks_oak.png"),
    /**
     * 白色粘土
     */
    WHITE_STAINED_HARDENED_CLAY("白色粘土", 159, 0, "textures/blocks/hardened_clay_stained_white.png"),
    /**
     * 橙色粘土
     */
    ORANGE_STAINED_HARDENED_CLAY("橙色粘土", 159, 1, "textures/blocks/hardened_clay_stained_orange.png"),
    /**
     * 品红色粘土
     */
    SOLFERINO_STAINED_HARDENED_CLAY("品红色粘土", 159, 2, "textures/blocks/hardened_clay_stained_magenta.png"),
    /**
     * 淡蓝色粘土
     */
    LIGHT_BLUE_STAINED_HARDENED_CLAY("淡蓝色粘土", 159, 3, "textures/blocks/hardened_clay_stained_light_blue.png"),
    /**
     * 黄色粘土
     */
    YELLOW_STAINED_HARDENED_CLAY("黄色粘土", 159, 4, "textures/blocks/hardened_clay_stained_yellow.png"),
    /**
     * 黄绿色粘土
     */
    OLIVINE_STAINED_HARDENED_CLAY("黄绿色粘土", 159, 5, "textures/blocks/hardened_clay_stained_lime.png"),
    /**
     * 粉红色粘土
     */
    PINK_STAINED_HARDENED_CLAY("粉红色粘土", 159, 6, "textures/blocks/hardened_clay_stained_pink.png"),
    /**
     * 灰色粘土
     */
    GRAY_STAINED_HARDENED_CLAY("灰色粘土", 159, 7, "textures/blocks/hardened_clay_stained_gray.png"),
    /**
     * 淡灰色粘土
     */
    LIGHT_GRAY_STAINED_HARDENED_CLAY("淡灰色粘土", 159, 8, "textures/blocks/concrete_gray.png"),
    /**
     * 青色粘土
     */
    CYAN_STAINED_HARDENED_CLAY("青色粘土", 159, 9, "textures/blocks/hardened_clay_stained_lime.png"),
    /**
     * 紫色粘土
     */
    VIOLET_STAINED_HARDENED_CLAY("紫色粘土", 159, 10, "textures/blocks/hardened_clay_stained_purple.png"),
    /**
     * 蓝色粘土
     */
    BLUE_STAINED_HARDENED_CLAY("蓝色粘土", 159, 11, "textures/blocks/hardened_clay_stained_blue.png"),
    /**
     * 棕色粘土
     */
    BROWN_STAINED_HARDENED_CLAY("棕色粘土", 159, 12, "textures/blocks/hardened_clay_stained_brown.png"),
    /**
     * 绿色粘土
     */
    GREEN_STAINED_HARDENED_CLAY("绿色粘土", 159, 13, "textures/blocks/hardened_clay_stained_green.png"),
    /**
     * 红色粘土
     */
    RED_STAINED_HARDENED_CLAY("红色粘土", 159, 14, "textures/blocks/hardened_clay_stained_red.png"),
    /**
     * 黑色粘土
     */
    BLACK_STAINED_HARDENED_CLAY("黑色粘土", 159, 15, "textures/blocks/hardened_clay_stained_black.png"),
    /**
     * 白色玻璃板
     */
    WHITE_STAINED_GLASS_PANE("白色玻璃板", 160, 0, "textures/blocks/glass_pane_top_white.png"),
    /**
     * 橙色玻璃板
     */
    ORANGE_STAINED_GLASS_PANE("橙色玻璃板", 160, 1, "textures/blocks/glass_pane_top_orange.png"),
    /**
     * 品红色玻璃板
     */
    SOLFERINO_STAINED_GLASS_PANE("品红色玻璃板", 160, 2, "textures/blocks/glass_pane_top_magenta.png"),
    /**
     * 淡蓝色玻璃板
     */
    LIGHT_BLUE_STAINED_GLASS_PANE("淡蓝色玻璃板", 160, 3, "textures/blocks/glass_light_blue.png"),
    /**
     * 黄色玻璃板
     */
    YELLOW_STAINED_GLASS_PANE("黄色玻璃板", 160, 4, "textures/blocks/glass_yellow.png"),
    /**
     * 黄绿色玻璃板
     */
    OLIVINE_STAINED_GLASS_PANE("黄绿色玻璃板", 160, 5, "textures/blocks/glass_pane_top_lime.png"),
    /**
     * 粉红色玻璃板
     */
    PINK_STAINED_GLASS_PANE("粉红色玻璃板", 160, 6, "textures/blocks/glass_pane_top_pink.png"),
    /**
     * 灰色玻璃板
     */
    GRAY_STAINED_GLASS_PANE("灰色玻璃板", 160, 7, "textures/blocks/glass_gray.png"),
    /**
     * 淡灰色玻璃板
     */
    LIGHT_GRAY_STAINED_GLASS_PANE("淡灰色玻璃板", 160, 8, "textures/blocks/glass_brown.png"),
    /**
     * 青色玻璃板
     */
    CYAN_STAINED_GLASS_PANE("青色玻璃板", 160, 9, "textures/blocks/glass_pane_top_cyan.png"),
    /**
     * 紫色玻璃板
     */
    VIOLET_STAINED_GLASS_PANE("紫色玻璃板", 160, 10, "textures/blocks/glass_pane_top_purple.png"),
    /**
     * 蓝色玻璃板
     */
    BLUE_STAINED_GLASS_PANE("蓝色玻璃板", 160, 11, "textures/blocks/glass_blue.png"),
    /**
     * 棕色玻璃板
     */
    BROWN_STAINED_GLASS_PANE("棕色玻璃板", 160, 12, "textures/blocks/glass_brown.png"),
    /**
     * 绿色玻璃板
     */
    GREEN_STAINED_GLASS_PANE("绿色玻璃板", 160, 13, "textures/blocks/glass_pane_top_green.png"),
    /**
     * 红色玻璃板
     */
    RED_STAINED_GLASS_PANE("红色玻璃板", 160, 14, "textures/blocks/glass_red.png"),
    /**
     * 黑色玻璃板
     */
    BLACK_STAINED_GLASS_PANE("黑色玻璃板", 160, 15, "textures/blocks/glass_black.png"),
    /**
     * 金合欢叶
     */
    ACACIA_LEAVES("金合欢叶", 161, 0, "textures/blocks/leaves_acacia_opaque.png"),
    /**
     * 深色橡树叶
     */
    DARK_OAK_LEAF("深色橡树叶", 161, 1, "textures/blocks/leaves_big_oak_opaque.png"),
    /**
     * 金合欢木
     */
    ACACIA_WOOD("金合欢木", 162, 0, "textures/blocks/log_acacia.png"),
    /**
     * 深色橡木
     */
    DARK_OAK("深色橡木", 162, 1, "textures/blocks/log_acacia.png"),
    /**
     * 金合欢木阶梯
     */
    ACACIA_STAIRS("金合欢木阶梯", 163, 0, "textures/blocks/planks_acacia.png"),
    /**
     * 深色橡木阶梯
     */
    DARK_OAK_STAIRS("深色橡木阶梯", 164, 0, "textures/blocks/planks_big_oak.png"),
    /**
     * 粘液块
     */
    SLIME("粘液块", 165, 0, "textures/blocks/slime.png"),
    /**
     * 铁门
     */
    IRON_DOOR("铁门", 330, 0, "textures/blocks/door_iron_upper.png"),
    /**
     * 海晶石
     */
    PRISMARINE("海晶石", 168, 0, "textures/blocks/prismarine_dark.png"),
    /**
     * 暗海晶石
     */
    DARK_PRISMARINE("暗海晶石", 168, 1, "textures/blocks/prismarine_dark.png"),
    /**
     * 海晶石砖
     */
    PRISMARINE_STONE_BRICK("海晶石砖", 168, 2, "textures/blocks/prismarine_bricks.png"),
    /**
     * 海晶灯
     */
    SEA_LANTERN("海晶灯", 169, 0, "textures/blocks/sea_lantern.png"),
    /**
     * 干草捆
     */
    HAY_BLOCK("干草捆", 170, 0, "textures/blocks/hay_block_side.png"),
    /**
     * 白色地毯
     */
    WHITE_CARPET("白色地毯", 171, 0, "textures/blocks/wool_colored_white.png"),
    /**
     * 橙色地毯
     */
    ORANGE_CARPET("橙色地毯", 171, 1, "textures/blocks/wool_colored_orange.png"),
    /**
     * 品红色地毯
     */
    SOLFERINO_CARPET("品红色地毯", 171, 2, "textures/blocks/wool_colored_magenta.png"),
    /**
     * 淡蓝色地毯
     */
    LIGHT_BLUE_CARPET("淡蓝色地毯", 171, 3, "textures/blocks/wool_colored_light_blue.png"),
    /**
     * 黄色地毯
     */
    YELLOW_CARPET("黄色地毯", 171, 4, "textures/blocks/wool_colored_yellow.png"),
    /**
     * 黄绿色地毯
     */
    OLIVINE_CARPET("黄绿色地毯", 171, 5, "textures/blocks/wool_colored_lime.png"),
    /**
     * 粉红色地毯
     */
    PINK_CARPET("粉红色地毯", 171, 6, "textures/blocks/wool_colored_pink.png"),
    /**
     * 灰色地毯
     */
    GRAY_CARPET("灰色地毯", 171, 7, "textures/blocks/wool_colored_gray.png"),
    /**
     * 淡灰色地毯
     */
    LIGHT_GRAY_CARPET("淡灰色地毯", 171, 8, "textures/blocks/wool_colored_silver.png"),
    /**
     * 青色地毯
     */
    CYAN_CARPET("青色地毯", 171, 9, "textures/blocks/wool_colored_cyan.png"),
    /**
     * 紫色地毯
     */
    VIOLET_CARPET("紫色地毯", 171, 10, "textures/blocks/wool_colored_purple.png"),
    /**
     * 蓝色地毯
     */
    BLUE_CARPET("蓝色地毯", 171, 11, "textures/blocks/wool_colored_blue.png"),
    /**
     * 棕色地毯
     */
    BROWN_CARPET("棕色地毯", 171, 12, "textures/blocks/wool_colored_brown.png"),
    /**
     * 绿色地毯
     */
    GREEN_CARPET("绿色地毯", 171, 13, "textures/blocks/wool_colored_green.png"),
    /**
     * 红色地毯
     */
    RED_CARPET("红色地毯", 171, 14, "textures/blocks/wool_colored_red.png"),
    /**
     * 黑色地毯
     */
    BLACK_CARPET("黑色地毯", 171, 15, "textures/blocks/wool_colored_black.png"),
    /**
     * 硬化粘土
     */
    HARDENED_CLAY("硬化粘土", 172, 0, "textures/blocks/hardened_clay.png"),
    /**
     * 煤炭块
     */
    COAL_BLOCK("煤炭块", 173, 0, "textures/blocks/coal_block.png"),
    /**
     * 浮冰
     */
    PACKED_ICE("浮冰", 174, 0, "textures/blocks/ice_packed.png"),
    /**
     * 向日葵
     */
    SUNFLOWER("向日葵", 175, 0, "textures/blocks/double_plant_sunflower_front.png"),
    /**
     * 丁香
     */
    LILAC("丁香", 175, 1, "textures/blocks/flower_cornflower.png"),
    /**
     * 高草丛
     */
    TALL_GRASS("高草丛", 175, 2, "textures/blocks/double_plant_grass_carried.png"),
    /**
     * 大型蕨
     */
    LARGE_FERN("大型蕨", 175, 3, "textures/blocks/tallgrass.png"),
    /**
     * 玫瑰丛
     */
    ROSE_BUSH("玫瑰丛", 175, 4, "textures/blocks/sweet_berry_bush_stage3.png"),
    /**
     * 牡丹
     */
    PEONY("牡丹", 175, 5, "textures/blocks/flower_allium.png"),
    /**
     * 旗帜
     */
    STANDING_BANNER("旗帜", 176, 0, "textures/blocks/bone_block_side.png"),
    /**
     * 悬挂的旗帜
     */
    WALL_BANNER("悬挂的旗帜", 177, 0, "textures/blocks/bone_block_side.png"),
    /**
     * 红沙石
     */
    RED_SANDSTONE("红沙石", 179, 0, "textures/blocks/red_sandstone_bottom.png"),
    /**
     * 錾制红沙石
     */
    CHISELED_RED_SANDSTONE("錾制红沙石", 179, 1, "textures/blocks/red_sandstone_carved.png"),
    /**
     * 平滑红沙石
     */
    SMOOTH_RED_SANDSTONE("平滑红沙石", 179, 2, "textures/blocks/red_sandstone_smooth.png"),
    /**
     * 红沙石阶梯
     */
    RED_SANDSTONE_STAIRS("红沙石阶梯", 180, 0, "textures/blocks/red_sandstone_carved.png"),
    /**
     * 红沙石台阶
     */
    STONE_SLAB2("红沙石台阶", 182, 0, "textures/blocks/red_sandstone_smooth.png"),
    /**
     * 云杉围墙大门
     */
    SPRUCE_FENCE_GATE("云杉围墙大门", 183, 0, "textures/blocks/door_spruce_lower.png"),
    /**
     * 桦木围墙大门
     */
    BIRCH_FENCE_GATE("桦木围墙大门", 184, 0, "textures/blocks/door_birch_upper.png"),
    /**
     * 丛林木围墙大门
     */
    JUNGLE_FENCE_GATE("丛林木围墙大门", 185, 0, "textures/blocks/door_acacia_upper.png"),
    /**
     * 深色橡木围墙大门
     */
    DARK_OAK_FENCE_GATE("深色橡木围墙大门", 186, 0, "textures/blocks/door_dark_oak_lower.png"),
    /**
     * 金合欢木围墙大门
     */
    ACACIA_FENCE_GATE("金合欢木围墙大门", 187, 0, "textures/blocks/door_spruce_lower.png"),
    /**
     * 重复命令块
     */
    SPRUCE_FENCE("重复命令块", 188, 0, "textures/blocks/chain_command_block_conditional_mipmap.png"),
    /**
     * 链命令块
     */
    BIRCH_FENCE("链命令块", 189, 0, "textures/blocks/repeating_command_block_back_mipmap.png"),
    /**
     * 桦木门
     */
    BIRCH_DOOR("桦木门", 194, 0, "textures/blocks/door_birch_upper.png"),
    /**
     * 绿茵小道
     */
    END_ROD("绿茵小道", 198, 0, "textures/blocks/end_rod.png"),
    /**
     * 合唱花
     */
    CHORUS_FLOWER("合唱花", 200, 0, "textures/blocks/chorus_flower.png"),
    /**
     * 紫珀方块
     */
    PURPUR_BLOCK("紫珀方块", 201, 0, "textures/blocks/purpur_block.png"),
    /**
     * 紫珀阶梯
     */
    PURPUR_STAIRS("紫珀阶梯", 203, 0, "textures/blocks/purpur_block.png"),
    /**
     * 潜匿之贝箱子
     */
    PURPUR_SLAB("潜匿之贝箱子", 205, 0, "textures/items/shulker_shell.png"),
    /**
     * 末地石砖
     */
    END_BRICKS("末地石砖", 206, 0, "textures/blocks/end_bricks.png"),
    /**
     * 末地棒
     */
    GRASS_PATH("末地棒", 208, 0, "textures/blocks/grass_path_side.png"),
    /**
     * 末地门2
     */
    END_GATEWAY("末地门2", 209, 0, "textures/blocks/end_gateway.png"),
    /**
     * 铁锹
     */
    IRON_SHOVEL("铁锹", 256, 0, "textures/items/iron_shovel.png"),
    /**
     * 铁镐
     */
    IRON_PICKAXE("铁镐", 257, 0, "textures/items/iron_pickaxe.png"),
    /**
     * 铁斧
     */
    IRON_AXE("铁斧", 258, 0, "textures/items/iron_axe.png"),
    /**
     * 打火石
     */
    FLINT_AND_STEEL("打火石", 259, 0, "textures/items/flint_and_steel.png"),
    /**
     * 苹果
     */
    APPLE("苹果", 260, 0, "textures/items/apple.png"),
    /**
     * 弓
     */
    BOW("弓", 261, 0, "textures/items/bow_standby.png"),
    /**
     * 箭
     */
    ARROW("箭", 262, 0, "textures/items/arrow.png"),
    /**
     * 煤炭
     */
    COAL("煤炭", 263, 0, "textures/items/coal.png"),
    /**
     * 木炭
     */
    CHARCOAL("木炭", 263, 1, "textures/items/charcoal.png"),
    /**
     * 钻石
     */
    DIAMOND("钻石", 264, 0, "textures/items/diamond.png"),
    /**
     * 铁锭
     */
    IRON_INGOT("铁锭", 265, 0, "textures/items/iron_ingot.png"),
    /**
     * 金锭
     */
    GOLD_INGOT("金锭", 266, 0, "textures/items/gold_ingot.png"),
    /**
     * 铁剑
     */
    IRON_SWORD("铁剑", 267, 0, "textures/items/iron_sword.png"),
    /**
     * 木剑
     */
    WOODEN_SWORD("木剑", 268, 0, "textures/items/wood_sword.png"),
    /**
     * 木锹
     */
    WOODEN_SHOVEL("木锹", 269, 0, "textures/items/wood_shovel.png"),
    /**
     * 木镐
     */
    WOODEN_PICKAXE("木镐", 270, 0, "textures/items/wood_pickaxe.png"),
    /**
     * 木斧
     */
    WOODEN_AXE("木斧", 271, 0, "textures/items/wood_axe.png"),
    /**
     * 石剑
     */
    STONE_SWORD("石剑", 272, 0, "textures/items/stone_sword.png"),
    /**
     * 石锹
     */
    STONE_SHOVEL("石锹", 273, 0, "textures/items/stone_shovel.png"),
    /**
     * 石镐
     */
    STONE_PICKAXE("石镐", 274, 0, "textures/items/stone_pickaxe.png"),
    /**
     * 石斧
     */
    STONE_AXE("石斧", 275, 0, "textures/items/stone_axe.png"),
    /**
     * 钻石剑
     */
    DIAMOND_SWORD("钻石剑", 276, 0, "textures/items/diamond_sword.png"),
    /**
     * 钻石锹
     */
    DIAMOND_SHOVEL("钻石锹", 277, 0, "textures/items/diamond_shovel.png"),
    /**
     * 钻石镐
     */
    DIAMOND_PICKAXE("钻石镐", 278, 0, "textures/items/diamond_pickaxe.png"),
    /**
     * 钻石斧
     */
    DIAMOND_AXE("钻石斧", 279, 0, "textures/items/diamond_axe.png"),
    /**
     * 木棍
     */
    STICK("木棍", 280, 0, "textures/items/stick.png"),
    /**
     * 碗
     */
    BOWL("碗", 281, 0, "textures/items/bowl.png"),
    /**
     * 蘑菇煲
     */
    MUSHROOM_STEW("蘑菇煲", 282, 0, "textures/items/mushroom_stew.png"),
    /**
     * 金剑
     */
    GOLDEN_SWORD("金剑", 283, 0, "textures/items/gold_sword.png"),
    /**
     * 金锹
     */
    GOLDEN_SHOVEL("金锹", 284, 0, "textures/items/gold_shovel.png"),
    /**
     * 金镐
     */
    GOLDEN_PICKAXE("金镐", 285, 0, "textures/items/gold_pickaxe.png"),
    /**
     * 金斧
     */
    GOLDEN_AXE("金斧", 286, 0, "textures/items/gold_axe.png"),
    /**
     * 蛛丝
     */
    STRING("蛛丝", 287, 0, "textures/items/string.png"),
    /**
     * 羽毛
     */
    FEATHER("羽毛", 288, 0, "textures/items/feather.png"),
    /**
     * 火药
     */
    GUNPOWDER("火药", 289, 0, "textures/items/gunpowder.png"),
    /**
     * 木锄
     */
    WOODEN_HOE("木锄", 290, 0, "textures/items/wood_hoe.png"),
    /**
     * 石锄
     */
    STONE_HOE("石锄", 291, 0, "textures/items/stone_hoe.png"),
    /**
     * 铁锄
     */
    IRON_HOE("铁锄", 292, 0, "textures/items/iron_hoe.png"),
    /**
     * 钻石锄
     */
    DIAMOND_HOE("钻石锄", 293, 0, "textures/items/diamond_hoe.png"),
    /**
     * 金锄
     */
    GOLDEN_HOE("金锄", 294, 0, "textures/items/gold_hoe.png"),
    /**
     * 种子
     */
    WHEAT_SEEDS("种子", 295, 0, "textures/items/seeds_wheat.png"),
    /**
     * 小麦
     */
    WHEAT("小麦", 296, 0, "textures/items/wheat.png"),
    /**
     * 面包
     */
    BREAD("面包", 297, 0, "textures/items/bread.png"),
    /**
     * 皮革头盔
     */
    LEATHER_HELMET("皮革头盔", 298, 0, "textures/items/leather_helmet.tga"),
    /**
     * 皮革胸甲
     */
    LEATHER_CHESTPLATE("皮革胸甲", 299, 0, "textures/items/leather_chestplate.png"),
    /**
     * 皮革护腿
     */
    LEATHER_LEGGINGS("皮革护腿", 300, 0, "textures/items/leather_leggings.tga"),
    /**
     * 皮革靴子
     */
    LEATHER_BOOTS("皮革靴子", 301, 0, "textures/items/leather_boots.tga"),
    /**
     * 锁链头盔
     */
    CHAINMAIL_HELMET("锁链头盔", 302, 0, "textures/items/chainmail_helmet.png"),
    /**
     * 锁链胸甲
     */
    CHAINMAIL_CHESTPLATE("锁链胸甲", 303, 0, "textures/items/chainmail_chestplate.png"),
    /**
     * 锁链护腿
     */
    CHAINMAIL_LEGGINGS("锁链护腿", 304, 0, "textures/items/chainmail_leggings.png"),
    /**
     * 锁链靴子
     */
    CHAINMAIL_BOOTS("锁链靴子", 305, 0, "textures/items/chainmail_boots.png"),
    /**
     * 铁头盔
     */
    IRON_HELMET("铁头盔", 306, 0, "textures/items/iron_helmet.png"),
    /**
     * 铁胸甲
     */
    IRON_CHESTPLATE("铁胸甲", 307, 0, "textures/items/iron_chestplate.png"),
    /**
     * 铁护腿
     */
    IRON_LEGGINGS("铁护腿", 308, 0, "textures/items/iron_leggings.png"),
    /**
     * 铁靴子
     */
    IRON_BOOTS("铁靴子", 309, 0, "textures/items/iron_boots.png"),
    /**
     * 钻石头盔
     */
    DIAMOND_HELMET("钻石头盔", 310, 0, "textures/items/diamond_helmet.png"),
    /**
     * 钻石胸甲
     */
    DIAMOND_CHESTPLATE("钻石胸甲", 311, 0, "textures/items/diamond_chestplate.png"),
    /**
     * 钻石护腿
     */
    DIAMOND_LEGGINGS("钻石护腿", 312, 0, "textures/items/diamond_leggings.png"),
    /**
     * 钻石靴子
     */
    DIAMOND_BOOTS("钻石靴子", 313, 0, "textures/items/diamond_boots.png"),
    /**
     * 金头盔
     */
    GOLDEN_HELMET("金头盔", 314, 0, "textures/items/gold_helmet.png"),
    /**
     * 金胸甲
     */
    GOLDEN_CHESTPLATE("金胸甲", 315, 0, "textures/items/gold_chestplate.png"),
    /**
     * 金护腿
     */
    GOLDEN_LEGGINGS("金护腿", 316, 0, "textures/items/gold_leggings.png"),
    /**
     * 金靴子
     */
    GOLDEN_BOOTS("金靴子", 317, 0, "textures/items/gold_boots.png"),
    /**
     * 燧石
     */
    FLINT("燧石", 318, 0, "textures/items/flint.png"),
    /**
     * 生猪排
     */
    PORKCHOP("生猪排", 319, 0, "textures/items/porkchop_raw.png"),
    /**
     * 熟猪排
     */
    COOKED_PORKCHOP("熟猪排", 320, 0, "textures/items/porkchop_cooked.png"),
    /**
     * 画
     */
    PAINTING("画", 321, 0, "textures/items/painting.png"),
    /**
     * 金苹果
     */
    GOLDEN_APPLE("金苹果", 322, 0, "textures/items/apple_golden.png"),
    /**
     * 告示牌
     */
    SIGN("告示牌", 323, 0, "textures/items/sign.png"),
    /**
     * 橡木门
     */
    WOODEN_DOOR("橡木门", 324, 0, "textures/items/door_wood.png"),
    /**
     * 桶
     */
    BUCKET("桶", 325, 0, "textures/items/bucket_empty.png"),
    /**
     * 矿车
     */
    MINECART("矿车", 328, 0, "textures/items/minecart_normal.png"),
    /**
     * 鞍
     */
    SADDLE("鞍", 329, 0, "textures/items/saddle.png"),
    /**
     * 红石
     */
    REDSTONE("红石", 331, 0, "textures/items/redstone_dust.png"),
    /**
     * 雪球
     */
    SNOWBALL("雪球", 332, 0, "textures/items/snowball.png"),
    /**
     * 橡木船
     */
    BOAT("橡木船", 333, 0, "textures/items/boat.png"),
    /**
     * 皮革
     */
    LEATHER("皮革", 334, 0, "textures/items/leather.png"),
    /**
     * 粘土球
     */
    CLAY_BALL("粘土球", 337, 0, "textures/items/clay_ball.png"),
    /**
     * 甘蔗
     */
    REEDS("甘蔗", 338, 0, "textures/items/reeds.png"),
    /**
     * 纸
     */
    PAPER("纸", 339, 0, "textures/items/paper.png"),
    /**
     * 书
     */
    BOOK("书", 340, 0, "textures/items/book_normal.png"),
    /**
     * 粘液球
     */
    SLIME_BALL("粘液球", 341, 0, "textures/items/slimeball.png"),
    /**
     * 箱子矿车
     */
    CHEST_MINECART("箱子矿车", 342, 0, "textures/items/minecart_chest.png"),
    /**
     * 鸡蛋
     */
    EGG("鸡蛋", 344, 0, "textures/items/egg.png"),
    /**
     * 指南针
     */
    COMPASS("指南针", 345, 0, "textures/items/compass_item.png"),
    /**
     * 鱼竿
     */
    FISHING_ROD("鱼竿", 346, 0, "textures/items/fishing_rod_cast.png"),
    /**
     * 时钟
     */
    CLOCK("时钟", 347, 0, "textures/items/clock_item.png"),
    /**
     * 荧石粉
     */
    GLOWSTONE_DUST("荧石粉", 348, 0, "textures/items/glowstone_dust.png"),
    /**
     * 鱼
     */
    FISH("鱼", 349, 0, "textures/items/fish_raw.png"),
    /**
     * 熟鱼
     */
    COOKED_FISH("熟鱼", 350, 0, "textures/items/fish_cooked.png"),
    /**
     * 墨囊
     */
    DYE("墨囊", 351, 0, "textures/items/dye_powder_black.png"),
    /**
     * 品红色染料
     */
    SOLFERINO_DYE("品红色染料", 351, 1, "textures/items/dye_powder_purple.png"),
    /**
     * 绿色染料
     */
    GREEN_DYE("绿色染料", 351, 2, "textures/items/dye_powder_green.png"),
    /**
     * 可可豆
     */
    BROWN_DYE("可可豆", 351, 3, "textures/items/dye_powder_brown.png"),
    /**
     * 蓝色染料
     */
    BLUE_DYE("蓝色染料", 351, 4, "textures/items/dye_powder_blue.png"),
    /**
     * 紫色染料
     */
    VIOLET_DYE("紫色染料", 351, 5, "textures/items/dye_powder_purple.png"),
    /**
     * 青色染料
     */
    CYAN_DYE("青色染料", 351, 6, "textures/items/dye_powder_cyan.png"),
    /**
     * 淡灰色染料
     */
    LIGHT_GRAY_DYE("淡灰色染料", 351, 7, "textures/items/dye_powder_silver.png"),
    /**
     * 灰色染料
     */
    GRAY_DYE("灰色染料", 351, 8, "textures/items/dye_powder_pink.png"),
    /**
     * 粉红色染料
     */
    PINK_DYE("粉红色染料", 351, 9, "textures/items/dye_powder_pink.png"),
    /**
     * 黄绿色染料
     */
    OLIVINE_DYE("黄绿色染料", 351, 10, "textures/items/dye_powder_lime.png"),
    /**
     * 黄色染料
     */
    YELLOW_DYE("黄色染料", 351, 11, "textures/items/dye_powder_yellow.png"),
    /**
     * 淡蓝色染料
     */
    LIGHT_BLUE_DYE("淡蓝色染料", 351, 12, "textures/items/dye_powder_light_blue.png"),
    /**
     * 红色染料
     */
    RED_DYE("红色染料", 351, 13, "textures/items/dye_powder_red.png"),
    /**
     * 橙色染料
     */
    ORANGE_DYE("橙色染料", 351, 14, "textures/items/dye_powder_orange.png"),
    /**
     * 骨粉
     */
    WHITE_DYE("骨粉", 351, 15, "textures/items/dye_powder_white.png"),
    /**
     * 骨头
     */
    BONE("骨头", 352, 0, "textures/items/bone.png"),
    /**
     * 糖
     */
    SUGAR("糖", 353, 0, "textures/items/sugar.png"),
    /**
     * 蛋糕
     */
    CAKE("蛋糕", 354, 0, "textures/items/cake.png"),
    /**
     * 床
     */
    BED("床", 355, 0, "textures/items/bed_red.png"),
    /**
     * 中继器
     */
    REPEATER("中继器", 356, 0, "textures/items/repeater.png"),
    /**
     * 曲奇
     */
    COOKIE("曲奇", 357, 0, "textures/items/cookie.png"),
    /**
     * 地图(满)
     */
    FILLED_MAP("地图(满)", 358, 0, "textures/items/map_nautilus.png"),
    /**
     * 剪刀
     */
    SHEARS("剪刀", 359, 0, "textures/items/shears.png"),
    /**
     * 西瓜
     */
    MELON("西瓜", 360, 0, "textures/items/melon.png"),
    /**
     * 南瓜种子
     */
    MELON_SEEDS("南瓜种子", 362, 0, "textures/items/seeds_melon.png"),
    /**
     * 生牛肉
     */
    BEEF("生牛肉", 363, 0, "textures/items/beef_raw.png"),
    /**
     * 熟牛肉
     */
    COOKED_BEEF("熟牛肉", 364, 0, "textures/items/beef_cooked.png"),
    /**
     * 生鸡肉
     */
    CHICKEN("生鸡肉", 365, 0, "textures/items/chicken_raw.png"),
    /**
     * 熟鸡肉
     */
    COOKED_CHICKEN("熟鸡肉", 366, 0, "textures/items/chicken_cooked.png"),
    /**
     * 腐肉
     */
    ROTTEN_FLESH("腐肉", 367, 0, "textures/items/rotten_flesh.png"),
    /**
     * 末影珍珠
     */
    ENDER_PEARL("末影珍珠", 368, 0, "textures/items/ender_pearl.png"),
    /**
     * 烈焰棒
     */
    BLAZE_ROD("烈焰棒", 369, 0, "textures/items/blaze_rod.png"),
    /**
     * 恶魂泪
     */
    GHAST_TEAR("恶魂泪", 370, 0, "textures/items/ghast_tear.png"),
    /**
     * 金粒
     */
    GOLD_NUGGET("金粒", 371, 0, "textures/items/gold_nugget.png"),
    /**
     * 地狱疣
     */
    NETHER_WART("地狱疣", 372, 0, "textures/items/nether_wart.png"),
    /**
     * 水瓶
     */
    POTION("水瓶", 373, 0, "textures/items/potion_bottle_drinkable.png"),
    /**
     * 玻璃瓶
     */
    GLASS_BOTTLE("玻璃瓶", 374, 0, "textures/items/potion_bottle_empty.png"),
    /**
     * 蜘蛛眼
     */
    SPIDER_EYE("蜘蛛眼", 375, 0, "textures/items/spider_eye.png"),
    /**
     * 发酵蜘蛛眼
     */
    SPIDER_EYE_FERMENTED("发酵蜘蛛眼", 376, 0, "textures/items/spider_eye_fermented.png"),
    /**
     * 烈焰粉
     */
    BLAZE_POWDER("烈焰粉", 377, 0, "textures/items/blaze_powder.png"),
    /**
     * 岩浆膏
     */
    MAGMA_CREAM("岩浆膏", 378, 0, "textures/items/magma_cream.png"),
    /**
     * 末影之眼
     */
    ENDER_EYE("末影之眼", 381, 0, "textures/items/ender_eye.png"),
    /**
     * 金西瓜
     */
    SPECKLED_MELON("金西瓜", 382, 0, "textures/items/melon_speckled.png"),
    /**
     * 经验瓶
     */
    EXPERIENCE_BOTTLE("经验瓶", 384, 0, "textures/items/experience_bottle.png"),
    /**
     * 火球
     */
    FIRE_CHARGE("火球", 385, 0, "textures/items/fireball.png"),
    /**
     * 绿宝石
     */
    EMERALD("绿宝石", 388, 0, "textures/items/emerald.png"),
    /**
     * 展示框
     */
    ITEM_FRAME("展示框", 389, 0, "textures/items/item_frame.png"),
    /**
     * 花盆
     */
    FLOWER_POT("花盆", 390, 0, "textures/items/flower_pot.png"),
    /**
     * 胡萝卜
     */
    CARROT("胡萝卜", 391, 0, "textures/items/carrot.png"),
    /**
     * 马铃薯
     */
    POTATO("马铃薯", 392, 0, "textures/items/potato.png"),
    /**
     * 烤马铃薯
     */
    BAKED_POTATO("烤马铃薯", 393, 0, "textures/items/potato_baked.png"),
    /**
     * 毒马铃薯
     */
    POISONOUS_POTATO("毒马铃薯", 394, 0, "textures/items/potato_poisonous.png"),
    /**
     * 空地图
     */
    MAP("空地图", 395, 0, "textures/items/map_empty.png"),
    /**
     * 金胡萝卜
     */
    GOLDEN_CARROT("金胡萝卜", 396, 0, "textures/items/carrot_golden.png"),
    /**
     * 骷髅头
     */
    SKELETON_SKULL("骷髅头", 397, 0, "textures/items/bone.png"),
    /**
     * 凋零骷髅头
     */
    LEIERDA_SKULL("凋零骷髅头", 397, 1, "textures/blocks/observer_front.png"),
    /**
     * 僵尸头
     */
    ZOMBIE_SKULL("僵尸头", 397, 2, "textures/blocks/observer_front.png"),
    /**
     * 史蒂夫头
     */
    STEVE_SKULL("史蒂夫头", 397, 3, "textures/blocks/observer_front.png"),
    /**
     * 苦力怕头
     */
    CREEPER_SKULL("苦力怕头", 397, 4, "textures/blocks/observer_front.png"),
    /**
     * 龙头
     */
    DRAGON_SKULL("龙头", 397, 5, "textures/blocks/observer_front.png"),
    /**
     * 胡萝卜杆
     */
    CARROT_ON_A_STICK("胡萝卜杆", 398, 0, "textures/items/carrot_on_a_stick.png"),
    /**
     * 下届之星
     */
    NETHER_STAR("下届之星", 399, 0, "textures/items/nether_star.png"),
    /**
     * 南瓜派
     */
    PUMPKIN_PIE("南瓜派", 400, 0, "textures/items/pumpkin_pie.png"),
    /**
     * 附魔书
     */
    ENCHANTED_BOOK("附魔书", 403, 0, "textures/items/book_writable.png"),
    /**
     * 比较器
     */
    COMPARATOR("比较器", 404, 0, "textures/items/comparator.png"),
    /**
     * 地狱石英
     */
    QUARTZ("地狱石英", 406, 0, "textures/items/quartz.png"),
    /**
     * tnt矿车
     */
    TNT_MINECART("tnt矿车", 407, 0, "textures/items/minecart_tnt.png"),
    /**
     * 漏斗矿车
     */
    HOPPER_MINECART("漏斗矿车", 408, 0, "textures/items/minecart_hopper.png"),
    /**
     * 海晶碎片
     */
    PRISMARINE_SHARD("海晶碎片", 409, 0, "textures/items/prismarine_shard.png"),
    /**
     * 海晶灯粉
     */
    PRISMARINE_CRYSTALS("海晶灯粉", 410, 0, "textures/items/prismarine_crystals.png"),
    /**
     * 生兔子肉
     */
    RABBIT("生兔子肉", 411, 0, "textures/items/rabbit_raw.png"),
    /**
     * 熟兔子肉
     */
    COOKED_RABBIT("熟兔子肉", 412, 0, "textures/items/rabbit_cooked.png"),
    /**
     * 兔子煲
     */
    RABBIT_STEW("兔子煲", 413, 0, "textures/items/rabbit_stew.png"),
    /**
     * 兔子脚
     */
    RABBIT_FOOT("兔子脚", 414, 0, "textures/items/rabbit_foot.png"),
    /**
     * 兔子皮
     */
    RABBIT_HIDE("兔子皮", 415, 0, "textures/items/rabbit_hide.png"),
    /**
     * 皮革马鞍
     */
    ARMOR_STAND("皮革马鞍", 416, 0, "textures/items/saddle.png"),
    /**
     * 铁马鞍
     */
    IRON_HORSE_ARMOR("铁马鞍", 417, 0, "textures/items/iron_horse_armor.png"),
    /**
     * 金马鞍
     */
    GOLD_HORSE_ARMOR("金马鞍", 418, 0, "textures/items/gold_horse_armor.png"),
    /**
     * 钻石马鞍
     */
    DIAMOND_HORSE_ARMOR("钻石马鞍", 419, 0, "textures/items/diamond_horse_armor.png"),
    /**
     * 栓绳
     */
    LEAD("栓绳", 420, 0, "textures/items/lead.png"),
    /**
     * 命名牌
     */
    NAME_TAG("命名牌", 421, 0, "textures/items/name_tag.png"),
    /**
     * 命令方块矿车
     */
    COMMAND_BLOCK_MINECART("命令方块矿车", 422, 0, "textures/items/minecart_command_block.png"),
    /**
     * 生羊肉
     */
    MUTTON("生羊肉", 423, 0, "textures/items/mutton_raw.png"),
    /**
     * 熟羊肉
     */
    COOKED_MUTTON("熟羊肉", 424, 0, "textures/items/mutton_cooked.png"),
    /**
     * 云杉木门
     */
    SPRUCE_DOOR("云杉木门", 427, 0, "textures/items/door_jungle.png"),
    /**
     * 桦树木门
     */
    BIRCH_WOOD_DOOR("桦树木门", 428, 0, "textures/items/door_birch.png"),
    /**
     * 丛林木门
     */
    JUNGLE_DOOR("丛林木门", 429, 0, "textures/items/door_spruce.png"),
    /**
     * 金合欢木门
     */
    ACACIA_DOOR("金合欢木门", 430, 0, "textures/items/door_acacia.png"),
    /**
     * 深色橡木门
     */
    DARK_OAK_DOOR("深色橡木门", 431, 0, "textures/items/door_dark_oak.png"),
    /**
     * 共鸣果
     */
    CHORUS_FRUIT("共鸣果", 432, 0, "textures/items/chorus_fruit.png"),
    /**
     * 爆裂共鸣果
     */
    POPPED_CHORUS_FRUIT("爆裂共鸣果", 433, 0, "textures/items/chorus_fruit_popped.png"),
    /**
     * 龙息
     */
    DRAGON_BREATH("龙息", 437, 0, "textures/items/dragons_breath.png"),
    /**
     * 喷溅的水瓶
     */
    SPLASH_POTION("喷溅的水瓶", 438, 0, "textures/items/potion_bottle_splash.png"),
    /**
     * 遗留的水瓶
     */
    LINGERING_POTION("遗留的水瓶", 441, 0, "textures/items/potion_bottle_lingering_waterBreathing.png"),
    /**
     * 翅鞘
     */
    ELYTRA("翅鞘", 444, 0, "textures/items/elytra.png"),
    /**
     * 潜匿之壳
     */
    BIRCH_BOAT("潜匿之壳", 445, 0, "textures/items/shulker_shell.png"),
    /**
     * 鸡刷怪蛋
     */
    SPAWN_MOB("鸡刷怪蛋", 383, 10, "textures/items/egg_chicken.png"),
    /**
     * 末影水晶
     * */
    END_CRYSTAL("末影水晶",426,0,"textures/items/end_crystal.png");

    private int ID, Damage;
    private String Name, Path;
    private static final HashMap<String, Map<String, Object>> NAME_MAP = new HashMap<>();
    private static final HashMap<String, Map<String, Object>> ID_MAP = new HashMap<>();
    private static final HashMap<String, ItemIDSunName> ItemIDSunName_MAP = new HashMap<>();
    private static final ArrayList<HashMap<String, Object>> All = new ArrayList<>();
    static {
        for (ItemIDSunName item : ItemIDSunName.values()) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ID", item.ID);
            map.put("Damage", item.Damage);
            map.put("Path", item.Path);
            map.put("Name", item.Name);
            map.put("item", item);
            All.add(map);
            NAME_MAP.put(item.Name, map);
            ID_MAP.put(item.ID + ":" + item.Damage, map);
            ItemIDSunName_MAP.put(item.ID + ":" + item.Damage, item);
        }
    }

    /**
     * @param Name   物品名称
     * @param id     物品ID
     * @param Damage 物品特殊值
     * @param Path   物品贴图路径
     */
    private ItemIDSunName(String Name, int id, int Damage, String Path) {
        this.ID = id;
        this.Name = Name;
        this.Damage = Damage;
        this.Path = Path;
    }

    /**
     * @return 物品贴图路径
     */
    public String getPath() {
        return this.Path;
    }

    /**
     * @return 物品名称
     */
    public String getName() {
        return this.Name;
    }

    /**
     * @return 物品特殊值
     */
    public int getDamage() {
        return this.Damage;
    }

    /**
     * @return 物品ID
     */
    public int getID() {
        return this.ID;
    }

    /**
     * @param ID 物品ID
     * @return 根据物品ID获取物品贴图路径
     */
    public static String getIDByPath(int ID) {
        return getIDByPath(ID + ":0");
    }

    /**
     * @param ID     物品ID
     * @param Damage 物品特殊值
     * @return 根据物品ID获取物品贴图路径
     */
    public static String getIDByPath(int ID, int Damage) {
        return getIDByPath(ID + ":" + Damage);
    }

    /**
     * @param ID 物品ID（ID：特殊值）
     * @return 根据物品ID获取物品贴图路径（ID：特殊值）
     */
    public static String getIDByPath(String ID) {
        Map<String, Object> map = ID_MAP.getOrDefault(ID, null);
        if (map == null || map.getOrDefault("Path", null) == null)
            return null;
        return String.valueOf(map.get("Path"));
    }



    public static String getIDByName(Item item) {
        String name = getIDByName(item.getId() + ":" + item.getDamage());
        if(name == null){
            if(item.hasCustomName()){
                return item.getCustomName();
            }else{
                return item.getName();
            }
        }else{
            if(item.hasCustomName()) {
                return item.getCustomName();
            }
            return name;
        }
    }

    /**
     * @param ID 物品ID（ID：特殊值）
     * @return 根据物品ID获取物品名称
     */
    public static String getIDByName(String ID) {
        Map<String, Object> map = ID_MAP.getOrDefault(ID, null);
        if (map == null || map.getOrDefault("Name", null) == null)
            return null;
        return String.valueOf(map.get("Name"));
    }

    /**
     * @param Name 物品名称
     * @return 根据物品名称获取物品ID
     */
    public static int getNameByID(String Name) {
        Map<String, Object> map = NAME_MAP.getOrDefault(Name, null);
        if (map == null || map.getOrDefault("ID", null) == null)
            return 0;
        return Integer.parseInt(String.valueOf(map.get("ID")));
    }

    /**
     * @param Name 物品名称
     * @return 根据物品名称获取物品特殊值
     */
    public static int getNameByDamage(String Name) {
        Map<String, Object> map = NAME_MAP.getOrDefault(Name, null);
        if (map == null || map.getOrDefault("Damage", null) == null)
            return 0;
        return Integer.parseInt(String.valueOf(map.get("Damage")));
    }

    /**
     * @param Name 物品名称
     * @return 根据物品名称获取物品贴图路径
     */
    public static String getNameByPath(String Name) {
        Map<String, Object> map = NAME_MAP.getOrDefault(Name, null);
        if (map == null || map.getOrDefault("Path", null) == null)
            return null;
        return String.valueOf(map.get("Path"));
    }

    /**
     * @param ID 物品ID
     * @return 根据物品ID获取物品枚举对象
     */
    public static ItemIDSunName getItem(int ID) {
        return getItem(ID, 0);
    }

    /**
     * @param ID     物品ID
     * @param Damage 物品特殊值
     * @return 根据物品ID获取物品枚举对象
     */
    public static ItemIDSunName getItem(int ID, int Damage) {
        return ItemIDSunName_MAP.getOrDefault(ID + ":" + Damage, null);
    }

    /**
     * @param Name 物品名称
     * @return 根据物品ID获取物品枚举对象
     */
    public static ItemIDSunName getItem(String Name) {
        return getItem(getNameByID(Name), getNameByDamage(Name));
    }

    /**
     * @param ID（ID：特殊值）/物品名称
     * @return 尝试解析冰获取物品ID（ID：特殊值）
     */
    public static String UnknownToID(String ID) {
        if (!ID.contains(":")) {
            if (getNameByPath(ID) != null)
                return getNameByID(ID) + ":" + getNameByDamage(ID);
            else if (getIDByPath(ID + ":0") != null)
                return ID + ":0";
            else
                return ID;
        } else {
            if (getIDByPath(ID) != null)
                return ID;
            else if (getNameByPath(ID) != null)
                return getNameByID(ID) + ":" + getNameByDamage(ID);
            else
                return ID;
        }
    }

}