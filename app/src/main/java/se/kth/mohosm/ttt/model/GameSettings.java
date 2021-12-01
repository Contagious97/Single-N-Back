package se.kth.mohosm.ttt.model;

public class GameSettings {
    private static boolean audioStimuli;
    private static boolean patternStimuli;
    private static int timeBetweenEvents;
    private static int nrOfEvents;

    private GameSettings() {
    }

    public static boolean isAudioStimuli() {
        return audioStimuli;
    }

    public static void setAudioStimuli(boolean audioStimuli) {
        GameSettings.audioStimuli = audioStimuli;
    }

    public static boolean isPatternStimuli() {
        return patternStimuli;
    }

    public static void setPatternStimuli(boolean patternStimuli) {
        GameSettings.patternStimuli = patternStimuli;
    }

    public static int getTimeBetweenEvents() {
        return timeBetweenEvents;
    }

    public static void setTimeBetweenEvents(int timeBetweenEvents) {
        GameSettings.timeBetweenEvents = timeBetweenEvents;
    }

    public static int getNrOfEvents() {
        return nrOfEvents;
    }

    public static void setNrOfEvents(int nrOfEvents) {
        GameSettings.nrOfEvents = nrOfEvents;
    }
}
