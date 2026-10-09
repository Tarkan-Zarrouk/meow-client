package Main.main.meow.client.hud;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.modules.Module;
import Main.main.meow.client.modules.hud.NotificationsModule;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;

public final class NotificationManager {
    private static String message;
    private static long shownAt;
    private static final long DISPLAY_MS = 2600L;
    private static final long EXIT_MS = 260L;

    private NotificationManager() {
    }

    public static void moduleChanged(Module module) {
        NotificationsModule notifications = (NotificationsModule) MeowClient.MODULE_MANAGER.byName("Notifications");
        if (notifications == null || !notifications.isEnabled()) return;
        String status = module.isEnabled() ? "enabled" : "disabled";
        if (notifications.mode() == NotificationsModule.Mode.CHAT) {
            Minecraft client = Minecraft.getInstance();
            if (client.player != null) {
                MutableComponent prefix = Component.literal("[MeowMod] ").withStyle(ChatFormatting.LIGHT_PURPLE);
                client.gui.getChat().addClientSystemMessage(prefix.append(Component.literal(module.name() + " " + status).withStyle(ChatFormatting.WHITE)));
            }
            return;
        }
        message = module.name() + " " + status;
        shownAt = System.currentTimeMillis();
    }

    public static void render(net.minecraft.client.gui.GuiGraphicsExtractor graphics, Minecraft client) {
        if (message == null) return;
        // Check if notifications are still enabled and in ALERT mode (this was mostly inspired by NextUI's toast component lol)
        NotificationsModule notifications = (NotificationsModule) MeowClient.MODULE_MANAGER.byName("Notifications");
        if (notifications == null || !notifications.isEnabled() || notifications.mode() != NotificationsModule.Mode.ALERT) {
            message = null;
            return;
        }
        long elapsed = System.currentTimeMillis() - shownAt;
        float progress;
        if(elapsed < 240L) {
            progress = elapsed / 240f;
        } else if(elapsed > DISPLAY_MS) {
            progress = Math.max(0f, 1f - (elapsed - DISPLAY_MS) / (float) EXIT_MS);
        } else {
            progress = 1f;
        }
        if (progress <= 0f) {
            message = null;
            return;
        }
        float eased = progress * progress * (3f - 2f * progress); // math: smoothstep easing function
        int width = client.font.width(message) + 28; // 28 = 14px padding on each side
        int x = client.getWindow().getGuiScaledWidth() - width - 12; // 12px from right edge
        int y = 12 - Math.round((1f - eased) * 18f); // 18px slide-in from top
        int alpha = Math.round(eased * 255f); // 0-255 alpha value or smth
        roundedFill(graphics, x, y, width, 32, alpha << 24 | 0xff202632, 4); // 32px height, 4px corner radius, bitwise OR to combine alpha and color
        long lifetime = DISPLAY_MS + EXIT_MS; // total lifetime of the notification in milliseconds
        float remaining;
        if (elapsed < DISPLAY_MS) {
            remaining = 1f;
        } else if (elapsed > lifetime) {
            remaining = 0f;
        } else {
            remaining = 1f - (elapsed - DISPLAY_MS) / (float) EXIT_MS;
        }
        int timerWidth = Math.round(width * remaining);
        if (timerWidth > 0) {
            drawTimerBorder(graphics, x, y, width, timerWidth, alpha << 24 | 0xffc52dbd); // bitwise OR to combine alpha and color
        }
        graphics.text(client.font, Component.literal(message), x + 14, y + 11, alpha << 24 | 0xffffffff, false); // bitwsiue to combine
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
