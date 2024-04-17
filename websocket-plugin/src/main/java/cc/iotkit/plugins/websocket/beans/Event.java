package cc.iotkit.plugins.websocket.beans;

import lombok.Data;

/**
 * @Author：tfd
 * @Date：2024/4/11 16:57
 */
@Data
public class Event {
    private String eventType;
    private EventData data;
    private String origin;
    private String timeFired;
    private Object context;

    @Data
    public class EventData{
        private String entityId;
        private StateData oldState;
        private StateData newState;
    }

    @Data
    public static class StateData{
        private String entityId;
        private String state;
        private Object attributes;
        private String lastChanged;
        private String lastUpdated;
        private Object context;
    }

}
