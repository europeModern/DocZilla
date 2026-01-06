package org.example.liquidsort;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LiquidSortSolver {
    private List<Tube> tubes;
    private List<Move> solution;
    private final int numTubes;
    private final int tubeCapacity;
    private static final int MAX_MOVES = 10000;

    public LiquidSortSolver(String[][] initialState) {
        this.numTubes = initialState.length;
        this.tubeCapacity = initialState[0].length;
        this.tubes = new ArrayList<>();
        this.solution = new ArrayList<>();


        for (String[] tubeData : initialState) {
            Tube tube = new Tube(tubeCapacity);
            for (String color : tubeData) {
                if (color != null && !color.equals("0") && !color.trim().isEmpty()) { // 0 - пусто
                    tube.addDrop(color);
                }
            }
            tubes.add(tube);
        }
    }


    public List<Move> solve() {
        solution.clear();
        
        if (isSolved()) {
            return solution;
        }
        int movesCount = 0;
        Set<String> visitedStates = new HashSet<>();
        
        while (!isSolved() && movesCount < MAX_MOVES) {
            Move bestMove = findBestMove(visitedStates);
            if (bestMove == null) {
                break;
            }
            makeMove(bestMove.getFromTube(), bestMove.getToTube());
            movesCount++;
            String stateKey = getStateKey();
            visitedStates.add(stateKey);
        }
        return solution;
    }

    private Move findBestMove(Set<String> visitedStates) {
        Move bestMove = null;
        int bestScore = Integer.MIN_VALUE;

        for (int from = 0; from < numTubes; from++) {
            for (int to = 0; to < numTubes; to++) {
                if (from == to) continue;
                
                if (isValidMove(from, to)) {
                    int score = evaluateMove(from, to, visitedStates);
                    
                    if (score > bestScore) {
                        bestScore = score;
                        bestMove = new Move(from, to);
                    }
                }
            }
        }
        return bestMove;
    }

    private boolean isValidMove(int from, int to) {
        Tube fromTube = tubes.get(from);
        Tube toTube = tubes.get(to);

        if (fromTube.isEmpty()) {
            return false;
        }
        if (toTube.isFull()) {
            return false;
        }

        String topColor = fromTube.getTopColor();
        if (toTube.isEmpty()) {
            return true;
        }

        return toTube.getTopColor().equals(topColor);
    }

    private int evaluateMove(int from, int to, Set<String> visitedStates) {
        Tube fromTube = tubes.get(from);
        Tube toTube = tubes.get(to);
        int score = 0;

        LiquidSortSolver testSolver = this.copy();
        boolean moveSuccess = testSolver.makeMove(from, to);
        if (!moveSuccess) {
            return Integer.MIN_VALUE;
        }
        
        String newStateKey = testSolver.getStateKey();
        
        if (visitedStates.contains(newStateKey)) {
            return Integer.MIN_VALUE;
        }
        boolean fromWasSorted = fromTube.isSorted();
        boolean toWasSorted = toTube.isSorted();
        boolean toWasEmpty = toTube.isEmpty();

        int topColorCount = fromTube.getTopColorCount();
        int freeSpace = toTube.getFreeSpace();
        int canPour = Math.min(topColorCount, freeSpace);

        Tube newFromTube = testSolver.getTubes().get(from);
        Tube newToTube = testSolver.getTubes().get(to);

        boolean fromNowSorted = newFromTube.isSorted();
        boolean toNowSorted = newToTube.isSorted();

        if (toWasEmpty && fromNowSorted) {
            score += 1000;
        }
        if (toWasEmpty && canPour == fromTube.getSize()) {
            score += 500;
        }
        if (!toWasSorted && toNowSorted) {
            score += 800;
        }
        if (fromWasSorted && !fromNowSorted) {
            score -= 1000;
        }
        if (toWasSorted && !toNowSorted) {
            score -= 1000;
        }

        if (toTube.getTopColor() != null && toTube.getTopColor().equals(fromTube.getTopColor())) {
            int combinedCount = canPour + toTube.getTopColorCount();
            if (combinedCount == tubeCapacity) {
                score += 600;
            } else {
                score += combinedCount * 10;
            }
        }

        score += canPour * 5;

        return score;
    }

    private String getStateKey() {
        StringBuilder sb = new StringBuilder();
        for (Tube tube : tubes) {
            sb.append("[");
            for (String drop : tube.getDrops()) {
                sb.append(drop).append(",");
            }
            sb.append("]");
        }
        return sb.toString();
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
        String[][] state = new String[numTubes][tubeCapacity];
        for (int i = 0; i < numTubes; i++) {
            List<String> drops = tubes.get(i).getDrops();
            for (int j = 0; j < tubeCapacity; j++) {
                if (j < drops.size()) {
                    state[i][j] = drops.get(j);
                } else {
                    state[i][j] = "0";
                }
            }
        }
        LiquidSortSolver copy = new LiquidSortSolver(state);
        copy.solution = new ArrayList<>(this.solution);
        return copy;
    }
}