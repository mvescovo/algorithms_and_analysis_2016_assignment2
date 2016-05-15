package mazeSolver;

import maze.Cell;
import maze.Maze;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

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

    @Override
	public void solveMaze(Maze maze) {
        mMaze = maze;
        boolean visited[][] = new boolean[maze.sizeR][maze.sizeC];
        int numCellsUnvisited = maze.sizeR * maze.sizeC;
        boolean thereAreUnvisitedNeighbors = true;
        int randomNeighbor;
        Stack<Cell> previousCell = new Stack<>();

        // Start at entrance
        Cell currentCell = maze.entrance;

        // Mark starting cell as visited
        visited[currentCell.r][currentCell.c] = true;
        numCellsUnvisited--;
        maze.drawFtPrt(currentCell);

        // Keep traversing the maze until there are no unvisited cells
        while (numCellsUnvisited > 0) {
            while (thereAreUnvisitedNeighbors) {

                // List all unvisited neighbors
                ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                for (int i = 0; i < Maze.NUM_DIR; i++) {
                    Cell currentNeighbor = currentCell.neigh[i];
                    if ((isIn(currentNeighbor)) && (!currentCell.wall[i].present)
                            && (!visited[currentNeighbor.r][currentNeighbor.c])) {
                        unvisitedNeighbors.add(i);
                    }
                }

                // Randomly pick an unvisited neighbour
                if (unvisitedNeighbors.size() > 0) {
                    randomNeighbor = unvisitedNeighbors.get(mRandGen.nextInt(unvisitedNeighbors.size()));

                    // Move to the random unvisited neighbor
                    previousCell.add(currentCell);
                    currentCell = currentCell.neigh[randomNeighbor];
                    maze.drawFtPrt(currentCell);

                    // Mark the new current cell as visited
                    visited[currentCell.r][currentCell.c] = true;
                    numCellsUnvisited--;

                    // Check if we are at the exit
                    if (currentCell == maze.exit) {

                        // Found the exit!
                        mExitReached = true;
                        mNumCellsVisited = (maze.sizeR * maze.sizeC) - numCellsUnvisited;
                        isSolved();
                        return;
                    }
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

        // Exit not found but every cell was visited.
        isSolved();
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
        return row >= 0 && row < mMaze.sizeR && column >= 0 && column < mMaze.sizeC;
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
