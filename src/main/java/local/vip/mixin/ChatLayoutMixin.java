package local.vip.mixin;
import local.vip.RenderScope;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChatHudLine.class)
public abstract class ChatLayoutMixin {
    // Keep match styling out of chat line wrapping
    @Inject(method = "breakLines", at = @At("HEAD"))
    private void start(CallbackInfoReturnable<?> cir) { RenderScope.chat = true; }
    @Inject(method = "breakLines", at = @At("RETURN"))
    private void end(CallbackInfoReturnable<?> cir) { RenderScope.chat = false; }
}
