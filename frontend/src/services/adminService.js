import api from './api'

const adminService = {
  // Create time slot
  createTimeSlot: async (laboratoryId, slotTime, totalCapacity) => {
    const response = await api.post('/admin/time-slots', {
      laboratoryId,
      slotTime,
      totalCapacity
    })
    return response.data
  },

  // Update slot capacity
  updateSlotCapacity: async (slotId, capacity) => {
    const response = await api.put(`/admin/time-slots/${slotId}`, null, {
      params: { capacity }
    })
    return response.data
  },

  // Create lab test
  createLabTest: async (name, description, averageProcessingMinutes) => {
    const response = await api.post('/admin/lab-tests', {
      name,
      description,
      averageProcessingMinutes
    })
    return response.data
  },

  // Get all lab tests
  getAllLabTests: async () => {
    const response = await api.get('/bookings/lab-tests')
    return response.data
  }
}

export default adminService
