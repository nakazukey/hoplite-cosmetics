package local.vip.mixin;
import local.vip.VipStyle;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(Entity.class)
public abstract class ServerNameMixin {
    // Server holograms
    @Inject(method = "getCustomName", at = @At("RETURN"), cancellable = true)
    private void vip$hologram(CallbackInfoReturnable<Text> cir) {
        if (!((Entity)(Object)this).getEntityWorld().isClient()) return;
        Text name = cir.getReturnValue();
        if (name != null) cir.setReturnValue(VipStyle.server(name));
    }
}

