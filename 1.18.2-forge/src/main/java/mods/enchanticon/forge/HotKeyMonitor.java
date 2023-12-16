package mods.enchanticon.forge;

import com.mojang.blaze3d.platform.InputConstants;
import mods.enchanticon.HotKeys;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "enchant_icon", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public final class HotKeyMonitor {

    @SubscribeEvent
    public static void onKeyPressWithInGui(ScreenEvent.KeyboardKeyPressedEvent.Pre event) {
        if (HotKeys.toggleEnchantIcon.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
            HotKeys.isPressedWithInGUI = true;
        }
    }

    @SubscribeEvent
    public static void onKeyReleaseWithInGui(ScreenEvent.KeyboardKeyReleasedEvent.Post event) {
        if (HotKeys.toggleEnchantIcon.isActiveAndMatches(InputConstants.getKey(event.getKeyCode(), event.getScanCode()))) {
            HotKeys.isPressedWithInGUI = false;
        }
    }

}
