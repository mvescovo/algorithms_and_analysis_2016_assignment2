package mazeGenerator;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.Map.Entry;
import maze.*;
import maze.Cell;

/**
 * Implements the Kruskal algorithm to generate mazes
 * @author larvinloy
 *
 */
public class KruskalGenerator implements MazeGenerator 
{
	private Maze mMaze;
    
	/**
     * Check if a cell is in the maze if the maze is a hex
     *
     * @param row the row of the cell to check
     * @param column the column of the cell to check
     * @return weather the cell is in the hex maze
     */
    private boolean isInHexMaze(int row, int column) 
    {
    	return row >= 0 && row < mMaze.sizeR && column >= (row + 1) / 2 && column < mMaze.sizeC + (row + 1) / 2;
    }
    
    /**
     * ALGORITHM KRUSKALMAZE(M)
     * 
	 * Input: Maze M, all walls built up, start and exit points marked.
	 * Output: Maze M, appropriate walls knocked down to form a perfect maze from start to exit.
	 * 
	 * 1. Create a list of all possible edges and shuffle the list
	 * 2. For every cell in the maze, create a data structure that store the tree(parent) it belongs to 
	 * 		and the children it has
	 * 3. Remove an edge from the list of edges and check if both cells have the same parent
	 * 		3.1 If both have the same parent: Go back to step 3.
	 * 		3.2 If both have different parent: break the wall between them, add the children of the parent
	 * 			of the second cell to the parent of the first cell. And set the parent of cell 1
	 * 			as the parent of all the children cells.
	 * 4. Repeat step 3 until all edges are exhausted.
	 * 5. A perfect maze if created after all edges are checked.
	 * 			
     */
	@Override
	public void generateMaze(Maze maze) 
	{
		 mMaze = maze;
		 
		 //Array list to store all the possible edges
		 ArrayList<Edge> edges = new ArrayList<Edge>();
		 
		 //Create list of edges for hex type maze
		 if(maze.type == Maze.HEX)
		 {
			 for (int i = 0; i < maze.sizeR; i++) {
	             for (int j = (i + 1) / 2; j < maze.sizeC + (i + 1) / 2; j++) 
	             {
	                 if (!isInHexMaze(i, j))
	                     continue;
	                 Cell currentCell = maze.map[i][j];
	                 for (int d = 0; d < Maze.NUM_DIR; d++) 
	                 {
	                     Cell currentNeighbor = currentCell.neigh[d];
	                     if(currentNeighbor != null)
	                     {
	                    		 Edge edge = new Edge(currentCell,currentNeighbor);
	                    		 edges.add(edge);
	                     }
	                     
	                 }
	             }
	         }
		 }
		 
		 //Create a list of edges for normal and tunnel type mazes
		 else if(maze.type == Maze.NORMAL || maze.type == Maze.TUNNEL)
		 {
			 for(int i=0;i<maze.sizeR;i++)
			 {
				 for(int j=0;j<maze.sizeC;j++)
				 {
					 if((j == (maze.sizeC-1)) && (i < (maze.sizeR-1)))
					 {
						Edge edge = new Edge(maze.map[i][j],maze.map[i + 1][j]);
						edges.add(edge);
						continue;
					 }
					 else if(i == (maze.sizeR-1) && (j < (maze.sizeC-1)))
					 {
						 Edge edge = new Edge(maze.map[i][j],maze.map[i][j+1]);
						 edges.add(edge);
						 continue;
					 }
					 else if(i < (maze.sizeR-1) && j < (maze.sizeC-1))
					 {
						 Edge edge = new Edge(maze.map[i][j],maze.map[i][j+1]);
						 edges.add(edge);
						 edge = new Edge(maze.map[i][j],maze.map[i + 1][j]);
						 edges.add(edge);
					 }
				 }
			 }
		 }
	
		 //Shuffle the edges randomly so that an edge is picked at random
		 Collections.shuffle(edges);
		 
		 /*
		  * Create a hash map of Cell and the CellTree data structure
		  * The cell tree data structure store information about the parent tree/cell it belongs to
		  * If the cell is a parent cell, it store information about he nodes in the parent tree
		  */
		 HashMap<Cell,CellTree> cellHash = new HashMap<Cell,CellTree>();
		 
		 /*
		  * Initialize all the CellTree data structures for normal and tunnel mazes
		  * Initially the parent cell and children of a cell is the cell itself
		  */
		 if(maze.type == Maze.NORMAL || maze.type == Maze.TUNNEL)
		 {
			 for(int i=0;i<maze.sizeR;i++)
			 {
				 for(int j=0;j<maze.sizeC;j++)
				 {
					 cellHash.put(maze.map[i][j],new CellTree(maze.map[i][j]));
				 }
			 }
		 }
		 
		 /*
		  * If maze is tunnel type, create a list of cells that have tunnels
		  */
		 ArrayList<Cell> tunnelList = new ArrayList<Cell>();
		 if(maze.type == Maze.TUNNEL)
		 {
			 for(int i=0;i<maze.sizeR;i++)
			 {
				 for(int j=0;j<maze.sizeC;j++)
				 {
					 if(maze.map[i][j].tunnelTo != null)
						 tunnelList.add(maze.map[i][j]);
				 }
			 } 
		 }
		 
		 /*
		  * Create another lists of cells but with only one end of the tunnels
		  */
		 ArrayList<Cell> singleEndTunnelList = new ArrayList<Cell>();
		 for(int i = 0; i< tunnelList.size(); i ++)
		 {
			 Cell end1 = tunnelList.get(i);
			 if((!singleEndTunnelList.contains(end1.tunnelTo)) && (!singleEndTunnelList.contains(end1)))
			 {
				 singleEndTunnelList.add(end1); 
			 }
		 }
		 
		 /*
		  * Consider the other end of the tunnel as an edge, 
		  * Mark one end as the parent of the other end	
		  * Update the respective CellTree for the cells with tunnels	  
		  */
		 for(int i = 0; i<singleEndTunnelList.size(); i ++)
		 {
			 cellHash.get(singleEndTunnelList.get(i).tunnelTo).setParent(singleEndTunnelList.get(i));
			 ArrayList<Cell> temp = new ArrayList<Cell>();
			 temp.add(singleEndTunnelList.get(i).tunnelTo);
			 cellHash.get(singleEndTunnelList.get(i)).addChildren(temp);
		 }
		 
		 /*
		  * Initialize all the CellTree data structures for hex type maze
		  * Initially the parent cell and children of a cell is the cell itself
		  */
		 if(maze.type == Maze.HEX)
		 {
			 for (int i = 0; i < maze.sizeR; i++) 
			 {
	             for (int j = (i + 1) / 2; j < maze.sizeC + (i + 1) / 2; j++) 
	             {
	            	 cellHash.put(maze.map[i][j],new CellTree(maze.map[i][j]));
	             }
			 }
		 }
		 
		 /*
		  * Take each edge in the list of edges and remove the wall between them if they dont
		  * belong to the same parent tree
		  */
		 while(!edges.isEmpty())
		 {
			 //Get the first edge in the list
			 Edge edge = edges.remove(0);
			 
			 Cell c1 = edge.c1;
			 Cell c2 = edge.c2;
			 
			//Check if both cells have the same parent
			 Cell parentCellofc1 = cellHash.get(cellHash.get(c1).parent).parent;
			 Cell parentCellofc2 = cellHash.get(cellHash.get(c2).parent).parent;
			 if(parentCellofc1.equals(parentCellofc2))
			 {
				 /*
				  * This edge is useless as it belongs to the same parent cell
				  */
			 }
			 else
			 {
				 /*
				  * Both cells have different parents, 
				  * Break the wall between them
				 */
				 for (int i = 0; i < Maze.NUM_DIR; i++) 
				 {
					 Cell currentNeighbor = c1.neigh[i];
		             if ((currentNeighbor == c2))
		             {
		            	 c1.wall[i].present = false;
		             }
				 }
				 /* So, add the children of cell 2's parent to the list of children of the parent 
				  * of cell 1
				  * Change the parent of the every child of cell 2's parent(which includes cell 2) to
				  * cell 1's parent
				  */	
				 ArrayList<Cell> children = cellHash.get(cellHash.get(c2).parent).getChildren();
				 cellHash.get(parentCellofc1).addChildren(cellHash.get(cellHash.get(c2).parent).getChildren());
				 for(Cell e: children)
				 {
					 cellHash.get(e).setParent(cellHash.get(c1).getParent());
					 
				 }	 
			 }
		 }		 
	} // end of generateMaze()
	
	/**
	 * Data structure to identify the parent of each cell (root of the tree it belongs to)
	 * Also store the children of the cell (nodes of the tree)
	 * The children are only valid if the cell is the parent of itself (cell is the root itself)
	 * @author larvinloy
	 *
	 */
	public class CellTree
	{
		//The parent cell of this tree
		private Cell parent;
		
		//The children in this tree
		private ArrayList<Cell> children = new ArrayList<Cell>();
		
		public CellTree(Cell init)
		{
			this.parent = init;
			this.children.add(init);
		}

		public Cell getParent()
		{
			return parent;
		}

		public void setParent(Cell parent)
		{
			this.parent = parent;
		}

		public ArrayList<Cell> getChildren()
		{
			return children;
		}

		public void addChildren(ArrayList<Cell> children)
		{
			this.children.addAll(children);
		}
	}
	
	/**
	 * Data structure to store edges
	 * Each Edge object contains two cells c1 and c2
	 * @author larvinloy
	 *
	 */
	public class Edge
	{
		private Cell c1;
		private Cell c2;
		
		public Edge(Cell c1, Cell c2)
		{
			this.c1 = c1;
			this.c2 = c2;
		}
		
		public Cell c1()
		{
			return this.c1;
		}
		
		public Cell c2()
		{
			return this.c2;
		}
	}

} // end of class KruskalGenerator
