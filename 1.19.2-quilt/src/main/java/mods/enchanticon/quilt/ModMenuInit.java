package mods.enchanticon.quilt;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import mods.enchanticon.enums.ApplyingScope;
import mods.enchanticon.enums.BackgroundType;
import mods.enchanticon.enums.LevelMarkType;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ModMenuInit implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ModMenuInit::create;
    }

    public static Screen create(Screen previous) {
        ConfigBuilder builder = ConfigBuilder.create()
                .setParentScreen(previous)
                .setTitle(Component.translatable("enchant_icon.config"))
                .setSavingRunnable(ModConfigImpl.CONFIG_INSTANCE::save);
        ConfigCategory generalCategory = builder.getOrCreateCategory(Component.translatable("enchant_icon.config.general"));
        generalCategory.addEntry(builder.entryBuilder()
                .startEnumSelector(Component.translatable("enchant_icon.config.background_type"), BackgroundType.class, ModConfigImpl.backgroundTypeProp.getRealValue())
                .setDefaultValue(BackgroundType.STICKY_NOTE)
                .setTooltip(Component.translatable("enchant_icon.config.background_type.tooltip"))
                .setSaveConsumer(newValue -> ModConfigImpl.backgroundTypeProp.setValue(newValue, true))
                .build());
        generalCategory.addEntry(builder.entryBuilder()
                .startEnumSelector(Component.translatable("enchant_icon.config.level_mark_type"), LevelMarkType.class, ModConfigImpl.levelTypeProp.getRealValue())
                .setDefaultValue(LevelMarkType.DIGIT_COLORED)
                .setTooltip(Component.translatable("enchant_icon.config.level_mark_type.tooltip"))
                .setSaveConsumer(newValue -> ModConfigImpl.levelTypeProp.setValue(newValue, true))
                .build());
        generalCategory.addEntry(builder.entryBuilder()
                .startEnumSelector(Component.translatable("enchant_icon.config.scope_for_gui_like"), ApplyingScope.class, ModConfigImpl.applyingScopeInGuiProp.getRealValue())
                .setDefaultValue(ApplyingScope.ENCHANTED_BOOK_ONLY)
                .setTooltip(Component.translatable("enchant_icon.config.scope_for_gui_like.tooltip"))
                .setSaveConsumer(newValue -> ModConfigImpl.applyingScopeInGuiProp.setValue(newValue, true))
                .build());
        generalCategory.addEntry(builder.entryBuilder()
                .startEnumSelector(Component.translatable("enchant_icon.config.scope_for_hand_held"), ApplyingScope.class, ModConfigImpl.applyingScopeInHandProp.getRealValue())
                .setDefaultValue(ApplyingScope.ENCHANTED_BOOK_ONLY)
                .setTooltip(Component.translatable("enchant_icon.config.scope_for_hand_held.tooltip"))
                .setSaveConsumer(newValue -> ModConfigImpl.applyingScopeInHandProp.setValue(newValue, true))
                .build());
        return builder.build();
    }
}
