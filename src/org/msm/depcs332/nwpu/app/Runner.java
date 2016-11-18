/**
 * 
 */
package org.msm.depcs332.nwpu.app;

import java.util.ArrayList;
import java.util.Random;

import org.msm.depcs332.nwpu.app.business.Job;
import org.msm.depcs332.nwpu.app.domain.Graph;
import org.msm.depcs332.nwpu.app.domain.Point;
import org.msm.depcs332.nwpu.app.service.Divider;
import org.msm.depcs332.nwpu.app.service.Quality;
import org.msm.depcs332.nwpu.app.service.bio.BioDataPreparator;

/**
 * @author ioi
 *
 */
public class Runner {

	/**
	 * @param args
	 */
	
	public static void run(Graph originalGraph, STRUCT best) {
		ArrayList<Graph> list = originalGraph.split();		
		ArrayList<Graph> outList = new ArrayList<>();
		for (Graph g: list) {
			Job job = new Job(g);
			outList.addAll(job.getMsms());
		}
		
		double thisModularity = Quality.getModularity(outList);
		if (thisModularity > best.getModularity()) {
			best.setModularity(thisModularity);
			best.setList(outList);
		}
		
		// debug output
		int size = 0;
		for (Graph g: outList) {
			size += g.size();
		}
		System.out.println("@ sample: [number: " + outList.size() + ", size: " + size + ", modularity: " + thisModularity + "]");
		System.out.print("\t--: ");
		for (Graph g: outList) {
			System.out.print(g.size() + " | ");
		} 
		System.out.println();
		//-----------
	}
	
	public static void main(String[] args) {
		Graph g = new Graph();
//		g.readFromSif("SIMU_AA.txt", 0);
//		g.readFromSif("SIMU_BB.txt", 1);
//		g.readFromSif("SIMU_AABB.txt", 2);
		BioDataPreparator prep = new BioDataPreparator();
		prep.readNodeAttributesFrom("test\\data\\big\\out_node_atrr_v1.txt");
		prep.readEdgeFrom("test\\data\\big\\out_net_v4.txt");
		System.out.println("@ load finish.");
		g.readFromPerparator(prep);
		//g.writeToSif("test\\out\\bit.sif");
		
		
		STRUCT best = new STRUCT();
		for (int i = 0; i < 10; i++) {
			System.out.print("<" + (i + 1) + ">\t");
			Graph cp_g = (Graph)g.clone();
			long t0 = System.currentTimeMillis();
			Runner.run(cp_g, best);
			long tx = System.currentTimeMillis();
			System.out.println("time consume: " + (tx - t0) / 1000);
		}
		
		for (int i = 0; i < best.getList().size(); i++) {
			String out = "test\\out\\big\\out" + (i + 1) + ".sif";
			best.getList().get(i).writeToSif(out);
		}
	}
}
