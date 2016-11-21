/**
 * 
 */
package org.msm.depcs332.nwpu.app.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Random;

import org.msm.depcs332.nwpu.app.domain.Degree;
import org.msm.depcs332.nwpu.app.domain.Graph;
import org.msm.depcs332.nwpu.app.domain.Pair;
import org.msm.depcs332.nwpu.app.domain.Point;



/**
 * @author ioi
 *
 */
public class Divider {
// data
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
	/* configure */
	// suppose there are n subgraphs in original graph
	private static final int n = Integer.valueOf(props.getProperty("divider.subgraph.number.predict")); 
	// beta belif level
	public static final double lambda = Double.valueOf(props.getProperty("divider.beta.beliflevel")); 
	public static final double MSE_TOLERANCE = Double.valueOf(props.getProperty("divider.mse.tolerance"));
	public static final int GRAPH_SIZE_LOWERBOUND = Integer.valueOf(props.getProperty("divider.graph.size.lowerbound"));
	public static final int NUMBER_PER_CUT = Integer.valueOf(props.getProperty("divider.percut.number.plan"));
	/* graph to divide */
	private Graph graph = new Graph();
	/* occurrence likelihood */
	HashMap<Pair, Double> beta_ij = new HashMap<>();
	HashMap<Pair, Double> simi_ij = new HashMap<>();
	HashMap<Pair, Double> Ocur_ij = new HashMap<>();
	/* em iteration */
	ArrayList<Double> m_zk = new ArrayList<>();
	HashMap<Pair, ArrayList<Double>> sita_ij_zk = new HashMap<>();
	HashMap<Pair, ArrayList<Double>> Q_ij_zk = new HashMap<>();
	/* entropy divide */
	HashMap<Pair, ArrayList<Double>> P_ij_zk = new HashMap<>();
	HashMap<Pair, Double> H_ij = new HashMap<>();
	
// constructor
//
	public Divider() {
		
	}
	public Divider(Graph graph) {
		this.graph = graph;
	}
	
// getter
//
	public Graph getGraph() {
		return this.graph;
	}
	
// occurrence likelihood
//
	private void initBeta() {
		for (Pair pair: getGraph().getEdgeSet()) {
			this.beta_ij.put(pair, getBeta_ij(pair));
		}
	}
	private void initSimi() {
		for (Pair pair: getGraph().getEdgeSet()) {
			this.simi_ij.put(pair, getSimi_ij(pair));
		}
	}
	private void initOcur() {
		initBeta();
		initSimi();
		for (Pair pair: getGraph().getEdgeSet()) {
			this.Ocur_ij.put(pair, getOcur_ij(pair));
		}
	}
	
	/* lib tools */
	private Double getBeta_ij(Pair pair_ij) {
		Point pi = pair_ij.getP1();
		Point pj = pair_ij.getP2();
		Degree di = getGraph().getPointContent().get(pi);
		Degree dj = getGraph().getPointContent().get(pj);
		
		Double beta_ij = 0.0;
		if (pi.getType() == pj.getType()) {
			int type_ij = pi.getType();
			beta_ij = (double)di.getInDegree() * dj.getInDegree() / getGraph().getSumInDegree(type_ij);
		}
		else {
			beta_ij = (double)di.getOutDegree() * dj.getOutDegree() * 2 / getGraph().getSumOutDegree();
		}
		return beta_ij;
	}
	private Double getSimi_ij(Pair pair_ij) {
		Point pi = pair_ij.getP1();
		Point pj = pair_ij.getP2();
		HashSet<Point> pisNeighbors = getGraph().getEdgeContent().get(pi);
		HashSet<Point> pjsNeighbors = getGraph().getEdgeContent().get(pj);
		
		HashSet<Point> intNeighbors = new HashSet<>();
		for (Point point: pisNeighbors) {
			intNeighbors.add((Point)point.clone());
		}
		
		intNeighbors.retainAll(pjsNeighbors);
		Double simi_ij = (double)intNeighbors.size() * intNeighbors.size()
				/ (pisNeighbors.size() * pjsNeighbors.size());
		return simi_ij;
	}
	private Double getOcur_ij(Pair pair_ij) {
		Double beta = this.beta_ij.get(pair_ij);
		Double simi = this.simi_ij.get(pair_ij);
		Double Ocur = lambda * beta + (1.0 - lambda) * simi;
		return Ocur;
	}
	
// em iteration
//
	private void initM() {
		Random rand = new Random();
		double lowerbound = GRAPH_SIZE_LOWERBOUND;
		double length = getGraph().size() - lowerbound;
		for (int k = 0; k < n; k++) {
			double initVal = rand.nextDouble() * length + lowerbound;
			this.m_zk.add(initVal);
		}
	}
	private void initSita() {
		Random rand = new Random();
		double length = 1.0 / n;
		for (Pair pair: getGraph().getEdgeSet()) {
			ArrayList<Double> sita_ij = new ArrayList<>(); 
			for (int k = 0; k < n; k++) {
				double initVal = rand.nextDouble() * length;
				sita_ij.add(initVal);
			}
			this.sita_ij_zk.put(pair, sita_ij);
		}
	}
	private void initQ() {
		for (Pair pair: getGraph().getEdgeSet()) {
			ArrayList<Double> Q_ij = new ArrayList<>();
			for (int k = 0; k < n; k++) {
				Q_ij.add(0.0);
			}
			this.Q_ij_zk.put(pair, Q_ij);
		}
	}
	private void initEmIteration() {
		initM();
		initSita();
		initQ();
	}
	private void QIteration() {
		for (Pair pair: getGraph().getEdgeSet()) {
			double deno = 0.0;
			for (int k = 0; k < n; k++) {
				double m_zk = this.m_zk.get(k);
				double sita_ij_zk = this.sita_ij_zk.get(pair).get(k);
				double nume = m_zk * (1.0 - Math.exp(-sita_ij_zk));
				deno += nume;
				this.Q_ij_zk.get(pair).set(k, nume);				
			}
			for (int k = 0; k < n; k++) {
				double nume = this.Q_ij_zk.get(pair).get(k);
				this.Q_ij_zk.get(pair).set(k, nume / deno);
			}
		}
	}
	private void mIteration() {
		for (int k = 0; k < n; k++) {
			double sum = 0.0;
			for (Pair pair: getGraph().getEdgeSet()) {
				sum += this.Q_ij_zk.get(pair).get(k);
			}
			this.m_zk.set(k, sum);
		}
	}
	private void sitaIteration() {
		for (int k = 0; k < n; k++) {
			double deno = 0.0;
			for (Pair pair: getGraph().getEdgeSet()) {
				double nume = this.Q_ij_zk.get(pair).get(k);
				deno += nume;
				this.sita_ij_zk.get(pair).set(k, nume);
			}
			for (Pair pair: getGraph().getEdgeSet()) {
				double nume = this.Q_ij_zk.get(pair).get(k);
				double iterVal = nume / (deno * this.Ocur_ij.get(pair));
				this.sita_ij_zk.get(pair).set(k, iterVal);
			}
		}
	}	

	private void emIteration() {
		double mse = 100.0;
		while (mse > MSE_TOLERANCE) {
			HashMap<Pair, ArrayList<Double>> old_sita_ij_zk = new HashMap<>();
			for (Pair pair: this.sita_ij_zk.keySet()) {
				ArrayList<Double> vals = new ArrayList<>();
				for (Double val: this.sita_ij_zk.get(pair)) {
					vals.add(new Double(val));
				}
				old_sita_ij_zk.put((Pair)pair.clone(), vals);
			}
			
			QIteration();
			mIteration();
			sitaIteration();
			
			//System.out.println(this.sita_ij_zk);/////////////////////////
			
			mse = 0.0;
			for (Pair pair: getGraph().getEdgeSet()) {
				for (int k = 0; k < n; k++) {
					double oldVal = old_sita_ij_zk.get(pair).get(k);
					double newVal = this.sita_ij_zk.get(pair).get(k);
					mse += (newVal - oldVal) * (newVal - oldVal);
				}
			}
			mse = Math.sqrt(mse / (getGraph().size() * n));			
		}
	}
	
// edge entropy
//
	private void initP() {
		for (Pair pair: getGraph().getEdgeSet()) {
			ArrayList<Double> P_ij = new ArrayList<>();
			for (int k = 0; k < n; k++) {
				double sita = this.sita_ij_zk.get(pair).get(k);
				double P = 1.0 - Math.exp(-sita);
				P_ij.add(P);
			}
			this.P_ij_zk.put(pair, P_ij);
		}
	}
	private void initH() {
		initP();
		for (Pair pair: getGraph().getEdgeSet()) {
			double H = 0.0;
			for (int k = 0; k < n; k++) {
				double P = this.P_ij_zk.get(pair).get(k);
				H += P * Math.log10(P);
			}
			this.H_ij.put(pair, -H);
		}
	}
	
// entropy divide
//
	public ArrayList<Graph> entropyDivide() {
		if (getGraph().size() < GRAPH_SIZE_LOWERBOUND) {
			return new ArrayList<>(); // avoid null
		}
		
		initOcur();
		initEmIteration();
		emIteration();
		initH();
		
		LinkedList<Entry<Pair, Double>> sortedH_ij = new LinkedList<>(this.H_ij.entrySet());
		Collections.sort(sortedH_ij, new Comparator<Entry<Pair, Double>>() {
			@Override
			public int compare(Entry<Pair, Double> o1, Entry<Pair, Double> o2) {
				double cmp = o1.getValue() - o2.getValue();
				if (cmp < 0) {
					return 1;
				}
				else if (cmp == 0) {
					return 0;
				}
				return -1;
			}
		});
		
//		System.out.println(sortedH_ij);
//		System.out.println("beta: " + this.beta_ij);
//		System.out.println("simi: " + this.simi_ij);
//		System.out.println("ocur: " + this.Ocur_ij);
//		System.out.println("sita: " + this.sita_ij_zk);
//		System.out.println("m: " + this.m_zk);
//		System.out.println("Q: " + this.Q_ij_zk);
//		System.out.println(sortedH_ij);
		
		
		int cd = NUMBER_PER_CUT;
		ArrayList<Graph> outList = new ArrayList<>();
		outList.add((Graph)getGraph().clone());
		while (outList.size() < n && !sortedH_ij.isEmpty()) {
			for (int i = 0; i < cd && !sortedH_ij.isEmpty(); i++) {
				Pair pair = sortedH_ij.poll().getKey();
				Point pi = pair.getP1();
				Point pj = pair.getP2();
				for (Graph g: outList) {
					g.deleteEdge(pi, pj);
				}
			}
			
			ArrayList<Graph> tempList = new ArrayList<>();
			for (Graph g: outList) {
				tempList.addAll(g.split());
			}
			outList = tempList;
		} 
		
		// *-----------------*
		Iterator<Graph> iter = outList.iterator();
		while (iter.hasNext()) {
			if (iter.next().size() < GRAPH_SIZE_LOWERBOUND) {
				iter.remove();
			}
		}
				
		return outList;
	}	
}
