package local.vip.mixin;
import local.vip.VipStyle;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(DrawContext.class)
public abstract class HudTextMixin {
    // HUD labels
    @ModifyVariable(method = {"drawText(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;IIIZ)V", "drawCenteredTextWithShadow(Lnet/minecraft/client/font/TextRenderer;Lnet/minecraft/text/Text;III)V"}, at = @At("HEAD"), argsOnly = true)
    private Text vip$serverText(Text text) { return local.vip.RenderScope.chat ? text : VipStyle.hud(text); }
}

