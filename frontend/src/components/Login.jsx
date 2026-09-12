import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import authService from '../services/authService'

function Login() {
  const navigate = useNavigate()

  const [formData, setFormData] = useState({
    username: '',
    password: ''
  })

  const [errors, setErrors] = useState({})
  const [loading, setLoading] = useState(false)
  const [apiError, setApiError] = useState('')

  const handleChange = (e) => {
    const { name, value } = e.target

    setFormData(prev => ({
      ...prev,
      [name]: value
    }))

    if (errors[name]) {
      setErrors(prev => ({
        ...prev,
        [name]: ''
      }))
    }
  }

  const validate = () => {
    const newErrors = {}

    if (!formData.username.trim()) {
      newErrors.username = 'Username is required'
    }

    if (!formData.password) {
      newErrors.password = 'Password is required'
    } else if (formData.password.length < 6) {
      newErrors.password = 'Password must be at least 6 characters'
    }

    return newErrors
  }

  const handleSubmit = async (e) => {
    e.preventDefault()

    const newErrors = validate()

    if (Object.keys(newErrors).length > 0) {
      setErrors(newErrors)
      return
    }

    setLoading(true)
    setApiError('')

    try {
      const response = await authService.login(
          formData.username,
          formData.password
      )

      authService.saveUserData(
          response.token,
          response.username,
          response.role
      )

      if (response.role === 'PATIENT') {
        navigate('/patient/dashboard')
      } else if (response.role === 'LAB_TECHNICIAN') {
        navigate('/technician/dashboard')
      } else if (response.role === 'ADMIN') {
        navigate('/admin/dashboard')
      }

    } catch (error) {
      console.error('Login error:', error)

      if (error.response?.status === 401) {
        setApiError('Invalid username or password')
      } else if (error.response?.data?.message) {
        setApiError(error.response.data.message)
      } else {
        setApiError('Unable to connect to server. Please try again.')
      }

    } finally {
      setLoading(false)
    }
  }

  return (
      <div className="min-vh-100 d-flex align-items-center justify-content-center bg-light">
        <div className="container">
          <div className="row justify-content-center">
            <div className="col-md-5 col-lg-4">

              <div className="card shadow-sm border-0 rounded-3">
                <div className="card-body p-4 p-md-5">

                  {/* Header */}
                  <div className="text-center mb-4">
                    <div className="mb-3">
                      <i
                          className="bi bi-heart-pulse-fill text-primary"
                          style={{ fontSize: '3rem' }}
                      ></i>
                    </div>

                    <h3 className="fw-bold mb-2">
                      Lab Test Booking
                    </h3>

                    <p className="text-muted small">
                      Sign in to your account
                    </p>
                  </div>

                  {/* Form */}
                  <form onSubmit={handleSubmit}>

                    {/* API Error Alert */}
                    {apiError && (
                        <div
                            className="alert alert-danger alert-dismissible fade show"
                            role="alert"
                        >
                          <i className="bi bi-exclamation-triangle-fill me-2"></i>

                          {apiError}

                          <button
                              type="button"
                              className="btn-close"
                              onClick={() => setApiError('')}
                          ></button>
                        </div>
                    )}

                    {/* Username */}
                    <div className="mb-3">
                      <label
                          htmlFor="username"
                          className="form-label small fw-semibold"
                      >
                        Username
                      </label>

                      <input
                          type="text"
                          className={`form-control ${
                              errors.username ? 'is-invalid' : ''
                          }`}
                          id="username"
                          name="username"
                          value={formData.username}
                          onChange={handleChange}
                          placeholder="Enter your username"
                          disabled={loading}
                      />

                      {errors.username && (
                          <div className="invalid-feedback">
                            {errors.username}
                          </div>
                      )}
                    </div>

                    {/* Password */}
                    <div className="mb-4">
                      <label
                          htmlFor="password"
                          className="form-label small fw-semibold"
                      >
                        Password
                      </label>

                      <input
                          type="password"
                          className={`form-control ${
                              errors.password ? 'is-invalid' : ''
                          }`}
                          id="password"
                          name="password"
                          value={formData.password}
                          onChange={handleChange}
                          placeholder="Enter your password"
                          disabled={loading}
                      />

                      {errors.password && (
                          <div className="invalid-feedback">
                            {errors.password}
                          </div>
                      )}
                    </div>

                    {/* Submit Button */}
                    <button
                        type="submit"
                        className="btn btn-primary w-100 py-2 fw-semibold"
                        disabled={loading}
                    >
                      {loading ? (
                          <>
                            <span className="spinner-border spinner-border-sm me-2"></span>
                            Signing in...
                          </>
                      ) : (
                          'Sign In'
                      )}
                    </button>

                  </form>

                  {/* Register Link */}
                  <div className="text-center mt-4">
                    <p className="text-muted small mb-0">
                      Don't have an account?{' '}

                      <a
                          href="/register"
                          className="text-decoration-none fw-semibold"
                      >
                        Register here
                      </a>
                    </p>
                  </div>

                </div>
              </div>

              {/* Footer */}
              <div className="text-center mt-3">
                <p className="text-muted small">
                  © 2026 Lab Test Booking System
                </p>
              </div>

            </div>
          </div>
        </div>
      </div>
  )
}

export default Login