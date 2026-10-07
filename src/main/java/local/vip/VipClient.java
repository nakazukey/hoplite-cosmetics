package local.vip;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
public final class VipClient implements ClientModInitializer {
    @Override public void onInitializeClient() {
        // Saved settings
        VipSettings.load();
        // Menu key
        KeyBinding menu = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.vip_local.numpad_menu", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_KP_SUBTRACT,
            KeyBinding.Category.create(Identifier.of("vip_local", "general"))));
        // Open the menu
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menu.wasPressed()) {
                if (client.currentScreen instanceof VipScreen) client.setScreen(null);
                else if (client.currentScreen == null) client.setScreen(new VipScreen());
            }
        });
    }
}

