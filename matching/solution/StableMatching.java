import java.util.*;

public class StableMatching {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String line;

    while ((line = sc.nextLine()).startsWith("#")) continue;
    
    String firstLine = line.trim().split("=")[1];
    int pairs = Integer.parseInt(firstLine);


    Map<Integer, Deque<Integer>> propPref = new HashMap<>();
    // rejId        propID,     rank
    Map<Integer, Map<Integer, Integer>> rejPref = new HashMap<>();

    String[] names = new String[pairs*2];

    for (int i = 1; i <= pairs * 2; i++) {
      names[i-1] = sc.nextLine().trim().split(" ")[1];
    }

    // empty line
    sc.nextLine();

    for (int i = 1; i <= pairs * 2; i++) {
      int[] idxs = Arrays
        .stream(sc.nextLine().split(":")[1].trim().split(" "))
        .mapToInt(Integer::parseInt).toArray();

      if (i % 2 == 0) {
        // Rejecter == WOMEN        
        Map<Integer, Integer> rankByMan = new HashMap<>();
        for (int j = 0; j < idxs.length; j++) {
          int man = idxs[j];
          int rank = j;
          rankByMan.put(man, rank);
        }
        rejPref.put(i, rankByMan);

      } else {
        // Proposer == MEN
        Deque<Integer> prefs = new ArrayDeque<>();
        for (var idx : idxs) prefs.addLast(idx);

        propPref.put(i, prefs);
      }
    }

    Deque<Integer> propStack = new ArrayDeque<>();
    for (int i = 1; i < names.length; i+=2) {
      propStack.add(i);
    }

    Map<Integer, Integer> matches = new HashMap<>();

    while (!propStack.isEmpty()) {

      int currentProposer = propStack.pop();
      var prefRejecter = propPref.get(currentProposer).pollFirst();

      if (matches.containsKey(prefRejecter)) { 
        
        // check if better match for rejecter

        Map<Integer, Integer> rejRanks = rejPref.get(prefRejecter);

        int currentMatch = matches.get(prefRejecter);
        int currentRank = rejRanks.get(currentMatch);
        
        int alternativeRank = rejRanks.get(currentProposer);

        // if not better match carry on
        if (currentRank < alternativeRank) {
          propStack.push(currentProposer);
        } else {
          // if better match replace
          matches.put(prefRejecter, currentProposer);
          propStack.push(currentMatch);
        } 

      } else { // prefferedRejecter has no match yet
        matches.put(prefRejecter, currentProposer);
      }
    }

    //Printing
    for (int i = 2; i <= pairs * 2; i+=2) {
      int manIndex = matches.get(i);
      System.out.println(names[manIndex-1] + " -- " + names[i-1]);
    }

    
  }
}
