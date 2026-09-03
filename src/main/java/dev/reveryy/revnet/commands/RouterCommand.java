package dev.reveryy.revnet.commands;

import dev.jorel.commandapi.CommandAPICommand;
import dev.reveryy.revnet.managers.RouterManager;

public class RouterCommand {

    public static CommandAPICommand getRouterCommand(RouterManager routerManager) {
        return new CommandAPICommand("router");
    }
}
