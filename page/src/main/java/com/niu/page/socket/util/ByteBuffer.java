package com.niu.page.socket.util;

import java.util.Arrays;

/**
 * @program: myChatClient
 * @description:
 * @author: niuzilian
 * @create: 2019-04-27 00:41
 **/
public final class ByteBuffer {
    /**
     * 核心数组
     */
    private byte[] value;
    /**
     * 实际值的长度
     */
    private int count;

    public ByteBuffer() {
        this.value = new byte[16];
    }

    public ByteBuffer(byte[] bytes) {
        this.value = Arrays.copyOf(bytes, bytes.length);
        this.count = bytes.length;
    }

    public ByteBuffer append(byte[] bytes) {
        if (bytes.length == 0) {
            return this;
        }
        ensureCapacityInternal(bytes.length + count);
        System.arraycopy(bytes, 0, value, count, bytes.length);
        count = count + bytes.length;
        return this;
    }

    /**
     * 1.实际长度+新的数组长度小于value.length    保持原数组
     * 2.实际长度+新的数组长度大于value.length
     * a.求 value.length*2+2 大小
     * b.取 value.length*2+2 和 实际长度+新的数组 中的最大值
     * 3.创建新数组
     */
    private void ensureCapacityInternal(int minimumCapacity) {
        if (minimumCapacity > value.length) {
            int newCapacity = newCapacity(minimumCapacity);
            value = Arrays.copyOf(value, newCapacity);
        }
    }

    private int newCapacity(int minimumCapacity) {
        int newCapacity = (value.length << 1) + 2;
        if (newCapacity - minimumCapacity < 0) {
            newCapacity = minimumCapacity;
        }
        return newCapacity;
    }

    public int length() {
        return count;
    }

    public int capacity() {
        return value.length;
    }

    public byte[] toBytes() {
        if (count == value.length) {
            return value;
        }
        return Arrays.copyOf(value, count);
    }
}
