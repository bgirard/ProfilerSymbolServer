package org.mozilla.bgirard.mozsymserver;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class LibrarySymbol {
	
	private List<Symbol> symbols = new ArrayList<Symbol>();

	public LibrarySymbol(String filecontent) throws Exception {
		this(new Scanner(filecontent));
	}
	
	public LibrarySymbol(Scanner scanner) throws Exception {
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if( line.startsWith("FUNC ") ) {
				String[] parts = line.split(" ", 5);
				long start = Long.parseLong(parts[1], 16);
				long length = Long.parseLong(parts[2], 16);
				String name = parts[4];
				
				Symbol s = new Symbol(name, start, length);
				
				symbols.add( s );
			}
		}
		Collections.sort(symbols);
	}
	
	private Symbol lookup(String addressStr) {
		if( addressStr.startsWith("0x") ) addressStr = addressStr.substring(2);
		
		long address = Long.parseLong(addressStr, 16);
		int insertionPoint = Collections.binarySearch(symbols, address);
		if( insertionPoint < 0 ) {
			insertionPoint = -insertionPoint + 1;
		}
		if( insertionPoint >= 0 ) {
			Symbol symbol = symbols.get(insertionPoint);
			if( symbol.start <= address && symbol.end >= address ) {
				return symbol;
			}
		}
		if( insertionPoint+1 < symbols.size() ) {
			Symbol symbol = symbols.get(insertionPoint+1);
			if( symbol.start <= address && symbol.end >= address ) {
				return symbol;
			}
		}
		return null;
	}
	
	public Symbol lookup(long address) {
		int insertionPoint = Collections.binarySearch(symbols, address);
		if( insertionPoint < 0 ) {
			insertionPoint = -insertionPoint - 2;
		}
		if( insertionPoint >= 0 ) {
			Symbol symbol = symbols.get(insertionPoint);
			if( symbol.start <= address && symbol.end >= address ) {
				return symbol;
			}
		}
		if( insertionPoint+1 < symbols.size() ) {
			Symbol symbol = symbols.get(insertionPoint+1);
			if( symbol.start <= address && symbol.end >= address ) {
				return symbol;
			}
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(Runtime.getRuntime().totalMemory()/1024/1024);
		LibrarySymbol librarySymbol = new LibrarySymbol("symbols/xul.sym");
		long start = System.currentTimeMillis();
		for(int i = 0; i < 10000000; i++)
			librarySymbol.lookup("16ee");
		String name = librarySymbol.lookup("16ee").name;
		System.out.println(name);
		System.out.println(System.currentTimeMillis() - start);
		System.out.println(Runtime.getRuntime().totalMemory()/1024/1024);
	}
}
