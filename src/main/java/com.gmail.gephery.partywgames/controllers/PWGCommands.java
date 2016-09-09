package com.gmail.gephery.partywgames.controllers;

import static com.gmail.gephery.partywgames.controllers.PWGText.*;

import com.gmail.gephery.partywgames.modules.GameBoardController.AchievementType;
import com.gmail.gephery.partywgames.modules.GameBoardController;
import com.gmail.gephery.partywgames.util.Messenger;
import net.projectzombie.survivalteams.file.buffers.PlayerBuffer;
import net.projectzombie.survivalteams.team.Team;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Gephery
 */
public class PWGCommands implements CommandExecutor
{
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (args[0].equalsIgnoreCase(ADD_STATE_CMD_KEY)
                && sender.hasPermission(ADD_STATE_PERM))
            {
                boolean success = false;
                if (args.length == ADD_STATE_REQ_PARAM)
                {
                    success = GameBoardController.addState(args[1], Integer.valueOf(args[2]));
                }
                sender.sendMessage(addStateMSG(success));
            }
            else if (args[0].equalsIgnoreCase(ADD_STATE_CMD_CMD_KEY)
                     && sender.hasPermission(ADD_STATE_CMD_PERM))
            {
                boolean success = false;
                if (args.length >= ADD_STATE_CMD_REQ_PARAM)
                {
                    StringBuilder builder = new StringBuilder();
                    for (int i = ADD_STATE_CMD_REQ_PARAM - 1; i < args.length; i++) {
                        builder.append(" ");
                        builder.append(args[i]);
                    }
                    String cmd = builder.toString().trim();
                    success = GameBoardController.addCMDToState(args[1], Integer.valueOf(args[2]),
                                                                cmd);
                }
                sender.sendMessage(addStateCMDMSG(success));
            }
            else if (args[0].equalsIgnoreCase(ADD_ACHIEVEMENT_CMD_KEY)
                     && sender.hasPermission(ADD_ACHIEVEMENT_PERM))
            {
                boolean success = false;
                if (args.length == ADD_ACHIEVEMENT_REQ_PARAM)
                {
                    success = GameBoardController.addAchievement(args[1],
                                                                 AchievementType.valueOf(args[2]),
                                                                 0, Integer.valueOf(args[3]));
                }
                sender.sendMessage(addAchievementMSG(success));
            }
            else if (args[0].equalsIgnoreCase(ADD_ACHIEVEMENT_CMD_CMD_KEY)
                     && sender.hasPermission(ADD_ACHIEVEMENT_CMD_PERM))
            {
                boolean success = false;
                if (args.length >= ADD_ACHIEVEMENT_CMD_REQ_PARAM)
                {
                    StringBuilder builder = new StringBuilder();
                    for (int i = ADD_ACHIEVEMENT_CMD_REQ_PARAM - 1; i < args.length; i++) {
                        builder.append(" ");
                        builder.append(args[i]);
                    }
                    String cmd = builder.toString().trim();
                    success = GameBoardController.addCMDToAchievement(args[1], cmd,
                                                                      AchievementType.valueOf(args[2]),
                                                                      0, Integer.valueOf(args[3]));
                }
                sender.sendMessage(addAchievementCMDMSG(success));
            }
            else if (args[0].equalsIgnoreCase(VOTE_CMD_KEY) && sender.hasPermission(VOTE_PERM))
            {
                boolean success = false;
                if (sender instanceof Player && args.length == VOTE_REQ_PARAM)
                {
                    Player player = (Player) sender;
                    Team team = PlayerBuffer.get(player.getUniqueId()).getTeam();
                    success = true;
                    GameBoardController.vote(team, player, Boolean.valueOf(args[1]));
                }
                sender.sendMessage(voteMSG(success));
            }
            else if (args[0].equalsIgnoreCase(SWITCH_STATE_CMD_KEY)
                     && sender.hasPermission(SWTICH_PERM))
            {
                boolean success = false;
                if (args.length == SWITCH_STATE_REQ_PARAM)
                {
                    success = GameBoardController.switchGameState(args[1], Integer.valueOf(args[2]));
                }
                sender.sendMessage(switchStateMSG(success));
            }
            else if (args[0].equalsIgnoreCase(END_STATE_CMD_KEY)
                     && sender.hasPermission(END_STATE_PERM))
            {
                boolean success = args.length == END_STATE_REQ_PARAM;
                if (success)
                {
                    GameBoardController.endGame(args[1]);
                }
                sender.sendMessage(endStateMSG(success));
            }
            else if (args[0].equalsIgnoreCase(DEBUG_CMD_KEY)
                    && sender.hasPermission(DEBUG_PERM))
            {
                boolean success = args.length == DEBUG_REQ_PARAM;
                if (success)
                {
                    GameBoardController.setDebug(Boolean.valueOf(args[1]));
                }
                sender.sendMessage(debugMSG(success));
            }
            else if (args[0].equalsIgnoreCase(GAME_CMD_CMD_KEY)
                    && sender.hasPermission(GAME_CMD_PERM))
            {
                boolean success = false;
                if (args.length >= GAME_CMD_REQ_PARAM)
                {
                    StringBuilder builder = new StringBuilder();
                    String gameName = args[1];
                    for (int i = GAME_CMD_REQ_PARAM - 1; i < args.length; i++) {
                        builder.append(" ");
                        builder.append(args[i]);
                    }
                    String cmd = builder.toString().trim();
                    success = GameBoardController.sendCMDToGame(gameName, cmd);
                }
                sender.sendMessage(gameCMDMSG(success));
            }
            return true;
        }
        //TODO add leave game command
        return false;
    }
}
