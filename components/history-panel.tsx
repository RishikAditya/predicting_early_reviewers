"use client"

import { Trash2, Eye, Calendar } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import type { HistoryEntry } from "@/types"

interface HistoryPanelProps {
  history: HistoryEntry[]
  onDelete: (id: number) => void
  onLoad: (entry: HistoryEntry) => void
}

export default function HistoryPanel({ history, onDelete, onLoad }: HistoryPanelProps) {
  if (history.length === 0) {
    return (
      <Card className="p-12 text-center">
        <Calendar className="w-12 h-12 text-muted-foreground mx-auto mb-4 opacity-50" />
        <p className="text-muted-foreground">No analysis history yet. Upload a CSV to get started.</p>
      </Card>
    )
  }

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between mb-6">
        <h3 className="text-lg font-semibold text-foreground">Previous Analyses</h3>
        <p className="text-sm text-muted-foreground">{history.length} total</p>
      </div>
      {history.map((item: HistoryEntry) => (
        <Card key={item.id} className="p-4 hover:bg-card/80 transition-colors">
          <div className="flex items-start justify-between">
            <div className="flex-1">
              <h4 className="font-semibold text-foreground mb-1">{item.fileName}</h4>
              <div className="grid grid-cols-3 gap-4 text-sm text-muted-foreground mb-2">
                <div>
                  <p className="text-xs text-muted-foreground/70">Total Reviewers</p>
                  <p className="font-semibold text-foreground">{item.totalReviewers}</p>
                </div>
                <div>
                  <p className="text-xs text-muted-foreground/70">Early Reviewers</p>
                  <p className="font-semibold text-primary">{item.earlyReviewers}</p>
                </div>
                <div>
                  <p className="text-xs text-muted-foreground/70">Date</p>
                  <p className="font-semibold text-foreground">{new Date(item.timestamp).toLocaleDateString()}</p>
                </div>
              </div>
            </div>
            <div className="flex gap-2">
              <Button size="sm" variant="outline" onClick={() => onLoad(item)} className="gap-2">
                <Eye className="w-4 h-4" />
                View
              </Button>
              <Button
                size="sm"
                variant="ghost"
                className="text-destructive hover:text-destructive"
                onClick={() => onDelete(item.id)}
              >
                <Trash2 className="w-4 h-4" />
              </Button>
            </div>
          </div>
        </Card>
      ))}
    </div>
  )
}
