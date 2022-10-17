import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


/**
 * . description:
 *
 * @Param $PARAMS$ $RETURN$
 */
public class MovieAnalyzer {

  /**
   * . description:
   *
   * @Param $PARAMS$ $RETURN$
   */
  public static void main(String[] args) {
    MyInterface ref = () -> 3.1415;
    System.out.println("Pi = " + ref.getPiValue());

    List<String> strList = new ArrayList<String>();
    strList.add("abc");
    strList.add("bcd");
    //print every element in the list
    strList.forEach(elem -> System.out.println(elem));
  }

  BufferedReader br;
  int number = 0;
  String[][] movies = new String[100000][16];

  /**
   * . description:
   *
   * @Param $PARAMS$ $RETURN$
   */
  public MovieAnalyzer(String dataset_path) {
    try {
      InputStreamReader fReader = new InputStreamReader(new FileInputStream(dataset_path), "UTF-8");
      br = new BufferedReader(fReader);

      String line;
      br.readLine();
      int cnt = 0;
      while ((line = br.readLine()) != null) {
        number++;
        List<String> result = CSVReader.parseLine(line,',','"');
        movies[++cnt][0] = result.get(0);
        for (int i = 1; i < 16; i++) {
          movies[cnt][i] = result.get(i);
        }
      }
      br.close();
    } catch (Exception e) {
      System.out.println("fuck");

    }
  }

  /**
   * . description:
   *
   * @Param $PARAMS$ $RETURN$
   */
  public Map<Integer, Integer> getMovieCountByYear() {
    Map<Integer, Integer> map = new HashMap<>();
    for (int i = 1; i <= number; i++) {
      Integer year = Integer.parseInt(movies[i][2]);
      if (map.containsKey(year)) {
        map.put(year, map.get(year) + 1);
      } else {
        map.put(year, 1);
      }
    }
    TreeMap<Integer, Integer> sortedMap = new TreeMap<>(Comparator.reverseOrder());
    sortedMap.putAll(map);
    return sortedMap;
  }

  /**
   * . description:
   *
   * @Param $PARAMS$ $RETURN$
   */
  public static <K extends Comparable, V extends Comparable> Map<K, V> sortMapByValues(
      Map<K, V> aMap) {
    HashMap<K, V> finalOut = new LinkedHashMap<>();
    aMap.entrySet()
        .stream()
        .sorted(
            (p1, p2) -> (p2.getValue().equals(p1.getValue())) ? p1.getKey().compareTo(p2.getKey())
                : p2.getValue().compareTo(p1.getValue()))
        .collect(Collectors.toList()).forEach(ele -> finalOut.put(ele.getKey(), ele.getValue()));
    return finalOut;
  }

  /**
   * . description:
   *
   * @Param $PARAMS$ $RETURN$
   */
  public Map<String, Integer> getMovieCountByGenre() {
    Map<String, Integer> map = new TreeMap<>(Comparator.reverseOrder());
    for (int i = 1; i <= number; i++) {
      String genres = movies[i][5];
      String[] genre = genres.split(", ");
      for (int j = 0; j < genre.length; j++) {
        if (map.containsKey(genre[j])) {
          map.put(genre[j], map.get(genre[j]) + 1);
        } else {
          map.put(genre[j], 1);
        }
      }


    }
    System.out.println(map);
    LinkedHashMap<String, Integer> sorted = new LinkedHashMap<>(sortMapByValues(map));
    return sorted;
  }
  /**.
   * description:

   * @Param $PARAMS$
  $RETURN$
   */
  public Map<List<String>, Integer> getCoStarCount() {
    Map<List<String>, Integer> map = new HashMap<>();
    for (int i = 1; i <= number; i++) {
      for (int j = 10; j <= 12; j++) {
        for (int k = j + 1; k <= 13; k++) {
          List<String> co = new ArrayList<>();
          co.add(movies[i][j]);
          co.add(movies[i][k]);
          co.sort(Comparator.naturalOrder());
          boolean has = false;
          for (List<String> key : map.keySet()) {
            if (key.get(0).equals(co.get(0)) && key.get(1).equals(co.get(1))) {
              has = true;
              map.put(key, map.get(key) + 1);
              break;
            }
          }
          if (!has) {
            map.put(co, 1);
          }
        }
      }
    }

    return map;
  }
  /**.
   * description:

   * @Param $PARAMS$
  $RETURN$
   */
  public static <K extends Comparable, V extends Comparable> Map<K, V> sortMapByValues_forGetTop(
      Map<K, V> aMap, String[][] movies) {
    HashMap<K, V> finalOut = new LinkedHashMap<>();
    aMap.entrySet()
        .stream()
        .sorted((p1, p2) -> (p2.getValue().equals(p1.getValue()))
            ? movies[(int) p1.getKey()][1].compareTo(movies[(int) p2.getKey()][1])
            : p2.getValue().compareTo(p1.getValue()))
        .collect(Collectors.toList()).forEach(ele -> finalOut.put(ele.getKey(), ele.getValue()));
    return finalOut;
  }
  /**.
   * description:

   * @Param $PARAMS$
  $RETURN$
   */
  public List<String> getTopMovies(int top_k, String by) {
    Map<Integer, Integer> map = new HashMap<>();
    if (by.equals("runtime")) {
      for (int i = 1; i <= number; i++) {
        map.put(i, Integer.parseInt(movies[i][4].split(" min")[0]));
      }
    } else if (by.equals("overview")) {
      for (int i = 1; i <= number; i++) {
        map.put(i, movies[i][7].length());
        for (int j = 0; j <movies[i][7].length() ; j++) {
          if(movies[i][7].charAt(j)=='"'){
            map.put(i,map.get(i)+1);
          }
        }
      }
    }
    LinkedHashMap<Integer, Integer> sorted = new LinkedHashMap<>(
        sortMapByValues_forGetTop(map, movies));
    List<String> top = new ArrayList<>();
    int cnt = 0;
    for (Integer key : sorted.keySet()) {
      top.add(movies[key][1]);
      if (++cnt == top_k) {
        break;
      }
    }
    return top;
  }

  public List<String> getTopStars(int top_k, String by)
  {
    Map<String, Double> map = new HashMap<>();
    Map<String, Integer> map_num = new HashMap<>();
    for (int i = 1; i <= number; i++) {
      for (int j = 10; j <= 13; j++) {
        String star = movies[i][j];
        if (map_num.containsKey(star))
          map_num.put(star, map_num.get(star) + 1);
        else
          map_num.put(star, 1);
        if(by.equals("rating")) {
          Float rating=Float.parseFloat(movies[i][6]);
          if (map.containsKey(star))
            map.put(star, map.get(star) + rating);
          else
            map.put(star, Double.valueOf(rating));
        }

        else if(by.equals("gross")){
          String[] money=movies[i][15].split(",");
          String gross="";
          for(String mon:money)
          {
            gross+=mon;
          }
          if(!gross.equals("")) {
            Float Gross = Float.parseFloat(gross);
            if (map.containsKey(star))
              map.put(star, map.get(star) + Gross);
            else
              map.put(star, Double.valueOf(Gross));
          }
          else
          {
            map_num.put(star, map_num.get(star)-1 );
          }
        }

      }

    }
    Map<String, Double> newmap = new HashMap<>();
    for(String key: map.keySet())
    {
      newmap.put(key,(map.get(key).doubleValue()/map_num.get(key)));
    }
    LinkedHashMap<String, Double> sorted = new LinkedHashMap<>(sortMapByValues(newmap));
    List<String> top=new ArrayList<>();
    for(String name:sorted.keySet())
    {
      top.add(name);
      if(--top_k==0) break;
    }
    return top;
  }

  public List<String> searchMovies(String genre, float min_rating, int max_runtime)
  {
    List<String> list=new ArrayList<>();
    for (int i = 1; i <=number ; i++) {
      String[] genres=movies[i][5].split(", ");
      for(String a:genres)
      {
        if(a.equals(genre))
        {
          if(Float.parseFloat(movies[i][6])>=min_rating && Integer.parseInt(movies[i][4].split(" min")[0])<=max_runtime)
            list.add(movies[i][1]);
          break;
        }
      }

    }
    list.sort(Comparator.naturalOrder());
    return list;
  }

  /**.
   * description:

   * @Param $PARAMS$
  $RETURN$
   */
  public interface MyInterface {

    // abstract method
    double getPiValue();
  }

}


class CSVReader {

  public static void main(String[] args) throws Exception {
  }
  public static List<String> parseLine(String line, char s, char c) {
    List<String> r = new ArrayList<>();
    if (line.isEmpty()  && line == null)
    {
      return r;
    }
    StringBuffer cval = new StringBuffer();
    boolean in = false;
    boolean star = false;
    boolean quo = false;
    char[] chars = line.toCharArray();
    for (char h : chars)
    {
      if (in)
      {
        star = true;
        if (h == c)
        {
          in = false;
          quo = false;
        }
        else
        {
          if (h == '\"')
          {
            if (!quo)
            {
              cval.append(h);
              quo = true;
            }
          }
          else
          {
            cval.append(h);
          }

        }
      }
      else
      {
        if (h == c)
        {
          in = true;
          if (chars[0] != '"' && c == '\"')
          {
            cval.append('"');
          }
          if (star)
          {
            cval.append('"');
          }
        }
        else if (h == s)
        {
          r.add(cval.toString());
          cval = new StringBuffer();
          star = false;
        }
        else if (h == '\r')
        {
          continue;
        }
        else if (h == '\n')
        {
          break;
        }
        else
        {
          cval.append(h);
        }
      }
    }
    r.add(cval.toString());
    return r;
  }

}