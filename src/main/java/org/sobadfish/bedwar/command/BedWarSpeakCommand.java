package org.sobadfish.bedwar.command;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import org.sobadfish.bedwar.BedWarMain;
import org.sobadfish.bedwar.player.PlayerInfo;


/**
 * @author SoBadFish
 * 2022/1/15
 */
public class BedWarSpeakCommand extends Command {

    public BedWarSpeakCommand(String name) {
        super(name);
    }


    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if(commandSender instanceof Player){
            PlayerInfo info = BedWarMain.getRoomManager().getPlayerInfo((Player) commandSender);
            if(info == null){
                new PlayerInfo((Player)commandSender).sendForceMessage("&cYou weren't in the game room!");
                return false;
            }else{
                if(strings.length > 0){
                    info.getGameRoom().sendFaceMessage("&l&7(ALL)&r "+info+"&r >> "+strings[0]);

                }else{
                    info.sendForceMessage("&cCommand :/bws <msg> all message");
                }
            }

        }
        return true;
    }
}
