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

    public static int getPanel() {
        return PANEL;
    }

    public static int drawInfo(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        List<String> lines = new ArrayList<>();
        if (enabled("BPS"))
            lines.add("BPS " + String.format(Locale.ROOT, "%.2f", MeowClient.bps));
        if (enabled("Clock"))
            lines.add(java.time.LocalTime.now().format(java.time.format.DateTimeFormatter.ofPattern("HH:mm:ss")));
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

    public static int getTEXT() {
        return TEXT;
    }

    public static int getTextWidth(Minecraft client, String text) {
        return client.font.width(text);
    }
    public static int getTextHeight(Minecraft client, String text) {
        return client.font.lineHeight;
    }

//    public static void drawArmor(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
//        Map<ItemStack, Float> equipmentObjects = new LinkedHashMap<>(); // <Armour, Percentage>
//        ItemStack armourHelmet = player.getInventory().getItem(39);
//        ItemStack armourChestplate = player.getInventory().getItem(38);
//        ItemStack armourLeggings = player.getInventory().getItem(37);
//        ItemStack armourBoots = player.getInventory().getItem(36);
//        float percentageHelmet = armourPercentageHelper(armourHelmet);
//        float percentageChestplate = armourPercentageHelper(armourChestplate);
//        float percentageLeggings = armourPercentageHelper(armourLeggings);
//        float percentageBoots = armourPercentageHelper(armourBoots);
//        equipmentObjects.put(armourHelmet, percentageHelmet);
//        equipmentObjects.put(armourChestplate, percentageChestplate);
//        equipmentObjects.put(armourLeggings, percentageLeggings);
//        equipmentObjects.put(armourBoots, percentageBoots);
//        panel(g, x, y, 48, 112);
//        int count = 1;
//        for (ItemStack item : equipmentObjects.keySet()) {
//            float percentage = equipmentObjects.get(item);
//            g.item(item, x, y + (count * 18));
//            if (!item.isEmpty()) {
//                text(g, client, Float.toString(percentage), x + 30, y + (count * 18), TEXT);
//            }
//            count++;
//        }
//    }

    private static float armourPercentageHelper(ItemStack item) {
        float damage = (float) item.getDamageValue();
        float maxDamage = (float) item.getMaxDamage();
        return (1f - (damage / maxDamage)) * 100f;
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

//    public static void drawKeys(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
//        // this entire method is ai, i lowkey have no idea how i could do this without it being buggy lol
//        long now = System.nanoTime();
//        float delta = Math.min(0.05f, (now - lastKeyTime) / 1000000000f);
//        lastKeyTime = now;
//        // whether the keys are perssed or not
//        boolean[] pressed = {player.input.keyPresses.forward(), player.input.keyPresses.left(), player.input.keyPresses.backward(), player.input.keyPresses.right(), player.input.keyPresses.jump()};
//        for (int i = 0; i < KEY_LEVELS.length; i++) {
//            float target;
//            if (pressed[i]) {
//                target = 1f;
//            } else {
//                target = 0f;
//            }
//            KEY_LEVELS[i] += (target - KEY_LEVELS[i]) * (1f - (float) Math.exp(-delta * 18f));
//        }
//        g.fill(x, y, x + 122, y + 102, PANEL);
//        key(g, client, "W", x + 41, y + 8, 40, 28, KEY_LEVELS[0]);
//        key(g, client, "A", x + 7, y + 40, 40, 28, KEY_LEVELS[1]);
//        key(g, client, "S", x + 41, y + 40, 40, 28, KEY_LEVELS[2]);
//        key(g, client, "D", x + 75, y + 40, 40, 28, KEY_LEVELS[3]);
//        key(g, client, "SPACE", x + 7, y + 72, 108, 22, KEY_LEVELS[4]);
//    }

    public static void drawItemDurability(GuiGraphicsExtractor g, Minecraft client, LocalPlayer player, int x, int y) {
        ItemStack stack = player.getMainHandItem();
        int damage = stack.getDamageValue();
        int maxDamage = stack.getMaxDamage();
        int percent = Math.round((1f - (float) damage / maxDamage) * 100f);
        panel(g, x, y, 210, 30);
        g.item(stack, x + 6, y + 6);
        if (percent < 20) {
            text(g, client, stack.getHoverName().getString() + " " + percent + "%", x + 28, y + 10, WARNING);
        } else {
            text(g, client, stack.getHoverName().getString() + " " + percent + "%", x + 28, y + 10, TEXT);
        }
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


//    public static void key(GuiGraphicsExtractor g, Minecraft client, String label, int x, int y, int width, int height, float level) {
//        int color = blend(0xff202735, 0xff8d3e9a, level);
//        g.fill(x, y, x + width, y + height, color);
//        int textWidth = client.font.width(label);
//        text(g, client, label, x + (width - textWidth) / 2, y + (height - 8) / 2, blend(TEXT, 0xffffffff, level));
//    }
//
//    public static int blend(int from, int to, float amount) {
//        // i had to look up whatever the hell this does, but it basically blends two colors together based on the amount (0-1)
//        int r = (int) (((from >> 16 & 255) * (1 - amount)) + ((to >> 16 & 255) * amount));
//        int g = (int) (((from >> 8 & 255) * (1 - amount)) + ((to >> 8 & 255) * amount));
//        int b = (int) (((from & 255) * (1 - amount)) + ((to & 255) * amount));
//        return 0xff000000 | r << 16 | g << 8 | b;
//    }

    public static void text(GuiGraphicsExtractor graphics, Minecraft client, String value, int x, int y, int color) {
        graphics.text(client.font, Component.literal(value), x, y, color, true);
    }

    public static String formatDuration(int ticks) {
        if (ticks == MobEffectInstance.INFINITE_DURATION) {
            return "∞";
        }
        return (ticks / 20) / 60 + ":" + String.format(Locale.ROOT, "%02d", (ticks / 20) % 60); // format as mm:ss
    }

    public static void panel(GuiGraphicsExtractor g, int x, int y, int width, int height) {
        g.fill(x, y, x + width, y + height, PANEL);
    }
    public static void panel(GuiGraphicsExtractor g, int x, int y, int width, int height, int color) {
        g.fill(x, y, x + width, y + height, color);
    }

    public static boolean enabled(String name) {
        Module module = MeowClient.MODULE_MANAGER.byName(name);
        return module != null && module.isEnabled();
    }
}
