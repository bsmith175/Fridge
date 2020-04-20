package src.main.java.edu.brown.cs.teams.state;

import java.util.HashMap;
import java.util.Map;

public class Config {

  private Map<String, Double> catToVal = new HashMap();
  private static int embedLength = 0;
  private Map<String, double[]> recToVec = new HashMap<>();

  public Config(){
    catToVal.put("meat", Double.POSITIVE_INFINITY);
    catToVal.put("fish", 100.0);
    catToVal.put("seafood", 100.0);
    catToVal.put("leaf", 10.0);
    catToVal.put("vegmain", 80.0);
    catToVal.put("vegflav", 70.0);
    catToVal.put("fruits", 60.0);
    catToVal.put("carbs", 200.0);
    catToVal.put("dairy", 40.0);
    catToVal.put("beans", 10.0);
    catToVal.put("baking", 500.0);
    catToVal.put("fats", 30.0);
    catToVal.put("spices", 5.0);
    catToVal.put("nuts", 15.0);
    catToVal.put("sauces", 100.0);
  }

  public Map getCatToVal() {
    return catToVal;
  }

  public static double cosineSimilarity(double[] vec1, double[] vec2) {
    double dotProduct = 0.0;
    double normA = 0.0;
    double normB = 0.0;
    for (int i = 0; i < vec1.length; i++) {
      dotProduct += vec1[i] * vec2[i];
      normA += Math.pow(vec1[i], 2);
      normB += Math.pow(vec2[i], 2);
    }
    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
  }

  public static double[] arrayAdd(double[][] arrays) {
    double[] result = new double[300];
    for (int i = 0; i < result.length; i ++){
      for (double[] currArr: arrays) {
        result[i] += currArr[i];
      }
    }
    for (int i = 0; i < result.length; i ++) {
      result[i] /= arrays.length;
    }
    return result;
  }

  public static int getEmbedLength() {
    return embedLength;
  }

  public double[] getRecVec(String id) {
    return recToVec.get(id);
  }
}
