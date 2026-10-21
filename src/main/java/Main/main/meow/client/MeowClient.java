package Main.main.meow.client;

import Main.main.meow.client.ui.hud.hudgui.HudGuiRenderer;
import Main.main.meow.client.ui.hud.HudComponent;
import Main.main.meow.client.ui.hud.HudComponentManager;
import Main.main.meow.client.ui.hud.HudEditorScreen;
import Main.main.meow.client.manager.NotificationManager;
import Main.main.meow.client.module.Module;
import Main.main.meow.client.manager.ModuleManager;
import Main.main.meow.client.module.modules.client.ClickGuiModule;
import Main.main.meow.client.module.modules.visuals.ZoomModule;
import Main.main.meow.client.module.modules.hud.*;
import Main.main.meow.client.module.modules.misc.WaypointsModule;
import Main.main.meow.client.module.modules.movement.SprintModule;
import Main.main.meow.client.module.modules.player.*;
import Main.main.meow.client.setting.MeowConfig;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

public class MeowClient implements ClientModInitializer {
    public static final ModuleManager MODULE_MANAGER = new ModuleManager();
    public static final HudComponentManager HUD_COMPONENTS = new HudComponentManager();
    private static boolean rightShiftDown;

    @Override
    public void onInitializeClient() {
        registerModules();
        registerHudComponents();
        MeowConfig.load(Minecraft.getInstance(), MODULE_MANAGER, HUD_COMPONENTS);
        MODULE_MANAGER.setChangeListener(() -> MeowConfig.save(Minecraft.getInstance(), MODULE_MANAGER, HUD_COMPONENTS));
        MODULE_MANAGER.setEnabledChangeListener(NotificationManager::moduleChanged);
        ClientTickEvents.END_CLIENT_TICK.register(MeowClient::handleHudEditorKey);
        ClientTickEvents.END_CLIENT_TICK.register(BpsModule::getBps);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (MODULE_MANAGER.byName("Sprint") instanceof SprintModule sprint) {
                sprint.tick(client);
            }
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> MeowConfig.save(client, MODULE_MANAGER, HUD_COMPONENTS));
    }

    private static void registerHudComponents() {
        PositionModule coordinates = (PositionModule) MODULE_MANAGER.byName("PositionModule");
        ArmorStatusModule armor = (ArmorStatusModule) MODULE_MANAGER.byName("Armor Status");
        PotionEffectsModule effects = (PotionEffectsModule) MODULE_MANAGER.byName("Potion Effects");
        KeystrokesModule keystrokes = (KeystrokesModule) MODULE_MANAGER.byName("Keystrokes");
        InventoryViewer inventory = (InventoryViewer) MODULE_MANAGER.byName("Inventory Viewer");
        BiomeModule biome = (BiomeModule) MODULE_MANAGER.byName("Biome");
        InfoModule info = (InfoModule) MODULE_MANAGER.byName("Info");
        ClockModule clock = (ClockModule) MODULE_MANAGER.byName("Clock");
        TotemCounterModule totem = (TotemCounterModule) MODULE_MANAGER.byName("Totem Counter");
        ArrowCounterModule arrow = (ArrowCounterModule) MODULE_MANAGER.byName("Arrow Counter");
        BpsModule bps = (BpsModule) MODULE_MANAGER.byName("BPS");

        HUD_COMPONENTS.add(new HudComponent("BPS", 8, 8, bps) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if ((editor || bps.isEnabled()) && c.player != null) {
                    BpsModule.drawInfo(g, c,x(), y());
                }
            }
        });

        HUD_COMPONENTS.add(new HudComponent("Arrow Counter", 8, 28, arrow) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if ((editor || arrow.isEnabled()) && c.player != null) {
                    ArrowCounterModule.drawArrowCounter(g, c, x(), y(), Items.ARROW, c.player);
                }
            }
        });

        HUD_COMPONENTS.add(new HudComponent("Totem Counter", 8, 48, totem) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if ((editor || totem.isEnabled()) && c.player != null) {
                    TotemCounterModule.drawTotemCounter(g, c, x(), y(), Items.TOTEM_OF_UNDYING, c.player);
                }
            }
        });

        HUD_COMPONENTS.add(new HudComponent("clock", 200, 150, clock) {
            protected void renderComponent(GuiGraphicsExtractor graphics, Minecraft client, boolean editor) {
                if((editor || clock.isEnabled()) && client.player != null) {
                    ClockModule.drawInfo(graphics, client, client.player, x(), y(), clock.getBackgroundColour());
                }
            }
        });

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
                    ArmorStatusModule.drawInfo(g, c, c.player, x(), y(), armor.getColor());
                }
            }
        });
        HUD_COMPONENTS.add(new HudComponent("effects", 8, 128, effects) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if ((editor || effects.isEnabled()) && c.player != null) {
                    PotionEffectsModule.drawEffects(g, c, c.player, x(), y());
                }
            }
        });
        HUD_COMPONENTS.add(new HudComponent("keystrokes", 8, 200, keystrokes) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if (c.player != null && (editor || keystrokes.isEnabled())) {
                    KeystrokesModule.drawInfo(g, c, c.player, x(), y(), keystrokes.getBackgroundColour());
                }
            }
        });
        HUD_COMPONENTS.add(new HudComponent("inventory", 8, 280, inventory) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if (c.player != null && (editor || inventory.isEnabled())) {
                    InventoryViewer.drawInventory(g, c, c.player, x(), y());
                }
            }
        });
        HUD_COMPONENTS.add(new HudComponent("info", 8, 8, info) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if (c.player != null && (editor || info.isEnabled())) {
                    InfoModule.drawInfo(g, c, c.player, x(), y());
                }
            }
        });
    }

    private static void registerModules() {
        MODULE_MANAGER.register(new ClickGuiModule());
        MODULE_MANAGER.register(new ArmorStatusModule());
        MODULE_MANAGER.register(new PotionEffectsModule());
        MODULE_MANAGER.register(new KeystrokesModule());
        MODULE_MANAGER.register(new WaypointsModule());
        MODULE_MANAGER.register(new ZoomModule());
        MODULE_MANAGER.register(new NotificationsModule());
        MODULE_MANAGER.register(new SprintModule());
        MODULE_MANAGER.register(new BpsModule());
        MODULE_MANAGER.register(new PositionModule());
        MODULE_MANAGER.register(new BiomeModule());
        MODULE_MANAGER.register(new ClockModule());
        MODULE_MANAGER.register(new TotemCounterModule());
        MODULE_MANAGER.register(new ArrowCounterModule());
        MODULE_MANAGER.register(new InventoryViewer());
        MODULE_MANAGER.register(new InfoModule());
    }

    private static void handleHudEditorKey(Minecraft client) {
        Module clickGui = MODULE_MANAGER.byName("ClickGUI");
        int key;
        if(clickGui == null) {
            key = GLFW.GLFW_KEY_RIGHT_SHIFT;
        } else {
            key = clickGui.keybind();
        }
        boolean rightShift;
        if(key == GLFW.GLFW_KEY_UNKNOWN) {
            rightShift = GLFW.glfwGetKey(client.getWindow().handle(), GLFW.GLFW_KEY_RIGHT_SHIFT) == GLFW.GLFW_PRESS;
        } else {
            rightShift = GLFW.glfwGetKey(client.getWindow().handle(), key) == GLFW.GLFW_PRESS;
        }
        if (rightShift && !rightShiftDown && client.screen == null) {
            client.setScreen(new HudGuiRenderer(MODULE_MANAGER));
        }
        if (GLFW.glfwGetKey(client.getWindow().handle(), GLFW.GLFW_KEY_F8) == GLFW.GLFW_PRESS && client.screen == null) {
            client.setScreen(new HudEditorScreen(HUD_COMPONENTS));
        }
        rightShiftDown = rightShift;
    }
}
