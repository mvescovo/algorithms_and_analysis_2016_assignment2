package mazeGenerator;

import maze.Cell;
import maze.Maze;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;

import static maze.Maze.HEX;
import static maze.Maze.NUM_DIR;

/**
 * Generate maze with recursive back tracker algorithm
 *
 * @author michael vescovo
 */
public class RecursiveBacktrackerGenerator implements MazeGenerator {

    private Maze mMaze;
    private Random mRandGen = new Random(System.currentTimeMillis());

    /**
     * @param maze The reference of Maze object to generate
     */
    @Override
    public void generateMaze(Maze maze) {
        mMaze = maze;
        boolean normalVisited[][] = new boolean[maze.sizeR][maze.sizeC];
        HashSet<Cell> hexVisited = new HashSet<>();
        int numNormalCellsUnvisited = maze.sizeR * maze.sizeC;
        int numHexCellsUnvisited;
        boolean thereAreUnvisitedNeighbors = true;
        int randomNeighbor;
        Stack<Cell> previousCell = new Stack<>();
        ArrayList<Cell> lockedCells = new ArrayList<>();

        if (maze.type == Maze.NORMAL) {

            // Randomly pick a starting cell
            int randomRow = mRandGen.nextInt(maze.sizeR);
            int randomCol = mRandGen.nextInt(maze.sizeC);
            Cell currentCell = maze.map[randomRow][randomCol];

            // Mark starting cell as visited
            normalVisited[currentCell.r][currentCell.c] = true;
            numNormalCellsUnvisited--;

            // Visit every cell in the maze to ensure a perfect maze
            while (numNormalCellsUnvisited > 0) {
                while (thereAreUnvisitedNeighbors) {

                    // List all unvisited neighbors
                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    for (int i = 0; i < NUM_DIR; i++) {
                        Cell currentNeighbor = currentCell.neigh[i];
                        if ((isIn(currentNeighbor)) && (!normalVisited[currentNeighbor.r][currentNeighbor.c])) {
                            unvisitedNeighbors.add(i);
                        }
                    }

                    // Randomly pick an unvisited neighbour
                    if (unvisitedNeighbors.size() > 0) {
                        randomNeighbor = unvisitedNeighbors.get(mRandGen.nextInt(unvisitedNeighbors.size()));

                        // Carve a path and move to the random unvisited neighbor
                        currentCell.wall[randomNeighbor].present = false;
                        previousCell.add(currentCell);
                        currentCell = currentCell.neigh[randomNeighbor];

                        // Mark the new current cell as visited
                        normalVisited[currentCell.r][currentCell.c] = true;
                        numNormalCellsUnvisited--;
                    } else {
                        thereAreUnvisitedNeighbors = false;
                    }
                }

                // Backtrack to the previous cell
                if (previousCell.size() > 0) {
                    currentCell = previousCell.pop();
                }

                // Assume unvisited neighbors at the previous cell
                thereAreUnvisitedNeighbors = true;
            }
        } else if (maze.type == Maze.HEX) {

            // List valid hex cells in the maze
            ArrayList<Cell> validCells = new ArrayList<>();
            for (int i = 0; i < maze.sizeR; i++) {
                for (int j = (i + 1) / 2; j < maze.sizeC + (i + 1) / 2; j++) {
                    if (!isIn(i, j))
                        continue;
                    validCells.add(mMaze.map[i][j]);
                }
            }

            // Set the number of cells still to visit
            numHexCellsUnvisited = validCells.size();

            // Randomly pick a starting cell
            Cell currentCell = validCells.get(mRandGen.nextInt(validCells.size()));

            // Mark starting cell as visited
            hexVisited.add(currentCell);
            numHexCellsUnvisited--;

            // Visit every cell in the maze to ensure a perfect maze
            while (numHexCellsUnvisited > 0) {
                while (thereAreUnvisitedNeighbors) {

                    // List all unvisited neighbors
                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    for (int i = 0; i < Maze.NUM_DIR; i++) {
                        Cell currentNeighbor = currentCell.neigh[i];
                        if ((isIn(currentNeighbor)) && (!hexVisited.contains(currentNeighbor))) {
                            unvisitedNeighbors.add(i);
                        }
                    }

                    // Randomly pick an unvisited neighbour
                    if (unvisitedNeighbors.size() > 0) {
                        randomNeighbor = unvisitedNeighbors.get(mRandGen.nextInt(unvisitedNeighbors.size()));

                        // Carve a path and move to the random unvisited neighbor
                        currentCell.wall[randomNeighbor].present = false;
                        previousCell.add(currentCell);
                        currentCell = currentCell.neigh[randomNeighbor];

                        // Mark the new current cell as visited
                        hexVisited.add(currentCell);
                        numHexCellsUnvisited--;
                    } else {
                        thereAreUnvisitedNeighbors = false;
                    }
                }

                // Backtrack to the previous cell
                if (previousCell.size() > 0) {
                    currentCell = previousCell.pop();
                }

                // Assume unvisited neighbors at the previous cell
                thereAreUnvisitedNeighbors = true;
            }
        } else if (maze.type == Maze.TUNNEL) {

            // Randomly pick a starting cell
            int randomRow = mRandGen.nextInt(maze.sizeR);
            int randomCol = mRandGen.nextInt(maze.sizeC);
            Cell currentCell = maze.map[randomRow][randomCol];

            // Mark starting cell as visited
            normalVisited[currentCell.r][currentCell.c] = true;
            numNormalCellsUnvisited--;

            // Visit every cell in the maze to ensure a perfect maze
            while (numNormalCellsUnvisited > 0) {
                while (thereAreUnvisitedNeighbors) {

                    // List all unvisited neighbors
                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    for (int i = 0; i < NUM_DIR; i++) {
                        Cell currentNeighbor = currentCell.neigh[i];
                        if ((isIn(currentNeighbor)) && (!normalVisited[currentNeighbor.r][currentNeighbor.c])
                                && (!lockedCells.contains(currentNeighbor))) {
                            unvisitedNeighbors.add(i);
                        }
                    }

                    if ((currentCell.tunnelTo != null)
                            && (!normalVisited[currentCell.tunnelTo.r][currentCell.tunnelTo.c])) {

                        // Add an extra neighbor position for the tunnel neighbor
                        unvisitedNeighbors.add(6);
                    }

                    // Randomly pick an unvisited neighbour
                    if (unvisitedNeighbors.size() > 0) {
                        int randomNeighborIndex = mRandGen.nextInt(unvisitedNeighbors.size());
                        randomNeighbor = unvisitedNeighbors.get(randomNeighborIndex);

                        // Carve a path and move to the random unvisited neighbor
                        if (randomNeighbor != 6) {

                            // Don't go through the tunnel if there is one

                            // Lock the other end of the tunnel if there is one
                            if (currentCell.tunnelTo != null) {
                                lockedCells.add(currentCell.tunnelTo);
                            }

                            // Carve path and move
                            currentCell.wall[randomNeighbor].present = false;
                            previousCell.add(currentCell);
                            currentCell = currentCell.neigh[randomNeighbor];
                        } else {

                            // Go through the tunnel, no need to carve a path
                            previousCell.add(currentCell);
                            currentCell = currentCell.tunnelTo;
                        }

                        // Mark the new current cell as visited
                        normalVisited[currentCell.r][currentCell.c] = true;
                        numNormalCellsUnvisited--;
                    } else {
                        thereAreUnvisitedNeighbors = false;
                    }
                }

                // Backtrack to the previous cell
                if (previousCell.size() > 0) {
                    currentCell = previousCell.pop();
                }

                // Assume unvisited neighbors at the previous cell
                thereAreUnvisitedNeighbors = true;
            }
        }
    } // end of generateMaze()

    /**
     * Check if a cell is in the maze
     *
     * @param row the row of the cell to check
     * @param column the column of the cell to check
     * @return weather the cell is in the maze
     */
    private boolean isIn(int row, int column) {
        if (mMaze.type == HEX) {
            return row >= 0 && row < mMaze.sizeR && column >= (row + 1) / 2 && column < mMaze.sizeC + (row + 1) / 2;
        } else {
            return row >= 0 && row < mMaze.sizeR && column >= 0 && column < mMaze.sizeC;
        }
    }

    /**
     * Check whether the cell is in the maze.
     *
     * @param cell The cell being checked.
     * @return True if in the maze. Otherwise false.
     */
    private boolean isIn(Cell cell) {
        return cell != null && isIn(cell.r, cell.c);
    }
} // end of class RecursiveBacktrackerGenerator

// until we reach a cell with no unvisited neighbords
// Then back track