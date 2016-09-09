package com.gmail.gephery.partywgames.modules;

import static com.gmail.gephery.partywgames.modules.GameBoardController.STRING_SEPARATOR;
import com.gmail.gephery.partywgames.modules.GameBoardController.AchievementType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Gephery
 */
public class GameAchievement
{

    private GameBoardController.AchievementType type;
    private int progress;
    private int goal;
    private Set<String> completionCMDs;

    protected GameAchievement(final AchievementType type, final int progress, final int goal)
    {
        this.type = type;
        this.progress = progress;
        this.goal = goal;
        completionCMDs = new HashSet<String>();
    }

    protected GameAchievement(final AchievementType type, final int progress, final int goal,
                              final Collection<String> cmds)
    {
        this(type, progress, goal);
        this.completionCMDs.addAll(cmds);
    }

    final AchievementType getType()           { return this.type; }
    final int getProgress()                   { return this.progress; }
    final int getGoal()                       { return goal; }
    final Set<String> getCompletionCMDs()     { return this.completionCMDs; }

    void addCmd(final String cmd)             { this.completionCMDs.add(cmd); }
    void setProgress(final int progress)      { this.progress = progress; }
    void incrementProgress(final int i)       { this.progress += i; }
    void setGoal(final int goal)              { this.goal = goal; }
    void incrementGoal(final int i)           { this.goal += i; }
    void setType(final AchievementType type)  { this.type = type; }

    public final String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(this.type);
        builder.append(STRING_SEPARATOR);

        builder.append(this.progress);
        builder.append(STRING_SEPARATOR);

        builder.append(this.goal);

        for (String cmd : this.completionCMDs)
        {
            builder.append(STRING_SEPARATOR);
            builder.append(cmd);
        }

        return builder.toString();
    }

    protected final String toID()
    {
        StringBuilder builder = new StringBuilder();

        builder.append(this.type);
        builder.append(STRING_SEPARATOR);

        builder.append(this.progress);
        builder.append(STRING_SEPARATOR);

        builder.append(this.goal);

        return builder.toString();
    }

    protected static GameAchievement fromString(final String ga)
    {
        String[] gaParts = ga.split(STRING_SEPARATOR);
        if (gaParts.length >= 3)
        {
            AchievementType type = AchievementType.valueOf(gaParts[0]);
            int progress = Integer.valueOf(gaParts[1]);
            int goal = Integer.valueOf(gaParts[2]);
            if (gaParts.length > 3)
            {
                List<String> cmds = new ArrayList<String>(gaParts.length - 3);
                for (int i = 3; i < gaParts.length; i++)
                    cmds.add(gaParts[i]);
                return new GameAchievement(type, progress, goal, cmds);
            }
            return new GameAchievement(type, progress, goal);
        }
        return new GameAchievement(AchievementType.INVALID, 0, 0);
    }
}
