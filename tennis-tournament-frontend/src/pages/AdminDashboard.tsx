import React, { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import api from '../services/api';
import axios from 'axios';
import { User } from '../types/User';

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

interface Match {
  matchId: number;
  tournamentId: number;
  tournamentName?: string;
  player1Name: string;
  player2Name: string;
  refereeName: string;
  matchDate: string;
  venue: string;
  overallScore?: string;
}

const AdminDashboard: React.FC = () => {
  const { user, logout } = useAuth();

  const [activeTab, setActiveTab] = useState<'users'|'tournaments'|'matches'|'export'>('users');

  return (
    <div className="container mt-4">
      <div className="d-flex justify-content-between align-items-center mb-3">
        <h2>Welcome back, {user?.username}</h2>
        <button className="btn btn-outline-danger" onClick={logout}>
          Logout
        </button>
      </div>

      <h3>Admin Dashboard</h3>

      <ul className="nav nav-tabs">
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'users' ? 'active' : ''}`}
            onClick={() => setActiveTab('users')}
          >
            Manage Users
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'tournaments' ? 'active' : ''}`}
            onClick={() => setActiveTab('tournaments')}
          >
            Manage Tournaments
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'matches' ? 'active' : ''}`}
            onClick={() => setActiveTab('matches')}
          >
            Manage Matches
          </button>
        </li>
        <li className="nav-item">
          <button
            className={`nav-link ${activeTab === 'export' ? 'active' : ''}`}
            onClick={() => setActiveTab('export')}
          >
            Export Matches
          </button>
        </li>
      </ul>

      {activeTab === 'users' && <AdminUsersSection />}
      {activeTab === 'tournaments' && <AdminTournamentsSection />}
      {activeTab === 'matches' && <AdminMatchesSection />}
      {activeTab === 'export' && <AdminExportSection />}
    </div>
  );
};

export default AdminDashboard;

//manage users section
const AdminUsersSection: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [error, setError] = useState('');

  const [editingUserId, setEditingUserId] = useState<number | null>(null);
  const [editForm, setEditForm] = useState<Partial<User & { password?: string }>>({});

  const [newUserForm, setNewUserForm] = useState<Partial<User & { password?: string }>>({
    username: '',
    email: '',
    password: '',
    role: 'PLAYER',
    firstName: '',
    lastName: '',
    phoneNumber: '',
  });

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    try {
      const res = await api.get<User[]>('/users');
      setUsers(res.data);
    } catch (err) {
      handleError(err, setError, 'Error fetching users.');
    }
  };

  const handleError = (
    err: unknown,
    setter: (msg: string) => void,
    defaultMsg: string
  ) => {
    let msg = defaultMsg;
    if (axios.isAxiosError(err) && err.response && err.response.data) {
      msg = String(err.response.data);
    } else if (err instanceof Error) {
      msg = err.message;
    }
    setter(msg);
  };

  const createUser = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload: Partial<User & { password?: string }> = {
        username: newUserForm.username,
        email: newUserForm.email,
        role: newUserForm.role,
        firstName: newUserForm.firstName,
        lastName: newUserForm.lastName,
        phoneNumber: newUserForm.phoneNumber,
      };
      if (newUserForm.password && newUserForm.password.trim() !== '') {
        payload.password = newUserForm.password.trim();
      }

      await api.post('/users', payload); 
      alert('User created successfully');
      setNewUserForm({
        username: '',
        email: '',
        password: '',
        role: 'PLAYER',
        firstName: '',
        lastName: '',
        phoneNumber: '',
      });
      fetchUsers();
    } catch (err) {
      alertError(err, 'Error creating user.');
    }
  };

  const alertError = (err: unknown, defaultMsg: string) => {
    let msg = defaultMsg;
    if (axios.isAxiosError(err) && err.response && err.response.data) {
      msg = String(err.response.data);
    } else if (err instanceof Error) {
      msg = err.message;
    }
    alert(msg);
  };

  const startEdit = (u: User) => {
    setEditingUserId(u.id);
    setEditForm({
      username: u.username,
      email: u.email,
      role: u.role,
      firstName: u.firstName,
      lastName: u.lastName,
      phoneNumber: u.phoneNumber,
      password: '',
    });
  };

  const cancelEdit = () => {
    setEditingUserId(null);
    setEditForm({});
  };

  const saveEdit = async (id: number) => {
    try {
      const payload: Partial<User & { password?: string }> = {
        username: editForm.username,
        email: editForm.email,
        role: editForm.role,
        firstName: editForm.firstName,
        lastName: editForm.lastName,
        phoneNumber: editForm.phoneNumber,
      };
      if (editForm.password && editForm.password.trim() !== '') {
        payload.password = editForm.password.trim();
      }

      await api.put(`/users/${id}`, payload);
      cancelEdit();
      fetchUsers();
    } catch (err) {
      alertError(err, 'Error saving user.');
    }
  };

  const deleteUser = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this user?')) return;
    try {
      await api.delete(`/users/${id}`);
      fetchUsers();
    } catch (err) {
      alertError(err, 'Error deleting user.');
    }
  };

  return (
    <div className="mt-3">
      <h4>All Users</h4>
      {error && <div className="text-danger">{error}</div>}

      <div className="card p-3 mb-4">
        <h5>Create New User</h5>
        <form onSubmit={createUser}>
          <div className="mb-2">
            <label>Username</label>
            <input
              className="form-control"
              value={newUserForm.username || ''}
              onChange={(e) =>
                setNewUserForm((prev) => ({ ...prev, username: e.target.value }))
              }
              required
            />
          </div>
          <div className="mb-2">
            <label>Email</label>
            <input
              className="form-control"
              type="email"
              value={newUserForm.email || ''}
              onChange={(e) =>
                setNewUserForm((prev) => ({ ...prev, email: e.target.value }))
              }
              required
            />
          </div>
          <div className="mb-2">
            <label>Password</label>
            <input
              className="form-control"
              type="password"
              value={newUserForm.password || ''}
              onChange={(e) =>
                setNewUserForm((prev) => ({ ...prev, password: e.target.value }))
              }
            />
            <small className="text-muted">
              Leave blank if you want to set/generate a password later
            </small>
          </div>
          <div className="mb-2">
            <label>Role</label>
            <select
              className="form-select"
              value={newUserForm.role || 'PLAYER'}
              onChange={(e) =>
                setNewUserForm((prev) => ({ ...prev, role: e.target.value }))
              }
            >
              <option value="PLAYER">PLAYER</option>
              <option value="REFEREE">REFEREE</option>
              <option value="ADMIN">ADMIN</option>
            </select>
          </div>
          <div className="mb-2">
            <label>First Name</label>
            <input
              className="form-control"
              value={newUserForm.firstName || ''}
              onChange={(e) =>
                setNewUserForm((prev) => ({ ...prev, firstName: e.target.value }))
              }
            />
          </div>
          <div className="mb-2">
            <label>Last Name</label>
            <input
              className="form-control"
              value={newUserForm.lastName || ''}
              onChange={(e) =>
                setNewUserForm((prev) => ({ ...prev, lastName: e.target.value }))
              }
            />
          </div>
          <div className="mb-2">
            <label>Phone Number</label>
            <input
              className="form-control"
              value={newUserForm.phoneNumber || ''}
              onChange={(e) =>
                setNewUserForm((prev) => ({ ...prev, phoneNumber: e.target.value }))
              }
            />
          </div>
          <button type="submit" className="btn btn-success">
            Create User
          </button>
        </form>
      </div>

      <div className="row mt-3">
        {users.map((u) => (
          <div key={u.id} className="col-md-4 mb-3">
            <div className="card p-3">
              {editingUserId === u.id ? (
                <>
                  <input
                    className="form-control mb-2"
                    value={editForm.username || ''}
                    onChange={(e) =>
                      setEditForm({ ...editForm, username: e.target.value })
                    }
                  />
                  <input
                    className="form-control mb-2"
                    type="email"
                    value={editForm.email || ''}
                    onChange={(e) => setEditForm({ ...editForm, email: e.target.value })}
                  />
                  <select
                    className="form-select mb-2"
                    value={editForm.role || 'PLAYER'}
                    onChange={(e) => setEditForm({ ...editForm, role: e.target.value })}
                  >
                    <option value="PLAYER">PLAYER</option>
                    <option value="REFEREE">REFEREE</option>
                    <option value="ADMIN">ADMIN</option>
                  </select>
                  <input
                    className="form-control mb-2"
                    value={editForm.firstName || ''}
                    onChange={(e) =>
                      setEditForm({ ...editForm, firstName: e.target.value })
                    }
                  />
                  <input
                    className="form-control mb-2"
                    value={editForm.lastName || ''}
                    onChange={(e) =>
                      setEditForm({ ...editForm, lastName: e.target.value })
                    }
                  />
                  <input
                    className="form-control mb-2"
                    value={editForm.phoneNumber || ''}
                    onChange={(e) =>
                      setEditForm({ ...editForm, phoneNumber: e.target.value })
                    }
                  />
                  <label className="form-label">New Password</label>
                  <input
                    className="form-control mb-2"
                    type="password"
                    value={editForm.password || ''}
                    onChange={(e) =>
                      setEditForm({ ...editForm, password: e.target.value })
                    }
                  />
                  <small className="text-muted">
                    Leave blank if you do not want to change the password
                  </small>

                  <div className="mt-2">
                    <button
                      className="btn btn-primary me-2"
                      onClick={() => saveEdit(u.id)}
                    >
                      Save
                    </button>
                    <button
                      className="btn btn-secondary"
                      onClick={cancelEdit}
                    >
                      Cancel
                    </button>
                  </div>
                </>
              ) : (
                <>
                  <div><b>ID:</b> {u.id}</div>
                  <div><b>Username:</b> {u.username}</div>
                  <div><b>Email:</b> {u.email}</div>
                  <div><b>Role:</b> {u.role}</div>
                  <div><b>Name:</b> {u.firstName} {u.lastName}</div>
                  <div><b>Phone:</b> {u.phoneNumber}</div>
                  <div className="mt-2">
                    <button
                      className="btn btn-warning me-2"
                      onClick={() => startEdit(u)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-danger"
                      onClick={() => deleteUser(u.id)}
                    >
                      Delete
                    </button>
                  </div>
                </>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

//manage tournaments section
// manage tournaments section
const AdminTournamentsSection: React.FC = () => {
  const [tournaments, setTournaments] = useState<Tournament[]>([]);
  const [error, setError] = useState('');

  // Form data for creating a new tournament
  const [newForm, setNewForm] = useState({
    name: '',
    location: '',
    startDate: '',
    endDate: '',
    registrationDeadline: '', 
    description: '',
  });

  const [editingId, setEditingId] = useState<number | null>(null);
  const [editForm, setEditForm] = useState({
    name: '',
    location: '',
    startDate: '',
    endDate: '',
    registrationDeadline: '', 
    description: '',
  });

  useEffect(() => {
    fetchAllTournaments();
  }, []);

  const fetchAllTournaments = async () => {
    try {
      const res = await api.get<Tournament[]>('/tournaments/all');
      setTournaments(res.data);
    } catch (err) {
      handleError(err, 'Error fetching tournaments.');
    }
  };

  // Reusable error helper
  const handleError = (err: unknown, defaultMsg: string) => {
    let msg = defaultMsg;
    if (axios.isAxiosError(err) && err.response && err.response.data) {
      msg = String(err.response.data);
    } else if (err instanceof Error) {
      msg = err.message;
    }
    setError(msg);
  };

  const createTournament = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');

    try {
      const startDateObj = new Date(newForm.startDate);
      startDateObj.setHours(0, 0, 0, 0);

      const endDateObj = new Date(newForm.endDate);
      endDateObj.setHours(0, 0, 0, 0);

      let deadlineObj: Date | null = null;
      if (newForm.registrationDeadline.trim()) {
        deadlineObj = new Date(newForm.registrationDeadline);
        deadlineObj.setHours(0, 0, 0, 0);
      }

      await api.post('/tournaments', {
        name: newForm.name,
        location: newForm.location,
        startDate: startDateObj.toISOString(),
        endDate: endDateObj.toISOString(),
        registrationDeadline: deadlineObj ? deadlineObj.toISOString() : null,
        description: newForm.description,
      });

      alert('Tournament created!');
      fetchAllTournaments();

      setNewForm({
        name: '',
        location: '',
        startDate: '',
        endDate: '',
        registrationDeadline: '',
        description: '',
      });
    } catch (err) {
      handleError(err, 'Error creating tournament.');
    }
  };

  const startEdit = (t: Tournament) => {
    setEditingId(t.id);
    setEditForm({
      name: t.name,
      location: t.location,
      startDate: t.startDate.slice(0, 10),
      endDate: t.endDate.slice(0, 10),
      registrationDeadline: t.registrationDeadline
        ? t.registrationDeadline.slice(0, 10)
        : '',  
      description: t.description || '',
    });
  };

  const cancelEdit = () => {
    setEditingId(null);
  };

  const updateTournament = async (id: number) => {
    try {
      const startDateObj = new Date(editForm.startDate);
      startDateObj.setHours(0, 0, 0, 0);

      const endDateObj = new Date(editForm.endDate);
      endDateObj.setHours(0, 0, 0, 0);

      let deadlineObj: Date | null = null;
      if (editForm.registrationDeadline.trim()) {
        deadlineObj = new Date(editForm.registrationDeadline);
        deadlineObj.setHours(0, 0, 0, 0);
      }

      await api.put(`/tournaments/${id}`, {
        name: editForm.name,
        location: editForm.location,
        startDate: startDateObj.toISOString(),
        endDate: endDateObj.toISOString(),
        registrationDeadline: deadlineObj ? deadlineObj.toISOString() : null,
        description: editForm.description,
      });
      fetchAllTournaments();
      setEditingId(null);
    } catch (err) {
      handleError(err, 'Error updating tournament.');
    }
  };

  const deleteTournament = async (id: number) => {
    if (!window.confirm('Are you sure you want to delete this tournament?')) return;
    try {
      await api.delete(`/tournaments/${id}`);
      fetchAllTournaments();
    } catch (err) {
      handleError(err, 'Error deleting tournament.');
    }
  };

  return (
    <div className="mt-3">
      <h4>Manage Tournaments</h4>
      {error && <div className="alert alert-danger">{error}</div>}

      <div className="card p-3 mb-4">
        <h5>Create New Tournament</h5>
        <form onSubmit={createTournament} className="row g-3">
          <div className="col-md-6">
            <label className="form-label">Name</label>
            <input
              className="form-control"
              value={newForm.name}
              onChange={(e) => setNewForm({ ...newForm, name: e.target.value })}
              required
            />
          </div>
          <div className="col-md-6">
            <label className="form-label">Location</label>
            <input
              className="form-control"
              value={newForm.location}
              onChange={(e) => setNewForm({ ...newForm, location: e.target.value })}
              required
            />
          </div>
          <div className="col-md-4">
            <label className="form-label">Start Date</label>
            <input
              type="date"
              className="form-control"
              value={newForm.startDate}
              onChange={(e) => setNewForm({ ...newForm, startDate: e.target.value })}
              required
            />
          </div>
          <div className="col-md-4">
            <label className="form-label">End Date</label>
            <input
              type="date"
              className="form-control"
              value={newForm.endDate}
              onChange={(e) => setNewForm({ ...newForm, endDate: e.target.value })}
              required
            />
          </div>
          <div className="col-md-4">
            <label className="form-label">Registration Deadline</label>
            <input
              type="date"
              className="form-control"
              value={newForm.registrationDeadline}
              onChange={(e) => setNewForm({ ...newForm, registrationDeadline: e.target.value })}
            />
          </div>
          <div className="col-12">
            <label className="form-label">Description</label>
            <textarea
              className="form-control"
              value={newForm.description}
              onChange={(e) => setNewForm({ ...newForm, description: e.target.value })}
            />
          </div>
          <div className="col-12">
            <button className="btn btn-success">Create</button>
          </div>
        </form>
      </div>

      <h5>Existing Tournaments</h5>
      <div className="row">
        {tournaments.map((t) => (
          <div key={t.id} className="col-md-6 mb-3">
            <div className="card p-3">
              {editingId === t.id ? (
                <>
                  <input
                    className="form-control mb-2"
                    value={editForm.name}
                    onChange={(e) => setEditForm({ ...editForm, name: e.target.value })}
                  />
                  <input
                    className="form-control mb-2"
                    value={editForm.location}
                    onChange={(e) => setEditForm({ ...editForm, location: e.target.value })}
                  />
                  <label>Start Date:</label>
                  <input
                    type="date"
                    className="form-control mb-2"
                    value={editForm.startDate}
                    onChange={(e) => setEditForm({ ...editForm, startDate: e.target.value })}
                  />
                  <label>End Date:</label>
                  <input
                    type="date"
                    className="form-control mb-2"
                    value={editForm.endDate}
                    onChange={(e) => setEditForm({ ...editForm, endDate: e.target.value })}
                  />
                  <label>Registration Deadline:</label>
                  <input
                    type="date"
                    className="form-control mb-2"
                    value={editForm.registrationDeadline}
                    onChange={(e) => setEditForm({ ...editForm, registrationDeadline: e.target.value })}
                  />
                  <textarea
                    className="form-control mb-2"
                    value={editForm.description}
                    onChange={(e) => setEditForm({ ...editForm, description: e.target.value })}
                  />
                  <div>
                    <button
                      className="btn btn-primary me-2"
                      onClick={() => updateTournament(t.id)}
                    >
                      Save
                    </button>
                    <button
                      className="btn btn-secondary"
                      onClick={cancelEdit}
                    >
                      Cancel
                    </button>
                  </div>
                </>
              ) : (
                <>
                  <div><b>{t.name}</b></div>
                  <div>Location: {t.location}</div>
                  <div>Start: {t.startDate}</div>
                  <div>End: {t.endDate}</div>
                  {t.registrationDeadline && (
                    <div>Deadline: {t.registrationDeadline}</div>
                  )}
                  <div>Status: {t.status}</div>
                  {t.description && <div>Description: {t.description}</div>}
                  <div className="mt-2">
                    <button
                      className="btn btn-warning me-2"
                      onClick={() => startEdit(t)}
                    >
                      Edit
                    </button>
                    <button
                      className="btn btn-danger"
                      onClick={() => deleteTournament(t.id)}
                    >
                      Delete
                    </button>
                  </div>
                </>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

//manage matches section
const AdminMatchesSection: React.FC = () => {
  const [tournaments, setTournaments] = useState<Tournament[]>([]);
  const [selectedTournamentId, setSelectedTournamentId] = useState<number | null>(null);

  const [registeredPlayers, setRegisteredPlayers] = useState<
    { id: number; username: string }[]
  >([]);
  const [referees, setReferees] = useState<User[]>([]);

  const [newMatch, setNewMatch] = useState({
    player1Id: '',
    player2Id: '',
    refereeId: '',
    matchDate: '',
    venue: '',
  });

  const [error, setError] = useState('');

  const [matches, setMatches] = useState<Match[]>([]);

  useEffect(() => {
    fetchTournaments();
    fetchReferees();
  }, []);

  const fetchTournaments = async () => {
    try {
      const res = await api.get<Tournament[]>('/tournaments/all');
      setTournaments(res.data);
    } catch (err) {
      handleError(err, 'Error fetching tournaments.');
    }
  };

  const fetchReferees = async () => {
    try {
      const res = await api.get<User[]>('/users/referees');
      setReferees(res.data);
    } catch (err) {
      handleError(err, 'Error fetching referees.');
    }
  };

  const handleError = (err: unknown, defaultMsg: string) => {
    let msg = defaultMsg;
    if (axios.isAxiosError(err) && err.response && err.response.data) {
      msg = String(err.response.data);
    } else if (err instanceof Error) {
      msg = err.message;
    }
    setError(msg);
  };

  const onTournamentSelect = async (value: string) => {
    setMatches([]); 
    if (!value) {
      setSelectedTournamentId(null);
      setRegisteredPlayers([]);
      setNewMatch({
        player1Id: '',
        player2Id: '',
        refereeId: '',
        matchDate: '',
        venue: '',
      });
      return;
    }

    const tId = Number(value);
    setSelectedTournamentId(tId);
    setNewMatch({
      player1Id: '',
      player2Id: '',
      refereeId: '',
      matchDate: '',
      venue: '',
    });
    setError('');

    try {
      const regRes = await api.get<Registration[]>(`/registrations/tournament/${tId}`);
      const players = regRes.data.map(r => ({
        id: r.playerId,
        username: r.playerUsername,
      }));
      setRegisteredPlayers(players);

      const matchRes = await api.get<Match[]>(`/matches/tournament/${tId}`);
      setMatches(matchRes.data);

    } catch (err) {
      handleError(err, 'Error fetching tournament info.');
    }
  };

  const createMatch = async (e: React.FormEvent) => {
    e.preventDefault();
    if (selectedTournamentId === null) {
      alert('Please select a tournament first.');
      return;
    }

    const tournament = tournaments.find(t => t.id === selectedTournamentId);
    if (!tournament) {
      setError('Tournament details not found.');
      return;
    }

    const matchDateTime = new Date(newMatch.matchDate);
    const tStart = new Date(tournament.startDate);
    const tEnd = new Date(tournament.endDate);

    if (matchDateTime < tStart || matchDateTime > tEnd) {
      setError(
        `Match date must be between ${tournament.startDate.slice(0,10)} 
         and ${tournament.endDate.slice(0,10)}.`
      );
      return;
    }

    setError('');

    try {
      await api.post('/matches', {
        tournamentId: selectedTournamentId,
        player1Id: Number(newMatch.player1Id),
        player2Id: Number(newMatch.player2Id),
        refereeId: Number(newMatch.refereeId),
        matchDate: matchDateTime.toISOString(),
        venue: newMatch.venue,
      });
      alert('Match created successfully!');

      setNewMatch({
        player1Id: '',
        player2Id: '',
        refereeId: '',
        matchDate: '',
        venue: '',
      });

      const updatedMatches = await api.get<Match[]>(`/matches/tournament/${selectedTournamentId}`);
      setMatches(updatedMatches.data);

    } catch (err) {
      handleError(err, 'Error creating match.');
    }
  };

  return (
    <div className="mt-3">
      <h4>Manage Matches</h4>
      {error && <div className="alert alert-danger">{error}</div>}

      <div className="mb-3">
        <label className="form-label">Select Tournament</label>
        <select
          className="form-select"
          value={selectedTournamentId !== null ? selectedTournamentId : ''}
          onChange={(e) => onTournamentSelect(e.target.value)}
        >
          <option value="">-- Choose a Tournament --</option>
          {tournaments.map((t) => (
            <option key={t.id} value={t.id}>
              {t.name} ({t.startDate.slice(0,10)} to {t.endDate.slice(0,10)})
            </option>
          ))}
        </select>
      </div>

      {selectedTournamentId && (
        <form onSubmit={createMatch} className="card p-3 mb-4">
          <h5>Create a Match</h5>
          <div className="row g-3">
            <div className="col-md-4">
              <label className="form-label">Player 1</label>
              <select
                className="form-select"
                value={newMatch.player1Id}
                onChange={(e) => setNewMatch({ ...newMatch, player1Id: e.target.value })}
                required
              >
                <option value="">-- select --</option>
                {registeredPlayers.map((p) => (
                  <option key={p.id} value={p.id}>
                    {p.username}
                  </option>
                ))}
              </select>
            </div>

            <div className="col-md-4">
              <label className="form-label">Player 2</label>
              <select
                className="form-select"
                value={newMatch.player2Id}
                onChange={(e) => setNewMatch({ ...newMatch, player2Id: e.target.value })}
                required
              >
                <option value="">-- select --</option>
                {registeredPlayers
                  .filter(p => p.id !== Number(newMatch.player1Id))
                  .map((p) => (
                    <option key={p.id} value={p.id}>
                      {p.username}
                    </option>
                  ))}
              </select>
            </div>

            <div className="col-md-4">
              <label className="form-label">Referee</label>
              <select
                className="form-select"
                value={newMatch.refereeId}
                onChange={(e) => setNewMatch({ ...newMatch, refereeId: e.target.value })}
                required
              >
                <option value="">-- select --</option>
                {referees.map((r) => (
                  <option key={r.id} value={r.id}>
                    {r.username} ({r.firstName} {r.lastName})
                  </option>
                ))}
              </select>
            </div>

            <div className="col-md-6">
              <label className="form-label">Match Date/Time</label>
              <input
                type="datetime-local"
                className="form-control"
                value={newMatch.matchDate}
                onChange={(e) => setNewMatch({ ...newMatch, matchDate: e.target.value })}
                required
              />
            </div>

            <div className="col-md-6">
              <label className="form-label">Venue</label>
              <input
                className="form-control"
                value={newMatch.venue}
                onChange={(e) => setNewMatch({ ...newMatch, venue: e.target.value })}
                required
              />
            </div>
          </div>
          <div className="mt-3">
            <button className="btn btn-success" type="submit">
              Create Match
            </button>
          </div>
        </form>
      )}

      {selectedTournamentId && (
        <div>
          <h5>Matches in this Tournament</h5>
          {matches.length === 0 ? (
            <p>No matches found for this tournament.</p>
          ) : (
            <table className="table table-striped">
              <thead>
                <tr>
                  <th>Match ID</th>
                  <th>Player 1</th>
                  <th>Player 2</th>
                  <th>Referee</th>
                  <th>Date</th>
                  <th>Venue</th>
                  <th>Score</th>
                </tr>
              </thead>
              <tbody>
                {matches.map(m => (
                  <tr key={m.matchId}>
                    <td>{m.matchId}</td>
                    <td>{m.player1Name}</td>
                    <td>{m.player2Name}</td>
                    <td>{m.refereeName}</td>
                    <td>{m.matchDate}</td>
                    <td>{m.venue}</td>
                    <td>{m.overallScore || 'N/A'}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      )}
    </div>
  );
};

//export section
const AdminExportSection: React.FC = () => {
  const [filters, setFilters] = useState({
    tournamentId: '',
    playerId: '',
    refereeId: '',
  });
  const [tournaments, setTournaments] = useState<Tournament[]>([]);
  const [players, setPlayers] = useState<User[]>([]);
  const [referees, setReferees] = useState<User[]>([]);

  useEffect(() => {
    fetchTournaments();
    fetchPlayers();
    fetchReferees();
  }, []);

  const handleFiltersChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setFilters(prev => ({
      ...prev,
      [e.target.name]: e.target.value,
    }));
  };

  const exportMatches = async (format: 'csv' | 'txt') => {
    try {
      const queryParams = new URLSearchParams();
      queryParams.append('format', format);
      if (filters.tournamentId) queryParams.append('tournamentId', filters.tournamentId);
      if (filters.playerId) queryParams.append('playerId', filters.playerId);
      if (filters.refereeId) queryParams.append('refereeId', filters.refereeId);

      const res = await api.get(`/matches/export?${queryParams.toString()}`, {
        responseType: 'blob',
      });
      const blob = new Blob([res.data], { type: 'text/plain;charset=utf-8' });
      const url = window.URL.createObjectURL(blob);
      const link = document.createElement('a');
      link.href = url;
      link.download = `matches_export.${format}`;
      link.click();
      window.URL.revokeObjectURL(url);
    } catch (err) {
      if (axios.isAxiosError(err) && err.response && err.response.data) {
        alert(String(err.response.data));
      } else if (err instanceof Error) {
        alert(err.message);
      } else {
        alert('Error exporting matches.');
      }
    }
  };

  const fetchTournaments = async () => {
    try {
      const res = await api.get<Tournament[]>('/tournaments/all');
      setTournaments(res.data);
    } catch (err) {
      console.error('Error fetching tournaments', err);
    }
  };

  const fetchPlayers = async () => {
    try {
      const res = await api.get<User[]>('/users/players');
      setPlayers(res.data);
    } catch (err) {
      console.error('Error fetching players', err);
    }
  };

  const fetchReferees = async () => {
    try {
      const res = await api.get<User[]>('/users/referees');
      setReferees(res.data);
    } catch (err) {
      console.error('Error fetching referees', err);
    }
  };

  return (
    <div className="mt-3">
      <h4>Export Matches</h4>
      <div className="row g-2 mb-3">
        <div className="col">
          <select
            className="form-select"
            name="tournamentId"
            value={filters.tournamentId}
            onChange={handleFiltersChange}
          >
            <option value="">All Tournaments</option>
            {tournaments.map((t) => (
              <option key={t.id} value={t.id}>
                {t.name}
              </option>
            ))}
          </select>
        </div>
        <div className="col">
          <select
            className="form-select"
            name="playerId"
            value={filters.playerId}
            onChange={handleFiltersChange}
          >
            <option value="">All Players</option>
            {players.map((p) => (
              <option key={p.id} value={p.id}>
                {p.username}
              </option>
            ))}
          </select>
        </div>
        <div className="col">
          <select
            className="form-select"
            name="refereeId"
            value={filters.refereeId}
            onChange={handleFiltersChange}
          >
            <option value="">All Referees</option>
            {referees.map((r) => (
              <option key={r.id} value={r.id}>
                {r.username}
              </option>
            ))}
          </select>
        </div>
      </div>
      <button
        className="btn btn-outline-success me-2"
        onClick={() => exportMatches('csv')}
      >
        Export Matches (CSV)
      </button>
      <button
        className="btn btn-outline-success"
        onClick={() => exportMatches('txt')}
      >
        Export Matches (TXT)
      </button>
    </div>
  );
};
