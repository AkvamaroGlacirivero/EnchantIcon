package mods.enchanticon.neoforge;

import mods.enchanticon.BakedEnchantIconModel;
import mods.enchanticon.Constants;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.geometry.IGeometryBakingContext;
import net.neoforged.neoforge.client.model.geometry.IUnbakedGeometry;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EnchantIconModel implements IUnbakedGeometry<EnchantIconModel> {

    private final Map<String, BlockModel> bgModels;
    private final Map<String, Map<String, BlockModel>> levelMarks;
    private final Map<String, BlockModel> defaultLevelMarks;

    public EnchantIconModel(Map<String, BlockModel> bgModels, Map<String, Map<String, BlockModel>> levelMarks, Map<String, BlockModel> defaultLevelMarks) {
        this.bgModels = bgModels;
        this.levelMarks = levelMarks;
        this.defaultLevelMarks = defaultLevelMarks;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        // Bake all background models.
        var bakedBg = new HashMap<String, BakedModel>();
        for (var entry : this.bgModels.entrySet()) {
            var raw = entry.getValue();
            var baked = raw.bake(baker, raw, spriteGetter, modelState, modelLocation, context.useBlockLight());
            bakedBg.put(entry.getKey(), baked);
        }
        // Bake all the models for enchant icons.
        var enchantIcons = new HashMap<ResourceLocation, BakedModel>();
        for (ResourceLocation registryKey : BuiltInRegistries.ENCHANTMENT.keySet()) {
            ResourceLocation iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
            BakedModel enchantIconModel = baker.bake(iconKey, BlockModelRotation.X0_Y0, spriteGetter);
            if (enchantIconModel != null) {
                enchantIcons.put(registryKey, enchantIconModel);
            }
        }
        // Bake level mark models.
        var bakedLevelMarksByType = new HashMap<String, Map<String, BakedModel>>();
        for (var entry : this.levelMarks.entrySet()) {
            var bakedLevelMarks = bakedLevelMarksByType.computeIfAbsent(entry.getKey(), unused -> new HashMap<>());
            for (var subEntry : entry.getValue().entrySet()) {
                var raw = subEntry.getValue();
                var baked = raw.bake(baker, raw, spriteGetter, modelState, modelLocation, context.useBlockLight());
                bakedLevelMarks.put(subEntry.getKey(), baked);
            }
        }
        var bakedDefaultLevelMarks = new HashMap<String, BakedModel>();
        for (var entry : this.defaultLevelMarks.entrySet()) {
            var raw = entry.getValue();
            var baked = raw.bake(baker, raw, spriteGetter, modelState, modelLocation, context.useBlockLight());
            bakedDefaultLevelMarks.put(entry.getKey(), baked);
        }
        return new BakedEnchantIconModel(bakedBg, enchantIcons, bakedLevelMarksByType, bakedDefaultLevelMarks);
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        for (var bg : this.bgModels.values()) {
            bg.resolveParents(modelGetter);
        }
        for (ResourceLocation registryKey : BuiltInRegistries.ENCHANTMENT.keySet()) {
            ResourceLocation iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
            var model = modelGetter.apply(iconKey);
            model.resolveParents(modelGetter);
        }
        for (var marks : this.levelMarks.values()) {
            for (var mark : marks.values()) {
                mark.resolveParents(modelGetter);
            }
        }
        for (var mark : this.defaultLevelMarks.values()) {
            mark.resolveParents(modelGetter);
        }
    }
}
