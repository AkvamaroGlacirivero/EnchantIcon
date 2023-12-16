package mods.enchanticon.forge;

import com.mojang.blaze3d.vertex.PoseStack;
import mods.enchanticon.Constants;
import mods.enchanticon.HotKeys;
import mods.enchanticon.PuppetModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ExtensionPoint;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.network.FMLNetworkConstants;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

@Mod("enchant_icon")
public class ForgeEntryPoint {

    public ForgeEntryPoint() {
        ModLoadingContext context = ModLoadingContext.get();
        // This mod does nothing on physical server.
        // We therefore register our display test here, to suppress that infamous "red X".
        // Since Forge 41.0.15, a new property in mods.toml is available to use: 'displayTest',
        // which we will use in 1.19.
        // See https://github.com/MinecraftForge/MinecraftForge/pull/8656 for more details.
        context.registerExtensionPoint(ExtensionPoint.DISPLAYTEST,
                () -> Pair.of(() -> FMLNetworkConstants.IGNORESERVERONLY, (incomingVer, isNetwork) -> true));
        context.registerConfig(ModConfig.Type.CLIENT, ModConfigImpl.init(new ForgeConfigSpec.Builder()));

        if (ModList.get().isLoaded("cloth-config")) {
            ModLoadingContext.get().registerExtensionPoint(ExtensionPoint.CONFIGGUIFACTORY,
                    () -> ModConfigScreenImpl::create);
        }
    }

    @Mod.EventBusSubscriber(modid = "enchant_icon", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static final class ClientInit {

        // In 1.19.2, model loader registration has its own event: ModelEvent.RegisterGeometryLoaders
        // Prior that, use ModelRegistryEvent. This is what Forge itself uses.
        @SubscribeEvent
        public static void modelLoaderReg(ModelRegistryEvent event) {
            // Register our own model loader.
            // Typically, we would also like to register the model loader as a resource reloading listener,
            // but since we are not doing anything there right now, we omit that call.
            // In event that we need also to register reloading listener, subscribe to RegisterClientReloadListenersEvent
            // for that purpose.
            ModelLoaderRegistry.registerLoader(new ResourceLocation(Constants.MOD_ID, "enchant_icon"),
                    new EnchantIconModelLoader());
            // Force load enchantment models
            for (ResourceLocation registryKey : ForgeRegistries.ENCHANTMENTS.getKeys()) {
                ResourceLocation iconKey = new ResourceLocation("enchant_icon", "enchant/" + registryKey.getNamespace() + "/" + registryKey.getPath());
                ModelLoader.addSpecialModel(iconKey);
            }
            ModelLoader.addSpecialModel(Constants.ENCHANT_ICON_MODEL);
        }

        @SubscribeEvent
        public static void setup(FMLClientSetupEvent event) {
            // Registry our keybinding here.
            // In 1.19.2, we need a dedicated event for this: RegisterKeyMappingsEvent
            ClientRegistry.registerKeyBinding(HotKeys.toggleEnchantIcon);

            PuppetModel.Factory.Holder.impl = (base, override, isEnchantedBook) -> new PuppetModel(base, override, isEnchantedBook) {
                // Bridge Forge-exposed method back to our own.
                @Override
                public BakedModel handlePerspective(ItemTransforms.TransformType transformType, PoseStack poseStack) {
                    BakedModel swapped = this.applyTransform(transformType);
                    // DO NOT FORGET TO APPLY THE TRANSFORM
                    return swapped.handlePerspective(transformType, poseStack);
                }
            };
        }

    }
}
