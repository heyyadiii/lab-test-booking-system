import api from './api'

const sampleService = {
  // Get samples by state (for technician queue)
  getSamplesByState: async (state) => {
    const response = await api.get(`/samples/queue/${state}`)
    return response.data
  },

  // Update sample status
  updateSampleStatus: async (sampleId, newState) => {
    const response = await api.put(`/samples/${sampleId}/status`, {
      newState
    })
    return response.data
  },

  // Get processing estimation
  getProcessingEstimation: async (sampleId) => {
    const response = await api.get(`/samples/${sampleId}/estimation`)
    return response.data
  }
}

export default sampleService
