package mods.enchanticon.quilt;

import mods.enchanticon.Constants;
import mods.enchanticon.EnchantIconConfig;
import mods.enchanticon.enums.ApplyingScope;
import mods.enchanticon.enums.BackgroundType;
import mods.enchanticon.enums.LevelMarkType;
import org.quiltmc.config.api.Config;
import org.quiltmc.config.api.WrappedConfig;
import org.quiltmc.config.api.annotations.Comment;
import org.quiltmc.config.api.values.TrackedValue;
import org.quiltmc.loader.api.config.QuiltConfig;

import java.util.List;

public class ModConfigImpl {

    public static final WrappedConfig CONFIG_INSTANCE = QuiltConfig.create(Constants.MOD_ID, "main", ConfigModel.class);;

    public static TrackedValue<BackgroundType> backgroundTypeProp;
    public static TrackedValue<LevelMarkType> levelTypeProp;
    public static TrackedValue<ApplyingScope> applyingScopeInGuiProp;
    public static TrackedValue<ApplyingScope> applyingScopeInHandProp;

    public static void init() {
        CONFIG_INSTANCE.registerCallback(ModConfigImpl::onConfigUpdate);
        onConfigUpdate(CONFIG_INSTANCE);
        backgroundTypeProp = (TrackedValue<BackgroundType>) CONFIG_INSTANCE.getValue(List.of("backgroundType"));
        levelTypeProp = (TrackedValue<LevelMarkType>) CONFIG_INSTANCE.getValue(List.of("levelMarkType"));
        applyingScopeInGuiProp = (TrackedValue<ApplyingScope>) CONFIG_INSTANCE.getValue(List.of("scopeForGuiLike"));
        applyingScopeInHandProp = (TrackedValue<ApplyingScope>) CONFIG_INSTANCE.getValue(List.of("scopeForHandHeld"));
    }

    static void onConfigUpdate(Config config) {
        var newConfig = new EnchantIconConfig();
        newConfig.backgroundType = (BackgroundType) config.getValue(List.of("backgroundType")).getRealValue();
        newConfig.levelMarkType = (LevelMarkType) config.getValue(List.of("levelMarkType")).getRealValue();
        newConfig.guiScope = (ApplyingScope) config.getValue(List.of("scopeForGuiLike")).getRealValue();
        newConfig.inHandScope = (ApplyingScope) config.getValue(List.of("scopeForHandHeld")).getRealValue();
        EnchantIconConfig.instance = newConfig;
    }

    public static final class ConfigModel extends WrappedConfig {
        @Comment("Type of background texture to use. Supports 'sticky_note' and 'enchanted_book'.")
        public final BackgroundType backgroundType = BackgroundType.STICKY_NOTE;

        @Comment("Type of level mark texture to use. Supports 'solid', 'vanilla' and 'color_blind'.")
        public final LevelMarkType levelMarkType = LevelMarkType.DIGIT_COLORED;

        @Comment("Scope in which this mod should display enchantment icons, when in a GUI-like scenario, for example inventory or item frame.")
        public final ApplyingScope scopeForGuiLike = ApplyingScope.ENCHANTED_BOOK_ONLY;

        @Comment("Scope in which this mod should display enchantment icons, when the item is hand-held by someone.")
        public final ApplyingScope scopeForHandHeld = ApplyingScope.ENCHANTED_BOOK_ONLY;
    }
}
