package com.gmail.gephery.partywgames.controllers;

import net.projectzombie.consistentchatapi.PluginChat;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

/**
 * @author Gephery
 */
public class PWGText
{
    private static final PluginChat TEAM_CHAT = new PluginChat("Team", ChatColor.GREEN,
                                                ChatColor.WHITE, ChatColor.BOLD,
                                                ChatColor.ITALIC);

    private static final String TEAM_TAG = TEAM_CHAT.getTag();
    private static final PluginChat PRIVATE_CHAT = new PluginChat("Private", ChatColor.DARK_BLUE,
                                                    ChatColor.WHITE, ChatColor.BOLD,
                                                    ChatColor.ITALIC);
    private static final String PRIVATE_TAG = TEAM_CHAT.getTag();

    private static final PluginChat PWG_CHAT = new PluginChat("Games", ChatColor.LIGHT_PURPLE,
                                                ChatColor.WHITE, ChatColor.BOLD,
                                                ChatColor.ITALIC);
    private static final String PWG_TAG = PWG_CHAT.getTag();
    public static final ChatColor PLUGIN_MSG_COLOR = ChatColor.WHITE;

    public static final String COMMAND_ROOT = "game";
    public static final String PLUGIN_INITIALS = "PWG";

    public static final int
        ADD_STATE_REQ_PARAM = 3,
        ADD_STATE_CMD_REQ_PARAM = 4,
        ADD_ACHIEVEMENT_REQ_PARAM = 4,
        ADD_ACHIEVEMENT_CMD_REQ_PARAM = 5,
        VOTE_REQ_PARAM = 2,
        SWITCH_STATE_REQ_PARAM = 3,
        END_STATE_REQ_PARAM = 2,
        DEBUG_REQ_PARAM = 2,
        GAME_CMD_REQ_PARAM = 3;


    public static final String

        ADD_STATE_CMD_KEY = "addstate",
        ADD_STATE_CMD_CMD_KEY = "addstatecmd",
        ADD_ACHIEVEMENT_CMD_KEY = "addachievement",
        ADD_ACHIEVEMENT_CMD_CMD_KEY = "addachievementcmd",
        VOTE_CMD_KEY = "vote",
        SWITCH_STATE_CMD_KEY = "switch",
        END_STATE_CMD_KEY = "end",
        DEBUG_CMD_KEY = "debug",
        GAME_CMD_CMD_KEY = "rungamecmd",

        ADD_STATE_SUCCESS = formatForPluginMsging("State successfully added."),
        ADD_STATE_CMD_SUCCESS = formatForPluginMsging("CMD successfully added to state."),
        ADD_ACHIEVEMENT_SUCCESS = formatForPluginMsging("Achievement successfully added."),
        ADD_ACHIEVEMENT_CMD_SUCCESS = formatForPluginMsging("CMD successfully added to " +
                                        "achievement."),
        VOTE_SUCCESS = formatForPluginMsging("Great job voting! :)"),
        SWITCH_SUCCESS = formatForPluginMsging("State has been switched"),
        END_SUCCESS = formatForPluginMsging("Game has been ended."),
        DEBUG_SUCCESS = formatForPluginMsging("Reload to start receiving debug msgs."),
        GAME_CMD_SUCCESS = formatForPluginMsging("Game cmd successfully run."),

        ADD_STATE_USAGE = formatForUsage(ADD_STATE_CMD_KEY + " <gameName> <stateID >= 2>"),
        ADD_STATE_CMD_USAGE = formatForUsage(ADD_STATE_CMD_CMD_KEY + " <gameName> <stateID >= 2>" +
                             " <cmd...>"),
        ADD_ACHIEVEMENT_USAGE = formatForUsage(ADD_ACHIEVEMENT_CMD_KEY +
                                " <gameName> <ZOMBIE_KILL:PLAYER_KILL>" +
                                " <goal>"),
        ADD_ACHIEVEMENT_CMD_USAGE = formatForUsage(ADD_ACHIEVEMENT_CMD_CMD_KEY + " <gameName> " +
                                    "<ZOMBIE_KILL:PLAYER_KILL> <goal> <cmd...>"),
        VOTE_USAGE = formatForUsage(VOTE_CMD_KEY + " <t:f>"),
        SWITCH_USAGE = formatForUsage(SWITCH_STATE_CMD_KEY + " <gameName> <stateID>"),
        END_STATE_USAGE = formatForUsage(END_STATE_CMD_KEY + " <gameName>"),
        DEBUG_USAGE = formatForUsage(DEBUG_CMD_KEY + " <t:f>"),
        GAME_CMD_USAGE = formatForUsage(DEBUG_CMD_KEY + " <gameName> <cmd...>"),

        ADD_STATE_PERM = formatPerm(ADD_STATE_CMD_KEY),
        ADD_STATE_CMD_PERM = formatPerm(ADD_STATE_CMD_CMD_KEY),
        ADD_ACHIEVEMENT_PERM = formatPerm(ADD_ACHIEVEMENT_CMD_KEY),
        ADD_ACHIEVEMENT_CMD_PERM = formatPerm(ADD_ACHIEVEMENT_CMD_CMD_KEY),
        VOTE_PERM = formatPerm(VOTE_CMD_KEY),
        SWTICH_PERM = formatPerm(SWITCH_STATE_CMD_KEY),
        END_STATE_PERM = formatPerm(END_STATE_CMD_KEY),
        DEBUG_PERM = formatPerm(DEBUG_CMD_KEY),
        GAME_CMD_PERM = formatPerm(GAME_CMD_CMD_KEY);

    public static String addStateMSG(final boolean success)
    { return success ? ADD_STATE_SUCCESS : ADD_STATE_USAGE; }

    public static String addStateCMDMSG(final boolean success)
    { return success ? ADD_STATE_CMD_SUCCESS : ADD_STATE_CMD_USAGE; }

    public static String addAchievementMSG(final boolean success)
    { return success ? ADD_ACHIEVEMENT_SUCCESS : ADD_ACHIEVEMENT_USAGE; }

    public static String addAchievementCMDMSG(final boolean success)
    { return success ? ADD_ACHIEVEMENT_CMD_SUCCESS : ADD_ACHIEVEMENT_CMD_USAGE; }

    public static String voteMSG(final boolean success)
    { return success ? VOTE_SUCCESS : VOTE_USAGE; }

    public static String switchStateMSG(final boolean success)
    { return success ? SWITCH_SUCCESS : SWITCH_USAGE; }

    public static String endStateMSG(final boolean success)
    { return success ? END_SUCCESS : END_STATE_USAGE; }

    public static String debugMSG(final boolean success)
    { return success ? DEBUG_SUCCESS : DEBUG_USAGE; }

    public static String gameCMDMSG(final boolean success)
    { return success ? GAME_CMD_SUCCESS : GAME_CMD_USAGE; }

    private static String formatPerm(final String perm)
    { return PLUGIN_INITIALS + "." + perm; }

    private static String formatPlayerName(final Player player)
    { return "<" + player.getDisplayName() + "> "; }

    public static String formatForUsage(final String usage)
    { return formatForPluginMsging("/" + COMMAND_ROOT + " " + usage); }

    /** Used to add the tag and allow difference in failed and success message.
     * txtS will be used if success is true and txtF will be used if success is false.
     */
    public static String formatForTeamChat(final String msg, final Player player)
    { return TEAM_TAG + formatPlayerName(player) + msg; }

    public static String formatForPrivateChat(final String msg, final Player player)
    { return PRIVATE_TAG + formatPlayerName(player) + msg; }

    public static String formatForPluginMsging(final String msg)
    { return PWG_TAG + msg; }
}
