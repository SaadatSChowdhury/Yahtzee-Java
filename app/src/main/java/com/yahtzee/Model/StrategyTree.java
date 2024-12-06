package com.yahtzee.Model;

import java.util.Optional;

/**
 * This class represents a strategy tree used for decision-making in the Yahtzee game.
 * It constructs a hierarchical tree structure where each node represents a potential strategy
 * based on dice values and the depth of the tree.
 */
public class StrategyTree {
    private final StrategyNode root;

    /**
     * Private constructor for the StrategyTree class. It initializes the tree with a root node
     * and recursively builds the subtree based on the specified depth.
     */
    private StrategyTree() {
        root = new StrategyNode();
        buildSubtree(5, root);
    }

    /**
     * Recursively builds the subtree of the strategy tree based on the specified depth.
     *
     * @param depth  the depth of the subtree
     * @param parent the parent node of the subtree
     */
    private void buildSubtree(int depth, StrategyNode parent) {
        if (depth == 0) return;

        for (int i = 1; i <= 6; i++) {
            parent.addChild(i);
            Optional<StrategyNode> child = parent.getChild(i);
            buildSubtree(depth - 1, child.get());
        }

    }

    /**
     * Returns the singleton instance of the StrategyTree class.
     *
     * @return the singleton instance of the StrategyTree
     */
    public static StrategyTree getInstance() {
        return StrategyTreeHolder.INSTANCE;
    }

    /**
     * Private static inner class to hold the singleton instance of the StrategyTree class.
     */
    private static class StrategyTreeHolder {
        private static final StrategyTree INSTANCE = new StrategyTree();
    }

    /**
     * Returns the root node of the strategy tree.
     *
     * @return the root node of the strategy tree
     */
    public StrategyNode getRoot() {
        return root;
    }
}