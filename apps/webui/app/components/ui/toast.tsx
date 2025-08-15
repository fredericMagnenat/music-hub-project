import * as React from "react"
import { cn } from "~/lib/utils"

export type ToastVariant = "success" | "error" | "info"

export interface ToastOptions {
  message: string
  variant?: ToastVariant
}

interface ToastItem extends ToastOptions {
  id: number
  variant: ToastVariant
}

interface ToastContextValue {
  toast: (options: ToastOptions) => void
  remove: (id: number) => void
  toasts: ToastItem[]
}

const ToastContext = React.createContext<ToastContextValue | undefined>(undefined)

let nextId = 1

export function ToastProvider({ children }: { children: React.ReactNode }) {
  const [toasts, setToasts] = React.useState<ToastItem[]>([])

  const remove = React.useCallback((id: number) => {
    setToasts((prev) => prev.filter((t) => t.id !== id))
  }, [])

  const toast = React.useCallback((options: ToastOptions) => {
    const id = nextId++
    const item: ToastItem = {
      id,
      message: options.message,
      variant: options.variant ?? "info",
    }
    setToasts((prev) => [...prev, item])
    // Auto-dismiss after 3.2s
    window.setTimeout(() => remove(id), 3200)
  }, [remove])

  const value = React.useMemo(() => ({ toast, remove, toasts }), [toast, remove, toasts])

  return (
    <ToastContext.Provider value={value}>{children}</ToastContext.Provider>
  )
}

export function useToast() {
  const ctx = React.useContext(ToastContext)
  if (!ctx) throw new Error("useToast must be used within ToastProvider")
  return ctx
}

export function Toaster() {
  const { toasts, remove } = useToast()
  return (
    <div className="pointer-events-none fixed inset-0 z-50 flex flex-col items-end p-4 gap-2">
      <div className="flex w-full flex-col items-end gap-2">
        {toasts.map((t) => (
          <div
            key={t.id}
            className={cn(
              "pointer-events-auto w-full max-w-sm rounded-md border p-3 shadow-sm transition-all",
              t.variant === "success" && "border-green-200 bg-green-50 text-green-800",
              t.variant === "error" && "border-red-200 bg-red-50 text-red-800",
              t.variant === "info" && "border-gray-200 bg-white text-gray-900"
            )}
            role="status"
            aria-live="polite"
          >
            <div className="flex items-start justify-between gap-3">
              <div className="text-sm">{t.message}</div>
              <button
                type="button"
                onClick={() => remove(t.id)}
                className="inline-flex h-6 w-6 items-center justify-center rounded text-xs text-gray-500 hover:bg-gray-100 hover:text-gray-800"
                aria-label="Close notification"
              >
                ×
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
