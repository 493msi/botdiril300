package cz.tefek.botdiril.command.gambling;

import cz.tefek.botdiril.BotMain;
import cz.tefek.botdiril.Botdiril;
import cz.tefek.botdiril.framework.command.CallObj;
import cz.tefek.botdiril.framework.command.Command;
import cz.tefek.botdiril.framework.command.CommandCategory;
import cz.tefek.botdiril.framework.command.invoke.CmdInvoke;
import cz.tefek.botdiril.framework.command.invoke.CmdPar;
import cz.tefek.botdiril.framework.command.invoke.ParType;
import cz.tefek.botdiril.framework.util.CommandAssert;
import cz.tefek.botdiril.framework.util.MR;
import cz.tefek.botdiril.internal.GlobalProperties;
import cz.tefek.botdiril.userdata.random.GambleEngine;
import cz.tefek.botdiril.userdata.timers.Timers;
import cz.tefek.botdiril.userdata.xp.XPAdder;
import cz.tefek.botdiril.userdata.xp.XPRewards;

@Command(value = "gamble", aliases = {}, category = CommandCategory.GAMBLING, levelLock = 5, description = "The good old gamble.")
public class CommandGamble
{
    @CmdInvoke
    public static void gamble(CallObj co, @CmdPar(value = "amount of keks", type = ParType.AMOUNT_CLASSIC_KEKS) long keks)
    {
        CommandAssert.numberMoreThanZeroL(keks, "You can't gamble zero keks...");

        BotMain.sql.lock();

        if (co.ui.useTimer(Timers.gambleXP) == -1)
        {
            var lvl = co.ui.getLevel();
            XPAdder.addXP(co, Math.round(Math.min(Math.sqrt(keks + 100), XPRewards.getXPAtLevel(lvl) * 0.001) * XPRewards.getLevel(lvl).getGambleFalloff()));
        }

        var outcome = GambleEngine.roll(co.caller.getIdLong());
        var lvl = co.ui.getLevel();
        var result = outcome.getApplier().apply(lvl, keks);

        co.ui.addKeks(result);

        if (result < 0)
        {
            result = -result;

            GlobalProperties.add(GlobalProperties.JACKPOT, Math.round(result * 0.6));
            GlobalProperties.add(GlobalProperties.JACKPOT_RESET, Math.round(result * 0.1));
        }

        MR.send(co.textChannel, String.format(outcome.getText(), result));

        BotMain.sql.unlock();
    }
}