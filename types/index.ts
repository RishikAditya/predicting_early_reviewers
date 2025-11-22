export interface Prediction {
  reviewer_id: string | number
  review_count: number
  avg_rating: number
  days_since_first_review: number
  is_early_reviewer: boolean
  confidence_score: number
}

export interface HistoryEntry {
  id: number
  fileName: string
  timestamp: string
  stats: any
  predictions: Prediction[]
  totalReviewers: number
  earlyReviewers: number
}

export interface Results {
  success: boolean
  predictions: Prediction[]
  stats?: any
  error?: string
}

