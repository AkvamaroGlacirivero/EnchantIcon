package mods.enchanticon.neoforge;

import mods.enchanticon.EnchantIconConfig;
import mods.enchanticon.enums.ApplyingScope;
import mods.enchanticon.enums.BackgroundType;
import mods.enchanticon.enums.LevelMarkType;
import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfigImpl {

    public static ModConfigSpec configSpec;

    public static ModConfigSpec.EnumValue<BackgroundType> backgroundTypeProp;
    public static ModConfigSpec.EnumValue<LevelMarkType> levelTypeProp;
    public static ModConfigSpec.EnumValue<ApplyingScope> applyingScopeInGuiProp;
    public static ModConfigSpec.EnumValue<ApplyingScope> applyingScopeInHandProp;

    public static ModConfigSpec init(ModConfigSpec.Builder builder) {
        backgroundTypeProp = builder
                .comment("Type of background texture to use.")
                .translation("enchant_icon.config.background_type")
                .defineEnum("background_type", BackgroundType.STICKY_NOTE);
        levelTypeProp = builder
                .comment("Type of level mark texture to use.")
                .translation("enchant_icon.config.level_mark_type")
                .defineEnum("level_mark_type", LevelMarkType.DIGIT_COLORED);
        applyingScopeInGuiProp = builder
                .comment("Scope in which this mod should display enchantment icons, when in a GUI-like scenario, for example inventory or item frame.")
                .translation("enchant_icon.config.scope_for_gui_like")
                .defineEnum("scope_for_gui_like", ApplyingScope.ENCHANTED_BOOK_ONLY);
        applyingScopeInHandProp = builder
                .comment("Scope in which this mod should display enchantment icons, when the item is hand-held by someone.")
                .translation("enchant_icon.config.scope_for_hand_held")
                .defineEnum("scope_for_hand_held", ApplyingScope.ENCHANTED_BOOK_ONLY);
        var showEnchantInternalName = builder
                .comment("Display internal name of all enchantments on an item in their tooltips. Useful for resource pack maker.")
                .translation("enchant_icon.config.show_enchant_internal_name")
                .define("show_enchant_internal_name", false);

        EnchantIconConfig.backgroundType = backgroundTypeProp;
        EnchantIconConfig.levelMarkType = levelTypeProp;
        EnchantIconConfig.guiScope = applyingScopeInGuiProp;
        EnchantIconConfig.inHandScope = applyingScopeInHandProp;
        EnchantIconConfig.showEnchantInternalNameInTooltip = () -> showEnchantInternalName.get() == Boolean.TRUE;

        return (configSpec = builder.build());
    }

}
