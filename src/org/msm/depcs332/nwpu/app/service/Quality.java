/**
 * 
 */
package org.msm.depcs332.nwpu.app.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Properties;

import org.msm.depcs332.nwpu.app.domain.Graph;



/**
 * @author ioi
 *
 */
public class Quality {
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
	public static final double DENSITY_LOWERBOUND = Double.valueOf(props.getProperty("quality.density.lowerbound"));
	public static final double MODULARITY_TOLERANCE = Double.valueOf(props.getProperty("quality.modularity.tolerance"));
	
	public static double getDensity(Graph graph) {
		int nume = 2 * graph.size();
		int nPoint = graph.getPointContent().size();
		int deno = nPoint * (nPoint - 1);
		
		return (double)nume / (deno + 0.1);
	}
	public static double getModularity(ArrayList<Graph> list) {
		double m0 = 0;
		double m1 = 0;
		double mx = 0;
		for (Graph g: list) {
			m0 += g.getSumInEdge(0);
			m1 += g.getSumInEdge(1);
			mx += g.getSumOutEdge();
		}
		m0 += 0.1;
		m1 += 0.1;
		mx += 0.1;
		
		double q = 0.0;
		for (Graph g: list) {
			int l0 = g.getSumInEdge(0);
			int l1 = g.getSumInEdge(1);
			int lx = g.getSumOutEdge();
			
			int d0 = g.getSumInDegree(0);
			int d1 = g.getSumInDegree(1);
			int dx0 = g.getSumOutDegree_byType(0);
			int dx1 = g.getSumOutDegree_byType(1);
			
			double q0 = (l0 / m0) - (d0 / (2 * m0)) * (d0 / (2 * m0));
			double q1 = (l1 / m1) - (d1 / (2 * m1)) * (d1 / (2 * m1));
			double qx = (lx / mx) - (dx0 * dx1) / (mx * mx);
			
			q += q0 + q1 + qx;
		}
		q /= 3;
		
		return q;
	}
}
