package mods.enchanticon;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BakedEnchantIconModel implements BakedModel {

    private final CyclingEnchantmentIconOverride overrides;

    public BakedEnchantIconModel(Map<String, BakedModel> bg,
                                 Map<ResourceLocation, BakedModel> enchantIconModels,
                                 BakedModel defaultEnchantIcon,
                                 Map<String, Map<String, BakedModel>> levelsByType,
                                 Map<String, BakedModel> defaultLevels) {
        this.overrides = new CyclingEnchantmentIconOverride(bg, enchantIconModels, defaultEnchantIcon, defaultLevels, levelsByType);
    }

    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
        return Collections.emptyList();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public TextureAtlasSprite getParticleIcon() {
        return null;
    }

    @Override
    public ItemTransforms getTransforms() {
        return ItemTransforms.NO_TRANSFORMS;
    }

    @Override
    public ItemOverrides getOverrides() {
        return this.overrides;
    }
}
