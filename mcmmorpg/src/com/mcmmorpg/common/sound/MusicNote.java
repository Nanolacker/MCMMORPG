package com.mcmmorpg.common.sound;

/**
 * Music notes whose pitches can be easily obtained.
 */
public enum MusicNote {
    F_SHARP_1(0.5f), G_FLAT_1(0.5f), G_2(0.529732f), G_SHARP_3(0.561231f), A_FLAT_3(0.561231f), A_4(0.594604f),
    A_SHARP_5(0.629961f), B_FLAT_5(0.629961f), B_6(0.667420f), C_7(0.707107f), C_SHARP_8(0.749154f),
    D_Flat_8(0.749154f), D_9(0.793701f), D_SHARP_10(0.840896f), E_FLAT_10(0.840896f), E_11(0.890899f), F_12(0.943874f),
    F_SHARP_13(1), G_FLAT_13(1), G_14(1.059463f), G_SHARP_15(1.122462f), A_FLAT_15(1.122462f), A_16(1.189207f),
    A_SHARP_17(1.259921f), B_FLAT_17(1.259921f), B_18(1.334840f), C_19(1.414214f), C_SHARP_20(1.498307f),
    D_FLAT_20(1.498307f), D_21(1.587401f), D_SHARP_22(1.681793f), E_FLAT_22(1.681793f), E_23(1.781797f),
    F_24(1.887749f), F_SHARP_24(2), G_FLAT_24(2);

    private final float pitch;

    private MusicNote(float pitch) {
        this.pitch = pitch;
    }

    /**
     * Returns the pitch of this music note.
     */
    public float getPitch() {
        return pitch;
    }
}
