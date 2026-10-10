package Main.main.meow.client;

import Main.main.meow.client.hud.hudgui.HudGuiRenderer;
import Main.main.meow.client.hud.HudComponent;
import Main.main.meow.client.hud.HudComponentManager;
import Main.main.meow.client.hud.HudEditorScreen;
import Main.main.meow.client.hud.QolHudRenderer;
import Main.main.meow.client.hud.NotificationManager;
import Main.main.meow.client.modules.Module;
import Main.main.meow.client.modules.ModuleManager;
import Main.main.meow.client.modules.client.ClickGuiModule;
import Main.main.meow.client.modules.client.ZoomModule;
import Main.main.meow.client.modules.hud.*;
import Main.main.meow.client.modules.misc.WaypointsModule;
import Main.main.meow.client.modules.movement.SprintModule;
import Main.main.meow.client.modules.player.*;
import Main.main.meow.client.modules.visuals.*;
import Main.main.meow.client.settings.MeowConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import org.lwjgl.glfw.GLFW;
import java.util.ArrayDeque;
import java.util.Deque;

public class MeowClient implements ClientModInitializer {
    public static final ModuleManager MODULE_MANAGER = new ModuleManager();
    public static final HudComponentManager HUD_COMPONENTS = new HudComponentManager();
    private static final Deque<Long> CLICKS = new ArrayDeque<>();
    private static double lastX;
    private static double lastZ;
    private static long lastSpeedTime;
    public static int cps;
    public static double bps;
    private static boolean rightShiftDown;

    @Override
    public void onInitializeClient() {
        registerModules();
        registerHudComponents();
        MeowConfig.load(Minecraft.getInstance(), MODULE_MANAGER, HUD_COMPONENTS);
        MODULE_MANAGER.setChangeListener(() -> MeowConfig.save(Minecraft.getInstance(), MODULE_MANAGER, HUD_COMPONENTS));
        MODULE_MANAGER.setEnabledChangeListener(NotificationManager::moduleChanged);
        ClientTickEvents.END_CLIENT_TICK.register(MeowClient::handleHudEditorKey);
        ClientTickEvents.END_CLIENT_TICK.register(MeowClient::sampleStats);
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> MeowConfig.save(client, MODULE_MANAGER, HUD_COMPONENTS));
    }

    private static void registerHudComponents() {
        PositionModule coordinates = (PositionModule) MODULE_MANAGER.byName("Coordinates");
        ArmorStatusModule armor = (ArmorStatusModule) MODULE_MANAGER.byName("Armor Status");
        ItemDurabilityModule itemDurability = (ItemDurabilityModule) MODULE_MANAGER.byName("Item Durability");
        PotionEffectsModule effects = (PotionEffectsModule) MODULE_MANAGER.byName("Potion Effects");
        KeystrokesModule keystrokes = (KeystrokesModule) MODULE_MANAGER.byName("Keystrokes");
        InventoryViewer inventory = (InventoryViewer) MODULE_MANAGER.byName("Inventory Viewer");
        BiomeModule biome = (BiomeModule) MODULE_MANAGER.byName("Biome");

        HUD_COMPONENTS.add(new HudComponent("biome", 200, 100, biome) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if((editor || biome.isEnabled()) && c.player != null) {
                    BiomeModule.drawInfo(g,c, c.player, x(), y());
                }
            }
        });

        HUD_COMPONENTS.add(new HudComponent("coordinates", 200, 8, coordinates) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if ((editor || coordinates.isEnabled()) && c.player != null) {
                    PositionModule.drawInfo(g, c, c.player, x(), y());
                }
            }
        });
        HUD_COMPONENTS.add(new HudComponent("armor", 8, 68, armor) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if ((editor || armor.isEnabled()) && c.player != null) {
                    QolHudRenderer.drawArmor(g, c, c.player, x(), y());
                }
            }
        });
        HUD_COMPONENTS.add(new HudComponent("item-durability", 8, 188, itemDurability) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if (c.player != null && (editor || itemDurability.isEnabled())) {
                    QolHudRenderer.drawItemDurability(g, c, c.player, x(), y());
                }
            }
        });
        HUD_COMPONENTS.add(new HudComponent("effects", 8, 128, effects) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if ((editor || effects.isEnabled()) && c.player != null) {
                    QolHudRenderer.drawEffects(g, c, c.player, x(), y());
                }
            }
        });
        HUD_COMPONENTS.add(new HudComponent("keystrokes", 8, 200, keystrokes) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if (c.player != null && (editor || keystrokes.isEnabled())) {
                    QolHudRenderer.drawKeys(g, c, c.player, x(), y());
                }
            }
        });
        HUD_COMPONENTS.add(new HudComponent("inventory", 8, 280, inventory) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if (c.player != null && (editor || inventory.isEnabled())) {
                    QolHudRenderer.drawInventory(g, c, c.player, x(), y());
                }
            }
        });
    }

    private static void registerModules() {
        MODULE_MANAGER.register(new ClickGuiModule());
        MODULE_MANAGER.register(new HudModule());
        MODULE_MANAGER.register(new ArmorStatusModule());
        MODULE_MANAGER.register(new PotionEffectsModule());
        MODULE_MANAGER.register(new KeystrokesModule());
        MODULE_MANAGER.register(new WaypointsModule());
        MODULE_MANAGER.register(new ZoomModule());
        MODULE_MANAGER.register(new NotificationsModule());
        MODULE_MANAGER.register(new InventoryMoverModule());
        MODULE_MANAGER.register(new SprintModule());
        MODULE_MANAGER.register(new CpsModule());
        MODULE_MANAGER.register(new BpsModule());
        MODULE_MANAGER.register(new PositionModule());
        MODULE_MANAGER.register(new BiomeModule());
        MODULE_MANAGER.register(new ClockModule());
        MODULE_MANAGER.register(new ArmorDurabilityModule());
        MODULE_MANAGER.register(new ItemDurabilityModule());
        MODULE_MANAGER.register(new TotemCounterModule());
        MODULE_MANAGER.register(new ArrowCounterModule());
        MODULE_MANAGER.register(new InventoryViewer());
    }

    public static void recordClick() {
        long now = System.currentTimeMillis();
        CLICKS.addLast(now);
        trimClicks(now);
    }

    private static void sampleStats(Minecraft client) {
        MODULE_MANAGER.handleKeybinds(client.getWindow().handle());
        Module sprint = MODULE_MANAGER.byName("Sprint");
        if (sprint instanceof SprintModule sprintModule && sprintModule.isEnabled()) {
            sprintModule.tick(client);
        }
        long now = System.currentTimeMillis();
        trimClicks(now);
        cps = CLICKS.size();
        if (client.player != null) {
            if (lastSpeedTime != 0) {
                double seconds = (now - lastSpeedTime) / 1000.0;
                if (seconds > 0) {
                    bps = Math.hypot(client.player.getX() - lastX, client.player.getZ() - lastZ) / seconds; // this is a rough estimate of blocks per second, it doesn't account for vertical movement or diagonal movement, had to see another client code lopl
                }
            }
            lastX = client.player.getX();
            lastZ = client.player.getZ();
            lastSpeedTime = now;
        }
    }

    private static void trimClicks(long now) {
        while (!CLICKS.isEmpty() && now - CLICKS.peekFirst() > 1000) CLICKS.removeFirst();
    }

    private static void handleHudEditorKey(Minecraft client) {
        Module clickGui = MODULE_MANAGER.byName("ClickGUI");
        int key = clickGui == null ? GLFW.GLFW_KEY_RIGHT_SHIFT : clickGui.keybind();
        boolean rightShift = key != GLFW.GLFW_KEY_UNKNOWN && GLFW.glfwGetKey(client.getWindow().handle(), key) == GLFW.GLFW_PRESS;
        if (rightShift && !rightShiftDown && client.screen == null) {
            client.setScreen(new HudGuiRenderer(MODULE_MANAGER));
        }
        if (GLFW.glfwGetKey(client.getWindow().handle(), GLFW.GLFW_KEY_F8) == GLFW.GLFW_PRESS && client.screen == null) {
            client.setScreen(new HudEditorScreen(HUD_COMPONENTS));
        }
        rightShiftDown = rightShift;
    }
}
