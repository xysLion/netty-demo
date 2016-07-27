package com.ancun.common.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

/**
 * 把ByteBuff转换成Bytes工具类
 *
 * @Created on 2015年5月8日
 * @author xys
 * @version 1.0
 * @Copyright:杭州安存网络科技有限公司 Copyright (c) 2015
 */
public class ByteBufToBytes {
    private ByteBuf	temp;

    private boolean	end	= true;

    public ByteBufToBytes() {}

    public ByteBufToBytes(int length) {
        temp = Unpooled.buffer(length);
    }

    /**
     * 从一个ByteBuf中读取到临时ByteBuf
     *
     * @param datas 目标ByteBuf
     */
    public void reading(ByteBuf datas) {
        datas.readBytes(temp, datas.readableBytes());
        if (this.temp.writableBytes() != 0) {
            end = false;
        } else {
            end = true;
        }
    }

    /**
     * 是否已经复制完
     *
     * @return
     */
    public boolean isEnd() {
        return end;
    }

    /**
     * 将临时ByteBuf转换成byte数组
     *
     * @return
     */
    public byte[] readFull() {
        if (end) {
            byte[] contentByte = new byte[this.temp.readableBytes()];
            this.temp.readBytes(contentByte);
            this.temp.release();
            return contentByte;
        } else {
            return null;
        }
    }

    /**
     * 将目标ByteBuf转换成byte数组
     *
     * @param datas 目标ByteBuf
     * @return
     */
    public byte[] read(ByteBuf datas) {
        byte[] bytes = new byte[datas.readableBytes()];
        datas.readBytes(bytes);
        return bytes;
    }
}
