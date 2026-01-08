package org.example.liquidsort;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

public class LiquidSortSolver {
    private List<Tube> tubes;
    private List<Move> solution;
    private final int numTubes;
    private final int tubeCapacity;

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

        PriorityQueue<StateNode> queue = new PriorityQueue<>(Comparator.comparingInt(n -> n.fCost));
        Set<String> visitedStates = new HashSet<>();
        
        int initialHCost = calculateHeuristic(this);
        StateNode initialNode = new StateNode(this.copy(), new ArrayList<>(), 0, initialHCost);
        queue.offer(initialNode);
        visitedStates.add(this.getStateKey());
        
        int maxDepth = 200;
        int nodesProcessed = 0;
        int maxNodes = 1000000;
        while (!queue.isEmpty() && nodesProcessed < maxNodes) {
            StateNode current = queue.poll();
            if (current == null) break;
            
            nodesProcessed++;
            
            if (current.state.isSolved()) {
                solution = current.path;
                return solution;
            }
            
            if (current.path.size() >= maxDepth) {
                continue;
            }
            
            List<Move> validMoves = current.state.getAllValidMoves();
            for (Move move : validMoves) {
                LiquidSortSolver nextState = current.state.copy();
                nextState.makeMove(move.getFromTube(), move.getToTube());
                
                String stateKey = nextState.getStateKey();
                if (!visitedStates.contains(stateKey)) {
                    visitedStates.add(stateKey);
                    List<Move> newPath = new ArrayList<>(current.path);
                    newPath.add(move);
                    int newGCost = current.gCost + 1;
                    int newHCost = calculateHeuristic(nextState);
                    queue.offer(new StateNode(nextState, newPath, newGCost, newHCost));
                }
            }
        }
        return solution;
    }

    private int calculateHeuristic(LiquidSortSolver state) {
        int heuristic = 0;
        List<Tube> tubes = state.getTubes();
        for (Tube tube : tubes) {
            if (tube.isEmpty() || tube.isSorted()) {
                continue;
            }
            List<String> drops = tube.getDrops();
            String firstColor = drops.get(0);
            int mismatches = 0;
            for (String drop : drops) {
                if (!drop.equals(firstColor)) {
                    mismatches++;
                }
            }
            heuristic += mismatches;
        }
        
        return heuristic;
    }
    private List<Move> getAllValidMoves() {
        List<Move> moves = new ArrayList<>();

        for (int from = 0; from < numTubes; from++) {
            for (int to = 0; to < numTubes; to++) {
                if (from != to && isValidMove(from, to)) {
                    moves.add(new Move(from, to));
                }
            }
        }

        moves.sort((a, b) -> {
            int scoreA = evaluateMoveSimple(a.getFromTube(), a.getToTube());
            int scoreB = evaluateMoveSimple(b.getFromTube(), b.getToTube());
            return Integer.compare(scoreB, scoreA);
        });

        return moves;
    }

    private int evaluateMoveSimple(int from, int to) {
        Tube fromTube = tubes.get(from);
        Tube toTube = tubes.get(to);
        
        if (fromTube.isEmpty() || toTube.isFull()) {
            return Integer.MIN_VALUE;
        }
        
        int score = 0;

        boolean fromWasSorted = fromTube.isSorted();
        boolean toWasSorted = toTube.isSorted();
        boolean toWasEmpty = toTube.isEmpty();

        int topColorCount = fromTube.getTopColorCount();
        int freeSpace = toTube.getFreeSpace();
        int canPour = Math.min(topColorCount, freeSpace);
        LiquidSortSolver testSolver = this.copy();

        testSolver.makeMove(from, to);
        Tube newFromTube = testSolver.getTubes().get(from);
        Tube newToTube = testSolver.getTubes().get(to);

        boolean fromNowSorted = newFromTube.isSorted();
        boolean toNowSorted = newToTube.isSorted();

        if (toWasEmpty && fromNowSorted) {
            score += 10000;
        }
        if (!toWasSorted && toNowSorted) {
            score += 8000;
        }
        if (fromWasSorted && !fromNowSorted) {
            score -= 5000;
        }
        if (toWasSorted && !toNowSorted) {
            score -= 5000;
        }

        if (toTube.getTopColor() != null && toTube.getTopColor().equals(fromTube.getTopColor())) {
            int combinedCount = canPour + toTube.getTopColorCount();
            if (combinedCount == tubeCapacity) {
                score += 6000;
            } else {
                score += combinedCount * 50;
            }
        }
        if (toWasEmpty) {
            score += 200;
        }
        score += canPour * 20;
        return score;
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