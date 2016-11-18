package org.msm.depcs332.nwpu.app.domain;

public class Point implements Cloneable, Comparable<Point> {
// constant: point's type
//
	public class Type {
		static final int NONE = -1;
		static final int GENE = 0;
		static final int EXON = 1;
	}
	
// data
//
	private int id = -1;
	private int type = Type.NONE;
	
// constructor
//
	public Point() {
		
	}
	public Point(int id, int type) {
		this.id = id;
		this.type = type;
	}

// getter
//
	public int getId() {
		return this.id;
	}
	public int getType() {
		return this.type;
	}
	
// setter
//
	public void setId(int id) {
		this.id = id;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
// clone
//
	@Override
	public Object clone() {
		Point copy = null;  
        try {  
            copy = (Point)super.clone();
        }  
        catch (CloneNotSupportedException e) { 
            e.printStackTrace();  
        }  
        return copy;  
	}
	
// map-key&set constraint: hashCode & equals
//
	@Override
	public int hashCode() {
		return getId();
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj) { // ref check
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) { // null & runtime-class check
			return false;
		}
		
		Point other = (Point)obj;
		if (getId() == other.getId()) { // id check
			return true;
		}
		return false;
	}

// comparable
//
	@Override
	public int compareTo(Point o) {
		return getId() - o.getId();
	}
	
// debug output
//
	@Override
	public String toString() {
		return getId() + "(" + getType() + ")";
	}
}
