import { MemoryRouter, Route, Routes } from 'react-router-dom'
import { render } from '@testing-library/react'

export function renderRoute(ui, { route = '/', path = '/' } = {}) {
  return render(
    <MemoryRouter initialEntries={[route]}>
      <Routes>
        <Route path={path} element={ui} />
      </Routes>
    </MemoryRouter>
  )
}
