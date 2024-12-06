package com.yahtzee.Model;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * This class represents a node in the strategy tree used for decision-making in the Yahtzee game.
 * Each node stores a value, a reference to its parent node, and a map of its child nodes, representing
 * possible strategies based on the current dice values.
 */
public class StrategyNode {
    private int value;
    private StrategyNode parent;
    private final Map<Integer, StrategyNode> children;

    /**
     * Constructs a new StrategyNode with a default value of 0 and no parent or children.
     */
    public StrategyNode() {
        this.value = 0;
        this.parent = null;
        this.children = new HashMap<>();
    }

    /**
     * Constructs a new StrategyNode with the specified value and no parent or children.
     *
     * @return The root node of the strategy tree
     */
    public StrategyNode getRoot() {
        StrategyNode currentNode = this;
        while (currentNode.parent != null) {
            currentNode = currentNode.parent;
        }
        return currentNode;
    }

    /**
     * Returns a string representation of the path from the root node to the current node, formatted
     * as a series of values separated by " -> ".
     *
     * @return A string representing the path from the root to the current node.
     */
    @NonNull
    @Override
    public String toString() {
        return getPath().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" -> "));
    }

    /**
     * Adds a new child node with the specified value to the current node. If a child node with the
     * specified value already exists, no new node is added.
     *
     * @param value the value of the new child node
     */
    public void addChild(int value) {
        if (children.containsKey(value)) return;

        StrategyNode child = new StrategyNode();
        child.value = value;
        child.parent = this;
        children.put(value, child);
    }

    /**
     * Returns whether the current node is a leaf node, i.e., it has no children.
     *
     * @return true if the node is a leaf, false otherwise
     */
    public boolean isLeaf() {
        return children.isEmpty();
    }

    /**
     * Returns the child node with the specified value, if it exists.
     *
     * @param value the value of the child node to retrieve
     * @return an Optional containing the child node, or an empty Optional if no such child exists
     */
    public Optional<StrategyNode> getChild(int value) {
        return Optional.ofNullable(children.get(value));
    }

    /**
     * Retrieves the child node corresponding to a specific path of values.
     * The path is a sequence of dice values, and this method navigates through the tree to find the
     * corresponding node.
     *
     * @param path A list of integers representing the path to the desired child node.
     * @return An Optional containing the node at the specified path, or an empty Optional if not found.
     */
    public Optional<StrategyNode> getChild(List<Integer> path) {
        StrategyNode currentNode = this;
        for (int node : path) {
            Optional<StrategyNode> child = currentNode.getChild(node);
            if (child.isEmpty()) return Optional.empty();
            currentNode = child.get();
        }
        return Optional.of(currentNode);
    }

    /**
     * Returns the path from the root node to the current node as a list of values.
     *
     * @return A list of integers representing the path from the root to the current node.
     */
    public List<Integer> getPath() {
        List<Integer> path = new ArrayList<>();
        StrategyNode currentNode = this;
        while (currentNode.parent != null) {
            path.add(currentNode.value);
            currentNode = currentNode.parent;
        }
        Collections.reverse(path);
        return path;
    }

    /**
     * Returns the value of the current node.
     *
     * @return the value of the node
     */
    public int getValue() {
        return value;
    }

    /**
     * Returns the parent node of the current node.
     *
     * @return the parent node
     */
    public double getProbability(Category category) {
        // Placeholder for getProbability method
        if (isLeaf()) {
            if (category.isValid(getPath())) {
                return 1.0;
            } else {
                return 0.0;
            }
        }

        return children.values().stream()
                .map(child -> child.getProbability(category))
                .reduce(0.0, Double::sum)
                / children.size();
    }

    /**
     * Calculates the expected score based on the current path and a given category.
     * If the node is a leaf, it calculates the score for the path using the category. Otherwise,
     * it calculates the expected value based on its children.
     *
     * @param category The category to evaluate.
     * @return The expected score for the current path and category.
     */
    public double getExpectedValue(Category category) {
        if (isLeaf()) {
            if (category.isValid(getPath())) {
                return category.calculateScore(getPath());
            } else {
                return 0.0;
            }
        }

        return children.values().stream()
                .map(child -> child.getExpectedValue(category))
                .reduce(0.0, Double::sum)
                / children.size();
    }

    /**
     * Calculates the expected score for a list of categories, selecting the maximum score across all categories.
     * If the node is a leaf, it calculates the score for each category and returns the highest value.
     * Otherwise, it calculates the expected value based on its children.
     *
     * @param categories A list of categories to evaluate.
     * @return The expected score for the current path and categories.
     */
    public double getExpectedValue(List<Category> categories) {
        if (isLeaf()) {
            return categories.stream()
                    .mapToDouble(category -> category.calculateScore(getPath()))
                    .max()
                    .orElse(0.0);
        }

        return children.values().stream()
                .map(child -> child.getExpectedValue(categories))
                .reduce(0.0, Double::sum)
                / children.size();

    }

    /**
     * Returns the best descendant node based on a given category.
     * The best descendant is the child node with the highest score for the given category.
     *
     * @param category The category to evaluate.
     * @return The best descendant node based on the category.
     */
    public StrategyNode getBestDescendant(Category category) {
        if (isLeaf()) {
            return this;
        }

        return children.values().stream()
                .map(child -> child.getBestDescendant(category))
                .max(Comparator.comparingInt(child -> category.calculateScore(child.getPath())))
                .get();
    }

    /**
     * Returns the best descendant node based on a list of categories.
     * The best descendant is the child node with the highest score across all categories.
     *
     * @param categories A list of categories to evaluate.
     * @return The best descendant node based on the categories.
     */
    public StrategyNode getBestDescendant(List<Category> categories) {
        if (isLeaf()) {
            return this;
        }

        return children.values().stream()
                .map(child -> child.getBestDescendant(categories))
                .max(Comparator.comparingDouble(child -> child.getExpectedValue(categories)))
                .get();
    }

    /**
     * Returns the leaf nodes of the current node.
     *
     * @return A list of leaf nodes.
     */
    public List<StrategyNode> getLeafNodes() {
        if (isLeaf()) {
            return List.of(this);
        }

        return children.values().stream()
                .map(StrategyNode::getLeafNodes)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }
}