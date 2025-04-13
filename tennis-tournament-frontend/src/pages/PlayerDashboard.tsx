import React, { useEffect, useState } from 'react';
import api from '../services/api';
import { useAuth } from '../context/AuthContext';
import axios from 'axios';

interface Tournament {
  id: number;
  name: string;
  location: string;
  startDate: string; 
  endDate: string; 
  status: string;
  description?: string;
  registrationDeadline?: string; 
}

interface Registration {
  id: number;
  playerId: number;
  playerUsername: string;
  tournamentId: number;
  tournamentName: string;
  registrationDate: string; 
  status: string;   
}

interface MatchDTO {
  matchId: number;
  tournamentId: number;
  tournamentName: string;
  player1Id: number;
  player2Id: number;
  player1Name: string;
  player2Name: string;
  refereeId: number;
  refereeName: string;
  winnerId: number | null;
  winnerName: string;
  matchDate: string; 
  venue: string;
  overallScore: string;
}

interface ProfileFormData {
  username: string;
  email: string;
  password?: string;
  firstName: string;
  lastName: string;
  phoneNumber: string;
}

type Tab = 'matches' | 'tournaments' | 'profile';

const PlayerDashboard: React.FC = () => {
  const { user, logout, login } = useAuth();

  const [activeTab, setActiveTab] = useState<Tab>('matches');

  const [error, setError] = useState('');

  const [tournaments, setTournaments] = useState<Tournament[]>([]);
  const [myRegistrations, setMyRegistrations] = useState<Registration[]>([]);

  const [myMatches, setMyMatches] = useState<MatchDTO[]>([]);

  const [profileForm, setProfileForm] = useState<ProfileFormData>({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
    phoneNumber: '',
  });
  const [successMsg, setSuccessMsg] = useState('');

  useEffect(() => {
    fetchAllTournaments();
    if (user) {
      fetchMyRegistrations(user.id);
      fetchMyMatches(user.id);

      setProfileForm({
        username: user.username || '',
        email: user.email || '',
        password: '',
        firstName: user.firstName || '',
        lastName: user.lastName || '',
        phoneNumber: user.phoneNumber || '',
      });
    }
  }, [user]);

  const fetchAllTournaments = async () => {
    try {
      const res = await api.get<Tournament[]>('/tournaments/all');
      setTournaments(res.data);
    } catch (err) {
      handleError(err, 'Error fetching tournaments.');
    }
  };

  const fetchMyRegistrations = async (playerId: number) => {
    try {
      const res = await api.get<Registration[]>(`/registrations/player/${playerId}`);
      setMyRegistrations(res.data);
    } catch (err) {
      handleError(err, 'Error fetching registrations.');
    }
  };

  const fetchMyMatches = async (playerId: number) => {
    try {
      const res = await api.get<MatchDTO[]>(`/matches/player/${playerId}`);
      setMyMatches(res.data);
    } catch (err) {
      handleError(err, 'Error fetching your matches.');
    }
  };

  const registerForTournament = async (tId: number) => {
    if (!user) return;
    try {
      await api.post(`/registrations/player/${user.id}/tournament/${tId}`);
      alert('Registered successfully!');
      fetchMyRegistrations(user.id);
    } catch (err) {
      handleError(err, 'Error registering for tournament.');
    }
  };

  //profile form handling
  const handleProfileChange = (
    e: React.ChangeEvent<HTMLInputElement>
  ) => {
    setProfileForm((prev) => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const updateProfile = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!user) return;

    setError('');
    setSuccessMsg('');

    try {
      const payload: Partial<ProfileFormData> = {
        username: profileForm.username,
        email: profileForm.email,
        firstName: profileForm.firstName,
        lastName: profileForm.lastName,
        phoneNumber: profileForm.phoneNumber,
      };
      if (profileForm.password && profileForm.password.trim() !== '') {
        payload.password = profileForm.password.trim();
      }

      await api.put(`/users/${user.id}`, payload);

      const updatedRes = await api.get('/users/me');
      login(localStorage.getItem('token') || '', updatedRes.data);

      setSuccessMsg('Profile updated successfully!');
      setProfileForm((prev) => ({ ...prev, password: '' }));
    } catch (err) {
      handleError(err, 'Error updating profile.');
    }
  };

  const handleError = (err: unknown, defaultMsg: string) => {
    let errorMsg = defaultMsg;
    if (axios.isAxiosError(err) && err.response) {
      errorMsg = err.response.data || err.message;
    } else if (err instanceof Error) {
      errorMsg = err.message;
    }
    setError(errorMsg);
  };

  const now = new Date();

  const upcomingMatches = myMatches.filter(m => new Date(m.matchDate) >= now);
  const pastMatches = myMatches.filter(m => new Date(m.matchDate) < now);

  const registeredTournamentIds = new Set(myRegistrations.map(r => r.tournamentId));

  const availableTournaments = tournaments.filter((t) => {
    if (registeredTournamentIds.has(t.id)) return false;
    if (!t.registrationDeadline) return true;
    const deadline = new Date(t.registrationDeadline);
    return new Date() <= deadline;
  });

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Welcome back, {user?.username}</h2>
        <button className="btn btn-outline-danger" onClick={logout}>
          Logout
        </button>
      </div>

      <h3>Player Dashboard</h3>
      {error && <div className="alert alert-danger">{error}</div>}

      <ul className="nav nav-tabs mb-3">
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'matches' ? 'active' : ''}`}
            onClick={() => setActiveTab('matches')}
          >
            My Matches
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'tournaments' ? 'active' : ''}`}
            onClick={() => setActiveTab('tournaments')}
          >
            Tournaments
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

      {activeTab === 'matches' && (
        <MatchesSection upcomingMatches={upcomingMatches} pastMatches={pastMatches} />
      )}

      {activeTab === 'tournaments' && (
        <TournamentsSection
          myRegistrations={myRegistrations}
          tournaments={tournaments}
          availableTournaments={availableTournaments}
          registerForTournament={registerForTournament}
        />
      )}

      {activeTab === 'profile' && (
        <ProfileSection
          profileForm={profileForm}
          successMsg={successMsg}
          handleProfileChange={handleProfileChange}
          updateProfile={updateProfile}
        />
      )}
    </div>
  );
};

export default PlayerDashboard;

//matches section
interface MatchesSectionProps {
  upcomingMatches: MatchDTO[];
  pastMatches: MatchDTO[];
}

const MatchesSection: React.FC<MatchesSectionProps> = ({ upcomingMatches, pastMatches }) => {
  return (
    <div>
      <div className="my-4">
        <h4>Upcoming Matches</h4>
        {upcomingMatches.length === 0 ? (
          <p>No upcoming matches.</p>
        ) : (
          upcomingMatches.map((m) => (
            <div key={m.matchId} className="card p-3 mb-2">
              <b>Tournament:</b> {m.tournamentName} <br />
              <b>Date:</b> {m.matchDate} <br />
              <b>Venue:</b> {m.venue} <br />
              <b>Referee:</b> {m.refereeName} <br />
              <b>Players:</b> {m.player1Name} vs {m.player2Name}
            </div>
          ))
        )}
      </div>

      <div className="my-4">
        <h4>Past Matches</h4>
        {pastMatches.length === 0 ? (
          <p>No past matches.</p>
        ) : (
          pastMatches.map((m) => (
            <div key={m.matchId} className="card p-3 mb-2">
              <b>Tournament:</b> {m.tournamentName} <br />
              <b>Date:</b> {m.matchDate} <br />
              <b>Venue:</b> {m.venue} <br />
              <b>Referee:</b> {m.refereeName} <br />
              <b>Players:</b> {m.player1Name} vs {m.player2Name} <br />
              <b>Winner:</b> {m.winnerName} <br />
              <b>Final Score:</b> {m.overallScore}
            </div>
          ))
        )}
      </div>
    </div>
  );
};

//tournaments section
interface TournamentsSectionProps {
  myRegistrations: Registration[];
  tournaments: Tournament[];
  availableTournaments: Tournament[];
  registerForTournament: (tId: number) => Promise<void>;
}

const TournamentsSection: React.FC<TournamentsSectionProps> = ({
  myRegistrations,
  tournaments,
  availableTournaments,
  registerForTournament,
}) => {
  const registeredTournamentIds = new Set(myRegistrations.map(r => r.tournamentId));
  const registeredTournaments = tournaments.filter(t => registeredTournamentIds.has(t.id));

  return (
    <div>
      <div className="my-4">
        <h4>My Registered Tournaments</h4>
        {registeredTournaments.length === 0 && (
          <p>You are not registered in any tournament yet.</p>
        )}
        <div className="row">
          {registeredTournaments.map((t) => (
            <div key={t.id} className="col-md-4 mb-3">
              <div className="card p-3">
                <b>{t.name}</b>
                <div>Location: {t.location}</div>
                <div>Start: {t.startDate}</div>
                <div>End: {t.endDate}</div>
                <div>Status: {t.status}</div>
                {t.registrationDeadline && (
                  <div>Reg. Deadline: {t.registrationDeadline}</div>
                )}
              </div>
            </div>
          ))}
        </div>
      </div>

      <div className="my-4">
        <h4>Available Tournaments</h4>
        {availableTournaments.length === 0 && (
          <p>No tournaments currently available.</p>
        )}
        <div className="row">
          {availableTournaments.map((t) => (
            <div key={t.id} className="col-md-4 mb-3">
              <div className="card p-3">
                <b>{t.name}</b>
                <div>Location: {t.location}</div>
                <div>Start: {t.startDate}</div>
                <div>End: {t.endDate}</div>
                <div>Status: {t.status}</div>
                {t.registrationDeadline && (
                  <div>Reg. Deadline: {t.registrationDeadline}</div>
                )}
                <button
                  className="btn btn-primary mt-2"
                  onClick={() => registerForTournament(t.id)}
                >
                  Register
                </button>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

//profile section
interface ProfileSectionProps {
  profileForm: ProfileFormData;
  successMsg: string;
  handleProfileChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  updateProfile: (e: React.FormEvent) => void;
}

const ProfileSection: React.FC<ProfileSectionProps> = ({
  profileForm,
  successMsg,
  handleProfileChange,
  updateProfile,
}) => {
  return (
    <div>
      <h4>Update My Profile</h4>
      {successMsg && <div className="alert alert-success">{successMsg}</div>}
      <form onSubmit={updateProfile} style={{ maxWidth: '500px' }}>
        <div className="mb-3">
          <label className="form-label">Username</label>
          <input
            className="form-control"
            name="username"
            value={profileForm.username}
            onChange={handleProfileChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Email</label>
          <input
            className="form-control"
            type="email"
            name="email"
            value={profileForm.email}
            onChange={handleProfileChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">
            New Password (leave blank to keep existing)
          </label>
          <input
            className="form-control"
            type="password"
            name="password"
            value={profileForm.password}
            onChange={handleProfileChange}
          />
        </div>

        <div className="mb-3">
          <label className="form-label">First Name</label>
          <input
            className="form-control"
            name="firstName"
            value={profileForm.firstName}
            onChange={handleProfileChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Last Name</label>
          <input
            className="form-control"
            name="lastName"
            value={profileForm.lastName}
            onChange={handleProfileChange}
            required
          />
        </div>

        <div className="mb-3">
          <label className="form-label">Phone Number</label>
          <input
            className="form-control"
            name="phoneNumber"
            value={profileForm.phoneNumber}
            onChange={handleProfileChange}
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
