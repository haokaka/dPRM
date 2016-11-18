/**
 * 
 */
package org.msm.depcs332.nwpu.app.service.bio;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.msm.depcs332.nwpu.app.domain.Pair;
import org.msm.depcs332.nwpu.app.domain.Point;

/**
 * @author ioi
 *
 */
public class BioDataPreparator {

	/**
	 * @param args
	 */
// data
//
	private HashSet<BioNode> nodeSet = new HashSet<>();
	private HashSet<Pair> pairSet = new HashSet<>();
	
// getter
//
	public HashSet<BioNode> getNodeSet() {
		return nodeSet;
	}
	public HashSet<Pair> getPairSet() {
		return pairSet;
	}
	
// setter
//
	public void setNodeSet(HashSet<BioNode> nodeSet) {
		this.nodeSet = nodeSet;
	}
	public void setPairSet(HashSet<Pair> pairSet) {
		this.pairSet = pairSet;
	}

// IO
//
	public void readNodeAttributesFrom(String file) {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] attrs = line.split("\\s+");
				if (attrs.length != 2) {
					continue;
				}
				
				String name = attrs[0];
				int type = (attrs[1].equals(BioNode.GENE)? 0: 1);
				BioNode node = new BioNode(name, -1, type);
				getNodeSet().add(node);
			}
			br.close();
			
			int id = 0;
			for (BioNode node: getNodeSet()) {
				node.setId(id++);
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	public void readEdgeFrom(String file) {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			String line = null;
			while ((line = br.readLine()) != null) {
				String[] names = line.split("\\s+");
				if (names.length != 2) {
					continue;
				}
				
				BioNode node1 = getNodeByName(names[0]);
				BioNode node2 = getNodeByName(names[1]);
				if (node1 != null && node2 != null) {
					Point point1 = new Point(node1.getId(), node1.getType());
					Point point2 = new Point(node2.getId(), node2.getType());
					getPairSet().add(new Pair(point1, point2));
				}
			}
			br.close();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}		
	}
	
	/* lib tools */
	private BioNode getNodeByName(String name) {
		for (BioNode node: getNodeSet()) {
			if (name.equals(node.getName())) {
				return node;
			}
		}
		
		return null;
	}
	
// debug test
//
	public static void main(String[] args) {
		BioDataPreparator prep = new BioDataPreparator();
		prep.readNodeAttributesFrom("test\\data\\big\\out_node_atrr.txt");
		prep.readEdgeFrom("test\\data\\big\\out_net_real.txt");
		for (Pair pair: prep.getPairSet()) {
			System.out.println(pair);
		}
		System.out.println(prep.getPairSet().size());
		
	}

}
