package mazeGenerator;

import maze.Cell;
import maze.Maze;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;

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
        boolean visited[][] = new boolean[maze.sizeR][maze.sizeC];
        int numCellsUnvisited = maze.sizeR * maze.sizeC;
        boolean thereAreUnvisitedNeighbors = true;
        int randomNeighbor;
        Stack<Cell> previousCell = new Stack<>();

        // Randomly pick a starting cell
        int randomRow = mRandGen.nextInt(maze.sizeR);
        int randomCol = mRandGen.nextInt(maze.sizeC);
        Cell currentCell = maze.map[randomRow][randomCol];

        // Mark starting cell as visited
        visited[currentCell.r][currentCell.c] = true;
        numCellsUnvisited--;

        // Visit every cell in the maze to ensure a perfect maze
        while (numCellsUnvisited > 0) {
            while (thereAreUnvisitedNeighbors) {
                // List all unvisited neighbors
                ArrayList<Integer> unvisitedNeighbors = new ArrayList<>();
                for (int i = 0; i < Maze.NUM_DIR; i++) {
                    Cell currentNeighbor = currentCell.neigh[i];
                    if ((isIn(currentNeighbor)) && (!visited[currentNeighbor.r][currentNeighbor.c])) {
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
                    visited[currentCell.r][currentCell.c] = true;
                    numCellsUnvisited--;
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
    } // end of generateMaze()

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
    protected boolean isIn(Cell cell) {
        if (cell == null)
            return false;
        return isIn(cell.r, cell.c);
    }

} // end of class RecursiveBacktrackerGenerator

// until we reach a cell with no unvisited neighbords
// Then back track