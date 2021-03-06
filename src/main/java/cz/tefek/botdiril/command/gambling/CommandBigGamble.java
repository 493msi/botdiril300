package cz.tefek.botdiril.command.gambling;

import java.math.BigDecimal;
import java.math.BigInteger;

import cz.tefek.botdiril.Botdiril;
import cz.tefek.botdiril.framework.command.CallObj;
import cz.tefek.botdiril.framework.command.Command;
import cz.tefek.botdiril.framework.command.CommandCategory;
import cz.tefek.botdiril.framework.command.invoke.CmdInvoke;
import cz.tefek.botdiril.framework.command.invoke.CmdPar;
import cz.tefek.botdiril.framework.command.invoke.CommandException;
import cz.tefek.botdiril.framework.command.invoke.ParType;
import cz.tefek.botdiril.framework.util.BigNumbers;
import cz.tefek.botdiril.framework.util.MR;
import cz.tefek.botdiril.userdata.item.Icons;
import cz.tefek.botdiril.userdata.stat.EnumStat;
import cz.tefek.botdiril.userdata.tempstat.Curser;
import cz.tefek.botdiril.userdata.tempstat.EnumBlessing;
import cz.tefek.botdiril.userdata.tempstat.EnumCurse;

@Command(value = "biggamble", aliases = { "gamblemega",
        "mega" }, category = CommandCategory.GAMBLING, description = "Gamble in " + Icons.MEGAKEK + " style. There is a dark secret though.", levelLock = 12)
public class CommandBigGamble
{
    @CmdInvoke
    public static void gamble(CallObj co, @CmdPar(value = "amount", type = ParType.AMOUNT_MEGA_KEKS) BigInteger amount)
    {
        if (amount.doubleValue() < 0 || amount.compareTo(BigInteger.ZERO) == 0 || amount.doubleValue() == Double.NEGATIVE_INFINITY)
        {
            throw new CommandException("You can't gamble negative or zero " + Icons.MEGAKEK + ".");
        }

        double roll = Math.pow(10, Botdiril.RDG.nextUniform(-1, 6));

        var dma = new BigDecimal(amount);

        var pow = dma.precision() - dma.scale() - 1;

        var chanceToLoseEverything = Math.pow(pow / 3500, 1.28) / 2.0 + 0.011;

        if (Curser.isBlessed(co, EnumBlessing.MEGAKEK_LOSS_IMMUNITY))
        {
            chanceToLoseEverything *= 0.6;
        }

        if (Curser.isCursed(co, EnumCurse.DOUBLE_CHANCE_TO_LOSE_MEGA))
        {
            chanceToLoseEverything *= 2;
        }

        if (Botdiril.RDG.nextUniform(0, 1) < chanceToLoseEverything)
        {
            MR.send(co.textChannel, String.format("**You lost every single %s.**", Icons.MEGAKEK));
            co.po.incrementLong(EnumStat.TIMES_LOST_ALL_MEGAKEKS.getName());
            co.ui.setMegaKeks(BigInteger.ZERO);
            return;
        }

        var mul = dma.multiply(new BigDecimal(roll));

        var diff = mul.subtract(dma).toBigInteger();

        co.ui.addMegaKeks(diff);

        if (diff.compareTo(BigInteger.ZERO) < 0)
        {
            MR.send(co.textChannel, String.format("You **lose** **%s** %s.", BigNumbers.stringifyBoth(diff.abs()), Icons.MEGAKEK));
        }
        else if (diff.compareTo(BigInteger.ZERO) > 0)
        {
            MR.send(co.textChannel, String.format("You **win** **%s** %s.", BigNumbers.stringifyBoth(diff), Icons.MEGAKEK));
        }
        else
        {
            MR.send(co.textChannel, "You neither win or lose... Somehow.");
        }
    }
}
