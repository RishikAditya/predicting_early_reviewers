import { Card } from "@/components/ui/card"
import { TrendingUp } from "lucide-react"
import type { Results } from "@/types"

interface StatsSummaryProps {
  results: Results
}

export default function StatsSummary({ results }: StatsSummaryProps) {
  const earlyReviewers = results.predictions.filter((p: { is_early_reviewer: boolean }) => p.is_early_reviewer).length
  const avgConfidence = (
    (results.predictions.reduce((sum: number, p: { confidence_score: number }) => sum + p.confidence_score, 0) / results.predictions.length) *
    100
  ).toFixed(1)

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
      <Card className="p-6 bg-gradient-to-br from-primary/10 to-primary/5 border-primary/20">
        <div className="flex items-center justify-between">
          <div>
            <p className="text-sm text-muted-foreground mb-1">Early Reviewers Found</p>
            <p className="text-3xl font-bold text-foreground">{earlyReviewers}</p>
          </div>
          <TrendingUp className="w-8 h-8 text-primary opacity-20" />
        </div>
      </Card>

      <Card className="p-6 bg-gradient-to-br from-chart-2/10 to-chart-2/5 border-chart-2/20">
        <div>
          <p className="text-sm text-muted-foreground mb-1">Average Confidence</p>
          <p className="text-3xl font-bold text-foreground">{avgConfidence}%</p>
        </div>
      </Card>

      <Card className="p-6 bg-gradient-to-br from-chart-3/10 to-chart-3/5 border-chart-3/20">
        <div>
          <p className="text-sm text-muted-foreground mb-1">Total Reviewers</p>
          <p className="text-3xl font-bold text-foreground">{results.predictions.length}</p>
        </div>
      </Card>
    </div>
  )
}
