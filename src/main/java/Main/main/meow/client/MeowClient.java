package Main.main.meow.client;

import Main.main.meow.client.manager.*;
import Main.main.meow.client.module.modules.client.commandmodule.CommandModule;
import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.module.modules.hud.modules.*;
import Main.main.meow.client.module.modules.hud.modules.armorstatus.ArmorStatusModule;
import Main.main.meow.client.module.modules.hud.modules.bps.BpsModule;
import Main.main.meow.client.module.modules.hud.modules.clock.ClockModule;
import Main.main.meow.client.module.modules.hud.modules.enabledmodules.EnabledModules;
import Main.main.meow.client.module.modules.hud.modules.info.InfoModule;
import Main.main.meow.client.module.modules.hud.modules.notification.NotificationsModule;
import Main.main.meow.client.module.modules.hud.modules.position.PositionModule;
import Main.main.meow.client.module.modules.movement.autowalkmodule.AutoMoveModule;
import Main.main.meow.client.module.modules.visuals.CameraTools;
import Main.main.meow.client.module.modules.visuals.walkblockhighlighter.WalkBlockHighlighter;
import Main.main.meow.client.module.modules.visuals.raycastentityhitboxrenderer.RayCastEntityHitBoxRenderer;
import Main.main.meow.client.module.modules.visuals.raycastblockcustomizer.RayCastBlockCustomizer;
import Main.main.meow.client.ui.hud.HudComponent;
import Main.main.meow.client.ui.hud.HudEditorScreen;
import Main.main.meow.client.module.modules.client.clickgui.ClickGuiModule;
import Main.main.meow.client.module.modules.visuals.ZoomModule;
import Main.main.meow.client.module.modules.misc.WaypointsModule;
import Main.main.meow.client.module.modules.movement.SprintModule;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.message.v1.ClientSendMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.level.LevelRenderEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class MeowClient implements ClientModInitializer {
    private static final ModuleManager MODULE_MANAGER = new ModuleManager();
    private static final HudComponentManager HUD_COMPONENTS = new HudComponentManager();
    private static final String MOD_ID = "meow";
    public static CommandManager COMMAND_MANAGER;
    public static String getModId() {
        return MOD_ID;
    }
    public static ModuleManager getModuleManager() {
        return MODULE_MANAGER;
    }
    public static HudComponentManager getHudComponentManager() {
        return HUD_COMPONENTS;
    }

    @Override
    public void onInitializeClient() {
        registerModules();
        registerHudComponents();
        COMMAND_MANAGER = new CommandManager("*");
        ConfigManager.load(Minecraft.getInstance(), MODULE_MANAGER, HUD_COMPONENTS);
        MODULE_MANAGER.setChangeListener(() -> ConfigManager.save(Minecraft.getInstance(), MODULE_MANAGER, HUD_COMPONENTS));
        MODULE_MANAGER.setEnabledChangeListener(NotificationManager::moduleChanged);
        ClientTickEvents.END_CLIENT_TICK.register(MeowClient::clientModuleRegistration);
        ClientSendMessageEvents.ALLOW_CHAT.register(message -> {
            if (!message.startsWith(COMMAND_MANAGER.getPrefix())) return true;
            COMMAND_MANAGER.executeCommand(message);
            return false;
        });
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> ConfigManager.save(client, MODULE_MANAGER, HUD_COMPONENTS));
        LevelRenderEvents.AFTER_TRANSLUCENT_FEATURES.register(WalkBlockHighlighter::draw);
//        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.parse("thingie"), this::renderAnimation);
    }
    private static void clientModuleRegistration(Minecraft client) {
        HudEditorScreen.handleHudEditorKey(client);
        BpsModule.getBps(client);
        if(client.screen == null) {
            MODULE_MANAGER.handleKeybinds(client.getWindow().handle());
        }
        if(MODULE_MANAGER.byName("Sprint") instanceof SprintModule sprint) {
            sprint.tick(client);
        }
        if (MODULE_MANAGER.byName("AutoWalk") instanceof AutoMoveModule autoMove) {
            autoMove.tick(client);
        }
        if (MODULE_MANAGER.byName("RaycastBlockCustomizer") instanceof RayCastBlockCustomizer rayCastBlockCustomizer) {
            rayCastBlockCustomizer.rayCastBlockRenderer();

        }
        if (MODULE_MANAGER.byName("RayCastEntityHitBoxRenderer") instanceof RayCastEntityHitBoxRenderer rayCastEntityHitboxRenderer) {
            rayCastEntityHitboxRenderer.rayCastEntityHitBoxRenderer();
        }
    }


//    private float previousX = 10f;
//    private float currentX = 10f;
//    private float speedPerTick = 2.5f;
//
//    private void renderAnimation(GuiGraphicsExtractor guiGraphicsExtractor, DeltaTracker deltaTracker) {
////        float changeInTickRate = deltaTracker.getGameTimeDeltaPartialTick(true);
//        float frameChange = deltaTracker.getRealtimeDeltaTicks();
//        currentX += speedPerTick * frameChange;
//
//        int screenWidth = guiGraphicsExtractor.guiWidth();
//        if(currentX > screenWidth) {
//            currentX = -20f;
//        }
//        guiGraphicsExtractor.pose().pushMatrix();
//        guiGraphicsExtractor.pose().translate(currentX, 50.0f);
//
//        guiGraphicsExtractor.fill(0,0,20,20,0xffffffff);
//        guiGraphicsExtractor.pose().popMatrix();
//    }


    private static void registerHudComponents() {
        HudDisplayModule positionModule = (PositionModule) MODULE_MANAGER.byName(new PositionModule().getName());
        HudDisplayModule armor = (ArmorStatusModule) MODULE_MANAGER.byName(new ArmorStatusModule().getName());
        HudDisplayModule effects = (PotionEffectsModule) MODULE_MANAGER.byName(new PotionEffectsModule().getName());
        HudDisplayModule keystrokes = (KeystrokesModule) MODULE_MANAGER.byName(new KeystrokesModule().getName());
        HudDisplayModule inventory = (InventoryViewer) MODULE_MANAGER.byName(new InventoryViewer().getName());
        HudDisplayModule biome = (BiomeModule) MODULE_MANAGER.byName(new BiomeModule().getName());
        HudDisplayModule info = (InfoModule) MODULE_MANAGER.byName(new InfoModule().getName());
        HudDisplayModule clock = (ClockModule) MODULE_MANAGER.byName(new ClockModule().getName());
        HudDisplayModule totem = (TotemCounterModule) MODULE_MANAGER.byName(new TotemCounterModule().getName());
        HudDisplayModule arrow = (ArrowCounterModule) MODULE_MANAGER.byName(new ArrowCounterModule().getName());
        HudDisplayModule bps = (BpsModule) MODULE_MANAGER.byName(new BpsModule().getName());
        HudDisplayModule entityInfoViewer = (EntityInfoViewer) MODULE_MANAGER.byName(new EntityInfoViewer().getName());
        HudDisplayModule whatAmILookingAt = (WhatAmILookingAt) MODULE_MANAGER.byName(new WhatAmILookingAt().getName());
        HudDisplayModule enabledModules = (EnabledModules) MODULE_MANAGER.byName(new EnabledModules().getName());
        int initialXPos = 8; // not doing allat to just have the user move it lol
        registerHudComponent(bps.getName(), initialXPos, 8, bps);
        registerHudComponent(positionModule.getName(), initialXPos, 24, positionModule);
        registerHudComponent(armor.getName(), initialXPos, 40, armor);
        registerHudComponent(effects.getName(), initialXPos, 56, effects);
        registerHudComponent(keystrokes.getName(), initialXPos, 72, keystrokes);
        registerHudComponent(inventory.getName(), initialXPos, 88, inventory);
        registerHudComponent(biome.getName(), initialXPos, 104, biome);
        registerHudComponent(info.getName(), initialXPos, 120, info);
        registerHudComponent(clock.getName(), initialXPos, 136, clock);
        registerHudComponent(totem.getName(), initialXPos, 152, totem);
        registerHudComponent(arrow.getName(), initialXPos, 168, arrow);
        registerHudComponent(entityInfoViewer.getName(), initialXPos, 184, entityInfoViewer);
        registerHudComponent(whatAmILookingAt.getName(), initialXPos, 200, whatAmILookingAt);
        registerHudComponent(enabledModules.getName(), initialXPos, 216, enabledModules);
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
                new EntityInfoViewer(),
                new WhatAmILookingAt(),
                new RayCastBlockCustomizer(),
                new WalkBlockHighlighter(),
                new EnabledModules(),
                new RayCastEntityHitBoxRenderer(),
                new AutoMoveModule(),
                new CommandModule(),
                new CameraTools()
        );
    }
}
