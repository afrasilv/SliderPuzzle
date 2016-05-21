package com.afrasilv.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

/**
 * Created by alex on 21/05/16.
 */
public class Solver {

    // Tiles for successfully completed puzzle.
    static Object [] goalTiles;
    static Object [] initial;
    static int maxRow;

    // A* priority queue.
    static final PriorityQueue<State> queue = new PriorityQueue<>(300, new Comparator<State>() {
        @Override
        public int compare(State a, State b) {
            return a.priority() - b.priority();
        }
    });

    static final HashSet<State> closed = new HashSet <>();

    // State of the puzzle including its priority and chain to start state.
    static class State {
        final Object [] tiles;    // Tiles left to right, top to bottom.
        final int spaceIndex;   // Index of space (zero) in tiles
        final int g;            // Number of moves from start.
        final int h;            // Heuristic value (difference from goal)
        final State prev;       // Previous state in solution chain.
        ArrayList<String> solMovs;


        // A* priority function (often called F in books).
        int priority() {
            return g + h;
        }

        // Build a start state.
        State(Object [] initial) {
            tiles = initial;
            spaceIndex = index(tiles, 0);
            g = 0;
            h = heuristic(tiles);
            prev = null;
            solMovs = new ArrayList<>();
        }

        // Build a successor to prev by sliding tile from given index.
        State(State prev, int slideFromIndex, String move) {
            tiles = Arrays.copyOf(prev.tiles, prev.tiles.length);
            tiles[prev.spaceIndex] = tiles[slideFromIndex];
            tiles[slideFromIndex] = 0;
            spaceIndex = slideFromIndex;
            g = prev.g + 1;
            h = heuristic(tiles);
            if(solMovs == null)
                solMovs = new ArrayList<>();
            solMovs.add(move);
            this.prev = prev;
        }

        // Return true iif this is the goal state.
        boolean isGoal() {
            return Arrays.equals(tiles, goalTiles);
        }
        // Successor states due to south, north, west, and east moves.
        State moveS() {
            if(spaceIndex > (maxRow-1)) {
                return new State(this, spaceIndex - maxRow, "N");
            }
            return null;
        }
        State moveN() {
            if(spaceIndex < (maxRow*(maxRow-1))) {
                return new State(this, spaceIndex + maxRow, "S");
            }
            return null;
        }
        State moveE() {
            if(spaceIndex % maxRow > 0) {
                return new State(this, spaceIndex - 1, "W");
            }
            return null;
        }
        State moveW() {
            if(spaceIndex % maxRow < (maxRow-1)) {
                return new State(this, spaceIndex + 1, "E");
            }
            return null;
        }

        // Print the solution chain with start state first.
        ArrayList<String> printAll() {

            if (prev != null) solMovs.addAll(0, prev.printAll());

            return solMovs;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof State) {
                State other = (State)obj;
                return Arrays.equals(tiles, other.tiles);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Arrays.hashCode(tiles);
        }
    }

    // Add a valid (non-null and not closed) successor to the A* queue.
    static void addSuccessor(State successor) {
        if (successor != null && !closed.contains(successor))
            queue.add(successor);
    }

    // Run the solver.
   public static ArrayList<String> solve(Object[] itemList, Object[] goalArray, int maxRowParam) {

       goalTiles = goalArray;
       initial = itemList;
       maxRow = maxRowParam;

        queue.clear();
        closed.clear();

        // Add initial state to queue.
        queue.add(new State(initial));

        while (!queue.isEmpty()) {

            // Get the lowest priority state.
            State state = queue.poll();

            // If it's the goal, we're done.
            if (state.isGoal()) {
                return state.printAll();
            }

            // Make sure we don't revisit this state.
            closed.add(state);

            // Add successors to the queue.
            addSuccessor(state.moveS());
            addSuccessor(state.moveN());
            addSuccessor(state.moveW());
            addSuccessor(state.moveE());
        }

       return null;
    }

    // Return the index of val in given byte array or -1 if none found.
    static int index(Object [] a, int val) {
        for (int i = 0; i < a.length; i++)
            if ((int) a[i] == val) return i;
        return -1;
    }

    // Return the Manhatten distance between tiles with indices a and b.
    static int manhattanDistance(int a, int b) {
        return Math.abs(a / maxRow - b / maxRow) + Math.abs(a % maxRow - b % maxRow);
    }

    // For our A* heuristic, we just use max of Manhatten distances of all tiles.
    static int heuristic(Object [] tiles) {
        int h = 0;
        for (int i = 0; i < tiles.length; i++)
            if ((int) tiles[i] != 0)
                h = Math.max(h, manhattanDistance(i, (int) tiles[i]));
        return h;
    }
}
