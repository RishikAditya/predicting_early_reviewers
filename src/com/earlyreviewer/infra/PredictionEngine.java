package com.earlyreviewer.infra;

import com.earlyreviewer.domain.Reviewer;
import com.earlyreviewer.util.LoggerUtil;

import java.util.logging.Logger;

/**
 * PredictionEngine applies rule-based logic to determine if a reviewer is an "early reviewer".
 * Rules are applied in order; first matching rule determines the prediction.
 */
public class PredictionEngine {
    private static final Logger logger = LoggerUtil.getLogger(PredictionEngine.class);

    // Thresholds
    private static final int EXPERIENCE_THRESHOLD = 5;
    private static final double SIGNIFICANTLY_FASTER_MULTIPLIER = 0.85; // 15% faster

    /**
     * Predicts early reviewer status based on rules.
     * Rules (in order):
     * 1. If avgTime < globalAvg AND experienced -> EARLY_REVIEWER
     * 2. Else if avgTime < (globalAvg * 0.85) AND (familiar OR contextual knowledge) -> EARLY_REVIEWER
     * 3. Else -> NOT_EARLY_REVIEWER
     *
     * @param reviewer The reviewer to predict
     * @param globalAverageTime The global average review time
     */
    public static void predict(Reviewer reviewer, double globalAverageTime) {
        // Check if reviewer has valid data
        if (reviewer.getValidRecordCount() == 0) {
            reviewer.setEarlyReviewer(false);
            reviewer.setExplanation("Insufficient data.");
            return;
        }

        double avgTime = reviewer.getAverageTimeToReview();

        // Rule 1: Faster than global average AND experienced
        if (avgTime < globalAverageTime && reviewer.isExperienced()) {
            reviewer.setEarlyReviewer(true);
            reviewer.setExplanation("Faster than global average and experienced.");
            return;
        }

        // Rule 2: Significantly faster AND (familiar OR contextual knowledge)
        if (avgTime < (globalAverageTime * SIGNIFICANTLY_FASTER_MULTIPLIER) &&
                (reviewer.hasTeamFamiliarity() || reviewer.hasContextualKnowledge())) {
            reviewer.setEarlyReviewer(true);
            reviewer.setExplanation("Significantly faster and familiar with code/context.");
            return;
        }

        // Rule 3: No rule matched
        reviewer.setEarlyReviewer(false);
        reviewer.setExplanation("No early-review rule matched.");
    }
}
