package com.mcmmorpg.common.ai;

import java.util.List;

/**
 * A behavior which has child behaviors.
 */
public abstract class Composite extends Behavior {
    private List<Behavior> children;

    public Behavior getChild(int index) {
        return children.get(index);
    }

    public int getChildCount() {
        return children.size();
    }
}
