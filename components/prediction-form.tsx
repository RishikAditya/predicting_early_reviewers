"use client"

import { Card } from "@/components/ui/card"
import { Button } from "@/components/ui/button"

interface PredictionFormProps {
  onSubmit: (e: React.FormEvent<HTMLFormElement>) => void
  loading: boolean
}

export default function PredictionForm({ onSubmit, loading }: PredictionFormProps) {
  return (
    <Card className="p-6">
      <form onSubmit={onSubmit} className="space-y-4">
        <div className="grid grid-cols-2 gap-4">
          <input
            type="number"
            placeholder="Review Count"
            className="px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            required
          />
          <input
            type="number"
            step="0.1"
            placeholder="Avg Rating (0-5)"
            className="px-3 py-2 border border-border rounded-lg bg-background text-foreground focus:outline-none focus:ring-2 focus:ring-primary"
            required
          />
        </div>
        <Button type="submit" disabled={loading} className="w-full">
          {loading ? "Analyzing..." : "Predict"}
        </Button>
      </form>
    </Card>
  )
}
