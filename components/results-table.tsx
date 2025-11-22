import { Card } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import type { Prediction } from "@/types"

interface ResultsTableProps {
  predictions: Prediction[]
}

export default function ResultsTable({ predictions }: ResultsTableProps) {
  return (
    <Card className="overflow-hidden">
      <div className="overflow-x-auto">
        <table className="w-full text-sm">
          <thead className="bg-muted border-b border-border">
            <tr>
              <th className="px-6 py-3 text-left font-semibold text-foreground">Reviewer ID</th>
              <th className="px-6 py-3 text-left font-semibold text-foreground">Reviews</th>
              <th className="px-6 py-3 text-left font-semibold text-foreground">Avg Rating</th>
              <th className="px-6 py-3 text-left font-semibold text-foreground">Days Active</th>
              <th className="px-6 py-3 text-left font-semibold text-foreground">Early Reviewer</th>
              <th className="px-6 py-3 text-left font-semibold text-foreground">Confidence</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-border">
            {predictions.map((pred: Prediction, idx: number) => (
              <tr key={idx} className="hover:bg-muted/50 transition-colors">
                <td className="px-6 py-3 text-foreground font-medium">{pred.reviewer_id}</td>
                <td className="px-6 py-3 text-muted-foreground">{pred.review_count}</td>
                <td className="px-6 py-3 text-muted-foreground">{pred.avg_rating.toFixed(2)}</td>
                <td className="px-6 py-3 text-muted-foreground">{pred.days_since_first_review}</td>
                <td className="px-6 py-3">
                  <Badge variant={pred.is_early_reviewer ? "default" : "secondary"}>
                    {pred.is_early_reviewer ? "Yes" : "No"}
                  </Badge>
                </td>
                <td className="px-6 py-3 text-muted-foreground">{(pred.confidence_score * 100).toFixed(0)}%</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </Card>
  )
}
