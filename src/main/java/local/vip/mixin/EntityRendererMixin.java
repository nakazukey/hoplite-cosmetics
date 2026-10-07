package local.vip.mixin;
import local.vip.VipStyle;
import local.vip.VipMatch;
import local.vip.VipSettings;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.render.entity.state.EntityRenderState;
@Mixin(EntityRenderer.class)
public abstract class EntityRendererMixin {
    // World nameplates
    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    private void vip$name(Entity entity, CallbackInfoReturnable<Text> cir) {
        if (!VipSettings.enabled) return;
        if (VipStyle.isSelf(entity)) cir.setReturnValue(VipMatch.visualName());
        else cir.setReturnValue(VipStyle.server(entity.getDisplayName()));
    }
    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void vip$finalLabel(Entity entity, EntityRenderState state, float delta, CallbackInfo ci) {
        if (!VipSettings.enabled || state.displayName == null) return;
        state.displayName = VipStyle.isSelf(entity) ? VipMatch.visualName() : VipStyle.server(state.displayName);
    }
}

