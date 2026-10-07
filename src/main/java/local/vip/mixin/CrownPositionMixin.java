package local.vip.mixin;
import net.minecraft.client.render.entity.feature.ArmorFeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.component.DataComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ArmorFeatureRenderer.class)
public abstract class CrownPositionMixin {
    // Lift only the rose crown
    private static boolean crown(ItemStack stack, EquipmentSlot slot) {
        var equipment = stack.get(DataComponentTypes.EQUIPPABLE);
        return slot == EquipmentSlot.HEAD && equipment != null && equipment.assetId()
            .map(key -> key.getValue().toString().equals("vip_local:rose_crown")).orElse(false);
    }
    @Inject(method = "renderArmor", at = @At("HEAD"))
    private void vip$lift(MatrixStack matrices, OrderedRenderCommandQueue queue, ItemStack stack, EquipmentSlot slot, int light, BipedEntityRenderState state, CallbackInfo ci) {
        if (crown(stack, slot)) { matrices.push(); matrices.translate(0, -2.0 / 16.0, 0); }
    }
    @Inject(method = "renderArmor", at = @At("RETURN"))
    private void vip$restore(MatrixStack matrices, OrderedRenderCommandQueue queue, ItemStack stack, EquipmentSlot slot, int light, BipedEntityRenderState state, CallbackInfo ci) {
        if (crown(stack, slot)) matrices.pop();
    }
}

