package org.example.liquidsort;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String[][] initialState;
        if (args.length > 0 && args[0].equals("--example")) {
            initialState = getExampleState();
            printState(initialState);
        } else {
            initialState = readStateFromInput();
        }

        LiquidSortSolver solver = new LiquidSortSolver(initialState);
        List<Move> solution = solver.solve();
        if (solver.isSolved()) {
            System.out.println("\nРешение найдено за " + solution.size() + " ходов:");
            printSolution(solution);
        } else {
            System.out.println("\nРешение не найдено.");
            printCurrentState(solver);
        }
    }

    private static String[][] readStateFromInput() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Количество пробирок:");
        int n = scanner.nextInt();
        
        System.out.println("Объем пробирки:");
        int v = scanner.nextInt();
        
        System.out.println("Состояние пробирок (0 - пусто):");
        String[][] state = new String[n][v];
        
        scanner.nextLine();
        for (int i = 0; i < n; i++) {
            String line = scanner.nextLine();
            String[] values = line.trim().split("\\s+");
            for (int j = 0; j < v && j < values.length; j++) {
                state[i][j] = values[j];
            }
        }

        scanner.close();
        return state;
    }

    private static String[][] getExampleState() {
        return new String[][]{
            {"2", "10", "4", "4"},
            {"1", "8", "12", "8"},
            {"10", "7", "5", "9"},
            {"5", "3", "2", "5"},
            {"6", "11", "8", "7"},
            {"12", "12", "1", "2"},
            {"4", "7", "8", "11"},
            {"10", "11", "3", "1"},
            {"10", "7", "9", "9"},
            {"6", "2", "6", "11"},
            {"4", "6", "9", "3"},
            {"5", "3", "12", "1"},
            {"0", "0", "0", "0"},
            {"0", "0", "0", "0"}
        };
    }

    private static void printState(String[][] state) {
        for (int i = 0; i < state.length; i++) {
            System.out.print(i + ": [");
            for (int j = 0; j < state[i].length; j++) {
                if (state[i][j] == null || state[i][j].equals("0") || state[i][j].trim().isEmpty()) {
                    System.out.print(" ");
                } else {
                    System.out.print(state[i][j]);
                }
                if (j < state[i].length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }
    }

    private static void printSolution(List<Move> solution) {
        for (int i = 0; i < solution.size(); i++) {
            System.out.print(solution.get(i));
            if ((i + 1) % 8 == 0) {
                System.out.println();
            } else if (i < solution.size() - 1) {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    private static void printCurrentState(LiquidSortSolver solver) {
        List<Tube> tubes = solver.getTubes();
        for (int i = 0; i < tubes.size(); i++) {
            Tube tube = tubes.get(i);
            System.out.print(i + ": [");
            List<String> drops = tube.getDrops();
            for (int j = 0; j < drops.size(); j++) {
                System.out.print(drops.get(j));
                if (j < drops.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
        }
    }
}
