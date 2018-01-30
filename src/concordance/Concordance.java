package concordance;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Concordance {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		
		Scanner sc = new Scanner( new File("input.txt"), "UTF-8");
		
		PrintWriter writer = new PrintWriter("output.csv", "UTF-8");
		

		
		ArrayList<Lem> theLems = new ArrayList<>();
		Lem curLem = null;
		while(sc.hasNextLine())
		{
			String wholeLine = sc.nextLine();

			
			wholeLine = wholeLine.replaceAll("([A-Za-z])([0-9])", "$1 $2");
			wholeLine = wholeLine.replaceAll("([0-9])([\\?\\(\\)<A-Za-z\\.])", "$1 $2");
			wholeLine = wholeLine.replaceAll("([A-Za-z])([<0-9])", "$1 $2");
			wholeLine = wholeLine.replaceAll("([0-9]+)\\—[0-9]+[;:,\\.]?", "$1");
			wholeLine = wholeLine.replaceAll("</B>\\(", "</B> \\(");
			System.out.println(wholeLine);
			String[] line = wholeLine.split(" ");
			System.out.println(Arrays.toString(line));
			boolean off = false;
			for(int i= 0; i < line.length; ++i){
				String cur = line[i];

				System.out.println("cur " + cur);
				if(cur.equals("<I>bis,</I>")){
					curLem.repeat();
				}
				else if(cur.startsWith("<I")){
					if(!cur.endsWith("</I>")){
						off=true;
					}
				}else if(cur.endsWith("</I>")){
					off=false;
				}else if(off){
					
				}
				else if(cur.startsWith("<B>")){
					while(!cur.endsWith("</B>")){
						i++;
						cur+=line[i];

						System.out.println("cur" + cur);
						
					}
					String lem = cur.substring(3).substring(0,cur.length()-7);
					System.out.println(lem);
					lem = lem.replaceAll("[,;:]", "");
					curLem = new Lem(lem);
					theLems.add(curLem);
					
					
				}else if( cur.matches("[IVXLC]+")){
					String rom = cur.replaceAll("[,;\\.]", "");
					
					System.out.println(rom);
					int book = romanToInt(rom);
					curLem.setBook(book);
				}else if(cur.matches("[0-9].*")){
					System.out.println("trying to get int from " + cur.replaceAll("[,;:\\.]",""));
					int page = Integer.parseInt(cur.replaceAll("[,;\\.:]",""));

					System.out.println(page);
					curLem.addLocation(page);
				}
			}
			System.out.println("done with line");
		}
		System.out.println("done");
		System.out.println(theLems);
		writer.println("Lemma,Note,Form,Book,Verse");
		for(int i = 0; i < theLems.size(); ++i){
			String thisLemsCSV = theLems.get(i).toCSVFile();
			if(thisLemsCSV.length() > 0){

				writer.println(thisLemsCSV);
			}
		}
		System.out.println(theLems.size());
		writer.close();
	}
	
	
	
	static class Lem{
		String lem;
		ArrayList<Integer> books;
		ArrayList<Integer> lines;
		int curBook=0;
		private boolean bookFresh;
		public Lem(String lem){

			books = new ArrayList<>();
			lines = new ArrayList<>();
			this.lem = lem;
		}
		public String toString(){
			String result = lem;
			for(int i = 0; i < books.size(); ++i){
				result += " " + books.get(i) + ":" + lines.get(i);
			}
			return result;
		}
		
		public String toTabFile(){
			String result = lem +"\t\t\t"+books.get(0) + "\t"+lines.get(0);
			for(int i = 1; i < books.size(); ++i){
				result+="\n\t\t\t"+books.get(i) + "\t"+lines.get(i);
			}
			return result;
		}		
		public String toCSVFile(){
			System.out.println(lem + books + " " + lines);
			if(books.size() ==0){
				return "";
			}
			String result = lem +",,,"+books.get(0) + ","+lines.get(0);
			for(int i = 1; i < books.size(); ++i){
				result+="\n"+lem+",,,"+books.get(i) + ","+lines.get(i);
			}
			return result;
		}
		
		public void setBook(int book){
			curBook = book;
			bookFresh=true;
		}
		
		public void addLocation(int line){
			
			if(books.size()>0){
				if(books.get(books.size()-1) == curBook  && !bookFresh){
					int prevLine = lines.get(lines.size()-1);
					if(line<100 && prevLine >=100){
						line+=100*(prevLine/100);
					}
				}
			}
			bookFresh = false;
			books.add(curBook);
			lines.add(line);
		}
		
		public void repeat(){
			books.add(curBook);
			lines.add(lines.get(lines.size()-1));
		}
		
	}
	
	public static  final int[] NUMBER_VALUES = { 1000, 900, 500, 400, 100, 90, 50, 40, 10, 9, 5, 4, 1 }; // array containing all of the values
    public static  final String[] NUMBER_LETTERS = { "M", "CM", "D", "CD", "C", "XC", "L", "XL", "X", "IX", "V", "IV", "I" }; // array containing all of the numerals

    
    public static int romanToInt(String roman){
    	int val = 0;
    	System.out.println("roman " + roman);
    	while(roman.length() > 0){
    		for(int i = 0; i < NUMBER_LETTERS.length; ++i){
    			if(roman.startsWith(NUMBER_LETTERS[i])){
    				val+=NUMBER_VALUES[i];
    				roman = roman.substring(NUMBER_LETTERS[i].length());
    			}
    		}
    	}
    	System.out.println("returing " + val);
    	return val;
    }

}


