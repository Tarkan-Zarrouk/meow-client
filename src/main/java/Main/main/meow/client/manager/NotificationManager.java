package Main.main.meow.client.manager;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.module.Module;
import Main.main.meow.client.module.modules.hud.modules.notification.Mode;
import Main.main.meow.client.module.modules.hud.modules.notification.NotificationPosition;
import Main.main.meow.client.module.modules.hud.modules.notification.NotificationsModule;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.ChatFormatting;

public final class NotificationManager {
    private static String message;
    private static long shownAt;
    private static long enterMs() { return NotificationsModule.getEnterTime(); }
    private static long displayMs() { return NotificationsModule.getDisplayTime(); }
    private static long exitMs() { return NotificationsModule.getExitTime(); }

    public static void moduleChanged(Module module) {
        NotificationsModule notifications = (NotificationsModule) MeowClient.getModuleManager().byName("Notifications");
        // the module itself can still announce being turned off
        if (notifications == null || (!notifications.getEnabled() && module != notifications)) return;
        String status;
        if(module.getEnabled()) {
            status = "enabled";
        } else {
            status = "disabled";
        }
        if(notifications.mode() == Mode.CHAT) {
            sendChatMessage(module.getName() + " " + status);
            return;
        }
        long now = System.currentTimeMillis();
        // keep the slide continuous when a toast is replaced while it is still on screen
        long elapsed = now - shownAt;
        float visible = 0f;
        if (message != null) {
            visible = visibility(elapsed);
        }
        message = module.getName() + " " + status;
        if (visible >= 1f) {
            shownAt = now - enterMs();
        } else {
            shownAt = now - (int) (visible * enterMs());
        }
    }
    // non-module specific notifier
    public static void notify(String msg) {
        NotificationsModule notifications = (NotificationsModule) MeowClient.getModuleManager().byName("Notifications");
        if (notifications.mode() == Mode.CHAT) {
            NotificationManager.sendChatMessage(msg);
            return;
        }
        long now = System.currentTimeMillis();
        // keep the slide continuous when a toast is replaced while it is still on screen
        long elapsed = now - shownAt;
        float visible = 0f;
        if (message != null) {
            visible = visibility(elapsed);
        }
        message = msg;
        if (visible >= 1f) {
            shownAt = now - enterMs();
        } else {
            shownAt = now - (int) (visible * enterMs());
        }
    }

    // 0..1 slide/fade amount for a toast that has been alive for elapsed ms
    private static float visibility(long elapsed) {
        if (elapsed < enterMs()) {
            return Math.max(0f, elapsed / (float) enterMs());
        }
        if (elapsed > displayMs()) {
            return Math.max(0f, 1f - (elapsed - displayMs()) / (float) exitMs());
        }
        return 1f;
    }

    public static void sendChatMessage(String msg) {
        Minecraft client = Minecraft.getInstance();
        if (client.player != null) {
            MutableComponent prefix = Component.literal("[MeowMod] ").withStyle(ChatFormatting.LIGHT_PURPLE);
            client.gui.getChat().addClientSystemMessage(prefix.append(Component.literal(msg).withStyle(ChatFormatting.WHITE)));
        }
    }

    public static void render(GuiGraphicsExtractor graphics, Minecraft client) {
        if (message == null) return;
        drawNotification(graphics, client, message);
    }
    public static void drawNotification(GuiGraphicsExtractor graphics, Minecraft client, String msg) {
        NotificationsModule notifications = (NotificationsModule) MeowClient.getModuleManager().byName("Notifications");
        long elapsed = System.currentTimeMillis() - shownAt;
        float progress = visibility(elapsed);
        if (elapsed > displayMs() + exitMs() || progress <= 0f) {
            return;
        }
        float eased = (float) Math.pow(progress, 2) * (3f - (2f * progress));
        int width = TextUtil.getTextWidth(client, msg) + NotificationsModule.getNotificationWidth();
        int height = NotificationsModule.getNotificationHeight();
        int margin = 12;
        NotificationPosition position = NotificationsModule.getNotificationPosition();
        // custom positions
        int xPos = switch (position) {
            case TOP_LEFT, BOTTOM_LEFT, LEFT_CENTER -> margin;
            case TOP_MIDDLE, BOTTOM_MIDDLE -> (client.getWindow().getGuiScaledWidth() - width) / 2;
            case TOP_RIGHT, BOTTOM_RIGHT, RIGHT_CENTER -> client.getWindow().getGuiScaledWidth() - width - margin;
        };
        int yPos = switch (position) {
            case TOP_LEFT, TOP_RIGHT, TOP_MIDDLE -> margin;
            case BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM_MIDDLE -> client.getWindow().getGuiScaledHeight() - height - margin;
            case LEFT_CENTER, RIGHT_CENTER -> (client.getWindow().getGuiScaledHeight() - height) / 2;
        };
        int slide = (int) ((1f - eased) * (float) NotificationsModule.getSlideTimeVal());
        switch (position) {
            case TOP_LEFT, TOP_RIGHT, TOP_MIDDLE -> yPos -= slide;
            case BOTTOM_LEFT, BOTTOM_RIGHT, BOTTOM_MIDDLE -> yPos += slide;
            case LEFT_CENTER -> xPos -= slide;
            case RIGHT_CENTER -> xPos += slide;
        }
        if (NotificationsModule.getShowBackground()) {
            int background = NotificationsModule.getBackgroundColor();
            RenderUtil.roundedFill(graphics, xPos, yPos, width, height, background, NotificationsModule.getTopLeftRound(), NotificationsModule.getTopRightRound(), NotificationsModule.getBottomLeftRound(), NotificationsModule.getBottomRightRound(), NotificationsModule.getBorderRadiusValue());
        }
        // the timer drains from full to empty while the toast is held, so it is already gone when the toast slides away
        float remaining = 1f;
        if (notifications.showTimer()) {
            if (elapsed <= enterMs()) {
                remaining = 1f;
            } else if (elapsed >= displayMs()) {
                remaining = 0f;
            } else {
                remaining = 1f - (elapsed - enterMs()) / (float) (displayMs() - enterMs());
            }
        }
        int timerEnclosure = Math.clamp(width / 4, 0, NotificationsModule.getBorderRadiusValue() / 2 + 2);
        int timerMaxWidth = width - timerEnclosure * 2;
        int timerWidth = (int) (timerMaxWidth * remaining);
        if (notifications.showTimer() && timerWidth > 0) {
            RenderUtil.roundedFill(graphics, xPos + timerEnclosure, yPos + height - 2, timerWidth, 2, NotificationsModule.getTimerColor(), NotificationsModule.getTimerTopLeftRound(), NotificationsModule.getTimerTopRightRound(), NotificationsModule.getTimerBottomLeftRound(), NotificationsModule.getTimerBottomRightRound(), 1);
        }
        RenderUtil.drawText(graphics, client, msg, xPos + (NotificationsModule.getNotificationWidth() / 2), yPos + (NotificationsModule.getNotificationHeight() / 2) - 4, NotificationsModule.getTextColor());
    }
}
