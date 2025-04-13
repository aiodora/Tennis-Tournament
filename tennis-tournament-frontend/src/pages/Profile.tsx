import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import axios from 'axios';

interface ProfileFormData {
  username: string;
  email: string;
  password?: string;
  role?: string;
  firstName: string;
  lastName: string;
  contactInfo: string;
}

const Profile: React.FC = () => {
  const { user, login, logout } = useAuth();
  
  const [formData, setFormData] = useState<ProfileFormData>({
    username: '',
    email: '',
    password: '',
    role: '',
    firstName: '',
    lastName: '',
    contactInfo: '',
  });
  
  const [error, setError] = useState('');
  const [successMsg, setSuccessMsg] = useState('');

  useEffect(() => {
    if (user) {
      setFormData({
        username: user.username || '',
        email: user.email || '',
        password: '',
        role: user.role || 'PLAYER',
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        contactInfo: user.phoneNumber || '',
      });
    }
  }, [user]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    setFormData(prev => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;
    setError('');
    setSuccessMsg('');

    try {
      const payload = { ...formData };

      if (user.role !== 'ADMIN') {
        delete payload.role;
      }

      if (!payload.password) {
        delete payload.password;
      }

      await api.put(`/users/${user.id}`, payload);

      const updatedMeRes = await api.get('/users/me');
      login(localStorage.getItem('token') || '', updatedMeRes.data);

      setSuccessMsg('Profile updated successfully!');
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response && err.response.data) {
        setError(String(err.response.data));
      } else if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Error updating profile');
      }
    }
  };

  if (!user) {
    return <div>Please log in first.</div>;
  }

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Welcome back, {user.username}</h2>
        <button className="btn btn-outline-danger" onClick={logout}>
          Logout
        </button>
      </div>

      <h3>My Profile</h3>
      {error && <div className="alert alert-danger">{error}</div>}
      {successMsg && <div className="alert alert-success">{successMsg}</div>}

      <form onSubmit={handleSubmit} style={{ maxWidth: '500px' }}>
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
          <label className="form-label">New Password (leave blank to keep existing)</label>
          <input
            className="form-control"
            name="password"
            type="password"
            value={formData.password}
            onChange={handleChange}
          />
        </div>

        {user.role === 'ADMIN' && (
          <div className="mb-3 text-start">
            <label className="form-label">Role</label>
            <select
              className="form-select"
              name="role"
              value={formData.role}
              onChange={handleChange}
            >
              <option value="PLAYER">PLAYER</option>
              <option value="REFEREE">REFEREE</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>
        )}

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
            name="contactInfo"
            value={formData.contactInfo}
            onChange={handleChange}
            required
          />
        </div>

        <button type="submit" className="btn btn-success">
          Update Profile
        </button>
      </form>
    </div>
  );
};

export default Profile;
