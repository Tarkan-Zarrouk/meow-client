package Main.main.meow.client.clickgui;

import Main.main.meow.client.MeowClient;
import Main.main.meow.client.settings.MeowConfig;
import Main.main.meow.client.modules.Module;
import Main.main.meow.client.modules.ModuleManager;
import Main.main.meow.client.modules.category.Category;
import Main.main.meow.client.modules.client.ZoomModule;
import Main.main.meow.client.modules.hud.NotificationsModule;
import Main.main.meow.client.clickgui.settings.ClickGuiLayoutSettings;
import Main.main.meow.client.clickgui.settings.ClickGuiSettings;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

/**
 * the main clickgui screen for browsing and configuring modules.
 */
public final class HudGuiScreen extends Screen {
    private static final FontDescription ODIN_FONT =
            new FontDescription.Resource(Identifier.fromNamespaceAndPath("meow", "odin"));
    private static final int BACKDROP = 0x42000000;
    private static final int ROW = 0xe62c313b;
    private static final int ROW_HOVER = 0xf03a404b;
    private static final int TEXT = 0xfff7f4f7;
    private static final int CATEGORY_TEXT = 0xff000000;
    private static final int MUTED = 0xffc9c8cc;
    private static final int BORDER = 0xffffffff;
    private static final int COLUMN_GAP = 8;
    private static final int HEADER_HEIGHT = 30;
    private static final int ROW_HEIGHT = 20;
    private static final int SUBCATEGORY_HEIGHT = 20;
    private static final int CORNER_RADIUS = 16;
    private static final long TRANSITION_MS = 180L;

    private final ModuleManager modules;
    private final List<Category> visibleCategories = new ArrayList<>();
    private int left;
    private int top;
    private int columnWidth;
    private int layoutWidth;
    private int layoutHeight;
    private boolean dragging;
    private int draggingColumn = -1;
    private int fontSizeColumn = -1;
    private double dragOffsetX;
    private double dragOffsetY;
    private int[] columnX = new int[0];
    private int[] columnY = new int[0];
    private long transitionStarted;
    private final Set<Module> expanded = new HashSet<>();
    private Module awaitingKeybind;
    private ZoomModule awaitingZoomKeybind;
    private ColorPicker colorPicker;
    private boolean pickingCategoryColor;
    private boolean draggingFontSize;

    public HudGuiScreen() {
        this(MeowClient.MODULE_MANAGER);
    }

    public HudGuiScreen(ModuleManager modules) {
        super(Component.literal("HUD Modules"));
        this.modules = modules;
    }

    @Override
    protected void init() {
        // rebuild the columns every time because the window can resize. pretty basic, bro what.
        calculateLayout();
        expanded.clear();
        for (Module module : modules.all()) {
            if (ClickGuiLayoutSettings.isExpanded(module.name())) {
                expanded.add(module);
            }
        }
        left = Math.max(8, (width - layoutWidth) / 2);
        top = 12;
        columnX = new int[visibleCategories.size()];
        columnY = new int[visibleCategories.size()];
        for (int column = 0; column < visibleCategories.size(); column++) {
            Category category = visibleCategories.get(column);
            ClickGuiLayoutSettings.Position position = ClickGuiLayoutSettings.position(category.title(),
                    left + column * (columnWidth + COLUMN_GAP), top);
            columnX[column] = clamp(position.x(), 8, Math.max(8, width - columnWidth - 8));
            columnY[column] = clamp(position.y(), 8, Math.max(8, height - columnHeight(column) - 8));
        }
        transitionStarted = System.currentTimeMillis();
    }

    private void calculateLayout() {
        // empty categories are skipped so we don't render alot of sad blank boxes.
        visibleCategories.clear();
        for (Category category : Category.values()) {
            if (!modules.inCategory(category).isEmpty()) {
                visibleCategories.add(category);
            }
        }
        int available = Math.max(220, width - 16);
        columnWidth = Math.max(92, (available - (visibleCategories.size() - 1) * COLUMN_GAP)
                / Math.max(1, visibleCategories.size()));
        layoutWidth = visibleCategories.size() * columnWidth
                + Math.max(0, visibleCategories.size() - 1) * COLUMN_GAP;
        int tallest = 0;
        for (Category category : visibleCategories) {
            tallest = Math.max(tallest, modules.inCategory(category).size());
        }
        layoutHeight = HEADER_HEIGHT + tallest * ROW_HEIGHT;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        // the backdrop is intentionally extracted first so the panels stay readable.
        extractBlurredBackground(graphics);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        float progress = transitionProgress();
        float eased = easeIn(progress);
        int animationOffset = (int) ((1f - eased) * -18f);
        String tooltipDescription = null;
        for (int column = 0; column < visibleCategories.size(); column++) {
            Category category = visibleCategories.get(column);
            int x = columnX[column];
            int yTop = columnY[column] + animationOffset;
            roundedFill(graphics, x, yTop, columnWidth, HEADER_HEIGHT,
                    withAlpha(ClickGuiSettings.categoryColor(), transitionAlpha()), true, true, false, false);
            graphics.fill(x + CORNER_RADIUS, yTop + HEADER_HEIGHT - 1,
                    x + columnWidth - CORNER_RADIUS, yTop + HEADER_HEIGHT,
                    withAlpha(BORDER, transitionAlpha()));
            String categoryLabel = truncate(category.title(), columnWidth - 10);
            drawText(graphics, styled(categoryLabel), centeredX(x, columnWidth, categoryLabel),
                    centeredY(yTop, HEADER_HEIGHT), withAlpha(CATEGORY_TEXT, transitionAlpha()), false);

            List<Module> entries = modules.inCategory(category);
            for (int row = 0; row < entries.size(); row++) {
                Module module = entries.get(row);
                int y = moduleY(column, row);
                boolean hovered = mouseX >= x && mouseX < x + columnWidth
                        && mouseY >= y && mouseY < y + ROW_HEIGHT;
                if (hovered) {
                    tooltipDescription = module.description();
                }
                boolean lastModule = row == entries.size() - 1;
                boolean roundedBottom = lastModule && !expanded.contains(module);
                roundedFill(graphics, x, y, columnWidth, ROW_HEIGHT,
                        withAlpha(module.isEnabled() ? ClickGuiSettings.moduleColor()
                                        : (hovered ? ROW_HOVER : ROW),
                                transitionAlpha()), false, false, roundedBottom, roundedBottom);
                String moduleLabel = truncate(module.name(), columnWidth - 24);
                drawText(graphics, styled(moduleLabel), centeredX(x, columnWidth, moduleLabel),
                        centeredY(y, ROW_HEIGHT), withAlpha(TEXT, transitionAlpha()), true);
                drawText(graphics, styled("..."), x + columnWidth - 14, y + 10,
                        withAlpha(MUTED, transitionAlpha()), false);
                if (expanded.contains(module)) {
                    int dropdownY = y + ROW_HEIGHT;
                    List<String> settings = settingsFor(module);
                    for (int setting = 0; setting < settings.size(); setting++) {
                        int settingY = dropdownY + setting * SUBCATEGORY_HEIGHT;
                        String text = settings.get(setting);
                        if (mouseX >= x && mouseX < x + columnWidth
                                && mouseY >= settingY && mouseY < settingY + SUBCATEGORY_HEIGHT) {
                            tooltipDescription = settingDescription(module, setting);
                        }
                        boolean lastSetting = lastModule && setting == settings.size() - 1;
                        roundedFill(graphics, x, settingY, columnWidth, SUBCATEGORY_HEIGHT,
                                withAlpha(0xff171a22, transitionAlpha()), false, false,
                                lastSetting, lastSetting);
                        drawText(graphics, styled(text), centeredX(x, columnWidth, text),
                                centeredY(settingY, SUBCATEGORY_HEIGHT),
                                withAlpha(MUTED, transitionAlpha()), false);
                        if (module.name().equalsIgnoreCase("ClickGUI") && setting == 4) {
                            int sliderX = x + 8;
                            int sliderY = settingY + SUBCATEGORY_HEIGHT - 4;
                            int sliderWidth = columnWidth - 16;
                            graphics.fill(sliderX, sliderY, sliderX + sliderWidth, sliderY + 2, 0xff555861);
                            int knobX = sliderX + Math.round((ClickGuiSettings.fontSize() - 5f)
                                    / 15f * sliderWidth);
                            graphics.fill(knobX - 2, sliderY - 2, knobX + 3, sliderY + 4,
                                    ClickGuiSettings.moduleColor());
                        }
                    }
                }
            }
        }
        if (colorPicker != null) {
            colorPicker.setPosition(Math.max(8, (width - colorPicker.width()) / 2),
                    Math.max(8, (height - colorPicker.height()) / 2));
            colorPicker.render(graphics);
        } else if (tooltipDescription != null && !tooltipDescription.isBlank()) {
            renderTooltip(graphics, tooltipDescription, mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        // color picking gets first dibs on the mouse, otherwise clicks leak into the gui.
        if (transitionProgress() < 1f) {
            return true;
        }
        if (colorPicker != null) {
            if (colorPicker.mouseClicked(event.x(), event.y())) return true;
            if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
                saveSettings();
                colorPicker = null;
                return true;
            }
        }
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_RIGHT) {
            int column = columnAt(event.x(), event.y());
            Module module = moduleAt(column, event.x(), event.y());
            if (module != null) {
                if (!expanded.add(module)) expanded.remove(module);
                saveSettings();
                awaitingKeybind = null;
                awaitingZoomKeybind = null;
                return true;
            }
            return true;
        }
        if (event.button() != GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            return super.mouseClicked(event, doubleClick);
        }
        int column = columnAt(event.x(), event.y());
        if (column >= 0 && event.y() >= columnY[column]
                && event.y() < columnY[column] + HEADER_HEIGHT) {
            dragging = true;
            draggingColumn = column;
            dragOffsetX = event.x() - columnX[column];
            dragOffsetY = event.y() - columnY[column];
            return true;
        }
        Module module = moduleAt(column, event.x(), event.y());
        if (module != null) {
            int rowY = moduleY(column, modules.inCategory(visibleCategories.get(column)).indexOf(module));
            if (expanded.contains(module) && event.y() >= rowY + ROW_HEIGHT
                    && event.y() < rowY + ROW_HEIGHT + settingsFor(module).size() * SUBCATEGORY_HEIGHT) {
                int setting = (int) ((event.y() - rowY - ROW_HEIGHT) / SUBCATEGORY_HEIGHT);
                if (setting == 0) {
                    awaitingKeybind = module;
                } else if (module instanceof ZoomModule zoom && setting == 1) {
                    awaitingZoomKeybind = zoom;
                } else if (module.name().equalsIgnoreCase("ClickGUI") && setting == 1) {
                    openColorPicker(true);
                } else if (module.name().equalsIgnoreCase("ClickGUI") && setting == 2) {
                    openColorPicker(false);
                } else if (module.name().equalsIgnoreCase("ClickGUI") && setting == 3) {
                    ClickGuiSettings.setOdinFont(!ClickGuiSettings.odinFont());
                    saveSettings();
                } else if (module.name().equalsIgnoreCase("ClickGUI") && setting == 4) {
                    setFontSizeFromMouse(event.x(), columnX[column], columnWidth);
                    draggingFontSize = true;
                    fontSizeColumn = column;
                    saveSettings();
                } else if (module instanceof NotificationsModule notifications && setting == 1) {
                    notifications.setMode(notifications.mode() == NotificationsModule.Mode.CHAT
                            ? NotificationsModule.Mode.ALERT : NotificationsModule.Mode.CHAT);
                    saveSettings();
                } else if (module instanceof NotificationsModule notifications && setting == 2) {
                    notifications.setShowTimer(!notifications.showTimer());
                    saveSettings();
                }
            } else if (module.name().equalsIgnoreCase("ClickGUI")) {
                return true;
            } else {
                module.toggle();
            }
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double deltaX, double deltaY) {
        if (colorPicker != null && colorPicker.mouseDragged(event.x(), event.y())) return true;
        if (draggingFontSize) {
            if (fontSizeColumn >= 0 && fontSizeColumn < columnX.length) {
                setFontSizeFromMouse(event.x(), columnX[fontSizeColumn], columnWidth);
            }
            saveSettings();
            return true;
        }
        if (transitionProgress() >= 1f && dragging) {
            int columnHeight = columnHeight(draggingColumn);
            columnX[draggingColumn] = clamp((int) (event.x() - dragOffsetX),
                    8, Math.max(8, width - columnWidth - 8));
            columnY[draggingColumn] = clamp((int) (event.y() - dragOffsetY),
                    8, Math.max(8, height - columnHeight - 8));
            ClickGuiLayoutSettings.setPosition(visibleCategories.get(draggingColumn).title(),
                    columnX[draggingColumn], columnY[draggingColumn]);
            return true;
        }
        return super.mouseDragged(event, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (colorPicker != null) {
            colorPicker.mouseReleased();
            saveSettings();
        }
        if (event.button() == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            dragging = false;
            draggingColumn = -1;
            draggingFontSize = false;
            fontSizeColumn = -1;
            saveSettings();
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (awaitingKeybind != null) {
            if (event.key() == GLFW.GLFW_KEY_DELETE) {
                awaitingKeybind.setKeybind(GLFW.GLFW_KEY_UNKNOWN);
            } else if (event.key() != GLFW.GLFW_KEY_ESCAPE) {
                awaitingKeybind.setKeybind(event.key());
            }
            if (awaitingZoomKeybind != null) {
                if (event.key() == GLFW.GLFW_KEY_DELETE) {
                    awaitingZoomKeybind.setZoomKeybind(GLFW.GLFW_KEY_UNKNOWN);
                } else if (event.key() != GLFW.GLFW_KEY_ESCAPE) {
                    awaitingZoomKeybind.setZoomKeybind(event.key());
                }
                awaitingZoomKeybind = null;
                saveSettings();
                return true;
            }
            awaitingKeybind = null;
            return true;
        }
        if (event.key() == GLFW.GLFW_KEY_ESCAPE) {
            saveSettings();
            net.minecraft.client.Minecraft.getInstance().setScreen(null);
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public void removed() {
        saveSettings();
        super.removed();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private int columnAt(double mouseX, double mouseY) {
        for (int column = columnX.length - 1; column >= 0; column--) {
            int columnHeight = columnHeight(column);
            if (mouseX >= columnX[column] && mouseX < columnX[column] + columnWidth
                    && mouseY >= columnY[column] && mouseY < columnY[column] + columnHeight) {
                return column;
            }
        }
        return -1;
    }

    private int moduleY(int column, int row) {
        int y = columnY[column] + HEADER_HEIGHT;
        List<Module> entries = modules.inCategory(visibleCategories.get(column));
        for (int i = 0; i < row; i++) {
            y += ROW_HEIGHT;
            if (expanded.contains(entries.get(i))) {
                y += settingsFor(entries.get(i)).size() * SUBCATEGORY_HEIGHT;
            }
        }
        return y;
    }

    private int columnHeight(int column) {
        int height = HEADER_HEIGHT;
        List<Module> entries = modules.inCategory(visibleCategories.get(column));
        for (Module module : entries) {
            height += ROW_HEIGHT;
            if (expanded.contains(module)) {
                height += settingsFor(module).size() * SUBCATEGORY_HEIGHT;
            }
        }
        return height;
    }

    private Module moduleAt(int column, double mouseX, double mouseY) {
        if (column < 0 || mouseX < columnX[column] || mouseX >= columnX[column] + columnWidth) {
            return null;
        }
        List<Module> entries = modules.inCategory(visibleCategories.get(column));
        for (int row = 0; row < entries.size(); row++) {
            int y = moduleY(column, row);
            if (mouseY >= y && mouseY < y + ROW_HEIGHT) return entries.get(row);
            if (expanded.contains(entries.get(row))
                    && mouseY >= y + ROW_HEIGHT
                    && mouseY < y + ROW_HEIGHT
                    + settingsFor(entries.get(row)).size() * SUBCATEGORY_HEIGHT) {
                return entries.get(row);
            }
        }
        return null;
    }

    private String keyName(int key) {
        if (key == GLFW.GLFW_KEY_UNKNOWN) return "None";
        String name = GLFW.glfwGetKeyName(key, 0);
        if (name != null) return name.toUpperCase();
        return switch (key) {
            case GLFW.GLFW_KEY_ESCAPE -> "ESCAPE";
            case GLFW.GLFW_KEY_ENTER -> "ENTER";
            case GLFW.GLFW_KEY_TAB -> "TAB";
            case GLFW.GLFW_KEY_BACKSPACE -> "BACKSPACE";
            case GLFW.GLFW_KEY_INSERT -> "INSERT";
            case GLFW.GLFW_KEY_DELETE -> "DELETE";
            case GLFW.GLFW_KEY_RIGHT -> "RIGHT ARROW";
            case GLFW.GLFW_KEY_LEFT -> "LEFT ARROW";
            case GLFW.GLFW_KEY_DOWN -> "DOWN ARROW";
            case GLFW.GLFW_KEY_UP -> "UP ARROW";
            case GLFW.GLFW_KEY_SPACE -> "SPACE";
            case GLFW.GLFW_KEY_LEFT_SHIFT -> "LEFT SHIFT";
            case GLFW.GLFW_KEY_RIGHT_SHIFT -> "RIGHT SHIFT";
            case GLFW.GLFW_KEY_LEFT_CONTROL -> "LEFT CTRL";
            case GLFW.GLFW_KEY_RIGHT_CONTROL -> "RIGHT CTRL";
            case GLFW.GLFW_KEY_LEFT_ALT -> "LEFT ALT";
            case GLFW.GLFW_KEY_RIGHT_ALT -> "RIGHT ALT";
            case GLFW.GLFW_KEY_CAPS_LOCK -> "CAPS LOCK";
            case GLFW.GLFW_KEY_NUM_LOCK -> "NUM LOCK";
            case GLFW.GLFW_KEY_SCROLL_LOCK -> "SCROLL LOCK";
            case GLFW.GLFW_KEY_PAGE_UP -> "PAGE UP";
            case GLFW.GLFW_KEY_PAGE_DOWN -> "PAGE DOWN";
            case GLFW.GLFW_KEY_HOME -> "HOME";
            case GLFW.GLFW_KEY_END -> "END";
            case GLFW.GLFW_KEY_PRINT_SCREEN -> "PRINT SCREEN";
            case GLFW.GLFW_KEY_PAUSE -> "PAUSE";
            case GLFW.GLFW_KEY_KP_0 -> "NUMPAD 0";
            case GLFW.GLFW_KEY_KP_1 -> "NUMPAD 1";
            case GLFW.GLFW_KEY_KP_2 -> "NUMPAD 2";
            case GLFW.GLFW_KEY_KP_3 -> "NUMPAD 3";
            case GLFW.GLFW_KEY_KP_4 -> "NUMPAD 4";
            case GLFW.GLFW_KEY_KP_5 -> "NUMPAD 5";
            case GLFW.GLFW_KEY_KP_6 -> "NUMPAD 6";
            case GLFW.GLFW_KEY_KP_7 -> "NUMPAD 7";
            case GLFW.GLFW_KEY_KP_8 -> "NUMPAD 8";
            case GLFW.GLFW_KEY_KP_9 -> "NUMPAD 9";
            case GLFW.GLFW_KEY_KP_DECIMAL -> "NUMPAD DECIMAL";
            case GLFW.GLFW_KEY_KP_DIVIDE -> "NUMPAD DIVIDE";
            case GLFW.GLFW_KEY_KP_MULTIPLY -> "NUMPAD MULTIPLY";
            case GLFW.GLFW_KEY_KP_SUBTRACT -> "NUMPAD MINUS";
            case GLFW.GLFW_KEY_KP_ADD -> "NUMPAD PLUS";
            case GLFW.GLFW_KEY_KP_ENTER -> "NUMPAD ENTER";
            case GLFW.GLFW_KEY_F1 -> "F1";
            case GLFW.GLFW_KEY_F2 -> "F2";
            case GLFW.GLFW_KEY_F3 -> "F3";
            case GLFW.GLFW_KEY_F4 -> "F4";
            case GLFW.GLFW_KEY_F5 -> "F5";
            case GLFW.GLFW_KEY_F6 -> "F6";
            case GLFW.GLFW_KEY_F7 -> "F7";
            case GLFW.GLFW_KEY_F8 -> "F8";
            case GLFW.GLFW_KEY_F9 -> "F9";
            case GLFW.GLFW_KEY_F10 -> "F10";
            case GLFW.GLFW_KEY_F11 -> "F11";
            case GLFW.GLFW_KEY_F12 -> "F12";
            default -> "UNKNOWN";
        };
    }

    private List<String> settingsFor(Module module) {
        // keep the labels in the same order as the click handling below. future me, please.
        String key = awaitingKeybind == module ? "Press a key..." : "Keybind: " + keyName(module.keybind());
        if (module instanceof ZoomModule zoom) {
            String zoomKey = awaitingZoomKeybind == zoom ? "Press a key..." :
                    "Zoom key: " + keyName(zoom.zoomKeybind());
            return List.of(key, zoomKey);
        }
        if (module instanceof NotificationsModule notifications) {
            return List.of(key, "Type: " + (notifications.mode() == NotificationsModule.Mode.CHAT
                    ? "Chat" : "Alert"), notifications.showTimer() ? "Show Timer" : "Hide Timer");
        }
        if (module.name().equalsIgnoreCase("ClickGUI")) {
            return List.of(key,
                    "Category colour: " + hex(ClickGuiSettings.categoryColor()),
                    "Module colour: " + hex(ClickGuiSettings.moduleColor()),
                    "Font: " + (ClickGuiSettings.odinFont() ? "Inter" : "Minecraft"),
                    "Font size: " + Math.round(ClickGuiSettings.fontSize()));
        }
        return List.of(key);
    }

    private String settingDescription(Module module, int setting) {
        if (setting == 0) {
            return "Right-click this setting, then press a key to toggle the module. Press Delete to clear the keybind.";
        }
        if (module instanceof ZoomModule && setting == 1) {
            return "Hold this key to smoothly zoom to a 30 degree field of view. Press Delete to clear it.";
        }
        if (module instanceof NotificationsModule && setting == 1) {
            return "Switches between pink [MeowMod] chat messages and animated top-right alerts.";
        }
        if (module instanceof NotificationsModule && setting == 2) {
            return "Shows or hides the magenta timer bar that drains until the alert is cleared.";
        }
        if (module.name().equalsIgnoreCase("ClickGUI") && setting == 1) {
            return "Opens a picker that changes the color of every category header in real time.";
        }
        if (module.name().equalsIgnoreCase("ClickGUI") && setting == 2) {
            return "Opens a picker that changes the color of every module row in real time.";
        }
        if (module.name().equalsIgnoreCase("ClickGUI") && setting == 3) {
            return "Switches the ClickGUI between the custom Inter font and Minecraft's default font.";
        }
        if (module.name().equalsIgnoreCase("ClickGUI") && setting == 4) {
            return "Drag across this slider to change the active font size in real time.";
        }
        return "";
    }

    private void renderTooltip(GuiGraphicsExtractor graphics, String description, int mouseX, int mouseY) {
        // tooltips are wrapped manually since vanilla's tooltip path is a little awkward here.
        List<String> lines = wrapTooltip(description, 220);
        int tooltipWidth = 0;
        for (String line : lines) {
            tooltipWidth = Math.max(tooltipWidth, textWidth(styled(line)));
        }
        tooltipWidth += 12;
        int tooltipHeight = lines.size() * textLineHeight() + 8;
        int tooltipX = mouseX + 12;
        int tooltipY = mouseY + 12;
        if (tooltipX + tooltipWidth > width - 4) tooltipX = mouseX - tooltipWidth - 12;
        if (tooltipY + tooltipHeight > height - 4) tooltipY = mouseY - tooltipHeight - 12;
        tooltipX = Math.max(4, tooltipX);
        tooltipY = Math.max(4, tooltipY);
        roundedFill(graphics, tooltipX, tooltipY, tooltipWidth, tooltipHeight,
                0xf0181b22, true, true, true, true);
        for (int line = 0; line < lines.size(); line++) {
            drawText(graphics, styled(lines.get(line)), tooltipX + 6,
                    tooltipY + 4 + line * textLineHeight(), TEXT, false);
        }
    }

    private List<String> wrapTooltip(String description, int maxWidth) {
        List<String> lines = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        for (String word : description.split(" ")) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            if (!current.isEmpty() && textWidth(styled(candidate)) > maxWidth) {
                lines.add(current.toString());
                current.setLength(0);
            }
            current.append(current.isEmpty() ? word : " " + word);
        }
        if (!current.isEmpty()) lines.add(current.toString());
        return lines;
    }

    private void saveSettings() {
        Set<String> expandedNames = new HashSet<>();
        for (Module module : expanded) {
            expandedNames.add(module.name());
        }
        ClickGuiLayoutSettings.setExpandedModules(expandedNames);
        MeowConfig.save(net.minecraft.client.Minecraft.getInstance(), modules, MeowClient.HUD_COMPONENTS);
    }

    private void openColorPicker(boolean category) {
        pickingCategoryColor = category;
        colorPicker = new ColorPicker(260, 190,
                category ? ClickGuiSettings.categoryColor() : ClickGuiSettings.moduleColor(),
                color -> {
                    if (pickingCategoryColor) ClickGuiSettings.setCategoryColor(color);
                    else ClickGuiSettings.setModuleColor(color);
                    saveSettings();
                });
    }

    private static String hex(int color) {
        return String.format("#%06X", color & 0xffffff);
    }

    private String truncate(String value, int maxWidth) {
        if (textWidth(styled(value)) <= maxWidth) return value;
        String suffix = "..";
        StringBuilder result = new StringBuilder();
        for (char character : value.toCharArray()) {
            if (textWidth(styled(result.toString() + character + suffix)) > maxWidth) break;
            result.append(character);
        }
        return result + suffix;
    }

    private int centeredX(int x, int containerWidth, String value) {
        return x + Math.max(4, (containerWidth - textWidth(styled(value))) / 2);
    }

    private int centeredY(int y, int containerHeight) {
        return y + Math.max(0, (containerHeight - textLineHeight()) / 2);
    }

    private static Component styled(String value) {
        MutableComponent component = Component.literal(value);
        return ClickGuiSettings.odinFont() ? component.withStyle(style -> style.withFont(ODIN_FONT)) : component;
    }

    private void setFontSizeFromMouse(double mouseX, int columnStart, int columnWidth) {
        double sliderStart = columnStart + 8;
        double sliderWidth = Math.max(1, columnWidth - 16);
        double progress = (mouseX - sliderStart) / sliderWidth;
        int size = 5 + (int) Math.round(Math.max(0d, Math.min(1d, progress)) * 15d);
        ClickGuiSettings.setFontSize(size);
    }

    private int textWidth(Component value) {
        return Math.round(font.width(value) * textScale());
    }

    private int textLineHeight() {
        return Math.round(font.lineHeight * textScale());
    }

    private float textScale() {
        return ClickGuiSettings.fontSize() / 11f;
    }

    private void drawText(GuiGraphicsExtractor graphics, Component value, int x, int y,
                          int color, boolean shadow) {
        float scale = textScale();
        graphics.pose().pushMatrix();
        graphics.pose().translate(x, y);
        graphics.pose().scale(scale, scale);
        graphics.text(font, value, 0, 0, color, shadow && !ClickGuiSettings.odinFont());
        graphics.pose().popMatrix();
    }

    private static void roundedFill(GuiGraphicsExtractor graphics, int x, int y,
                                    int width, int height, int color,
                                    boolean topLeft, boolean topRight,
                                    boolean bottomLeft, boolean bottomRight) {
        // scanlines are less fancy than a shader, but they behave nicely at tiny gui sizes.
        int radius = Math.min(CORNER_RADIUS, width / 2);
        for (int row = 0; row < height; row++) {
            int leftInset = 0;
            int rightInset = 0;
            if (topLeft && row < radius) {
                leftInset = roundedInset(radius, row);
            }
            if (topRight && row < radius) {
                rightInset = roundedInset(radius, row);
            }
            if (bottomLeft && row >= height - radius) {
                leftInset = roundedInset(radius, height - row - 1);
            }
            if (bottomRight && row >= height - radius) {
                rightInset = roundedInset(radius, height - row - 1);
            }
            if (leftInset > 0 || rightInset > 0) {
                graphics.fill(x + leftInset, y + row, x + width - rightInset, y + row + 1, color);
            } else {
                graphics.fill(x, y + row, x + width, y + row + 1, color);
            }
        }
    }

    private static int roundedInset(int radius, int distance) {
        double circle = radius - 0.5;
        double offset = distance - circle;
        return Math.max(0, (int) Math.ceil(circle - Math.sqrt(Math.max(0, circle * circle - offset * offset))));
    }

    private float transitionProgress() {
        return Math.min(1f, (System.currentTimeMillis() - transitionStarted) / (float) TRANSITION_MS);
    }

    private int transitionAlpha() {
        return Math.round(easeIn(transitionProgress()) * 255f);
    }

    private static float easeIn(float value) {
        return value * value;
    }

    private static int withAlpha(int color, int alpha) {
        return (color & 0x00ffffff) | (alpha << 24);
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(value, max));
    }
}
