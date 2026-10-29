package Main.main.meow.client.ui.clickgui.model;

import Main.main.meow.client.module.Module;
import Main.main.meow.client.manager.ModuleManager;
import Main.main.meow.client.module.modules.client.clickgui.ClickGuiModule;
import Main.main.meow.client.ui.clickgui.controller.ClickGuiSettingsHandler;
import Main.main.meow.client.ui.clickgui.view.ClickGuiStyler;

import java.util.ArrayList;
import java.util.List;

public final class ClickGuiLayout {
    private static final int HEADER_HEIGHT = 30, SUBCATEGORY_HEIGHT = 20;
    private static final long TRANSITION_MS = 180L;

    private final ModuleManager modules;
    private final ClickGuiSettingsHandler settings;
    private final List<Category> visibleCategories = new ArrayList<>();
    private int columnWidth = -1;
    private int columnGap = -1;
    private int[] columnX;
    private int[] columnY;
    private long transitionStarted;

    public ClickGuiLayout(ModuleManager modules, ClickGuiSettingsHandler settings) {
        this.modules = modules;
        this.settings = settings;
    }

    public static int getColumnGap() {
        return 8;
    }
    public static int getHeaderHeight() {
        return HEADER_HEIGHT;
    }
    public static int getRowHeight() {
        return 20;
    }
    public static int getSubcategoryHeight() {
        return SUBCATEGORY_HEIGHT;
    }

    // expanded modules must be loaded first because they change the column heights
    public void init(int screenWidth, int screenHeight) {
        visibleCategories.clear();
        for (Category category : Category.values()) {
            if (!modules.inCategory(category).isEmpty()) {
                visibleCategories.add(category);
            }
        }
        int count = visibleCategories.size();
        columnGap = ClickGuiModule.getColumnGap();
        columnWidth = ClickGuiModule.getModuleWidth();
        int layoutWidth = count * columnWidth + Math.max(0, count - 1) * columnGap;
        int left = Math.max(8, (screenWidth - layoutWidth) / 2);
        int top = 12;
        columnX = new int[count];
        columnY = new int[count];
        for (int column = 0; column < count; column++) {
            Category category = visibleCategories.get(column);
            ClickGuiLayoutSettings.Position position = ClickGuiLayoutSettings.position(category.title(), left + column * (columnWidth + columnGap), top);
            int clampedX = ClickGuiStyler.clamp(position.x(), 8, Math.max(8, screenWidth - columnWidth - 8));
            int clampedY = ClickGuiStyler.clamp(position.y(), 8, Math.max(8, screenHeight - columnHeight(column) - 8));
            columnX[column] = clampedX;
            columnY[column] = clampedY;
        }
        transitionStarted = System.currentTimeMillis();
    }

    // width and gap are live settings; saved positions would pin the old spacing, so reset them when either changes
    public void syncWithSettings(int screenWidth, int screenHeight) {
        if (columnWidth == ClickGuiModule.getModuleWidth() && columnGap == ClickGuiModule.getColumnGap()) {
            return;
        }
        ClickGuiLayoutSettings.clearPositions();
        long started = transitionStarted;
        init(screenWidth, screenHeight);
        transitionStarted = started;
    }

    public int columnCount() {
        return visibleCategories.size();
    }

    public Category category(int column) {
        return visibleCategories.get(column);
    }

    public List<Module> modulesIn(int column) {
        return modules.inCategory(visibleCategories.get(column));
    }

    public int columnWidth() {
        return columnWidth;
    }

    public int columnX(int column) {
        return columnX[column];
    }

    public int columnY(int column) {
        return columnY[column];
    }

    public void moveColumn(int column, int x, int y, int screenWidth, int screenHeight) {
        columnX[column] = ClickGuiStyler.clamp(x, 8, Math.max(8, screenWidth - columnWidth() - 8));
        columnY[column] = ClickGuiStyler.clamp(y, 8, Math.max(8, screenHeight - columnHeight(column) - 8));
        ClickGuiLayoutSettings.setPosition(visibleCategories.get(column).title(), columnX[column], columnY[column]);
    }

    public int columnAt(double mouseX, double mouseY) {
        for (int column = columnX.length - 1; column >= 0; column--) {
            if (mouseX >= columnX[column] && mouseX < columnX[column] + columnWidth() && mouseY >= columnY[column] && mouseY < columnY[column] + columnHeight(column)) {
                return column;
            }
        }
        return -1;
    }

    public int moduleY(int column, int row) {
        int y = columnY[column] + HEADER_HEIGHT;
        List<Module> entries = modulesIn(column);
        for (int i = 0; i < row; i++) {
            y += rowHeight(entries.get(i));
        }
        return y;
    }

    public int columnHeight(int column) {
        int height = HEADER_HEIGHT;
        for (Module module : modulesIn(column)) {
            height += rowHeight(module);
        }
        return height;
    }

    public Module moduleAt(int column, double mouseX, double mouseY) {
        if (column < 0 || mouseX < columnX[column] || mouseX >= columnX[column] + columnWidth()) {
            return null;
        }
        List<Module> entries = modulesIn(column);
        for (int row = 0; row < entries.size(); row++) {
            int y = moduleY(column, row);
            if (mouseY >= y && mouseY < y + rowHeight(entries.get(row))) return entries.get(row);
        }
        return null;
    }

    private int rowHeight(Module module) {
        int height = ClickGuiModule.getModuleHeight();
        if (settings.isExpanded(module))
            height += settings.settingsFor(module).size() * SUBCATEGORY_HEIGHT;
        return height;
    }

    public float transitionProgress() {
        return Math.min(1f, (System.currentTimeMillis() - transitionStarted) / (float) TRANSITION_MS);
    }

    public int transitionAlpha() {
        return Math.round(ClickGuiStyler.easeIn(transitionProgress()) * 255f);
    }
}
