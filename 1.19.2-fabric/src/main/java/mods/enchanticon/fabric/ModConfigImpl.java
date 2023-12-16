package mods.enchanticon.fabric;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.ConfigHolder;
import me.shedaniel.autoconfig.annotation.Config;
import mods.enchanticon.Constants;
import mods.enchanticon.EnchantIconConfig;
import mods.enchanticon.enums.ApplyingScope;
import mods.enchanticon.enums.BackgroundType;
import mods.enchanticon.enums.LevelMarkType;
import net.minecraft.world.InteractionResult;

@Config(name = Constants.MOD_ID)
public class ModConfigImpl implements ConfigData {

    public BackgroundType backgroundType = BackgroundType.STICKY_NOTE;

    public LevelMarkType levelMarkType = LevelMarkType.DIGIT_COLORED;

    public ApplyingScope guiScope = ApplyingScope.ENCHANTED_BOOK_ONLY;

    public ApplyingScope inHandScope = ApplyingScope.ENCHANTED_BOOK_ONLY;

    public static InteractionResult syncConfig(ConfigHolder<ModConfigImpl> holder, ModConfigImpl config) {
        EnchantIconConfig.instance.backgroundType = config.backgroundType;
        EnchantIconConfig.instance.levelMarkType = config.levelMarkType;
        EnchantIconConfig.instance.guiScope = config.guiScope;
        EnchantIconConfig.instance.inHandScope = config.inHandScope;
        return InteractionResult.PASS;
    }
}
