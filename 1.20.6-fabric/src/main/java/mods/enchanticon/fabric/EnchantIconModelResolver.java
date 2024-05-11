package mods.enchanticon.fabric;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;
import mods.enchanticon.Constants;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelResolver;
import net.fabricmc.fabric.api.client.model.loading.v1.PreparableModelLoadingPlugin;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class EnchantIconModelResolver implements ModelResolver {

    public static final class PreLoader implements PreparableModelLoadingPlugin.DataLoader<EnchantIconModel> {
        private static final ResourceLocation MODEL_LOCATION = new ResourceLocation(Constants.MOD_ID, "models/item/enchantment_icon.json");
        @Override
        public CompletableFuture<EnchantIconModel> load(ResourceManager resourceManager, Executor executor) {
            return CompletableFuture.supplyAsync(() -> {
                try (var input = resourceManager.getResourceOrThrow(MODEL_LOCATION).open()) {
                    try (var reader = new InputStreamReader(input, StandardCharsets.UTF_8)) {
                        return GSON.fromJson(reader, EnchantIconModel.class);
                    }
                } catch (IOException | JsonSyntaxException e) {
                    throw new RuntimeException("Failed to load model", e);
                }
            }, executor);
        }
    }

    static final Gson GSON = new GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation()
            .registerTypeAdapter(BlockModel.class, new BlockModel.Deserializer())
            .create();

    private final EnchantIconModel rawModel;

    public EnchantIconModelResolver(EnchantIconModel rawModel) {
        this.rawModel = rawModel;
    }

    @Override
    public @Nullable UnbakedModel resolveModel(Context context) {
        var resourceId = context.id();
        if (Constants.MOD_ID.equals(resourceId.getNamespace()) && "item/enchantment_icon".equals(resourceId.getPath())) {
            return this.rawModel;
        }
        return null;
    }
}
