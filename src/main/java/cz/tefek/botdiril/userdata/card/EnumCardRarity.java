package cz.tefek.botdiril.userdata.card;

import cz.tefek.botdiril.userdata.item.Icons;

public enum EnumCardRarity
{
    BASIC("Basic", 1, 100, 1.8, Icons.CARD_BASIC),
    COMMON("Common", 2, 400, 1.9, Icons.CARD_COMMON),
    RARE("Rare", 3, 1500, 1.85, Icons.CARD_RARE),
    LEGACY("Legacy", 4, 1200, 1.95, Icons.CARD_LEGACY),
    LEGENDARY("Legendary", 5, 8000, 1.75, Icons.CARD_LEGENDARY),
    LEGACY_LEGENDARY("Legacy Legendary", 6, 16000, 1.7, Icons.CARD_LEGACYLEGENDARY),
    ULTIMATE("Ultimate", 7, 40000, 1.585, Icons.CARD_ULTIMATE),
    LIMITED("Limited", 8, 64000, 1.57, Icons.CARD_LIMITED),
    MYTHIC("Mythic", 9, 80000, 1.525, Icons.CARD_MYTHIC),
    UNIQUE("Unique", 10, 150000, 1.49, Icons.CARD_UNIQUE);

    public static EnumCardRarity getByLevel(int lvl)
    {
        var vals = values();

        for (EnumCardRarity val : vals)
        {
            if (lvl == val.level)
                return val;
        }

        return EnumCardRarity.BASIC;
    }

    private int level;
    private long basePrice;
    private double levelPriceIncrease;
    private String cardIcon;

    private String rarityName;

    private EnumCardRarity(String rarityName, int level, long basePrice, double levelPriceIncrease, String cardIcon)
    {
        this.rarityName = rarityName;
        this.level = level;
        this.basePrice = basePrice;
        this.levelPriceIncrease = levelPriceIncrease;
        this.cardIcon = cardIcon;
    }

    public long getBasePrice()
    {
        return this.basePrice;
    }

    public String getCardIcon()
    {
        return this.cardIcon;
    }

    public int getLevel()
    {
        return this.level;
    }

    public double getLevelPriceIncrease()
    {
        return this.levelPriceIncrease;
    }

    public String getRarityName()
    {
        return this.rarityName;
    }
}
