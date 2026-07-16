import { NavLink } from 'react-router-dom'

export default function Footer() {
  return (
    <footer className="site-footer">
      <div className="site-footer-inner">
        <div className="footer-column footer-brand-column">
          <span className="footer-kicker">Greenroot Nursery</span>
          <h2>Fresh greenery for calm, thoughtful spaces.</h2>
          <p>
            Premium plants, seeds, and planters presented with a clean nursery
            experience for everyday shopping and management.
          </p>
        </div>

        <div className="footer-column">
          <h3>Quick Links</h3>
          <NavLink to="/plants">Plants</NavLink>
          <NavLink to="/seeds">Seeds</NavLink>
          <NavLink to="/planters">Planters</NavLink>
          <NavLink to="/orders">My Orders</NavLink>
        </div>

        <div className="footer-column">
          <h3>Contact</h3>
          <p>Customer support during nursery hours</p>
          <p>Mon - Sat, 9:00 AM - 6:00 PM</p>
          <p>Order, catalog, and inventory assistance</p>
        </div>
      </div>

      <div className="site-footer-bottom">
        <span>Copyright 2026 Greenroot Nursery. All rights reserved.</span>
      </div>
    </footer>
  )
}
