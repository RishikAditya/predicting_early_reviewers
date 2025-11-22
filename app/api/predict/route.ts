import { type NextRequest, NextResponse } from "next/server"

export async function POST(request: NextRequest) {
  try {
    const formData = await request.formData()
    const file = formData.get("file") as File

    if (!file) {
      return NextResponse.json({ success: false, error: "No file provided" }, { status: 400 })
    }

    const text = await file.text()
    const lines = text.trim().split("\n")

    if (lines.length < 2) {
      return NextResponse.json({ success: false, error: "CSV file must contain data" }, { status: 400 })
    }

    const headers = lines[0].split(",").map((h) => h.trim().toLowerCase())
    const predictions = []

    for (let i = 1; i < lines.length; i++) {
      const values = lines[i].split(",").map((v) => v.trim())
      if (values.length < 4) continue

      const reviewerId = values[0]
      const reviewCount = Number.parseInt(values[1]) || 0
      const avgRating = Number.parseFloat(values[2]) || 0
      const daysSinceFirstReview = Number.parseInt(values[3]) || 0

      // Prediction rules
      const isHighReviewer = reviewCount >= 10
      const isHighRating = avgRating >= 4.0
      const isRecent = daysSinceFirstReview <= 90

      // Early reviewer if meets at least 2 of 3 criteria
      const criteriaCount = [isHighReviewer, isHighRating, isRecent].filter(Boolean).length
      const isEarlyReviewer = criteriaCount >= 2
      const confidenceScore = criteriaCount / 3

      predictions.push({
        reviewer_id: reviewerId,
        review_count: reviewCount,
        avg_rating: avgRating,
        days_since_first_review: daysSinceFirstReview,
        is_early_reviewer: isEarlyReviewer,
        confidence_score: confidenceScore,
      })
    }

    return NextResponse.json({
      success: true,
      predictions,
      summary: {
        total: predictions.length,
        earlyReviewers: predictions.filter((p) => p.is_early_reviewer).length,
      },
    })
  } catch (error) {
    console.error(error)
    return NextResponse.json({ success: false, error: "Failed to process file" }, { status: 500 })
  }
}
