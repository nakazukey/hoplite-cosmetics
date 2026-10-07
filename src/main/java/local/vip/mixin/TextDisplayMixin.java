package local.vip.mixin;
import local.vip.VipStyle;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.client.render.entity.DisplayEntityRenderer;
import net.minecraft.client.render.entity.state.TextDisplayEntityRenderState;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(DisplayEntityRenderer.TextDisplayEntityRenderer.class)
public abstract class TextDisplayMixin {
    // Text-display leaderboards
    @Shadow private DisplayEntity.TextDisplayEntity.TextLines getLines(Text text, int width) { throw new AssertionError(); }
    @Inject(method = "updateRenderState(Lnet/minecraft/entity/decoration/DisplayEntity$TextDisplayEntity;Lnet/minecraft/client/render/entity/state/TextDisplayEntityRenderState;F)V", at = @At("TAIL"))
    private void vip$leaderboard(DisplayEntity.TextDisplayEntity entity, TextDisplayEntityRenderState state, float delta, CallbackInfo ci) {
        if (state.data == null) return;
        Text original = state.data.text(), styled = VipStyle.server(original);
        if (styled != original) state.textLines = getLines(styled, state.data.lineWidth());
    }
}

