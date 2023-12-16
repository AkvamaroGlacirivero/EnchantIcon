package mods.enchanticon.fabric.mixin;

import com.mojang.blaze3d.platform.InputConstants;
import mods.enchanticon.HotKeys;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {

    @Shadow
    @Final
    private static Map<String, KeyMapping> ALL;

    @Shadow
    @Final
    private static Map<InputConstants.Key, KeyMapping> MAP;

    @Unique
    private static Set<KeyMapping> conflicted = new HashSet<>();

    @Inject(method = "set", at = @At("TAIL"))
    private static void alwaysCheckOurKeyMapping(InputConstants.Key inputKey, boolean held, CallbackInfo ci) {
        KeyMapping current = MAP.get(inputKey);
        if (current != HotKeys.toggleEnchantIcon) {
            if (conflicted.contains(current)) {
                HotKeys.toggleEnchantIcon.setDown(held);
            }
        } else {
            for (KeyMapping keyMapping : conflicted) {
                keyMapping.setDown(held);
            }
        }
    }

    @Inject(method = "resetMapping", at = @At("TAIL"))
    private static void checkConflict(CallbackInfo ci) {
        conflicted.clear();

        for (KeyMapping keyMapping : ALL.values()) {
            if (HotKeys.toggleEnchantIcon.same(keyMapping) && HotKeys.toggleEnchantIcon != keyMapping) {
                conflicted.add(keyMapping);
            }
        }
    }
}
