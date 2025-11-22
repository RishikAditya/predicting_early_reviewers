"use client"

import { useState, useRef, useEffect } from "react"
import { Upload, BarChart3, Download, Zap, History } from "lucide-react"
import { Button } from "@/components/ui/button"
import { Card } from "@/components/ui/card"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import ResultsTable from "@/components/results-table"
import StatsSummary from "@/components/stats-summary"
import HistoryPanel from "@/components/history-panel"
import AnalyticsOverview from "@/components/analytics-overview"
import type { Results, HistoryEntry, Prediction } from "@/types"

export default function Home() {
  const [results, setResults] = useState<Results | null>(null)
  const [loading, setLoading] = useState(false)
  const [history, setHistory] = useState<HistoryEntry[]>([])
  const [activeTab, setActiveTab] = useState("upload")
  const fileInputRef = useRef<HTMLInputElement>(null)

  useEffect(() => {
    const saved = localStorage.getItem("predictionHistory")
    if (saved) {
      setHistory(JSON.parse(saved))
    }
  }, [])

  const handleFileUpload = async (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0]
    if (!file) return

    setLoading(true)
    try {
      const formData = new FormData()
      formData.append("file", file)

      const response = await fetch("/api/predict", {
        method: "POST",
        body: formData,
      })

      const data = await response.json()
      if (data.success) {
        const newEntry = {
          id: Date.now(),
          fileName: file.name,
          timestamp: new Date().toISOString(),
          stats: data.stats,
          predictions: data.predictions,
          totalReviewers: data.predictions.length,
          earlyReviewers: data.predictions.filter((p: Prediction) => p.is_early_reviewer).length,
        }
        const updatedHistory = [newEntry, ...history]
        setHistory(updatedHistory)
        localStorage.setItem("predictionHistory", JSON.stringify(updatedHistory))
        setResults(data)
        setActiveTab("results")
      } else {
        alert(data.error || "Failed to process file")
      }
    } catch (error) {
      alert("Error uploading file")
      console.error(error)
    } finally {
      setLoading(false)
    }
  }

  const handleDeleteFromHistory = (id: number) => {
    const updatedHistory = history.filter((item) => item.id !== id)
    setHistory(updatedHistory)
    localStorage.setItem("predictionHistory", JSON.stringify(updatedHistory))
  }

  const handleLoadFromHistory = (entry: HistoryEntry) => {
    setResults({
      success: true,
      predictions: entry.predictions,
      stats: entry.stats,
    })
    setActiveTab("results")
  }

  const handleExport = () => {
    if (!results) return

    const csv = generateCSV(results.predictions)
    const blob = new Blob([csv], { type: "text/csv" })
    const url = URL.createObjectURL(blob)
    const a = document.createElement("a")
    a.href = url
    a.download = `predictions-${new Date().toISOString().split("T")[0]}.csv`
    a.click()
    URL.revokeObjectURL(url)
  }

  return (
    <main className="min-h-screen bg-gradient-to-br from-background via-background to-background">
      {/* Header */}
      <header className="border-b border-border bg-card/40 backdrop-blur sticky top-0 z-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-4">
          <div className="flex items-center justify-between">
            <div className="flex items-center gap-3">
              <div className="p-2 bg-primary rounded-lg">
                <BarChart3 className="w-6 h-6 text-primary-foreground" />
              </div>
              <div>
                <h1 className="text-2xl font-bold text-foreground">Reviewer Predictor</h1>
                <p className="text-xs text-muted-foreground">Professional Analysis Dashboard</p>
              </div>
            </div>
            <div className="text-right">
              <p className="text-sm font-semibold text-foreground">{history.length}</p>
              <p className="text-xs text-muted-foreground">Analyses</p>
            </div>
          </div>
        </div>
      </header>

      {/* Main Content */}
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full">
          <TabsList className="grid w-full grid-cols-4 mb-6">
            <TabsTrigger value="upload">Upload</TabsTrigger>
            <TabsTrigger value="results" disabled={!results}>
              Results
            </TabsTrigger>
            <TabsTrigger value="analytics" disabled={history.length === 0}>
              Analytics
            </TabsTrigger>
            <TabsTrigger value="history" className="flex items-center gap-2">
              <History className="w-4 h-4" />
              History ({history.length})
            </TabsTrigger>
          </TabsList>

          {/* Upload Tab */}
          <TabsContent value="upload" className="space-y-6">
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
              <div className="lg:col-span-2">
                <Card
                  className="border-2 border-dashed border-border hover:border-primary/50 transition-colors p-12 flex flex-col items-center justify-center min-h-96 cursor-pointer hover:bg-card/50"
                  onClick={() => fileInputRef.current?.click()}
                >
                  <input
                    ref={fileInputRef}
                    type="file"
                    accept=".csv"
                    onChange={handleFileUpload}
                    disabled={loading}
                    className="hidden"
                  />
                  <Upload className="w-16 h-16 text-muted-foreground mb-4" />
                  <h2 className="text-2xl font-semibold text-foreground mb-2">Upload CSV File</h2>
                  <p className="text-muted-foreground text-center mb-4">
                    Drag and drop your CSV file or click to browse
                  </p>
                  <p className="text-sm text-muted-foreground text-center">
                    Expected columns: reviewer_id, review_count, avg_rating, days_since_first_review
                  </p>
                  {loading && <p className="text-primary mt-6 font-medium animate-pulse">Processing file...</p>}
                </Card>
              </div>

              <div className="space-y-4">
                <Card className="p-6 bg-card/40">
                  <div className="flex items-start gap-3">
                    <Zap className="w-5 h-5 text-primary mt-1 flex-shrink-0" />
                    <div>
                      <h3 className="font-semibold text-foreground mb-3">Quick Guide</h3>
                      <ul className="text-sm text-muted-foreground space-y-2">
                        <li className="flex gap-2">
                          <span className="font-bold text-primary">1.</span> Upload CSV data
                        </li>
                        <li className="flex gap-2">
                          <span className="font-bold text-primary">2.</span> Algorithm analyzes
                        </li>
                        <li className="flex gap-2">
                          <span className="font-bold text-primary">3.</span> View insights
                        </li>
                        <li className="flex gap-2">
                          <span className="font-bold text-primary">4.</span> Export results
                        </li>
                      </ul>
                    </div>
                  </div>
                </Card>

                {history.length > 0 && (
                  <Card className="p-4 bg-primary/10 border-primary/20">
                    <p className="text-xs font-semibold text-primary mb-3">Recent Analyses</p>
                    <div className="space-y-2">
                      {history.slice(0, 3).map((item) => (
                        <button
                          key={item.id}
                          onClick={() => handleLoadFromHistory(item)}
                          className="w-full text-left text-xs p-2 rounded hover:bg-primary/20 transition-colors"
                        >
                          <p className="font-medium text-foreground truncate">{item.fileName}</p>
                          <p className="text-muted-foreground text-xs">
                            {new Date(item.timestamp).toLocaleDateString()}
                          </p>
                        </button>
                      ))}
                    </div>
                  </Card>
                )}
              </div>
            </div>
          </TabsContent>

          {/* Results Tab */}
          <TabsContent value="results" className="space-y-6">
            {results && (
              <>
                <div className="flex items-center justify-between">
                  <div>
                    <h2 className="text-2xl font-bold text-foreground">Analysis Results</h2>
                    <p className="text-muted-foreground">
                      Reviewed {results.predictions.length} reviewers on {new Date().toLocaleDateString()}
                    </p>
                  </div>
                  <div className="flex gap-2">
                    <Button variant="outline" onClick={() => setResults(null)}>
                      New Upload
                    </Button>
                    <Button onClick={handleExport} className="gap-2">
                      <Download className="w-4 h-4" />
                      Export CSV
                    </Button>
                  </div>
                </div>

                <StatsSummary results={results} />
                <ResultsTable predictions={results.predictions} />
              </>
            )}
          </TabsContent>

          {/* Analytics Tab */}
          <TabsContent value="analytics">{history.length > 0 && <AnalyticsOverview history={history} />}</TabsContent>

          {/* History Tab */}
          <TabsContent value="history">
            <HistoryPanel history={history} onDelete={handleDeleteFromHistory} onLoad={handleLoadFromHistory} />
          </TabsContent>
        </Tabs>
      </div>

      {/* Footer */}
      <footer className="border-t border-border bg-card/40 backdrop-blur mt-20">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="grid grid-cols-3 gap-8 mb-8">
            <div>
              <h4 className="font-semibold text-foreground mb-3">About</h4>
              <p className="text-sm text-muted-foreground">Professional early reviewer prediction tool</p>
            </div>
            <div>
              <h4 className="font-semibold text-foreground mb-3">Features</h4>
              <ul className="text-sm text-muted-foreground space-y-1">
                <li>CSV Import</li>
                <li>AI Analysis</li>
                <li>History Tracking</li>
              </ul>
            </div>
            <div>
              <h4 className="font-semibold text-foreground mb-3">Version</h4>
              <p className="text-sm text-muted-foreground">1.0.0</p>
            </div>
          </div>
          <div className="border-t border-border pt-6">
            <p className="text-center text-xs text-muted-foreground">Copyright 2025. Early Reviewer Prediction Tool.</p>
          </div>
        </div>
      </footer>
    </main>
  )
}

function generateCSV(predictions: Prediction[]) {
  const headers = [
    "reviewer_id",
    "review_count",
    "avg_rating",
    "days_since_first_review",
    "is_early_reviewer",
    "confidence_score",
  ]
  const rows = predictions.map((p: Prediction) => [
    p.reviewer_id,
    p.review_count,
    p.avg_rating.toFixed(2),
    p.days_since_first_review,
    p.is_early_reviewer ? "Yes" : "No",
    p.confidence_score.toFixed(2),
  ])
  return [headers, ...rows].map((row) => row.join(",")).join("\n")
}
