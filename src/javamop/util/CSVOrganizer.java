package javamop.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.TreeSet;


public class CSVOrganizer {

	
	
	static public void printHelp(){
		System.out.println("[Usage] Provide files as arguments.");
	}
	
	static public void main(String[] args){
		if(args.length < 1){
			printHelp();
			return;
		}
		
		ArrayList<CSVFile> files = new ArrayList<CSVFile>();
		
		for(String fileName : args){
			File file = new File(fileName);
			
			if(!file.exists()){
				System.err.println(fileName + " does not exist.");
				return;
			}
			
			try{
				files.add(new CSVFile(file));
			} catch (Exception e){
				System.err.println(fileName + " cannot be read.");
			}
		}
		
		TreeSet<String> programs = new TreeSet<String>();
		
		for(CSVFile csv : files){
			programs.addAll(csv.table.keySet());
		}

		for(CSVFile csv : files){
			System.out.println(csv.name);
		}

		
		for(CSVFile csv : files){
			System.out.print(csv.name);
			
			for(String prog : programs){
				System.out.print("," + prog);
				
				for(Integer number : csv.table.get(prog)){
					System.out.print(", " + number);
				}
				
				System.out.println();
			}
		}
		
	}
}


class CSVFile{
	public String name;
	
	public TreeMap<String, ArrayList<Integer>> table = new TreeMap<String, ArrayList<Integer>>(); 
	
	CSVFile(File file) throws IOException{
		String filename = file.getName();
		this.name = filename.substring(0, filename.lastIndexOf("."));
		
		String contents = Tool.convertFileToString(file);
		
		String[] rows = contents.split("[\\r\\n]+");
		
		for(String row : rows)
			processRow(row);
		
	}
	
	private void processRow(String row){
		String[] cols = row.split(",");

		if(cols.length < 1)
			return;
		
		String program = cols[0].trim();
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		
		table.put(program, numbers);
		
		for(int i = 1; i < cols.length; i++){
			numbers.add(Integer.parseInt(cols[i].trim()));
		}
	}
}
