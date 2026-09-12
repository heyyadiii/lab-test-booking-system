import api from './api'

const bookingService = {
  // Get available time slots (with optional laboratory filter)
  getAvailableSlots: async (startTime, endTime, laboratoryId = null) => {
    const params = { startTime, endTime }
    if (laboratoryId) {
      params.laboratoryId = laboratoryId
    }
    const response = await api.get('/bookings/slots', { params })
    return response.data
  },

  // Create booking
  createBooking: async (timeSlotId, labTestId) => {
    const response = await api.post('/bookings', {
      timeSlotId,
      labTestId
    })
    return response.data
  },

  // Get user's bookings
  getUserBookings: async () => {
    const response = await api.get('/bookings')
    return response.data
  },

  // Get booking by ID
  getBookingById: async (bookingId) => {
    const response = await api.get(`/bookings/${bookingId}`)
    return response.data
  }
}

export default bookingService
