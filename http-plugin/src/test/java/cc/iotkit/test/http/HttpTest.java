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

package cc.iotkit.test.http;

import cc.iotkit.common.utils.ThreadUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HttpTest {

    public static void main(String[] args) {
        ScheduledThreadPoolExecutor timer = ThreadUtil.newScheduled(1, "http-test");
        timer.scheduleWithFixedDelay(HttpTest::report, 0, 3, TimeUnit.SECONDS);
    }

    public static void report() {
        HttpResponse rst = HttpUtil.createPost("http://127.0.0.1:9084/sys/cGCrkK7Ex4FESAwe/cz00001/properties")
                .header("secret", "mBCr3TKstTj2KeM6")
                .body(new JsonObject()
                        .put("id", IdUtil.fastSimpleUUID())
                        .put("params", new JsonObject()
                                .put("powerstate", RandomUtil.randomInt(0, 2))
                                .put("rssi", RandomUtil.randomInt(-127, 127))
                                .getMap()
                        ).encode()
                ).execute();
        log.info("send result:status={},body={}", rst.getStatus(), rst.body());
    }

}
