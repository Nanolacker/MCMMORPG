package com.mcmmorpg.common.quest;

/**
 * Stores a player's progress on a single quest.
 */
public class PlayerCharacterQuestData {
    private final String questName;
    private final int[] objectiveData;

    PlayerCharacterQuestData(Quest quest) {
        this.questName = quest.getName();
        objectiveData = new int[quest.getObjectives().length];
        for (int i = 0; i < objectiveData.length; i++) {
            objectiveData[i] = -1;
            // by default, objectives are not accessible to player characters
        }
    }

    String getQuestName() {
        return questName;
    }

    boolean isAccessible(int objectiveIndex) {
        return objectiveData[objectiveIndex] != -1;
    }

    void setAccessible(int objectiveIndex, boolean accessible) {
        int value = accessible ? 0 : -1;
        objectiveData[objectiveIndex] = value;
    }

    int getProgress(int objectiveIndex) {
        return objectiveData[objectiveIndex];
    }

    void setProgress(int objectiveIndex, int progress) {
        objectiveData[objectiveIndex] = progress;
    }
}
