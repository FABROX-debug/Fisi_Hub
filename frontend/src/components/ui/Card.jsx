function Card({ children, className = '', ...props }) {
  return (
    <section
      className={`rounded-xl border border-border bg-card p-5 shadow-card sm:p-6 ${className}`}
      {...props}
    >
      {children}
    </section>
  )
}

export default Card

