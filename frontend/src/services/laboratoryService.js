import api from './api'

const laboratoryService = {
  // Get all active laboratories (Public)
  getActiveLaboratories: async () => {
    const response = await api.get('/laboratories')
    return response.data
  },

  // Get laboratories by city
  getLaboratoriesByCity: async (city) => {
    const response = await api.get(`/laboratories/city/${city}`)
    return response.data
  },

  // Get laboratories by state
  getLaboratoriesByState: async (state) => {
    const response = await api.get(`/laboratories/state/${state}`)
    return response.data
  },

  // Get laboratory by ID
  getLaboratoryById: async (id) => {
    const response = await api.get(`/laboratories/${id}`)
    return response.data
  },

  // Admin: Get all laboratories (including inactive)
  getAllLaboratories: async () => {
    const response = await api.get('/laboratories/all')
    return response.data
  },

  // Admin: Create laboratory
  createLaboratory: async (laboratoryData) => {
    const response = await api.post('/laboratories', laboratoryData)
    return response.data
  },

  // Admin: Update laboratory
  updateLaboratory: async (id, laboratoryData) => {
    const response = await api.put(`/laboratories/${id}`, laboratoryData)
    return response.data
  },

  // Admin: Deactivate laboratory
  deactivateLaboratory: async (id) => {
    const response = await api.delete(`/laboratories/${id}`)
    return response.data
  },

  // Admin: Activate laboratory
  activateLaboratory: async (id) => {
    const response = await api.put(`/laboratories/${id}/activate`)
    return response.data
  }
}

export default laboratoryService
