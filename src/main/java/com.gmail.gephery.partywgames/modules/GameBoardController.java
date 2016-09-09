package com.gmail.gephery.partywgames.modules;

import com.gmail.gephery.partywgames.main.Main;
import com.gmail.gephery.partywgames.util.CMDKeyWordTools;
import com.gmail.gephery.partywgames.util.Messenger;
import com.gmail.gephery.teleport.file.FileBuffer;
import com.gmail.gephery.teleport.file.FileBufferController;
import net.projectzombie.survivalteams.player.TeamPlayer;
import net.projectzombie.survivalteams.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Gephery
 */
public class GameBoardController
{
    public enum AchievementType
    {
        INVALID, ZOMBIE_KILL, PLAYER_KILL;
    }

    //TODO add world UUID to games key to allow same game to be in different worlds or sign loc.
    protected static final String STRING_SEPARATOR = ",";
    protected static final String YAML_SEPTOR = ".";
    public static final String CONFIG_FILE_PATH_ROOT = "config";
    public static final String DEBUG_FP_KEY = "debug";
    public static final String DEBUG_MSG_ROOT = "Debug: ";

    private static Map<String, GameBoard> games = new HashMap<String, GameBoard>();
    private static Map<String, String> teamToGame = new HashMap<String, String>();
    private static Map<String, Voter> gameVote = new HashMap<String, Voter>();
    private static boolean debugMode;

    public static boolean isDebugMode()             { return debugMode; }
    public static void setDebug(final boolean mode) { debugMode = mode; }

    public static boolean isValidGame(final String gameName)
    { return games.get(gameName) != null; }

    public static void init()
    {
        debugMode = readDebug();
        readInAllGameBoards();
    }

    public static boolean isGameOpen(final String gameName)
    { return games.get(gameName) != null && games.get(gameName).getCurrentTeam() == null; }

    public static boolean sendCMDToGame(final String gameName, final String cmd)
    {
        GameBoard gameBoard = games.get(gameName);
        if (gameBoard != null)
        {
            Team team = gameBoard.getCurrentTeam();
            if (team != null)
            {
                for (TeamPlayer teamPlayer : team.getPlayers())
                {
                    Player player = teamPlayer.getPlayer();
                    String filteredCMD = CMDKeyWordTools.runAllFilters(cmd, team, player);
                    CommandSender sender = Bukkit.getServer().getConsoleSender();
                    Main.getPlugin().getServer().dispatchCommand(sender, filteredCMD);
                }
                return true;
            }
        }
        return false;
    }

    public static String getTeamCurrentGame(final String teamName)
    { return teamToGame.get(teamName); }

    public static boolean incrementAchievements(final String gameName, final AchievementType type)
    {
        Messenger.sendDebugMSG(new String[]{gameName, " is trying to increment ", type.toString(),
                               " types."}, GameBoardController.class);
        if (games.get(gameName) != null)
        {
            Messenger.sendDebugMSG(new String[]{gameName, " is incrementing ", type.toString(),
                                   " types."}, GameBoardController.class);
            games.get(gameName).incrementAchievements(type);
            return true;
        }
        return false;
    }

    public static void createVote(final Team team, final Player player, final String gameName)
    {
        Messenger.sendDebugMSG(new String[]{player.getDisplayName(), " has started a game vote for ",
                               team.getName(), " for the game of ", gameName},
                               GameBoardController.class);
        gameVote.put(team.getName(), new Voter(team.getPlayers().size(), gameName));
        vote(team, player, true);
    }

    public static void vote(final Team team, final Player player, final boolean choice)
    {
        if (team != null)
        {
            Messenger.sendDebugMSG(new String[]{player.getDisplayName(), " from ", team.getName(),
                                   " is voting as ", choice + ""}, GameBoardController.class);
            Voter voter = gameVote.get(team.getName());
            //TODO In future change vote to create voters and check validity
            if (voter != null)
            {
                voter.vote(choice, player);
                Messenger.sendDebugMSG(new String[]{"Vote over: ", voter.isVoteOver() + "",
                                       " Vote infavor: ", voter.isVotePro() + ""},
                                       GameBoardController.class);
                if (voter.isVoteOver())
                {
                    if (voter.isVotePro())
                    {
                        startGame(voter.getVotedGame(), team);
                        Messenger.msgTeamFromServer(team, new String[]{"Game is starting!"});
                        gameVote.remove(team.getName());
                        return;
                    }
                    gameVote.remove(team.getName());
                    Messenger.msgTeamFromServer(team, new String[]{"Vote failed, make sure your" +
                                                " team wants to play this game."});
                    return;
                }
                Messenger.msgTeamFromServer(team, new String[]{"Still waiting on ",
                        Integer.toString(voter.getVoters())," players to vote"});
            }
        }
    }

    public static Team getTeam(final String gameName)
    { return games.get(gameName).getCurrentTeam(); }

    public static boolean createGameBoard(final String gameName, final Location signLocation)
    {
        GameBoard gameBoard = new GameBoard(gameName, signLocation);
        boolean success = gameBoard.writeGameBoard();
        if (success)
            addGameBoard(gameBoard);
        Messenger.sendDebugMSG(new String[]{gameName + "'(s) creation was successful: " + success},
                               GameBoardController.class);
        return success;
    }

    private static void addGameBoard(final GameBoard gameBoard)
    { games.put(gameBoard.getGameName(), gameBoard); }

    private static void readInAllGameBoards()
    {
        List<World> worlds = Main.getPlugin().getServer().getWorlds();
        for (World world : worlds)
        {
            FileBuffer fileBuffer = FileBufferController
                                        .instance(Main.getPluginName()).getFile(world,
                                                                                Main.FILE_NAME);
            fileBuffer.safeLoadFile(world);
            Set<String> gameBoardNames = GameBoard.readGameBoardNames(world);
            for (String gameName : gameBoardNames)
            {
                addGameBoard(GameBoard.readGameBoard(world, gameName));
            }
        }
        Messenger.sendDebugMSG(new String[]{"Reading in gameBoards"}, GameBoardController.class);
    }

    public static boolean addState(final String gameName, final int stateID)
    {
        GameBoard gameBoard = games.get(gameName);
        boolean validGame = gameBoard != null;
        Messenger.sendDebugMSG(new String[]{gameName, " had the state ",
                Integer.toString(stateID), " added ",
                "successfully: ", Boolean.toString(validGame)}, GameBoardController.class);
        if (validGame)
        {
            return gameBoard.addState(stateID) && gameBoard.writeGameBoard();
        }
        return validGame;
    }

    public static boolean addCMDToState(final String gameName, final int stateID, final String cmd)
    {
        GameBoard gameBoard = games.get(gameName);
        boolean validGame = gameBoard != null;
        Messenger.sendDebugMSG(new String[]{gameName, " had the state cmd added to ", stateID + " added ",
                "successfully: ", Boolean.toString(validGame)}, GameBoardController.class);
        if (validGame)
        {
            return gameBoard.addCMD(stateID, cmd) && gameBoard.writeGameBoard();
        }
        return validGame;
    }

    public static boolean addAchievement(final String gameName, final AchievementType type,
                                         final int progress, final int goal)
    {
        GameBoard gameBoard = games.get(gameName);
        boolean validGame = gameBoard != null;
        Messenger.sendDebugMSG(new String[]{gameName, " has a achievement of ", type.toString(), " being added, " +
                "successfully: ", Boolean.toString(validGame)}, GameBoardController.class);
        if (validGame)
        {
            return gameBoard.addAchievement(type, progress, goal) && gameBoard.writeGameBoard();
        }
        return validGame;
    }

    public static boolean addCMDToAchievement(final String gameName, final String cmd,
                                              final AchievementType type, final int progress,
                                              final int goal)
    {
        GameBoard gameBoard = games.get(gameName);
        boolean validGame = gameBoard != null;
        Messenger.sendDebugMSG(new String[]{gameName, " has the achievement ",
                type.toString(), ":", " added ",
                "successfully: ", Boolean.toString(validGame)}, GameBoardController.class);
        if (validGame)
        {
            return gameBoard.addCMDToAchievement(cmd, type, progress, goal)
                    && gameBoard.writeGameBoard();
        }
        return validGame;
    }

    private static String configFilePath()
    { return CONFIG_FILE_PATH_ROOT; }

    private static String debugFilePath()
    { return configFilePath() + YAML_SEPTOR + DEBUG_FP_KEY; }

    private static boolean readDebug()
    {
        String pluginName = Main.getPluginName();
        FileBuffer fileBuffer = FileBufferController.instance(pluginName).getFile(
                                Main.FILE_NAME);
        fileBuffer.safeLoadFileNoFF();
        return fileBuffer.isSafePath(debugFilePath()) &&
                fileBuffer.file.getBoolean(debugFilePath());
    }

    private static boolean writeDebug()
    {
        String pluginName = Main.getPluginName();
        FileBuffer fileBuffer = FileBufferController.instance(pluginName).getFile(
                Main.FILE_NAME);
        fileBuffer.safeLoadFileNoFF();
        fileBuffer.file.set(debugFilePath(), debugMode);
        return fileBuffer.saveFiles();
    }

    public static boolean writeGameBoard(final GameBoard gameBoard)
    { return gameBoard.writeGameBoard(); }

    public static boolean removeGameBoard(final String gameName, final World world)
    {
        games.remove(gameName);
        return GameBoard.flushGameBoard(gameName, world);
    }

    public static boolean startGame(final String gameName, final Team team)
    {
        gameVote.remove(team.getName());
        GameBoard gameBoard = games.get(gameName);
        int teamSize = team.getPlayers().size();
        boolean validGame = gameBoard != null
                            && teamSize <= gameBoard.getMaxTeamSize()
                            && teamSize >= gameBoard.getMinTeamSize()
                            && gameBoard.getCurrentTeam() == null;
        Messenger.sendDebugMSG(new String[]{gameName, " is being started with ", team.getName(),
                               " success: ", Boolean.toString(validGame)},
                               GameBoardController.class);
        if (validGame)
        {
            gameBoard.setCurrentTeam(team);
            teamToGame.put(team.getName(), gameName);
            gameBoard.runStart();
        }
        return validGame;
    }

    public static boolean switchGameState(final String gameName, final int stateID)
    {
        GameBoard gameBoard = games.get(gameName);
        boolean validGame = gameBoard != null;
        Messenger.sendDebugMSG(new String[]{gameName, " is having its state switched to ",
                Integer.toString(stateID), ", success: ", Boolean.toString(validGame)},
                GameBoardController.class);
        if (validGame)
        {
            return gameBoard.runState(stateID);
        }
        return validGame;
    }

    public static void endGame(final String gameName)
    {
        Messenger.sendDebugMSG(new String[]{gameName, " is being ended."},
                GameBoardController.class);
        GameBoard gameBoard = games.get(gameName);
        if (gameBoard != null)
        {
            Team team = gameBoard.getCurrentTeam();
            if (team != null)
            {
                for (TeamPlayer teamPlayer : team.getPlayers())
                {
                    if (teamPlayer != null)
                    {
                        Player player = teamPlayer.getPlayer();
                        player.teleport(player.getWorld().getSpawnLocation());
                    }
                }
                teamToGame.remove(team.getName());
            }
            gameBoard.resetAchievements();
            gameBoard.setCurrentTeam(null);
            gameBoard.runEnd();
        }
    }

    public static void disableGameBoard()
    {
        endAllGames();
        writeDebug();
    }

    private static void endAllGames()
    {
        for (String gameName : games.keySet())
            endGame(gameName);
    }
}
