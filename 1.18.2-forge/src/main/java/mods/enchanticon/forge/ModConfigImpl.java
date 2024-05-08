package mods.enchanticon.forge;

import mods.enchanticon.EnchantIconConfig;
import mods.enchanticon.enums.ApplyingScope;
import mods.enchanticon.enums.BackgroundType;
import mods.enchanticon.enums.LevelMarkType;
import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfigImpl {

    public static ForgeConfigSpec configSpec;

    public static ForgeConfigSpec.EnumValue<BackgroundType> backgroundTypeProp;
    public static ForgeConfigSpec.EnumValue<LevelMarkType> levelTypeProp;
    public static ForgeConfigSpec.EnumValue<ApplyingScope> applyingScopeInGuiProp;
    public static ForgeConfigSpec.EnumValue<ApplyingScope> applyingScopeInHandProp;

    public static ForgeConfigSpec init(ForgeConfigSpec.Builder builder) {
        backgroundTypeProp = builder
                .comment("Type of background texture to use. Supports 'sticky_note' and 'enchanted_book'.")
                .translation("enchant_icon.config.background_type")
                .defineEnum("background_type", BackgroundType.STICKY_NOTE);
        levelTypeProp = builder
                .comment("Type of level mark texture to use. Supports 'solid', 'vanilla' and 'color_blind'.")
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

        EnchantIconConfig.backgroundType = backgroundTypeProp;
        EnchantIconConfig.levelMarkType = levelTypeProp;
        EnchantIconConfig.guiScope = applyingScopeInGuiProp;
        EnchantIconConfig.inHandScope = applyingScopeInHandProp;
        
        return (configSpec = builder.build());
    }

}
