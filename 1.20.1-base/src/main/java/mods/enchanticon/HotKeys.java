package mods.enchanticon;

import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class HotKeys {

    public static KeyMapping toggleEnchantIcon = new KeyMapping("enchant_icon.key.toggle", GLFW.GLFW_KEY_LEFT_SHIFT, "enchant_icon.key");

    public static boolean isPressedWithInGUI = false;

    public static boolean enchantIconKeyToggled() {
        return isPressedWithInGUI || toggleEnchantIcon.isDown();
    }
}
