/**
 * 
 */
package org.msm.depcs332.nwpu.app;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Properties;
import java.util.Random;

import org.msm.depcs332.nwpu.app.business.Job;
import org.msm.depcs332.nwpu.app.domain.Graph;
import org.msm.depcs332.nwpu.app.domain.Pair;
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
	
// run algorithm & get result
//
	public static void run(Graph originGraph, STRUCT best) {
		Job job = new Job(originGraph);
		ArrayList<Graph> outList = new ArrayList<>();
		outList.addAll(job.getMsms());
		ArrayList<Graph> recList = job.recover(outList);
		
		double thisModularity = Quality.getModularity(outList, originGraph);
		if (thisModularity > best.getModularity()) {
			best.setModularity(thisModularity);
			best.setList(recList);
		}
		
	// debug output
	//
		int size = 0;
		for (Graph g: outList) {
			size += g.size();
		}
		pln("    reuslt: [" +
				"split-blocks: " + outList.size() + ", " +
				"left-edges: " + size + ", " +
				"cut-edges: " + (originGraph.size() - size) + ", " + 
				"cut-level: " + (int)((originGraph.size() - size) / (double)originGraph.size() * 100) + "%, " +
				"modularity: " + thisModularity + "]");
		p("\teach module:    ");
		for (Graph g: outList) {
			p("[" + g.getPointContent().size() + "(" + g.size() + ")]    ");
		}
		pln("");
		p("\teach recove:    ");
		for (Graph g: recList) {
			p("[" + g.getPointContent().size() + "(" + g.size() + ")]    ");
		}
		pln("");
	}
	
// write result
// 
	public static void writeResult2Txt(ArrayList<Graph> resultList, BioDataPreparator prep) {
		String prefix = "test\\data\\output\\";
		String fileNode = props.getProperty("output.file.node"); // "node_attr.txt";
		String fileEdge = props.getProperty("output.file.edge"); // "net_attr.txt";
		
		PrintStream psNode;
		PrintStream psEdge;
		try {
			psNode = new PrintStream(new FileOutputStream(prefix + fileNode));
			for (Graph g: resultList) {
				for (Point point: g.getPointContent().keySet()) {
					psNode.print(prep.getNameById(point.getId()) + "\t");
				}
				psNode.println();
			}
			psNode.close();
			
			psEdge = new PrintStream(new FileOutputStream(prefix + fileEdge));
			for (Graph g: resultList) {
				for (Pair pair: g.getEdgeSet()) {
					int id1 = pair.getP1().getId();
					int id2 = pair.getP2().getId();
					psEdge.println(prep.getNameById(id1) + "\t" + prep.getNameById(id2));
				}
			}
			psEdge.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
// main process
//
	/* configuration */
	private static Properties props = new Properties();
	static {
		try {
			props.load(new FileInputStream("conf.properties"));	
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void main(String[] args) {
		Graph inputGraph = new Graph();
		Graph originGraph = new Graph();
		BioDataPreparator prep = new BioDataPreparator();
		String prefix = "test\\data\\input\\";
		String fileNode = prefix + props.getProperty("input.file.node"); // "out_node_atrr_new.txt";
		String fileEdge = prefix + props.getProperty("input.file.edge"); // "out_net_v7.txt";
		String sifOriginGraph = "test\\data\\output\\bit.sif";
		
		pln("#1:"); // data preparation
			pln("@load from $file{" + fileNode + "} & $file{" + fileEdge + "} ...");
				prep.readNodeAttributesFrom(fileNode);
				prep.readEdgeFrom(fileEdge);
				inputGraph.readFromPerparator(prep);
			pln("@load finished.\n");
		// #1
		
		pln("#2:"); // runner initialization
			pln("@check sub-graph within the input:");
				p("    sub-graph size: ");
					ArrayList<Graph> list = inputGraph.split();
					Graph leftSubGraph = new Graph();
					for (Graph sub_g: list) {
						p("" + sub_g.size());
						if (sub_g.size() < Divider.GRAPH_SIZE_LOWERBOUND) {
							p("(脳)\t");
						}
						else {
							p("(鈭�)\t");
							leftSubGraph = sub_g;
						}
					}
					originGraph = leftSubGraph;
				pln("");
			pln("@write origin-graph into $file{"+ sifOriginGraph + "} ...");
				inputGraph.writeToSif(sifOriginGraph);
			pln("@write finished.\n");
		// #2
		
		pln("#3:");  // running configuration
		pln("@configuration:");
			pln("\t###########################");
			pln("\t# graph-size-lowerbound: " + Divider.GRAPH_SIZE_LOWERBOUND);
			pln("\t# mse-tolerance: " + Divider.MSE_TOLERANCE);
			pln("\t# edges-percut: " + Divider.NUMBER_PER_CUT);
			pln("\t# modularity-cutoff: " + Quality.MODULARITY_TOLERANCE);
			pln("\t# density-lowerbound: " + Quality.DENSITY_LOWERBOUND);
			pln("\t# belif-level: " + Divider.lambda);
			pln("\t###########################\n");
		// #3
			
	// run for result
	//
		pln("@ready to run ...\n");
		STRUCT best = new STRUCT();
		int testRoundNum = Integer.valueOf(props.getProperty("test.round.number"));  // 4 configuration============================
		for (int i = 0; i < testRoundNum; i++) {
			pln("<round" + (i + 1) + "> ... ");
				long t0 = System.currentTimeMillis();
					Runner.run(originGraph, best);
				long tx = System.currentTimeMillis();
			pln("  -:- time consuming: " + (tx - t0) / 1000 + " sec. -:-");
		}
		
//		for (int i = 0; i < best.getList().size(); i++) {
//			String out = "test\\out\\big\\out" + (i + 1) + ".sif";
//			best.getList().get(i).writeToSif(out);
//		}
		writeResult2Txt(best.getList(), prep);
	}
	
// debug output
//
	public static void pln(String msg) {
		System.out.println(msg);
	}
	public static void p(String msg) {
		System.out.print(msg);
	}
}
