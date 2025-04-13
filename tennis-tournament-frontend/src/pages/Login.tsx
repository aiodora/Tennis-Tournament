import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';
import { User } from '../types/User';
import axios from 'axios';

const Login: React.FC = () => {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [errorMsg, setErrorMsg] = useState('');
  const navigate = useNavigate();
  const { login } = useAuth();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErrorMsg('');

    try {
      const res = await api.post<string>('/auth/login', { username, password });
      const token = res.data;

      api.defaults.headers.common['Authorization'] = `Bearer ${token}`;

      const meRes = await api.get<User>('/users/me');
      const theUser = meRes.data;

      login(token, theUser);

      switch (theUser.role) {
        case 'ADMIN':
          navigate('/admin');
          break;
        case 'REFEREE':
          navigate('/referee');
          break;
        default:
          navigate('/player');
      }
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response) {
        if (err.response.status === 404) {
          setErrorMsg("Username not found. Please check your username.");
        } else if (err.response.status === 401) {
          setErrorMsg("Incorrect password. Please try again.");
        } else if (
          typeof err.response.data === 'string' &&
          err.response.data.length > 0
        ) {
          setErrorMsg(err.response.data);
        } else {
          setErrorMsg("Login failed. Please try again.");
        }
      } else if (err instanceof Error) {
        setErrorMsg(err.message);
      } else {
        setErrorMsg("Login failed. Please try again.");
      }
    }       
  };

  return (
    <div className="container d-flex justify-content-center align-items-center" style={{ minHeight: '80vh' }}>
      <div style={{ maxWidth: '400px', width: '100%' }}>
        <h2 className="text-center mb-4">Login</h2>
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
              value={username}
              onChange={e => setUsername(e.target.value)}
              required
            />
          </div>
          <div className="mb-3 text-start">
            <label className="form-label">Password</label>
            <input
              className="form-control"
              type="password"
              value={password}
              onChange={e => setPassword(e.target.value)}
              required
            />
          </div>
          <button type="submit" className="btn btn-primary w-100">Login</button>
        </form>
        <div className="mt-3 text-center">
          No account? <Link to="/register">Register here</Link>.
        </div>
      </div>
    </div>
  );
};

export default Login;
