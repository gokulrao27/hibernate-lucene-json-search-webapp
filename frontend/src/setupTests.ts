import '@testing-library/jest-dom'

class IntersectionObserverMock {
  callbacks: IntersectionObserverCallback[] = []
  observe = jest.fn()
  unobserve = jest.fn()
  disconnect = jest.fn()
  takeRecords = jest.fn()
  constructor(private cb?: IntersectionObserverCallback) {
    if (cb) this.callbacks.push(cb)
  }
}

global.IntersectionObserver = global.IntersectionObserver || (IntersectionObserverMock as any)
