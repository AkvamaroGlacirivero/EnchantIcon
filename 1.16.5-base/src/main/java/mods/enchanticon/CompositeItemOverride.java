package mods.enchanticon;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

/**
 * A special {@link ItemOverrides} that combines two ItemOverrides into one.
 * Used in compositing {@link BakedModel}.
 */
public class CompositeItemOverride extends ItemOverrides {

    private final ItemOverrides first, second;

    public CompositeItemOverride(ItemOverrides first, ItemOverrides second) {
        this.first = first;
        this.second = second;
    }

    @Nullable
    @Override
    public BakedModel resolve(BakedModel model, ItemStack item, @Nullable ClientLevel level, @Nullable LivingEntity holder) {
        BakedModel override = this.first.resolve(model, item, level, holder);
        if (override == model) {
            override = this.second.resolve(model, item, level, holder);
        }
        return override;
    }
}
