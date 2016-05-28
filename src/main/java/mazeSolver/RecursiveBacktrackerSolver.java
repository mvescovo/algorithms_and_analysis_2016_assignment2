package mazeSolver;

import maze.Cell;
import maze.Maze;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Stack;

import static maze.Maze.HEX;

/**
 * Implements the recursive backtracking maze solving algorithm.
 *
 * @author michael vescovo
 */
public class RecursiveBacktrackerSolver implements MazeSolver {

    private Maze mMaze;
    private Random mRandGen = new Random(System.currentTimeMillis());
    private int mNumCellsVisited = 0;
    private boolean mExitReached = false;

    /**
     * Solve a perfect maze using the following recursive backtracker algorithm:
     *
     * ALGORITHM SMRB(M)
     * Traverse the maze from the starting cell until the exit cell has been reached.
     *
     * Input: Maze M, appropriate walls knocked down to form a perfect maze from start to exit.
     * Output: Maze M, a solution to the maze from start to exit.
     *
     * 1: Start at the entrance cell.
     *
     * 2: Pick a random unvisited neighbouring cell and move to that neighbour.
     *
     * 3: Continue this process until we reach a cell that has no unvisited neighbours.
     *    In that case, backtrack one cell at a time, until we've backtracked to a cell that has unvisited neighbours.
     *    Repeat step 2.
     *
     * 4: When there are no more unvisited neighbours for all cells,
     *    then every cell would have been visited and we would have found the exit.
     *
     * @param maze The maze to solve.
     */
    @Override
    public void solveMaze(Maze maze) {
        mMaze = maze;
        boolean normalVisited[][] = new boolean[maze.sizeR][maze.sizeC];
        HashSet<Cell> hexVisited = new HashSet<>();
        int numNormalCellsUnvisited = maze.sizeR * maze.sizeC;
        int numHexCellsUnvisited = 0;
        boolean thereAreUnvisitedNeighbors = true;
        int randomNeighbor;
        Stack<Cell> previousCell = new Stack<>();

        if (mMaze.type == Maze.NORMAL) {

            // (Step 1) Start at entrance
            Cell currentCell = maze.entrance;

            // Mark starting cell as visited
            normalVisited[currentCell.r][currentCell.c] = true;
            numNormalCellsUnvisited--;
            maze.drawFtPrt(currentCell);

            // If the maze started with a ridiculous 1 x 1 grid then catch this here
            if (numNormalCellsUnvisited == 0) {
                mNumCellsVisited = 1;
                mExitReached = true;
            }

            // (Step 4) Keep traversing the maze until there are no unvisited cells
            while (numNormalCellsUnvisited > 0) {

                // (Step 3) Keep doing step 2 until no more unvisited neighbours
                while (thereAreUnvisitedNeighbors) {

                    // (Step 2) List all unvisited neighbors
                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    for (int i = 0; i < Maze.NUM_DIR; i++) {
                        Cell currentNeighbor = currentCell.neigh[i];
                        if ((isIn(currentNeighbor)) && (!currentCell.wall[i].present)
                                && (!normalVisited[currentNeighbor.r][currentNeighbor.c])) {
                            unvisitedNeighbors.add(i);
                        }
                    }

                    // (Step 2) Randomly pick an unvisited neighbour
                    if (unvisitedNeighbors.size() > 0) {
                        randomNeighbor = unvisitedNeighbors.get(mRandGen.nextInt(unvisitedNeighbors.size()));

                        // (Step 2) Move to the random unvisited neighbor
                        previousCell.add(currentCell);
                        currentCell = currentCell.neigh[randomNeighbor];
                        maze.drawFtPrt(currentCell);

                        // Mark the new current cell as visited
                        normalVisited[currentCell.r][currentCell.c] = true;
                        numNormalCellsUnvisited--;

                        // Check if we are at the exit
                        if (currentCell == maze.exit) {

                            // Found the exit!
                            mExitReached = true;
                            mNumCellsVisited = (maze.sizeR * maze.sizeC) - numNormalCellsUnvisited;
                            isSolved();
                            return;
                        }
                    } else {
                        thereAreUnvisitedNeighbors = false;
                    }
                }

                // (Step 3) Backtrack to the previous cell
                if (previousCell.size() > 0) {
                    currentCell = previousCell.pop();
                }

                // (Step 3) Assume unvisited neighbors at the previous cell
                thereAreUnvisitedNeighbors = true;
            }

            // Exit not found but every cell was visited.
            isSolved();
        } else if (mMaze.type == Maze.HEX) {

            // List valid hex cells in the maze
            ArrayList<Cell> validCells = new ArrayList<>();
            for (int i = 0; i < maze.sizeR; i++) {
                for (int j = (i + 1) / 2; j < maze.sizeC + (i + 1) / 2; j++) {
                    if (!isIn(i, j)) {
                        System.out.println("cell " + i + ", " + j + " not in the maze");
                        continue;
                    }
                    validCells.add(mMaze.map[i][j]);
                }
            }

            System.out.println("valid cell count: " + validCells.size());

            // Set the number of cells still to visit
            numHexCellsUnvisited = validCells.size();

            // (Step 1) Start at entrance
            Cell currentCell = maze.entrance;

            // Mark starting cell as visited
            hexVisited.add(currentCell);
            numHexCellsUnvisited--;
            maze.drawFtPrt(currentCell);

            // If the maze started with a ridiculous 1 x 1 grid then catch this here
            if (numNormalCellsUnvisited == 0) {
                mNumCellsVisited = 1;
                mExitReached = true;
            }

            // (Step 4) Keep traversing the maze until there are no unvisited cells
            while (numHexCellsUnvisited > 0) {

                // (Step 3) Keep doing step 2 until no more unvisited neighbours
                while (thereAreUnvisitedNeighbors) {

                    // (Step 2) List all unvisited neighbors
                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    for (int i = 0; i < Maze.NUM_DIR; i++) {
                        Cell currentNeighbor = currentCell.neigh[i];
                        if ((isIn(currentNeighbor)) && (!currentCell.wall[i].present)
                                && (!hexVisited.contains(currentNeighbor))) {
                            unvisitedNeighbors.add(i);
                        }
                    }

                    // (Step 2) Randomly pick an unvisited neighbour
                    if (unvisitedNeighbors.size() > 0) {
                        randomNeighbor = unvisitedNeighbors.get(mRandGen.nextInt(unvisitedNeighbors.size()));

                        // (Step 2) Move to the random unvisited neighbor
                        previousCell.add(currentCell);
                        currentCell = currentCell.neigh[randomNeighbor];
                        maze.drawFtPrt(currentCell);

                        // Mark the new current cell as visited
                        hexVisited.add(currentCell);
                        numHexCellsUnvisited--;

                        // Check if we are at the exit
                        if (currentCell == maze.exit) {

                            // Found the exit!
                            mExitReached = true;
                            mNumCellsVisited = (maze.sizeR * maze.sizeC) - numHexCellsUnvisited;
                            isSolved();
                            return;
                        }
                    } else {
                        thereAreUnvisitedNeighbors = false;
                    }
                }

                // (Step 3) Backtrack to the previous cell
                if (previousCell.size() > 0) {
                    currentCell = previousCell.pop();
                }

                // (Step 3) Assume unvisited neighbors at the previous cell
                thereAreUnvisitedNeighbors = true;
            }

            // Exit not found but every cell was visited.
            isSolved();
        } else if (mMaze.type == Maze.TUNNEL) {

            // (Step 1) Start at entrance
            Cell currentCell = maze.entrance;

            // Mark starting cell as visited
            normalVisited[currentCell.r][currentCell.c] = true;
            numNormalCellsUnvisited--;
            maze.drawFtPrt(currentCell);

            // If the maze started with a ridiculous 1 x 1 grid then catch this here
            if (numNormalCellsUnvisited == 0) {
                mNumCellsVisited = 1;
                mExitReached = true;
            }

            // (Step 4) Keep traversing the maze until there are no unvisited cells
            while (numNormalCellsUnvisited > 0) {

                // (Step 3) Keep doing step 2 until no more unvisited neighbours
                while (thereAreUnvisitedNeighbors) {

                    // (Step 2) List all unvisited neighbors
                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    for (int i = 0; i < Maze.NUM_DIR; i++) {
                        Cell currentNeighbor = currentCell.neigh[i];
                        if ((isIn(currentNeighbor)) && (!currentCell.wall[i].present)
                                && (!normalVisited[currentNeighbor.r][currentNeighbor.c])) {
                            unvisitedNeighbors.add(i);
                        }
                    }

                    if ((currentCell.tunnelTo != null)
                            && (!normalVisited[currentCell.tunnelTo.r][currentCell.tunnelTo.c])) {

                        // Add an extra neighbor position for the tunnel neighbor
                        unvisitedNeighbors.add(6);
                    }

                    // (Step 2) Randomly pick an unvisited neighbour
                    if (unvisitedNeighbors.size() > 0) {
                        randomNeighbor = unvisitedNeighbors.get(mRandGen.nextInt(unvisitedNeighbors.size()));

                        // (Step 2) Move to the random unvisited neighbor
                        if (randomNeighbor != 6) {
                            previousCell.add(currentCell);
                            currentCell = currentCell.neigh[randomNeighbor];
                            maze.drawFtPrt(currentCell);
                        } else {
                            previousCell.add(currentCell);
                            currentCell = currentCell.tunnelTo;
                            maze.drawFtPrt(currentCell);
                        }

                        // Mark the new current cell as visited
                        normalVisited[currentCell.r][currentCell.c] = true;
                        numNormalCellsUnvisited--;

                        // Check if we are at the exit
                        if (currentCell == maze.exit) {

                            // Found the exit!
                            mExitReached = true;
                            mNumCellsVisited = (maze.sizeR * maze.sizeC) - numNormalCellsUnvisited;
                            isSolved();
                            return;
                        }
                    } else {
                        thereAreUnvisitedNeighbors = false;
                    }
                }

                // (Step 3) Backtrack to the previous cell
                if (previousCell.size() > 0) {
                    currentCell = previousCell.pop();
                }

                // (Step 3) Assume unvisited neighbors at the previous cell
                thereAreUnvisitedNeighbors = true;
            }

            // Exit not found but every cell was visited.
            isSolved();
        }
    } // end of solveMaze()

    @Override
    public boolean isSolved() {
        return mExitReached;
    } // end if isSolved()

    @Override
    public int cellsExplored() {
        return mNumCellsVisited;
    } // end of cellsExplored()

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

} // end of class RecursiveBackTrackerSolver
