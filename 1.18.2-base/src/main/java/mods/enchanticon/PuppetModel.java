package mods.enchanticon;

import mods.enchanticon.enums.ApplyingScope;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * A special {@link ModelWithSeparateTransform} implementation, where its {@link #applyTransform(ItemTransforms.TransformType)}
 * is controlled by external configuration (thus the name "puppet").
 */
public class PuppetModel implements BakedModel, ModelWithSeparateTransform {

    public interface Factory {
        public static final class Holder {
            public static Factory impl = PuppetModel::new;
        }

        BakedModel create(BakedModel base, BakedModel override, boolean isEnchantedBook);

        static BakedModel createFrom(BakedModel base, BakedModel override, boolean isEnchantedBook) {
            return Holder.impl.create(base, override, isEnchantedBook);
        }
    }

    public static final Set<ItemTransforms.TransformType> GUI_LIKE = EnumSet.of(
            ItemTransforms.TransformType.GUI,
            ItemTransforms.TransformType.FIXED
    );

    public static final Set<ItemTransforms.TransformType> HAND_HELD_LIKE = EnumSet.of(
            ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
            ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND,
            ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
            ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND
    );

    private final BakedModel base, override;
    private final boolean isEnchantedBook;

    public PuppetModel(BakedModel base, BakedModel override, boolean isEnchantedBook) {
        this.base = base;
        this.override = override;
        this.isEnchantedBook = isEnchantedBook;
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
        return this.base.getQuads(blockState, direction, random);
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.base.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.base.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        // Required to ensure that we have enough lighting when being rendered in GUI.
        return false;
    }

    @Override
    public boolean isCustomRenderer() {
        return this.base.isCustomRenderer();
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return this.base.getParticleIcon();
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.base.getTransforms();
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.base.getOverrides();
    }

    @Override
    public BakedModel applyTransform(ItemTransforms.TransformType transformType) {
        if (this.isEnchantedBook) {
            if (GUI_LIKE.contains(transformType)) {
                return EnchantIconConfig.guiScope.get() != ApplyingScope.NONE ? this.override : this.base;
            } else if (HAND_HELD_LIKE.contains(transformType)) {
                return EnchantIconConfig.inHandScope.get() != ApplyingScope.NONE ? this.override : this.base;
            } else {
                return this.base;
            }
        } else {
            if (GUI_LIKE.contains(transformType)) {
                return EnchantIconConfig.guiScope.get() == ApplyingScope.ALL ? this.override : this.base;
            } else if (HAND_HELD_LIKE.contains(transformType)) {
                return EnchantIconConfig.inHandScope.get() == ApplyingScope.ALL ? this.override : this.base;
            } else {
                return this.base;
            }
        }
    }
}
