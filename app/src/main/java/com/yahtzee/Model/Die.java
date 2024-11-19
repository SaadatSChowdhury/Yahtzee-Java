package com.yahtzee.Model;

import java.util.Random;

public class Die {
    private int value;
    private boolean kept;

    private boolean marked;
    private boolean markedForHelpKeep;

    public Die() {
        value = 0;
        kept = false;
        marked = false;
        markedForHelpKeep = false;
    }

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        if (value < 1) {
            this.value = 6;
        } else if (value > 6) {
            this.value = 1;
        } else {
            this.value = value;
        }
    }

    public boolean isKept() {
        return kept;
    }

    public void setKept(boolean kept) {
        this.kept = kept;
        this.markedForHelpKeep = false;
    }

    public boolean isMarked() {
        return marked;
    }

    public void setMarked(boolean marked) {
        this.marked = marked;
    }

    public boolean isMarkedForHelpKeep() {
        return markedForHelpKeep;
    }

    public void setMarkedForHelpKeep(boolean markedForHelpKeep) {
        this.markedForHelpKeep = markedForHelpKeep;
    }

    public void roll() {
        value = new Random().nextInt(6) + 1;
    }

    public void reset() {
        value = 0;
        kept = false;
        marked = false;
        markedForHelpKeep = false;
    }
}
