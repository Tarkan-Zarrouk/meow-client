package Main.main.meow.client.ui.hud;

import Main.main.meow.client.module.HudDisplayModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class HudComponent {
    private final String id;
    private int x;
    private int y;
    private int width;
    private int height;
    private final HudDisplayModule module;

    protected HudComponent(String id, int x, int y, HudDisplayModule module) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.module = module;
        refreshSize(Minecraft.getInstance());
    }

    public final String id() { return id; }
    public final int x() { return x; }
    public final int y() { return y; }
    public final int width() { return width; }
    public final int height() { return height; }
    public final void moveTo(int x, int y, int screenWidth, int screenHeight) {
        refreshSize(Minecraft.getInstance());
        this.x = Math.max(0, Math.min(x, screenWidth - width));
        this.y = Math.max(0, Math.min(y, screenHeight - height));
    }
    public final void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public final boolean contains(double mouseX, double mouseY) {
        refreshSize(Minecraft.getInstance());
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
    public final void render(GuiGraphicsExtractor graphics, Minecraft client, boolean editor) {
        refreshSize(client);
        renderComponent(graphics, client, editor);
    }

    private void refreshSize(Minecraft client) {
        width = Math.max(0, module.panelWidth(client));
        height = Math.max(0, module.panelHeight(client));
    }

    protected abstract void renderComponent(GuiGraphicsExtractor graphics, Minecraft client, boolean editor);
}
