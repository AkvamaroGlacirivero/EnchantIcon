package mods.enchanticon;

import mods.enchanticon.enums.ApplyingScope;
import mods.enchanticon.enums.BackgroundType;
import mods.enchanticon.enums.LevelMarkType;

public class EnchantIconConfig {

    public static EnchantIconConfig instance = new EnchantIconConfig();

    public BackgroundType backgroundType = BackgroundType.STICKY_NOTE;

    public LevelMarkType levelMarkType = LevelMarkType.DIGIT_COLORED;

    public ApplyingScope guiScope = ApplyingScope.ENCHANTED_BOOK_ONLY;

    public ApplyingScope inHandScope = ApplyingScope.ENCHANTED_BOOK_ONLY;
}
