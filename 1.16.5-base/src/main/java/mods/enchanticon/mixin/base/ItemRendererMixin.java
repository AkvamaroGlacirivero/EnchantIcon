package mods.enchanticon.mixin.base;

import com.mojang.blaze3d.vertex.PoseStack;
import mods.enchanticon.Constants;
import mods.enchanticon.HotKeys;
import mods.enchanticon.PuppetModel;
import mods.enchanticon.EnchantIconConfig;
import mods.enchanticon.enums.ApplyingScope;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    /**
     * Disable the "foil" (the glowing overlay) when we cycle through enchantments.
     * @param subject The ItemStack in question
     * @param itemStack The original argument passed in {@link ItemRenderer#render}
     * @param transformType The original argument passed in {@link ItemRenderer#render}
     * @param leftHand The original argument passed in {@link ItemRenderer#render}
     * @param poseStack The original argument passed in {@link ItemRenderer#render}
     * @param buffer The original argument passed in {@link ItemRenderer#render}
     * @param combinedLight The original argument passed in {@link ItemRenderer#render}
     * @param combinedOverlay The original argument passed in {@link ItemRenderer#render}
     * @param model The original argument passed in {@link ItemRenderer#render}
     * @return true if the model should have glowing effect; false otherwise.
     */
    @Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;hasFoil()Z"))
    private boolean noGlowWhenEnchantIconKeyToggle(ItemStack subject, ItemStack itemStack,
                                                   ItemTransforms.TransformType transformType,
                                                   boolean leftHand, PoseStack poseStack, MultiBufferSource buffer,
                                                   int combinedLight, int combinedOverlay, BakedModel model) {
        if (HotKeys.enchantIconKeyToggled()) {
            if (subject.getItem() == Items.ENCHANTED_BOOK) {
                if (PuppetModel.GUI_LIKE.contains(transformType)) {
                    // If the applying scope is not NONE, the foil should not be displayed regardless
                    // when the hotkey is toggled or not.
                    // If the applying scope is NONE, then we let ItemStack.hasFoil to decide.
                    return EnchantIconConfig.instance.guiScope == ApplyingScope.NONE && subject.hasFoil();
                } else if (PuppetModel.HAND_HELD_LIKE.contains(transformType)) {
                    // Same above.
                    return EnchantIconConfig.instance.inHandScope == ApplyingScope.NONE && subject.hasFoil();
                } else {
                    return subject.hasFoil();
                }
            } else {
                if (PuppetModel.GUI_LIKE.contains(transformType)) {
                    // If the applying scope is ALL, the foil should not be displayed regardless
                    // when the hotkey is toggled or not.
                    // If the applying scope is not ALL, then we let ItemStack.hasFoil to decide.
                    return EnchantIconConfig.instance.guiScope != ApplyingScope.ALL && subject.hasFoil();
                } else if (PuppetModel.HAND_HELD_LIKE.contains(transformType)) {
                    // Same above.
                    return EnchantIconConfig.instance.inHandScope != ApplyingScope.ALL && subject.hasFoil();
                } else {
                    return subject.hasFoil();
                }
            }
        } else {
            return subject.hasFoil();
        }
    }

    @Shadow
    @Final
    private ItemModelShaper itemModelShaper;

    @Unique
    private static final ModelResourceLocation TRIDENT_IN_HAND_MODEL = new ModelResourceLocation(new ResourceLocation("minecraft", "trident_in_hand"), "inventory");

    @Inject(method = "getModel", at = @At("HEAD"), cancellable = true)
    private void swapModel(ItemStack itemInQuestion, Level level, LivingEntity holder, CallbackInfoReturnable<BakedModel> cir) {
        if (HotKeys.enchantIconKeyToggled()) {
            BakedModel original;
            if (itemInQuestion.getItem() == Items.TRIDENT) {
                original = this.itemModelShaper.getModelManager().getModel(TRIDENT_IN_HAND_MODEL);
            } else {
                original = this.itemModelShaper.getItemModel(itemInQuestion);
            }
            BakedModel enchantIconModel = this.itemModelShaper.getModelManager().getModel(Constants.ENCHANT_ICON_MODEL);
            ClientLevel clientLevel = level instanceof ClientLevel ? (ClientLevel)level : null;
            enchantIconModel = enchantIconModel.getOverrides().resolve(original, itemInQuestion, clientLevel, holder);
            cir.setReturnValue(enchantIconModel);
        }
    }

}
