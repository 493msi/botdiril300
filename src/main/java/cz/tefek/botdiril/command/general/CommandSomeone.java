package cz.tefek.botdiril.command.general;

import java.util.stream.Collectors;

import net.dv8tion.jda.api.EmbedBuilder;

import cz.tefek.botdiril.Botdiril;
import cz.tefek.botdiril.framework.command.CallObj;
import cz.tefek.botdiril.framework.command.Command;
import cz.tefek.botdiril.framework.command.CommandCategory;
import cz.tefek.botdiril.framework.command.invoke.CmdInvoke;
import cz.tefek.botdiril.framework.util.MR;

@Command(value = "someone", category = CommandCategory.GENERAL, aliases = {
        "randomuser" }, description = "Gets a random user present in this channel, excludes bots.")
public class CommandSomeone
{
    @CmdInvoke
    public static void choose(CallObj co)
    {
        var memberList = co.textChannel.getMembers().stream().filter(m -> !m.getUser().isBot()).collect(Collectors.toList());
        var member = memberList.get(Botdiril.RANDOM.nextInt(memberList.size()));

        var eb = new EmbedBuilder();
        eb.setTitle(co.callerMember.getEffectiveName() + ", here is the user you rolled:");
        eb.setDescription(member.getAsMention());
        eb.setColor(0x008080);
        eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());

        MR.send(co.textChannel, eb.build());
    }
}
