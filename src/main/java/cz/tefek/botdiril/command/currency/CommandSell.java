package cz.tefek.botdiril.command.currency;

import cz.tefek.botdiril.Botdiril;
import cz.tefek.botdiril.framework.command.CallObj;
import cz.tefek.botdiril.framework.command.Command;
import cz.tefek.botdiril.framework.command.CommandCategory;
import cz.tefek.botdiril.framework.command.invoke.CmdInvoke;
import cz.tefek.botdiril.framework.command.invoke.CmdPar;
import cz.tefek.botdiril.framework.command.invoke.CommandException;
import cz.tefek.botdiril.framework.command.invoke.ParType;
import cz.tefek.botdiril.framework.util.CommandAssert;
import cz.tefek.botdiril.framework.util.MR;
import cz.tefek.botdiril.userdata.IIdentifiable;
import cz.tefek.botdiril.userdata.card.Card;
import cz.tefek.botdiril.userdata.item.Icons;
import cz.tefek.botdiril.userdata.item.Item;
import cz.tefek.botdiril.userdata.item.Items;
import cz.tefek.botdiril.userdata.item.ShopEntries;
import cz.tefek.botdiril.userdata.preferences.EnumUserPreference;
import cz.tefek.botdiril.userdata.preferences.UserPreferences;
import cz.tefek.botdiril.userdata.tempstat.Curser;
import cz.tefek.botdiril.userdata.tempstat.EnumBlessing;
import cz.tefek.botdiril.userdata.tempstat.EnumCurse;
import cz.tefek.botdiril.util.BotdirilFmt;

@Command(value = "sell", aliases = {}, category = CommandCategory.CURRENCY, description = "Sell items for " + Icons.COIN + ".", levelLock = 2)
public class CommandSell
{
    public static final double CHANCE_TO_EXPLODE = 0.005;

    public static final long GOLDEDOIL_SELL_BOOST = 10;
    public static final long EXPLOSION_MODIFIER = 850;

    public static void sellRoutine(CallObj co, String articleName, long amountOfArticle, long totalMoney)
    {
        long resellModifier = 1000;

        long goldenOils = UserPreferences.isBitEnabled(co.po, EnumUserPreference.GOLDEN_OIL_DISABLED) ? 0
                : co.ui.howManyOf(Items.goldenOil);

        boolean exploded = false;

        if (Botdiril.RDG.nextUniform(0, 1, true) < goldenOils * CHANCE_TO_EXPLODE)
        {
            exploded = true;
            resellModifier = EXPLOSION_MODIFIER;
            co.ui.setItem(Items.goldenOil, 0);
        }
        else
        {
            resellModifier += GOLDEDOIL_SELL_BOOST * goldenOils;
        }

        var value = totalMoney;

        if (Curser.isCursed(co, EnumCurse.HALVED_SELL_VALUE))
        {
            value /= 2;
        }

        if (Curser.isBlessed(co, EnumBlessing.BETTER_SELL_PRICES))
        {
            value = value * 17 / 10;
        }

        long actualValue = value * resellModifier / 1000;

        co.ui.addCoins(actualValue);

        if (exploded)
        {
            var sb = new StringBuilder();
            sb.append("*:boom: Your **%d %s** barrels exploded, making you lose **%s** %s from this trade.*\n");
            sb.append("You sold **%s %s** for **%s** %s.");

            MR.send(co.textChannel, String.format(sb.toString(), goldenOils, Items.goldenOil.inlineDescription(), BotdirilFmt.format(value - actualValue), Icons.COIN, BotdirilFmt.format(amountOfArticle), articleName, BotdirilFmt.format(actualValue), Icons.COIN));
        }
        else
        {
            if (actualValue != value)
            {
                MR.send(co.textChannel, String.format("You sold **%s %s** for **%s** %s plus extra **%s** %s from your **%d %s** barrels.", BotdirilFmt.format(amountOfArticle), articleName, BotdirilFmt.format(value), Icons.COIN, BotdirilFmt.format(actualValue - value), Icons.COIN, goldenOils, Items.goldenOil.inlineDescription()));
            }
            else
            {
                MR.send(co.textChannel, String.format("You sold **%s %s** for **%s** %s.", BotdirilFmt.format(amountOfArticle), articleName, BotdirilFmt.format(value), Icons.COIN));
            }
        }
    }

    @CmdInvoke
    public static void sell(CallObj co, @CmdPar(value = "item or card", type = ParType.ITEM_OR_CARD) IIdentifiable item)
    {
        sell(co, item, 1);
    }

    @CmdInvoke
    public static void sell(CallObj co, @CmdPar(value = "item or card", type = ParType.ITEM_OR_CARD) IIdentifiable item, @CmdPar(value = "amount", type = ParType.AMOUNT_ITEM_OR_CARD) long amount)
    {
        CommandAssert.numberMoreThanZeroL(amount, "You can't sell zero items / cards.");

        if (!ShopEntries.canBeSold(item))
        {
            throw new CommandException("That item / card cannot be sold.");
        }

        if (item instanceof Item)
        {
            CommandAssert.numberNotAboveL(amount, co.ui.howManyOf((Item) item), "You don't have that many items of that type.");
            co.ui.addItem((Item) item, -amount);
        }
        else if (item instanceof Card)
        {
            CommandAssert.numberNotAboveL(amount, co.ui.howManyOf((Card) item), "You don't have that many items of that type.");
            co.ui.addCard((Card) item, -amount);
        }

        sellRoutine(co, item.inlineDescription(), amount, amount * ShopEntries.getSellValue(item));
    }
}
