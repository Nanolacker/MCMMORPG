package com.mcmmorpg.test;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import com.mcmmorpg.common.MMORPGPlugin;
import com.mcmmorpg.common.event.EventManager;
import com.mcmmorpg.common.sound.MusicNote;
import com.mcmmorpg.common.sound.Noise;
import com.mcmmorpg.common.sound.SoundSequence;
import com.mcmmorpg.common.sound.SoundSequencePlayer;

public class SoundtrackTest extends MMORPGPlugin implements Listener {

	@Override
	protected void onMMORPGStart() {
		EventManager.registerEvents(this);
	}

	@Override
	protected void onMMORPGStop() {
	}

	@EventHandler
	private void onPlayerJoin(PlayerJoinEvent event) {
		SoundSequence song = getSong();
		Player player = event.getPlayer();
		SoundSequencePlayer songPlayer = new SoundSequencePlayer(song, player);
		songPlayer.setLooping(true);
		songPlayer.play();
	}

	private SoundSequence getSong() {
		SoundSequence song = new SoundSequence(12);
		Noise a1 = new Noise(Sound.BLOCK_NOTE_BLOCK_HARP, 1, MusicNote.A_4);
		Noise b1 = new Noise(Sound.BLOCK_NOTE_BLOCK_HARP, 1, MusicNote.B_6);
		Noise c1 = new Noise(Sound.BLOCK_NOTE_BLOCK_HARP, 1, MusicNote.C_7);
		Noise d1 = new Noise(Sound.BLOCK_NOTE_BLOCK_HARP, 1, MusicNote.D_9);
		Noise e1 = new Noise(Sound.BLOCK_NOTE_BLOCK_HARP, 1, MusicNote.E_11);
		Noise f1 = new Noise(Sound.BLOCK_NOTE_BLOCK_HARP, 1, MusicNote.F_12);
		Noise g1 = new Noise(Sound.BLOCK_NOTE_BLOCK_HARP, 1, MusicNote.G_14);
		Noise a2 = new Noise(Sound.BLOCK_NOTE_BLOCK_HARP, 1, MusicNote.A_16);
		Noise b2 = new Noise(Sound.BLOCK_NOTE_BLOCK_HARP, 1, MusicNote.B_18);

		song.add(a1, 0);
		song.add(b1, 0.33);
		song.add(b1, 1);
		song.add(c1, 1.33);
		song.add(c1, 2);
		song.add(d1, 2.33);
		song.add(d1, 3);
		song.add(e1, 3.33);

		song.add(b1, 4);
		song.add(c1, 4.33);
		song.add(c1, 5);
		song.add(d1, 5.33);
		song.add(d1, 6);
		song.add(e1, 6.33);
		song.add(e1, 7);
		song.add(f1, 7.33);

		song.add(d1, 8);
		song.add(e1, 8.33);
		song.add(e1, 9);
		song.add(f1, 9.33);
		song.add(f1, 10);
		song.add(g1, 10.33);
		song.add(g1, 11);
		song.add(a2, 11.33);

		return song;
	}

}
