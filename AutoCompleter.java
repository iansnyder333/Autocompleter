package cs1501_p2;
import java.io.*;
import java.util.*;

public class AutoCompleter implements AutoComplete_Inter{
	String dic;
	String uhis;
	DLB dictionary;
	UserHistory uhistory;
	ArrayList<String> userhist = new ArrayList<String>();



	public AutoCompleter(String dic){
		dic = dic;
		dictionary = new DLB();
		try(Scanner s = new Scanner(new File(dic))){
			while(s.hasNext()){
				dictionary.add(s.nextLine());
			}
			}
			catch(IOException e){
				e.printStackTrace();
			}

		uhistory = new UserHistory();

	}
	
	public AutoCompleter(String dic, String uhis){
		dic = dic;
		uhis = uhis;
		dictionary = new DLB();
		try(Scanner s = new Scanner(new File(dic))){
			while(s.hasNext()){
				dictionary.add(s.nextLine());
			}
			}
			catch(IOException e){
				e.printStackTrace();
			}

		uhistory = new UserHistory();
		try(Scanner n = new Scanner(new File(uhis))){
			while(n.hasNext()){
				uhistory.add(n.nextLine());
			}
		}
		catch(IOException e){
			e.printStackTrace();
		}

	}



  /**
	 * Produce up to 5 suggestions based on the current word the user has
	 * entered These suggestions should be pulled first from the user history
	 * dictionary then from the initial dictionary
	 *
	 * @param 	next char the user just entered
	 *
	 * @return	ArrayList<String> List of up to 5 words prefixed by cur
	 */
	public ArrayList<String> nextChar(char next){
		ArrayList<String> sug = new ArrayList<String>();
		int us = uhistory.searchByChar(next);
		int db = dictionary.searchByChar(next);
		if(us==-1){

			sug = dictionary.suggest();
			return sug;
		}
		sug = uhistory.suggest();

		if(sug.size()==5) return sug;
		ArrayList<String> dic = dictionary.suggest();
		for(String n: dic){
			if(sug.size()<5){
				if(sug.contains(n)==false) sug.add(n);
			}
		}


		return sug;
	}

	/**
	 * Process the user having selected the current word
	 *
	 * @param 	cur String representing the text the user has entered so far
	 */
	public void finishWord(String cur){

		uhistory.add(cur);
		userhist.add(cur);
		dictionary.resetByChar();
		uhistory.resetByChar();


	}




	/**
	 * Save the state of the user history to a file
	 *
	 * @param	fname String filename to write history state to
	 */
	public void saveUserHistory(String fname){
		try{
			BufferedWriter file = new BufferedWriter(new FileWriter(fname, false));
			for(String k: userhist){
				file.write(k);
				file.newLine();
			}
			file.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}

	}


}
