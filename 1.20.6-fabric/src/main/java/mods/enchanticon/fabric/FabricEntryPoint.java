package mods.enchanticon.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import mods.enchanticon.Constants;
import mods.enchanticon.EnchantTooltipAppender;
import mods.enchanticon.HotKeys;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;

public class FabricEntryPoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {

        PreparableModelLoadingPlugin.register(new EnchantIconModelResolver.PreLoader(), (loadedRawModel, pluginContext) -> {
            // Register our own model loader.
            pluginContext.resolveModel().register(new EnchantIconModelResolver(loadedRawModel));

            // Force load enchantment models
            var modelIds = new ArrayList<ResourceLocation>();
            for (var registryKey : BuiltInRegistries.ENCHANTMENT.keySet()) {
                var iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
                modelIds.add(iconKey);
            }
            modelIds.add(Constants.ENCHANT_ICON_MODEL);
            pluginContext.addModels(modelIds);
        });

        // Registry our keybinding here.
        // Thus, we have hard dependency on fabric-keybinding.
        KeyBindingHelper.registerKeyBinding(HotKeys.toggleEnchantIcon);

        // Register additional key-pressing handler.
        // In Forge, we have similar callbacks using ScreenEvent.KeyPressed.Pre.
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            ScreenKeyboardEvents.beforeKeyPress(screen).register((screen1, key, scancode, modifiers) -> {
                if (HotKeys.toggleEnchantIcon.matches(key, scancode)) {
                    HotKeys.isPressedWithInGUI = true;
                }
            });
            ScreenKeyboardEvents.afterKeyRelease(screen).register((screen1, key, scancode, modifiers) -> {
                if (HotKeys.toggleEnchantIcon.matches(key, scancode)) {
                    HotKeys.isPressedWithInGUI = false;
                }
            });
        });

        ItemTooltipCallback.EVENT.register((item, context, flags, lines) -> EnchantTooltipAppender.appendTooltip(item, flags, lines));

        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            var config = AutoConfig.register(ModConfigImpl.class, Toml4jConfigSerializer::new);
            config.registerLoadListener(ModConfigImpl::syncConfig);
            config.registerSaveListener(ModConfigImpl::syncConfig);
            config.load();
        }
    }
}
