import { Card } from "@/components/ui/card"
import { TrendingUp } from "lucide-react"
import type { HistoryEntry } from "@/types"

interface AnalyticsOverviewProps {
  history: HistoryEntry[]
}

export default function AnalyticsOverview({ history }: AnalyticsOverviewProps) {
  const totalAnalyses = history.length
  const totalReviewersAnalyzed = history.reduce((sum: number, item: HistoryEntry) => sum + item.totalReviewers, 0)
  const totalEarlyReviewersFound = history.reduce((sum: number, item: HistoryEntry) => sum + item.earlyReviewers, 0)
  const avgEarlyReviewerPercentage =
    totalReviewersAnalyzed > 0 ? ((totalEarlyReviewersFound / totalReviewersAnalyzed) * 100).toFixed(1) : 0

  const recentTrend = history.slice(0, 5).map((item: HistoryEntry) => ({
    name: item.fileName.split(".")[0].substring(0, 12),
    percentage: ((item.earlyReviewers / item.totalReviewers) * 100).toFixed(1),
  }))

  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
        <Card className="p-6 bg-gradient-to-br from-primary/10 to-primary/5 border-primary/20">
          <div className="flex items-start justify-between">
            <div>
              <p className="text-xs text-muted-foreground mb-2">Total Analyses</p>
              <p className="text-3xl font-bold text-foreground">{totalAnalyses}</p>
            </div>
            <TrendingUp className="w-5 h-5 text-primary" />
          </div>
        </Card>

        <Card className="p-6 bg-gradient-to-br from-blue-500/10 to-blue-500/5 border-blue-500/20">
          <div>
            <p className="text-xs text-muted-foreground mb-2">Reviewers Analyzed</p>
            <p className="text-3xl font-bold text-foreground">{totalReviewersAnalyzed}</p>
          </div>
        </Card>

        <Card className="p-6 bg-gradient-to-br from-green-500/10 to-green-500/5 border-green-500/20">
          <div>
            <p className="text-xs text-muted-foreground mb-2">Early Reviewers Found</p>
            <p className="text-3xl font-bold text-green-600 dark:text-green-400">{totalEarlyReviewersFound}</p>
          </div>
        </Card>

        <Card className="p-6 bg-gradient-to-br from-amber-500/10 to-amber-500/5 border-amber-500/20">
          <div>
            <p className="text-xs text-muted-foreground mb-2">Avg. % Early Reviewers</p>
            <p className="text-3xl font-bold text-amber-600 dark:text-amber-400">{avgEarlyReviewerPercentage}%</p>
          </div>
        </Card>
      </div>

      <Card className="p-6">
        <h3 className="text-lg font-semibold text-foreground mb-6">Recent Trend</h3>
        <div className="space-y-4">
          {recentTrend.map((item: { name: string; percentage: string }, idx: number) => (
            <div key={idx} className="flex items-center gap-4">
              <div className="min-w-32 text-sm font-medium text-foreground">{item.name}</div>
              <div className="flex-1 bg-muted rounded-full h-2 overflow-hidden">
                <div className="bg-primary h-full transition-all" style={{ width: `${item.percentage}%` }} />
              </div>
              <div className="min-w-12 text-right text-sm font-semibold text-foreground">{item.percentage}%</div>
            </div>
          ))}
        </div>
      </Card>
    </div>
  )
}
