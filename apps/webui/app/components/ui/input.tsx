import * as React from "react"
import { cn } from "~/lib/utils"

export interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {}

const Input = React.forwardRef<HTMLInputElement, InputProps>(({ className, type = "text", ...props }, ref) => {
  const isInvalid = props['aria-invalid'] === 'true' || props['aria-invalid'] === true
  
  return (
    <input
      type={type}
      className={cn(
        "flex h-10 w-full rounded-md border bg-white px-3 py-2 text-sm placeholder:text-gray-500 focus-visible:outline-none focus-visible:ring-2 disabled:cursor-not-allowed disabled:opacity-50",
        isInvalid 
          ? "border-red-300 focus-visible:ring-red-500" 
          : "border-gray-300 focus-visible:ring-[var(--hub-primary)]",
        className
      )}
      ref={ref}
      {...props}
    />
  )
})
Input.displayName = "Input"

export { Input }
