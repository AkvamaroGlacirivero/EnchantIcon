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
        Map<String, BakedModel> bakedBg = new HashMap<>();
        for (Map.Entry<String, BlockModel> entry : this.bgModels.entrySet()) {
            BlockModel raw = entry.getValue();
            BakedModel baked = raw.bake(bakery, raw, spriteGetter, modelState, modelLocation, context.isSideLit());
            bakedBg.put(entry.getKey(), baked);
        }
        // Bake all the models for enchant icons.
        Map<ResourceLocation, BakedModel> enchantIcons = new HashMap<>();
        for (ResourceLocation registryKey : ForgeRegistries.ENCHANTMENTS.getKeys()) {
            ResourceLocation iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
            BakedModel enchantIconModel = bakery.getBakedModel(iconKey, BlockModelRotation.X0_Y0, spriteGetter);
            if (enchantIconModel != null) {
                enchantIcons.put(registryKey, enchantIconModel);
            }
        }
        // Bake level mark models.
        Map<String, Map<String, BakedModel>> bakedLevelMarksByType = new HashMap<>();
        for (Map.Entry<String, Map<String, BlockModel>> entry : this.levelMarks.entrySet()) {
            Map<String, BakedModel> bakedLevelMarks = bakedLevelMarksByType.computeIfAbsent(entry.getKey(), unused -> new HashMap<>());
            for (Map.Entry<String, BlockModel> subEntry : entry.getValue().entrySet()) {
                BlockModel raw = subEntry.getValue();
                BakedModel baked = raw.bake(bakery, raw, spriteGetter, modelState, modelLocation, context.isSideLit());
                bakedLevelMarks.put(subEntry.getKey(), baked);
            }
        }
        Map<String, BakedModel> bakedDefaultLevelMarks = new HashMap<>();
        for (Map.Entry<String, BlockModel> entry : this.defaultLevelMarks.entrySet()) {
            BlockModel raw = entry.getValue();
            BakedModel baked = raw.bake(bakery, raw, spriteGetter, modelState, modelLocation, context.isSideLit());
            bakedDefaultLevelMarks.put(entry.getKey(), baked);
        }
        return new BakedEnchantIconModel(bakedBg, enchantIcons, bakedLevelMarksByType, bakedDefaultLevelMarks);
    }

    @Override
    public Collection<Material> getTextures(IModelConfiguration context, Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> textureErrors) {
        Collection<Material> allMaterials = new ArrayList<>();
        for (BlockModel bg : this.bgModels.values()) {
            allMaterials.addAll(bg.getMaterials(modelGetter, textureErrors));
        }
        for (ResourceLocation registryKey : ForgeRegistries.ENCHANTMENTS.getKeys()) {
            ResourceLocation iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
            UnbakedModel model = modelGetter.apply(iconKey);
            allMaterials.addAll(model.getMaterials(modelGetter, textureErrors));
        }
        for (Map<String, BlockModel> marks : this.levelMarks.values()) {
            for (BlockModel mark : marks.values()) {
                allMaterials.addAll(mark.getMaterials(modelGetter, textureErrors));
            }
        }
        for (BlockModel mark : this.defaultLevelMarks.values()) {
            allMaterials.addAll(mark.getMaterials(modelGetter, textureErrors));
        }
        return allMaterials;
    }
}
