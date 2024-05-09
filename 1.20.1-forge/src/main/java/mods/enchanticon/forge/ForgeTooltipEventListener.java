package mods.enchanticon.forge;

import mods.enchanticon.Constants;
import mods.enchanticon.EnchantTooltipAppender;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Constants.MOD_ID)
public class ForgeTooltipEventListener {

    @SubscribeEvent
    public static void appendTooltipWhen(ItemTooltipEvent event) {
        EnchantTooltipAppender.appendTooltip(event.getItemStack(), event.getFlags(), event.getToolTip());
    }
}
