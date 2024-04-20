package mods.enchanticon;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.List;
import java.util.Objects;

public class EnchantTooltipAppender {

    public static void appendTooltip(ItemStack item, TooltipFlag flags, List<Component> tooltips) {
        if (!flags.isAdvanced()) {
            return;
        }
        ListTag enchantList = item.getEnchantmentTags();
        if (enchantList.isEmpty()) {
            enchantList = EnchantedBookItem.getEnchantments(item);
        }
        if (enchantList.isEmpty()) {
            return;
        }
        tooltips.add(Component.literal("Enchantment IDs and display names:").withStyle(ChatFormatting.DARK_GRAY));
        for (int i = 0; i < enchantList.size(); i++) {
            CompoundTag enchantData = enchantList.getCompound(i);
            ResourceLocation id = EnchantmentHelper.getEnchantmentId(enchantData);
            MutableComponent enchantDisplayName = Component.translatable(Util.makeDescriptionId("enchantment", id));
            tooltips.add(enchantDisplayName.append(" => ").append(Objects.toString(id)).withStyle(ChatFormatting.DARK_GRAY));
        }
    }
}
