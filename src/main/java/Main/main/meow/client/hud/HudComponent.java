package Main.main.meow.client.hud;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public abstract class HudComponent {
    private final String id;
    private int x;
    private int y;
    private int width;
    private int height;

    protected HudComponent(String id, int x, int y, int width, int height) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public final String id() { return id; }
    public final int x() { return x; }
    public final int y() { return y; }
    public final int width() { return width; }
    public final int height() { return height; }
    public final void moveTo(int x, int y, int screenWidth, int screenHeight) {
        this.x = Math.max(0, Math.min(x, screenWidth - width));
        this.y = Math.max(0, Math.min(y, screenHeight - height));
    }
    public final void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public final boolean contains(double mouseX, double mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y && mouseY <= y + height;
    }
    public abstract void render(GuiGraphicsExtractor graphics, Minecraft client, boolean editor);
}
