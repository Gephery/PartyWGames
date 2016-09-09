package com.gmail.gephery.partywgames.controllers;

import com.gmail.gephery.partywgames.modules.GameBoardController;
import net.projectzombie.survivalteams.file.buffers.PlayerBuffer;
import net.projectzombie.survivalteams.team.Team;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 * @author Gephery
 */
public class PWGAchievementListener implements Listener
{
    @EventHandler
    public void onZombieDeath(EntityDeathEvent event)
    {
        if (event.getEntity() instanceof Zombie)
        {
            Player player = event.getEntity().getKiller();
            if (player != null)
            {
                Team team = PlayerBuffer.get(player.getUniqueId()).getTeam();
                String gameName = GameBoardController.getTeamCurrentGame(team.getName());
                GameBoardController
                        .incrementAchievements(gameName,
                                GameBoardController.AchievementType.ZOMBIE_KILL);
            }
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event)
    {

    }
}
