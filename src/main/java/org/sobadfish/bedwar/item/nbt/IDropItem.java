package org.sobadfish.bedwar.item.nbt;

import org.sobadfish.bedwar.player.PlayerInfo;

/**
 * 是否为可投掷
 * @author Sobadfish
 * @date 2024/8/26
 */
public interface IDropItem {

    /**
     * 丢出去后砸到对象的执行
     * @param player 丢出的玩家
     * */
    void onRun(PlayerInfo player);
}
