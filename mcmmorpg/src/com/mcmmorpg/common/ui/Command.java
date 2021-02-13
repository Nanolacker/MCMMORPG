package com.mcmmorpg.common.ui;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

/**
 * Represents a command that can be created at runtime and dispatched by command
 * senders. Commands must be registered with the command manager before being
 * used.
 */
public abstract class Command {
    final BukkitCommand bukkitCommand;

    /**
     * Create a new command. The command must be registered with the command manager
     * before being used.
     */
    protected Command(String name) {
        bukkitCommand = new BukkitCommand0(name, this);
    }

    /**
     * Set the description of this command.
     */
    public void setDescription(String description) {
        bukkitCommand.setDescription(description);
    }

    /**
     * Set the usage message of this command.
     */
    public void setUsageMessage(String usageMessage) {
        bukkitCommand.setUsage(usageMessage);
    }

    /**
     * Set the aliases of this command.
     */
    public void setAliases(List<String> aliases) {
        bukkitCommand.setAliases(aliases);
    }

    /**
     * What executes when the command is dispatched.
     */
    protected abstract void execute(CommandSender sender, String[] args);

    /**
     * Used for internal registration of commands, so that commands don't have to be
     * inconveniently listed in the plugin.yml file.
     */
    private static class BukkitCommand0 extends BukkitCommand {
        private final Command command;

        protected BukkitCommand0(String name, Command command) {
            super(name);
            this.command = command;
        }

        @Override
        public boolean execute(CommandSender sender, String alias, String[] args) {
            command.execute(sender, args);
            return true;
        }
    }
}
