package mods.enchanticon.forge;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.client.model.IModelLoader;

import java.util.HashMap;
import java.util.Map;

public class EnchantIconModelLoader implements IModelLoader<EnchantIconModel> {
    @Override
    public EnchantIconModel read(JsonDeserializationContext deserializationContext, JsonObject modelContents) {
        var bgRawModels = new HashMap<String, BlockModel>();
        var levelRawModelsByType = new HashMap<String, Map<String, BlockModel>>();
        var defaultLevelRawModelsByType = new HashMap<String, BlockModel>();
        for (var entry : modelContents.getAsJsonObject("background").entrySet()) {
            bgRawModels.put(entry.getKey(), deserializationContext.deserialize(entry.getValue(), BlockModel.class));
        }
        for (var entry : modelContents.getAsJsonObject("level_mark").entrySet()) {
            var levelRawModels = levelRawModelsByType.computeIfAbsent(entry.getKey(), unused -> new HashMap<>());
            for (var subEntry : entry.getValue().getAsJsonObject().entrySet()) {
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
