package org.mozilla.bgirard.mozsymserver;

import java.util.HashMap;

public class SymbolDB {
	
	private HashMap<String, BuildSymbols> buildSymbolMap = new HashMap<String, BuildSymbols>();
	
	public SymbolDB() {
		
	}

	public void put(String buildid, BuildSymbols sym) {
		buildSymbolMap.put(buildid, sym);
	}

	public Symbol lookup(String buildid, String library, long address) {
		BuildSymbols buildSymbols = buildSymbolMap.get(buildid);
		if( buildSymbols == null ) {
			return null;
		}
		
		return buildSymbols.lookup(library, address);
	}
	
}
