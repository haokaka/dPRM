/**
 * 
 */
package org.msm.depcs332.nwpu.app;

import java.util.ArrayList;

import org.msm.depcs332.nwpu.app.domain.Graph;

/**
 * @author ioi
 *
 */
class STRUCT {
	private ArrayList<Graph> list = new ArrayList<>();
	private double modularity = 0.0;
	
	public STRUCT() {
		
	}
	public STRUCT(ArrayList<Graph> list, double modularity) {
		this.list = list;
		this.modularity = modularity;
	}
	
	public ArrayList<Graph> getList() {
		return list;
	}
	public void setList(ArrayList<Graph> list) {
		this.list = list;
	}
	public double getModularity() {
		return modularity;
	}
	public void setModularity(double modularity) {
		this.modularity = modularity;
	}	
}