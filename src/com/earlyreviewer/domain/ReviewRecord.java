package com.earlyreviewer.domain;

/**
 * ReviewRecord represents a single review event with timestamps and metadata.
 * This is an immutable domain object that holds raw data from CSV and derived fields.
 */
public class ReviewRecord {
    private final String reviewerId;
    private final int pastReviewsCount;
    private final long submissionTimestamp;
    private final long completionTimestamp;
    private final boolean teamFamiliarity;
    private final boolean contextualKnowledge;
    private final long timeToReview; // Derived: completionTimestamp - submissionTimestamp

    public ReviewRecord(String reviewerId, int pastReviewsCount, long submissionTimestamp,
                        long completionTimestamp, boolean teamFamiliarity, boolean contextualKnowledge) {
        this.reviewerId = reviewerId;
        this.pastReviewsCount = pastReviewsCount;
        this.submissionTimestamp = submissionTimestamp;
        this.completionTimestamp = completionTimestamp;
        this.teamFamiliarity = teamFamiliarity;
        this.contextualKnowledge = contextualKnowledge;
        this.timeToReview = completionTimestamp - submissionTimestamp;
    }

    // Getters
    public String getReviewerId() {
        return reviewerId;
    }

    public int getPastReviewsCount() {
        return pastReviewsCount;
    }

    public long getSubmissionTimestamp() {
        return submissionTimestamp;
    }

    public long getCompletionTimestamp() {
        return completionTimestamp;
    }

    public boolean isTeamFamiliarity() {
        return teamFamiliarity;
    }

    public boolean isContextualKnowledge() {
        return contextualKnowledge;
    }

    public long getTimeToReview() {
        return timeToReview;
    }

    /**
     * Checks if this record is valid for analysis.
     * Valid records have positive time-to-review values.
     */
    public boolean isValid() {
        return timeToReview > 0;
    }
}
