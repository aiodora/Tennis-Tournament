import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Home: React.FC = () => {
  const { user } = useAuth();

  return (
    <div className="container mt-5 text-center" style={{ maxWidth: '600px' }}>
      <h1 className="mb-4">Welcome to the Tennis App</h1>
      <p>
        This application allows players to register for tournaments, view match
        schedules, and check the latest scores. Referees can manage scores for
        the matches they supervise, and administrators can manage users and
        generate match data exports.
      </p>

      {!user && (
        <div className="mt-5">
          <Link to="/login" className="btn btn-primary btn-lg me-3">
            Login
          </Link>
          <Link to="/register" className="btn btn-success btn-lg">
            Register
          </Link>
        </div>
      )}

      {user && (
        <div className="mt-4">
          <h5>You are logged in as: {user.username} ({user.role})</h5>
          <p>Navigate using the menu or your assigned role dash:</p>
          {user.role === 'ADMIN' && (
            <Link to="/admin" className="btn btn-primary me-3">
              Admin Dashboard
            </Link>
          )}
          {user.role === 'PLAYER' && (
            <Link to="/player" className="btn btn-primary me-3">
              Player Dashboard
            </Link>
          )}
          {user.role === 'REFEREE' && (
            <Link to="/referee" className="btn btn-primary me-3">
              Referee Dashboard
            </Link>
          )}
        </div>
      )}
    </div>
  );
};

export default Home;
