package cz.tefek.botdiril.command.currency;

import java.util.function.Function;

import java.math.BigDecimal;
import java.math.BigInteger;

import cz.tefek.botdiril.framework.command.CallObj;
import cz.tefek.botdiril.framework.command.Command;
import cz.tefek.botdiril.framework.command.CommandCategory;
import cz.tefek.botdiril.framework.command.invoke.CmdInvoke;
import cz.tefek.botdiril.framework.util.BigNumbers;
import cz.tefek.botdiril.framework.util.CommandAssert;
import cz.tefek.botdiril.framework.util.MR;
import cz.tefek.botdiril.userdata.item.Icons;
import cz.tefek.botdiril.userdata.timers.Timers;
import cz.tefek.botdiril.userdata.xp.XPAdder;
import cz.tefek.botdiril.util.BotdirilFmt;

@Command(value = "payoutmegakeks", aliases = { "payoutmega",
        "bigpayout" }, category = CommandCategory.CURRENCY, description = "Pay out your " + Icons.MEGAKEK + " for some " + Icons.KEK, levelLock = 15)
public class CommandPayoutMegaKeks
{
    private static final Function<Double, Long> conversion = (pow) -> Math.round(Math.pow(Math.pow((pow + 80) / 1300, 3) * 1000, 3.5) - Math.pow(pow / 4 - 20, 2) + 100 * pow + 300);

    @CmdInvoke
    public static void payout(CallObj co)
    {
        var has = co.ui.getMegaKeks();
        CommandAssert.assertNotEquals(has, BigInteger.ZERO, "You can't pay out zero keks.");

        CommandAssert.assertTimer(co.ui, Timers.payout, "You need to wait **$** before paying out again.");

        CommandAssert.assertNotEquals(has, BigInteger.ZERO, "You can't pay out zero " + Icons.MEGAKEK + ".");

        var dma = new BigDecimal(has);

        var numStr = BigNumbers.stringifyBoth(has);

        var pow = dma.precision() - dma.scale() - 1;

        var gets = conversion.apply((double) pow);

        co.ui.setMegaKeks(BigInteger.ZERO);
        co.ui.addKeks(gets);

        var xp = 10 + pow * pow / 10;

        XPAdder.addXP(co, xp);

        MR.send(co.textChannel, String.format("Paid out **%s** %s for **%s** %s. **[+%s XP]**", numStr, Icons.MEGAKEK, BotdirilFmt.format(gets), Icons.KEK, BotdirilFmt.format(xp)));
    }
}
