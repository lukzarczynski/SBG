package main.statistics;

/**
 * Created by lukza on 24.01.2017.
 */
public class FileStatistic {

    private Integer humaLikenessValue;
    private Double rappValue;
    private String filename;

    public Integer getHumaLikenessValue() {
        return humaLikenessValue;
    }

    public void setHumaLikenessValue(Integer humaLikenessValue) {
        this.humaLikenessValue = humaLikenessValue;
    }

    public Double getRappValue() {
        return rappValue;
    }

    public void setRappValue(Double rappValue) {
        this.rappValue = rappValue;
    }

    public String getFile() {
        return filename;
    }

    public void setFile(String file) {
        this.filename = file;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileStatistic that = (FileStatistic) o;

        return filename.equals(that.filename);
    }

    @Override
    public int hashCode() {
        return filename.hashCode();
    }
}
