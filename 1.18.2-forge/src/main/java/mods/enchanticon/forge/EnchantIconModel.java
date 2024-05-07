package mods.enchanticon.forge;

import com.mojang.datafixers.util.Pair;
import mods.enchanticon.BakedEnchantIconModel;
import mods.enchanticon.Constants;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.IModelConfiguration;
import net.minecraftforge.client.model.geometry.IModelGeometry;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class EnchantIconModel implements IModelGeometry<EnchantIconModel> {

    private final Map<String, BlockModel> bgModels;
    private final Map<String, Map<String, BlockModel>> levelMarks;
    private final Map<String, BlockModel> defaultLevelMarks;

    public EnchantIconModel(Map<String, BlockModel> bgModels, Map<String, Map<String, BlockModel>> levelMarks, Map<String, BlockModel> defaultLevelMarks) {
        this.bgModels = bgModels;
        this.levelMarks = levelMarks;
        this.defaultLevelMarks = defaultLevelMarks;
    }

    @Override
    public BakedModel bake(IModelConfiguration context, ModelBakery bakery, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        // Bake all background models.
        var bakedBg = new HashMap<String, BakedModel>();
        for (var entry : this.bgModels.entrySet()) {
            var raw = entry.getValue();
            var baked = raw.bake(bakery, raw, spriteGetter, modelState, modelLocation, context.isSideLit());
            bakedBg.put(entry.getKey(), baked);
        }
        // Bake all the models for enchant icons.
        var enchantIcons = new HashMap<ResourceLocation, BakedModel>();
        for (ResourceLocation registryKey : ForgeRegistries.ENCHANTMENTS.getKeys()) {
            ResourceLocation iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
            BakedModel enchantIconModel = bakery.bake(iconKey, BlockModelRotation.X0_Y0, spriteGetter);
            if (enchantIconModel != null) {
                enchantIcons.put(registryKey, enchantIconModel);
            }
        }
        var fallbackEnchantIcon = bakery.bake(new ResourceLocation(Constants.MOD_ID, "enchant/unknown"), BlockModelRotation.X0_Y0, spriteGetter);
        // Bake level mark models.
        var bakedLevelMarksByType = new HashMap<String, Map<String, BakedModel>>();
        for (var entry : this.levelMarks.entrySet()) {
            var bakedLevelMarks = bakedLevelMarksByType.computeIfAbsent(entry.getKey(), unused -> new HashMap<>());
            for (var subEntry : entry.getValue().entrySet()) {
                var raw = subEntry.getValue();
                var baked = raw.bake(bakery, raw, spriteGetter, modelState, modelLocation, context.isSideLit());
                bakedLevelMarks.put(subEntry.getKey(), baked);
            }
        }
        var bakedDefaultLevelMarks = new HashMap<String, BakedModel>();
        for (var entry : this.defaultLevelMarks.entrySet()) {
            var raw = entry.getValue();
            var baked = raw.bake(bakery, raw, spriteGetter, modelState, modelLocation, context.isSideLit());
            bakedDefaultLevelMarks.put(entry.getKey(), baked);
        }
        return new BakedEnchantIconModel(bakedBg, enchantIcons, fallbackEnchantIcon, bakedLevelMarksByType, bakedDefaultLevelMarks);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> textureErrors) {
        var allMaterials = new ArrayList<Material>();
        for (var bg : this.bgModels.values()) {
            allMaterials.addAll(bg.getMaterials(modelGetter, textureErrors));
        }
        for (ResourceLocation registryKey : ForgeRegistries.ENCHANTMENTS.getKeys()) {
            ResourceLocation iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
            var model = modelGetter.apply(iconKey);
            allMaterials.addAll(model.getMaterials(modelGetter, textureErrors));
        }
        var fallbackEnchantIconModel = modelGetter.apply(new ResourceLocation(Constants.MOD_ID, "enchant/unknown"));
        allMaterials.addAll(fallbackEnchantIconModel.getMaterials(modelGetter, textureErrors));
        for (var marks : this.levelMarks.values()) {
            for (var mark : marks.values()) {
                allMaterials.addAll(mark.getMaterials(modelGetter, textureErrors));
            }
        }
        for (var mark : this.defaultLevelMarks.values()) {
            allMaterials.addAll(mark.getMaterials(modelGetter, textureErrors));
        }
        return allMaterials;
    }
}
