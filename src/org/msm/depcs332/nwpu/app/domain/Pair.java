/**
 * 
 */
package org.msm.depcs332.nwpu.app.domain;

/**
 * @author ioi
 *
 */
public class Pair implements Cloneable {
// data: point pair
//
	private Point p1 = null;
	private Point p2 = null;
	
// getter
//
	public Point getP1() {
		return p1;
	}
	public Point getP2() {
		return p2;
	}

// setter
//
	public void setP1(Point p1) {
		if (p1.compareTo(getP2()) > 0) {
			this.p1 = this.p2;
			this.p2 = p1;
			return ;
		}
		this.p1 = p1;
	}
	public void setP2(Point p2) {
		if (p2.compareTo(getP1()) < 0) {
			this.p2 = this.p1;
			this.p1 = p2;
			return ;
		}
		this.p2 = p2;
	}

// constructor
//
	public Pair(Point p1, Point p2) {
		if (p1.compareTo(p2) > 0) {
			this.p1 = p2;
			this.p2 = p1;
		}
		else {
			this.p1 = p1;
			this.p2 = p2;
		}
	}
	
// map-key&set constraint: hashCode & equals
//
	@Override
	public int hashCode() {
		return p1.getId() * 503 + p2.getId() * 131;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) { // ref check
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) { // null & runtime-class check
			return false;
		}
		
		Pair other = (Pair)obj;
		if (getP1().compareTo(other.getP1()) == 0
				&& getP2().compareTo(other.getP2()) == 0) { // id check
			return true;
		}
		return false;
	}
	
// clone
//
	@Override
	public Object clone() {
		Pair copy = null;  
        try {  
            copy = (Pair)super.clone();
        }  
        catch (CloneNotSupportedException e) { 
            e.printStackTrace();  
        } 
        copy.p1 = (Point)this.p1.clone();
        copy.p2 = (Point)this.p2.clone();
        return copy;  
	}
	
// debug output
//
	@Override
	public String toString() {
		return getP1() + ":-:" + getP2();
	}
}
