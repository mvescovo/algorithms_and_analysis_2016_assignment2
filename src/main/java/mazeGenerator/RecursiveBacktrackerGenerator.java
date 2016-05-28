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

    private Random mRandGen = new Random(System.currentTimeMillis());
    private Maze mMaze;
    private boolean mNormalVisited[][];
    private HashSet<Cell> mHexVisited;
    private Cell mCurrentCell;
    private ArrayList<Cell> mValidCells;

    /**
     * Generate a perfect maze inside the input maze object, using the following recursive backtracker algorithm:
     *
     * ALGORITHM GMRB(M)
     * Knock down walls in the starting maze to generate a perfect maze.
     *
     * Input: Maze M, all walls built up, start and exit points marked.
     * Output: Maze M, appropriate walls knocked down to form a perfect maze from start to exit.
     *
     * 1: Randomly pick a starting cell.
     *
     * 2: Pick a random unvisited neighbouring cell and move to that neighbour.
     *    In the process, carve a path between cells.
     *
     * 3: Continue this process until we reach a cell that has no unvisited neighbours.
     *    In that case, backtrack one cell at a time, until we've backtracked to a cell that has unvisited neighbours.
     *    Repeat step 2.
     *
     * 4: When there are no more unvisited neighbours for all cells,
     *    then every cell would have been visited and we have generated a perfect maze.
     *
     * @param maze The reference of Maze object to generate
     */
    @Override
    public void generateMaze(Maze maze) {
        mMaze = maze;
        mNormalVisited = new boolean[maze.sizeR][maze.sizeC];
        mHexVisited = new HashSet<>();
        int numCellsUnvisited;
        boolean thereAreUnvisitedNeighbors = true;
        int randomNeighbor;
        Stack<Cell> previousCell = new Stack<>();
        ArrayList<Cell> lockedCells = new ArrayList<>();

        // (Step 1) Randomly pick a starting cell
        selectStartingCell();

        if (maze.type == Maze.NORMAL) {
            numCellsUnvisited = maze.sizeR * maze.sizeC;

            // Mark starting cell as visited
            mNormalVisited[mCurrentCell.r][mCurrentCell.c] = true;
            numCellsUnvisited--;

            // (Step 4) Visit every cell in the maze to ensure a perfect maze
            while (numCellsUnvisited > 0) {

                // (Step 3) Keep doing step 2 until no more unvisited neighbours
                while (thereAreUnvisitedNeighbors) {

                    // (Step 2) List all unvisited neighbors
                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    for (int i = 0; i < NUM_DIR; i++) {
                        Cell currentNeighbor = mCurrentCell.neigh[i];
                        if ((isIn(currentNeighbor)) && (notVisited(currentNeighbor))) {
                            unvisitedNeighbors.add(i);
                        }
                    }

                    // (Step 2) Randomly pick an unvisited neighbour
                    if (unvisitedNeighbors.size() > 0) {
                        randomNeighbor = unvisitedNeighbors.get(mRandGen.nextInt(unvisitedNeighbors.size()));

                        // (Step 2) Carve a path and move to the random unvisited neighbor
                        mCurrentCell.wall[randomNeighbor].present = false;
                        previousCell.add(mCurrentCell);
                        mCurrentCell = mCurrentCell.neigh[randomNeighbor];

                        // Mark the new current cell as visited
                        mNormalVisited[mCurrentCell.r][mCurrentCell.c] = true;
                        numCellsUnvisited--;
                    } else {
                        thereAreUnvisitedNeighbors = false;
                    }
                }

                // (Step 3) Backtrack to the previous cell
                if (previousCell.size() > 0) {
                    mCurrentCell = previousCell.pop();
                }

                // (Step 3) Assume unvisited neighbors at the previous cell
                thereAreUnvisitedNeighbors = true;
            }
        } else if (maze.type == Maze.HEX) {

            // List valid hex cells in the maze
            mValidCells = new ArrayList<>();
            for (int i = 0; i < maze.sizeR; i++) {
                for (int j = (i + 1) / 2; j < maze.sizeC + (i + 1) / 2; j++) {
                    if (!isIn(i, j))
                        continue;
                    mValidCells.add(mMaze.map[i][j]);
                }
            }

            // Set the number of cells still to visit
            numCellsUnvisited = mValidCells.size();

            // Mark starting cell as visited
            mHexVisited.add(mCurrentCell);
            numCellsUnvisited--;

            // (Step 4) Visit every cell in the maze to ensure a perfect maze
            while (numCellsUnvisited > 0) {

                // (Step 3) Keep doing step 2 until no more unvisited neighbours
                while (thereAreUnvisitedNeighbors) {

                    // (Step 2) List all unvisited neighbors
                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    for (int i = 0; i < Maze.NUM_DIR; i++) {
                        Cell currentNeighbor = mCurrentCell.neigh[i];
                        if ((isIn(currentNeighbor)) && (notVisited(currentNeighbor))) {
                            unvisitedNeighbors.add(i);
                        }
                    }

                    // (Step 2) Randomly pick an unvisited neighbour
                    if (unvisitedNeighbors.size() > 0) {
                        randomNeighbor = unvisitedNeighbors.get(mRandGen.nextInt(unvisitedNeighbors.size()));

                        // (Step 2) Carve a path and move to the random unvisited neighbor
                        mCurrentCell.wall[randomNeighbor].present = false;
                        previousCell.add(mCurrentCell);
                        mCurrentCell = mCurrentCell.neigh[randomNeighbor];

                        // Mark the new current cell as visited
                        mHexVisited.add(mCurrentCell);
                        numCellsUnvisited--;
                    } else {
                        thereAreUnvisitedNeighbors = false;
                    }
                }

                // (Step 3) Backtrack to the previous cell
                if (previousCell.size() > 0) {
                    mCurrentCell = previousCell.pop();
                }

                // (Step 3) Assume unvisited neighbors at the previous cell
                thereAreUnvisitedNeighbors = true;
            }
        } else if (maze.type == Maze.TUNNEL) {
            numCellsUnvisited = maze.sizeR * maze.sizeC;

            // Mark starting cell as visited
            mNormalVisited[mCurrentCell.r][mCurrentCell.c] = true;
            numCellsUnvisited--;

            // (Step 4) Visit every cell in the maze to ensure a perfect maze
            while (numCellsUnvisited > 0) {

                // (Step 3) Keep doing step 2 until no more unvisited neighbours
                while (thereAreUnvisitedNeighbors) {

                    // (Step 2) List all unvisited neighbors
                    ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                    for (int i = 0; i < NUM_DIR; i++) {
                        Cell currentNeighbor = mCurrentCell.neigh[i];
                        if ((isIn(currentNeighbor)) && (notVisited(currentNeighbor))
                                && (!lockedCells.contains(currentNeighbor))) {
                            unvisitedNeighbors.add(i);
                        }
                    }

                    if ((mCurrentCell.tunnelTo != null)
                            && (!mNormalVisited[mCurrentCell.tunnelTo.r][mCurrentCell.tunnelTo.c])) {

                        // (Step 2) Add an extra neighbor position for the tunnel neighbor
                        unvisitedNeighbors.add(6);
                    }

                    // (Step 2) Randomly pick an unvisited neighbour
                    if (unvisitedNeighbors.size() > 0) {
                        int randomNeighborIndex = mRandGen.nextInt(unvisitedNeighbors.size());
                        randomNeighbor = unvisitedNeighbors.get(randomNeighborIndex);

                        // (Step 2) Carve a path and move to the random unvisited neighbor
                        if (randomNeighbor != 6) {

                            // Don't go through the tunnel if there is one

                            // Lock the other end of the tunnel if there is one
                            if (mCurrentCell.tunnelTo != null) {
                                lockedCells.add(mCurrentCell.tunnelTo);
                            }

                            // Carve path and move
                            mCurrentCell.wall[randomNeighbor].present = false;
                            previousCell.add(mCurrentCell);
                            mCurrentCell = mCurrentCell.neigh[randomNeighbor];
                        } else {

                            // Go through the tunnel, no need to carve a path
                            previousCell.add(mCurrentCell);
                            mCurrentCell = mCurrentCell.tunnelTo;
                        }

                        // Mark the new current cell as visited
                        mNormalVisited[mCurrentCell.r][mCurrentCell.c] = true;
                        numCellsUnvisited--;
                    } else {
                        thereAreUnvisitedNeighbors = false;
                    }
                }

                // (Step 3) Backtrack to the previous cell
                if (previousCell.size() > 0) {
                    mCurrentCell = previousCell.pop();
                }

                // (Step 3) Assume unvisited neighbors at the previous cell
                thereAreUnvisitedNeighbors = true;
            }
        }
    } // end of generateMaze()

    /**
     * Check if a cell has not been visited
     *
     * @param cell the cell
     */
    private boolean notVisited(Cell cell) {
        if (mMaze.type == HEX) {
            return !mHexVisited.contains(cell);
        } else {
            return !mNormalVisited[cell.r][cell.c];
        }
    }

    private void selectStartingCell() {
        if (mMaze.type == HEX) {
            mCurrentCell = mValidCells.get(mRandGen.nextInt(mValidCells.size()));
        } else {
            int randomRow = mRandGen.nextInt(mMaze.sizeR);
            int randomCol = mRandGen.nextInt(mMaze.sizeC);
            mCurrentCell = mMaze.map[randomRow][randomCol];
        }
    }

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