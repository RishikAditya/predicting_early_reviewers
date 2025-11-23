# Characterizing and Predicting Early Reviewers

### Introduction

Open-source projects like Linux rely on developers who contribute code to improve the software or operating system. Their code is sent to reviewers who have strong knowledge of the codebase and can read, check for errors, and decide whether to release or include changes in the next version of the software. Reviewers are responsible for checking code and dispatching it as quickly as possible, but they are often slow. This creates a bottleneck that delays releases and leads to the following problems:

1. Reduced project velocity
2. Delayed releases that cause opportunity and market losses
3. Rising costs
4. Contributor burnout — developers lose momentum and interest while waiting for feedback on submitted code

### Project Goal

Build a computational model that can anticipate fast reviewers and route PRs to predicted early reviewers, dramatically reducing cycle time and accelerating feature delivery.

### Data Source and Input Parameters

Historical review event data from version control platforms (e.g., GitHub), including code versions, reviewer comments, discussions, and decisions.

This data is provided in a mock CSV format and includes:

1. Key timestamps (measured in seconds)
2. Reviewer attributes
    1. Unique Reviewer ID
    2. Past reviews count (experience)
    3. Time taken to complete each review

Implementation will focus on Java collections or custom classes that manage large datasets containing PR submission time and review completion time (measured in seconds for precision).

### Characterization of Reviewer

1. Timeliness feature
    1. Calculate time to review
    2. Establish a baseline for response speed
2. Experience feature
    1. Past reviews count — IF reviewer experience ≥ 5 past reviews, THEN predict EARLY reviewer
    2. Team familiarity — understanding of team workflows and standards
    3. Contextual knowledge — sufficient knowledge of the codebase
3. Feature synthesis — identify behavioral patterns distinguishing fast responders from slower reviewers

### Rule-Based Prediction Model

Simplified approach that avoids complex ML mathematics.

Relies on core logic and conditional statements with clear thresholds.

### Key Outcomes and Feature Importance

Measure model effectiveness and identify which characteristics drive accurate early-reviewer prediction.

Performance metrics:

- Compare predictions against ground-truth labels
- Calculate overall accuracy as correct predictions / total events

### Limitations

1. Simplified thresholds — a reviewer with 6 past reviews is treated the same as one with 100 because both meet the threshold
2. Design trade-off — prioritizing simplicity over maximal predictive accuracy achievable with ML algorithms

### Conclusion

Established a foundation — at a base level, we can demonstrate measurable accuracy in identifying fast responders using experience and historical patterns.

### Future Work

- Implement advanced machine learning algorithms
