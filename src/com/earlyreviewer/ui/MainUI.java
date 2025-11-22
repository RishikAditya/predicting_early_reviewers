package com.earlyreviewer.ui;

import com.earlyreviewer.domain.ReviewRecord;
import com.earlyreviewer.infra.CSVParser;
import com.earlyreviewer.usecase.ReviewerAnalyzer;
import com.earlyreviewer.domain.Reviewer;
import com.earlyreviewer.util.CSVExporter;
import com.earlyreviewer.util.LoggerUtil;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * MainUI provides the Swing-based desktop interface for the Early Reviewer Prediction Tool.
 * Handles user interactions: CSV upload, prediction, export, and results display.
 */
public class MainUI extends JFrame {
    private static final Logger logger = LoggerUtil.getLogger(MainUI.class);
    
    private JButton uploadButton;
    private JButton predictButton;
    private JButton exportButton;
    private JTable resultsTable;
    private JTextArea summaryPanel;
    private JFileChooser fileChooser;
    private Map<String, Reviewer> currentReviewers;
    private File currentFile;

    public MainUI() {
        initializeUI();
        currentReviewers = new HashMap<>();
        fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".csv");
            }

            @Override
            public String getDescription() {
                return "CSV Files (*.csv)";
            }
        });
    }

    /**
     * Initializes all UI components and layout.
     */
    private void initializeUI() {
        setTitle("Early Reviewer Prediction Tool");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 700);
        setLocationRelativeTo(null);

        // Top button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        uploadButton = new JButton("Upload CSV");
        uploadButton.addActionListener(e -> handleUploadCSV());
        buttonPanel.add(uploadButton);

        predictButton = new JButton("Predict Early Reviewers");
        predictButton.setEnabled(false);
        predictButton.addActionListener(e -> handlePredict());
        buttonPanel.add(predictButton);

        exportButton = new JButton("Export Results");
        exportButton.setEnabled(false);
        exportButton.addActionListener(e -> handleExport());
        buttonPanel.add(exportButton);

        // Results table
        resultsTable = new JTable();
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane tableScrollPane = new JScrollPane(resultsTable);

        // Summary panel
        summaryPanel = new JTextArea(5, 50);
        summaryPanel.setEditable(false);
        summaryPanel.setLineWrap(true);
        summaryPanel.setWrapStyleWord(true);
        summaryPanel.setText("Ready to analyze. Upload a CSV file to begin.");
        JScrollPane summaryScrollPane = new JScrollPane(summaryPanel);

        // Main layout
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(buttonPanel, BorderLayout.NORTH);
        contentPane.add(tableScrollPane, BorderLayout.CENTER);
        contentPane.add(summaryScrollPane, BorderLayout.SOUTH);
    }

    /**
     * Handles CSV file upload via file chooser.
     */
    private void handleUploadCSV() {
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentFile = fileChooser.getSelectedFile();
            try {
                List<ReviewRecord> records = CSVParser.parse(currentFile);
                currentReviewers = ReviewerAnalyzer.process(records);
                displayReviewers();
                predictButton.setEnabled(true);
                exportButton.setEnabled(false);
                summaryPanel.setText("Loaded " + currentReviewers.size() + " reviewers. Click 'Predict Early Reviewers' to analyze.");
                logger.log(Level.INFO, "Loaded file: " + currentFile.getAbsolutePath());
            } catch (IOException e) {
                showError("Error loading CSV file: " + e.getMessage());
                logger.log(Level.SEVERE, "Failed to load CSV", e);
            }
        }
    }

    /**
     * Handles prediction button click - reruns analyzer.
     */
    private void handlePredict() {
        if (currentReviewers.isEmpty()) {
            showError("No reviewers loaded.");
            return;
        }

        try {
            // Refresh predictions
            List<ReviewRecord> records = CSVParser.parse(currentFile);
            currentReviewers = ReviewerAnalyzer.process(records);
            displayReviewers();
            updateSummary();
            exportButton.setEnabled(true);
            logger.log(Level.INFO, "Prediction analysis completed");
        } catch (IOException e) {
            showError("Error during prediction: " + e.getMessage());
            logger.log(Level.SEVERE, "Prediction failed", e);
        }
    }

    /**
     * Displays reviewers in the table.
     */
    private void displayReviewers() {
        String[] columns = {"Reviewer ID", "Avg Time (s)", "Past Reviews", "Experience",
                "Team Familiarity", "Contextual Knowledge", "Early Reviewer", "Explanation"};

        DefaultTableModel model = new DefaultTableModel(columns, 0);

        // Sort reviewers by ID for consistent display
        List<Reviewer> sortedReviewers = new ArrayList<>(currentReviewers.values());
        sortedReviewers.sort((a, b) -> a.getReviewerId().compareTo(b.getReviewerId()));

        for (Reviewer reviewer : sortedReviewers) {
            Object[] row = {
                    reviewer.getReviewerId(),
                    String.format("%.0f", reviewer.getAverageTimeToReview()),
                    reviewer.getPastReviewsCount(),
                    reviewer.isExperienced() ? "Experienced" : "Low",
                    reviewer.hasTeamFamiliarity() ? "Yes" : "No",
                    reviewer.hasContextualKnowledge() ? "Yes" : "No",
                    reviewer.isEarlyReviewer() ? "YES" : "NO",
                    reviewer.getExplanation()
            };
            model.addRow(row);
        }

        resultsTable.setModel(model);
        // Adjust column widths
        resultsTable.getColumnModel().getColumn(7).setPreferredWidth(200);
    }

    /**
     * Updates the summary panel with statistics.
     */
    private void updateSummary() {
        int[] summary = ReviewerAnalyzer.computeSummary(currentReviewers);
        String text = String.format("Analysis Complete\n\nTotal Reviewers: %d\nEarly Reviewers: %d\nNot Early: %d",
                summary[0], summary[1], summary[2]);
        summaryPanel.setText(text);
    }

    /**
     * Handles export to CSV file.
     */
    private void handleExport() {
        JFileChooser exportChooser = new JFileChooser();
        exportChooser.setDefaultFileName("early_reviewers_results.csv");
        exportChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".csv");
            }

            @Override
            public String getDescription() {
                return "CSV Files (*.csv)";
            }
        });

        int result = exportChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File outputFile = exportChooser.getSelectedFile();
            try {
                CSVExporter.export(currentReviewers.values(), outputFile);
                JOptionPane.showMessageDialog(this, "Results exported to: " + outputFile.getAbsolutePath());
                logger.log(Level.INFO, "Exported results to: " + outputFile.getAbsolutePath());
            } catch (IOException e) {
                showError("Error exporting results: " + e.getMessage());
                logger.log(Level.SEVERE, "Export failed", e);
            }
        }
    }

    /**
     * Shows an error dialog to the user.
     */
    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    /**
     * Application entry point.
     */
    public static void main(String[] args) {
        LoggerUtil.initialize();
        SwingUtilities.invokeLater(() -> {
            MainUI frame = new MainUI();
            frame.setVisible(true);
        });
    }
}
