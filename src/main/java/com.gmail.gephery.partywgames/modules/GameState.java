package com.gmail.gephery.partywgames.modules;

import static com.gmail.gephery.partywgames.modules.GameBoardController.STRING_SEPARATOR;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * GamesStates represent states a GameBoard can be in.
 */
class GameState
{
    public static final int INVALID_STATE_ID = -1;

    private int id;
    private Set<String> cmds;

    protected GameState(final int id)
    {
        this.id = id;
        this.cmds = new HashSet<String>();
    }

    protected GameState(final int id, final Collection<String> cmds)
    {
        this(id);
        this.cmds.addAll(cmds);
    }

    protected final int getID()             { return this.id; }
    protected final Set<String> getCMDs()   { return this.cmds; }

    protected void addCmd(final String cmd) { this.cmds.add(cmd); }
    protected void setID(final int nID)     { this.id = nID; }

    public final String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(this.id);
        for (String cmd : this.cmds)
        {
            builder.append(STRING_SEPARATOR);
            builder.append(cmd);
        }
        return builder.toString();
    }

    protected static GameState fromString(final String gs)
    {
        String[] gsParts = gs.split(STRING_SEPARATOR);
        if (gsParts.length >= 1)
        {
            int id = Integer.valueOf(gsParts[0]);
            if (gsParts.length > 1)
            {
                List<String> cmds = new ArrayList<String>(gsParts.length - 1);
                for (int i = 1; i < gsParts.length; i++)
                    cmds.add(gsParts[i]);
                return new GameState(id, cmds);
            }
            return new GameState(id);
        }
        return new GameState(INVALID_STATE_ID);
    }

}
