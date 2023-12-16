package mods.enchanticon;

import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public final class Constants {

    public static final String MOD_ID = "enchant_icon";

    // Note that if we specify "inventory" as the variant, Minecraft will assume it is under /models/item directory,
    // and prepend that path before the resource path given here.
    // That said, if we have `new ModelResourceLocation(new ResourceLocation("enchant_icon", "item/enchantment_icon"), "inventory")`,
    // Minecraft will try loading model from assets/enchant_icon/models/item/item/enchantment_icon.json,
    // which is typically not the desired result.
    // Hence, we do not specify the full path here, only the model name.
    public static final ModelResourceLocation ENCHANT_ICON_MODEL = new ModelResourceLocation(new ResourceLocation(MOD_ID, "enchantment_icon"), "inventory");
}
