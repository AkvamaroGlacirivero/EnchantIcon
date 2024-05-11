package mods.enchanticon;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.List;
import java.util.Objects;

public class EnchantTooltipAppender {

    public static void appendTooltip(ItemStack item, TooltipFlag flags, List<Component> tooltips) {
        if (!EnchantIconConfig.showEnchantInternalNameInTooltip.getAsBoolean()) {
            return;
        }
        var enchantList = item.getEnchantments();
        if (enchantList.isEmpty()) {
            enchantList = item.get(DataComponents.STORED_ENCHANTMENTS);
        }
        if (enchantList == null || enchantList.isEmpty()) {
            return;
        }
        tooltips.add(Component.translatable("enchant_icon.tooltip.internal_names.header").withStyle(ChatFormatting.DARK_GRAY));
        for (Holder<Enchantment> enchant : enchantList.keySet()) {
            ResourceLocation id = enchant.unwrapKey().map(ResourceKey::location).orElse(null);
            if (id == null) {
                continue;
            }
            MutableComponent enchantDisplayName = Component.translatable(enchant.value().getDescriptionId());
            tooltips.add(enchantDisplayName.append(" => ").append(Objects.toString(id)).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
