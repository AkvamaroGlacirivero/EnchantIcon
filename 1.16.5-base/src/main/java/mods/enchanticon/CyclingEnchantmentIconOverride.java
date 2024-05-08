package mods.enchanticon;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

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
    public BakedModel resolve(BakedModel model, ItemStack item, @Nullable ClientLevel level, @Nullable LivingEntity holder) {
        // Try getting enchantments from the ItemStack using the regular way
        ListTag enchants = item.getEnchantmentTags();
        // If it is empty, then try getting them using the Enchanted-Book-specific way
        if (enchants.isEmpty()) {
            enchants = EnchantedBookItem.getEnchantments(item);
        }
        // If there are enchantments, display them in a cyclic fashion
        if (!enchants.isEmpty()) {
            // Gather necessary info: current time (so that we can cycle through all enchantments),
            // current enchantment type & level to display.
            int currentIndexToDisplay = (int) (System.currentTimeMillis() / 1200) % enchants.size();
            CompoundTag enchant = enchants.getCompound(currentIndexToDisplay);
            // Assemble look-up cache key
            AssembledKey lookupKey = new AssembledKey(
                    ResourceLocation.tryParse(enchant.getString("id")),
                    enchant.getInt("lvl"),
                    EnchantIconConfig.backgroundType.get().type,
                    EnchantIconConfig.levelMarkType.get().type
            );
            // Get the cached model, create one if cache misses.
            BakedModel assembled = this.cache.get(lookupKey);
            if (assembled == null) {
                assembled = this.assemble(lookupKey);
                this.cache.put(lookupKey, assembled);
            }
            return PuppetModel.Factory.createFrom(model, assembled, item.getItem() == Items.ENCHANTED_BOOK);
        }
        // Delegate to the original ItemOverrides, so things like clock and compass work properly
        ItemOverrides originalOverrides = model.getOverrides();
        model = originalOverrides.resolve(model, item, level, holder);
        return model;
    }

    private BakedModel assemble(AssembledKey key) {
        BakedModel enchantModel = this.enchantIcons.getOrDefault(key.type, this.defaultEnchantIcon);
        BakedModel bgModel = this.bg.get(key.bg);
        String enchantLevel = Integer.toString(key.level);
        BakedModel levelMark = this.levelMarks.getOrDefault(key.mark, Collections.emptyMap()).get(enchantLevel);
        if (levelMark == null) {
            levelMark = this.defaultLevelMarks.get(key.mark);
        }
        return new AssembledIcon(bgModel, enchantModel, levelMark);
    }

    private static final class AssembledKey {
        final ResourceLocation type;
        final int level;
        final String bg;
        final String mark;

        public AssembledKey(ResourceLocation type, int level, String bg, String mark) {
            this.type = type;
            this.level = level;
            this.bg = bg;
            this.mark = mark;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            AssembledKey that = (AssembledKey) o;
            return level == that.level && Objects.equals(type, that.type) && Objects.equals(bg, that.bg) && Objects.equals(mark, that.mark);
        }

        @Override
        public int hashCode() {
            return Objects.hash(type, level, bg, mark);
        }

        @Override
        public String toString() {
            return "AssembledKey{" +
                    "type=" + type +
                    ", level=" + level +
                    ", bg='" + bg + '\'' +
                    ", mark='" + mark + '\'' +
                    '}';
        }
    }

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
        public List<BakedQuad> getQuads(@Nullable BlockState blockState, @Nullable Direction direction, Random random) {
            if (direction == null) {
                if (this.quadsWithoutDirection == null) {
                    List<BakedQuad> list = new ArrayList<>();
                    list.addAll(this.bg.getQuads(blockState, null, random));
                    list.addAll(this.enchantIcon.getQuads(blockState, null, random));
                    list.addAll(this.levelMark.getQuads(blockState, null, random));
                    this.quadsWithoutDirection = Collections.unmodifiableList(list);
                }
                return this.quadsWithoutDirection;
            } else {
                if (!this.quadsByDirection.containsKey(direction)) {
                    List<BakedQuad> list = new ArrayList<>();
                    list.addAll(this.bg.getQuads(blockState, direction, random));
                    list.addAll(this.enchantIcon.getQuads(blockState, direction, random));
                    list.addAll(this.levelMark.getQuads(blockState, direction, random));
                    this.quadsByDirection.put(direction, Collections.unmodifiableList(list));
                }
                return this.quadsByDirection.getOrDefault(direction, Collections.emptyList());
            }
        }

        @Override
        public boolean useAmbientOcclusion() {
            return enchantIcon.useAmbientOcclusion();
        }

        @Override
        public boolean isGui3d() {
            return enchantIcon.isGui3d();
        }

        @Override
        public boolean usesBlockLight() {
            return enchantIcon.usesBlockLight();
        }

        @Override
        public boolean isCustomRenderer() {
            return enchantIcon.isCustomRenderer();
        }

        @Override
        public TextureAtlasSprite getParticleIcon() {
            return enchantIcon.getParticleIcon();
        }

        @Override
        public ItemTransforms getTransforms() {
            return enchantIcon.getTransforms();
        }

        @Override
        public ItemOverrides getOverrides() {
            return enchantIcon.getOverrides();
        }
    }
}
