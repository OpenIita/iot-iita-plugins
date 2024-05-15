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

package cc.iotkit.plugins.hydrovalve.analysis;

/**
 * @Author：tfd
 * @Date：2024/1/9 15:41
 */
public abstract  class ModBusAnalysis {
    /**
     * 编码：将实体打包成报文
     *
     * @param entity 实体
     * @return 数据报文
     */
    public abstract byte[] packCmd4Entity(ModBusEntity entity);

    /**
     * 解包：将报文解码成实体
     *
     * @param arrCmd 报文
     * @return 实体
     */
    public abstract ModBusEntity unPackCmd2Entity(byte[] arrCmd);
}
