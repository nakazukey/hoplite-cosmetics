package local.vip.mixin;
import local.vip.VipStyle;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.text.OrderedText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
@Mixin(TextRenderer.class)
public abstract class RenderedTextMixin {
    // Final text rendering
    @ModifyVariable(method = {"prepare(Lnet/minecraft/text/OrderedText;FFIZZI)Lnet/minecraft/client/font/TextRenderer$GlyphDrawable;", "getWidth(Lnet/minecraft/text/OrderedText;)I", "draw(Lnet/minecraft/text/OrderedText;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/render/VertexConsumerProvider;Lnet/minecraft/client/font/TextRenderer$TextLayerType;II)V", "drawWithOutline"}, at = @At("HEAD"), argsOnly = true)
    private OrderedText vip$rendered(OrderedText text) { return VipStyle.rendered(text); }
}

