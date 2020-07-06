package com.kuaishou.kcode;

import java.nio.MappedByteBuffer;

/**
    * 用于字符串转换各种类型，现在我们只需要转整形和长整型
 */
public class FlyAsciiConverter {
	MappedByteBuffer buffer;    /* 待转换的映射缓冲区 */
    int offset;                 /* 转换成一个类型后，新的偏移 */

    FlyAsciiConverter(MappedByteBuffer buffer, int offset) {
        this.buffer = buffer;
        this.offset = offset;
    }

    /**
                 * 字符串转换为整型，我们不考虑负数
     *
     * @return 转换成功的整数
     */
    int convertInt() {
        int value = 0;
        /* 逐个把字符串的字符转换为数字 */
        char tt = (char)buffer.get(offset);
        while ( (buffer.get(offset)) >= '0' ) {//比较的ASCII码
            value *= 10;
            value += buffer.get(offset) - '0';
            offset++;
        }

//        /* 对于 \r 则后面还有一个 \n，也需要跳过 */
//        if( (buffer.get(offset)) == '\r' )
//            offset++;
        offset++;       /* 跳过后面的 '\n' */
        return value;
    }
    
    
    /**
                *    字符串转换为长整型，我们不考虑负数
     *
     * @return 转换成功的长整数
     */
    long convertLong() {
        long value = 0;
        /* 逐个把字符串的字符转换为数字 */
//        char temp = (char) buffer.get(offset);
        while ( (buffer.get(offset)) >= '0' ) {
            value *= 10;
            value += buffer.get(offset) - '0';
            offset++;
        }

        offset++;       /* 跳过后面的 ',' */
        return value;
    }

    /**
                 * 字符串转字符串？更确切的来说是获取一个以 ',' 结尾的字符串，不包括 ','
     * @return 转换成功的字符串
     */
    String convertString() {
        StringBuilder ans = new StringBuilder();
        while ( (buffer.get(offset)) != ',' ) {
            ans.append((char)buffer.get(offset));
            offset++;
        }
        offset++;       /* 跳过后面的 ',' */
        return ans.toString();
    }

    /**
                 * 跳过一行记录
     */
    void skipOneLine() {
        while ( (this.buffer.get(this.offset)) != '\n' ) {
            this.offset++;
        }
        this.offset++;
    }

}
