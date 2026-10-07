package local.vip;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.text.Text;

public final class VipScreen extends Screen {
    // Menu layout
    private int top;
    private int page;
    private static final int PAGE_SIZE = 12;
    public VipScreen() { super(Text.literal("Hoplite VIP")); }
    @Override protected void init() {
        top = Math.max(4, (height - 236) / 2);
        int panel = Math.min(440, width - 16);
        int left = (width - panel) / 2;
        addDrawableChild(ButtonWidget.builder(Text.literal("Mod: " + (VipSettings.enabled ? "ON" : "OFF")), button -> {
            VipSettings.enabled = !VipSettings.enabled;
            changed();
        }).dimensions(left, top + 42, (panel - 4) / 2, 20).build());
        addDrawableChild(ButtonWidget.builder(Text.literal("Rose crown: " + (VipSettings.roseCrown ? "ON" : "OFF")), button -> {
            VipSettings.roseCrown = !VipSettings.roseCrown;
            changed();
        }).dimensions(left + (panel + 4) / 2, top + 42, (panel - 4) / 2, 20).build());
        // Color pages
        int cellWidth = (panel - 12) / 4;
        for (int slot = 0; slot < PAGE_SIZE && page * PAGE_SIZE + slot < VipSettings.COLORS.length; slot++) {
            int displayIndex = page * PAGE_SIZE + slot;
            // Hoplite colors first
            int i = displayIndex < 12 ? 17 + displayIndex : displayIndex < 22 ? 29 + displayIndex - 12 : displayIndex - 22;
            var option = VipSettings.COLORS[i];
            Text label = Text.empty().append(VipStyle.badge(option)).append(Text.literal(option.id().equals(VipSettings.color().id()) ? " ✓" : ""));
            addDrawableChild(ButtonWidget.builder(label, button -> {
                VipSettings.select(option.id());
                changed();
            }).dimensions(left + (slot % 4) * (cellWidth + 4), top + 84 + (slot / 4) * 24, cellWidth, 20)
                .tooltip(Tooltip.of(Text.literal(option.label() + String.format(" (#%06X)", option.rgb())))).build());
        }
        // Page buttons
        int pages = (VipSettings.COLORS.length + PAGE_SIZE - 1) / PAGE_SIZE;
        var previous = addDrawableChild(ButtonWidget.builder(Text.literal("<"), button -> { page--; clearAndInit(); })
            .dimensions(left, top + 164, 40, 20).build());
        previous.active = page > 0;
        var next = addDrawableChild(ButtonWidget.builder(Text.literal(">"), button -> { page++; clearAndInit(); })
            .dimensions(left + panel - 40, top + 164, 40, 20).build());
        next.active = page + 1 < pages;
        addDrawableChild(ButtonWidget.builder(Text.literal("Close"), button -> close())
            .dimensions(left, top + 212, panel, 20).build());
    }
    // Save menu changes
    private void changed() {
        VipSettings.save();
        if (client.inGameHud != null) client.inGameHud.getChatHud().reset();
        clearAndInit();
    }
    // Draw the menu
    @Override public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, top + 2, 0xFFFFFFFF);
        context.fill(width / 2 - 120, top + 17, width / 2 + 120, top + 36, 0xFF888888);
        context.drawCenteredTextWithShadow(textRenderer, VipStyle.name(), width / 2, top + 23, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Name & VIP badge color"), width / 2, top + 69, 0xFFFFFFFF);
        // Page buttons
        int pages = (VipSettings.COLORS.length + PAGE_SIZE - 1) / PAGE_SIZE;
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("Page " + (page + 1) + " / " + pages), width / 2, top + 170, 0xFFFFFFFF);
        context.drawCenteredTextWithShadow(textRenderer, Text.literal("By Nakazukey"), width / 2, top + 197, 0xFFAAAAAA);
        super.render(context, mouseX, mouseY, delta);
    }
    // Simple background, no extra blur
    @Override public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {
        context.fill(0, 0, width, height, 0xD0101018);
    }
    @Override public boolean shouldPause() { return false; }
}


