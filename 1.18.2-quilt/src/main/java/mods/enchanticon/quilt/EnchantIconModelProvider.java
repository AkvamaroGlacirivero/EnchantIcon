package mods.enchanticon.quilt;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import mods.enchanticon.Constants;
import net.fabricmc.fabric.api.client.model.ModelProviderContext;
import net.fabricmc.fabric.api.client.model.ModelProviderException;
import net.fabricmc.fabric.api.client.model.ModelResourceProvider;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class EnchantIconModelProvider implements ModelResourceProvider {

    private static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(BlockModel.class, new BlockModel.Deserializer())
            .create();

    private final ResourceManager manager;

    public EnchantIconModelProvider(ResourceManager manager) {
        this.manager = manager;
    }

    @Override
    public @Nullable UnbakedModel loadModelResource(ResourceLocation resourceId, ModelProviderContext context) throws ModelProviderException {
        // Hard-code the model id to load, as this is the only model that this loader should load.
        if (Constants.MOD_ID.equals(resourceId.getNamespace()) && "item/enchantment_icon".equals(resourceId.getPath())) {
            // Deserialize the model to do further check
            try (var input = this.manager.getResource(new ResourceLocation(Constants.MOD_ID, "models/item/enchantment_icon.json")).getInputStream()) {
                try (var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                    return GSON.fromJson(reader, EnchantIconModel.class);
                }
            } catch (IOException | JsonSyntaxException e) {
                throw new ModelProviderException("Failed to load model", e);
            }
        }
        return null;
    }
}
