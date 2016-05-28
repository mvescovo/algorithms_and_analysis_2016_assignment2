package mazeSolver;

import maze.Cell;
import maze.Maze;

import java.util.HashSet;
import java.util.LinkedList;

import static maze.Maze.HEX;

/**
 * Implements Bi-directional BFS maze solving algorithm.
 * 
 * @author larvinloy
 *
 */
public class BiDirectionalBFSSolver implements MazeSolver 
{
	private boolean meet = false;
	private Maze mMaze;
	private HashSet<Cell> entryVisitedCells;
	private HashSet<Cell> exitVisitedCells;
	private int cellVisited = 0;
	
	/**
     * Check if a cell is in the maze
     *
     * @param row the row of the cell to check
     * @param column the column of the cell to check
     * @return weather the cell is in the maze
     */
	private boolean isIn(int row, int column) 
	{
		if (mMaze.type == HEX) 
		{
			return row >= 0 && row < mMaze.sizeR && column >= (row + 1) / 2 && column < mMaze.sizeC + (row + 1) / 2;
		} 
		else 
		{
			return row >= 0 && row < mMaze.sizeR && column >= 0 && column < mMaze.sizeC;
	    }
	}
	
	/**
     * Check whether the cell is in the maze.
     *
     * @param cell The cell being checked.
     * @return True if in the maze. Otherwise false.
     */
	private boolean isIn(Cell cell) 
	{
        return cell != null && isIn(cell.r, cell.c);
	}
	
	/**
	 * ALGORITHM BIDIRECTIONALBFS(M)
	 * 
	 * Input: Maze M, appropriate walls knocked down to form a perfect maze from start to exit.
     * Output: Maze M, a solution to the maze from start to exit.
     * 
     * 1. Create two queues. One for BFS starting from the entrance of the maze, the other for
     * 		BFS strating from the exit of the maze.
     * 2. Add the entrance of the maze to the entry queue and the exit to the exit queue
     * 3. Start loop: 
     * 	  Remove the first element from the entry queue and add mark it as visited
     * 		3.1 For every neighbor(tunnel included) of this cell, check if the cell is in exit queue or was visited
     * 			by exit BFS
     * 		3.2 If the above is true, mark the neighbor and add it to the list of cells visited by 
     * 			entry BFS and exit loop. The maze has been solved
     * 		3.3 If 3.1 turn out as false, add the neighbor to the entry queue
     * 4. Remove the first element from the entry queue and add mark it as visited
     * 		4.1 For every neighbor(tunnel included) of this cell, check if the cell is in exit queue or was visited
     * 			by exit BFS
     * 		4.2 If the above is true, mark the neighbor and add it to the list of cells visited by 
     * 			entry BFS and exit loop. The maze has been solved
     * 		4.3 If 4.1 turn out as false, add the neighbor to the entry queue
     * 5. Repeat step 3 and 4 until both ends meet and the loop is exited
     * 6. A path from entrance to exit has been found
     * 
	 */
	@Override
	public void solveMaze(Maze maze) 
	{
		this.mMaze = maze;
		//Queue for entry side BFS
		LinkedList<Cell> entryQueue = new LinkedList<Cell>();
		//Queue for exit side BFS
		LinkedList<Cell> exitQueue = new LinkedList<Cell>();
		//List of cells visited by BFS from entry side
		entryVisitedCells = new HashSet<Cell>();
		//List of cells visited by BFS from exit side
		exitVisitedCells = new HashSet<Cell>();
		
		//Add the entrance cell to the entry queue
		entryQueue.add(maze.entrance);
		//Add the exit cell to the exit queue
		exitQueue.add(maze.exit);
		
		//Boolean variable to check if both ends of BFS have met
		meet = false;
		
		//Current cell being inspected in the entry queue
		Cell entryCurrent;
		//Current cell being inspected in the exit queue
		Cell exitCurrent;
		
		/*
		 * If the entrance and exit are the same cell then the maze is already solved.
		 * Mark the entrance and exit
		 */
		if(maze.entrance.equals(maze.exit))
		{
			meet = true;
			entryVisitedCells.add(maze.entrance);
		}
		
		while(!meet)
		{
			//ENTRY BFS
			//Get the first cell from entry queue
			entryCurrent = entryQueue.removeFirst();
			//Draw it as visited
			maze.drawFtPrt(entryCurrent);
			//Add it to the entry visited cells
			entryVisitedCells.add(entryCurrent);
			//Check if the cell has a tunnel
			if(entryCurrent.tunnelTo != null)
			{
				/*
				 * Check if the tunnel end is visited by the exit side BFS
				 * If it has, then both ends have met, draw the other end of tunnel
				 * Otherwise add the other end of the tunnel to the queue
				 */
				Cell tunnelNeighbor = entryCurrent.tunnelTo;
				
				if((exitQueue.contains(tunnelNeighbor)) || exitVisitedCells.contains(tunnelNeighbor))
                {
					if(!exitVisitedCells.contains(tunnelNeighbor))
					{
						maze.drawFtPrt(tunnelNeighbor);
						entryVisitedCells.add(tunnelNeighbor);
					}
						
                	meet = true;
                	break;
                }
				else if ((isIn(tunnelNeighbor)) && (!entryQueue.contains(tunnelNeighbor)) 
                		&& (!entryVisitedCells.contains(tunnelNeighbor))) 
                {
                    entryQueue.add(tunnelNeighbor);
                }
			}
			
			//Add all the accessible neighbor cells to the queue
			for (int i = 0; i < Maze.NUM_DIR; i++) 
			{
                Cell currentNeighbor = entryCurrent.neigh[i];
                /*
                 * If exit queue contains the neighbor cell then both ends have met
                 * Draw the cell and exit	
                */
                if((exitQueue.contains(currentNeighbor)) && (!entryCurrent.wall[i].present))
                {
                	if(!exitVisitedCells.contains(currentNeighbor))
                	{
                		entryVisitedCells.add(currentNeighbor);
                    	maze.drawFtPrt(currentNeighbor);
                	}	
                	meet = true;
                	break;
                }
                /*
                 * If the neighbor cell is in the visited cells from exit side,
                 * Then both ends have met, exit
                 */
                else if((exitVisitedCells.contains(currentNeighbor)) && (!entryCurrent.wall[i].present))
                {
                	meet = true;
                	break;
                }
                /*
                 * Otherwise add the neighbor cell to the entry queue
                 */
                else if ((isIn(currentNeighbor)) && (!entryQueue.contains(currentNeighbor)) 
                		&& (!entryCurrent.wall[i].present) && (!entryVisitedCells.contains(currentNeighbor))) 
                {
                    entryQueue.add(currentNeighbor);
                }
            }
			//Exit loop if a path has been found
			if(meet == true)
				break;
			
			//EXIT BFS
			//Get the first cell from exit queue
			exitCurrent = exitQueue.removeFirst();
			//Draw it as visited
			maze.drawFtPrt(exitCurrent);
			//Add it to the exit visited cells
			exitVisitedCells.add(exitCurrent);
			//Check if the cell has a tunnel
			if(exitCurrent.tunnelTo != null)
			{
				/*
				 * Check if the tunnel end is visited by the entry side BFS
				 * If it has, then both ends have met, draw the other end of tunnel
				 * Otherwise add the other end of the tunnel to the queue
				 */
				Cell tunnelNeighbor = exitCurrent.tunnelTo;
				if((entryQueue.contains(tunnelNeighbor)) || entryVisitedCells.contains(tunnelNeighbor))
                {
					if(!entryVisitedCells.contains(tunnelNeighbor))
					{
						maze.drawFtPrt(tunnelNeighbor);
						exitVisitedCells.add(tunnelNeighbor);
					}		
                	meet = true;
                	break;
                }
				else if ((!exitQueue.contains(tunnelNeighbor)) 
                		&& (!exitVisitedCells.contains(tunnelNeighbor))) 
                {
                    exitQueue.add(tunnelNeighbor);
                }
			}
			//Add all the accessible neighbor cells to the queue
			for (int i = 0; i < Maze.NUM_DIR; i++) 
			{
				/*
                 * If entry queue contains the neighbor cell then both ends have met
                 * Draw the cell and exit	
                */
                Cell currentNeighbor = exitCurrent.neigh[i];
                if((entryQueue.contains(currentNeighbor)) && (!exitCurrent.wall[i].present))
                {
                	if(!entryVisitedCells.contains(currentNeighbor))
                	{
                		exitVisitedCells.add(currentNeighbor);
                    	maze.drawFtPrt(currentNeighbor);
                	}	
                	meet = true;
                	break;
                }
                /*
                 * If the neighbor cell is in the visited cells from entry side,
                 * Then both ends have met, exit
                 */
                else if((entryVisitedCells.contains(currentNeighbor)) && (!exitCurrent.wall[i].present))
                {
                	meet = true;
                	break;
                }
                /*
                 * Otherwise add the neighbor cell to the exit queue
                 */
                else if ((isIn(currentNeighbor)) && (!exitQueue.contains(currentNeighbor))
                		&& (!exitCurrent.wall[i].present) && (!exitVisitedCells.contains(currentNeighbor))) 
                {
                    exitQueue.add(currentNeighbor);
                }
            }
			//Exit loop if a path has been found
			if(meet == true)
				break;
		}
	} 


	@Override
	public boolean isSolved() 
	{
		return meet;
	}


	@Override
	public int cellsExplored() 
	{
		return exitVisitedCells.size() + entryVisitedCells.size();
	} 

} // end of class BiDirectionalBFSSolver

