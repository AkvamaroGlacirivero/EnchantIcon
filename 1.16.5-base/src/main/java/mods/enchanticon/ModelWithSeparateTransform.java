package mods.enchanticon;

import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;

public interface ModelWithSeparateTransform extends BakedModel {

    BakedModel applyTransform(ItemTransforms.TransformType transformType);

}
