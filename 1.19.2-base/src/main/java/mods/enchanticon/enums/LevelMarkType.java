package mods.enchanticon.enums;

public enum LevelMarkType {

    DIGIT_COLORED("digit_colored"), DIGIT_MONOCHROME("digit_monochrome"),
    ROMAN_NUMERAL_COLORED("roman_colored"), ROMAN_NUMERAL_MONOCHROME("roman_monochrome"),
    VANILLA("vanilla"), COLOR_BLIND("color_blind");

    public final String type;

    LevelMarkType(String type) {
        this.type = type;
    }
}
