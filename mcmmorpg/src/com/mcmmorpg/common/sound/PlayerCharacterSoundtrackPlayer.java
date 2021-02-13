package com.mcmmorpg.common.sound;

import com.mcmmorpg.common.character.PlayerCharacter;

/**
 * Manages the music played to a player character. Only plays one soundtrack at
 * a time.
 */
public class PlayerCharacterSoundtrackPlayer {
    private final PlayerCharacter pc;
    private SoundSequencePlayer noisePlayer;

    /**
     * Create a new soundtrack player for the specified player character.
     */
    public PlayerCharacterSoundtrackPlayer(PlayerCharacter pc) {
        this.pc = pc;
    }

    /**
     * Set the soundtrack to play to the player character, or null to stop playing
     * music.
     */
    public void setSoundtrack(SoundSequence soundtrack) {
        if (noisePlayer != null && soundtrack != noisePlayer.getSequence()) {
            noisePlayer.stop();
        }
        if (soundtrack == null) {
            noisePlayer = null;
        } else if (noisePlayer != null && soundtrack == noisePlayer.getSequence()) {
            return;
        } else {
            noisePlayer = new SoundSequencePlayer(soundtrack, pc);
            noisePlayer.setLooping(true);
            noisePlayer.play();
        }
    }
}
