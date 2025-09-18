import React from 'react'
import { render, screen } from '@testing-library/react'
import UserCard from '../UserCard'

const user = {
  id: 1,
  firstName: 'Jane',
  lastName: 'Doe',
  email: 'jane@example.com',
  age: 28,
  ssn: '123-45-6789'
}

test('renders user details', () => {
  render(<UserCard user={user as any} />)
  expect(screen.getByText(/Jane Doe/)).toBeInTheDocument()
  expect(screen.getByText(/jane@example.com/)).toBeInTheDocument()
  expect(screen.getByText(/SSN:/)).toBeInTheDocument()
})

