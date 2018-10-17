package com.lxgy.practice.interview;

import java.util.Random;

/**
 * @author Gryant
 * <p>
 * 10亿数据，求Top1000
 * <p>
 * 1，一台机器(内存 2G)
 * 这种情况可以用堆来做，在堆中维护1000个数的小顶堆
 * 根据堆的性质，每一个节点都比它的左右子节点要小
 * 先取前N个数构成小顶堆
 * 然后从文件中读取数据,并且和堆顶大小相比，如果比堆顶还小就直接丢弃
 * 如果比堆顶大，就替换堆顶，并调整最小堆
 * 然后对小顶堆进行调整，保持小顶堆的性质
 * 所有数据处理完毕后，小顶堆内就是topN
 * 并且，数据只会读取一次，不会存在多次读写的问题
 * <p>
 * 2，可以多台机器
 * 这种情况可以用 bitmap 分布式 思想
 * 将数据切分，分布在多台机器上
 * 分别结算每台机器的top1000，并行计算，最后再汇总
 */
public class TopN {

    /**
     * 父节点
     *
     * @param n
     * @return
     */
    private int parent(int n) {
        return (n - 1) / 2;
    }

    /**
     * 左孩子
     *
     * @param n
     * @return
     */
    private int left(int n) {
        return 2 * n + 1;
    }

    /**
     * 右孩子
     *
     * @param n
     * @return
     */
    private int right(int n) {
        return 2 * n + 2;
    }

    /**
     * 构建堆
     *
     * @param n
     * @param data
     */
    private void buildHeap(int n, int[] data) {
        for (int i = 1; i < n; i++) {

            // 父节点索引
            int parentIndex = parent(i);

            while (data[parentIndex] > data[i]) {
                int temp = data[i];
                data[i] = data[parentIndex];
                data[parentIndex] = temp;
                i = parentIndex;
            }
        }
    }

    /**
     * 调整data[i]
     *
     * @param i
     * @param n
     * @param data
     */
    private void adjust(int i, int n, int[] data) {

        if (data[i] <= data[0]) {
            return;
        }

        // 置换堆顶
        int temp = data[i];
        data[i] = data[0];
        data[0] = temp;

        // 调整堆顶
        int t = 0;
        while ((left(t) < n && data[t] > data[left(t)])
                || (right(t) < n && data[t] > data[right(t)])) {
            if (right(t) < n && data[right(t)] < data[left(t)]) {
                // 右孩子更小，置换右孩子
                temp = data[t];
                data[t] = data[right(t)];
                data[right(t)] = temp;
                t = right(t);
            } else {
                // 否则置换左孩子
                temp = data[t];
                data[t] = data[left(t)];
                data[left(t)] = temp;
                t = left(t);
            }
        }
    }

    /**
     * 寻找topN，该方法改变data，将topN排到最前面
     *
     * @param n
     * @param data
     */
    public void findTopN(int n, int[] data) {

        // 先构建n个数的小顶堆
        buildHeap(n, data);

        // n往后的数进行调整
        for (int i = n; i < data.length; i++) {
            adjust(i, n, data);
        }
    }

    /**
     * 打印数组
     *
     * @param data
     */
    public void print(int[] data) {

        for (int i = 0; i < data.length; i++) {
            System.out.print(data[i] + " ");
        }
        System.out.println();
    }

    public static void main(String[] args) {

        TopN topN = new TopN();

        // 第一组测试
        int[] arr1 = new int[]{56, 30, 71, 18, 29, 93, 44, 75, 20, 65, 68, 34};

        System.out.println("原数组：");
        topN.print(arr1);
        topN.findTopN(5, arr1);
        System.out.println("调整后数组：");
        topN.print(arr1);

        // 第二组测试
        int[] arr2 = new int[1000];
        for (int i = 0; i < arr2.length; i++) {
            arr2[i] = i + 1;
        }

        System.out.println("原数组：");
        topN.print(arr2);
        topN.findTopN(5, arr2);
        System.out.println("调整后数组：");
        topN.print(arr2);

        // 第三组测试
        Random random = new Random();
        int[] arr3 = new int[1000];
        for (int i = 0; i < arr3.length; i++) {
            arr3[i] = random.nextInt();
        }

        System.out.println("原数组：");
        topN.print(arr3);
        topN.findTopN(5, arr3);
        System.out.println("调整后数组：");
        topN.print(arr3);
    }

}
