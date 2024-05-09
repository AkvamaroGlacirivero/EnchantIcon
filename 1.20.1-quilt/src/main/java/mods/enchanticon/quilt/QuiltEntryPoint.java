package mods.enchanticon.quilt;

import mods.enchanticon.Constants;
import mods.enchanticon.EnchantTooltipAppender;
import mods.enchanticon.HotKeys;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.qsl.base.api.entrypoint.client.ClientModInitializer;
import org.quiltmc.qsl.screen.api.client.ScreenKeyboardEvents;
import org.quiltmc.qsl.tooltip.api.client.ItemTooltipCallback;

public class QuiltEntryPoint implements ClientModInitializer {

    // Remark: the correct entry-point to declare in quilt.mod.json is 'client_init'.
    // If you declare 'client', Quilt will enable backward-compatibility and try casting
    // your initializer as Fabric's ClientModInitializer which will not work!

    @Override
    public void onInitializeClient(ModContainer container) {
        ModConfigImpl.init();

        // Register our own model loader.
        // Note that this relies on Quilted Fabric API, which subjects to changes in "near" future.
        ModelLoadingRegistry.INSTANCE.registerResourceProvider(EnchantIconModelProvider::new);

        // Force load enchantment models
        // Note that this relies on Quilted Fabric API, which subjects to changes in "near" future.
        ModelLoadingRegistry.INSTANCE.registerModelProvider(((manager, out) -> {
            for (var registryKey : BuiltInRegistries.ENCHANTMENT.keySet()) {
                var iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
                out.accept(iconKey);
            }
            out.accept(Constants.ENCHANT_ICON_MODEL);
        }));

        // Registry our keybinding here.
        // Thus, we have hard dependency on fabric-keybinding.
        // Note that this relies on Quilted Fabric API, which subjects to changes in "near" future.
        KeyBindingHelper.registerKeyBinding(HotKeys.toggleEnchantIcon);

        // Register additional key-pressing handler.
        // In Forge, we have similar callbacks using ScreenEvent.KeyPressed.Pre.
        ScreenKeyboardEvents.BEFORE_KEY_PRESS.register((screen, key, scancode, modifiers) -> {
            if (HotKeys.toggleEnchantIcon.matches(key, scancode)) {
                HotKeys.isPressedWithInGUI = true;
            }
        });
        ScreenKeyboardEvents.AFTER_KEY_RELEASE.register((screen, key, scancode, modifiers) -> {
            if (HotKeys.toggleEnchantIcon.matches(key, scancode)) {
                HotKeys.isPressedWithInGUI = false;
            }
        });

        ItemTooltipCallback.EVENT.register((item, player, flags, tooltips) -> EnchantTooltipAppender.appendTooltip(item, flags, tooltips));
    }

}
