package org.example.liquidsort;

import java.util.ArrayList;
import java.util.List;

class StateNode {
    final LiquidSortSolver state;
    final List<Move> path;
    final int gCost;
    final int hCost;
    final int fCost;
    
    StateNode(LiquidSortSolver state, List<Move> path, int gCost, int hCost) {
        this.state = state;
        this.path = new ArrayList<>(path);
        this.gCost = gCost;
        this.hCost = hCost;
        this.fCost = gCost + hCost;
    }
}

