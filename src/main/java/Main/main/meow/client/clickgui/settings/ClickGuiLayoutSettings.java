package Main.main.meow.client.clickgui.settings;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public final class ClickGuiLayoutSettings {
    private static final Map<String, Position> positions = new HashMap<>();
    private static final Set<String> expandedModules = new HashSet<>();

    public static Position position(String category, int defaultX, int defaultY) {
        return positions.getOrDefault(category, new Position(defaultX, defaultY));
    }

    public static void setPosition(String category, int x, int y) {
        positions.put(category, new Position(x, y));
    }

    public static Map<String, Position> allPositions() {
        return Map.copyOf(positions);
    }

    public static boolean isExpanded(String module) {
        return expandedModules.contains(module);
    }

    public static void setExpandedModules(Set<String> modules) {
        expandedModules.clear();
        expandedModules.addAll(modules);
    }

    public static Set<String> expandedModules() {
        return Set.copyOf(expandedModules);
    }

    public record Position(int x, int y) {
    }
}
