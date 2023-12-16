package mods.enchanticon.forge;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;

import java.util.HashMap;
import java.util.Map;

public class EnchantIconModelLoader implements IModelLoader<EnchantIconModel> {
    @Override
    public EnchantIconModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        Map<String, BlockModel> bgRawModels = new HashMap<>();
        Map<String, Map<String, BlockModel>> levelRawModelsByType = new HashMap<>();
        Map<String, BlockModel> defaultLevelRawModelsByType = new HashMap<>();
        for (Map.Entry<String, JsonElement> entry : modelContents.getAsJsonObject("background").entrySet()) {
            bgRawModels.put(entry.getKey(), deserializationContext.deserialize(entry.getValue(), BlockModel.class));
        }
        for (Map.Entry<String, JsonElement> entry : modelContents.getAsJsonObject("level_mark").entrySet()) {
            Map<String, BlockModel> levelRawModels = levelRawModelsByType.computeIfAbsent(entry.getKey(), unused -> new HashMap<>());
            for (Map.Entry<String, JsonElement> subEntry : entry.getValue().getAsJsonObject().entrySet()) {
                if ("default".equals(subEntry.getKey())) {
                    defaultLevelRawModelsByType.put(entry.getKey(), deserializationContext.deserialize(subEntry.getValue(), BlockModel.class));
                } else {
                    levelRawModels.put(subEntry.getKey(), deserializationContext.deserialize(subEntry.getValue(), BlockModel.class));
                }
            }
        }
        return new EnchantIconModel(bgRawModels, levelRawModelsByType, defaultLevelRawModelsByType);
    }

    @Override
    public void onResourceManagerReload(ResourceManager manager) {
        // No-op
    }
}
