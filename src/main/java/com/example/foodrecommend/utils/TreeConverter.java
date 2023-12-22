package com.example.foodrecommend.utils;
import java.util.*;

public class TreeConverter {

    public static ArrayList<TreeNode> mapToTree(Map<String, Object> map) {
        ArrayList<TreeNode> treeNodes = new ArrayList<>();
        TreeNode root = new TreeNode("展开：每种属性最多选一种", null);

        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof List) {
                TreeNode node = new TreeNode(entry.getKey(), null);
                for (Object item : (List<?>) entry.getValue()) {
                    node.addChild(new TreeNode(item.toString(), item));
                }
                root.addChild(node);
            } else {
                root.addChild(new TreeNode(entry.getKey(), entry.getValue()));
            }
        }
        treeNodes.add(root);

        return treeNodes;
    }


//    public static void main(String[] args) {
//        Map<String, Object> data = new HashMap<>();
//        data.put("口味", Arrays.asList("辣味", "甜味"));
//        data.put("食材口味", "面粉类");
//        data.put("热量", "高卡");
//        data.put("烹饪方式", Arrays.asList("煮", "炸"));
//
//        TreeNode tree = mapToTree(data);
//        // 现在 tree 包含了转换后的树形结构
//        System.out.println(tree);
//    }
}
