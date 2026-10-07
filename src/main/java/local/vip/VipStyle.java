package local.vip;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.*;
import net.minecraft.util.Identifier;

public final class VipStyle {
    // Badge font
    private static final Style BADGE = Style.EMPTY
        .withFont(new StyleSpriteSource.Font(Identifier.of("vip_local", "badge")))
        .withColor(0xFFFFFF).withBold(false).withItalic(false);
    private VipStyle() {}

    // Local player
    public static String username() {
        MinecraftClient c = MinecraftClient.getInstance();
        return c.player == null ? c.getSession().getUsername() : c.player.getGameProfile().name();
    }
    public static boolean isSelf(Entity entity) {
        return entity == MinecraftClient.getInstance().player;
    }
    // Name and badge
    public static Text name() {
        return name(username());
    }
    static Text name(String username) {
        var color = VipSettings.color();
        MutableText result = Text.empty().append(badge(color).copy().append(Text.literal("\ue002").setStyle(BADGE)));
        if (!color.id().equals("rainbow")) return result.append(Text.literal(username).setStyle(Style.EMPTY.withColor(color.colorAt(1))));
        int[] letters = username.codePoints().toArray();
        for (int i = 0; i < letters.length; i++)
            result.append(Text.literal(new String(Character.toChars(letters[i])))
                .setStyle(Style.EMPTY.withColor(color.colorAt(letters.length < 2 ? 0 : (double)i / (letters.length - 1)))));
        return result;
    }
    public static Text badge(VipSettings.ColorOption color) {
        for (int i = 0; i < VipSettings.COLORS.length; i++) {
            if (VipSettings.COLORS[i].id().equals(color.id()))
                return Text.literal(String.valueOf((char)(0xE100 + i))).setStyle(BADGE);
        }
        return Text.literal("\ue100").setStyle(BADGE);
    }

    // Style only our sender prefix or our exact UHC join/leave notification.
    // Chat messages
    public static Text chat(Text original) {
        Text styled = chat(original, username());
        if (styled != original || isPlayerMessage(original.getString())) return styled;
        return server(original);
    }
    public static Text chatPlayer(Text original) { return chat(original, username()); }
    public static boolean isPlayerMessage(String plain) {
        if (plain.matches("(?s)^\\s*<[^>]+>.*")) return true;
        int delimiter = plain.indexOf(':');
        if (delimiter < 0) delimiter = plain.indexOf('»');
        if (delimiter < 0) return false;
        var handler = MinecraftClient.getInstance().getNetworkHandler();
        if (handler == null) return true;
        String prefix = plain.substring(0, delimiter).stripTrailing();
        for (var entry : handler.getPlayerList())
            if (Pattern.compile("(?<![A-Za-z0-9_])" + Pattern.quote(entry.getProfile().name()) + "$").matcher(prefix).find()) return true;
        return false;
    }
    public static Text server(Text original) { return server(original, username()); }
    // HUD and leaderboard text
    public static Text hud(Text original) {
        if (!VipSettings.enabled || !original.getString().contains(username())) return original;
        if (!VipSettings.enabled || !VipMatch.active()) return server(original);
        String plain = original.getString(), user = username();
        Matcher match = Pattern.compile("(?<![A-Za-z0-9_])" + Pattern.quote(user) + "(?![A-Za-z0-9_])").matcher(plain);
        MutableText result = Text.empty();
        int position = 0;
        boolean changed = false;
        while (match.find()) {
            int start = match.start();
            if (start >= 2 && plain.charAt(start - 1) == '\ue002' && plain.charAt(start - 2) >= '\ue100' && plain.charAt(start - 2) <= '\ue126') start -= 2;
            appendSlice(result, original, position, Math.max(position, start));
            result.append(Text.literal(user).withColor(0x55FF55));
            position = match.end();
            changed = true;
        }
        if (!changed) return original;
        appendSlice(result, original, position, plain.length());
        return result;
    }
    // Text that is already laid out
    public static OrderedText rendered(OrderedText original) {
        if (!VipSettings.enabled || local.vip.RenderScope.chat) return original;
        StringBuilder plain = new StringBuilder();
        original.accept((index, style, codePoint) -> { plain.appendCodePoint(codePoint); return true; });
        if (plain.indexOf(username()) < 0) return original;
        MutableText text = Text.empty();
        original.accept((index, style, codePoint) -> {
            text.append(Text.literal(new String(Character.toChars(codePoint))).setStyle(style));
            return true;
        });
        Text styled = hud(text);
        return styled == text ? original : styled.asOrderedText();
    }
    // Server mentions
    static Text server(Text original, String username) {
        if (!VipSettings.enabled || original.getString().indexOf('\ue002') >= 0) return original;
        Matcher match = Pattern.compile("(?<![A-Za-z0-9_])" + Pattern.quote(username) + "(?![A-Za-z0-9_])").matcher(original.getString());
        MutableText result = Text.empty();
        int position = 0;
        boolean changed = false;
        while (match.find()) {
            appendSlice(result, original, position, match.start());
            result.append(name(username));
            position = match.end();
            changed = true;
        }
        if (!changed) return original;
        appendSlice(result, original, position, original.getString().length());
        return result;
    }
    // Keep the surrounding text
    private static void appendSlice(MutableText target, Text original, int start, int end) {
        int[] position = {0};
        original.visit((style, value) -> {
            int from = Math.max(0, start - position[0]), to = Math.min(value.length(), end - position[0]);
            if (to > from) target.append(Text.literal(value.substring(from, to)).setStyle(style));
            position[0] += value.length();
            return Optional.<Object>empty();
        }, Style.EMPTY);
    }
    static Text chat(Text original, String username) {
        if (!VipSettings.enabled) return original;
        String plain = original.getString();
        Matcher match = Pattern.compile("(?<![A-Za-z0-9_])" + Pattern.quote(username)
            + "(?![A-Za-z0-9_])").matcher(plain);
        if (!match.find()) return original;
        int start = match.start(), end = match.end();
        int delimiter = -1;
        for (int i = 0; i < plain.length(); i++) {
            if (":>»➜→".indexOf(plain.charAt(i)) >= 0) { delimiter = i; break; }
        }
        if (start > 80) return original;
        boolean notification = Pattern.compile("\\s+has (?:joined|left)!(?:\\s*\\(\\d+/\\d+\\))?\\s*$")
            .matcher(plain.substring(end)).matches();
        boolean sender = delimiter >= end && delimiter - end <= 8
            && plain.substring(end, delimiter).isBlank();
        if (notification && delimiter >= 0 && delimiter < start) return original;
        if (!notification && !sender) return original;
        int whiteFrom = notification ? Integer.MAX_VALUE : delimiter + 1;
        record Span(String value, Style style) {}
        List<Span> spans = new ArrayList<>();
        original.visit((style, value) -> { spans.add(new Span(value, style)); return Optional.<Object>empty(); }, Style.EMPTY);
        MutableText result = Text.empty();
        int offset = 0;
        boolean inserted = false;
        for (Span span : spans) {
            int length = span.value().length();
            int spanEnd = offset + length;
            if (offset < start) {
                int count = Math.min(length, start - offset);
                result.append(Text.literal(span.value().substring(0, count)).setStyle(span.style()));
            }
            if (!inserted && spanEnd > start) { result.append(name(username)); inserted = true; }
            if (spanEnd > end) {
                int from = Math.max(0, end - offset);
                int split = Math.max(from, Math.min(length, whiteFrom - offset));
                if (split > from)
                    result.append(Text.literal(span.value().substring(from, split)).setStyle(span.style()));
                if (split < length)
                    result.append(Text.literal(span.value().substring(split)).setStyle(span.style().withColor(0xFFFFFF)));
            }
            offset = spanEnd;
        }
        return result;
    }
}

