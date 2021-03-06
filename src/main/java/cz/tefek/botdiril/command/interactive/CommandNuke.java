package cz.tefek.botdiril.command.interactive;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;

import java.text.MessageFormat;

import cz.tefek.botdiril.BotMain;
import cz.tefek.botdiril.Botdiril;
import cz.tefek.botdiril.framework.command.CallObj;
import cz.tefek.botdiril.framework.command.Command;
import cz.tefek.botdiril.framework.command.CommandCategory;
import cz.tefek.botdiril.framework.command.invoke.CmdInvoke;
import cz.tefek.botdiril.framework.command.invoke.CmdPar;
import cz.tefek.botdiril.framework.util.CommandAssert;
import cz.tefek.botdiril.framework.util.MR;
import cz.tefek.botdiril.userdata.UserInventory;
import cz.tefek.botdiril.userdata.item.Icons;
import cz.tefek.botdiril.userdata.item.Items;
import cz.tefek.botdiril.userdata.properties.PropertyObject;
import cz.tefek.botdiril.userdata.stat.EnumStat;
import cz.tefek.botdiril.userdata.tempstat.Curser;
import cz.tefek.botdiril.userdata.tempstat.EnumBlessing;
import cz.tefek.botdiril.userdata.timers.Timers;
import cz.tefek.botdiril.util.BotdirilFmt;

@Command(value = "nuke", aliases = {}, category = CommandCategory.INTERACTIVE, description = "Literally nuke someone's keks.", levelLock = 30)
public class CommandNuke
{
    private static final long uraniumNeeded = 200;
    private static final long toolboxesNeeded = 3;
    private static final long purpleGemsNeeded = 10;

    @CmdInvoke
    public static void nuke(CallObj co, @CmdPar("user") Member member)
    {
        BotMain.sql.lock();

        try
        {
            var userInv = new UserInventory(member.getUser().getIdLong());
            var uranium = co.ui.howManyOf(Items.uranium);
            var toolboxes = co.ui.howManyOf(Items.toolBox);
            var purpleGems = co.ui.howManyOf(Items.redGem);

            if (userInv.getKeks() < 100_000)
            {
                MR.send(co.textChannel, MessageFormat.format("This user is not worth nuking, try someone else with more {0}.", Icons.KEK));
                return;
            }

            if (uranium < uraniumNeeded || toolboxes < toolboxesNeeded || purpleGems < purpleGemsNeeded)
            {
                var resp = String.format("You need **%s %s**, **%s %s** and **%s %s** to do this.", BotdirilFmt.format(uraniumNeeded), Items.uranium.inlineDescription(), BotdirilFmt.format(toolboxesNeeded), Items.toolBox.inlineDescription(), BotdirilFmt.format(purpleGemsNeeded), Items.purpleGem.inlineDescription());
                MR.send(co.textChannel, resp);
                return;
            }

            CommandAssert.assertTimer(co.ui, Timers.steal, "You need to wait **$** before trying to **nuke** someone again (this command shares its cooldown with *steal*).");

            try (var po = new PropertyObject(member.getUser().getIdLong()))
            {
                if (Curser.isBlessed(po, EnumBlessing.NUKE_IMMUNE))
                {
                    co.ui.resetTimer(Timers.steal);

                    var eb = new EmbedBuilder();
                    eb.setTitle("Nuke");
                    eb.setThumbnail(co.bot.getEffectiveAvatarUrl());
                    eb.setColor(0x008080);
                    eb.setDescription("That person is immune. For some reason.");
                    MR.send(co.textChannel, eb.build());

                    return;
                }
            }

            co.ui.addItem(Items.uranium, -uraniumNeeded);
            co.ui.addItem(Items.toolBox, -toolboxesNeeded);
            co.ui.addItem(Items.purpleGem, -purpleGemsNeeded);

            if (Botdiril.RDG.nextUniform(0, 1) > 0.4)
            {
                MR.send(co.textChannel, "**You missed.** " + Icons.KEK);
                return;
            }

            var keks = Math.min(userInv.getKeks(), (co.ui.getKeks() + 1000) * 100);
            var lost = Math.round(Botdiril.RDG.nextUniform(0.01, 0.15) * keks);
            var stolen = Math.round(Botdiril.RDG.nextUniform(0, 0.02) * keks);

            userInv.addKeks(-(lost + stolen));
            co.ui.addKeks(stolen);

            try (var otherProps = new PropertyObject(member.getUser().getIdLong()))
            {
                co.po.incrementLong(EnumStat.TIMES_NUKED.getName());
            }

            if (co.po.getLongOrDefault(EnumStat.BIGGEST_NUKE.getName(), 0) < lost + stolen)
            {
                co.po.setLong(EnumStat.BIGGEST_NUKE.getName(), lost + stolen);
            }

            var eb = new EmbedBuilder();
            eb.setTitle("BOOM!");
            eb.setColor(0x008080);
            eb.setDescription("You nuked " + member.getAsMention() + "'s " + Icons.KEK + ".");
            eb.addField("Destroyed", String.format("**%s** %s", BotdirilFmt.format(lost), Icons.KEK), false);
            eb.addField("Stolen", String.format("**%s** %s", BotdirilFmt.format(stolen), Icons.KEK), false);

            MR.send(co.textChannel, eb.build());
        }
        finally
        {
            BotMain.sql.unlock();
        }
    }
}
