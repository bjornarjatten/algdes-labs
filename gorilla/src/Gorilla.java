import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Gorilla {
  static Map<Pair<String, String>, Integer> table;
  static Map<String, String> animalGenes;

  public static void main(String[] args) throws FileNotFoundException {
    table = getBlosum();
    animalGenes = getAnimalGenes();

    for(var a1 : animalGenes.keySet()) {
      for(var a2 : animalGenes.keySet()) {
        if (a1.equals(a2)) continue;
        var genA = animalGenes.get(a1).trim();
        var genB = animalGenes.get(a2).trim();
        AlignmentSolver solver = new AlignmentSolver(table, genA, genB);
        System.out.println(a1 + "--" + a2 + ": " + solver.getScore());
        System.out.println(solver.getAlignment());
      }
    }

  }

  private static Map<String, String> getAnimalGenes() {
    Scanner sc = new Scanner(System.in);

    Map<String, String> animalGenes = new HashMap<>();

    while(sc.hasNextLine()) {
      String animal = sc.nextLine().split(" ")[0].replace('>', Character.MIN_VALUE);

      String gen = String.join("", 
        sc.nextLine()//,
        //sc.nextLine(),
        //sc.nextLine()
      );

      animalGenes.put(animal, gen);
    }
    sc.close();
    return animalGenes;
  }

  private static Map<Pair<String, String>, Integer> getBlosum() throws FileNotFoundException {
    File blosumFile = new File("../data/BLOSUM62.txt");
    Scanner sc = new Scanner(blosumFile);
    
    String line = sc.nextLine();
    while(line.startsWith("#"))
      line = sc.nextLine();

    Map<Pair<String, String>, Integer> table = new HashMap<>();
    String[] letters = line.trim().split("  ");

    // System.out.print("\t");
    // for (int i = 0; i < letters.length; i++) 
    //   System.out.print(letters[i] + "\t");
    // System.out.println();

    for (int j = 0; j < letters.length; j++) {
      String letter = sc.next();
      // System.out.print(letter + "\t");
      for (int i = 0; i < letters.length; i++) {
        int num = sc.nextInt();
        // System.out.print(num + "\t");
        table.put(new Pair<>(letters[i], letter), num);
      }
      // System.out.println();
    }
    
    sc.close();
    return table;
  }



}

class Pair<A,B> {
  A fst;
  B snd;

  public Pair (A fst, B snd) {
    this.fst = fst;
    this.snd = snd;
  }

  @Override public int hashCode() {
    return ( ( 17 + fst.hashCode() ) << 31 + 5 ) + snd.hashCode();
  }

  @Override public boolean equals(Object other) {
     if (other instanceof Pair<?,?>)
      return this.fst.equals(((Pair<?,?>)other).fst) && 
             this.snd.equals(((Pair<?,?>)other).snd);

    return false;
  }
}

class Step {
  static Step FINISHED = new Step(0,0);
  static Step TAKE_LEFT = new Step(-1,0);
  static Step TAKE_RIGHT = new Step(0,-1);
  static Step TAKE_BOTH = new Step(-1,-1);

  int di, dj;
  public Step(int di, int dj) {this.di = di; this.dj = dj;}
  public Pair<Integer, Integer> apply(Pair<Integer,Integer> state) {
    return new Pair<>(state.fst + di, state.snd + dj);
  }

  public boolean isFinished() {
    return this.di == FINISHED.di && this.dj == FINISHED.dj;
  }
}

class AlignmentSolver {

  private Map<Pair<String, String>, Integer> blosum;
  private Map<Pair<Integer, Integer>, Step> opt = new HashMap<>();
  private Map<Pair<Integer, Integer>, Integer> sco = new HashMap<>();
  private String genA, genB;
  private int delta; // gap cost
  private int score;
  private Pair<String,String> alignmnent;

  public AlignmentSolver(Map<Pair<String, String>, Integer> table, String genA, String genB) {
    this.blosum = table;
    this.delta = blosum.get(new Pair<>("*", "A"));
    this.genA = genA;
    this.genB = genB;
    // this call has the side-effect of populating the steps
    this.score = solve(genA.length()-1, genB.length()-1);
    // so now we can construct the alignment
    computeAlignment(
      pair(
        genA.length()-1, 
        genB.length()-1
      ),
      new StringBuilder(),
      new StringBuilder(),
      true
    );
  }

  public int getScore() {
    return score;
  }

  public String getAlignment() {
    return alignmnent.fst + "\n" + alignmnent.snd;
  }

  private void computeAlignment (Pair<Integer, Integer> ij, StringBuilder aSb, StringBuilder bSb, boolean setField) {
    var step = opt.get(ij);
    if (step.isFinished()) return;

    computeAlignment(step.apply(ij), aSb, bSb, false);

    var lChar = step.di != 0 ? genA.charAt(ij.fst) : '-';
    var rChar = step.dj != 0 ? genB.charAt(ij.snd) : '-';

    aSb.append(lChar);
    bSb.append(rChar);

    if (setField) {
      this.alignmnent = new Pair<>(
        aSb.toString(),
        bSb.toString() 
      );
    }
  }

  private int solve(int i, int j) {
    var ij = pair(i,j);

    if (!sco.containsKey(ij)){
      if (i == -1 && j == -1) {
        opt.put(ij, Step.FINISHED);
        sco.put(ij, 0);
      } else if (i == 0 && j == 0) {
        int cur = cost(i, j);
        opt.put(ij, Step.TAKE_BOTH);
        sco.put(ij, cur + solve(i-1,j-1));
      } else if (i == -1) {
        opt.put(ij, Step.TAKE_RIGHT);
        sco.put(ij, delta + solve(i,j-1));
      } else if (j == -1) {
        opt.put(ij, Step.TAKE_LEFT);
        sco.put(ij, delta + solve(i-1,j));
      } else {

        int left = delta + solve(i-1, j);
        int both = cost(i,j) + solve(i-1, j-1);                    
        int right = delta + solve(i, j-1);

        if (both >= left && both >= right) {
          opt.put(ij, Step.TAKE_BOTH);
          sco.put(ij, both);
        } else if (left >= both && left >= both) {
          opt.put(ij, Step.TAKE_LEFT);
          sco.put(ij, left);
        } else { // right is only opt left
          opt.put(ij, Step.TAKE_RIGHT);
          sco.put(ij, right);
        }
      }
    }

    return sco.get(ij);
  }

  private int cost(int i, int j) {
    return blosum.get(new Pair<>(""+genA.charAt(i), ""+genB.charAt(j)));
  }

  private static Pair<Integer,Integer> pair(int i, int j) {
    return new Pair<>(i,j);
  }
  
}
