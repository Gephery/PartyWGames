package com.gmail.gephery.partywgames.controllers;

import com.gmail.gephery.partywgames.modules.GameBoardController;
import com.gmail.gephery.partywgames.util.Messenger;
import net.projectzombie.survivalteams.file.buffers.PlayerBuffer;
import net.projectzombie.survivalteams.file.buffers.TeamBuffer;
import net.projectzombie.survivalteams.player.TeamPlayer;
import net.projectzombie.survivalteams.team.Team;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Gephery
 */
public class PWGListeners implements Listener
{
    private static Set<UUID> playersInLimbo = new HashSet<>();

    @EventHandler
    public void onSignCreate(SignChangeEvent event)
    {
        if (event.getPlayer().hasPermission("PWG.sign") && event.getLine(0).equals("(Lobby)")) {
            event.setLine(0, ChatColor.LIGHT_PURPLE + event.getLine(0));

            Location signLocation = event.getBlock().getLocation();
            String gameName = event.getLine(1);
            GameBoardController.createGameBoard(gameName, signLocation);
        }
    }

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event)
    {
        if (event.getPlayer().hasPermission("PWG.signuse"))
        {
            Player player = event.getPlayer();
            Set<Material> mat = null;
            BlockState block = player.getTargetBlock(mat, 5).getState();
            if (block instanceof Sign)
            {
                Sign sign = (Sign) block;
                if (ChatColor.stripColor((sign.getLine(0))).equals("(Lobby)"))
                {
                    String gameName = sign.getLine(1);
                    if (GameBoardController.isValidGame(gameName))
                    {
                        if (GameBoardController.isGameOpen(gameName))
                        {
                            Team team = PlayerBuffer.get(player.getUniqueId()).getTeam();
                            GameBoardController.createVote(team, player, gameName);
                            Messenger.msgTeamFromServer(team,
                                                        new String[]{"To vote for ", gameName,
                                                        " please use /game vote <t:f>"});
                        }
                        else
                            player.sendMessage(PWGText.formatForPluginMsging("A game is currently" +
                                                                         " in session."));
                    }
                }
            }

        }
    }

    @EventHandler
    public void onPlayerDisconnect(PlayerQuitEvent event)
    {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Team team = PlayerBuffer.get(player.getUniqueId()).getTeam();
        Messenger.sendDebugMSG(new String[]{player.getDisplayName(),
                               " in ", team.getName(),
                               " has left ", GameBoardController.getTeamCurrentGame(team.getName())},
                               PWGListeners.class);
        if (GameBoardController.getTeamCurrentGame(team.getName()) != null)
        {
            playersInLimbo.add(player.getUniqueId());
            Messenger.sendDebugMSG(new String[]{player.getDisplayName(),
                            " in ", team.getName(),
                            " is in limbo."}, PWGListeners.class);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event)
    {
        Player player = event.getPlayer();
        World world = player.getWorld();
        Team team = PlayerBuffer.get(player.getUniqueId()).getTeam();
        Messenger.sendDebugMSG(new String[]{player.getDisplayName(),
                        " in ", team.getName(),
                        " has joined server into ",
                        GameBoardController.getTeamCurrentGame(team.getName())},
                        PWGListeners.class);
        if (playersInLimbo.contains(player.getUniqueId())
            && GameBoardController.getTeamCurrentGame(team.getName()) == null)
        {
            player.teleport(world.getSpawnLocation());
            Messenger.sendDebugMSG(new String[]{player.getDisplayName(),
                    " in ", team.getName(),
                    " is at spawn."}, PWGListeners.class);
        }
        playersInLimbo.remove(player.getUniqueId());
    }
}
