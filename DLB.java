package cs1501_p2;
import java.util.ArrayList;

public class DLB implements Dict{
  //Constructor
  final char TERMINATOR = '^';
  final int INITIALSIZE = 15;
  //root node is null and has no right pointer. the down pointer is where the DLB data begins.
  DLBNode root;
  int size;
  boolean unique;
  boolean contain;

  int currsize;
  char[] currword = new char[INITIALSIZE];








  /**
   * Add a new word to the dictionary
   *
   * @param 	key New word to be added to the dictionary
   */
  public void add(String key){
    //method constructors
    String k = key + TERMINATOR;
    if(root==null){
      root = new DLBNode(k.charAt(0));
      DLBNode curr = root;
      for(int i=1; i<k.length(); i++){
        curr.setDown(new DLBNode(k.charAt(i)));
        curr = curr.getDown();
      }
      size++;
    }
    else{

      DLBNode curr = root;
      unique = false;
      for(int i=0; i<k.length(); i++){
        curr = addHelper(curr, k.charAt(i));
        if(unique) size++;
      }
    }

    //next start traversal to id insert


  }
  private DLBNode addHelper(DLBNode x, char k){
    if(x==null){
      if(k==TERMINATOR) unique=true;
      x=new DLBNode(k);
      return x;
    }
    if(x.getLet()==k){
      if(k==TERMINATOR) return x;
       return x.getDown();
     }


    if(x.getDown()==null){
      if(x.getLet()==TERMINATOR){
        if(x.getRight()==null){
          x.setRight(new DLBNode(k));
          return x.getRight();
        }
      }
      if(k==TERMINATOR) unique=true;
      x.setDown(new DLBNode(k));
      return x.getDown();

    }
    if(x.getRight()==null){
      if(k==TERMINATOR) unique=true;
      x.setRight(new DLBNode(k));
      return x.getRight();
    }
    return addHelper(x.getRight(),k);

  }



  /**
   * Check if the dictionary contains a word
   *
   * @param	key	Word to search the dictionary for
   *
   * @return	true if key is in the dictionary, false otherwise
   */
  public boolean contains(String key){

    String k = key;
    DLBNode curr = root;
    contain=true;
    for(int i=0; i<k.length(); i++){
      curr = search(curr, k.charAt(i));
      if(!contain) return false;


    }
    return true;

  }
  private DLBNode search(DLBNode x, char k){
    while(x!=null){
      if(x.getLet()==k) return x.getDown();
      x = x.getRight();
    }
    contain = false;
    return x;
  }


  /**
   * Check if a String is a valid prefix to a word in the dictionary
   *
   * @param	pre	Prefix to search the dictionary for
   *
   * @return	true if prefix is valid, false otherwise
   */
  public boolean containsPrefix(String pre){

    DLBNode curr = root;
    String k = pre;
    contain=true;
    for(int i=0; i<k.length();i++){
      curr = search(curr, k.charAt(i));
      if(!contain) return false;

    }
    if(curr.getLet()==TERMINATOR){
      if(curr.getRight()==null) return false;
    }
    return true;

  }


  /**
   * Search for a word one character at a time
   *
   * @param	next Next character to search for
   *
   * @return	int value indicating result for current by-character search:
   *				-1: not a valid word or prefix
   *				 0: valid prefix, but not a valid word
   *				 1: valid word, but not a valid prefix to any other words
   *				 2: both valid word and a valid prefix to other words
   */
  public int searchByChar(char next){
    DLBNode curr = root;

    contain=true;
    currword[currsize]=next;
    currsize++;
    for(int i=0; i<currsize; i++){
      curr=search(curr,currword[i]);
      if(!contain) return -1;
    }
    if(curr==null) return -1;
    if(curr.getLet()==TERMINATOR){
      if(curr.getRight()==null && curr.getDown()==null) return 1;
      else return 2;
    }
    else if(curr.getDown()!=null){
      return 0;
    }
    else{
      while(curr.getRight()!=null){
        curr = curr.getRight();
        if(curr.getLet()==TERMINATOR) return 2;
      }
      return 0;
    }

    //if node == null ret -1
    //if last char has no terminator ret 0
    //if last char has terminator but no peers ret 1
    //if last char has a terminator and peers ret 2
  }


  /**
   * Reset the state of the current by-character search
   */
  public void resetByChar(){
    char[] newarr = new char[INITIALSIZE];
    currsize=0;
    currword = newarr;
  }


  /**
   * Suggest up to 5 words from the dictionary based on the current
   * by-character search
   *
   * @return	ArrayList<String> List of up to 5 words that are prefixed by
   *			the current by-character search
   */
  public ArrayList<String> suggest(){
    DLBNode pivot = root;
    ArrayList<String> suggestions = new ArrayList<String>();
    String prefix = "";

    for(int i=0; i<currsize; i++){
      pivot=find(pivot, currword[i]);
      prefix+=currword[i];
      pivot=pivot.getDown();
    }

    ArrayList<String> allsugs = getMatches(pivot,prefix);
    for(String k: allsugs){
      if(suggestions.size()<5){
        suggestions.add(k);
      }
      else break;
    }

  return suggestions;
}
  private DLBNode find(DLBNode x, char k){
    while(x!=null){
      if(x.getLet()==k) return x;
      x=x.getRight();
    }

    return x;
  }

  public ArrayList<String> getMatches(DLBNode x, String prefix){
    ArrayList<String> words = new ArrayList<String>();
    collectMatches(x,prefix,words);
    return words;
  }
  private void collectMatches(DLBNode x, String prefix, ArrayList<String> words){
    if(x==null) return;
    if(x.getLet()==TERMINATOR && x.getRight()==null){
      words.add(prefix);
    }
    else if(x.getLet()==TERMINATOR && x.getRight()!=null){
      words.add(prefix);
      x=x.getRight();
    }
    for(DLBNode y=x; y!=null; y=y.getRight()){
      collectMatches(y.getDown(), prefix+y.getLet(), words);
    }
  }





  /**
   * List all of the words currently stored in the dictionary
   * @return	ArrayList<String> List of all valid words in the dictionary
   */
  public ArrayList<String> traverse(){
    return getMatches(root, "");
  }




  /**
   * Count the number of words in the dictionary
   *
   * @return	int, the number of (distinct) words in the dictionary
   */
  public int count(){
    return size;
  }

}
