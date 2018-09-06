package io.github.jess4tech.skyhighshops;

import java.util.ArrayList;
import java.util.List;

public class CommandInfo {

    private CommandInfo() {

    }

    protected static final List<String> commands = new ArrayList<>();

    public static void registerCommands() {
        commands.add("List");
        commands.add("Help");
    }
}
