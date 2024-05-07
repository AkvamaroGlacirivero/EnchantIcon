package mods.enchanticon.fabric;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.mojang.datafixers.util.Pair;
import mods.enchanticon.BakedEnchantIconModel;
import mods.enchanticon.Constants;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class EnchantIconModel implements UnbakedModel {

    @Expose
    @SerializedName("background")
    public Map<String, BlockModel> bgModels = Collections.emptyMap();
    @Expose
    @SerializedName("level_mark")
    public Map<String, Map<String, BlockModel>> levelMarks = Collections.emptyMap();

    @Override
    public Collection<ResourceLocation> getDependencies() {
        Collection<ResourceLocation> dependencies = new HashSet<>();
        for (BlockModel model : this.bgModels.values()) {
            dependencies.addAll(model.getDependencies());
        }
        for (ResourceLocation registryKey : Registry.ENCHANTMENT.keySet()) {
            ResourceLocation iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
            dependencies.add(iconKey);
        }
        dependencies.add(new ResourceLocation(Constants.MOD_ID, "enchant/unknown"));
        for (Map<String, BlockModel> entry : this.levelMarks.values()) {
            for (BlockModel model : entry.values()) {
                dependencies.addAll(model.getDependencies());
            }
        }
        return dependencies;
    }

    @Override
    public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureError) {
        Collection<Material> allMaterials = new ArrayList<>();
        for (BlockModel bg : this.bgModels.values()) {
            allMaterials.addAll(bg.getMaterials(modelGetter, missingTextureError));
        }
        for (ResourceLocation registryKey : Registry.ENCHANTMENT.keySet()) {
            ResourceLocation iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
            UnbakedModel model = modelGetter.apply(iconKey);
            allMaterials.addAll(model.getMaterials(modelGetter, missingTextureError));
        }
        UnbakedModel fallbackEnchantIconModel = modelGetter.apply(new ResourceLocation(Constants.MOD_ID, "enchant/unknown"));
        allMaterials.addAll(fallbackEnchantIconModel.getMaterials(modelGetter, missingTextureError));
        for (Map<String, BlockModel> marks : this.levelMarks.values()) {
            for (BlockModel mark : marks.values()) {
                allMaterials.addAll(mark.getMaterials(modelGetter, missingTextureError));
            }
        }
        return allMaterials;
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBakery baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation location) {
        ItemModelGenerator itemModelGen = new ItemModelGenerator();
        // Bake all background models.
        Map<String, BakedModel> bakedBg = new HashMap<>();
        for (Map.Entry<String, BlockModel> entry : this.bgModels.entrySet()) {
            BlockModel raw = entry.getValue();
            if (raw.getRootModel() == ModelBakery.GENERATION_MARKER) {
                raw = itemModelGen.generateBlockModel(spriteGetter, raw);
            }
            BakedModel baked = raw.bake(baker, spriteGetter, transform, location);
            bakedBg.put(entry.getKey(), baked);
        }
        // Bake all the models for enchant icons.
        Map<ResourceLocation, BakedModel> enchantIcons = new HashMap<>();
        for (ResourceLocation registryKey : Registry.ENCHANTMENT.keySet()) {
            ResourceLocation iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
            BakedModel enchantIconModel = baker.bake(iconKey, BlockModelRotation.X0_Y0);
            if (enchantIconModel != null) {
                enchantIcons.put(registryKey, enchantIconModel);
            }
        }
        BakedModel fallbackEnchantIcon = baker.bake(new ResourceLocation(Constants.MOD_ID, "enchant/unknown"), BlockModelRotation.X0_Y0);
        // Bake level mark models.
        Map<String, Map<String, BakedModel>> bakedLevelMarksByType = new HashMap<>();
        Map<String, BakedModel> bakedDefaultLevelMarks = new HashMap<>();
        for (Map.Entry<String, Map<String, BlockModel>> entry : this.levelMarks.entrySet()) {
            Map<String, BakedModel> bakedLevelMarks = bakedLevelMarksByType.computeIfAbsent(entry.getKey(), unused -> new HashMap<>());
            for (Map.Entry<String, BlockModel> subEntry : entry.getValue().entrySet()) {
                BlockModel raw = subEntry.getValue();
                if (raw.getRootModel() == ModelBakery.GENERATION_MARKER) {
                    raw = itemModelGen.generateBlockModel(spriteGetter, raw);
                }
                BakedModel baked = raw.bake(baker, spriteGetter, transform, location);
                if ("default".equals(subEntry.getKey())) {
                    bakedDefaultLevelMarks.put(entry.getKey(), baked);
                } else {
                    bakedLevelMarks.put(subEntry.getKey(), baked);
                }
            }
        }
        return new BakedEnchantIconModel(bakedBg, enchantIcons, fallbackEnchantIcon, bakedLevelMarksByType, bakedDefaultLevelMarks);
    }
}
