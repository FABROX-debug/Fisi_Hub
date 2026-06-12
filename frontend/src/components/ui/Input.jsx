function Input({
  id,
  label,
  error,
  className = '',
  required = false,
  ...props
}) {
  return (
    <div className="w-full">
      {label && (
        <label
          htmlFor={id}
          className="mb-1.5 block text-sm font-medium text-textPrimary"
        >
          {label}
          {required && <span className="ml-1 text-danger">*</span>}
        </label>
      )}
      <input
        id={id}
        required={required}
        aria-invalid={Boolean(error)}
        aria-describedby={error && id ? `${id}-error` : undefined}
        className={`w-full rounded-lg border bg-white px-3.5 py-2.5 text-sm text-textPrimary outline-none transition placeholder:text-slate-400 ${
          error
            ? 'border-danger focus:border-danger focus:ring-2 focus:ring-danger/15'
            : 'border-border focus:border-accent focus:ring-2 focus:ring-accent/15'
        } ${className}`}
        {...props}
      />
      {error && (
        <p id={id ? `${id}-error` : undefined} className="mt-1.5 text-sm text-danger">
          {error}
        </p>
      )}
    </div>
  )
}

export default Input

