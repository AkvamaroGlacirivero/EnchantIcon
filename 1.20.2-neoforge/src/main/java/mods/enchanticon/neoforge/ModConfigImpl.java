package mods.enchanticon.neoforge;

import mods.enchanticon.Constants;
import mods.enchanticon.EnchantIconConfig;
import mods.enchanticon.enums.ApplyingScope;
import mods.enchanticon.enums.BackgroundType;
import mods.enchanticon.enums.LevelMarkType;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
        return (configSpec = builder.build());
    }

    @SubscribeEvent
    public static void onConfigReload(ModConfigEvent.Reloading event) {
        if (Constants.MOD_ID.equals(event.getConfig().getModId())) {
            LOGGER.info("Config change detected, syncing latest config...");
            var newConfig = new EnchantIconConfig();
            newConfig.backgroundType = backgroundTypeProp.get();
            newConfig.levelMarkType = levelTypeProp.get();
            newConfig.guiScope = applyingScopeInGuiProp.get();
            newConfig.inHandScope = applyingScopeInHandProp.get();
            EnchantIconConfig.instance = newConfig;
            LOGGER.info("Config synced.");
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger("EnchantIcon-Config-Watcher");

}
