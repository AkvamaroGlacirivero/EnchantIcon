package mods.enchanticon;

import mods.enchanticon.enums.ApplyingScope;
import mods.enchanticon.enums.BackgroundType;
import mods.enchanticon.enums.LevelMarkType;

import java.util.function.Supplier;

public final class EnchantIconConfig {

    public static Supplier<BackgroundType> backgroundType = () -> BackgroundType.STICKY_NOTE;

    public static Supplier<LevelMarkType> levelMarkType = () -> LevelMarkType.DIGIT_COLORED;

    public static Supplier<ApplyingScope> guiScope = () -> ApplyingScope.ENCHANTED_BOOK_ONLY;

    public static Supplier<ApplyingScope> inHandScope = () -> ApplyingScope.ENCHANTED_BOOK_ONLY;
}
