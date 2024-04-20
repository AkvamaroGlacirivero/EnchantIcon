package mods.enchanticon.neoforge;

import com.mojang.blaze3d.platform.InputConstants;
import mods.enchanticon.HotKeys;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ScreenEvent;

@Mod.EventBusSubscriber(modid = "enchant_icon", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class HotKeyMonitor {

    @SubscribeEvent
    public static void onKeyPressWithInGui(ScreenEvent.KeyPressed.Pre event) {
        if (HotKeys.toggleEnchantIcon.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
            HotKeys.isPressedWithInGUI = true;
        }
    }

    @SubscribeEvent
    public static void onKeyReleaseWithInGui(ScreenEvent.KeyReleased.Post event) {
        if (HotKeys.toggleEnchantIcon.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
            HotKeys.isPressedWithInGUI = false;
        }
    }

}
