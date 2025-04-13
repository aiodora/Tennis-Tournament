import React, { useEffect, useState, useCallback } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import axios, { AxiosError } from 'axios';

export interface MatchDTO {
  matchId: number;
  tournamentName: string;
  player1Name: string;
  player2Name: string;
  refereeName: string;
  winnerName: string;
  matchDate: string;  
  venue: string;
  overallScore: string;
}

interface ProfileFormData {
  username: string;
  email: string;
  password?: string;
  role?: string;
  firstName: string;
  lastName: string;
  contactInfo: string;
}

type ActiveTab = 'matches' | 'profile';

const RefereeDashboard: React.FC = () => {
  const { user, logout } = useAuth();
  const [activeTab, setActiveTab] = useState<ActiveTab>('matches');

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Welcome back, {user?.username}</h2>
        <button className="btn btn-outline-danger" onClick={logout}>
          Logout
        </button>
      </div>

      <h3>Referee Dashboard</h3>

      <ul className="nav nav-tabs mb-3">
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'matches' ? 'active' : ''}`}
            onClick={() => setActiveTab('matches')}
          >
            Manage My Matches
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'profile' ? 'active' : ''}`}
            onClick={() => setActiveTab('profile')}
          >
            My Profile
          </button>
        </li>
      </ul>

      {activeTab === 'matches' && <MatchesSection />}
      {activeTab === 'profile' && <ProfileSection />}
    </div>
  );
};

export default RefereeDashboard;

//matches section
const MatchesSection: React.FC = () => {
  const { user } = useAuth();
  const [matches, setMatches] = useState<MatchDTO[]>([]);
  const [inAction, setInAction] = useState<MatchDTO[]>([]);
  const [upcoming, setUpcoming] = useState<MatchDTO[]>([]);
  const [past, setPast] = useState<MatchDTO[]>([]);

  const [scoreUpdates, setScoreUpdates] = useState<{ [matchId: number]: string }>({});
  const [error, setError] = useState('');

  const fetchMatches = useCallback(async () => {
    if (user) {
      try {
        const res = await api.get<MatchDTO[]>(`/matches/referee/${user.id}`);
        setMatches(res.data);
      } catch (err) {
        if (err instanceof AxiosError) {
          setError(err.response?.data || err.message || 'Error fetching matches.');
        } else if (err instanceof Error) {
          setError(err.message);
        } else {
          setError('Error fetching matches.');
        }
      }
    }
  }, [user]);

  useEffect(() => {
    fetchMatches();
  }, [fetchMatches]);

  useEffect(() => {
    const now = Date.now(); 

    const inActionArr: MatchDTO[] = [];
    const upcomingArr: MatchDTO[] = [];
    const pastArr: MatchDTO[] = [];

    matches.forEach((m) => {
      const matchTime = new Date(m.matchDate).getTime();
      const diffHours = (now - matchTime) / (1000 * 3600);
      if (diffHours < 0) {
        upcomingArr.push(m);
      } else if (diffHours >= 0 && diffHours < 2) {
        inActionArr.push(m);
      } else {
        pastArr.push(m);
      }
    });

    setInAction(inActionArr);
    setUpcoming(upcomingArr);
    setPast(pastArr);
  }, [matches]);

  const handleScoreChange = (matchId: number, value: string) => {
    setScoreUpdates((prev) => ({
      ...prev,
      [matchId]: value,
    }));
  };

  const updateScore = async (matchId: number) => {
    setError('');
    const newScore = scoreUpdates[matchId];
    if (!newScore) {
      return alert("Please enter an overall score in the format 'X-Y'");
    }
    try {
      await api.put(`/matches/${matchId}/update-score`, null, {
        params: { overallScore: newScore },
      });
      fetchMatches();
      setScoreUpdates((prev) => ({ ...prev, [matchId]: '' }));
    } catch (err: unknown) {
      if (axios.isAxiosError(err) && err.response && err.response.data) {
        setError(String(err.response.data));
      } else if (err instanceof Error) {
        setError(err.message);
      } else {
        setError('Error updating score.');
      }
    }
  };

  return (
    <div className="mt-3">
      <h4>Manage My Matches</h4>
      {error && <div className="alert alert-danger">{error}</div>}

      {/* 1) IN ACTION MATCHES (score editable) */}
      <section className="mb-4">
        <h5>Matches In Action (can update score)</h5>
        {inAction.length === 0 ? (
          <p>No matches currently in action.</p>
        ) : (
          inAction.map((m) => (
            <div key={m.matchId} className="card p-3 mb-3">
              <h6>Match ID: {m.matchId}</h6>
              <div>
                <b>Tournament:</b> {m.tournamentName}
              </div>
              <div>
                <b>Players:</b> {m.player1Name} vs {m.player2Name}
              </div>
              <div>
                <b>Date:</b> {m.matchDate}
              </div>
              <div>
                <b>Venue:</b> {m.venue}
              </div>
              <div className="mt-2">
                <b>Current Overall Score:</b> {m.overallScore}
              </div>
              <div className="mt-2">
                <input
                  type="text"
                  className="form-control"
                  placeholder="e.g. 6-4"
                  value={scoreUpdates[m.matchId] || ''}
                  onChange={(e) => handleScoreChange(m.matchId, e.target.value)}
                />
                <button
                  className="btn btn-primary mt-2"
                  onClick={() => updateScore(m.matchId)}
                >
                  Update Score
                </button>
              </div>
            </div>
          ))
        )}
      </section>

      {/* 2) UPCOMING MATCHES (no score update) */}
      <section className="mb-4">
        <h5>Upcoming Matches (cannot update yet)</h5>
        {upcoming.length === 0 ? (
          <p>No upcoming matches.</p>
        ) : (
          upcoming.map((m) => (
            <div key={m.matchId} className="card p-3 mb-3">
              <h6>Match ID: {m.matchId}</h6>
              <div>
                <b>Tournament:</b> {m.tournamentName}
              </div>
              <div>
                <b>Players:</b> {m.player1Name} vs {m.player2Name}
              </div>
              <div>
                <b>Date:</b> {m.matchDate}
              </div>
              <div>
                <b>Venue:</b> {m.venue}
              </div>
              <div className="mt-2">
                <b>Score:</b> {m.overallScore}
              </div>
              <p className="text-muted mt-2">Score updates not available yet.</p>
            </div>
          ))
        )}
      </section>

      {/* 3) PAST MATCHES (no score update) */}
      <section className="mb-4">
        <h5>Past Matches (final results)</h5>
        {past.length === 0 ? (
          <p>No past matches.</p>
        ) : (
          past.map((m) => (
            <div key={m.matchId} className="card p-3 mb-3">
              <h6>Match ID: {m.matchId}</h6>
              <div>
                <b>Tournament:</b> {m.tournamentName}
              </div>
              <div>
                <b>Players:</b> {m.player1Name} vs {m.player2Name}
              </div>
              <div>
                <b>Date:</b> {m.matchDate}
              </div>
              <div>
                <b>Venue:</b> {m.venue}
              </div>
              <div className="mt-2">
                <b>Final Score:</b> {m.overallScore}
              </div>
            </div>
          ))
        )}
      </section>
    </div>
  );
};

//profile section
const ProfileSection: React.FC = () => {
  const { user, login } = useAuth();
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
        role: user.role || '',
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        contactInfo: user.phoneNumber || '',
      });
    }
  }, [user]);

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>
  ) => {
    setFormData((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    setSuccessMsg('');

    try {
      const payload: {
        username: string;
        email: string;
        firstName: string;
        lastName: string;
        phoneNumber: string;
        password?: string;
        role?: string;
      } = {
        username: formData.username,
        email: formData.email,
        firstName: formData.firstName,
        lastName: formData.lastName,
        phoneNumber: formData.contactInfo,
      };

      if (formData.password && formData.password.trim() !== '') {
        payload.password = formData.password.trim();
      }

      payload.role = user?.role; 

      await api.put(`/users/${user?.id}`, payload);

      const updatedRes = await api.get('/users/me');
      login(localStorage.getItem('token') || '', updatedRes.data);

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

  return (
    <div className="mt-3">
      <h4>My Profile</h4>
      {error && <div className="alert alert-danger">{error}</div>}
      {successMsg && <div className="alert alert-success">{successMsg}</div>}

      <form onSubmit={handleSubmit} style={{ maxWidth: '500px' }}>
        <div className="mb-3">
          <label className="form-label">Username</label>
          <input
            className="form-control"
            name="username"
            value={formData.username}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
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

        <div className="mb-3">
          <label className="form-label">
            New Password (leave blank to keep existing)
          </label>
          <input
            className="form-control"
            name="password"
            type="password"
            value={formData.password}
            onChange={handleChange}
          />
        </div>

        <div className="mb-3">
          <label className="form-label">First Name</label>
          <input
            className="form-control"
            name="firstName"
            value={formData.firstName}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Last Name</label>
          <input
            className="form-control"
            name="lastName"
            value={formData.lastName}
            onChange={handleChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Contact Info</label>
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
