package mazeGenerator;

import maze.Cell;
import maze.Maze;

import java.util.ArrayList;
import java.util.Random;

/**
 * Generate maze with modified prim's algorithm
 *
 * @author michael vescovo
 */
public class ModifiedPrimsGenerator implements MazeGenerator {

    private Maze mMaze;
    private Random mRandGen = new Random(System.currentTimeMillis());

    /**
     * @param maze The reference of Maze object to generate.
     */
    @Override
    public void generateMaze(Maze maze) {
        mMaze = maze;
        int numCells = maze.sizeR * maze.sizeC;
        ArrayList<Cell> z = new ArrayList<>();
        ArrayList<Cell> f = new ArrayList<>();

        // Pick a random starting cell and call it the current cell
        int randomRow = mRandGen.nextInt(maze.sizeR);
        int randomCol = mRandGen.nextInt(maze.sizeC);
        Cell currentCell = maze.map[randomRow][randomCol];

        // Add the current cell to z
        z.add(currentCell);

        // Keep looping until all cells are in z; every cell has been visited
        while (z.size() < numCells) {

            // Put all neighboring cells of the current cell into the frontier set f
            for (int i = 0; i < Maze.NUM_DIR; i++) {
                Cell currentNeighbor = currentCell.neigh[i];
                if ((isIn(currentNeighbor)) && (!f.contains(currentNeighbor)) && (!z.contains(currentNeighbor))) {
                    f.add(currentNeighbor);
                }
            }

            // Randomly select a cell c from the frontier set and remove it from f
            Cell c = f.get(mRandGen.nextInt(f.size()));
            f.remove(c);

            // List all cells in z that are adjacent to the cell c
            ArrayList<Cell> adjacentCells = new ArrayList<>();
            for (int i = 0; i < z.size(); i++) {
                Cell cellToCheck = z.get(i);
                if (isAdjacent(cellToCheck, c)) {
                    adjacentCells.add(cellToCheck);
                }
            }

            // Randomly select a cell b from adjacent cells
            Cell b = adjacentCells.get(mRandGen.nextInt(adjacentCells.size()));

            // Carve a path between c and b
            for (int i = 0; i < Maze.NUM_DIR; i++) {
                Cell currentNeighbor = b.neigh[i];
                if ((isIn(currentNeighbor)) && (currentNeighbor == c)) {
                    b.wall[i].present = false;
                }
            }

            // Add cell c to z
            z.add(c);

            // Reset current cell to c
            currentCell = c;
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
    private boolean isIn(Cell cell) {
        return cell != null && isIn(cell.r, cell.c);
    }

    private boolean isAdjacent(Cell firstCell, Cell secondCell) {
        for (int i = 0; i < Maze.NUM_DIR; i++) {
            Cell currentNeighbor = firstCell.neigh[i];
            if (currentNeighbor == secondCell) {
                return true;
            }
        }
        return false;
    }

} // end of class ModifiedPrimsGenerator
