package cz.tefek.botdiril.userdata.items.cardpack;

import cz.tefek.botdiril.framework.command.CallObj;
import cz.tefek.botdiril.framework.util.MR;
import cz.tefek.botdiril.userdata.card.Card;
import cz.tefek.botdiril.userdata.card.CardDrops;
import cz.tefek.botdiril.userdata.item.Icons;
import cz.tefek.botdiril.userdata.item.ShopEntries;
import cz.tefek.botdiril.userdata.pools.CardPools;
import cz.tefek.botdiril.userdata.stat.EnumStat;
import cz.tefek.botdiril.userdata.tempstat.Curser;
import cz.tefek.botdiril.userdata.tempstat.EnumCurse;
import cz.tefek.botdiril.util.BotdirilFmt;

public class ItemCardPackBasic extends ItemCardPack
{
    private static final int CONTENTS = 10;

    private static final int DISPLAY_LIMIT = 15;

    public ItemCardPackBasic()
    {
        super("basiccardpack", Icons.CARDPACK_BASIC, "Basic Card Pack");
        this.setDescription("Contains all the essential cards for your collections.");
        ShopEntries.addCoinSell(this, 1000);
    }

    @Override
    public void open(CallObj co, long amount)
    {
        var fm = String.format("You open %d %s and get the following cards:", amount, this.getIcon());
        var sb = new StringBuilder(fm);

        var cp = new CardDrops();

        for (int i = 0; i < CONTENTS * amount; i++)
        {
            if (Curser.isCursed(co, EnumCurse.CURSE_OF_YASUO))
            {
                cp.addItem(Card.getCardByName("yasuo"));
                continue;
            }

            cp.addItem((Card) CardPools.basicOrCommon.draw().draw(), 1);
        }

        var i = 0;

        for (var cardPair : cp)
        {
            var card = cardPair.getCard();
            var amt = cardPair.getAmount();

            co.ui.addCard(card, amt);

            if (i <= DISPLAY_LIMIT)
            {
                sb.append(String.format("\n%dx %s", amt, card.inlineDescription()));
            }

            i++;
        }

        var dc = cp.distintCount();

        if (dc > DISPLAY_LIMIT)
        {
            sb.append(String.format("\nand %d more different cards...", dc - DISPLAY_LIMIT));
        }

        var dustVal = cp.stream().mapToLong(cardPair -> ShopEntries.getDustForDisenchanting(cardPair.getCard()) * cardPair.getAmount()).sum();

        sb.append(String.format("\nTotal %s cards. Approximate value: %s%s", BotdirilFmt.format(cp.totalCount()), BotdirilFmt.format(dustVal), Icons.DUST));

        co.po.addLong(EnumStat.CARD_PACKS_OPENED.getName(), amount);

        MR.send(co.textChannel, sb.toString());
    }

}
