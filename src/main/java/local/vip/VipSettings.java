package local.vip;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import org.slf4j.LoggerFactory;
import net.fabricmc.loader.api.FabricLoader;

public final class VipSettings {
    // Color blending
    public record ColorOption(String id, String label, int rgb, int... stops) {
        public int colorAt(double position) {
            if (stops.length < 2) return rgb;
            double scaled = Math.max(0, Math.min(1, position)) * (stops.length - 1);
            int segment = Math.min(stops.length - 2, (int)scaled);
            double amount = scaled - segment;
            int a = stops[segment], b = stops[segment + 1], result = 0;
            for (int shift : new int[] {16, 8, 0}) {
                int channel = (int)Math.round(((a >> shift) & 255) * (1 - amount) + ((b >> shift) & 255) * amount);
                result |= channel << shift;
            }
            return result;
        }
    }
    // Available colors
    public static final ColorOption[] COLORS = {
        new ColorOption("original_pink", "Original pink", 0xD56191),
        new ColorOption("black", "Black", 0x000000),
        new ColorOption("dark_blue", "Dark blue", 0x0000AA),
        new ColorOption("dark_green", "Dark green", 0x00AA00),
        new ColorOption("dark_aqua", "Dark aqua", 0x00AAAA),
        new ColorOption("dark_red", "Dark red", 0xAA0000),
        new ColorOption("dark_purple", "Dark purple", 0xAA00AA),
        new ColorOption("gold", "Gold", 0xFFAA00),
        new ColorOption("gray", "Gray", 0xAAAAAA),
        new ColorOption("dark_gray", "Dark gray", 0x555555),
        new ColorOption("blue", "Blue", 0x5555FF),
        new ColorOption("green", "Green", 0x55FF55),
        new ColorOption("aqua", "Aqua", 0x55FFFF),
        new ColorOption("red", "Red", 0xFF5555),
        new ColorOption("light_purple", "Light purple", 0xFF55FF),
        new ColorOption("yellow", "Yellow", 0xFFFF55),
        new ColorOption("white", "White", 0xFFFFFF),
        new ColorOption("hoplite_gold", "Hoplite gold", 0xEFC104),
        new ColorOption("hoplite_lime", "Hoplite lime", 0x7DDD00),
        new ColorOption("hoplite_cyan", "Hoplite cyan", 0x27D4DE),
        new ColorOption("hoplite_red", "Hoplite red", 0xF25A49),
        new ColorOption("hoplite_pink", "Hoplite pink", 0xF186B8),
        new ColorOption("hoplite_violet", "Hoplite violet", 0xBD7DF6),
        new ColorOption("hoplite_teal", "Hoplite teal", 0x009795),
        new ColorOption("hoplite_forest", "Hoplite forest", 0x0C6B33),
        new ColorOption("hoplite_crimson", "Hoplite crimson", 0xA50F0F),
        new ColorOption("hoplite_purple", "Hoplite purple", 0x5A2677),
        new ColorOption("hoplite_gray", "Hoplite gray", 0x797979),
        new ColorOption("hoplite_black", "Hoplite black", 0x212121),
        new ColorOption("gradient_lime", "Hoplite lime gradient", 0x43B947, 0x43B947, 0x77CC53, 0x64B50C, 0xBDE658, 0xDFF05F),
        new ColorOption("gradient_ocean", "Hoplite ocean gradient", 0x00A352, 0x00A352, 0x00855C, 0x004549, 0x003472, 0x001562),
        new ColorOption("gradient_fire", "Hoplite fire gradient", 0xFC5C5C, 0xFC5C5C, 0xFC6454, 0xB24515, 0xFC8732, 0xFC9028),
        new ColorOption("gradient_prism", "Hoplite prism gradient", 0x29DE8F, 0x29DE8F, 0x809FFC, 0x8457F2, 0xE86AFC, 0xFC71A1),
        new ColorOption("sunset", "Sunset", 0xFF6B35, 0xFF6B35, 0xF05AA6, 0x8F51E5),
        new ColorOption("aurora", "Aurora", 0x55FF88, 0x55FF88, 0x55FFFF, 0x9A65FF),
        new ColorOption("ice", "Ice", 0xFFFFFF, 0xFFFFFF, 0x55FFFF, 0x5555FF),
        new ColorOption("royal", "Royal", 0xFFAA00, 0xFFAA00, 0xE86FBA, 0x772FE3),
        new ColorOption("rainbow", "Rainbow", 0xFF5555, 0xFF5555, 0xFFAA00, 0xFFFF55, 0x55FF55, 0x55FFFF, 0x5555FF, 0xFF55FF),
        new ColorOption("rose_gold", "Rose gold", 0xF186B8, 0xF186B8, 0xFFCF77)
    };
    // Current settings
    public static boolean enabled = true;
    public static boolean roseCrown = false;
    private static String selectedColor = "original_pink";
    private static final Gson JSON = new GsonBuilder().setPrettyPrinting().create();
    private record Saved(boolean enabled, String color, boolean roseCrown) {}
    private VipSettings() {}
    // Color selection
    public static ColorOption color() {
        for (var option : COLORS) if (option.id().equals(selectedColor)) return option;
        return COLORS[0];
    }
    public static void select(String id) {
        for (var option : COLORS) if (option.id().equals(id)) { selectedColor = id; return; }
        selectedColor = "original_pink";
    }
    // Config file
    private static Path path() { return FabricLoader.getInstance().getConfigDir().resolve("vip-local.json"); }
    public static void load() {
        load(path());
    }
    static void load(Path path) {
        if (!Files.isRegularFile(path)) return;
        try (var reader = Files.newBufferedReader(path)) {
            Saved saved = JSON.fromJson(reader, Saved.class);
            if (saved != null) { enabled = saved.enabled(); select(saved.color()); roseCrown = saved.roseCrown(); }
        } catch (IOException | RuntimeException e) {
            LoggerFactory.getLogger("vip_local").warn("Could not read Hoplite VIP settings", e);
        }
    }
    public static void save() { save(path()); }
    static void save(Path path) {
        try {
            Files.createDirectories(path.getParent());
            Files.writeString(path, JSON.toJson(new Saved(enabled, color().id(), roseCrown)));
        } catch (IOException e) {
            LoggerFactory.getLogger("vip_local").warn("Could not save Hoplite VIP settings", e);
        }
    }
}


