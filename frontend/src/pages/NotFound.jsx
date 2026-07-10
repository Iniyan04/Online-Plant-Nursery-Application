import { Link } from 'react-router-dom'

export default function NotFound() {
  return (
    <div className="state-block">
      <p className="eyebrow">404</p>
      <p className="page-title">This bed's empty</p>
      <p>There's nothing planted at this address.</p>
      <Link className="btn btn-outline mt-24" to="/plants">
        Back to catalog
      </Link>
    </div>
  )
}
