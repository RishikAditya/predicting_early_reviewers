package com.earlyreviewer.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * Reviewer aggregates statistics about a single reviewer across multiple reviews.
 * Computes derived properties such as experience level and average review time.
 */
public class Reviewer {
    private final String reviewerId;
    private final List<ReviewRecord> records;
    private double averageTimeToReview;
    private boolean experienced;
    private boolean teamFamiliarity;
    private boolean contextualKnowledge;
    private boolean earlyReviewer;
    private String explanation;

    public Reviewer(String reviewerId) {
        this.reviewerId = reviewerId;
        this.records = new ArrayList<>();
        this.averageTimeToReview = 0.0;
        this.experienced = false;
        this.teamFamiliarity = false;
        this.contextualKnowledge = false;
        this.earlyReviewer = false;
        this.explanation = "";
    }

    /**
     * Adds a review record to this reviewer's history.
     */
    public void addRecord(ReviewRecord record) {
        records.add(record);
    }

    /**
     * Computes average time-to-review from valid records.
     * Sets averageTimeToReview to 0.0 if no valid records exist.
     */
    public void computeStatistics() {
        List<Long> validTimes = new ArrayList<>();
        for (ReviewRecord record : records) {
            if (record.isValid()) {
                validTimes.add(record.getTimeToReview());
            }
        }

        if (validTimes.isEmpty()) {
            this.averageTimeToReview = 0.0;
        } else {
            long sum = 0;
            for (long time : validTimes) {
                sum += time;
            }
            this.averageTimeToReview = (double) sum / validTimes.size();
        }

        // Determine experience level: >= 5 reviews = experienced
        this.experienced = getPastReviewsCount() >= 5;

        // Set familiarity and knowledge flags (true if any record has them)
        for (ReviewRecord record : records) {
            if (record.isTeamFamiliarity()) {
                this.teamFamiliarity = true;
            }
            if (record.isContextualKnowledge()) {
                this.contextualKnowledge = true;
            }
        }
    }

    // Getters
    public String getReviewerId() {
        return reviewerId;
    }

    public List<ReviewRecord> getRecords() {
        return new ArrayList<>(records);
    }

    public double getAverageTimeToReview() {
        return averageTimeToReview;
    }

    public boolean isExperienced() {
        return experienced;
    }

    public boolean hasTeamFamiliarity() {
        return teamFamiliarity;
    }

    public boolean hasContextualKnowledge() {
        return contextualKnowledge;
    }

    public boolean isEarlyReviewer() {
        return earlyReviewer;
    }

    public String getExplanation() {
        return explanation;
    }

    public int getPastReviewsCount() {
        // Use the most recent record's pastReviewsCount (should be consistent per reviewer)
        if (records.isEmpty()) {
            return 0;
        }
        return records.get(0).getPastReviewsCount();
    }

    public int getValidRecordCount() {
        int count = 0;
        for (ReviewRecord record : records) {
            if (record.isValid()) {
                count++;
            }
        }
        return count;
    }

    // Setters for prediction results
    public void setEarlyReviewer(boolean earlyReviewer) {
        this.earlyReviewer = earlyReviewer;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }
}
