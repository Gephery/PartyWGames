package com.gmail.gephery.partywgames.modules;

import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * @author Gephery
 */
public class Voter
{
    private int voters;
    private int pro;
    private Set<UUID> voted;
    private String gameName;

    public final String getVotedGame()  { return gameName; }
    public final int getVoters()        { return voters; }
    public final boolean isVoteOver()   { return voters == 0; }
    public final boolean isVotePro()    { return voters == 0 && pro == voted.size(); }

    public Voter(final int voters, final String gameName)
    {
        this.voters = voters;
        this.gameName = gameName;
        this.pro = 0;
        this.voted = new HashSet<UUID>();
    }

    public boolean vote(final boolean choice, final Player player)
    {
        if (!this.voted.contains(player.getUniqueId()))
        {
            this.voters--;
            if (choice)
                pro++;
            voted.add(player.getUniqueId());
            return true;
        }
        return false;
    }
}
