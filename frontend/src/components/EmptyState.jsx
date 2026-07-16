export default function EmptyState({ icon, eyebrow, title, message, action, compact = false }) {
  return (
    <div className={`state-block ${compact ? 'state-block-compact' : ''}`}>
      <div className="state-illustration" aria-hidden="true">
        {icon}
      </div>
      {eyebrow ? <p className="eyebrow">{eyebrow}</p> : null}
      <p className="page-title">{title}</p>
      {message ? <p className="state-copy">{message}</p> : null}
      {action ? <div className="state-action">{action}</div> : null}
    </div>
  )
}
