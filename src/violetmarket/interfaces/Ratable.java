package violetmarket.interfaces;

public interface Ratable {
    void addRating(double score, String comment, String reviewerId);
    double getAverageRating();
    int getRatingCount();
}
