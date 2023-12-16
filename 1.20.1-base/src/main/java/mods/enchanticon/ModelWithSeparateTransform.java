package mods.enchanticon;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;

public interface ModelWithSeparateTransform extends BakedModel {

    BakedModel applyTransform(ItemDisplayContext transformType);

}
