package mods.enchanticon.enums;

/**
 * Represents the scope in which we should display enchantment icon on toggled.
 */
public enum ApplyingScope {

    /**
     * Represents that we should display enchantment icon for all enchanted items.
     */
    ALL,

    /**
     * Represents that we should display enchantment icon for vanilla enchanted book only.
     */
    ENCHANTED_BOOK_ONLY,

    /**
     * Represents that we should display enchantment icon for nothing (i.e. totally disable).
     */
    NONE;
}
