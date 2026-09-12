import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import authService from '../services/authService'
import adminService from '../services/adminService'
import laboratoryService from '../services/laboratoryService'

function AdminDashboard() {
  const navigate = useNavigate()
  const [user, setUser] = useState(null)
  const [activeTab, setActiveTab] = useState('slots')
  const [message, setMessage] = useState({ type: '', text: '' })

  // Laboratories
  const [laboratories, setLaboratories] = useState([])

  // Time Slot Form
  const [selectedLabId, setSelectedLabId] = useState('')
  const [slotDate, setSlotDate] = useState('')
  const [slotTime, setSlotTime] = useState('')
  const [slotCapacity, setSlotCapacity] = useState(10)
  const [creatingSlot, setCreatingSlot] = useState(false)

  // Lab Test Form
  const [testName, setTestName] = useState('')
  const [testDescription, setTestDescription] = useState('')
  const [testProcessingTime, setTestProcessingTime] = useState(30)
  const [creatingTest, setCreatingTest] = useState(false)

  // Lab Tests List
  const [labTests, setLabTests] = useState([])

  useEffect(() => {
    const currentUser = authService.getCurrentUser()
    if (!currentUser || currentUser.role !== 'ADMIN') {
      navigate('/login')
      return
    }
    setUser(currentUser)
    fetchLaboratories()
    fetchLabTests()
  }, [navigate])

  const fetchLaboratories = async () => {
    try {
      const data = await laboratoryService.getActiveLaboratories()
      setLaboratories(data)
      if (data.length > 0) {
        setSelectedLabId(data[0].id) // Set first lab as default
      }
    } catch (error) {
      console.error('Error fetching laboratories:', error)
    }
  }

  const fetchLabTests = async () => {
    try {
      const data = await adminService.getAllLabTests()
      setLabTests(data)
    } catch (error) {
      console.error('Error fetching lab tests:', error)
    }
  }

  const handleCreateSlot = async (e) => {
    e.preventDefault()
    if (!selectedLabId) {
      showMessage('error', 'Please select a laboratory')
      return
    }
    if (!slotDate || !slotTime) {
      showMessage('error', 'Please select date and time')
      return
    }

    setCreatingSlot(true)
    try {
      const dateTime = `${slotDate}T${slotTime}:00`
      await adminService.createTimeSlot(selectedLabId, dateTime, slotCapacity)
      showMessage('success', 'Time slot created successfully!')
      // Reset form
      setSlotDate('')
      setSlotTime('')
      setSlotCapacity(10)
    } catch (error) {
      console.error('Error creating slot:', error)
      showMessage('error', error.response?.data?.message || 'Failed to create time slot')
    } finally {
      setCreatingSlot(false)
    }
  }

  const handleCreateTest = async (e) => {
    e.preventDefault()
    if (!testName || !testDescription) {
      showMessage('error', 'Please fill all fields')
      return
    }

    setCreatingTest(true)
    try {
      await adminService.createLabTest(testName, testDescription, testProcessingTime)
      showMessage('success', 'Lab test created successfully!')
      // Reset form
      setTestName('')
      setTestDescription('')
      setTestProcessingTime(30)
      fetchLabTests()
    } catch (error) {
      console.error('Error creating test:', error)
      showMessage('error', error.response?.data?.message || 'Failed to create lab test')
    } finally {
      setCreatingTest(false)
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

  // Set default date to today
  useEffect(() => {
    const today = new Date().toISOString().split('T')[0]
    setSlotDate(today)
  }, [])

  if (!user) return null

  return (
    <div className="min-vh-100 bg-light">
      {/* Navbar */}
      <nav className="navbar navbar-expand-lg navbar-dark bg-danger shadow-sm">
        <div className="container-fluid px-4">
          <span className="navbar-brand">
            <i className="bi bi-shield-fill-check me-2"></i>
            Admin Portal
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

        {/* Tabs */}
        <ul className="nav nav-tabs mb-4">
          <li className="nav-item">
            <button 
              className={`nav-link ${activeTab === 'slots' ? 'active' : ''}`}
              onClick={() => setActiveTab('slots')}
            >
              <i className="bi bi-calendar-plus me-2"></i>
              Create Time Slots
            </button>
          </li>
          <li className="nav-item">
            <button 
              className={`nav-link ${activeTab === 'tests' ? 'active' : ''}`}
              onClick={() => setActiveTab('tests')}
            >
              <i className="bi bi-clipboard-plus me-2"></i>
              Manage Lab Tests
            </button>
          </li>
        </ul>

        {/* Create Time Slots Tab */}
        {activeTab === 'slots' && (
          <div className="row">
            <div className="col-md-6">
              <div className="card shadow-sm border-0">
                <div className="card-body p-4">
                  <h5 className="card-title mb-4">
                    <i className="bi bi-calendar-plus me-2"></i>
                    Create New Time Slot
                  </h5>

                  <form onSubmit={handleCreateSlot}>
                    <div className="mb-3">
                      <label className="form-label fw-semibold">Laboratory</label>
                      <select
                        className="form-select"
                        value={selectedLabId}
                        onChange={(e) => setSelectedLabId(e.target.value)}
                        required
                      >
                        <option value="">Select Laboratory</option>
                        {laboratories.map(lab => (
                          <option key={lab.id} value={lab.id}>
                            {lab.name} - {lab.city}
                          </option>
                        ))}
                      </select>
                    </div>

                    <div className="mb-3">
                      <label className="form-label fw-semibold">Date</label>
                      <input
                        type="date"
                        className="form-control"
                        value={slotDate}
                        onChange={(e) => setSlotDate(e.target.value)}
                        required
                      />
                    </div>

                    <div className="mb-3">
                      <label className="form-label fw-semibold">Time</label>
                      <input
                        type="time"
                        className="form-control"
                        value={slotTime}
                        onChange={(e) => setSlotTime(e.target.value)}
                        required
                      />
                    </div>

                    <div className="mb-4">
                      <label className="form-label fw-semibold">Capacity</label>
                      <input
                        type="number"
                        className="form-control"
                        value={slotCapacity}
                        onChange={(e) => setSlotCapacity(parseInt(e.target.value))}
                        min="1"
                        max="50"
                        required
                      />
                      <small className="text-muted">Number of patients that can book this slot</small>
                    </div>

                    <button 
                      type="submit" 
                      className="btn btn-danger w-100"
                      disabled={creatingSlot}
                    >
                      {creatingSlot ? (
                        <>
                          <span className="spinner-border spinner-border-sm me-2"></span>
                          Creating...
                        </>
                      ) : (
                        <>
                          <i className="bi bi-plus-circle me-2"></i>
                          Create Time Slot
                        </>
                      )}
                    </button>
                  </form>
                </div>
              </div>
            </div>

            <div className="col-md-6">
              <div className="card shadow-sm border-0 bg-light">
                <div className="card-body p-4">
                  <h6 className="text-muted mb-3">
                    <i className="bi bi-info-circle me-2"></i>
                    Quick Tips
                  </h6>
                  <ul className="small text-muted">
                    <li className="mb-2">Create slots for upcoming days to allow patients to book</li>
                    <li className="mb-2">Typical lab hours: 9 AM - 5 PM</li>
                    <li className="mb-2">Avoid lunch hours: 12 PM - 2 PM</li>
                    <li className="mb-2">Recommended capacity: 10-20 patients per slot</li>
                    <li>You can create multiple slots for the same day</li>
                  </ul>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Manage Lab Tests Tab */}
        {activeTab === 'tests' && (
          <div className="row">
            <div className="col-md-6">
              <div className="card shadow-sm border-0">
                <div className="card-body p-4">
                  <h5 className="card-title mb-4">
                    <i className="bi bi-clipboard-plus me-2"></i>
                    Create New Lab Test
                  </h5>

                  <form onSubmit={handleCreateTest}>
                    <div className="mb-3">
                      <label className="form-label fw-semibold">Test Name</label>
                      <input
                        type="text"
                        className="form-control"
                        value={testName}
                        onChange={(e) => setTestName(e.target.value)}
                        placeholder="e.g., Blood Test, X-Ray"
                        required
                      />
                    </div>

                    <div className="mb-3">
                      <label className="form-label fw-semibold">Description</label>
                      <textarea
                        className="form-control"
                        value={testDescription}
                        onChange={(e) => setTestDescription(e.target.value)}
                        placeholder="Brief description of the test"
                        rows="3"
                        required
                      />
                    </div>

                    <div className="mb-4">
                      <label className="form-label fw-semibold">Processing Time (minutes)</label>
                      <input
                        type="number"
                        className="form-control"
                        value={testProcessingTime}
                        onChange={(e) => setTestProcessingTime(parseInt(e.target.value))}
                        min="5"
                        max="300"
                        required
                      />
                      <small className="text-muted">Average time to complete this test</small>
                    </div>

                    <button 
                      type="submit" 
                      className="btn btn-danger w-100"
                      disabled={creatingTest}
                    >
                      {creatingTest ? (
                        <>
                          <span className="spinner-border spinner-border-sm me-2"></span>
                          Creating...
                        </>
                      ) : (
                        <>
                          <i className="bi bi-plus-circle me-2"></i>
                          Create Lab Test
                        </>
                      )}
                    </button>
                  </form>
                </div>
              </div>
            </div>

            <div className="col-md-6">
              <div className="card shadow-sm border-0">
                <div className="card-body p-4">
                  <h5 className="card-title mb-4">
                    <i className="bi bi-list-ul me-2"></i>
                    Existing Lab Tests
                  </h5>

                  {labTests.length > 0 ? (
                    <div className="list-group">
                      {labTests.map(test => (
                        <div key={test.id} className="list-group-item">
                          <div className="d-flex justify-content-between align-items-start">
                            <div>
                              <h6 className="mb-1">{test.name}</h6>
                              <p className="mb-1 small text-muted">{test.description}</p>
                              <small className="text-muted">
                                <i className="bi bi-clock me-1"></i>
                                {test.averageProcessingMinutes} minutes
                              </small>
                            </div>
                          </div>
                        </div>
                      ))}
                    </div>
                  ) : (
                    <div className="text-center py-4 text-muted">
                      <i className="bi bi-inbox" style={{fontSize: '2rem'}}></i>
                      <p className="mt-2">No lab tests created yet</p>
                    </div>
                  )}
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  )
}

export default AdminDashboard
