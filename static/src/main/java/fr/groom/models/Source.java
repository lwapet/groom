package fr.groom.models;

import java.util.List;

public class Source {
  private String sourceType;
  private String sourceSignature;
  private List<String> history;

  public Source(String sourceType, String sourceSignature, List<String> history) {
    this.sourceType = sourceType;
    this.sourceSignature = sourceSignature;
    this.history = history;
  }

  public String getSourceType() {
    return sourceType;
  }

  public String getSourceSignature() {
    return sourceSignature;
  }

  public List<String> getHistory() {
    return history;
  }
}
