/*
 *
 *  * | Licensed 未经许可不能去掉「OPENIITA」相关版权
 *  * +----------------------------------------------------------------------
 *  * | Author: xw2sy@163.com
 *  * +----------------------------------------------------------------------
 *
 *  Copyright [2024] [OPENIITA]
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * /
 */

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
