package mods.enchanticon.neoforge;

import com.mojang.blaze3d.vertex.PoseStack;
import mods.enchanticon.Constants;
import mods.enchanticon.HotKeys;
import mods.enchanticon.PuppetModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@Mod(Constants.MOD_ID)
public final class NeoForgeEntryPoint {

    public NeoForgeEntryPoint() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModConfigImpl.init(new ModConfigSpec.Builder()));

        if (ModList.get().isLoaded("cloth_config")) {
            ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                    () -> new ConfigScreenHandler.ConfigScreenFactory(ModConfigScreenImpl::create));
        }
    }

    @Mod.EventBusSubscriber(modid = Constants.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ClientInit {

        // In 1.19.2, model loader registration has its own event: ModelEvent.RegisterGeometryLoaders
        // Prior that, use ModelRegistryEvent. This is what Forge itself uses.
        @SubscribeEvent
        public static void modelLoaderReg(ModelEvent.RegisterGeometryLoaders event) {
            // Register our own model loader.
            // Note that Forge will automatically infer the current active mod container when firing
            // this event and use that to construct ResourceLocation, so we do not need to supply mod ID here.
            event.register(new ResourceLocation(Constants.MOD_ID, "enchant_icon"), new EnchantIconModelLoader());
        }

        @SubscribeEvent
        public static void addExtraModel(ModelEvent.RegisterAdditional event) {
            // Force load enchantment models here.
            for (var registryKey : BuiltInRegistries.ENCHANTMENT.keySet()) {
                var iconKey = new ResourceLocation(Constants.MOD_ID, "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
                event.register(iconKey);
            }
            // Force load enchant icon model here. This model is critical to the mod.
            event.register(Constants.ENCHANT_ICON_MODEL);
        }

        @SubscribeEvent
        public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
            event.register(HotKeys.toggleEnchantIcon);
        }

        @SubscribeEvent
        public static void setup(FMLClientSetupEvent event) {
            PuppetModel.Factory.Holder.impl = (base, override, isEnchantedBook) -> new PuppetModel(base, override, isEnchantedBook) {
                // Bridge Forge-exposed method back to our own.
                @Override
                public BakedModel applyTransform(ItemDisplayContext displayContext, PoseStack poseStack, boolean applyLeftHandTransform) {
                    BakedModel swapped = this.applyTransform(displayContext);
                    // DO NOT FORGET TO APPLY THE TRANSFORM
                    return swapped.applyTransform(displayContext, poseStack, applyLeftHandTransform);
                }
            };
        }

    }
}
