import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Pair<A,B> {
  private A fst;
  public A getFst() {
    return fst;
  }

  private B snd;

  public B getSnd() {
    return snd;
  }

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
        // sc.nextLine(),
        // sc.nextLine()
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

    for (int j = 0; j < letters.length; j++) {
      String letter = sc.next();
      for (int i = 0; i < letters.length; i++) {
        int num = sc.nextInt();
        table.put(new Pair<>(letters[i], letter), num);
      }
    }
    
    sc.close();
    return table;
  }



}

class AlignmentSolver {
  private Map<Pair<String, String>, Integer> table;
  private Map<Pair<Integer, Integer>, Pair<Integer, Pair<StringBuilder, StringBuilder>>> cache = new HashMap<>();
  private int solution;
  private String a;
  private String b;

  public AlignmentSolver(Map<Pair<String, String>, Integer> table, String genA, String genB) {
    this.table = table;
    var y = solve(genA, genA.length()-1, genB, genB.length()-1);
    this.solution = y.getFst();
    this.a = y.getSnd().getFst().toString();
    this.b = y.getSnd().getSnd().toString();
  }

  private Pair<Integer, Pair<StringBuilder, StringBuilder>> solve(String genA, int i, String genB, int j) {
    var delta = table.get(new Pair<>("*", "A"));
    if (!cache.containsKey(new Pair<>(i,j))){

      if (i == 0 && j == 0) {
        var x = new Pair<>(
          new StringBuilder().append(genA.charAt(i)), 
          new StringBuilder().append(genB.charAt(j))
        );
        var y = new Pair<>(
          table.get(new Pair<>(""+genA.charAt(i), ""+genB.charAt(j))),
          x
        );
        cache.put(
          new Pair<>(i,j),
          y
        );
      } else if (i == 0) {
        int cur = table.get(new Pair<>(""+genA.charAt(i), ""+genB.charAt(j)));
        int rest = delta * j;

        var genAString = new StringBuilder().append(genA.charAt(i));
        var genBString = new StringBuilder().append(genB.charAt(j));

        while (--j >= 0) {
          genAString.append('-');
          genBString.append(genB.charAt(j));
        }
        var x = new Pair<>(
          genAString,
          genBString
        );
        var y = new Pair<>(
          cur + rest,
          x
        );
        cache.put(
          new Pair<>(i, j), 
          y
        );
      } else if (j == 0) {
        int cur = table.get(new Pair<>(""+genA.charAt(i), ""+genB.charAt(j)));
        int rest = delta * i;

        var genAString = new StringBuilder(genA.charAt(i));
        var genBString = new StringBuilder(genB.charAt(j));
        
        while (--i >= 0) {
          genAString.append(genA.charAt(i));
          genBString.append('-');
        }

        var x = new Pair<>(
          genAString,
          genBString
        );
        var y = new Pair<>(
          cur + rest,
          x
        );

        cache.put(
          new Pair<>(i,j), 
          y
        );
      } else {
        var matchingSol = solve(genA, i-1, genB, j-1);
        int matching = table.get(new Pair<>(""+genA.charAt(i), ""+genB.charAt(j))) 
                        + matchingSol.getFst();                    

        var dropASol = solve(genA, i-1, genB, j);
        int dropA = delta + dropASol.getFst();
        
        var dropBSol = solve(genA, i, genB, j-1);
        int dropB = delta + dropBSol.getFst();

        Pair<Integer, Pair<StringBuilder, StringBuilder>> value;
        if (matching >= dropA && matching >= dropB) {
          var aa = matchingSol.getSnd().getFst().append(genA.charAt(i));
          var bb = matchingSol.getSnd().getSnd().append(genB.charAt(j));
          value = new Pair<>(
            matching,
            new Pair<>(aa, bb)
          );
        } else if (dropA >= matching && dropA >= dropB) {
          var aa = dropASol.getSnd().getFst().append(genA.charAt(i));
          var bb = dropASol.getSnd().getSnd().append('-');
          value = new Pair<>(
            dropA,
            new Pair<>(aa, bb)
          );
        } else {
          var aa = dropBSol.getSnd().getFst().append('-');
          var bb = dropBSol.getSnd().getSnd().append(genB.charAt(j));
          value = new Pair<>(
            dropB,
            new Pair<>(aa, bb)
          );     
        }

        var aa = value.getSnd().getFst();
        var bb = value.getSnd().getSnd();
        System.out.println(aa.toString());
        System.out.println(bb.toString());


        cache.put(
          new Pair<>(i,j), 
          value
        );
      }
    }

    return cache.get(new Pair<>(i,j));
  }

  public int getScore() {
    return this.solution;
  } 

  public String getAlignment() {
    return this.a + "\n" + this.b;
  }

}
