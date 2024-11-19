package com.yahtzee.Model;

import java.util.Optional;

public class StrategyTree {
    private final StrategyNode root;

    private StrategyTree() {
        root = new StrategyNode();
        buildSubtree(5, root);
    }

    private void buildSubtree(int depth, StrategyNode parent) {
        if (depth == 0) return;

        for (int i = 1; i <= 6; i++) {
            parent.addChild(i);
            Optional<StrategyNode> child = parent.getChild(i);
            buildSubtree(depth - 1, child.get());
        }

    }

    public static StrategyTree getInstance() {
        return StrategyTreeHolder.INSTANCE;
    }

    private static class StrategyTreeHolder {
        private static final StrategyTree INSTANCE = new StrategyTree();
    }

    public StrategyNode getRoot() {
        return root;
    }
}