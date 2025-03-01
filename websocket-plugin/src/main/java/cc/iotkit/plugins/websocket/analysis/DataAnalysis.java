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

package cc.iotkit.plugins.websocket.analysis;

import cc.iotkit.common.utils.JsonUtils;
import cc.iotkit.plugins.websocket.beans.Event;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author：tfd
 * @Date：2024/4/11 15:42
 */
@Slf4j
public class DataAnalysis {
    public static final String EVENT_STATE_CHANGED="state_changed";
    public static final ObjectMapper mapper = new ObjectMapper().setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);

    public static Map<String, Object> stateChangedEvent(Event.StateData oldState, Event.StateData newState){
        Map<String, Object> ret= new HashMap<>();
        if(!oldState.getState().equals(newState.getState())){
            ret.put("state",newState.getState());
        }
        HashMap<String,Object> oldAttributes=JsonUtils.parseObject(JsonUtils.toJsonString(oldState.getAttributes()),HashMap.class);
        HashMap<String,Object> newAttributes=JsonUtils.parseObject(JsonUtils.toJsonString(newState.getAttributes()),HashMap.class);
        newAttributes.forEach((key, value) -> {
            if(ObjectUtil.isNotNull(value)&&!JsonUtils.toJsonString(oldAttributes.get(key)).equals(JsonUtils.toJsonString(newAttributes.get(key)))){
                ret.put(StrUtil.toCamelCase(key),value);
            }
        });
        log.info("analysis:{}",JsonUtils.toJsonString(ret));
        return ret;
    }
}
