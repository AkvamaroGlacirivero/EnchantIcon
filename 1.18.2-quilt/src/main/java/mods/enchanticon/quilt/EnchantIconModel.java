package mods.enchanticon.quilt;

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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class EnchantIconModel implements UnbakedModel {

    @Expose
    @SerializedName("background")
    public Map<String, BlockModel> bgModels = Map.of();
    @Expose
    @SerializedName("level_mark")
    public Map<String, Map<String, BlockModel>> levelMarks = Map.of();

    @Override
    public Collection<ResourceLocation> getDependencies() {
        var dependencies = new HashSet<ResourceLocation>();
        for (var model : this.bgModels.values()) {
            dependencies.addAll(model.getDependencies());
        }
        for (ResourceLocation registryKey : Registry.ENCHANTMENT.keySet()) {
            ResourceLocation iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
            dependencies.add(iconKey);
        }
        dependencies.add(new ResourceLocation(Constants.MOD_ID, "enchant/unknown"));
        for (var entry : this.levelMarks.values()) {
            for (var model : entry.values()) {
                dependencies.addAll(model.getDependencies());
            }
        }
        return dependencies;
    }

    @Override
    public Collection<Material> getMaterials(Function<ResourceLocation, UnbakedModel> modelGetter, Set<Pair<String, String>> missingTextureError) {
        var allMaterials = new ArrayList<Material>();
        for (var bg : this.bgModels.values()) {
            allMaterials.addAll(bg.getMaterials(modelGetter, missingTextureError));
        }
        for (ResourceLocation registryKey : Registry.ENCHANTMENT.keySet()) {
            ResourceLocation iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
            var model = modelGetter.apply(iconKey);
            allMaterials.addAll(model.getMaterials(modelGetter, missingTextureError));
        }
        var fallbackEnchantIconModel = modelGetter.apply(new ResourceLocation(Constants.MOD_ID, "enchant/unknown"));
        allMaterials.addAll(fallbackEnchantIconModel.getMaterials(modelGetter, missingTextureError));
        for (var marks : this.levelMarks.values()) {
            for (var mark : marks.values()) {
                allMaterials.addAll(mark.getMaterials(modelGetter, missingTextureError));
            }
        }
        return allMaterials;
    }

    @Nullable
    @Override
    public BakedModel bake(ModelBakery baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState transform, ResourceLocation location) {
        var itemModelGen = new ItemModelGenerator();
        // Bake all background models.
        var bakedBg = new HashMap<String, BakedModel>();
        for (var entry : this.bgModels.entrySet()) {
            var raw = entry.getValue();
            if (raw.getRootModel() == ModelBakery.GENERATION_MARKER) {
                raw = itemModelGen.generateBlockModel(spriteGetter, raw);
            }
            var baked = raw.bake(baker, spriteGetter, transform, location);
            bakedBg.put(entry.getKey(), baked);
        }
        // Bake all the models for enchant icons.
        var enchantIcons = new HashMap<ResourceLocation, BakedModel>();
        for (ResourceLocation registryKey : Registry.ENCHANTMENT.keySet()) {
            ResourceLocation iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
            BakedModel enchantIconModel = baker.bake(iconKey, BlockModelRotation.X0_Y0);
            if (enchantIconModel != null) {
                enchantIcons.put(registryKey, enchantIconModel);
            }
        }
        var fallbackEnchantIcon = baker.bake(new ResourceLocation(Constants.MOD_ID, "enchant/unknown"), BlockModelRotation.X0_Y0);
        // Bake level mark models.
        var bakedLevelMarksByType = new HashMap<String, Map<String, BakedModel>>();
        var bakedDefaultLevelMarks = new HashMap<String, BakedModel>();
        for (var entry : this.levelMarks.entrySet()) {
            var bakedLevelMarks = bakedLevelMarksByType.computeIfAbsent(entry.getKey(), unused -> new HashMap<>());
            for (var subEntry : entry.getValue().entrySet()) {
                var raw = subEntry.getValue();
                if (raw.getRootModel() == ModelBakery.GENERATION_MARKER) {
                    raw = itemModelGen.generateBlockModel(spriteGetter, raw);
                }
                var baked = raw.bake(baker, spriteGetter, transform, location);
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
