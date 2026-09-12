import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import authService from '../services/authService'

function Register() {
    const navigate = useNavigate()

    const [formData, setFormData] = useState({
        username: '',
        password: '',
        confirmPassword: '',
        fullName: '',
        email: ''
    })

    const [errors, setErrors] = useState({})
    const [loading, setLoading] = useState(false)
    const [apiError, setApiError] = useState('')
    const [success, setSuccess] = useState('')

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

        setApiError('')
    }

    const validate = () => {
        const newErrors = {}

        if (!formData.fullName.trim()) {
            newErrors.fullName = 'Full name is required'
        }

        if (!formData.username.trim()) {
            newErrors.username = 'Username is required'
        } else if (formData.username.length < 3) {
            newErrors.username = 'Username must be at least 3 characters'
        }

        if (!formData.email.trim()) {
            newErrors.email = 'Email is required'
        } else if (!/^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(formData.email)) {
            newErrors.email = 'Please enter a valid email address'
        }

        if (!formData.password) {
            newErrors.password = 'Password is required'
        } else if (formData.password.length < 6) {
            newErrors.password = 'Password must be at least 6 characters'
        }

        if (!formData.confirmPassword) {
            newErrors.confirmPassword = 'Please confirm your password'
        } else if (formData.password !== formData.confirmPassword) {
            newErrors.confirmPassword = 'Passwords do not match'
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
        setSuccess('')

        try {
            const response = await authService.register({
                username: formData.username,
                password: formData.password,
                fullName: formData.fullName,
                email: formData.email
            })

            authService.saveUserData(
                response.token,
                response.username,
                response.role
            )

            setSuccess('Registration successful! Redirecting...')

            setTimeout(() => {
                navigate('/patient/dashboard')
            }, 1000)

        } catch (error) {
            console.error('Registration error:', error)

            if (error.response?.status === 409) {
                setApiError('Username already exists. Please choose another username.')
            } else if (error.response?.data?.message) {
                setApiError(error.response.data.message)
            } else {
                setApiError('Unable to register. Please try again.')
            }
        } finally {
            setLoading(false)
        }
    }

    return (
        <div className="min-vh-100 d-flex align-items-center justify-content-center bg-light">
            <div className="container">
                <div className="row justify-content-center">
                    <div className="col-md-6 col-lg-5">

                        <div className="card shadow-sm border-0 rounded-3">
                            <div className="card-body p-4 p-md-5">

                                {/* Header */}
                                <div className="text-center mb-4">
                                    <div className="mb-3">
                                        <i
                                            className="bi bi-person-plus-fill text-primary"
                                            style={{ fontSize: '3rem' }}
                                        ></i>
                                    </div>

                                    <h3 className="fw-bold mb-2">
                                        Create Account
                                    </h3>

                                    <p className="text-muted small">
                                        Register as a patient
                                    </p>
                                </div>

                                {/* API Error */}
                                {apiError && (
                                    <div className="alert alert-danger" role="alert">
                                        <i className="bi bi-exclamation-triangle-fill me-2"></i>
                                        {apiError}
                                    </div>
                                )}

                                {/* Success */}
                                {success && (
                                    <div className="alert alert-success" role="alert">
                                        <i className="bi bi-check-circle-fill me-2"></i>
                                        {success}
                                    </div>
                                )}

                                <form onSubmit={handleSubmit}>

                                    {/* Full Name */}
                                    <div className="mb-3">
                                        <label
                                            htmlFor="fullName"
                                            className="form-label small fw-semibold"
                                        >
                                            Full Name
                                        </label>

                                        <input
                                            type="text"
                                            className={`form-control ${
                                                errors.fullName ? 'is-invalid' : ''
                                            }`}
                                            id="fullName"
                                            name="fullName"
                                            value={formData.fullName}
                                            onChange={handleChange}
                                            placeholder="Enter your full name"
                                            disabled={loading}
                                        />

                                        {errors.fullName && (
                                            <div className="invalid-feedback">
                                                {errors.fullName}
                                            </div>
                                        )}
                                    </div>

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
                                            placeholder="Choose a username"
                                            disabled={loading}
                                        />

                                        {errors.username && (
                                            <div className="invalid-feedback">
                                                {errors.username}
                                            </div>
                                        )}
                                    </div>

                                    {/* Email */}
                                    <div className="mb-3">
                                        <label
                                            htmlFor="email"
                                            className="form-label small fw-semibold"
                                        >
                                            Email
                                        </label>

                                        <input
                                            type="email"
                                            className={`form-control ${
                                                errors.email ? 'is-invalid' : ''
                                            }`}
                                            id="email"
                                            name="email"
                                            value={formData.email}
                                            onChange={handleChange}
                                            placeholder="Enter your email"
                                            disabled={loading}
                                        />

                                        {errors.email && (
                                            <div className="invalid-feedback">
                                                {errors.email}
                                            </div>
                                        )}
                                    </div>

                                    {/* Password */}
                                    <div className="mb-3">
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
                                            placeholder="Create a password"
                                            disabled={loading}
                                        />

                                        {errors.password && (
                                            <div className="invalid-feedback">
                                                {errors.password}
                                            </div>
                                        )}
                                    </div>

                                    {/* Confirm Password */}
                                    <div className="mb-4">
                                        <label
                                            htmlFor="confirmPassword"
                                            className="form-label small fw-semibold"
                                        >
                                            Confirm Password
                                        </label>

                                        <input
                                            type="password"
                                            className={`form-control ${
                                                errors.confirmPassword ? 'is-invalid' : ''
                                            }`}
                                            id="confirmPassword"
                                            name="confirmPassword"
                                            value={formData.confirmPassword}
                                            onChange={handleChange}
                                            placeholder="Confirm your password"
                                            disabled={loading}
                                        />

                                        {errors.confirmPassword && (
                                            <div className="invalid-feedback">
                                                {errors.confirmPassword}
                                            </div>
                                        )}
                                    </div>

                                    {/* Register Button */}
                                    <button
                                        type="submit"
                                        className="btn btn-primary w-100 py-2 fw-semibold"
                                        disabled={loading}
                                    >
                                        {loading ? (
                                            <>
                                                <span className="spinner-border spinner-border-sm me-2"></span>
                                                Creating Account...
                                            </>
                                        ) : (
                                            <>
                                                <i className="bi bi-person-plus me-2"></i>
                                                Create Account
                                            </>
                                        )}
                                    </button>

                                </form>

                                {/* Login Link */}
                                <div className="text-center mt-4">
                                    <p className="text-muted small mb-0">
                                        Already have an account?{' '}
                                        <a
                                            href="/login"
                                            className="text-decoration-none fw-semibold"
                                        >
                                            Sign in
                                        </a>
                                    </p>
                                </div>

                                {/* Role Information */}
                                <div className="mt-4 p-3 bg-light rounded-3">
                                    <p className="small text-muted mb-0">
                                        <i className="bi bi-info-circle me-2"></i>
                                        New accounts are registered as <strong>Patient</strong>.
                                        Admin and Technician accounts are managed separately.
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

export default Register