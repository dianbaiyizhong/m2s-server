package com.nntk.m2s.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CollectionUtil {
    public static List<Integer> findLongestConsecutive(List<Integer> nums) {
        if (nums == null || nums.isEmpty()) {
            return new ArrayList<>();
        }

        // 先排序（假设原列表可能无序）
        Collections.sort(nums);

        List<Integer> currentSequence = new ArrayList<>();
        List<Integer> longestSequence = new ArrayList<>();

        currentSequence.add(nums.get(0));

        for (int i = 1; i < nums.size(); i++) {
            // 检查是否连续（当前数字等于前一个数字 +1）
            if (nums.get(i) == nums.get(i - 1) + 1) {
                currentSequence.add(nums.get(i));
            } else {
                // 如果不连续，比较并更新最长序列
                if (currentSequence.size() > longestSequence.size()) {
                    longestSequence = new ArrayList<>(currentSequence);
                }
                currentSequence.clear();
                currentSequence.add(nums.get(i));
            }
        }

        // 最后一次比较（防止最长序列在末尾未被处理）
        if (currentSequence.size() > longestSequence.size()) {
            longestSequence = new ArrayList<>(currentSequence);
        }

        return longestSequence;
    }
}
