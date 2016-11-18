/**
 * 
 */
package org.msm.depcs332.nwpu.app.domain;

/**
 * @author ioi
 *
 */
public class Degree {
	// constant: degree type
	//
		public static final int IN = 0;
		public static final int OUT = 1;
		
	// data
	//
		private int inDegree = 0;
		private int outDegree = 0;
		
	// constructor
	//
		public Degree() {
			
		}
		
	// getter
	//
		public int getInDegree() {
			return this.inDegree;
		}
		public int getOutDegree() {
			return this.outDegree;
		}	
		
	// setter
	//
		public void setInDegree(int inDegree) {
			this.inDegree = inDegree;
		}
		public void setOutDegree(int outDegree) {
			this.outDegree = outDegree;
		}	
		
	// degree deltator
		public void increaseInDegree() {
			++inDegree;
		}
		public void decreaseInDegree() {
			--inDegree;
		}
		public void increaseOutDegree() {
			++outDegree;
		}
		public void decreaseOutDegree() {
			--outDegree;
		}
	}
