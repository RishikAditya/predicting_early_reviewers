package com.earlyreviewer.infra;

import com.earlyreviewer.domain.ReviewRecord;
import com.earlyreviewer.util.LoggerUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CSVParser reads a CSV file and converts it into ReviewRecord objects.
 * Handles parsing errors gracefully with logging.
 * Expected CSV format: reviewerId, pastReviewsCount, submissionTimestamp, completionTimestamp,
 * teamFamiliarity, contextualKnowledge
 */
public class CSVParser {
    private static final Logger logger = LoggerUtil.getLogger(CSVParser.class);

    /**
     * Parses a CSV file and returns a list of ReviewRecord objects.
     * Skips header row and invalid records (logs warnings for issues).
     *
     * @param file The CSV file to parse
     * @return List of valid ReviewRecord objects
     * @throws IOException if file cannot be read
     */
    public static List<ReviewRecord> parse(File file) throws IOException {
        List<ReviewRecord> records = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            int lineNumber = 0;

            while ((line = reader.readLine()) != null) {
                lineNumber++;

                // Skip header row
                if (lineNumber == 1) {
                    continue;
                }

                try {
                    ReviewRecord record = parseLine(line);
                    if (record != null) {
                        records.add(record);
                    }
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Error parsing line " + lineNumber + ": " + e.getMessage());
                }
            }
        }

        logger.log(Level.INFO, "Parsed " + records.size() + " valid records from CSV");
        return records;
    }

    /**
     * Parses a single CSV line into a ReviewRecord.
     * Returns null if the record is invalid (missing submission/completion timestamps).
     *
     * @param line CSV line to parse
     * @return ReviewRecord or null if invalid
     */
    private static ReviewRecord parseLine(String line) {
        String[] parts = line.split(",");

        if (parts.length < 6) {
            throw new IllegalArgumentException("CSV line has fewer than 6 columns");
        }

        try {
            String reviewerId = parts[0].trim();
            int pastReviewsCount = Integer.parseInt(parts[1].trim());
            long submissionTimestamp = Long.parseLong(parts[2].trim());
            long completionTimestamp = Long.parseLong(parts[3].trim());
            boolean teamFamiliarity = parseBoolean(parts[4].trim());
            boolean contextualKnowledge = parseBoolean(parts[5].trim());

            // Validate required fields
            if (submissionTimestamp <= 0 || completionTimestamp <= 0) {
                logger.log(Level.WARNING, "Invalid timestamps for reviewer " + reviewerId);
                return null;
            }

            ReviewRecord record = new ReviewRecord(reviewerId, pastReviewsCount, submissionTimestamp,
                    completionTimestamp, teamFamiliarity, contextualKnowledge);

            // Log invalid records (zero or negative time-to-review)
            if (!record.isValid()) {
                logger.log(Level.WARNING, "Invalid time-to-review for reviewer " + reviewerId +
                        ": " + record.getTimeToReview() + " seconds");
                return null;
            }

            return record;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Number format error: " + e.getMessage());
        }
    }

    /**
     * Parses a string to boolean. Accepts "true" (case-insensitive) as true, all else as false.
     */
    private static boolean parseBoolean(String value) {
        return value.equalsIgnoreCase("true");
    }
}
