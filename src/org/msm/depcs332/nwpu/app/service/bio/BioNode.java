/**
 * 
 */
package org.msm.depcs332.nwpu.app.service.bio;

import com.sun.org.glassfish.external.arc.Stability;
import com.sun.xml.internal.fastinfoset.algorithm.IEEE754FloatingPointEncodingAlgorithm;

/**
 * @author ioi
 *
 */
public class BioNode {
	public static final int TYPE_NONE = -1;
	public static final int TYPE_GENE = 0;
	public static final int TYPE_EXON = 1;
	public static final String GENE = "Gene";
	public static final String EXON = "Exon";
	
	private String name = "none";
	private int id = -1;
	private int type = TYPE_NONE;
	
// constructor
//
	public BioNode() {
		
	}
	public BioNode(String name, int id, int type) {
		this.name = name;
		this.id = id;
		this.type = type;
	}
	
// getter
//
	public String getName() {
		return name;
	}
	public int getId() {
		return id;
	}
	public int getType() {
		return type;
	}

// setter
//
	public void setName(String name) {
		this.name = name;
	}
	public void setId(int id) {
		this.id = id;
	}
	public void setType(int type) {
		this.type = type;
	}
	
// map-key&set constraint: hashCode & equals
//
	@Override
	public int hashCode() {
		return getName().hashCode();
	}
	@Override
	public boolean equals(Object obj) {
		return getName().equals(obj);
	}
	
// debug output
//
	@Override
	public String toString() {
		return "[id: " + getId() + ", name: " + getName() + ", type: " + getType() + "]";
	}
}
