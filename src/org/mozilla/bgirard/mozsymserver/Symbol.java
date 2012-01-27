package org.mozilla.bgirard.mozsymserver;

public class Symbol implements Comparable<Object> {
	public String name;
	public long start;
	public long len;
	public long end;
	
	public Symbol(String name, long start, long len) {
		this.name = name;
		this.start = start;
		this.len = len;
		this.end = start+len-1;
	}

	@Override
	public int compareTo(Object in) {
		if( in instanceof Symbol ) {
			Symbol o = (Symbol)in;
			if( start < o.start ) return -1;
			if( start > o.start ) return 1;
		} else if( in instanceof Long ) {
			long o = (Long)in;
			if( start < o ) return -1;
			if( start > o ) return 1;
		}
		return 0;
	}
}
