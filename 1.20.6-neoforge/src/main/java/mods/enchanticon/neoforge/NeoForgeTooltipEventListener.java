package mods.enchanticon.neoforge;

import mods.enchanticon.Constants;
import mods.enchanticon.EnchantTooltipAppender;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = Constants.MOD_ID)
public class NeoForgeTooltipEventListener {

    @SubscribeEvent
    public static void appendTooltipWhen(ItemTooltipEvent event) {
        EnchantTooltipAppender.appendTooltip(event.getItemStack(), event.getFlags(), event.getToolTip());
    }
}
