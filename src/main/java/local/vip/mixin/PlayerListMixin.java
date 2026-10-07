package local.vip.mixin;
import local.vip.VipStyle;
import local.vip.VipMatch;
import local.vip.VipSettings;
import local.vip.VipOrder;
import java.util.Comparator;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
@Mixin(PlayerListHud.class)
public abstract class PlayerListMixin {
    // Our name in TAB
    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    private void vip$name(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        var self = MinecraftClient.getInstance().player;
        if (VipSettings.enabled && self != null && entry.getProfile().id().equals(self.getUuid())) cir.setReturnValue(VipMatch.visualName());
    }
    // Sort before the 80-player limit
    @ModifyArg(method = "collectPlayerEntries", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;sorted(Ljava/util/Comparator;)Ljava/util/stream/Stream;"), index = 0)
    private Comparator<PlayerListEntry> vip$first(Comparator<PlayerListEntry> original) {
        var self = MinecraftClient.getInstance().player;
        if (!VipSettings.enabled || self == null) return original;
        return VipOrder.selfFirst(original, entry -> entry.getProfile().id().equals(self.getUuid()));
    }
}

