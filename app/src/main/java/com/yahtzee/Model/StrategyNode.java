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

public class StrategyNode {
    private int value;
    private StrategyNode parent;
    private final Map<Integer, StrategyNode> children;

    public StrategyNode() {
        this.value = 0;
        this.parent = null;
        this.children = new HashMap<>();
    }

    public StrategyNode getRoot() {
        StrategyNode currentNode = this;
        while (currentNode.parent != null) {
            currentNode = currentNode.parent;
        }
        return currentNode;
    }

    @NonNull
    @Override
    public String toString() {
        return getPath().stream()
                .map(String::valueOf)
                .collect(Collectors.joining(" -> "));
    }

    public void addChild(int value) {
        if (children.containsKey(value)) return;

        StrategyNode child = new StrategyNode();
        child.value = value;
        child.parent = this;
        children.put(value, child);
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }

    public Optional<StrategyNode> getChild(int value) {
        return Optional.ofNullable(children.get(value));
    }

    public Optional<StrategyNode> getChild(List<Integer> path) {
        StrategyNode currentNode = this;
        for (int node : path) {
            Optional<StrategyNode> child = currentNode.getChild(node);
            if (child.isEmpty()) return Optional.empty();
            currentNode = child.get();
        }
        return Optional.of(currentNode);
    }

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

    public int getValue() {
        return value;
    }

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

    public StrategyNode getBestDescendant(Category category) {
        if (isLeaf()) {
            return this;
        }

        return children.values().stream()
                .map(child -> child.getBestDescendant(category))
                .max(Comparator.comparingInt(child -> category.calculateScore(child.getPath())))
                .get();
    }

    public StrategyNode getBestDescendant(List<Category> categories) {
        if (isLeaf()) {
            return this;
        }

        return children.values().stream()
                .map(child -> child.getBestDescendant(categories))
                .max(Comparator.comparingDouble(child -> child.getExpectedValue(categories)))
                .get();
    }

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