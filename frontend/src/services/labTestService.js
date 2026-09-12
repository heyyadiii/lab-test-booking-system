import api from './api'

const labTestService = {
  // Get all lab tests (for dropdown)
  getAllLabTests: async () => {
    const response = await api.get('/bookings/lab-tests')
    return response.data
  }
}

export default labTestService
