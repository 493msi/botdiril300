package cz.tefek.botdiril.framework.util;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

import cz.tefek.botdiril.framework.command.CallObj;
import cz.tefek.botdiril.internal.BotdirilConfig;

public class BH
{
    public static Member member(Guild g, User u)
    {
        return g.getMember(u);
    }

    public static User userByID(JDA jda, long u)
    {
        return jda.getUserById(u);
    }

    public static boolean findPrefix(Guild g, CallObj obj)
    {
        var prefix = obj.sc.getPrefix();
        var defaultPrefix = obj.contents.toLowerCase().startsWith(BotdirilConfig.UNIVERSAL_PREFIX.toLowerCase());

        if (prefix != null)
        {
            if (prefix.isEmpty() && defaultPrefix)
            {
                obj.contents = obj.contents.substring(BotdirilConfig.UNIVERSAL_PREFIX.length());

                return true;
            }
        }

        if (prefix != null)
        {
            if (obj.contents.toLowerCase().startsWith(prefix.toLowerCase()))
            {
                obj.contents = obj.contents.substring(prefix.length());

                return true;
            }
        }

        if (defaultPrefix)
        {
            obj.contents = obj.contents.substring(BotdirilConfig.UNIVERSAL_PREFIX.length());

            return true;
        }

        return false;
    }
}
