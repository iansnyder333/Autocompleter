package cs1501_p2;
import java.util.*;
import java.io.*;



public class UserHistory implements Dict{
  //Constructor
  final char TERMINATOR = '^';
  //root node is null and has no right pointer. the down pointer is where the DLB data begins.
  UHNode root;
  int size;
  boolean unique;
  boolean contain;
  ArrayList<Character> word = new ArrayList<Character>();
  HashSet<String> allwords = new HashSet<String>();


  /**
   * Add a new word to the dictionary
   *
   * @param 	key New word to be added to the dictionary
   */
  public void add(String key){
    String k = key+TERMINATOR;
    allwords.add(key);
    if(root==null){
      root = new UHNode(k.charAt(0));
      UHNode curr = root;
      for(int i=1; i<k.length(); i++){
        curr.down = new UHNode(k.charAt(i));
        curr = curr.down;
      }

    }
    else{
      UHNode curr = root;
      unique = false;
      for(int i=0; i<k.length(); i++){
        curr = addHelper(curr, k.charAt(i));
        if(unique) size++;
      }
    }
  }
    private UHNode addHelper(UHNode x, char k){
      if(x==null){
        if(k==TERMINATOR) unique=true;
        x=new UHNode(k);
        return x;
      }
      if(x.let==k){
        x.freq++;

        if(k==TERMINATOR) return x;
        return x.down;
      }
      if(x.down==null){
        if(x.let == TERMINATOR){
          if(x.right==null){
            x.right = new UHNode(k);
            return x.right;
          }
        }
        if(k==TERMINATOR) unique=true;
        x.down = new UHNode(k);
        return x.down;
      }
      if(x.right==null){
        if(k==TERMINATOR) unique=true;
        x.right = new UHNode(k);
        return x.right;
      }
      return addHelper(x.right,k);
    }





  /**
   * Check if the dictionary contains a word
   *
   * @param	key	Word to search the dictionary for
   *
   * @return	true if key is in the dictionary, false otherwise
   */
  public boolean contains(String key){
    return allwords.contains(key);
  }

  private UHNode search(UHNode x, char k){
    while(x!=null){
      if(x.let==k) return x.down;
      x=x.right;
    }
    contain=false;
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
    UHNode curr = root;
    String k = pre;
    contain=true;
    for(int i=0; i<k.length();i++){
      curr = search(curr, k.charAt(i));
      if(!contain) return false;
    }
    if(curr.let==TERMINATOR){
      if(curr.right==null)return false;
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
    UHNode curr = root;
    contain=true;
    word.add(next);
    for(int i=0; i<word.size(); i++){
      curr = search(curr, word.get(i));
      if(!contain) return -1;
    }
    if(curr==null) return -1;
    if(curr.let==TERMINATOR){
      if(curr.right==null && curr.down==null) return 1;
      else return 2;
    }
    else if(curr.down!=null){
      return 0;
    }
    else{
      while(curr.right!=null){
        curr = curr.right;
        if(curr.let==TERMINATOR) return 2;
      }
      return 0;
    }


  }

  /**
   * Reset the state of the current by-character search
   */
  public void resetByChar(){
    word.clear();
  }

  /**
   * Suggest up to 5 words from the dictionary based on the current
   * by-character search
   *
   * @return	ArrayList<String> List of up to 5 words that are prefixed by
   *			the current by-character search
   */
  public ArrayList<String> suggest(){
    ArrayList<String> sug = new ArrayList<String>();
    UHNode pivot=root;
    String prefix="";
    for(int i=0; i<word.size(); i++){
      pivot = find(pivot, word.get(i));
      prefix+=word.get(i);
      pivot=pivot.down;
    }

    //pivot = node before suggestion
    //base = word including pivot.let
    //if(pivot.down!=null) pivot=pivot.down;
    Map<String,Integer> possibilities = getMatches(pivot,prefix);
    List<Map.Entry<String, Integer>> allpossibilities = new ArrayList<>(possibilities.entrySet());
    Comparator<Map.Entry<String, Integer>> compare = Map.Entry.comparingByValue();
    allpossibilities.sort(compare);
    for(int i=allpossibilities.size()-1; i>=0; i--){
      if(sug.size()<5){
        Map.Entry<String,Integer> topsug = allpossibilities.get(i);
        sug.add(topsug.getKey());
      }
      else break;
    }
    return sug;
  }

    private UHNode find(UHNode x, char k){
      while(x!=null){
        if(x.let==k) return x;
        x=x.right;
      }
      return x;
    }

    public Map<String, Integer> getMatches(UHNode x, String prefix){

      Map<String,Integer> q = new HashMap<>();
      collect(x,prefix,q);
      return q;
    }

    private void collect(UHNode x, String prefix, Map<String,Integer> q){
      if(x==null) return;
      if(x.let==TERMINATOR && x.right==null){

         q.put(prefix,x.freq);
      }
      else if(x.let==TERMINATOR && x.right !=null){
        //q.add(prefix);
        q.put(prefix,x.freq);
        x=x.right;
      }
      for(UHNode y=x; y!=null; y=y.right){
        collect(y.down,prefix+y.let,q);
      }
    }

  /**
   * List all of the words currently stored in the dictionary
   * @return	ArrayList<String> List of all valid words in the dictionary
   */
  public ArrayList<String> traverse(){
    ArrayList<String> wo = new ArrayList<String>();
    Map<String,Integer> possibilities = getMatches(root,"");
    for(String k: possibilities.keySet()){
      wo.add(k);
    }
    return wo;
  }

  /**
   * Count the number of words in the dictionary
   *
   * @return	int, the number of (distinct) words in the dictionary
   */
  public int count(){
    return allwords.size();
  }
}

class UHNode{
  char let;
  int freq;
  UHNode right;
  UHNode down;

  public UHNode(char let){
    this.let=let;
    this.freq=1;
    this.right=null;
    this.down=null;
  }
}
