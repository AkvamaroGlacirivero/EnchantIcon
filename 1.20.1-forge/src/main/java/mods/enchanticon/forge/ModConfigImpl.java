package mods.enchanticon.forge;

import mods.enchanticon.enums.ApplyingScope;
import mods.enchanticon.enums.BackgroundType;
import mods.enchanticon.Constants;
import mods.enchanticon.enums.LevelMarkType;
import mods.enchanticon.EnchantIconConfig;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModConfigImpl {

    public static ForgeConfigSpec configSpec;

    public static ForgeConfigSpec.EnumValue<BackgroundType> backgroundTypeProp;
    public static ForgeConfigSpec.EnumValue<LevelMarkType> levelTypeProp;
    public static ForgeConfigSpec.EnumValue<ApplyingScope> applyingScopeInGuiProp;
    public static ForgeConfigSpec.EnumValue<ApplyingScope> applyingScopeInHandProp;

    public static ForgeConfigSpec init(ForgeConfigSpec.Builder builder) {
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

        EnchantIconConfig.backgroundType = backgroundTypeProp;
        EnchantIconConfig.levelMarkType = levelTypeProp;
        EnchantIconConfig.guiScope = applyingScopeInGuiProp;
        EnchantIconConfig.inHandScope = applyingScopeInHandProp;

        return (configSpec = builder.build());
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantIcon-Config-Watcher");

}
