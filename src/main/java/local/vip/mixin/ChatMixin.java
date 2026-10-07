package local.vip.mixin;
import local.vip.VipStyle;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.client.gui.hud.ChatHudLine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(ChatHud.class)
public abstract class ChatMixin {
    // Keep chat rendering separate
    @Inject(method = "render(Lnet/minecraft/client/gui/hud/ChatHud$Backend;IIZ)V", at = @At("HEAD"))
    private void vip$chatStart(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) { local.vip.RenderScope.chat = true; }
    @Inject(method = "render(Lnet/minecraft/client/gui/hud/ChatHud$Backend;IIZ)V", at = @At("RETURN"))
    private void vip$chatEnd(org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) { local.vip.RenderScope.chat = false; }
    @ModifyVariable(method = "addVisibleMessage", at = @At("HEAD"), argsOnly = true)
    private ChatHudLine vip$chat(ChatHudLine message) {
        var styled = message.signature() == null ? VipStyle.chat(message.content())
            : VipStyle.chatPlayer(message.content());
        if (styled == message.content()) return message;
        return new ChatHudLine(message.creationTick(), styled, message.signature(), message.indicator());
    }
}

