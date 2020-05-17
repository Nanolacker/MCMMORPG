package com.mcmmorpg.common.ui;

import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

public abstract class Command {

	final BukkitCommand bukkitCommand;

	public Command(String name) {
		bukkitCommand = new BukkitCommand0(name, this);
	}

	public void setDescription(String description) {
		bukkitCommand.setDescription(description);
	}

	public void setUsageMessage(String usageMessage) {
		bukkitCommand.setUsage(usageMessage);
	}

	protected abstract void onExecute(CommandSender sender, String[] args);

	private static class BukkitCommand0 extends BukkitCommand {

		private final Command command;

		protected BukkitCommand0(String name, Command command) {
			super(name);
			this.command = command;
		}

		@Override
		public boolean execute(CommandSender sender, String alias, String[] args) {
			command.onExecute(sender, args);
			return true;
		}

	}

}
