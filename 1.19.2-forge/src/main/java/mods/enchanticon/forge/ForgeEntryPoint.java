package mods.enchanticon.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import mods.enchanticon.Constants;
import mods.enchanticon.HotKeys;
import mods.enchanticon.PuppetModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(Constants.MOD_ID)
public final class ForgeEntryPoint {

    public ForgeEntryPoint() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ModConfigImpl.init(new ForgeConfigSpec.Builder()));

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
            event.register("enchant_icon", new EnchantIconModelLoader());
        }

        @SubscribeEvent
        public static void addExtraModel(ModelEvent.RegisterAdditional event) {
            // Force load enchantment models here.
            for (var registryKey : ForgeRegistries.ENCHANTMENTS.getKeys()) {
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
                public BakedModel applyTransform(ItemTransforms.TransformType transformType, PoseStack poseStack, boolean applyLeftHandTransform) {
                    BakedModel swapped = this.applyTransform(transformType);
                    // DO NOT FORGET TO APPLY THE TRANSFORM
                    return swapped.applyTransform(transformType, poseStack, applyLeftHandTransform);
                }
            };
        }

    }
}
