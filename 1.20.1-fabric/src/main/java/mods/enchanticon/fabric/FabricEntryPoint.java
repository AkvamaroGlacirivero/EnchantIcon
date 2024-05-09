package mods.enchanticon.fabric;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import mods.enchanticon.Constants;
import mods.enchanticon.EnchantTooltipAppender;
import mods.enchanticon.HotKeys;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class FabricEntryPoint implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register our own model loader.
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(EnchantIconModelProvider::new);

        // Force load enchantment models
        ModelLoadingRegistry.INSTANCE.registerModelProvider(((manager, out) -> {
            for (var registryKey : BuiltInRegistries.ENCHANTMENT.keySet()) {
                var iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
                out.accept(iconKey);
            }
            out.accept(Constants.ENCHANT_ICON_MODEL);
        }));

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

        ItemTooltipCallback.EVENT.register(EnchantTooltipAppender::appendTooltip);

        if (FabricLoader.getInstance().isModLoaded("cloth-config")) {
            var config = AutoConfig.register(ModConfigImpl.class, Toml4jConfigSerializer::new);
            config.registerLoadListener(ModConfigImpl::syncConfig);
            config.registerSaveListener(ModConfigImpl::syncConfig);
            config.load();
        }
    }
}
