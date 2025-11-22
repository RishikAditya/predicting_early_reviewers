package com.earlyreviewer.util;

import com.earlyreviewer.domain.Reviewer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CSVExporter writes reviewer analysis results to a CSV file.
 * Output format matches the input plus additional computed fields.
 */
public class CSVExporter {
    private static final Logger logger = LoggerUtil.getLogger(CSVExporter.class);

    /**
     * Exports reviewer results to a CSV file.
     * Columns: reviewerId, avgReviewTimeSeconds, pastReviewsCount, experienceCategory,
     * teamFamiliarity, contextualKnowledge, earlyReviewer, explanation
     *
     * @param reviewers Collection of Reviewer objects to export
     * @param outputFile Target CSV file
     * @throws IOException if file cannot be written
     */
    public static void export(Collection<Reviewer> reviewers, File outputFile) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile))) {
            // Write header
            writer.write("reviewerId,avgReviewTimeSeconds,pastReviewsCount,experienceCategory," +
                    "teamFamiliarity,contextualKnowledge,earlyReviewer,explanation\n");

            // Write data rows
            for (Reviewer reviewer : reviewers) {
                String line = formatReviewerRow(reviewer);
                writer.write(line);
                writer.write("\n");
            }

            logger.log(Level.INFO, "Exported " + reviewers.size() + " reviewers to " + outputFile.getAbsolutePath());
        }
    }

    /**
     * Formats a single reviewer as a CSV row.
     */
    private static String formatReviewerRow(Reviewer reviewer) {
        String experienceCategory = reviewer.isExperienced() ? "Experienced" : "Low Experience";
        String earlyReviewerStr = reviewer.isEarlyReviewer() ? "YES" : "NO";

        return String.format("%s,%d,%d,%s,%s,%s,%s,\"%s\"",
                reviewer.getReviewerId(),
                (long) reviewer.getAverageTimeToReview(),
                reviewer.getPastReviewsCount(),
                experienceCategory,
                reviewer.hasTeamFamiliarity(),
                reviewer.hasContextualKnowledge(),
                earlyReviewerStr,
                reviewer.getExplanation());
    }
}
