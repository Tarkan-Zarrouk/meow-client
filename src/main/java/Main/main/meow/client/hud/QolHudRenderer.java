package Main.main.meow.client.hud;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.modules.Module;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class QolHudRenderer {
    private static final int PANEL = 0xb9161d2a;
    private static final int TEXT = 0xfff2f0f4;
    private static final int MUTED = 0xffa7a5af;
    private static final int PURPLE = 0xffc45ad0;
    private static final int WARNING = 0xffff9c52;
    private static final float[] KEY_LEVELS = new float[5];
    private static long lastKeyTime = System.nanoTime();

    private QolHudRenderer() {
    }

    public static void render(GuiGraphicsExtractor graphics, Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null || client.level == null) {
            return;
        }
        MeowClient.HUD_COMPONENTS.render(graphics, client, false);
        NotificationManager.render(graphics, client);
    }

    public static int drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        BlockPos pos = player.blockPosition();
        String biome = client.level.getBiome(pos).unwrapKey()
                .map(key -> key.identifier().getPath().replace('_', ' ')).orElse("unknown");
        PlayerInfo info = client.getConnection() == null ? null : client.getConnection().getPlayerInfo(player.getUUID());
        int ping = info == null ? 0 : info.getLatency();
        String direction = player.getDirection().getName().toUpperCase(Locale.ROOT);
        List<String> lines = new ArrayList<>();
        if (enabled("Coordinates")) lines.add("XYZ " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
        if (enabled("Direction")) lines.add(direction);
        if (enabled("Biome")) lines.add(biome);
        if (enabled("CPS")) lines.add("CPS " + MeowClient.cps);
        if (enabled("BPS")) lines.add("BPS " + String.format(Locale.ROOT, "%.2f", MeowClient.bps));
        if (enabled("Clock")) lines.add(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        lines.add("FPS " + client.getFps() + "  |  Ping " + ping + " ms");
        panel(g, x, y, 210, lines.size() * 14 + 12);
        for (int i = 0; i < lines.size(); i++) {
            text(g, client, lines.get(i), x + 8, y + 7 + i * 14, i == lines.size() - 1 ? PURPLE : TEXT);
        }
        if (enabled("Totem Counter")) drawItemCount(g, client, x + 125, y + 7, Items.TOTEM_OF_UNDYING, player);
        if (enabled("Arrow Counter")) drawItemCount(g, client, x + 125, y + 25, Items.ARROW, player);
        return y + lines.size() * 14 + 12;
    }

    public static int drawArmor(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        List<String> lines = new ArrayList<>();
        for (EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.isDamageableItem()) {
                int percent = Math.round((1f - (float) stack.getDamageValue() / stack.getMaxDamage()) * 100f);
                lines.add(slot.getName() + " " + percent + "%");
            }
        }
        if (lines.isEmpty()) {
            return y;
        }
        panel(g, x, y, 210, lines.size() * 14 + 12);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            text(g, client, line, x + 8, y + 7 + i * 14,
                    line.endsWith("%") && Integer.parseInt(line.replaceAll("\\D", "")) < 20 ? WARNING : TEXT);
        }
        return y + lines.size() * 14 + 12;
    }

    public static int drawEffects(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        List<MobEffectInstance> effects = new ArrayList<>(player.getActiveEffects());
        if (effects.isEmpty()) {
            return y;
        }
        panel(g, x, y, 210, effects.size() * 20 + 12);
        for (int i = 0; i < effects.size(); i++) {
            MobEffectInstance effect = effects.get(i);
            g.blitSprite(RenderPipelines.GUI_TEXTURED, Gui.getMobEffectSprite(effect.getEffect()),
                    x + 6, y + 4 + i * 20, 16, 16);
            text(g, client, effect.getEffect().value().getDisplayName().getString() + " " +
                            formatDuration(effect.getDuration()), x + 27, y + 7 + i * 20, TEXT);
        }
        return y + effects.size() * 20 + 12;
    }

    public static void drawKeys(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        long now = System.nanoTime();
        float delta = Math.min(0.05f, (now - lastKeyTime) / 1_000_000_000f);
        lastKeyTime = now;
        boolean[] pressed = {player.input.keyPresses.forward(), player.input.keyPresses.left(),
                player.input.keyPresses.backward(), player.input.keyPresses.right(), player.input.keyPresses.jump()};
        for (int i = 0; i < KEY_LEVELS.length; i++) {
            float target = pressed[i] ? 1f : 0f;
            KEY_LEVELS[i] += (target - KEY_LEVELS[i]) * (1f - (float) Math.exp(-delta * 18f));
        }
        g.fill(x, y, x + 122, y + 102, PANEL);
        key(g, client, "W", x + 41, y + 8, 40, 28, KEY_LEVELS[0]);
        key(g, client, "A", x + 7, y + 40, 40, 28, KEY_LEVELS[1]);
        key(g, client, "S", x + 41, y + 40, 40, 28, KEY_LEVELS[2]);
        key(g, client, "D", x + 75, y + 40, 40, 28, KEY_LEVELS[3]);
        key(g, client, "SPACE", x + 7, y + 72, 108, 22, KEY_LEVELS[4]);
    }

    public static int drawItemDurability(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty() || !stack.isDamageableItem()) return y;
        int percent = Math.round((1f - (float) stack.getDamageValue() / stack.getMaxDamage()) * 100f);
        panel(g, x, y, 210, 30);
        g.item(stack, x + 6, y + 6);
        text(g, client, stack.getHoverName().getString() + " " + percent + "%", x + 28, y + 10,
                percent < 20 ? WARNING : TEXT);
        return y + 30;
    }

    private static void drawItemCount(GuiGraphicsExtractor g, Minecraft client, int x, int y,
                                      net.minecraft.world.item.Item item, LocalPlayer player) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(item)) count += player.getInventory().getItem(i).getCount();
        }
        g.item(new ItemStack(item), x, y - 4);
        text(g, client, Integer.toString(count), x + 18, y + 1, TEXT);
    }

    private static void key(GuiGraphicsExtractor g, Minecraft client, String label, int x, int y,
                            int width, int height, float level) {
        int color = blend(0xff202735, 0xff8d3e9a, level);
        g.fill(x, y, x + width, y + height, color);
        int textWidth = client.font.width(label);
        text(g, client, label, x + (width - textWidth) / 2, y + (height - 8) / 2,
                blend(TEXT, 0xffffffff, level));
    }

    private static int blend(int from, int to, float amount) {
        int r = (int) (((from >> 16 & 255) * (1 - amount)) + ((to >> 16 & 255) * amount));
        int g = (int) (((from >> 8 & 255) * (1 - amount)) + ((to >> 8 & 255) * amount));
        int b = (int) (((from & 255) * (1 - amount)) + ((to & 255) * amount));
        return 0xff000000 | r << 16 | g << 8 | b;
    }

    private static void text(GuiGraphicsExtractor graphics, Minecraft client, String value, int x, int y, int color) {
        graphics.text(client.font, Component.literal(value), x, y, color, true);
    }

    private static String formatDuration(int ticks) {
        if (ticks == MobEffectInstance.INFINITE_DURATION) {
            return "∞";
        }
        return (ticks / 20) / 60 + ":" + String.format(Locale.ROOT, "%02d", (ticks / 20) % 60);
    }

    private static void panel(GuiGraphicsExtractor g, int x, int y, int width, int height) {
        g.fill(x, y, x + width, y + height, PANEL);
    }

    private static boolean enabled(String name) {
        Module module = MeowClient.MODULE_MANAGER.byName(name);
        return module != null && module.isEnabled();
    }
}
