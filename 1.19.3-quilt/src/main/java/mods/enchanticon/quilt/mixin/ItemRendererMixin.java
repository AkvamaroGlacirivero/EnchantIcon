package mods.enchanticon.quilt.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import mods.enchanticon.ModelWithSeparateTransform;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {

    @ModifyVariable(method = "render", argsOnly = true,
            at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;translate(FFF)V"))
    public BakedModel swapModel(BakedModel original,
                                ItemStack item, ItemTransforms.TransformType transformType, boolean leftHand, PoseStack pose) {
        if (original instanceof ModelWithSeparateTransform) {
            pose.popPose();
            pose.pushPose();
            var swapped = ((ModelWithSeparateTransform) original).applyTransform(transformType);
            swapped.getTransforms().getTransform(transformType).apply(leftHand, pose);
            return swapped;
        }
        return original;
    }
}
