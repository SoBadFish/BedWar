package org.sobadfish.bedwar.panel.lib;

import cn.nukkit.Player;
import cn.nukkit.inventory.ContainerInventory;
import cn.nukkit.inventory.InventoryHolder;
import cn.nukkit.inventory.InventoryType;
import cn.nukkit.level.GlobalBlockPalette;
import cn.nukkit.math.BlockVector3;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.ContainerOpenPacket;
import cn.nukkit.network.protocol.UpdateBlockPacket;
import org.sobadfish.bedwar.manager.ThreadManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 本类引用 SupermeMortal 的 FakeInventories 插件
 * @author SupermeMortal*/
public abstract class AbstractFakeInventory extends ContainerInventory {
    public static boolean IS_PM1E = false;
    private static final BlockVector3 ZERO = new BlockVector3(0, 0, 0);

    private static final Map<Player, AbstractFakeInventory> OPEN = new ConcurrentHashMap<>();

    final Map<Player, List<BlockVector3>> blockPositions = new HashMap<>();
    private final String title;

    AbstractFakeInventory(InventoryType type, InventoryHolder holder, String title) {
        super(holder, type);
        this.title = title == null ? type.getDefaultTitle() : title;
    }

    @Override
    public void onOpen(Player who) {
//        checkForClosed();
        this.viewers.add(who);
        if (OPEN.putIfAbsent(who, this) != null) {
            throw new IllegalStateException("Inventory was already open");
        }

        List<BlockVector3> blocks = onOpenBlock(who);
        blockPositions.put(who, blocks);

        onFakeOpen(who, blocks);
    }

    void onFakeOpen(Player who, List<BlockVector3> blocks) {
        BlockVector3 blockPosition = blocks.isEmpty() ? ZERO : blocks.get(0);

        ContainerOpenPacket containerOpen = new ContainerOpenPacket();
        containerOpen.windowId = who.getWindowId(this);
        containerOpen.type = this.getType().getNetworkType();
        containerOpen.x = blockPosition.x;
        containerOpen.y = blockPosition.y;
        containerOpen.z = blockPosition.z;

        who.dataPacket(containerOpen);

        this.sendContents(who);
    }
    /**
     * 玩家开启
     * @param who 玩家
     * @return 方块坐标*/
    protected abstract List<BlockVector3> onOpenBlock(Player who);
    @Override
    public void onClose(Player who) {
        super.onClose(who);
        OPEN.remove(who, this);
        List<BlockVector3> blocks = blockPositions.get(who);
        for (int i = 0, size = blocks.size(); i < size; i++) {
            final int index = i;
            ThreadManager.SCHEDULED.execute(() -> {
                Vector3 blockPosition = blocks.get(index).asVector3();
                UpdateBlockPacket updateBlock = new UpdateBlockPacket();
                if(IS_PM1E){
                    updateBlock.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(who.protocol,who.getLevel().getBlock(blockPosition).getFullId());
                }else{
                    updateBlock.blockRuntimeId = GlobalBlockPalette.getOrCreateRuntimeId(who.getLevel().getBlock(blockPosition).getFullId());
                }
                updateBlock.flags = UpdateBlockPacket.FLAG_ALL_PRIORITY;
                updateBlock.x = blockPosition.getFloorX();
                updateBlock.y = blockPosition.getFloorY();
                updateBlock.z = blockPosition.getFloorZ();
                who.dataPacket(updateBlock);
            });
        }
    }



    @Override
    public String getTitle() {
        return title;
    }




}
