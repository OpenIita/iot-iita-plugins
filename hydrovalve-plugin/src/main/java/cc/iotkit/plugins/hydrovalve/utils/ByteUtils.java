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

package cc.iotkit.plugins.hydrovalve.utils;

/**
 * @Author：tfd
 * @Date：2024/1/8 15:15
 */
public class ByteUtils {
    /**
     * 将十六进制的字符串转换成字节数组
     *
     * @param hexString
     * @return
     */
    public static byte[] hexStrToBinaryStr(String hexString) {

        if (hexString==null) {
            return null;
        }
        try {
            hexString = hexString.replaceAll(" ", "");
            int len = hexString.length();
            int index = 0;
            byte[] bytes = new byte[len / 2];
            while (index < len) {
                String sub = hexString.substring(index, index + 2);
                bytes[index/2] = (byte)Integer.parseInt(sub,16);
                index += 2;
            }
            return bytes;
        }catch (Exception e){
            return null;
        }

    }

    /**
     * 将字节数组转换成十六进制的字符串
     *
     * @return
     */
    public static String BinaryToHexString(byte[] bytes,boolean isBalank) {
        String hexStr = "0123456789ABCDEF";
        String result = "";
        String hex = "";
        Boolean feStart=true;
        for (byte b : bytes) {
            hex = String.valueOf(hexStr.charAt((b & 0xF0) >> 4));
            hex += String.valueOf(hexStr.charAt(b & 0x0F));
            if("FE".equals(hex) && feStart){
                continue;
            }else {
                feStart=false;
            }
            result += hex + (isBalank?" ":"");
        }
        return result;
    }
}
