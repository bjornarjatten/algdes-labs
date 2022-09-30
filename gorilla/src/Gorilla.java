import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Pair<A,B> {
  private A fst;
  private B snd;

  public Pair (A fst, B snd) {
    this.fst = fst;
    this.snd = snd;
  }

  public Pair<A,B> of(A fst, B snd) {
    return new Pair<A,B>(fst,snd);
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

class AlignmentSolver {
  private Map<Pair<String, String>, Integer> table;
  private Map<Pair<Integer, Integer>, Integer> cache = new HashMap<>();
  private int solution;
  private StringBuilder genAString;
  private StringBuilder genBString;

  public AlignmentSolver(Map<Pair<String, String>, Integer> table, String genA, String genB) {
    this.table = table;
    this.genAString = new StringBuilder();
    this.genBString = new StringBuilder();
    this.solution = solve(genA, genA.length()-1, genB, genB.length()-1);
  }

  private int solve(String genA, int i, String genB, int j) {
    var delta = table.get(new Pair<>("*", "A"));
    if (!cache.containsKey(new Pair<>(i,j))){

      if (i == 0 && j == 0) {
        cache.put(
          new Pair<>(i,j),
          table.get(new Pair<>(""+genA.charAt(i), ""+genB.charAt(j))));
          genAString.append(genA.charAt(i));
          genBString.append(genA.charAt(j));
      } else if (i == 0) {
        int cur = table.get(new Pair<>(""+genA.charAt(i), ""+genB.charAt(j)));
        int rest = delta * j;

        genAString.append(genA.charAt(i));
        genBString.append(genB.charAt(j));

        while (--j >= 0) {
          genAString.append('-');
          genBString.append(genB.charAt(j));
        }

        cache.put(new Pair<>(i, j), cur + rest);
      } else if (j == 0) {
        int cur = table.get(new Pair<>(""+genA.charAt(i), ""+genB.charAt(j)));
        int rest = delta * i;

        genAString.append(genA.charAt(i));
        genBString.append(genB.charAt(j));
        
        while (--i >= 0) {
          genAString.append(genA.charAt(i));
          genBString.append('-');
        }

        cache.put(new Pair<>(i,j), cur + rest);
      } else {
        int matching = table.get(new Pair<>(""+genA.charAt(i), ""+genB.charAt(j))) 
                        + solve(genA, i-1, genB, j-1);                    

        int dropA = delta + solve(genA, i-1, genB, j);
        int dropB = delta + solve(genA, i, genB, j-1);

        if (matching >= dropA && matching >= dropB) { // length was i+1,j+1 - must have appended max(i,j) chars to sA sB
          int appendedPerBranch = Math.max(i,j);
          genAString.delete(appendedPerBranch, genAString.length());
          genBString.delete(appendedPerBranch, genBString.length());
          genAString.append(""+genA.charAt(i));
          genBString.append(""+genB.charAt(j));
        } else if (dropA >= matching && dropA >= dropB) {
          int appendedPerBranch = Math.max(i-1,j);
          genAString.delete(0, appendedPerBranch);
          genAString.delete(appendedPerBranch*2, genAString.length());
          genAString.append(genA.charAt(i));

          genBString.delete(appendedPerBranch*2, genBString.length());
          genBString.delete(0, appendedPerBranch);
          genBString.append('-');
        } else {
          int appendedPerBranch = Math.max(i,j-1);
          genAString.delete(0, appendedPerBranch*2);
          genBString.delete(0, appendedPerBranch*2);
          genAString.append('-');
          genBString.append(genB.charAt(j));         
        }


        cache.put(new Pair<>(i,j), Math.max(matching, Math.max(dropA, dropB)));
      }
    }

    return cache.get(new Pair<>(i,j));
  }

  public int getScore() {
    return this.solution;
  } 

  public String getAlignment() {
    return this.genAString.toString() + "\n" + this.genBString.toString();
  }

}
