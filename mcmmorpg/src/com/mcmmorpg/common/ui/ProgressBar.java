package com.mcmmorpg.common.ui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import com.mcmmorpg.common.character.PlayerCharacter;
import com.mcmmorpg.common.time.RepeatingTask;
import com.mcmmorpg.common.util.MathUtility;
import com.mcmmorpg.common.util.StringUtility;

/**
 * A progress bar. Override onComplete() to add completion behavior. Be sure to
 * set rate after instantiation.
 */
public class ProgressBar {
    /**
     * In seconds.
     */
    private static final double UPDATE_PERIOD = 0.15;
    private static final int TEXT_PANEL_PIPE_COUNT = 16;

    private static final List<ProgressBar> progressBars = new ArrayList<>();

    private String title;
    private ProgressBarColor color;
    private double progress;
    private double rate;
    private TextPanel textPanel;
    private BossBar bossBar;

    static {
        RepeatingTask progressUpdater = new RepeatingTask(UPDATE_PERIOD) {
            @Override
            protected void run() {
                for (int i = 0; i < progressBars.size(); i++) {
                    ProgressBar progressBar = progressBars.get(i);
                    progressBar.update();
                }
            }
        };
        progressUpdater.schedule();
    }

    /**
     * Create a new progress bar with the specified title and color.
     */
    public ProgressBar(String title, ProgressBarColor color) {
        this.title = title;
        this.color = color;
        this.progress = 0;
        this.rate = 0;
        this.textPanel = null;
        this.bossBar = null;
        progressBars.add(this);
    }

    private final void update() {
        setProgress(progress + rate * UPDATE_PERIOD);
    }

    /**
     * Returns the title of this progress bar.
     */
    public final String getTitle() {
        return title;
    }

    /**
     * Sets the title of this progress bar.
     */
    public final void setTitle(String title) {
        this.title = title;
        if (textPanel != null) {
            updateTextPanelText();
        }
        if (bossBar != null) {
            bossBar.setTitle(title);
        }
    }

    /**
     * Returns the color of this progress bar.
     */
    public final ProgressBarColor getColor() {
        return color;
    }

    /**
     * Sets the color of this progress bar.
     */
    public final void setColor(ProgressBarColor color) {
        this.color = color;
        if (textPanel != null) {
            updateTextPanelText();
        }
        if (bossBar != null) {
            bossBar.setColor(color.barColor);
        }
    }

    /**
     * Returns the progress of this progress bar from 0-1.
     */
    public final double getProgress() {
        return progress;
    }

    /**
     * Sets the progress of this progress bar from 0-1.
     */
    public final void setProgress(double progress) {
        this.progress = MathUtility.clamp(progress, 0, 1);
        if (this.progress == 1) {
            onComplete();
            dispose();
        } else {
            if (textPanel != null) {
                updateTextPanelText();
            }
            if (bossBar != null) {
                updateBossBarProgress();
            }
        }
    }

    /**
     * Returns the fill rate of this progress bar, in proportion per second.
     */
    public final double getRate() {
        return rate;
    }

    /**
     * Sets the fill rate of this progress bar, in proportion per second.
     */
    public final void setRate(double rate) {
        this.rate = rate;
    }

    /**
     * Returns the location at which this progress bar is being displayed via a text
     * panel, or null if it is not being displayed via text panel.
     */
    public final Location getDisplayLocation() {
        return textPanel == null ? null : textPanel.getLocation();
    }

    /**
     * Show this progress bar at the specified location using a text panel.
     * Subsequent calls of this method will relocate the text panel, but not
     * duplicate it. If location is null, the text panel will be removed.
     */
    public final void display(Location location) {
        if (location == null) {
            if (textPanel != null) {
                textPanel.setVisible(false);
            }
            textPanel = null;
        }
        if (textPanel == null) {
            textPanel = new TextPanel(location);
            updateTextPanelText();
            textPanel.setVisible(true);
        } else {
            textPanel.setLocation(location);
        }
    }

    /**
     * Display this progress bar to the specified player character using a boss bar.
     */
    public final void display(PlayerCharacter pc) {
        display(pc.getPlayer());
    }

    /**
     * Display this progress bar to the specified player using a boss bar.
     */
    public final void display(Player player) {
        if (bossBar == null) {
            bossBar = Bukkit.createBossBar(title, color.barColor, BarStyle.SOLID);
            bossBar.setProgress(0.0);
        }
        bossBar.addPlayer(player);
    }

    /**
     * Stops displaying this progress bar via boss bar to the specified player
     * character.
     */
    public final void hide(PlayerCharacter pc) {
        hide(pc.getPlayer());
    }

    /**
     * Stops displaying this progress bar via boss bar to the specified player.
     */
    public final void hide(Player player) {
        if (bossBar != null) {
            bossBar.removePlayer(player);
        }
    }

    private final void updateTextPanelText() {
        StringBuilder text = new StringBuilder();
        text.append(title);
        text.append("\n");
        text.append(ChatColor.GRAY + "[");
        int numColoredPipes = (int) (progress * TEXT_PANEL_PIPE_COUNT);
        int numGrayPipes = TEXT_PANEL_PIPE_COUNT - numColoredPipes;
        text.append(color.chatColor + StringUtility.repeat("|", numColoredPipes));
        text.append(ChatColor.GRAY + StringUtility.repeat("|", numGrayPipes));
        text.append(ChatColor.GRAY + "]");
        textPanel.setText(text.toString());
    }

    private final void updateBossBarProgress() {
        bossBar.setProgress(progress);
    }

    /**
     * Call this to get rid of it before it finishes. Automatically called when it
     * does finish.
     */
    public final void dispose() {
        this.rate = 0;
        if (textPanel != null) {
            textPanel.setVisible(false);
        }
        if (bossBar != null) {
            bossBar.removeAll();
        }
        progressBars.remove(this);
    }

    /**
     * Invoked when the progress bar becomes entirely full.
     */
    protected void onComplete() {
    }

    /**
     * A color scheme that can be used with a progress bar.
     */
    public static enum ProgressBarColor {
        BLUE(ChatColor.BLUE, BarColor.BLUE), GREEN(ChatColor.GREEN, BarColor.GREEN),
        PINK(ChatColor.LIGHT_PURPLE, BarColor.PINK), PURPLE(ChatColor.DARK_PURPLE, BarColor.PURPLE),
        RED(ChatColor.RED, BarColor.RED), WHITE(ChatColor.WHITE, BarColor.WHITE),
        YELLOW(ChatColor.YELLOW, BarColor.YELLOW);

        private final ChatColor chatColor;
        private final BarColor barColor;

        ProgressBarColor(ChatColor chatColor, BarColor barColor) {
            this.chatColor = chatColor;
            this.barColor = barColor;
        }
    }
}
