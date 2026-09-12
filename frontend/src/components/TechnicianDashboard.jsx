import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import authService from '../services/authService'
import sampleService from '../services/sampleService'
import reportService from '../services/reportService'

function TechnicianDashboard() {
  const navigate = useNavigate()
  const [user, setUser] = useState(null)
  const [samples, setSamples] = useState([])
  const [loading, setLoading] = useState(false)
  const [selectedState, setSelectedState] = useState('BOOKED')
  const [message, setMessage] = useState({ type: '', text: '' })
  const [actionInProgress, setActionInProgress] = useState(null)

  // Upload Report Modal State
  const [showReportModal, setShowReportModal] = useState(false)
  const [selectedSample, setSelectedSample] = useState(null)
  const [reportResults, setReportResults] = useState('')
  const [reportFindings, setReportFindings] = useState('')
  const [reportRecommendations, setReportRecommendations] = useState('')
  const [uploadingReport, setUploadingReport] = useState(false)

  useEffect(() => {
    const currentUser = authService.getCurrentUser()
    if (!currentUser || currentUser.role !== 'LAB_TECHNICIAN') {
      navigate('/login')
      return
    }
    setUser(currentUser)
    // Fetch samples after user is set
    fetchSamplesForState(selectedState)
  }, [navigate])

  useEffect(() => {
    if (user) {
      fetchSamplesForState(selectedState)
    }
  }, [selectedState])

  const fetchSamplesForState = async (state) => {
    setLoading(true)
    try {
      const data = await sampleService.getSamplesByState(state)
      setSamples(data)
    } catch (error) {
      console.error('Error fetching samples:', error)
      showMessage('error', 'Failed to fetch samples')
    } finally {
      setLoading(false)
    }
  }

  const fetchSamples = () => {
    fetchSamplesForState(selectedState)
  }

  const handleStatusUpdate = async (sampleId, newState) => {
    console.log('Updating sample:', sampleId, 'to state:', newState)
    setActionInProgress(sampleId)
    try {
      await sampleService.updateSampleStatus(sampleId, newState)
      showMessage('success', `Sample status updated to ${newState}`)
      fetchSamplesForState(selectedState)
    } catch (error) {
      console.error('Error updating status:', error)
      console.error('Error response:', error.response?.data)
      showMessage('error', error.response?.data?.message || 'Failed to update status')
    } finally {
      setActionInProgress(null)
    }
  }

  const handleUploadReport = (sample) => {
    setSelectedSample(sample)
    setReportResults('')
    setReportFindings('')
    setReportRecommendations('')
    setShowReportModal(true)
  }

  const submitReport = async () => {
    if (!reportResults || !reportFindings) {
      showMessage('error', 'Please fill Results and Findings fields')
      return
    }

    setUploadingReport(true)
    try {
      await reportService.createReport(
        selectedSample.sampleId,
        reportResults,
        reportFindings,
        reportRecommendations
      )
      showMessage('success', 'Report uploaded successfully!')
      setShowReportModal(false)
      fetchSamplesForState(selectedState)
    } catch (error) {
      console.error('Error uploading report:', error)
      showMessage('error', error.response?.data?.message || 'Failed to upload report')
    } finally {
      setUploadingReport(false)
    }
  }

  const handleLogout = () => {
    authService.logout()
    navigate('/login')
  }

  const showMessage = (type, text) => {
    setMessage({ type, text })
    setTimeout(() => setMessage({ type: '', text: '' }), 5000)
  }

  const formatDateTime = (dateTime) => {
    if (!dateTime) return 'N/A'
    return new Date(dateTime).toLocaleString('en-IN', {
      dateStyle: 'medium',
      timeStyle: 'short'
    })
  }

  const getStatusBadge = (status) => {
    const badges = {
      'BOOKED': 'bg-warning text-dark',
      'COLLECTED': 'bg-secondary',
      'IN_TEST': 'bg-info text-dark',
      'COMPLETED': 'bg-success',
      'REPORTED': 'bg-dark'
    }
    return badges[status] || 'bg-secondary'
  }

  const getNextAction = (currentState) => {
    const actions = {
      'BOOKED': { label: 'Collect Sample', nextState: 'COLLECTED', icon: 'bi-droplet', type: 'status' },
      'COLLECTED': { label: 'Start Test', nextState: 'IN_TEST', icon: 'bi-play-circle', type: 'status' },
      'IN_TEST': { label: 'Complete Test', nextState: 'COMPLETED', icon: 'bi-check-circle', type: 'status' },
      'COMPLETED': { label: 'Upload Report', icon: 'bi-file-earmark-arrow-up', type: 'report' }
    }
    return actions[currentState]
  }

  if (!user) return null

  return (
    <div className="min-vh-100 bg-light">
      {/* Navbar */}
      <nav className="navbar navbar-expand-lg navbar-dark bg-success shadow-sm">
        <div className="container-fluid px-4">
          <span className="navbar-brand">
            <i className="bi bi-clipboard2-pulse-fill me-2"></i>
            Lab Technician Portal
          </span>
          <div className="d-flex align-items-center">
            <span className="text-white me-3">
              <i className="bi bi-person-circle me-2"></i>
              {user.username}
            </span>
            <button className="btn btn-outline-light btn-sm" onClick={handleLogout}>
              <i className="bi bi-box-arrow-right me-1"></i>
              Logout
            </button>
          </div>
        </div>
      </nav>

      {/* Main Content */}
      <div className="container-fluid px-4 py-4">
        {/* Alert Messages */}
        {message.text && (
          <div className={`alert alert-${message.type === 'error' ? 'danger' : message.type} alert-dismissible fade show`}>
            {message.text}
            <button type="button" className="btn-close" onClick={() => setMessage({ type: '', text: '' })}></button>
          </div>
        )}

        <div className="card shadow-sm border-0">
          <div className="card-body p-4">
            <div className="d-flex justify-content-between align-items-center mb-4">
              <h5 className="card-title mb-0">
                <i className="bi bi-list-task me-2"></i>
                Sample Queue
              </h5>
              <button className="btn btn-sm btn-outline-success" onClick={fetchSamples}>
                <i className="bi bi-arrow-clockwise me-1"></i>
                Refresh
              </button>
            </div>

            {/* State Filter */}
            <div className="mb-4">
              <label className="form-label small fw-semibold">Filter by Status</label>
              <select 
                className="form-select"
                value={selectedState}
                onChange={(e) => setSelectedState(e.target.value)}
              >
                <option value="BOOKED">Booked (Ready to Collect)</option>
                <option value="COLLECTED">Collected (Ready to Test)</option>
                <option value="IN_TEST">In Test (Testing)</option>
                <option value="COMPLETED">Completed (Ready to Report)</option>
                <option value="REPORTED">Reported</option>
              </select>
            </div>

            {/* Samples Table */}
            {loading ? (
              <div className="text-center py-5">
                <div className="spinner-border text-success"></div>
                <p className="mt-3 text-muted">Loading samples...</p>
              </div>
            ) : samples.length > 0 ? (
              <div className="table-responsive">
                <table className="table table-hover">
                  <thead>
                    <tr>
                      <th>Sample ID</th>
                      <th>Patient</th>
                      <th>Lab Test</th>
                      <th>Status</th>
                      <th>Created At</th>
                      <th>Last Updated</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {samples.map(sample => {
                      const action = getNextAction(sample.state)
                      return (
                        <tr key={sample.sampleId}>
                          <td>
                            <strong>#{sample.sampleId}</strong>
                          </td>
                          <td>
                            <div>{sample.patientName}</div>
                            <small className="text-muted">{sample.patientUsername}</small>
                          </td>
                          <td>{sample.labTestName}</td>
                          <td>
                            <span className={`badge ${getStatusBadge(sample.state)}`}>
                              {sample.state}
                            </span>
                          </td>
                          <td>{formatDateTime(sample.createdAt)}</td>
                          <td>
                            {formatDateTime(
                              sample.reportedAt || sample.completedAt || 
                              sample.testStartedAt || sample.collectedAt || 
                              sample.createdAt
                            )}
                          </td>
                          <td>
                            {action && (
                              <button
                                className="btn btn-sm btn-success"
                                onClick={() => action.type === 'report' 
                                  ? handleUploadReport(sample) 
                                  : handleStatusUpdate(sample.sampleId, action.nextState)
                                }
                                disabled={actionInProgress === sample.sampleId}
                              >
                                {actionInProgress === sample.sampleId ? (
                                  <>
                                    <span className="spinner-border spinner-border-sm me-1"></span>
                                    Processing...
                                  </>
                                ) : (
                                  <>
                                    <i className={`${action.icon} me-1`}></i>
                                    {action.label}
                                  </>
                                )}
                              </button>
                            )}
                          </td>
                        </tr>
                      )
                    })}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="text-center py-5 text-muted">
                <i className="bi bi-inbox" style={{fontSize: '3rem'}}></i>
                <p className="mt-3">No samples in {selectedState} state</p>
              </div>
            )}
          </div>
        </div>
      </div>

      {/* Upload Report Modal */}
      {showReportModal && (
        <div className="modal show d-block" style={{backgroundColor: 'rgba(0,0,0,0.5)'}}>
          <div className="modal-dialog modal-dialog-centered modal-lg">
            <div className="modal-content">
              <div className="modal-header">
                <h5 className="modal-title">
                  <i className="bi bi-file-earmark-medical me-2"></i>
                  Upload Lab Report
                </h5>
                <button 
                  type="button" 
                  className="btn-close" 
                  onClick={() => setShowReportModal(false)}
                  disabled={uploadingReport}
                ></button>
              </div>
              <div className="modal-body">
                <div className="mb-3">
                  <label className="form-label small text-muted">Sample Information</label>
                  <div className="p-3 bg-light rounded">
                    <div className="row">
                      <div className="col-md-6">
                        <strong>Sample ID:</strong> #{selectedSample?.sampleId}
                      </div>
                      <div className="col-md-6">
                        <strong>Patient:</strong> {selectedSample?.patientName}
                      </div>
                      <div className="col-md-6 mt-2">
                        <strong>Test:</strong> {selectedSample?.labTestName}
                      </div>
                      <div className="col-md-6 mt-2">
                        <strong>Status:</strong> <span className="badge bg-success">COMPLETED</span>
                      </div>
                    </div>
                  </div>
                </div>

                <div className="mb-3">
                  <label className="form-label fw-semibold">
                    Test Results <span className="text-danger">*</span>
                  </label>
                  <textarea
                    className="form-control"
                    rows="4"
                    value={reportResults}
                    onChange={(e) => setReportResults(e.target.value)}
                    placeholder="Enter test results (e.g., Hemoglobin: 14.5 g/dL, WBC: 7500/μL)"
                    disabled={uploadingReport}
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label fw-semibold">
                    Findings <span className="text-danger">*</span>
                  </label>
                  <textarea
                    className="form-control"
                    rows="3"
                    value={reportFindings}
                    onChange={(e) => setReportFindings(e.target.value)}
                    placeholder="Enter clinical findings and observations"
                    disabled={uploadingReport}
                  />
                </div>

                <div className="mb-3">
                  <label className="form-label fw-semibold">
                    Recommendations
                  </label>
                  <textarea
                    className="form-control"
                    rows="3"
                    value={reportRecommendations}
                    onChange={(e) => setReportRecommendations(e.target.value)}
                    placeholder="Enter recommendations (optional)"
                    disabled={uploadingReport}
                  />
                </div>
              </div>
              <div className="modal-footer">
                <button 
                  type="button" 
                  className="btn btn-secondary"
                  onClick={() => setShowReportModal(false)}
                  disabled={uploadingReport}
                >
                  Cancel
                </button>
                <button 
                  type="button" 
                  className="btn btn-success"
                  onClick={submitReport}
                  disabled={uploadingReport || !reportResults || !reportFindings}
                >
                  {uploadingReport ? (
                    <>
                      <span className="spinner-border spinner-border-sm me-2"></span>
                      Uploading...
                    </>
                  ) : (
                    <>
                      <i className="bi bi-upload me-2"></i>
                      Upload Report
                    </>
                  )}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default TechnicianDashboard
