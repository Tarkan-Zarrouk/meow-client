package Main.main.meow.client.manager;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.module.Module;
import Main.main.meow.client.module.modules.hud.NotificationsModule;
import Main.main.meow.client.ui.hud.QolHudRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;

// not entirely ai but code completions helped fix roadblocks lol
public final class NotificationManager {
    private static String message;
    private static long shownAt;
    private static final long ENTER_MS = 240L;
    private static final long DISPLAY_MS = 2600L;
    private static final long EXIT_MS = 260L;

    private NotificationManager() {
    }

    public static void moduleChanged(Module module) {
        NotificationsModule notifications = (NotificationsModule) MeowClient.MODULE_MANAGER.byName("Notifications");
        // the module itself can still announce being turned off
        if (notifications == null || (!notifications.isEnabled() && module != notifications)) return;
        String status;
        if(module.isEnabled()) {
            status = "enabled";
        } else {
            status = "disabled";
        }
        if (notifications.mode() == NotificationsModule.Mode.CHAT) {
            Minecraft client = Minecraft.getInstance();
            if (client.player != null) {
                MutableComponent prefix = Component.literal("[MeowMod] ").withStyle(ChatFormatting.LIGHT_PURPLE);
                client.gui.getChat().addClientSystemMessage(prefix.append(Component.literal(module.name() + " " + status).withStyle(ChatFormatting.WHITE)));
            }
            return;
        }
        long now = System.currentTimeMillis();
        // keep the slide continuous when a toast is replaced while it is still on screen
        long elapsed = now - shownAt;
        float visible = 0f;
        if (message != null) {
            visible = visibility(elapsed);
        }
        message = module.name() + " " + status;
        if (visible >= 1f) {
            shownAt = now - ENTER_MS;
        } else {
            shownAt = now - Math.round(visible * ENTER_MS);
        }
    }

    // 0..1 slide/fade amount for a toast that has been alive for elapsed ms
    private static float visibility(long elapsed) {
        if (elapsed < ENTER_MS) {
            return Math.max(0f, elapsed / (float) ENTER_MS);
        }
        if (elapsed > DISPLAY_MS) {
            return Math.max(0f, 1f - (elapsed - DISPLAY_MS) / (float) EXIT_MS);
        }
        return 1f;
    }

    public static void render(net.minecraft.client.gui.GuiGraphicsExtractor graphics, Minecraft client) {
        if (message == null) return;
        NotificationsModule notifications = (NotificationsModule) MeowClient.MODULE_MANAGER.byName("Notifications");
        if (notifications == null || notifications.mode() != NotificationsModule.Mode.ALERT) {
            message = null;
            return;
        }
        long elapsed = System.currentTimeMillis() - shownAt;
        float progress = visibility(elapsed);
        if (elapsed > DISPLAY_MS + EXIT_MS || progress <= 0f) {
            message = null;
            return;
        }
        float eased = progress * progress * (3f - 2f * progress); // math: smoothstep easing function
        int width = QolHudRenderer.getTextWidth(client, message) + 28;
        int x = client.getWindow().getGuiScaledWidth() - width - 12; // 12px from right edge
        int y = 12 - Math.round((1f - eased) * 18f); // 18px slide-in from top
        int alpha = Math.round(eased * 255f); // 0-255 alpha value or smth
        roundedFill(graphics, x, y, width, 32, alpha << 24 | 0xff202632, 4); // 32px height, 4px corner radius, bitwise OR to combine alpha and color
        // the timer drains from full to empty while the toast is held, so it is already gone when the toast slides away
        float remaining = 1f;
        if (notifications.showTimer()) {
            if (elapsed <= ENTER_MS) {
                remaining = 1f;
            } else if (elapsed >= DISPLAY_MS) {
                remaining = 0f;
            } else {
                remaining = 1f - (elapsed - ENTER_MS) / (float) (DISPLAY_MS - ENTER_MS);
            }
        }
        int timerWidth = Math.round(width * remaining);
        if (timerWidth > 0) {
            drawTimerBorder(graphics, x, y, width, timerWidth, alpha << 24 | 0xffc52dbd); // bitwise OR to combine alpha and color
        }
        graphics.text(client.font, QolHudRenderer.styled(message), x + 14, y + 11, alpha << 24 | 0xffffffff, false); // bitwsiue to combine
    }

    private static void roundedFill(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int x, int y, int width, int height, int color, int radius) {
        radius = Math.min(radius, Math.min(width / 2, height / 2));
        for (int row = 0; row < height; row++) {
            int inset = 0;
            if (row < radius) {
                inset = roundedInset(radius, row);
            } else if (row >= height - radius) {
                inset = roundedInset(radius, height - row - 1); // bottom corner
            }

            graphics.fill(x + inset, y + row, x + width - inset, y + row + 1, color); // horizontal line current riow
        }
    }

    private static void drawTimerBorder(net.minecraft.client.gui.GuiGraphicsExtractor graphics, int x, int y, int width, int timerWidth, int color) {
        int radius = 4;
        boolean reachesRightEdge = timerWidth >= width;
        for (int row = 0; row < 2; row++) {
            int bottomDistance = 1 - row; // distance from bottom of the timer bar
            int leftInset = roundedInset(radius, bottomDistance);
            int rightInset;
            if (reachesRightEdge) {
                rightInset = roundedInset(radius, bottomDistance);
            } else {
                rightInset = 0;
            }
            graphics.fill(x + leftInset, y + 30 + row, x + timerWidth - rightInset, y + 31 + row, color);
        }
    }

    private static int roundedInset(int radius, int distance) {
        double circle = radius - 0.5;
        double offset = distance - circle;
        return Math.max(0, (int) Math.ceil(circle - Math.sqrt(Math.max(0, circle * circle - offset * offset)))); // read a minecraft clients code for this, i have no idea how this works but it does and it looks good so im not complaining
    }
}
