package model;

import java.time.LocalDate;

public class Rating {

    private final String reviewerId;
    private final double score;
    private final String comment;
    private final LocalDate createdAt;

    public Rating(String reviewerId, double score, String comment){
        this.reviewerId = reviewerId;
        this.score = Math.max(1.0, Math.min(5.0, score));
        this.comment = comment;
        this.createdAt = LocalDate.now();
    }

    // Getters

    public String getReviewerId(){
        return reviewerId;
    }

    public double getScore(){
        return score;
    }

    public String getComment(){
        return comment;
    }

    public LocalDate getCreatedAt(){
        return createdAt;
    }

    @Override
    public String toString(){
        return String.format("%.1f/5.0 — \"%s\" (%s)", score, comment, createdAt);
    }
}
