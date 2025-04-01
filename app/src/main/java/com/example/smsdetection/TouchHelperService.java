package com.example.smsdetection;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

// TouchHelperService.java
public class TouchHelperService extends AccessibilityService {
    private static TouchHelperService instance;

    @Override
    public void onServiceConnected() {
        instance = this;
    }

    public static String getWindowLayout() {
        if (instance == null) return "无障碍服务未启用";

        AccessibilityNodeInfo root = instance.getRootInActiveWindow();
        if (root == null) return "无法获取窗口信息";

        StringBuilder sb = new StringBuilder();
        dumpNodeInfo(root, sb, 0);
        root.recycle();

        return sb.toString();
    }

    private static void dumpNodeInfo(AccessibilityNodeInfo node, StringBuilder sb, int depth) {
        // 添加缩进
        for (int i = 0; i < depth; i++) sb.append("  ");

        // 添加节点信息
        sb.append("Class: ").append(node.getClassName())
                .append(", Text: ").append(node.getText())
                .append(", ID: ").append(node.getViewIdResourceName())
                .append("\n");

        // 递归处理子节点
        for (int i = 0; i < node.getChildCount(); i++) {
            AccessibilityNodeInfo child = node.getChild(i);
            if (child != null) {
                dumpNodeInfo(child, sb, depth + 1);
                child.recycle();
            }
        }
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {}

    @Override
    public void onInterrupt() {}
}