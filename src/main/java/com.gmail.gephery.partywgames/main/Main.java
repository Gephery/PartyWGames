package com.gmail.gephery.partywgames.main;

import static com.gmail.gephery.partywgames.controllers.PWGText.*;
import com.gmail.gephery.partywgames.controllers.PWGAchievementListener;
import com.gmail.gephery.partywgames.controllers.PWGCommands;
import com.gmail.gephery.partywgames.controllers.PWGListeners;
import com.gmail.gephery.partywgames.modules.GameBoardController;
import com.gmail.gephery.teleport.file.FileBufferController;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created by maxgr on 8/27/2016.
 */
public class Main extends JavaPlugin
{
    public static final String FILE_NAME = "pwg.yaml";

    private static JavaPlugin PLUGIN;
    private static String PLUGIN_NAME;

    public static JavaPlugin getPlugin() { return PLUGIN; }
    public static String getPluginName() { return PLUGIN_NAME; }

    @Override
    public void onEnable()
    {
        PLUGIN = this;
        PLUGIN_NAME = this.getName();

        FileBufferController.init(this);
        GameBoardController.init();

        this.getServer().getPluginManager().registerEvents(new PWGListeners(), this);
        this.getServer().getPluginManager().registerEvents(new PWGAchievementListener(), this);

        this.getCommand("game").setExecutor(new PWGCommands());

        // Permissions
        String[] perms = {ADD_STATE_PERM, ADD_STATE_CMD_PERM, ADD_ACHIEVEMENT_PERM,
                          ADD_ACHIEVEMENT_CMD_PERM, VOTE_PERM, SWTICH_PERM, END_STATE_PERM};

        // Adding the permissions
        PluginManager pM = getServer().getPluginManager();
        for (String perm : perms)
            pM.addPermission(new Permission(perm));
    }

    @Override
    public void onDisable()
    {
        GameBoardController.disableGameBoard();
    }
}
