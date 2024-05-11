package mods.enchanticon;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CyclingEnchantmentIconOverride extends ItemOverrides {

    private final Map<String, BakedModel> bg;
    private final Map<ResourceLocation, BakedModel> enchantIcons;
    private final BakedModel defaultEnchantIcon;
    private final Map<String, BakedModel> defaultLevelMarks;
    private final Map<String, Map<String, BakedModel>> levelMarks;

    private final Map<AssembledKey, BakedModel> cache = new HashMap<>();

    public CyclingEnchantmentIconOverride(Map<String, BakedModel> bg,
                                          Map<ResourceLocation, BakedModel> enchantIcons,
                                          BakedModel defaultEnchantIcon,
                                          Map<String, BakedModel> defaultLevelMarks,
                                          Map<String, Map<String, BakedModel>> levelMarks) {
        // Note: this no-arg constructor is private for 1.19, 1.19.1 and 1.19.2.
        // Access transformer / widener is required to make this accessible from our code.
        // Forge itself has declared such AT entry on its own; while it is not the case for
        // Fabric's transitive-access-widener.
        // As such, please check that the AW entry is in place before compiling the project.
        super();
        this.bg = bg;
        this.enchantIcons = enchantIcons;
        this.defaultEnchantIcon = defaultEnchantIcon;
        this.defaultLevelMarks = defaultLevelMarks;
        this.levelMarks = levelMarks;
    }

    @Nullable
    @Override
    public BakedModel resolve(BakedModel model, ItemStack item, @Nullable ClientLevel level, @Nullable LivingEntity holder, int tintIndex) {
        // Try getting enchantments from the ItemStack using the regular way
        var enchants = item.getEnchantments();
        if (enchants.isEmpty()) {
            enchants = item.get(DataComponents.STORED_ENCHANTMENTS);
        }
        // If there are enchantments, display them in a cyclic fashion
        if (enchants != null && !enchants.isEmpty()) {
            // Gather necessary info: current time (so that we can cycle through all enchantments),
            // current enchantment type & level to display.
            int currentIndexToDisplay = (int) (System.currentTimeMillis() / 1200) % enchants.size();
            var enchant = new ArrayList<>(enchants.entrySet()).get(currentIndexToDisplay);
            // Assemble look-up cache key
            var lookupKey = new AssembledKey(
                    enchant.getKey().unwrapKey().map(ResourceKey::location).orElse(null),
                    enchant.getIntValue(),
                    EnchantIconConfig.backgroundType.get().type,
                    EnchantIconConfig.levelMarkType.get().type
            );
            // Get the cached model, create one if cache misses.
            var assembled = this.cache.get(lookupKey);
            if (assembled == null) {
                assembled = this.assemble(lookupKey);
                this.cache.put(lookupKey, assembled);
            }
            return PuppetModel.Factory.createFrom(model, assembled, item.is(Items.ENCHANTED_BOOK));
        }
        // Delegate to the original ItemOverrides, so things like clock and compass work properly
        ItemOverrides originalOverrides = model.getOverrides();
        model = originalOverrides.resolve(model, item, level, holder, tintIndex);
        return model;
    }

    private BakedModel assemble(AssembledKey key) {
        var enchantModel = this.enchantIcons.getOrDefault(key.type, this.defaultEnchantIcon);
        var bgModel = this.bg.get(key.bg);
        var enchantLevel = Integer.toString(key.level);
        var levelMark = this.levelMarks.getOrDefault(key.mark, Map.of()).get(enchantLevel);
        if (levelMark == null) {
            levelMark = this.defaultLevelMarks.get(key.mark);
        }
        return new AssembledIcon(bgModel, enchantModel, levelMark);
    }

    private static record AssembledKey(ResourceLocation type, int level, String bg, String mark) {}

    private static final class AssembledIcon implements BakedModel {

        private final BakedModel bg, enchantIcon, levelMark;
        private final Map<Direction, List<BakedQuad>> quadsByDirection = new EnumMap<>(Direction.class);
        private List<BakedQuad> quadsWithoutDirection = null;

        private AssembledIcon(BakedModel bg, BakedModel enchantIcon, BakedModel levelMark) {
            this.bg = bg;
            this.enchantIcon = enchantIcon;
            this.levelMark = levelMark;
        }

        @Override
        public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, RandomSource randomSource) {
            if (direction == null) {
                if (this.quadsWithoutDirection == null) {
                    var list = new ArrayList<BakedQuad>();
                    list.addAll(this.bg.getQuads(blockState, null, randomSource));
                    list.addAll(this.enchantIcon.getQuads(blockState, null, randomSource));
                    list.addAll(this.levelMark.getQuads(blockState, null, randomSource));
                    this.quadsWithoutDirection = List.copyOf(list);
                }
                return this.quadsWithoutDirection;
            } else {
                if (!this.quadsByDirection.containsKey(direction)) {
                    var list = new ArrayList<BakedQuad>();
                    list.addAll(this.bg.getQuads(blockState, direction, randomSource));
                    list.addAll(this.enchantIcon.getQuads(blockState, direction, randomSource));
                    list.addAll(this.levelMark.getQuads(blockState, direction, randomSource));
                    this.quadsByDirection.put(direction, List.copyOf(list));
                }
                return this.quadsByDirection.getOrDefault(direction, List.of());
            }
        }

        @Override
        public boolean useAmbientOcclusion() {
            return this.enchantIcon.useAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return this.enchantIcon.isGui3d();
        }

        @Override
        public boolean usesBlockLight() {
            return this.enchantIcon.usesBlockLight();
        }

        @Override
        public boolean isCustomRenderer() {
            return this.enchantIcon.isCustomRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return this.enchantIcon.getParticleIcon();
        }

        @Override
        public ItemTransforms getTransforms() {
            return this.enchantIcon.getTransforms();
        }

        @Override
        public ItemOverrides getOverrides() {
            return this.enchantIcon.getOverrides();
        }
    }
}
