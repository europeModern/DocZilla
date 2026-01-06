package org.example.liquidsort;

import java.util.ArrayList;
import java.util.List;

public class LiquidSortSolver {
    private List<Tube> tubes;
    private List<Move> solution;
    private final int numTubes;
    private final int tubeCapacity;

    public LiquidSortSolver(int[][] initialState) {
        this.numTubes = initialState.length;
        this.tubeCapacity = initialState[0].length;
        this.tubes = new ArrayList<>();
        this.solution = new ArrayList<>();


        for (int[] tubeData : initialState) {
            Tube tube = new Tube(tubeCapacity);
            for (int color : tubeData) {
                if (color != 0) { // 0 - пусто
                    tube.addDrop(color);
                }
            }
            tubes.add(tube);
        }
    }


    public List<Move> solve() {
        solution.clear();
        return solution;
    }


    public boolean isSolved() {
        for (Tube tube : tubes) {
            if (!tube.isSorted()) {
                return false;
            }
        }
        return true;
    }

    public boolean makeMove(int fromTube, int toTube) {
        if (fromTube < 0 || fromTube >= numTubes || toTube < 0 || toTube >= numTubes) {
            return false;
        }
        if (fromTube == toTube) {
            return false;
        }

        Tube from = tubes.get(fromTube);
        Tube to = tubes.get(toTube);

        int poured = from.pourTo(to, null);
        if (poured > 0) {
            solution.add(new Move(fromTube, toTube));
            return true;
        }
        return false;
    }

    public List<Tube> getTubes() {
        return tubes;
    }


    public List<Move> getSolution() {
        return new ArrayList<>(solution);
    }

    public LiquidSortSolver copy() {
        int[][] state = new int[numTubes][tubeCapacity];
        for (int i = 0; i < numTubes; i++) {
            List<Integer> drops = tubes.get(i).getDrops();
            for (int j = 0; j < tubeCapacity; j++) {
                if (j < drops.size()) {
                    state[i][j] = drops.get(j);
                } else {
                    state[i][j] = 0;
                }
            }
        }
        LiquidSortSolver copy = new LiquidSortSolver(state);
        copy.solution = new ArrayList<>(this.solution);
        return copy;
    }
}

