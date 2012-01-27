package org.mozilla.bgirard.mozsymserver;

import java.util.HashMap;

public class BuildSymbols {
	private HashMap<String, LibrarySymbol> librarySymbols = new HashMap<String, LibrarySymbol>();

	public void put(String libraryName, LibrarySymbol librarySymbol) {
		librarySymbols.put(libraryName, librarySymbol);
	}

	public Symbol lookup(String library, long address) {
		if( library.endsWith(".dll") || library.endsWith(".exe") ) {
			library = library.substring(0, library.length() - 4);
		}
		
		LibrarySymbol librarySymbol = librarySymbols.get(library);
		if( librarySymbol == null ) {
			return null;
		}
		return librarySymbol.lookup(address);
	}
}
