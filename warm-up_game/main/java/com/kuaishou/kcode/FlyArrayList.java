package com.kuaishou.kcode;

import java.util.Arrays;

/* 自己用数组管理数据不用库里的 */
public class FlyArrayList {
	/* 本体 */
    int[] array;

    /* 有多少元素？ */
    int length;

    int total;

    /* 构造函数 */
    FlyArrayList() {
        this.array = new int[3150];
    }

    /* 重置 */
    void reset() {
        this.length = this.total = 0;
    }

    /* 获取元素 */
    int get(int idx) {
        return array[idx];
    }

    /* 添加元素 */
    void add(int elem) {
        this.array[this.length++] = elem;
        this.total += elem;
    }

    /* 排序::从小到大 */
    void sort() {
        Arrays.sort(this.array, 0, this.length);
    }
}
