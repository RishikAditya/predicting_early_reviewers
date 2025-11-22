package com.earlyreviewer.usecase;

import com.earlyreviewer.domain.ReviewRecord;
import com.earlyreviewer.domain.Reviewer;
import com.earlyreviewer.infra.PredictionEngine;
import com.earlyreviewer.util.LoggerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ReviewerAnalyzer orchestrates the analysis of review records.
 * Builds reviewer aggregates, computes statistics, and triggers predictions.
 */
public class ReviewerAnalyzer {
    private static final Logger logger = LoggerUtil.getLogger(ReviewerAnalyzer.class);

    /**
     * Processes a list of review records and returns aggregated reviewer data.
     * Filters out invalid records and computes per-reviewer statistics.
     *
     * @param records List of review records from CSV
     * @return Map of reviewerId -> Reviewer with computed statistics
     */
    public static Map<String, Reviewer> process(List<ReviewRecord> records) {
        Map<String, Reviewer> reviewers = new HashMap<>();

        // Filter valid records and aggregate by reviewer
        List<ReviewRecord> validRecords = new ArrayList<>();
        for (ReviewRecord record : records) {
            if (record.isValid()) {
                validRecords.add(record);
                
                // Add record to reviewer aggregate
                String reviewerId = record.getReviewerId();
                reviewers.putIfAbsent(reviewerId, new Reviewer(reviewerId));
                reviewers.get(reviewerId).addRecord(record);
            }
        }

        logger.log(Level.INFO, "Processed " + validRecords.size() + " valid records for " +
                reviewers.size() + " reviewers");

        // Compute statistics for each reviewer
        for (Reviewer reviewer : reviewers.values()) {
            reviewer.computeStatistics();
        }

        // Compute global average time across all valid records
        double globalAverageTime = computeGlobalAverageTime(validRecords);
        logger.log(Level.INFO, "Global average review time: " + String.format("%.2f", globalAverageTime) + " seconds");

        // Run prediction engine on all reviewers
        for (Reviewer reviewer : reviewers.values()) {
            PredictionEngine.predict(reviewer, globalAverageTime);
        }

        return reviewers;
    }

    /**
     * Computes the global average time-to-review across all valid records.
     *
     * @param validRecords List of valid review records
     * @return Average time in seconds, or 0 if no valid records
     */
    private static double computeGlobalAverageTime(List<ReviewRecord> validRecords) {
        if (validRecords.isEmpty()) {
            return 0.0;
        }

        long sum = 0;
        for (ReviewRecord record : validRecords) {
            sum += record.getTimeToReview();
        }

        return (double) sum / validRecords.size();
    }

    /**
     * Computes summary statistics from reviewer data.
     *
     * @param reviewers Map of reviewers
     * @return Array [totalReviewers, earlyReviewers, notEarlyReviewers]
     */
    public static int[] computeSummary(Map<String, Reviewer> reviewers) {
        int total = reviewers.size();
        int early = 0;
        int notEarly = 0;

        for (Reviewer reviewer : reviewers.values()) {
            if (reviewer.isEarlyReviewer()) {
                early++;
            } else {
                notEarly++;
            }
        }

        return new int[]{total, early, notEarly};
    }
}
