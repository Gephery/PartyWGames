package com.gmail.gephery.partywgames.modules;

import static com.gmail.gephery.partywgames.modules.GameBoardController.*;

import com.gmail.gephery.partywgames.main.Main;
import com.gmail.gephery.partywgames.modules.GameBoardController.AchievementType;
import com.gmail.gephery.partywgames.util.CMDKeyWordTools;
import com.gmail.gephery.partywgames.util.Messenger;
import com.gmail.gephery.teleport.file.FileBuffer;
import com.gmail.gephery.teleport.file.FileBufferController;
import net.projectzombie.survivalteams.file.WorldCoordinate;
import net.projectzombie.survivalteams.player.TeamPlayer;
import net.projectzombie.survivalteams.team.Team;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * @author Gephery
 */
class GameBoard
{
    private static final int DEFAULT_MAX_TEAM_SIZE = 4;
    private static final int DEFAULT_MIN_TEAM_SIZE = 1;
    private static final int START_STATE_ID = 0;
    private static final int END_STATE_ID = 1;
    private static final int STATE_STARTING_COUNT_ID = 2;
    private static final String GAMEBOARD_FILE_PATH_ROOT = "games";
    private static final String CURRENT_STATE_FP_KEY = "current";
    private static final String START_STATE_FP_KEY = "start";
    private static final String END_STATE_FP_KEY = "end";
    private static final String STATES_FP_KEY = "states";
    private static final String ACHIEVEMENTS_FP_KEY = "achievements";
    private static final String SIGN_LOCATION_FP_KEY = "sign";
    private static final String MAX_TEAM_SIZE_FP_KEY = "max";
    private static final String MIN_TEAM_SIZE_FP_KEY = "min";

    private String gameName;
    private int currentState;
    private GameState gameStart;
    private GameState gameEnd;
    private Team currentTeam;
    private Map<Integer, GameState> states;
    private Map<AchievementType, HashMap<String, GameAchievement>> achievements;
    private Location signLocation;
    private int maxTeamSize;
    private int minTeamSize;

    GameBoard(final String gameName, final Location signLocation)
    {
        this(gameName, START_STATE_ID, new GameState(START_STATE_ID),
                new GameState(END_STATE_ID), new HashMap<Integer, GameState>(),
                new HashMap<AchievementType, HashMap<String, GameAchievement>>(),
                signLocation, DEFAULT_MAX_TEAM_SIZE, DEFAULT_MIN_TEAM_SIZE);
    }

    GameBoard(final String gameName, final int currentState, final String gameStartS,
                        final String gameEndS, Collection<String> statesS,
                        final Collection<String> achievementsS, final Location signLocation,
                        final int maxTeamSize, final int minTeamSize)
    {
        this.gameName = gameName;
        this.currentState = currentState;
        this.gameStart = GameState.fromString(gameStartS);
        this.gameEnd = GameState.fromString(gameEndS);

        // Setting states
        this.states = new HashMap<Integer, GameState>();
        for (String stateS : statesS)
        {
            GameState gameState = GameState.fromString(stateS);
            this.states.put(gameState.getID(), gameState);
        }

        // Setting achievements
        this.achievements = new HashMap<AchievementType, HashMap<String, GameAchievement>>();
        for (String achievementS : achievementsS)
        {
            GameAchievement achievement = GameAchievement.fromString(achievementS);
            if (this.achievements.get(achievement.getType()) == null)
                this.achievements.put(achievement.getType(), new HashMap<String, GameAchievement>());
            this.achievements.get(achievement.getType()).put(achievement.toID(), achievement);
        }

        this.signLocation = signLocation;
        this.currentTeam = null;
        this.maxTeamSize = maxTeamSize;
        this.minTeamSize = minTeamSize;
    }

    private GameBoard(final String gameName, final int currentState,
                      final GameState gameStart, final GameState gameEnd,
                      final Map<Integer, GameState> states,
                      final Map<AchievementType, HashMap<String, GameAchievement>> achievements,
                      final Location signLocation, final int maxTeamSize, final int minTeamSize)
    {
        this.gameName = gameName;
        this.currentState = currentState;
        this.gameStart = gameStart;
        this.gameEnd = gameEnd;
        this.states = states;
        this.achievements = achievements;
        this.signLocation = signLocation;
        this.currentTeam = null;
        this.maxTeamSize = maxTeamSize;
        this.minTeamSize = minTeamSize;
    }

    final String getGameName()                    { return gameName; }
    final int getCurrentState()                   { return currentState; }
    final GameState getGameStart()                { return gameStart; }
    final GameState getGameEnd()                  { return gameEnd; }
    final GameState getState(final int iD)        { return states.get(iD); }
    final Location getSignLocation()              { return signLocation; }
    final Team getCurrentTeam()                   { return currentTeam; }
    final int getMaxTeamSize()                    { return maxTeamSize; }
    final int getMinTeamSize()                    { return minTeamSize; }

    void incrementAchievements(final AchievementType type)
    {
        for (GameAchievement achievement : getAchievements(type))
        {
            achievement.incrementProgress(1);
            if (achievement.getGoal() == achievement.getProgress())
            {
                runCMDS(achievement.getCompletionCMDs());
            }
        }
    }

    void resetAchievements()
    {
        for (HashMap<String, GameAchievement> achievementTypes: achievements.values())
        {
            for (GameAchievement gameAchievement : achievementTypes.values())
                gameAchievement.setProgress(0);
        }
    }

    void sendTeamMsg(final String msg)
    {
        if (currentTeam != null)
        {
            for (TeamPlayer teamPlayer : currentTeam.getPlayers())
            {
                if (teamPlayer != null)
                {
                    Player player = teamPlayer.getPlayer();
                    player.sendMessage(msg);
                }
            }
        }
    }

    void setCurrentTeam(final Team team)    { this.currentTeam = team; }

    final boolean addAchievement(final AchievementType type, final int progress, final int goal)
    {
        boolean validAchievement = goal > progress;
        if (validAchievement)
        {
            if (achievements.get(type) == null)
                achievements.put(type, new HashMap<String, GameAchievement>());
            GameAchievement gameAchievement = new GameAchievement(type, progress, goal);
            achievements.get(type).put(gameAchievement.toID(), gameAchievement);
        }
        return validAchievement;
    }

    final boolean addCMDToAchievement(final String cmd, final AchievementType type,
                                      final int progress, final int goal)
    {
        String achievementID = new GameAchievement(type, progress, goal).toID();
        boolean validAchievement = achievements.get(type) != null
                && achievements.get(type).get(achievementID) != null;
        if (validAchievement)
            achievements.get(type).get(achievementID).addCmd(cmd);
        return validAchievement;
    }

    final boolean addState(final int stateID)
    {
        boolean validStateID = stateID >= GameBoard.STATE_STARTING_COUNT_ID;
        if (validStateID)
            states.put(stateID, new GameState(stateID));
        return validStateID;
    }

    final boolean addCMD(final int stateID, final String cmd)
    {
        boolean validStateID = stateID == START_STATE_ID || stateID == END_STATE_ID
                                          || states.get(stateID) != null;
        if (validStateID)
        {
            if (stateID == START_STATE_ID)
                gameStart.addCmd(cmd);
            else if (stateID == END_STATE_ID)
                gameEnd.addCmd(cmd);
            else if (states.get(stateID) != null)
                states.get(stateID).addCmd(cmd);
        }
        return validStateID;
    }

    private List<String> getStatesAsStrings()
    {
        List<String> statesS = new ArrayList<String>();
        for (GameState gameState : states.values())
            statesS.add(gameState.toString());
        return statesS;
    }

    private List<String> getAchievementsAsStrings()
    {
        List<String> achievementsS = new ArrayList<String>();
        for (AchievementType type: achievements.keySet())
        {
            for (GameAchievement gameAchievement : getAchievements(type))
                achievementsS.add(gameAchievement.toString());
        }
        return achievementsS;
    }

    final Collection<GameAchievement> getAchievements(final AchievementType type)
    {
        return achievements.get(type) != null ? achievements.get(type).values() :
                new HashSet<GameAchievement>();
    }

    static Set<String> readGameBoardNames(final World world)
    {
        String pluginName = Main.getPluginName();
        FileBuffer fileBuffer = FileBufferController.instance(pluginName).getFile(world,
                Main.FILE_NAME);
        fileBuffer.safeLoadFile(world);
        return fileBuffer.isSafePath(GAMEBOARD_FILE_PATH_ROOT) ?
                fileBuffer.file.getConfigurationSection(GAMEBOARD_FILE_PATH_ROOT).getKeys(false) :
                new HashSet<String>();
    }

    static boolean flushGameBoard(final String gameName, final World world)
    {
        String pluginName = Main.getPluginName();
        FileBuffer fileBuffer = FileBufferController.instance(pluginName).getFile(world,
                Main.FILE_NAME);
        fileBuffer.safeLoadFile(world);
        fileBuffer.file.set(gameBoardFilePath(gameName), null);
        return fileBuffer.saveFiles();
    }

    static GameBoard readGameBoard(final World world, final String gameName)
    {
        String pluginName = Main.getPluginName();
        FileBuffer fileBuffer = FileBufferController.instance(pluginName).getFile(world,
                                                                                  Main.FILE_NAME);
        fileBuffer.safeLoadFile(world);
        int currentState = fileBuffer.file.getInt(currentStateFilePath(gameName));
        String gameStartS = fileBuffer.file.getString(startStateFilePath(gameName));
        String gameEndS = fileBuffer.file.getString(endStateFilePath(gameName));
        Collection<String> statesS = fileBuffer.file.getStringList(statesFilePath(gameName));
        Collection<String> achievementsS = fileBuffer
                                            .file.getStringList(achievementsFilePath(gameName));
        Location signLocation = WorldCoordinate
                                    .toLocation(fileBuffer
                                            .file
                                                .getString(signLocationFilePath(gameName)));
        int maxTeamSize = fileBuffer.file.getInt(maxTeamSizeFilePath(gameName));
        int minTeamSize = fileBuffer.file.getInt(minTeamSizeFilePath(gameName));
        return new GameBoard(gameName, currentState, gameStartS,
                             gameEndS, statesS, achievementsS, signLocation, maxTeamSize,
                             minTeamSize);
    }

    boolean writeGameBoard()
    {
        return writeGameBoard(this);
    }

    static boolean writeGameBoard(final GameBoard gameBoard)
    {
        String pluginName = Main.getPluginName();
        String gameName = gameBoard.getGameName();
        World world = gameBoard.getSignLocation().getWorld();
        FileBuffer fileBuffer = FileBufferController.instance(pluginName).getFile(world,
                Main.FILE_NAME);
        fileBuffer.safeLoadFile(world);
        fileBuffer.file.set(currentStateFilePath(gameName), gameBoard.getCurrentState());
        fileBuffer.file.set(startStateFilePath(gameName), gameBoard.getGameStart().toString());
        fileBuffer.file.set(endStateFilePath(gameName), gameBoard.getGameEnd().toString());
        fileBuffer.file.set(statesFilePath(gameName), gameBoard.getStatesAsStrings());
        fileBuffer.file.set(achievementsFilePath(gameName), gameBoard.getAchievementsAsStrings());
        fileBuffer.file.set(signLocationFilePath(gameName),
                            WorldCoordinate.toString(gameBoard.getSignLocation().getBlock()));
        fileBuffer.file.set(maxTeamSizeFilePath(gameName), gameBoard.getMaxTeamSize());
        fileBuffer.file.set(minTeamSizeFilePath(gameName), gameBoard.getMinTeamSize());
        return fileBuffer.saveFiles();
    }

    final String gameBoardFilePath()
    { return GAMEBOARD_FILE_PATH_ROOT + YAML_SEPTOR + getGameName(); }

    final String currentStateFilePath()
    { return gameBoardFilePath() + YAML_SEPTOR + CURRENT_STATE_FP_KEY; }

    final String startStateFilePath()
    { return gameBoardFilePath() + YAML_SEPTOR + START_STATE_FP_KEY; }

    final String endStateFilePath()
    { return gameBoardFilePath() + YAML_SEPTOR + END_STATE_FP_KEY; }

    final String statesFilePath()
    { return gameBoardFilePath() + YAML_SEPTOR + STATES_FP_KEY; }

    final String achievementsFilePath()
    { return gameBoardFilePath() + YAML_SEPTOR + ACHIEVEMENTS_FP_KEY; }

    final String signLocationFilePath()
    { return gameBoardFilePath() + YAML_SEPTOR + SIGN_LOCATION_FP_KEY; }

    final String maxTeamSizeFilePath()
    { return gameBoardFilePath() + YAML_SEPTOR + MAX_TEAM_SIZE_FP_KEY; }

    final String minTeamSizeFilePath()
    { return gameBoardFilePath() + YAML_SEPTOR + MIN_TEAM_SIZE_FP_KEY; }

    static String gameBoardFilePath(final String gameName)
    { return GAMEBOARD_FILE_PATH_ROOT + YAML_SEPTOR + gameName; }

    static String currentStateFilePath(final String gameName)
    { return gameBoardFilePath(gameName) + YAML_SEPTOR + CURRENT_STATE_FP_KEY; }

    static String startStateFilePath(final String gameName)
    { return gameBoardFilePath(gameName) + YAML_SEPTOR + START_STATE_FP_KEY; }

    static String endStateFilePath(final String gameName)
    { return gameBoardFilePath(gameName) + YAML_SEPTOR + END_STATE_FP_KEY; }

    static String statesFilePath(final String gameName)
    { return gameBoardFilePath(gameName) + YAML_SEPTOR + STATES_FP_KEY; }

    static String achievementsFilePath(final String gameName)
    { return gameBoardFilePath(gameName) + YAML_SEPTOR + ACHIEVEMENTS_FP_KEY; }

    static String signLocationFilePath(final String gameName)
    { return gameBoardFilePath(gameName) + YAML_SEPTOR + SIGN_LOCATION_FP_KEY; }

    static String maxTeamSizeFilePath(final String gameName)
    { return gameBoardFilePath(gameName) + YAML_SEPTOR + MAX_TEAM_SIZE_FP_KEY; }

    static String minTeamSizeFilePath(final String gameName)
    { return gameBoardFilePath(gameName) + YAML_SEPTOR + MIN_TEAM_SIZE_FP_KEY; }

    void runStart()
    { _runState(gameStart); }

    void runEnd()
    { _runState(gameEnd); }

    boolean runState(final int iD)
    {
        boolean runnableState = iD >= STATE_STARTING_COUNT_ID && states.containsKey(iD);
        if (runnableState)
            _runState(states.get(iD));
        return runnableState;
    }

    private void _runState(final GameState state)
    {
        currentState = state.getID();

        Set<String> cmds = state.getCMDs();
        runCMDS(cmds);

    }

    private void runCMDS(final Collection<String> cmds)
    {
        CommandSender sender = Main.getPlugin().getServer().getConsoleSender();
        for (String cmd : cmds)
        {
            if (currentTeam != null)
                cmd = CMDKeyWordTools.minecraftTeamFilter(cmd, currentTeam);
            cmd = CMDKeyWordTools.worldNameFilter(cmd, signLocation.getWorld());
            Messenger.sendDebugMSG(new String[]{"CMD being dispatched: ", cmd}, GameBoard.class);
            Main.getPlugin().getServer().dispatchCommand(sender, cmd);
        }
    }
}