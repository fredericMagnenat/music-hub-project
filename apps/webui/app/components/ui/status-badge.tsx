import * as React from "react"
import { cn } from "~/lib/utils"

export type TrackStatus = "Provisional" | "Verified"

export interface StatusBadgeProps extends React.HTMLAttributes<HTMLSpanElement> {
  status: TrackStatus
  className?: string
}

export function StatusBadge({ status, className, ...props }: StatusBadgeProps) {
  const isVerified = status === "Verified"

  return (
    <span
      className={cn(
        "inline-flex items-center rounded px-2 py-0.5 text-xs font-medium",
        isVerified
          ? "bg-[#10B981] text-[#065F46]"
          : "bg-[#F59E0B] text-[#92400E]",
        className
      )}
      aria-label={`Status: ${status}`}
      {...props}
    >
      {status}
    </span>
  )
}

export default StatusBadge
