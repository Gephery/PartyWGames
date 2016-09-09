package com.gmail.gephery.partywgames.util;

import com.gmail.gephery.partywgames.controllers.PWGText;
import com.gmail.gephery.partywgames.main.Main;
import com.gmail.gephery.partywgames.modules.GameBoardController;
import net.projectzombie.survivalteams.player.TeamPlayer;
import net.projectzombie.survivalteams.team.Team;
import org.bukkit.entity.Player;

/**
 * @author Gephery
 */
public class Messenger
{

    public static void msgTeamFromServer(final Team team, final String[] msg)
    {
        String finalMsg = buildMyMSG(msg);
        for (TeamPlayer teamPlayer : team.getPlayers())
        {
            teamPlayer.getPlayer().sendMessage(PWGText.formatForPluginMsging(finalMsg));
        }
    }

    public static void sendTeamMsgFromPlayer(final Team team, final Player player,
                                             final String[] msg)
    {
        String finalMsg = buildMyMSG(msg);
        for (TeamPlayer teamPlayer : team.getPlayers())
        {
            teamPlayer.getPlayer().sendMessage(PWGText.formatForTeamChat(finalMsg, player));
        }
    }

    public static void sendDebugMSG(final String[] msg, final Class className)
    {
        String finalMsg = buildMyMSG(msg);
        if (GameBoardController.isDebugMode())
        {
            Main.getPlugin().getLogger().info(GameBoardController.DEBUG_MSG_ROOT +
                                              className.getName() + ": " + finalMsg);
        }
    }

    private static String buildMyMSG(final String[] msg)
    {
        StringBuilder builder = new StringBuilder();
        for (String portion : msg)
        {
            builder.append(portion);
        }
        return builder.toString();
    }
}
