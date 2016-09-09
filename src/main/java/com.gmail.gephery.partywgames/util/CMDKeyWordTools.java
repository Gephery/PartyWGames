package com.gmail.gephery.partywgames.util;

import net.projectzombie.survivalteams.team.Team;
import org.bukkit.World;
import org.bukkit.entity.Player;

/**
 * @author Gephery
 */
public class CMDKeyWordTools
{
    public static String playerFilter(String cmd, final Player player)
    {
        while (cmd.contains("player")) {
            cmd = cmd.replace("player", player.getName());
        }
        return cmd;
    }

    public static String worldNameFilter(String cmd, final World world)
    {
        while (cmd.contains("world")) {
            cmd = cmd.replace("world", world.getName());
        }
        return cmd;
    }

    public static String worldUUIDFilter(String cmd, final World world)
    {
        while (cmd.contains("world")) {
            cmd = cmd.replace("world", world.getUID().toString());
        }
        return cmd;
    }

    public static String minecraftTeamFilter(String cmd, final Team team)
    {
        while (cmd.contains("team")) {
            cmd = cmd.replace("team", team.getName());
        }
        return cmd;
    }

    public static String runAllFilters(String cmd, final Team team, final Player player)
    {
        cmd = playerFilter(cmd, player);
        cmd = worldNameFilter(cmd, player.getWorld());
        cmd = minecraftTeamFilter(cmd, team);
        return cmd;
    }
}
