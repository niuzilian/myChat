package com.niu.common.util;

/**
 * @program: myChat
 * @description:int 和byte数组之间的相互转化
 * @author: niuzilian
 * @create: 2019-04-13 17:36
 **/
public class IntUtil {
    /**
     * 将整数转换成字节数组
     */
    public static byte[] int2ByteArr(int i){
        byte[] bytes = new byte[4] ;
        bytes[0] = (byte)(i >> 24) ;
        bytes[1] = (byte)(i >> 16) ;
        bytes[2] = (byte)(i >> 8) ;
        bytes[3] = (byte)(i >> 0) ;
        return bytes ;
    }

    /**
     * 将字节数组转换成整数
     */
    public static int byteArr2Int(byte[] arr) {
        return (arr[0] & 0xff) << 24
                | (arr[1] & 0xff) << 16
                | (arr[2] & 0xff) << 8
                | (arr[3] & 0xff) << 0;
    }
}
