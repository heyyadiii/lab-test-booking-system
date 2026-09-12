import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import authService from '../services/authService'
import bookingService from '../services/bookingService'
import labTestService from '../services/labTestService'
import reportService from '../services/reportService'
import laboratoryService from '../services/laboratoryService'
import '../styles/ModernPatient.css'

function PatientDashboard() {
  const navigate = useNavigate()
  const [user, setUser] = useState(null)
  const [activeTab, setActiveTab] = useState('slots')
  
  // Lab tests state
  const [labTests, setLabTests] = useState([])
  
  // Laboratories state
  const [laboratories, setLaboratories] = useState([])
  const [selectedLaboratoryId, setSelectedLaboratoryId] = useState('')
  
  // Slots state
  const [slots, setSlots] = useState([])
  const [loadingSlots, setLoadingSlots] = useState(false)
  const [startDate, setStartDate] = useState('')
  const [endDate, setEndDate] = useState('')
  
  // Bookings state
  const [bookings, setBookings] = useState([])
  const [loadingBookings, setLoadingBookings] = useState(false)
  
  // Booking modal state
  const [showBookingModal, setShowBookingModal] = useState(false)
  const [selectedSlot, setSelectedSlot] = useState(null)
  const [selectedLabTestId, setSelectedLabTestId] = useState('')
  const [bookingInProgress, setBookingInProgress] = useState(false)
  
  // View Report modal state
  const [showReportModal, setShowReportModal] = useState(false)
  const [selectedReport, setSelectedReport] = useState(null)
  const [loadingReport, setLoadingReport] = useState(false)
  const [downloadingPDF, setDownloadingPDF] = useState(false)
  
  // Success/Error messages
  const [message, setMessage] = useState({ type: '', text: '' })

  useEffect(() => {
    const currentUser = authService.getCurrentUser()
    if (!currentUser || currentUser.role !== 'PATIENT') {
      navigate('/login')
      return
    }
    setUser(currentUser)
    
    // Set default dates (today to 7 days from now)
    const today = new Date()
    const nextWeek = new Date(today)
    nextWeek.setDate(nextWeek.getDate() + 7)
    
    setStartDate(today.toISOString().split('T')[0])
    setEndDate(nextWeek.toISOString().split('T')[0])
    
    // Fetch lab tests, laboratories and bookings
    fetchLabTests()
    fetchLaboratories()
    fetchBookings()
  }, [navigate])
  
  const fetchLabTests = async () => {
    try {
      const data = await labTestService.getAllLabTests()
      setLabTests(data)
    } catch (error) {
      console.error('Error fetching lab tests:', error)
    }
  }

  const fetchLaboratories = async () => {
    try {
      const data = await laboratoryService.getActiveLaboratories()
      setLaboratories(data)
    } catch (error) {
      console.error('Error fetching laboratories:', error)
    }
  }

  const handleLogout = () => {
    authService.logout()
    navigate('/login')
  }

  const fetchSlots = async () => {
    if (!startDate || !endDate) {
      showMessage('error', 'Please select both start and end dates')
      return
    }

    setLoadingSlots(true)
    try {
      const startTime = `${startDate}T00:00:00`
      const endTime = `${endDate}T23:59:59`
      const labId = selectedLaboratoryId || null
      const data = await bookingService.getAvailableSlots(startTime, endTime, labId)
      setSlots(data)
      if (data.length === 0) {
        showMessage('info', 'No available slots found for selected criteria')
      }
    } catch (error) {
      console.error('Error fetching slots:', error)
      showMessage('error', 'Failed to fetch available slots')
    } finally {
      setLoadingSlots(false)
    }
  }

  const fetchBookings = async () => {
    setLoadingBookings(true)
    try {
      const data = await bookingService.getUserBookings()
      setBookings(data)
    } catch (error) {
      console.error('Error fetching bookings:', error)
    } finally {
      setLoadingBookings(false)
    }
  }

  const handleBookSlot = async (slot) => {
    setSelectedSlot(slot)
    setSelectedLabTestId('')
    setShowBookingModal(true)
  }
  
  const confirmBooking = async () => {
    if (!selectedLabTestId) {
      showMessage('error', 'Please select a lab test')
      return
    }

    setBookingInProgress(true)
    try {
      await bookingService.createBooking(parseInt(selectedSlot.id), parseInt(selectedLabTestId))
      showMessage('success', 'Booking created successfully!')
      setShowBookingModal(false)
      fetchSlots()
      fetchBookings()
    } catch (error) {
      console.error('Error creating booking:', error)
      showMessage('error', error.response?.data?.message || 'Failed to create booking')
    } finally {
      setBookingInProgress(false)
    }
  }

  const handleViewReport = async (sampleId) => {
    setLoadingReport(true)
    setShowReportModal(true)
    setSelectedReport(null)
    
    try {
      const report = await reportService.getReportBySampleId(sampleId)
      setSelectedReport(report)
    } catch (error) {
      console.error('Error fetching report:', error)
      showMessage('error', 'Failed to load report')
      setShowReportModal(false)
    } finally {
      setLoadingReport(false)
    }
  }

  const handleDownloadPDF = async (sampleId) => {
    setDownloadingPDF(true)
    try {
      await reportService.downloadReportPDF(sampleId)
      showMessage('success', 'Report downloaded successfully!')
    } catch (error) {
      console.error('Error downloading PDF:', error)
      showMessage('error', 'Failed to download PDF')
    } finally {
      setDownloadingPDF(false)
    }
  }

  const showMessage = (type, text) => {
    setMessage({ type, text })
    setTimeout(() => setMessage({ type: '', text: '' }), 5000)
  }

  const formatDateTime = (dateTime) => {
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

  if (!user) return null

  const reportedCount = bookings.filter(b => b.sampleState === 'REPORTED').length
  const pendingCount = bookings.filter(b => b.sampleState !== 'REPORTED').length

  return (
    <div className="modern-dashboard">
      {/* Top Navigation */}
      <div className="top-nav">
        <div className="logo-section">
          <i className="bi bi-heart-pulse-fill"></i>
          LabFlow
        </div>
        <div className="nav-icons">
          <button className="icon-btn">
            <i className="bi bi-bell"></i>
          </button>
          <button className="icon-btn">
            <i className="bi bi-gear"></i>
          </button>
          <div className="user-avatar" title={user.username}>
            {user.username.charAt(0).toUpperCase()}
          </div>
          <button className="icon-btn" onClick={handleLogout} title="Logout">
            <i className="bi bi-box-arrow-right"></i>
          </button>
        </div>
      </div>

      {/* Main Content */}
      <div className="main-container">
        {/* Alert Messages */}
        {message.text && (
          <div className={`alert alert-${message.type}`}>
            <i className={`bi bi-${message.type === 'success' ? 'check-circle-fill' : message.type === 'error' ? 'exclamation-circle-fill' : 'info-circle-fill'}`}></i>
            {message.text}
            <button 
              className="close-btn"
              style={{marginLeft: 'auto'}}
              onClick={() => setMessage({ type: '', text: '' })}
            >
              ×
            </button>
          </div>
        )}

        {/* Hero Card */}
        <div className="hero-card">
          <div className="hero-content">
            <h1 className="hero-title">Welcome back, {user.username}! 👋</h1>
            <p className="hero-subtitle">Manage your lab test bookings and view reports</p>
            
            <div className="hero-stats">
              <div className="stat-item" onClick={() => setActiveTab('slots')}>
                <div className="stat-value">{slots.length}</div>
                <div className="stat-label">Available Slots</div>
              </div>

              <div className="stat-item" onClick={() => setActiveTab('bookings')}>
                <div className="stat-value">{bookings.length}</div>
                <div className="stat-label">Total Bookings</div>
              </div>

              <div className="stat-item">
                <div className="stat-value">{pendingCount}</div>
                <div className="stat-label">Pending Tests</div>
              </div>

              <div className="stat-item">
                <div className="stat-value">{reportedCount}</div>
                <div className="stat-label">Reports Ready</div>
              </div>
            </div>
          </div>
        </div>

        {/* Available Slots Tab */}
        {activeTab === 'slots' && (
          <>
            <h2 className="section-title">
              <i className="bi bi-calendar-range"></i>
              Find Available Slots
            </h2>

            {/* Search Section */}
            <div className="search-section">
              <div className="search-inputs">
                <div className="input-group">
                  <label className="input-label">Laboratory</label>
                  <select
                    className="modern-input"
                    value={selectedLaboratoryId}
                    onChange={(e) => setSelectedLaboratoryId(e.target.value)}
                  >
                    <option value="">All Laboratories</option>
                    {laboratories.map(lab => (
                      <option key={lab.id} value={lab.id}>
                        {lab.name} - {lab.city}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="input-group">
                  <label className="input-label">Start Date</label>
                  <input
                    type="date"
                    className="modern-input"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                  />
                </div>
                <div className="input-group">
                  <label className="input-label">End Date</label>
                  <input
                    type="date"
                    className="modern-input"
                    value={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                  />
                </div>
                <button 
                  className="search-btn"
                  onClick={fetchSlots}
                  disabled={loadingSlots}
                >
                  {loadingSlots ? (
                    <>
                      <div className="spinner"></div>
                      Loading...
                    </>
                  ) : (
                    <>
                      <i className="bi bi-search"></i>
                      Search Slots
                    </>
                  )}
                </button>
              </div>
            </div>

            {/* Slots Grid */}
            {slots.length > 0 ? (
              <div className="slots-grid">
                {slots.map(slot => (
                  <div key={slot.id} className="slot-card">
                    <div className="slot-top">
                      <span className="slot-id">#{slot.id}</span>
                      <span className={`slot-badge ${slot.remainingCapacity > 0 ? 'available' : 'full'}`}>
                        {slot.remainingCapacity > 0 ? 'Available' : 'Full'}
                      </span>
                    </div>
                    <div style={{marginBottom: '0.75rem', color: '#667eea', fontWeight: '600', fontSize: '0.95rem'}}>
                      <i className="bi bi-hospital"></i> {slot.laboratoryName}
                    </div>
                    <div style={{marginBottom: '0.5rem', color: '#666', fontSize: '0.9rem'}}>
                      <i className="bi bi-geo-alt"></i> {slot.laboratoryCity}
                    </div>
                    <div className="slot-time">
                      <i className="bi bi-clock"></i>
                      {formatDateTime(slot.slotTime)}
                    </div>
                    <div className="slot-capacity">
                      <span className="capacity-label">
                        <i className="bi bi-people"></i>
                        Capacity
                      </span>
                      <span className="capacity-value">{slot.remainingCapacity} / {slot.totalCapacity}</span>
                    </div>
                    <button
                      className="book-btn"
                      onClick={() => handleBookSlot(slot)}
                      disabled={slot.remainingCapacity === 0}
                    >
                      <i className="bi bi-plus-circle"></i>
                      Book Slot
                    </button>
                  </div>
                ))}
              </div>
            ) : (
              <div className="empty-state">
                <div className="empty-icon">
                  <i className="bi bi-calendar-x"></i>
                </div>
                <div className="empty-title">No Slots Found</div>
                <div className="empty-subtitle">Select dates and click search to find available slots</div>
              </div>
            )}
          </>
        )}

        {/* My Bookings Tab */}
        {activeTab === 'bookings' && (
          <>
            <div className="search-header">
              <h2 className="section-title">
                <i className="bi bi-list-check"></i>
                My Bookings
              </h2>
              <button className="search-btn" onClick={fetchBookings}>
                <i className="bi bi-arrow-clockwise"></i>
                Refresh
              </button>
            </div>

            {loadingBookings ? (
              <div className="empty-state">
                <div className="spinner"></div>
                <div className="empty-subtitle" style={{marginTop: '1rem'}}>Loading bookings...</div>
              </div>
            ) : bookings.length > 0 ? (
              <div className="bookings-container">
                <table className="bookings-table">
                  <thead>
                    <tr>
                      <th>Booking ID</th>
                      <th>Lab Test</th>
                      <th>Slot Time</th>
                      <th>Sample ID</th>
                      <th>Status</th>
                      <th>Booked At</th>
                      <th>Action</th>
                    </tr>
                  </thead>
                  <tbody>
                    {bookings.map(booking => (
                      <tr key={booking.id}>
                        <td><strong>#{booking.id}</strong></td>
                        <td>{booking.labTestName}</td>
                        <td>{formatDateTime(booking.slotTime)}</td>
                        <td>{booking.sampleId || 'N/A'}</td>
                        <td>
                          <span className={`status-badge ${getStatusBadge(booking.sampleState)}`}>
                            {booking.sampleState}
                          </span>
                        </td>
                        <td>{formatDateTime(booking.createdAt)}</td>
                        <td>
                          {booking.sampleState === 'REPORTED' && booking.sampleId && (
                            <button
                              className="view-report-btn"
                              onClick={() => handleViewReport(booking.sampleId)}
                            >
                              <i className="bi bi-file-earmark-text"></i>
                              View Report
                            </button>
                          )}
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            ) : (
              <div className="empty-state">
                <div className="empty-icon">
                  <i className="bi bi-inbox"></i>
                </div>
                <div className="empty-title">No Bookings Yet</div>
                <div className="empty-subtitle">Book a test to get started!</div>
              </div>
            )}
          </>
        )}
      </div>

      {/* Booking Modal */}
      {showBookingModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <div className="modal-header">
              <h3 className="modal-title">
                <i className="bi bi-calendar-plus"></i>
                Book Lab Test
              </h3>
              <button 
                className="close-btn"
                onClick={() => setShowBookingModal(false)}
                disabled={bookingInProgress}
              >
                ×
              </button>
            </div>
            <div className="modal-body">
              <div style={{marginBottom: '1.5rem'}}>
                <label className="input-label">Selected Slot</label>
                <div style={{padding: '1rem', background: '#f8f9fa', borderRadius: '12px', display: 'flex', alignItems: 'center', gap: '0.5rem'}}>
                  <i className="bi bi-clock"></i>
                  {selectedSlot && formatDateTime(selectedSlot.slotTime)}
                </div>
              </div>
              <div className="input-group">
                <label className="input-label">Select Lab Test</label>
                <select 
                  className="modern-input"
                  value={selectedLabTestId}
                  onChange={(e) => setSelectedLabTestId(e.target.value)}
                  disabled={bookingInProgress}
                >
                  <option value="">-- Choose a test --</option>
                  {labTests.map(test => (
                    <option key={test.id} value={test.id}>
                      {test.name} ({test.averageProcessingMinutes} min)
                    </option>
                  ))}
                </select>
              </div>
            </div>
            <div className="modal-footer">
              <button 
                className="btn-secondary"
                onClick={() => setShowBookingModal(false)}
                disabled={bookingInProgress}
              >
                Cancel
              </button>
              <button 
                className="btn-primary"
                onClick={confirmBooking}
                disabled={bookingInProgress || !selectedLabTestId}
              >
                {bookingInProgress ? (
                  <>
                    <div className="spinner"></div>
                    Booking...
                  </>
                ) : (
                  <>
                    <i className="bi bi-check-circle"></i>
                    Confirm Booking
                  </>
                )}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* View Report Modal */}
      {showReportModal && (
        <div className="modal-overlay">
          <div className="modal-content" style={{maxWidth: '800px'}}>
            <div className="modal-header">
              <h3 className="modal-title">
                <i className="bi bi-file-earmark-medical-fill"></i>
                Lab Test Report
              </h3>
              <button 
                className="close-btn"
                onClick={() => setShowReportModal(false)}
              >
                ×
              </button>
            </div>
            <div className="modal-body">
              {loadingReport ? (
                <div className="empty-state">
                  <div className="spinner"></div>
                  <div className="empty-subtitle" style={{marginTop: '1rem'}}>Loading report...</div>
                </div>
              ) : selectedReport ? (
                <>
                  <div style={{display: 'grid', gridTemplateColumns: 'repeat(2, 1fr)', gap: '1.5rem', marginBottom: '2rem'}}>
                    <div>
                      <label className="input-label">Sample ID</label>
                      <div><strong>#{selectedReport.sampleId}</strong></div>
                    </div>
                    <div>
                      <label className="input-label">Patient Name</label>
                      <div><strong>{selectedReport.patientName}</strong></div>
                    </div>
                    <div>
                      <label className="input-label">Lab Test</label>
                      <div><strong>{selectedReport.labTestName}</strong></div>
                    </div>
                    <div>
                      <label className="input-label">Report Date</label>
                      <div><strong>{formatDateTime(selectedReport.reportedAt)}</strong></div>
                    </div>
                  </div>

                  <hr style={{border: 'none', borderTop: '1px solid #f8f9fa', margin: '2rem 0'}} />

                  <div style={{marginBottom: '1.5rem'}}>
                    <h6 style={{color: '#667eea', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem'}}>
                      <i className="bi bi-clipboard-data"></i>
                      Test Results
                    </h6>
                    <div style={{padding: '1rem', background: '#f8f9fa', borderRadius: '12px'}}>
                      <pre style={{whiteSpace: 'pre-wrap', fontFamily: 'inherit', margin: 0}}>
                        {selectedReport.results}
                      </pre>
                    </div>
                  </div>

                  <div style={{marginBottom: '1.5rem'}}>
                    <h6 style={{color: '#667eea', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem'}}>
                      <i className="bi bi-search"></i>
                      Findings
                    </h6>
                    <div style={{padding: '1rem', background: '#f8f9fa', borderRadius: '12px'}}>
                      <pre style={{whiteSpace: 'pre-wrap', fontFamily: 'inherit', margin: 0}}>
                        {selectedReport.findings}
                      </pre>
                    </div>
                  </div>

                  {selectedReport.recommendations && (
                    <div style={{marginBottom: '1.5rem'}}>
                      <h6 style={{color: '#667eea', marginBottom: '1rem', display: 'flex', alignItems: 'center', gap: '0.5rem'}}>
                        <i className="bi bi-lightbulb"></i>
                        Recommendations
                      </h6>
                      <div style={{padding: '1rem', background: '#f8f9fa', borderRadius: '12px'}}>
                        <pre style={{whiteSpace: 'pre-wrap', fontFamily: 'inherit', margin: 0}}>
                          {selectedReport.recommendations}
                        </pre>
                      </div>
                    </div>
                  )}
                </>
              ) : (
                <div className="empty-state">
                  <div className="empty-icon">
                    <i className="bi bi-exclamation-circle"></i>
                  </div>
                  <div className="empty-title">Report not found</div>
                </div>
              )}
            </div>
            <div className="modal-footer">
              <button 
                className="btn-secondary"
                onClick={() => setShowReportModal(false)}
              >
                Close
              </button>
              {selectedReport && (
                <button 
                  className="btn-primary"
                  onClick={() => handleDownloadPDF(selectedReport.sampleId)}
                  disabled={downloadingPDF}
                >
                  {downloadingPDF ? (
                    <>
                      <div className="spinner"></div>
                      Downloading...
                    </>
                  ) : (
                    <>
                      <i className="bi bi-download"></i>
                      Download PDF
                    </>
                  )}
                </button>
              )}
            </div>
          </div>
        </div>
      )}
    </div>
  )
}

export default PatientDashboard
