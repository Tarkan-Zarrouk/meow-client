package Main.main.meow.client.module.modules.hud.modules;

import Main.main.meow.client.module.modules.hud.HudDisplayModule;
import Main.main.meow.client.setting.NumberSetting;
import Main.main.meow.client.setting.modulesettings.BooleanSetting;
import Main.main.meow.client.setting.modulesettings.colorpicker.ColorSetting;
import Main.main.meow.client.ui.clickgui.Category;
import Main.main.meow.client.util.RenderUtil;
import Main.main.meow.client.util.TextUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.phys.EntityHitResult;

import java.util.Locale;

public final class EntityInfoViewer extends HudDisplayModule {
    private static final ColorSetting BACKGROUND_COLOR = new ColorSetting("Panel Color", "Change the panel color", RenderUtil.getGuiColorPanel());
    private static final ColorSetting TEXT_COLOR = new ColorSetting("Text Color", "Change the text color", RenderUtil.getGuiTextColor());
    private static final NumberSetting BORDER_RADIUS = new NumberSetting("Border Radius", "Border roundedness", 8, 1, 20);
    private static final NumberSetting HEARTS_PER_ROW = new NumberSetting("Hearts Per Row", "Change the number of hearts per row", 10, 5, 20);
    private static final NumberSetting MODEL_SIZE = new NumberSetting("Model Size", "Change the Size of the model", 50, 25, 100);
    private static final BooleanSetting ROUNDED_CORNER_TOP_LEFT = new BooleanSetting("Top LEFT Round", "Round Top Right?", true);
    private static final BooleanSetting ROUNDED_CORNER_TOP_RIGHT = new BooleanSetting("Top Right Round", "Round Top Right?", true);
    private static final BooleanSetting ROUNDED_CORNER_BOTTOM_LEFT = new BooleanSetting("Bottom LEFT Round", "Round Top Right?", true);
    private static final BooleanSetting ROUNDED_CORNER_BOTTOM_RIGHT = new BooleanSetting("Bottom Right Round", "Round Top Right?", true);
    private static final NumberSetting PADDING_VALUE = new NumberSetting("Padding", "Change the padding", 6, 1, 10);
    private int panelWidth;
    private int panelHeight;
    private int trackedId;
    private float displayedHealth;

    public EntityInfoViewer() {
        super("Entity Viewer", Category.HUD, false, "Shows information about the entity you're looking at");
        super.addSetting(BACKGROUND_COLOR, TEXT_COLOR, BORDER_RADIUS, HEARTS_PER_ROW, MODEL_SIZE, ROUNDED_CORNER_TOP_LEFT, ROUNDED_CORNER_TOP_RIGHT, ROUNDED_CORNER_BOTTOM_LEFT, ROUNDED_CORNER_BOTTOM_RIGHT, PADDING_VALUE);
    }

    @Override
    public void drawInfo(GuiGraphicsExtractor graphics, Minecraft client, LocalPlayer player, int x, int y) {
        Entity entity = client.hitResult instanceof EntityHitResult hit ? hit.getEntity() : null;
        if (entity == null) {
            trackedId = -1;
            // preview your own player so the component can still be positioned in the HUD editor
            if (client.screen == null) return;
            entity = player;
        }
        LivingEntity living = null;
        if(entity instanceof LivingEntity livingEntity) {
            living = livingEntity;
        }
        String name = null;
        if(entity != null) {
            name = entity.getName().getString();
        }
        float health = 0;
        float maxHealth = 0;
        if(living != null) {
            health = living.getHealth();
            maxHealth = living.getMaxHealth();
        }
        if (entity.getId() != trackedId) {
            trackedId = entity.getId();
            displayedHealth = health;
        }
        // eases toward the real health so hits visibly drain the hearts
        displayedHealth += (health - displayedHealth) * 0.15F;

        String damageText = "";
        if (living != null && living.getAttributes().hasAttribute(Attributes.ATTACK_DAMAGE)) {
            double dmg = living.getAttributeValue(Attributes.ATTACK_DAMAGE);
            damageText = String.format(Locale.ROOT, "Damage: %.1f (%.1f ♥)", dmg, dmg / 2.0);
        }

        boolean useHearts = living != null && maxHealth <= HEARTS_PER_ROW.value() * 6;
        int totalHearts = 0;
        int rows = 0;
        if(useHearts) {
            totalHearts = (int) Math.ceil(maxHealth / 2.0f);
            rows = ((totalHearts + HEARTS_PER_ROW.value() - 1) / HEARTS_PER_ROW.value());
        }
        int heartsWidth = Math.min(totalHearts, HEARTS_PER_ROW.value()) * 9;
        String healthText = String.format(Locale.ROOT, "%.1f / %.1f", displayedHealth, maxHealth);
        if(living == null) {
            healthText = null;
        }
        int textWidth = Math.max(TextUtil.getTextWidth(client,name), TextUtil.getTextWidth(client, damageText));
        if(!useHearts &&(healthText != null)) {
            textWidth = Math.max(textWidth, TextUtil.getTextWidth(client, healthText));
        }
        int infoWidth = Math.max(textWidth, heartsWidth);
        int lineHeight = TextUtil.getTextHeight(client) + 2;
        int infoHeight = lineHeight;
        if (living != null) {
            if (useHearts) {
                infoHeight += rows * 10;
            } else {
                infoHeight += lineHeight;
            }

            infoHeight += 2;
        }

        infoHeight += lineHeight;
        int padding = PADDING_VALUE.value();
        panelWidth = padding * 3 + MODEL_SIZE.value() + infoWidth;
        panelHeight = padding * 2 + Math.max(MODEL_SIZE.value(), infoHeight);

        RenderUtil.roundedFill(graphics, x, y, panelWidth, panelHeight, BACKGROUND_COLOR.color(), ROUNDED_CORNER_TOP_LEFT.value(), ROUNDED_CORNER_TOP_RIGHT.value(), ROUNDED_CORNER_BOTTOM_LEFT.value(), ROUNDED_CORNER_BOTTOM_RIGHT.value(), BORDER_RADIUS.value());

        if (living != null) {
            RenderUtil.drawModel(graphics, client, living, x + padding, y + padding, MODEL_SIZE.value());
        }

        int textX = x + padding * 2 + MODEL_SIZE.value();
        int cursorY = y + (panelHeight - infoHeight) / 2;
        RenderUtil.drawText(graphics, client, name, textX, cursorY, TEXT_COLOR.color());
        cursorY += lineHeight + 2;

        if (living != null) {
            if (useHearts) {
                drawHearts(graphics, textX, cursorY, totalHearts, displayedHealth);
                cursorY += rows * 12;
            } else {
                RenderUtil.drawText(graphics, client, healthText, textX, cursorY, TEXT_COLOR.color());
                cursorY += lineHeight;
            }
        }
        RenderUtil.drawText(graphics, client, damageText, textX, cursorY, TEXT_COLOR.color());
    }

    private static void drawHearts(GuiGraphicsExtractor graphics, int x, int y, int totalHearts, float health) {
        // <3 mcassets
        Identifier heartFull = Identifier.withDefaultNamespace("hud/heart/full");
        Identifier heartContainer = Identifier.withDefaultNamespace("hud/heart/container");
        Identifier heartHalf = Identifier.withDefaultNamespace("hud/heart/half");
        for (int i = 0; i < totalHearts; i++) {
            int hx = x + (i % HEARTS_PER_ROW.value()) * 9;
            int hy = y + (i / HEARTS_PER_ROW.value()) * 10;
            RenderUtil.drawSprite(graphics, heartContainer, hx, hy, 9, 9);
            float remaining = health - i * 2;
            if (remaining >= 2) {
                RenderUtil.drawSprite(graphics, heartFull, hx, hy, 9, 9);
            } else if (remaining >= 1) {
                RenderUtil.drawSprite(graphics, heartHalf, hx, hy, 9, 9);
            }
        }
    }

    @Override
    public int panelWidth(Minecraft client) {
        return panelWidth;
    }

    @Override
    public int panelHeight(Minecraft client) {
        return panelHeight;
    }
}
