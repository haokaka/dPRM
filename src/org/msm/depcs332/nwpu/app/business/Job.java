/**
 * 
 */
package org.msm.depcs332.nwpu.app.business;

import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import org.msm.depcs332.nwpu.app.domain.Degree;
import org.msm.depcs332.nwpu.app.domain.Graph;
import org.msm.depcs332.nwpu.app.domain.Point;
import org.msm.depcs332.nwpu.app.service.Divider;
import org.msm.depcs332.nwpu.app.service.Quality;



/**
 * @author ioi
 *
 */
public class Job {
	private Graph graph = new Graph();
	
	public Job() {
		
	}
	public Job(Graph graph) {
		this.graph = degreeFilter(graph);
	}
	
	public Graph getGraph() {
		return this.graph;
	}
	
	public ArrayList<Graph> getMsms() {
		if (getGraph().size() < Divider.GRAPH_SIZE_LOWERBOUND) {
			return new ArrayList<>();
		}
		
		Graph originGraph = (Graph)getGraph().clone();
		
		ArrayList<Graph> currentList = new ArrayList<>();
		currentList.add(originGraph);
		double currentModularity = Quality.getModularity(currentList, originGraph);
		while (true) {
			ArrayList<Graph> outList = new ArrayList<>();
			int notSplit = 0;
			
			for (Graph gOriginal: currentList) {
				Graph g = (Graph)gOriginal.clone();
				if (Quality.getDensity(g) > Quality.DENSITY_LOWERBOUND) {
					outList.add(g);
					++notSplit;
					continue;
				}
				outList.addAll(new Divider(g).entropyDivide());
			}
			
			if (notSplit == currentList.size()) {
				break;
			}
			
			double newModularity = Quality.getModularity(outList, originGraph);
			
			if (newModularity < currentModularity) { // down				
				if (currentModularity - newModularity < Quality.MODULARITY_TOLERANCE) {
					currentList = outList;
					currentModularity = newModularity;
					continue ;
				}
				break;
			}

			currentList = outList;
			currentModularity = newModularity;
		}
		
		return currentList;		
	}
	
	public ArrayList<Graph> recover(ArrayList<Graph> subGraphs) {
		ArrayList<Graph> recoverGraphs = new ArrayList<>();
		for (Graph g: subGraphs) {
			recoverGraphs.add(recover(g));
		}
		return recoverGraphs;
	}
	private Graph recover(Graph subGraph) {
		Graph recGraph = new Graph();
		Set<Point> subPointSet = subGraph.getPointContent().keySet();
		for (Point point_i: subPointSet) {
			Point pi = (Point)point_i.clone();
			for (Point point_j: getGraph().getEdgeContent().get(point_i)) {
				Point pj = (Point)point_j;
				if (subPointSet.contains(point_j)) {
					recGraph.addEdge(pi, pj);
				}
			}
		}
		return degreeFilter(recGraph);
	}
	private Graph degreeFilter(Graph g) {
		Graph cp_g = (Graph)g.clone();
		for (Entry<Point, Degree> entry: g.getPointContent().entrySet()) {
			Point pi = entry.getKey();
			Degree degree = entry.getValue();
			if (degree.getInDegree() + degree.getOutDegree() < 3) {
				for (Point pj: g.getEdgeContent().get(pi)) {
					cp_g.deleteEdge(pi, pj);
				}
			}
		}
		
		ArrayList<Graph> filts = cp_g.split();
		Graph filt_g = filts.isEmpty()? new Graph(): filts.get(0);
		return filt_g;
	}
}
