import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom'
import Login from './components/Login'
import Register from './components/Register'
import PatientDashboard from './components/PatientDashboard'
import TechnicianDashboard from './components/TechnicianDashboard'
import AdminDashboard from './components/AdminDashboard'

function App() {
    return (
        <Router>
            <Routes>
                <Route path="/" element={<Navigate to="/login" />} />
                <Route path="/login" element={<Login />} />
                <Route path="/register" element={<Register />} />
                <Route path="/patient/dashboard" element={<PatientDashboard />} />
                <Route path="/technician/dashboard" element={<TechnicianDashboard />} />
                <Route path="/admin/dashboard" element={<AdminDashboard />} />
            </Routes>
        </Router>
    )
}

export default App