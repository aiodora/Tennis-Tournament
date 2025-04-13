import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../services/api';
import axios from 'axios';

const Register: React.FC = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    phoneNumber: '',
  });
  const [errorMsg, setErrorMsg] = useState('');

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData(prev => ({ ...prev, [e.target.name]: e.target.value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg('');

    try {
      const payload = { ...formData, role: 'PLAYER' };
      await api.post('/auth/register', payload);
      alert('Registered successfully! You can now login.');
      navigate('/login');
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response) {

        if (err.response.status === 409) {
          setErrorMsg("Username or email already exists.");
        } else if (err.response.status === 400) {
          setErrorMsg("Invalid registration data. Please check your inputs.");
        } else if (
          typeof err.response.data === 'string' &&
          err.response.data.length > 0
        ) {
          setErrorMsg(err.response.data);
        } else {
          setErrorMsg("Registration failed. Please try again.");
        }
      } else if (err instanceof Error) {
        setErrorMsg(err.message);
      } else {
        setErrorMsg("Registration failed. Please try again.");
      }
    }
  };

  return (
    <div className="container d-flex justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
      <div style={{ maxWidth: '400px', width: '100%' }}>
        <h2 className="text-center mb-4">Register</h2>
        {errorMsg && (
          <div className="alert alert-danger" role="alert">
            {errorMsg}
          </div>
        )}
        <form onSubmit={handleSubmit}>
          <div className="mb-3 text-start">
            <label className="form-label">Username</label>
            <input
              className="form-control"
              name="username"
              value={formData.username}
              onChange={handleChange}
              required
            />
          </div>
          <div className="mb-3 text-start">
            <label className="form-label">Email</label>
            <input
              className="form-control"
              name="email"
              type="email"
              value={formData.email}
              onChange={handleChange}
              required
            />
          </div>
          <div className="mb-3 text-start">
            <label className="form-label">Password</label>
            <input
              className="form-control"
              name="password"
              type="password"
              value={formData.password}
              onChange={handleChange}
              required
            />
            <small className="text-muted">
              Password must contain upper/lowercase letters, a digit, a special character, and be 5–20 chars long.
            </small>
          </div>
          <div className="mb-3 text-start">
            <label className="form-label">First Name</label>
            <input
              className="form-control"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              required
            />
          </div>
          <div className="mb-3 text-start">
            <label className="form-label">Last Name</label>
            <input
              className="form-control"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              required
            />
          </div>
          <div className="mb-3 text-start">
            <label className="form-label">Phone Number</label>
            <input
              className="form-control"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              required
            />
          </div>
          <button type="submit" className="btn btn-success w-100">Register</button>
        </form>
        <div className="mt-3 text-center">
          Already have an account? <Link to="/login">Login</Link>.
        </div>
      </div>
    </div>
  );
};

export default Register;
