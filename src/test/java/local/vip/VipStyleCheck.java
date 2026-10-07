package local.vip;
import net.minecraft.text.*;
import java.util.Optional;
public class VipStyleCheck {
    public static void main(String[] args) {
        check("<Alice> ciao", "<\ue100\ue002Alice> ciao");
        check("[VIP] Alice: ciao Alice", "[VIP] \ue100\ue002Alice: ciao Alice");
        check("Alice » ciao", "\ue100\ue002Alice » ciao");
        check("<Bob> ciao Alice", "<Bob> ciao Alice");
        check("<AliceExtra> ciao", "<AliceExtra> ciao");
        check("Bob: Alice: ciao", "Bob: Alice: ciao");
        check("Alice ha effettuato l'accesso", "Alice ha effettuato l'accesso");
        Text split = Text.empty().append(Text.literal("<Al").withColor(0x123456))
            .append(Text.literal("ice> ").withColor(0x654321))
            .append(Text.literal("messaggio").withColor(0xABCDEF));
        Text result = VipStyle.chat(split, "Alice");
        if (!result.getString().equals("<\ue100\ue002Alice> messaggio")) throw new AssertionError("Split name");
        boolean[] kept = {false};
        result.visit((style, value) -> {
            if (value.contains("messaggio") && style.getColor().getRgb() == 0xFFFFFF) kept[0] = true;
            return Optional.<Object>empty();
        }, Style.EMPTY);
        if (!kept[0]) throw new AssertionError("Own message body is not white");
        check("Alice has joined! (61/70)", "\ue100\ue002Alice has joined! (61/70)");
        check("Alice has left!", "\ue100\ue002Alice has left!");
        check("Bob has joined! (61/70)", "Bob has joined! (61/70)");
        check("<Bob> Alice has left!", "<Bob> Alice has left!");
        Text notice = Text.literal("Alice has joined! ").withColor(0xAAAAAA)
            .append(Text.literal("(61/70)").withColor(0x55FF55));
        boolean[] counterKept = {false};
        VipStyle.chat(notice, "Alice").visit((style, value) -> {
            if (value.contains("61/70") && style.getColor().getRgb() == 0x55FF55) counterKept[0] = true;
            return Optional.<Object>empty();
        }, Style.EMPTY);
        if (!counterKept[0]) throw new AssertionError("Join counter recolored");
        RenderScope.chat = true;
        var mention = Text.literal("Bob: Alice").asOrderedText();
        if (VipStyle.rendered(mention) != mention) throw new AssertionError("Player chat render changed");
        RenderScope.chat = false;
        Text leaderboard = Text.literal("22137. Alice - ").withColor(0xAAAAAA)
            .append(Text.literal("29").withColor(0x55FFFF));
        Text board = VipStyle.server(leaderboard, "Alice");
        if (!board.getString().equals("22137. \ue100\ue002Alice - 29")) throw new AssertionError("Leaderboard name missing badge");
        if (VipStyle.server(board, "Alice") != board) throw new AssertionError("Duplicate badge");
        Text other = Text.literal("AliceExtra - 29");
        if (VipStyle.server(other, "Alice") != other) throw new AssertionError("Partial name matched");
        if (!VipStyle.server(Text.literal("Winner: Alice! Alice wins."), "Alice").getString()
            .equals("Winner: \ue100\ue002Alice! \ue100\ue002Alice wins.")) throw new AssertionError("Server mentions missed");
        VipSettings.enabled = false;
        Text disabled = Text.literal("<Alice> ciao");
        if (VipStyle.chat(disabled, "Alice") != disabled) throw new AssertionError("Disabled chat changed");
        VipSettings.enabled = true;
        for (var color : VipSettings.COLORS) {
            VipSettings.select(color.id());
            Text name = VipStyle.name("Alice");
            var nameStyle = name.getSiblings().get(1).getStyle();
            if (nameStyle.getColor().getRgb() != color.colorAt(color.id().equals("rainbow") ? 0 : 1)) throw new AssertionError(color.id());
            if (color.id().equals("rainbow")) {
                int last = name.getSiblings().getLast().getStyle().getColor().getRgb();
                if (last != color.colorAt(1)) throw new AssertionError("Gradient endpoint: " + color.id());
                if (name.getSiblings().size() != 6) throw new AssertionError("Per-letter gradient missing");
            } else if (name.getSiblings().size() != 2) {
                throw new AssertionError("Non-rainbow name must have a solid color");
            }
            if (!name.getString().equals(VipStyle.badge(color).getString() + "\ue002Alice")) throw new AssertionError("Extra spacing");
        }
        long vanillaCount = java.util.Arrays.stream(net.minecraft.util.Formatting.values()).filter(net.minecraft.util.Formatting::isColor).count();
        if (VipSettings.COLORS.length != vanillaCount + 23) throw new AssertionError("Missing colors");
        try {
            var directory = java.nio.file.Files.createTempDirectory("vip-local-check");
            var config = directory.resolve("settings.json");
            VipSettings.enabled = false;
            VipSettings.roseCrown = true;
            VipSettings.select("gold");
            VipSettings.save(config);
            VipSettings.enabled = true;
            VipSettings.roseCrown = false;
            VipSettings.select("red");
            VipSettings.load(config);
            if (VipSettings.enabled || !VipSettings.color().id().equals("gold") || !VipSettings.roseCrown) throw new AssertionError("Settings not restored");
            java.nio.file.Files.delete(config);
            java.nio.file.Files.delete(directory);
        } catch (java.io.IOException e) { throw new AssertionError(e); }
        var players = java.util.stream.IntStream.range(0, 120).mapToObj(i -> String.format("Player%03d", i)).toList();
        var ordered = players.stream().sorted(VipOrder.<String>selfFirst(java.util.Comparator.naturalOrder(), "Player119"::equals)).limit(80).toList();
        if (!ordered.getFirst().equals("Player119") || ordered.size() != 80) throw new AssertionError("Self missing from large TAB");
        if (!ordered.subList(1, 80).equals(players.subList(0, 79))) throw new AssertionError("Other players reordered");
        VipSettings.enabled = true;
        VipSettings.roseCrown = false;
        if (!VipMatch.detect(true, true, 1, 1)) throw new AssertionError("Duel not detected");
        if (!VipMatch.detect(false, true, 60, 58)) throw new AssertionError("UHC not detected");
        if (VipMatch.detect(false, false, 10, 10)) throw new AssertionError("Lobby detected as match");
        if (VipMatch.detect(true, true, 0, 0)) throw new AssertionError("Empty match detected");
        VipSettings.select("original_pink");
        System.out.println("Passed: chat and white messages, join/leave and preserved counters, OFF, all 39 colors with per-letter gradient endpoints, spacing, persistence, 120-player TAB ordering.");
    }
    private static void check(String input, String expected) {
        String actual = VipStyle.chat(Text.literal(input), "Alice").getString();
        if (!actual.equals(expected)) throw new AssertionError(input + " => " + actual);
    }
}



