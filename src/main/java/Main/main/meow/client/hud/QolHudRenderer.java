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
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public final class QolHudRenderer {
    private static final int PANEL = 0xb9161d2a, TEXT = 0xfff2f0f4, PURPLE = 0xffc45ad0, WARNING = 0xffff9c52;
    private static final float[] KEY_LEVELS = new float[5];
    private static long lastKeyTime = System.nanoTime();


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
        assert client.level != null; // ide stuff
        // there's a weird formatting thing with biomes, so i just replace underscores with spaces
        String biome = client.level.getBiome(pos).unwrapKey().map(key -> key.identifier().getPath().replace('_', ' ')).orElse("unknown");
        PlayerInfo info = client.getConnection() == null ? null : client.getConnection().getPlayerInfo(player.getUUID());
        int ping = info == null ? 0 : info.getLatency();
        String direction = player.getDirection().getName().toUpperCase(Locale.ROOT);
        List<String> lines = new ArrayList<>();
        if (enabled("Coordinates"))
            lines.add("XYZ " + pos.getX() + " " + pos.getY() + " " + pos.getZ());
        if (enabled("Direction"))
            lines.add(direction);
        if (enabled("Biome"))
            lines.add(biome);
        if (enabled("CPS"))
            lines.add("CPS " + MeowClient.cps);
        if (enabled("BPS"))
            lines.add("BPS " + String.format(Locale.ROOT, "%.2f", MeowClient.bps));
        if (enabled("Clock"))
            lines.add(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
        lines.add("FPS " + client.getFps() + "  |  Ping " + ping + " ms");
        panel(g, x, y, 210, lines.size() * 14 + 12);
        for (int i = 0; i < lines.size(); i++) {
            if (i == lines.size() - 1) {
                text(g, client, lines.get(i), x + 8, y + 7 + i * 14, PURPLE);
            } else {
                text(g, client, lines.get(i), x + 8, y + 7 + i * 14, TEXT);
            }
        }
        if (enabled("Totem Counter"))
            drawItemCount(g, client, x + 125, y + 7, Items.TOTEM_OF_UNDYING, player);
        if (enabled("Arrow Counter"))
            drawItemCount(g, client, x + 125, y + 25, Items.ARROW, player);
        return y + lines.size() * 14 + 12; // 14: line height, 12: padding (random)
    }

    public static int drawArmor(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        List<String> lines = new ArrayList<>();
        for (EquipmentSlot slot : List.of(EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET)) {
            ItemStack stack = player.getItemBySlot(slot);
            if (!stack.isEmpty() && stack.isDamageableItem()) {
                float damage = (float) stack.getDamageValue();
                float maxDamage = (float) stack.getMaxDamage();
                float percent = (1f - (damage / maxDamage)) * 100f;
                lines.add(slot.getName() + " " + Math.round(percent) + "%");
            }
        }
        if (lines.isEmpty()) {
            return y;
        }
        panel(g, x, y, 210, lines.size() * 14 + 12);
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            // had to use ai here, ts makes no sense gng :wilted_rose:
            if (line.endsWith("%") && Integer.parseInt(line.replaceAll("\\D", "")) < 20) {
                text(g, client, line, x + 8, y + 7 + i * 14, WARNING);
            } else {
                text(g, client, line, x + 8, y + 7 + i * 14, TEXT);
            }
        }
        return y + lines.size() * 14 + 12; // again, 14: line height, 12: padding (random)
    }

    public static int drawEffects(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        List<MobEffectInstance> effects = new ArrayList<>(player.getActiveEffects()); // it took a while to figure out how to get the effects, but this works ig
        if (effects.isEmpty()) {
            return y;
        }
        panel(g, x, y, 210, effects.size() * 20 + 12);
        for (int i = 0; i < effects.size(); i++) {
            MobEffectInstance effect = effects.get(i);
            int effectX = x + 6;
            int effectY = y + 4 + i * 20;
            int effectWidth = 16;
            int effectHeight = 16;
            g.blitSprite(RenderPipelines.GUI_TEXTURED, Gui.getMobEffectSprite(effect.getEffect()), effectX, effectY, effectWidth, effectHeight);
            int textX = x + 27;
            int textY = y + 7 + i * 20;
            text(g, client, effect.getEffect().value().getDisplayName().getString() + " " + formatDuration(effect.getDuration()), textX, textY, TEXT);
        }
        return y + effects.size() * 20 + 12;
    }

    public static void drawKeys(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        // this entire method is ai, i lowkey have no idea how i could do this without it being buggy lol
        long now = System.nanoTime();
        float delta = Math.min(0.05f, (now - lastKeyTime) / 1000000000f);
        lastKeyTime = now;
        // whether the keys are perssed or not
        boolean[] pressed = {player.input.keyPresses.forward(), player.input.keyPresses.left(), player.input.keyPresses.backward(), player.input.keyPresses.right(), player.input.keyPresses.jump()};
        for (int i = 0; i < KEY_LEVELS.length; i++) {
            float target;
            if (pressed[i]) {
                target = 1f;
            } else {
                target = 0f;
            }
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
        if (stack.isEmpty() || !stack.isDamageableItem())
            return y;
        // separate the math into variables
        int damage = stack.getDamageValue();
        int maxDamage = stack.getMaxDamage();
        int percent = Math.round((1f - (float) damage / maxDamage) * 100f);
        panel(g, x, y, 210, 30);
        g.item(stack, x + 6, y + 6);
        // use condition rather than ternary
        if (percent < 20) {
            text(g, client, stack.getHoverName().getString() + " " + percent + "%", x + 28, y + 10, WARNING);
        } else {
            text(g, client, stack.getHoverName().getString() + " " + percent + "%", x + 28, y + 10, TEXT);
        }
        return y + 30; // 30: height of the panel
    }

    private static void drawItemCount(GuiGraphicsExtractor g, Minecraft client, int x, int y, Item item, LocalPlayer player) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            if (player.getInventory().getItem(i).is(item))
                count += player.getInventory().getItem(i).getCount();
        }
        g.item(new ItemStack(item), x, y - 4); // offset bc mc is weird
        text(g, client, Integer.toString(count), x + 18, y + 1, TEXT);
    }

    public static void drawInventory(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        Map<ItemStack, Integer> playerInventoryStorage = new HashMap<>(); // <ItemStack, SlotPos>
        Inventory playerInventory = player.getInventory();
        for(int i = 9; i < 36; i++) {
            ItemStack itemStack = playerInventory.getItem(i);
            if(itemStack.isEmpty()) {
                playerInventoryStorage.put(null, i);
            } else {
                playerInventoryStorage.put(itemStack, i);
            }
        }
        panel(g, x, y, 162, 54);
        for(ItemStack item : playerInventoryStorage.keySet()) {
            int column = playerInventoryStorage.get(item) % 9;
            int row = playerInventoryStorage.get(item) / 9 - 1;
            if(item != null) {
                g.item(item, x + (column * 18), y + (row * 18));
                text(g, client, Integer.toString(playerInventory.getItem(playerInventoryStorage.get(item)).getCount()), x + (column * 18) + 7, y + (row * 18) + 7, TEXT);
            }
        }
    }


    private static void key(GuiGraphicsExtractor g, Minecraft client, String label, int x, int y, int width, int height, float level) {
        int color = blend(0xff202735, 0xff8d3e9a, level);
        g.fill(x, y, x + width, y + height, color);
        int textWidth = client.font.width(label);
        text(g, client, label, x + (width - textWidth) / 2, y + (height - 8) / 2, blend(TEXT, 0xffffffff, level));
    }

    private static int blend(int from, int to, float amount) {
        // i had to look up whatever the hell this does, but it basically blends two colors together based on the amount (0-1)
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
        return (ticks / 20) / 60 + ":" + String.format(Locale.ROOT, "%02d", (ticks / 20) % 60); // format as mm:ss
    }

    private static void panel(GuiGraphicsExtractor g, int x, int y, int width, int height) {
        g.fill(x, y, x + width, y + height, PANEL);
    }

    private static boolean enabled(String name) {
        Module module = MeowClient.MODULE_MANAGER.byName(name);
        return module != null && module.isEnabled();
    }
}
