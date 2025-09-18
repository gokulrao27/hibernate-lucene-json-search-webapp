import React from 'react'

type Props = { children: React.ReactNode }

type State = { hasError: boolean; error?: Error }

class ErrorBoundary extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props)
    this.state = { hasError: false }
  }

  static getDerivedStateFromError(error: Error) {
    return { hasError: true, error }
  }

  componentDidCatch(error: Error, info: any) {
    // You could log to an external service here
    console.error('ErrorBoundary caught', error, info)
  }

  render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen flex items-center justify-center">
          <div className="bg-white p-6 rounded card-shadow max-w-lg">
            <h2 className="text-xl font-semibold mb-2">Something went wrong</h2>
            <pre className="text-sm text-red-600 mb-4">{String(this.state.error)}</pre>
            <div className="flex gap-2">
              <button onClick={() => window.location.reload()} className="px-4 py-2 bg-primary text-white rounded">Reload</button>
            </div>
          </div>
        </div>
      )
    }
    return this.props.children
  }
}

export default ErrorBoundary

