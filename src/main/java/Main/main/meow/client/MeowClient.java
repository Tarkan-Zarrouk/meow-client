package Main.main.meow.client;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.module.modules.hud.modules.*;
import Main.main.meow.client.module.modules.hud.modules.armorstatus.ArmorStatusModule;
import Main.main.meow.client.module.modules.hud.modules.bps.BpsModule;
import Main.main.meow.client.module.modules.hud.modules.clock.ClockModule;
import Main.main.meow.client.module.modules.hud.modules.info.InfoModule;
import Main.main.meow.client.module.modules.hud.modules.notification.NotificationsModule;
import Main.main.meow.client.module.modules.hud.modules.position.PositionModule;
import Main.main.meow.client.ui.hud.HudComponent;
import Main.main.meow.client.manager.HudComponentManager;
import Main.main.meow.client.ui.hud.HudEditorScreen;
import Main.main.meow.client.manager.NotificationManager;
import Main.main.meow.client.manager.ModuleManager;
import Main.main.meow.client.module.modules.client.clickgui.ClickGuiModule;
import Main.main.meow.client.module.modules.visuals.ZoomModule;
import Main.main.meow.client.module.modules.misc.WaypointsModule;
import Main.main.meow.client.module.modules.movement.SprintModule;
import Main.main.meow.client.manager.ConfigManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class MeowClient implements ClientModInitializer {
    public static final ModuleManager MODULE_MANAGER = new ModuleManager();
    public static final HudComponentManager HUD_COMPONENTS = new HudComponentManager();

    @Override
    public void onInitializeClient() {
        registerModules();
        registerHudComponents();
        ConfigManager.load(Minecraft.getInstance(), MODULE_MANAGER, HUD_COMPONENTS);
        MODULE_MANAGER.setChangeListener(() -> ConfigManager.save(Minecraft.getInstance(), MODULE_MANAGER, HUD_COMPONENTS));
        MODULE_MANAGER.setEnabledChangeListener(NotificationManager::moduleChanged);
        ClientTickEvents.END_CLIENT_TICK.register(HudEditorScreen::handleHudEditorKey);
        ClientTickEvents.END_CLIENT_TICK.register(BpsModule::getBps);
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.screen == null) {
                MODULE_MANAGER.handleKeybinds(client.getWindow().handle());
            }
        });
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (MODULE_MANAGER.byName("Sprint") instanceof SprintModule sprint) {
                sprint.tick(client);
            }
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save(client, MODULE_MANAGER, HUD_COMPONENTS));
    }

    private static void registerHudComponents() {
        PositionModule positionModule = (PositionModule) MODULE_MANAGER.byName(new PositionModule().getName());
        ArmorStatusModule armor = (ArmorStatusModule) MODULE_MANAGER.byName(new ArmorStatusModule().getName());
        PotionEffectsModule effects = (PotionEffectsModule) MODULE_MANAGER.byName(new PotionEffectsModule().getName());
        KeystrokesModule keystrokes = (KeystrokesModule) MODULE_MANAGER.byName(new KeystrokesModule().getName());
        InventoryViewer inventory = (InventoryViewer) MODULE_MANAGER.byName(new InventoryViewer().getName());
        BiomeModule biome = (BiomeModule) MODULE_MANAGER.byName(new BiomeModule().getName());
        InfoModule info = (InfoModule) MODULE_MANAGER.byName(new InfoModule().getName());
        ClockModule clock = (ClockModule) MODULE_MANAGER.byName(new ClockModule().getName());
        TotemCounterModule totem = (TotemCounterModule) MODULE_MANAGER.byName(new TotemCounterModule().getName());
        ArrowCounterModule arrow = (ArrowCounterModule) MODULE_MANAGER.byName(new ArrowCounterModule().getName());
        BpsModule bps = (BpsModule) MODULE_MANAGER.byName(new BpsModule().getName());
        EntityInfoViewer entityInfoViewer = (EntityInfoViewer) MODULE_MANAGER.byName(new EntityInfoViewer().getName());

        registerHudComponent(bps.getName(), 8, 8, bps);
        registerHudComponent(positionModule.getName(), 8, 24, positionModule);
        registerHudComponent(armor.getName(), 8, 40, armor);
        registerHudComponent(effects.getName(), 8, 56, effects);
        registerHudComponent(keystrokes.getName(), 8, 72, keystrokes);
        registerHudComponent(inventory.getName(), 8, 88, inventory);
        registerHudComponent(biome.getName(), 8, 104, biome);
        registerHudComponent(info.getName(), 8, 120, info);
        registerHudComponent(clock.getName(), 8, 136, clock);
        registerHudComponent(totem.getName(), 8, 152, totem);
        registerHudComponent(arrow.getName(), 8, 168, arrow);
        registerHudComponent(entityInfoViewer.getName(), 8, 184, entityInfoViewer);
    }

    private static void registerHudComponent(String id, int x, int y, HudDisplayModule module) {
        HUD_COMPONENTS.add(new HudComponent(id, x, y, module) {
            protected void renderComponent(GuiGraphicsExtractor g, Minecraft c, boolean editor) {
                if ((editor || module.getEnabled()) && c.player != null) {
                    module.drawInfo(g, c, c.player, x(), y());
                }
            }
        });
    }

    private static void registerModules() {
        MODULE_MANAGER.register(
                new ClickGuiModule(),
                new ArmorStatusModule(),
                new PotionEffectsModule(),
                new KeystrokesModule(),
                new WaypointsModule(),
                new ZoomModule(),
                new NotificationsModule(),
                new SprintModule(),
                new BpsModule(),
                new PositionModule(),
                new BiomeModule(),
                new ClockModule(),
                new TotemCounterModule(),
                new ArrowCounterModule(),
                new InventoryViewer(),
                new InfoModule(),
                new EntityInfoViewer()
        );
    }
}
