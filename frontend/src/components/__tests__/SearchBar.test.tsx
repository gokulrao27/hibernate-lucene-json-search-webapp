import React from 'react'
import { render, fireEvent } from '@testing-library/react'
import SearchBar from '../SearchBar'

test('calls onSearch when Enter pressed with >=3 chars', () => {
  const onSearch = jest.fn()
  const { getByLabelText } = render(<SearchBar onSearch={onSearch} />)
  const input = getByLabelText('Search users') as HTMLInputElement

  fireEvent.change(input, { target: { value: ' ava ' } })
  fireEvent.keyDown(input, { key: 'Enter', code: 'Enter' })

  expect(onSearch).toHaveBeenCalledWith('ava')
})

test('calls onSearch when search button clicked with >=3 chars', () => {
  const onSearch = jest.fn()
  const { getByLabelText } = render(<SearchBar onSearch={onSearch} />)
  const input = getByLabelText('Search users') as HTMLInputElement
  const button = getByLabelText('Execute search') as HTMLButtonElement

  fireEvent.change(input, { target: { value: 'john' } })
  fireEvent.click(button)

  expect(onSearch).toHaveBeenCalledWith('john')
})
