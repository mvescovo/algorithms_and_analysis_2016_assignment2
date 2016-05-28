package mazeSolver;

import maze.Cell;
import maze.Maze;

import java.util.HashSet;
import java.util.LinkedList;

import static maze.Maze.HEX;

/** 
 * Implements Bi-directional BFS maze solving algorithm.
 */
public class BiDirectionalBFSSolver implements MazeSolver 
{
	private boolean meet = false;
	private Maze mMaze;
	private HashSet<Cell> entryVisitedCells;
	private HashSet<Cell> exitVisitedCells;
	
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
	
	private boolean isIn(Cell cell) 
	{
        return cell != null && isIn(cell.r, cell.c);
	}
	
	
	@Override
	public void solveMaze(Maze maze) 
	{
		this.mMaze = maze;
		LinkedList<Cell> entryQueue = new LinkedList<Cell>();
		LinkedList<Cell> exitQueue = new LinkedList<Cell>();
		entryVisitedCells = new HashSet<Cell>();
		exitVisitedCells = new HashSet<Cell>();
		
		entryQueue.add(maze.entrance);
		exitQueue.add(maze.exit);
		
		meet = false;
		
		Cell entryCurrent;
		Cell exitCurrent;
		
		
		while(!meet)
		{
			//entry queue display
			System.out.print("\nEntry Queue: " );
			for(int i = 0; i < entryQueue.size(); i++)
			{
				System.out.print("Cell(" + entryQueue.get(i).r + "," + entryQueue.get(i).c + ") ");
			}
			System.out.print("\nExit Queue: ");
			for(int i = 0; i < exitQueue.size(); i++)
			{
				System.out.print("Cell(" + exitQueue.get(i).r + "," + exitQueue.get(i).c + ") ");
			}
			
			
			
			//entry side start first
			entryCurrent = entryQueue.removeFirst();
			maze.drawFtPrt(entryCurrent);
			entryVisitedCells.add(entryCurrent);
			if(entryCurrent.tunnelTo != null)
			{
				Cell tunnelNeighbor = entryCurrent.tunnelTo;
				if((exitQueue.contains(tunnelNeighbor)) || exitVisitedCells.contains(tunnelNeighbor))
                {
                	maze.drawFtPrt(tunnelNeighbor);
                	meet = true;
                	break;
                }
				else if ((isIn(tunnelNeighbor)) && (!entryQueue.contains(tunnelNeighbor)) 
                		&& (!entryVisitedCells.contains(tunnelNeighbor))) 
                {
                    entryQueue.add(tunnelNeighbor);
                }
			}
			
			for (int i = 0; i < Maze.NUM_DIR; i++) 
			{
                Cell currentNeighbor = entryCurrent.neigh[i];
                if((exitQueue.contains(currentNeighbor)) && (!entryCurrent.wall[i].present))
                {
                	maze.drawFtPrt(currentNeighbor);
                	meet = true;
                	break;
                }
                //exp part
                else if((exitVisitedCells.contains(currentNeighbor)) && (!entryCurrent.wall[i].present))
                {
                	maze.drawFtPrt(currentNeighbor);
                	meet = true;
                	break;
                }
                //exp part
                else if ((isIn(currentNeighbor)) && (!entryQueue.contains(currentNeighbor)) 
                		&& (!entryCurrent.wall[i].present) && (!entryVisitedCells.contains(currentNeighbor))) 
                {
                    entryQueue.add(currentNeighbor);
                }
            }
			
			//exit part
			try
			{
				Thread.sleep(200);
			} catch (InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			exitCurrent = exitQueue.removeFirst();
			maze.drawFtPrt(exitCurrent);
			exitVisitedCells.add(exitCurrent);
			if(exitCurrent.tunnelTo != null)
			{
				Cell tunnelNeighbor = exitCurrent.tunnelTo;
				if((entryQueue.contains(tunnelNeighbor)) || entryVisitedCells.contains(tunnelNeighbor))
                {
                	maze.drawFtPrt(tunnelNeighbor);
                	meet = true;
                	break;
                }
				else if ((!exitQueue.contains(tunnelNeighbor)) 
                		&& (!exitVisitedCells.contains(tunnelNeighbor))) 
                {
                    exitQueue.add(tunnelNeighbor);
                }
			}
			for (int i = 0; i < Maze.NUM_DIR; i++) 
			{
                Cell currentNeighbor = exitCurrent.neigh[i];
                if((entryQueue.contains(currentNeighbor)) && (!exitCurrent.wall[i].present))
                {
                	maze.drawFtPrt(currentNeighbor);
                	meet = true;
                	break;
                }
                else if((entryVisitedCells.contains(currentNeighbor)) && (!exitCurrent.wall[i].present))
                {
                	maze.drawFtPrt(currentNeighbor);
                	meet = true;
                	break;
                }
                else if ((isIn(currentNeighbor)) && (!exitQueue.contains(currentNeighbor))
                		&& (!exitCurrent.wall[i].present) && (!exitVisitedCells.contains(currentNeighbor))) 
                {
                    exitQueue.add(currentNeighbor);
                }
            }
		}
		
		
	} // end of solveMaze()


	@Override
	public boolean isSolved() {
		// TODO Auto-generated method stub
		return meet;
	} // end of isSolved()


	@Override
	public int cellsExplored() {
		// TODO Auto-generated method stub
		return exitVisitedCells.size() + entryVisitedCells.size();
	} // end of cellsExplored()

} // end of class BiDirectionalBFSSolver

