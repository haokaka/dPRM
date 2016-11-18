/**
 * 
 */
package org.msm.depcs332.nwpu.app.domain;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.msm.depcs332.nwpu.app.service.bio.BioDataPreparator;

import com.sun.xml.internal.bind.v2.model.core.TypeInfo;



/**
 * @author ioi
 *
 */
public class Graph implements Cloneable {
// data
//
	private HashMap<Point, Degree> pointContent = new HashMap<>();
	private HashMap<Point, HashSet<Point>> edgeContent = new HashMap<>();

// constructor
//
	public Graph() {
		
	}
	
// getter
//
	public HashMap<Point, Degree> getPointContent() {
		return this.pointContent;
	}
	public HashMap<Point, HashSet<Point>> getEdgeContent() {
		return this.edgeContent;
	}
	
// setter
//
	public void setPointContent(HashMap<Point, Degree> pointContent) {
		this.pointContent = pointContent;
	}
	public void setEdgeContent(HashMap<Point, HashSet<Point>> edgeContent) {
		this.edgeContent = edgeContent;
	}
		
// builder
//		
	public void clear() {
		getPointContent().clear();
		getEdgeContent().clear();
	}
	public void addEdge(Point pi, Point pj) {
		if (addArc(pi, pj) && addArc(pj, pi)) {
			if (pi.getType() == pj.getType()) {
				getPointContent().get(pi).increaseInDegree();
				getPointContent().get(pj).increaseInDegree();
			}
			else {
				getPointContent().get(pi).increaseOutDegree();
				getPointContent().get(pj).increaseOutDegree();
			}
		}
	}
	public void deleteEdge(Point pi, Point pj) {
		if (deleteArc(pi, pj) && deleteArc(pj, pi)) {
			if (pi.getType() == pj.getType()) {
				getPointContent().get(pi).decreaseInDegree();
				getPointContent().get(pj).decreaseInDegree();
			}
			else {
				getPointContent().get(pi).decreaseOutDegree();
				getPointContent().get(pj).decreaseOutDegree();
			}
		}
	}
	public HashSet<Pair> getEdgeSet() {
		if (getEdgeContent().isEmpty()) {
			return new HashSet<>(); // avoid null
		}
		
		HashSet<Pair> edgeSet = new HashSet<>(); 
		for (Point pi: getEdgeContent().keySet()) {
			for (Point pj: getEdgeContent().get(pi)) {
				edgeSet.add(new Pair(pi, pj));
			}
		}
		return edgeSet;
	}
	public int size() {
		return getEdgeSet().size();
	}
	
	/* lib tools */
	private boolean addArc(Point from, Point to) {
		if (getPointContent().containsKey(from)) { // means arc have already created
			return getEdgeContent().get(from).add(to); // so [add] the arc
		}
		
	// means point have not been added, so as the arc
		
		// first added the point
		getPointContent().put(from, new Degree()); 
		// next [create] the arc 
		HashSet<Point> fromsNeighbors = new HashSet<>();
		fromsNeighbors.add(to);
		getEdgeContent().put(from, fromsNeighbors);
		return true;
	}
	private boolean deleteArc(Point from, Point to) {
		if (getPointContent().containsKey(from)) {
			if (getEdgeContent().get(from).contains(to)) {
				getEdgeContent().get(from).remove(to);
				return true;
			}
		}
		return false;
	}
		
// clone
//
	@Override
	public Object clone() {
		Graph copy = null;
		try {
			copy = (Graph)super.clone();
		}
		catch (CloneNotSupportedException e) {
	        e.printStackTrace();  
	    }
		
		copy.setPointContent(new HashMap<>());
		copy.setEdgeContent(new HashMap<>());
		for (Point this_pi: this.getEdgeContent().keySet()) {
			Point copy_pi = (Point)this_pi.clone();
			for (Point this_pj: this.getEdgeContent().get(this_pi)) {
				Point copy_pj = (Point)this_pj.clone();
				copy.addEdge(copy_pi, copy_pj);
			}
		}
		
		return copy;
	}

// get sum-degree
//
	public int getSumInDegree(int type) {
		int sum = 0;
		for (Point point: getPointContent().keySet()) {
			Degree degree = getPointContent().get(point);
			sum += (type == point.getType()? degree.getInDegree(): 0);
		}
		return sum;
	}
	public int getSumOutDegree() {
		int sum = 0;
		for (Point point: getPointContent().keySet()) {
			sum += getPointContent().get(point).getOutDegree();
		}
		return sum;
	}
	public int getSumOutDegree_byType(int type) {
		int sum = 0;
		for (Point point: getPointContent().keySet()) {
			sum += (point.getType() == type? 
				getPointContent().get(point).getOutDegree(): 0);
		}
		return sum;
	}
	
// get sum-edge
//
	public int getSumInEdge(int type) {
		return getSumInDegree(type) / 2;
	}
	public int getSumOutEdge() {
		return getSumOutDegree() / 2;
	}

// algorithm
//
	public ArrayList<Graph> split() {
		if (getPointContent().isEmpty()) {
			return new ArrayList<>(); // avoid null
		}
		
		ArrayList<Graph> outList = new ArrayList<>();
		
		Graph copy = (Graph)clone();
		while (!copy.getEdgeContent().isEmpty()) {
			Graph sub = new Graph();
			
			LinkedList<Point> queue = new LinkedList<>();
			queue.offer(copy.getEdgeContent().keySet().iterator().next());
			while (!queue.isEmpty()) {
				Point pi = queue.poll();
				if (copy.getEdgeContent().containsKey(pi)) {
					for (Point pj: copy.getEdgeContent().get(pi)) {
						sub.addEdge(pi, pj);
						queue.offer(pj);
					}
					copy.getEdgeContent().remove(pi);
				}
			}
			
			if (sub.getEdgeContent().size() > 0) {
				outList.add(sub);
			}
		}
		copy.clear();
		
		return outList;
	}

// IO
//
	public void readFromSif(String sif, int type) {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(sif)));
			Pattern pattern = Pattern.compile("\\d+");
			String line = null;
			while ((line = br.readLine()) != null) {
				Matcher matcher = pattern.matcher(line);
				matcher.find();
				Point pi = new Point(Integer.parseInt(matcher.group()), (type == 2? 0: type));
				matcher.find();
				Point pj = new Point(Integer.parseInt(matcher.group()), (type == 2? 1: type));
				addEdge(pi, pj);
			}
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
	public void readFromPerparator(BioDataPreparator prep) {
		if (prep.getPairSet().isEmpty()) {
			return ;
		}
		
		for (Pair pair: prep.getPairSet()) {
			addEdge(pair.getP1(), pair.getP2());
		}
	}
	public void writeToSif(String sif) {
		PrintStream ps;
		try {
			ps = new PrintStream(new FileOutputStream(sif));
			for (Pair pair: getEdgeSet()) {
				ps.println(" " + pair.getP1() + "\tinteracts with\t " + pair.getP2());
			}
			ps.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
