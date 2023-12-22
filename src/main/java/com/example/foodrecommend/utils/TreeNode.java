package com.example.foodrecommend.utils;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class TreeNode {
    public String label;
    public Object value;
    public List<TreeNode> children;

    public TreeNode() {
        // 无参构造函数
    }

    public TreeNode(String label, Object value) {
        this.label = label;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode node) {
        this.children.add(node);
    }

    // 可选：添加 getter 方法
}

