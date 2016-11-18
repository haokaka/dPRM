/**
 * 
 */
package org.msm.depcs332.nwpu.app.business;

import java.util.ArrayList;

import org.msm.depcs332.nwpu.app.domain.Graph;
import org.msm.depcs332.nwpu.app.service.Divider;
import org.msm.depcs332.nwpu.app.service.Quality;

import com.sun.xml.internal.bind.v2.model.annotation.Quick;

/**
 * @author ioi
 *
 */
public class Job {
	private Graph graph = new Graph();
	
	public Job() {
		
	}
	public Job(Graph graph) {
		this.graph = graph;
	}
	
	public Graph getGraph() {
		return this.graph;
	}
	
	public ArrayList<Graph> getMsms() {
		if (getGraph().size() < Divider.GRAPH_SIZE_LOWERBOUND) {
			return new ArrayList<>();
		}
		
		ArrayList<Graph> currentList = new ArrayList<>();
		currentList.add(getGraph());
		double currentModularity = Quality.getModularity(currentList);
		
		while (true) {
			ArrayList<Graph> outList = new ArrayList<>();
			int notSplit = 0;
			
			for (Graph gOriginal: currentList) {
				Graph g = (Graph)gOriginal.clone();
				
				if (Quality.getDensity(g) > Quality.DENSITY_LOWERBOUND) {
					//System.out.println("good: " + Quality.getDensity(g));
					outList.add(g);
					++notSplit;
					continue;
				}
				
				outList.addAll(new Divider(g).entropyDivide());
			}
			
			if (notSplit == currentList.size()) {
				break;
			}
			
			double newModularity = Quality.getModularity(outList);
			
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
}
