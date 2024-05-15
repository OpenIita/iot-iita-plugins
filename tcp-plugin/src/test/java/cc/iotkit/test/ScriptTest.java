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

package cc.iotkit.test;

import cc.iotkit.plugin.core.LocalPluginScript;
import cc.iotkit.script.IScriptEngine;
import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

public class ScriptTest {
    IScriptEngine scriptEngine;

    @Before
    public void init() {
        scriptEngine = new LocalPluginScript("script.js").getScriptEngine("");
    }

    @Test
    public void testEncode() {
        String rst = scriptEngine.invokeMethod(new TypeReference<>() {
        }, "encode", Map.of("powerstate", 1));
        System.out.println(rst);
    }

}
