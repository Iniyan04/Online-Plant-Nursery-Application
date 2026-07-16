export function CatalogSkeleton({ count = 6 }) {
  return (
    <div className="product-grid" aria-hidden="true">
      {Array.from({ length: count }).map((_, index) => (
        <div className="product-card product-card-skeleton" key={index}>
          <div className="product-card-img skeleton-box" />
          <div className="product-card-body">
            <div className="skeleton-line skeleton-line-tag" />
            <div className="skeleton-line skeleton-line-title" />
            <div className="skeleton-line skeleton-line-copy" />
            <div className="skeleton-line skeleton-line-copy short" />
            <div className="product-card-footer">
              <div className="skeleton-line skeleton-line-price" />
              <div className="skeleton-chip" />
            </div>
          </div>
        </div>
      ))}
    </div>
  )
}

export function DetailSkeleton() {
  return (
    <div className="detail-shell detail-shell-loading" aria-hidden="true">
      <div className="detail-media skeleton-box detail-hero" />
      <div className="detail-card">
        <div className="skeleton-line skeleton-line-tag" />
        <div className="skeleton-line skeleton-line-hero" />
        <div className="skeleton-line skeleton-line-copy" />
        <div className="skeleton-line skeleton-line-copy short" />
        <div className="detail-specs">
          {Array.from({ length: 6 }).map((_, index) => (
            <div className="detail-spec-card" key={index}>
              <div className="skeleton-line skeleton-line-mini" />
              <div className="skeleton-line skeleton-line-copy short" />
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

export function TableSkeleton({ rows = 5, columns = 5 }) {
  return (
    <div className="table-wrap table-wrap-skeleton" aria-hidden="true">
      <div className="table-skeleton-head">
        {Array.from({ length: columns }).map((_, index) => (
          <div className="skeleton-line skeleton-line-header" key={index} />
        ))}
      </div>
      <div className="table-skeleton-body">
        {Array.from({ length: rows }).map((_, rowIndex) => (
          <div className="table-skeleton-row" key={rowIndex}>
            {Array.from({ length: columns }).map((__, colIndex) => (
              <div className="skeleton-line skeleton-line-cell" key={colIndex} />
            ))}
          </div>
        ))}
      </div>
    </div>
  )
}
