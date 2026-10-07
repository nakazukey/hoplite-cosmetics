package local.vip;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.Optional;
public final class VipMatch {
    private VipMatch() {}
    // Read the server colors
    public static boolean active() {
        var c = MinecraftClient.getInstance();
        if (c.player == null || c.world == null || c.getNetworkHandler() == null) return false;
        var team = c.player.getScoreboardTeam();
        boolean greenSelf = team != null && (team.getColor() == Formatting.GREEN || team.getColor() == Formatting.DARK_GREEN);
        var own = c.getNetworkHandler().getPlayerListEntry(c.player.getUuid());
        greenSelf |= own != null && hasColor(own.getDisplayName(), 0x55FF55);
        boolean matchBoard = c.world.getScoreboard().getObjectives().stream().anyMatch(o -> {
            String title = o.getDisplayName().getString().toUpperCase(java.util.Locale.ROOT);
            return title.contains("DUELS") || title.contains("UHC");
        });
        boolean lobby = c.world.getScoreboard().getObjectives().stream().anyMatch(o ->
            o.getDisplayName().getString().toUpperCase(java.util.Locale.ROOT).contains("HOPLITE ROYALE"));
        if (lobby && !matchBoard) return false;
        int opponents = 0, red = 0;
        for (var entry : c.getNetworkHandler().getListedPlayerListEntries()) {
            if (entry.getProfile().id().equals(c.player.getUuid())) continue;
            opponents++;
            var otherTeam = entry.getScoreboardTeam();
            if ((otherTeam != null && (otherTeam.getColor() == Formatting.RED || otherTeam.getColor() == Formatting.DARK_RED))
                || hasColor(entry.getDisplayName(), 0xFF5555) || hasColor(entry.getDisplayName(), 0xAA0000)) red++;
        }
        return detect(greenSelf, matchBoard, opponents, red);
    }
    // Match rules
    static boolean detect(boolean greenSelf, boolean matchBoard, int opponents, int red) {
        return opponents > 0 && red > 0 && (greenSelf || (matchBoard && red * 4 >= opponents * 3));
    }
    private static boolean hasColor(Text text, int rgb) {
        if (text == null) return false;
        boolean[] found = {false};
        text.visit((style, value) -> {
            if (!value.isBlank() && style.getColor() != null && style.getColor().getRgb() == rgb) found[0] = true;
            return Optional.<Object>empty();
        }, net.minecraft.text.Style.EMPTY);
        return found[0];
    }
    // Green name during matches
    public static Text visualName() {
        return active() ? Text.literal(VipStyle.username()).withColor(0x55FF55) : VipStyle.name();
    }
}

