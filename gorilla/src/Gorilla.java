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
    return ( fst.hashCode() << 31 + 5 ) + snd.hashCode();
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
  static Map<Pair<Integer, Integer>, Integer> cache = new HashMap<>();

  public static void main(String[] args) throws FileNotFoundException {
    table = getBlosum();
    animalGenes = getAnimalGenes();

    for(var a1 : animalGenes.keySet()) {
      for(var a2 : animalGenes.keySet()) {
        if (a1.equals(a2)) continue;
        var genA = animalGenes.get(a1);
        var genB = animalGenes.get(a2);
        int score = optimal(genA, genA.length()-1, genB, genB.length()-1);
        System.out.println(a1 + "--" + a2 + ": " + score);
      }
    }

  }

  private static Map<String, String> getAnimalGenes() {
    Scanner sc = new Scanner(System.in);

    Map<String, String> animalGenes = new HashMap<>();

    while(sc.hasNextLine()) {
      String animal = sc.nextLine().split(" ")[0].replace('>', Character.MIN_VALUE);

      String gen = String.join("", 
        sc.nextLine()
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

    for (int j = 1; j < letters.length; j++) {
      String letter = sc.next();
      for (int i = 0; i < letters.length; i++) {
        int num = sc.nextInt();
        table.put(new Pair<>(letters[i], letter), num);
      }
    }
    sc.close();
    return table;
  }

  private static int optimal(String genA, int i, String genB, int j) {
    var delta = table.get(new Pair<>("*", "A"));
    if (!cache.containsKey(new Pair<>(i,j))){

      if (i == 0 && j == 0) {
        cache.put(new Pair<>(i,j), 0);
        return 1;
      } else if (i == 0) {
        cache.put(new Pair<>(i,j), delta * j);
        return delta;
      } else if (j == 0) {
        cache.put(new Pair<>(i,j), delta * i);
      } else {
        int matching = table.get(new Pair<>(""+genA.charAt(i), ""+genB.charAt(j))) 
                        + optimal(genA, i-1, genB, j-1);                    

        int dropA = delta + optimal(genA, i-1, genB, j);
        int dropB = delta + optimal(genA, i, genB, j-1);

        cache.put(new Pair<>(i,j), Math.max(matching, Math.max(dropA, dropB)));
      }
    }
    return cache.get(new Pair<>(i,j));
  }

}
