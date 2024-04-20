package mods.enchanticon.neoforge;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.neoforged.neoforge.client.model.geometry.IGeometryLoader;

import java.util.HashMap;
import java.util.Map;

public class EnchantIconModelLoader implements IGeometryLoader<EnchantIconModel> {
    @Override
    public EnchantIconModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
        var bgRawModels = new HashMap<String, BlockModel>();
        var levelRawModelsByType = new HashMap<String, Map<String, BlockModel>>();
        var defaultLevelRawModelsByType = new HashMap<String, BlockModel>();
        for (var entry : jsonObject.getAsJsonObject("background").entrySet()) {
            bgRawModels.put(entry.getKey(), deserializationContext.deserialize(entry.getValue(), BlockModel.class));
        }
        for (var entry : jsonObject.getAsJsonObject("level_mark").entrySet()) {
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
}
