import java.util.*;

public class StableMatching {

  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String line;

    // Skip preamble comments 
    while ((line = sc.nextLine()).startsWith("#")) continue;
    
    String firstLine = line.trim().split("=")[1];
    int pairs = Integer.parseInt(firstLine);

    // Map from proposers to rejectors, queued in order of preference
    Map<Integer, Queue<Integer>> propPref = new HashMap<>();

    // Map from rejecters to proposers with rank R -> (P -> RANK)
    Map<Integer, Map<Integer, Integer>> rejPref = new HashMap<>();

    // Names of rejecters union proposers
    String[] names = new String[pairs*2];

    // Read all the names
    for (int i = 1; i <= pairs * 2; i++)
      names[i-1] = sc.nextLine().trim().split(" ")[1];

    // Discard empty line between names and rankings
    sc.nextLine();

    // Read all the ranks
    for (int i = 1; i <= pairs * 2; i++) {

      // Line is    <Rejecter ID> : <Ranking List>
      String[] thisLine = sc.nextLine().split(":");
      int rejId = Integer.parseInt(thisLine[0]);
      int[] idxs = Arrays
        .stream(thisLine[1].trim().split(" "))
        .mapToInt(Integer::parseInt).toArray();

      // We check if the rejecterID is even or odd, 
      // determining their posistion as rejecter or proposer
      if (rejId % 2 == 0) {    
        Map<Integer, Integer> rankByProp = new HashMap<>();
        for (int j = 0; j < idxs.length; j++) {
          int propId = idxs[j];
          int rank = j;
          rankByProp.put(propId, rank);
        }
        rejPref.put(rejId, rankByProp);
      } else {
        Queue<Integer> prefs = new ArrayDeque<>();
        for (var idx : idxs) prefs.add(idx);

        propPref.put(rejId, prefs);
      }
    }

    // Put all proposers on a stack
    Deque<Integer> propStack = new ArrayDeque<Integer>();
    for (int i = 1; i < names.length; i+=2) {
      propStack.push(i);
    }

    // Keep track of matches R -> P
    Map<Integer, Integer> matches = new HashMap<>();

    // Find the matches
    while (!propStack.isEmpty()) {
      int currentProposer = propStack.pop();
      var prefRejecter = propPref.get(currentProposer).remove();

      // check if better match for rejecter
      if (matches.containsKey(prefRejecter)) {   
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

    // Print the result
    for (int i = 2; i <= pairs * 2; i+=2) {
      int manIndex = matches.get(i);
      System.out.println(names[manIndex-1] + " -- " + names[i-1]);
    }
  }
}
