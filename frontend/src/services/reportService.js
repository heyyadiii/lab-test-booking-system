import api from './api'

const reportService = {
  // Create/Upload report (Technician)
  createReport: async (sampleId, results, findings, recommendations) => {
    const response = await api.post('/reports', {
      sampleId,
      results,
      findings,
      recommendations
    })
    return response.data
  },

  // Get report by sample ID (Patient)
  getReportBySampleId: async (sampleId) => {
    const response = await api.get(`/reports/${sampleId}`)
    return response.data
  },

  // Download PDF report
  downloadReportPDF: async (sampleId) => {
    const response = await api.get(`/reports/${sampleId}/pdf`, {
      responseType: 'blob' // Important for file download
    })
    
    // Create download link
    const url = window.URL.createObjectURL(new Blob([response.data]))
    const link = document.createElement('a')
    link.href = url
    link.setAttribute('download', `report-${sampleId}.pdf`)
    document.body.appendChild(link)
    link.click()
    link.remove()
    
    return response.data
  }
}

export default reportService
