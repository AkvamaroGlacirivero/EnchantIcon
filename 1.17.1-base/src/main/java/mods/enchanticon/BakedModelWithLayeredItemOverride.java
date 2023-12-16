package mods.enchanticon;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public final class BakedModelWithLayeredItemOverride implements BakedModel {

    private final BakedModel base;
    private final ItemOverrides overrides;

    public BakedModelWithLayeredItemOverride(BakedModel base, BakedModel itemOverrideSource) {
        this.base = base;
        this.overrides = new CompositeItemOverride(itemOverrideSource.getOverrides(), base.getOverrides());
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
        return this.base.usesBlockLight();
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
        return this.overrides;
    }
}
