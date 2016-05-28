package mazeGenerator;

import maze.Cell;
import maze.Maze;

import java.util.ArrayList;
import java.util.Random;

import static maze.Maze.HEX;

/**
 * Generate maze with modified prim's algorithm
 *
 * @author michael vescovo
 */
public class ModifiedPrimsGenerator implements MazeGenerator {

    private Maze mMaze;
    private ArrayList<Cell> mAdjacentCells = new ArrayList<>();

    /**
     * Generate a perfect maze inside the input maze object, using the following modified prim's algorithm:
     *
     * ALGORITHM GMMP(M)
     * Knock down walls in the starting maze to generate a perfect maze.
     *
     * Input: Maze M, all walls built up, start and exit points marked.
     * Output: Maze M, appropriate walls knocked down to form a perfect maze from start to exit.
     *
     * 1: Pick a random starting cell and add it to set Z.
     *    Put all neighbouring cells of starting cell into the frontier set F.
     * 2: Randomly select a cell c from the frontier set and remove it from F.
     *    Randomly select a cell b that is in Z and adjacent to the cell c.
     *    Carve a path between c and b.
     * 3: Add cell c to the set Z.
     * 4: Repeat step 2 until Z includes every cell in the maze.
     *
     * @param maze The reference of Maze object to generate.
     */
    @Override
    public void generateMaze(Maze maze) {
        Random randGen = new Random(System.currentTimeMillis());
        mMaze = maze;
        int numCells = 0;
        ArrayList<Cell> z = new ArrayList<>();
        ArrayList<Cell> f = new ArrayList<>();
        Cell currentCell = null;

        if (maze.type == Maze.NORMAL) {

            // Get the number of cells in a normal maze
            numCells = maze.sizeR * maze.sizeC;

            // (Step 1) Pick a random starting cell and call it the current cell
            int randomRow = randGen.nextInt(maze.sizeR);
            int randomCol = randGen.nextInt(maze.sizeC);
            currentCell = maze.map[randomRow][randomCol];
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

            // Get the number of cells in a hex maze
            numCells = validCells.size();

            // (Step 1) Pick a random starting cell and call it the current cell
            currentCell = validCells.get(randGen.nextInt(validCells.size()));
        }

        // (Step 1) Add the current cell to z
        z.add(currentCell);

        // (Step 4) Keep looping until all cells are in z; every cell has been visited
        while (z.size() < numCells) {

            // (Step 1) Put all neighboring cells of the current cell into the frontier set f
            addToF(currentCell, z, f);

            // (Step 2) Randomly select a cell c from the frontier set and remove it from f
            Cell c = f.get(randGen.nextInt(f.size()));
            f.remove(c);

            // (Step 2) List all cells in z that are adjacent to the cell c
            mAdjacentCells.clear();
            listCellsInZAdjacentToC(z, c);

            // (Step 2) Randomly select a cell b from adjacent cells
            Cell b = mAdjacentCells.get(randGen.nextInt(mAdjacentCells.size()));

            // (Step 2) Carve a path between c and b
            carvePath(c, b);

            // (Step 3) Add cell c to z
            z.add(c);

            // (Step 4) Reset current cell to c
            currentCell = c;
        }
    } // end of generateMaze()

    /**
     * Add the neighbors of the current cell to the frontier set F if they are not already in the set F or Z
     *
     * @param currentCell the current cell
     * @param z the set Z
     * @param f the frontier set F
     */
    private void addToF(Cell currentCell, ArrayList<Cell> z, ArrayList<Cell> f) {
        for (int i = 0; i < Maze.NUM_DIR; i++) {
            Cell currentNeighbor = currentCell.neigh[i];
            if ((isIn(currentNeighbor)) && (!f.contains(currentNeighbor)) && (!z.contains(currentNeighbor))) {
                f.add(currentNeighbor);
            }
        }
    }

    /**
     * List all the cells in z that are adjacent to the cell c
     *
     * @param z the set Z
     * @param c the cell c
     */
    private void listCellsInZAdjacentToC(ArrayList<Cell> z, Cell c) {
        for (int i = 0; i < z.size(); i++) {
            Cell cellToCheck = z.get(i);
            if (isAdjacent(cellToCheck, c)) {
                mAdjacentCells.add(cellToCheck);
            }
        }
    }

    /**
     * Carve a path between cells c and b
     *
     * @param c the cell c
     * @param b the cell b
     */
    private void carvePath(Cell c, Cell b) {
        for (int i = 0; i < Maze.NUM_DIR; i++) {
            Cell currentNeighbor = b.neigh[i];
            if ((isIn(currentNeighbor)) && (currentNeighbor == c)) {
                b.wall[i].present = false;
            }
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
