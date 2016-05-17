package mazeGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

import maze.Cell;
import maze.Maze;
import mazeGenerator.KruskalGenerator.CellTree;
import mazeGenerator.KruskalGenerator.Edge;

public class KruskalGenerator implements MazeGenerator {

	private Maze mMaze;
    private Random mRandGen = new Random(System.currentTimeMillis());
    
    private boolean isInHexMaze(int row, int column) 
    {
    	return row >= 0 && row < mMaze.sizeR && column >= (row + 1) / 2 && column < mMaze.sizeC + (row + 1) / 2;
    }
    
	@Override
	public void generateMaze(Maze maze) 
	{
		 mMaze = maze;
		 int numCells = maze.sizeR * maze.sizeC;
		 ArrayList<Edge> edges = new ArrayList<Edge>();
		 
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
		 else if(maze.type == Maze.NORMAL)
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
	
		 
		 Collections.shuffle(edges);
		 
//		 System.out.println(edges.size());
//		 for(Edge e:edges)
//		 {
//			 System.out.println("\nEdge: ");
//			 System.out.println(e.c1().r + " " + e.c1().c);
//			 System.out.println(e.c2().r + " " + e.c2().c);
//		 }
		 
		 
		 
		 HashMap<Cell,CellTree> cellHash = new HashMap<Cell,CellTree>();
		 
		 if(maze.type == Maze.NORMAL)
		 {
			 for(int i=0;i<maze.sizeR;i++)
			 {
				 for(int j=0;j<maze.sizeC;j++)
				 {
					 cellHash.put(maze.map[i][j],new CellTree(maze.map[i][j]));
				 }
			 }
		 }
		 else if(maze.type == Maze.HEX)
		 {
			 for (int i = 0; i < maze.sizeR; i++) 
			 {
	             for (int j = (i + 1) / 2; j < maze.sizeC + (i + 1) / 2; j++) 
	             {
	            	 cellHash.put(maze.map[i][j],new CellTree(maze.map[i][j]));
	             }
			 }
		 }
		 
		 int c = 0;
		 while(!edges.isEmpty())
		 {
			 c++;
			 Edge edge = edges.remove(0);
			 Cell c1 = edge.c1;
			 Cell c2 = edge.c2;
			 Cell parentCellofc1 = cellHash.get(cellHash.get(c1).parent).parent;
			 Cell parentCellofc2 = cellHash.get(cellHash.get(c2).parent).parent;
			 if(parentCellofc1.equals(parentCellofc2))
			 {
//				 System.out.println("Useless edge");
			 }
			 else
			 {
				 for (int i = 0; i < Maze.NUM_DIR; i++) 
				 {
					 Cell currentNeighbor = c1.neigh[i];
		             if ((currentNeighbor == c2))
		             {
		            	 c1.wall[i].present = false;
		             }
				 }
				 ArrayList<Cell> children = cellHash.get(cellHash.get(c2).parent).getChildren();
				 cellHash.get(parentCellofc1).addChildren(cellHash.get(cellHash.get(c2).parent).getChildren());
				 for(Cell e: children)
				 {
					 cellHash.get(e).setParent(cellHash.get(c1).getParent());
					 
				 }
				 
				 
//				 System.out.println("\n Table:");
//				 for (Entry<Cell, CellTree> entry : cellHash.entrySet())
//				 {
//					 System.out.print(entry.getKey().r + "," 
//							 + entry.getKey().c + "  " 
//							 + entry.getValue().parent.r + "," 
//							 + entry.getValue().parent.c + "  ");
//					 children = entry.getValue().getChildren();
//					 for(Cell e: children)
//					 {
//						 System.out.print(e.r +"," + e.c + " ");
//					 }
//					 System.out.println();
//				 }
			 }
//			 if(c==10)
//				 break;
		 }
		 
		
		 
		 
		 
	} // end of generateMaze()
	
	public class CellTree
	{
		private Cell parent;
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