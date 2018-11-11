package cz.tefek.botdiril.command.currency;

import cz.tefek.botdiril.framework.command.CallObj;
import cz.tefek.botdiril.framework.command.Command;
import cz.tefek.botdiril.framework.command.CommandCategory;
import cz.tefek.botdiril.framework.command.invoke.CmdInvoke;
import cz.tefek.botdiril.framework.command.invoke.CmdPar;
import cz.tefek.botdiril.framework.command.invoke.ParType;
import cz.tefek.botdiril.framework.util.CommandAssert;
import cz.tefek.botdiril.framework.util.MR;
import cz.tefek.botdiril.userdata.items.Icons;
import cz.tefek.botdiril.userdata.timers.Timers;
import cz.tefek.botdiril.userdata.xp.XPAdder;

@Command(value = "payoutkeks", aliases = {
        "payout" }, category = CommandCategory.CURRENCY, description = "Pay out your " + Icons.KEK + " for some " + Icons.TOKEN, levelLock = 7)
public class CommandPayoutKeks
{
    private static final long conversionRate = 125;

    @CmdInvoke
    public static void payout(CallObj co, @CmdPar(value = "how many", type = ParType.AMOUNT_CLASSIC_KEKS) long keks)
    {
        CommandAssert.numberMoreThanZeroL(keks, "You can't pay out zero keks.");

        CommandAssert.assertTimer(co.ui, Timers.payout, "You need to wait **$** before paying out again.");

        co.ui.addKeks(-keks);
        var tokens = keks / conversionRate;
        co.ui.addKekTokens(tokens);
        var xp = Math.round(Math.pow(10, Math.sqrt(Math.log10(keks))));
        XPAdder.addXP(co, xp);

        MR.send(co.textChannel, String.format("Paid out **%d** %s for **%d** %s at a conversion rate of **%d:1**. **[+%d XP]**", keks, Icons.KEK, tokens, Icons.TOKEN, conversionRate, xp));
    }
}