/*
package com.krazzzzymonkey.catalyst.command;

import baritone.api.BaritoneAPI;
import baritone.api.pathing.goals.Goal;
import baritone.api.pathing.goals.GoalBlock;
import baritone.api.pathing.goals.GoalXZ;
import com.krazzzzymonkey.catalyst.utils.visual.ChatUtils;
import org.spongepowered.asm.mixin.injection.invoke.arg.ArgumentCountException;

public class Goto extends Command {

    public Goto() {
        super("goto");
    }

    @Override
    public void runCommand(String s, String[] args) {
        if (BaritoneAPI.getProvider().getPrimaryBaritone() == null) {
            ChatUtils.error("No Baritone API found!");
            return;
        }
        Goal goal;
        try {
            if (args.length == 2) {
                goal = new GoalXZ(Integer.parseInt(args[0]), Integer.parseInt(args[1]));
                ChatUtils.message("Set goal, X: " + Integer.parseInt(args[0]) + " Z: " + Integer.parseInt(args[1]));
            } else if (args.length == 3) {
                goal = new GoalBlock(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                ChatUtils.message("Set goal: X: " + Integer.parseInt(args[0]) + " Y: " + Integer.parseInt(args[1]) + " Z: " + Integer.parseInt(args[2]));
            } */
/*else if (args.length == 4) {
				goal = new GoalNear(new BlockPos(Integer.parseInt(args[0]), Integer.parseInt(args[1]), Integer.parseInt(args[2])), Integer.parseInt(args[3]));
			} *//*
 else if (args.length < 2) {
                throw new ArgumentCountException(args.length, 2, "");
            } else {
                throw new ArgumentCountException(args.length, 4, "");
            }

        } catch (NumberFormatException | ArgumentCountException e) {
            ChatUtils.error(getSyntax());
            return;
        }
        try {
            BaritoneAPI.getProvider().getPrimaryBaritone().getPathingBehavior().cancelEverything();
            BaritoneAPI.getProvider().getPrimaryBaritone().getCustomGoalProcess().setGoalAndPath(goal);
        } catch (NoClassDefFoundError e) {
            ChatUtils.error("No Baritone API found!");
        }

    }

    @Override
    public String getDescription() {
        return "Go to a coordinate or block";
    }

    @Override
    public String getSyntax() {
        return "goto <x,z/x,y,z";
    }

}
*/
