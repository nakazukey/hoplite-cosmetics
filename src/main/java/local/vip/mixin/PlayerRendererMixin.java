package local.vip.mixin;
import local.vip.VipStyle;
import local.vip.VipSettings;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.entity.PlayerLikeEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.util.Identifier;
import net.minecraft.component.type.EquippableComponent;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.equipment.EquipmentAssetKeys;
import net.minecraft.registry.RegistryKey;
@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerRendererMixin {
    // Crown appearance
    @Inject(method = "updateRenderState(Lnet/minecraft/entity/PlayerLikeEntity;Lnet/minecraft/client/render/entity/state/PlayerEntityRenderState;F)V", at = @At("TAIL"))
    private void vip$crown(PlayerLikeEntity player, PlayerEntityRenderState state, float delta, CallbackInfo ci) {
        if (!VipSettings.enabled || !VipSettings.roseCrown || !VipStyle.isSelf(player)) return;
        ItemStack visual = new ItemStack(Items.IRON_HELMET);
        visual.set(DataComponentTypes.EQUIPPABLE, EquippableComponent.builder(EquipmentSlot.HEAD)
            .model(RegistryKey.of(EquipmentAssetKeys.REGISTRY_KEY, Identifier.of("vip_local", "rose_crown"))).build());
        state.equippedHeadStack = visual;
        state.wearingSkullType = null;
        state.headItemRenderState.clear();
    }
    @Inject(method = "hasLabel(Lnet/minecraft/entity/PlayerLikeEntity;D)Z", at = @At("HEAD"), cancellable = true)
    private void vip$ownLabel(PlayerLikeEntity player, double distance, CallbackInfoReturnable<Boolean> cir) {
        if (VipSettings.enabled && VipStyle.isSelf(player)) {
            var c = MinecraftClient.getInstance();
            cir.setReturnValue(!c.options.getPerspective().isFirstPerson() && !c.options.hudHidden
                && !player.isInvisible() && distance <= 4096);
        }
    }
}


