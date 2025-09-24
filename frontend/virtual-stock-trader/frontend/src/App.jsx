import React, { useState, useEffect, createContext, useContext } from 'react';
import { BrowserRouter, Routes, Route, Link, useNavigate, Navigate, NavLink } from 'react-router-dom';
import axios from 'axios';
import { jwtDecode } from 'jwt-decode';
import { ShieldCheck, BarChart, BookOpen, Trophy, Newspaper, TrendingUp, Briefcase, GraduationCap, Gamepad2, UserCog } from 'lucide-react';
import { Line } from 'react-chartjs-2';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

// --- 1. API Configuration ---
const api = axios.create({ baseURL: 'http://localhost:8081' });
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});

// --- 2. Auth Context ---
const AuthContext = createContext();
const useAuth = () => useContext(AuthContext);

const AuthProvider = ({ children }) => {
  const [authToken, setAuthToken] = useState(() => localStorage.getItem('token'));
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(true);
  const logout = () => { localStorage.removeItem('token'); setAuthToken(null); setUser(null); };
  useEffect(() => {
    if (authToken) {
      try {
        const decoded = jwtDecode(authToken);
        if (decoded.exp * 1000 < Date.now()) logout();
        else setUser({ email: decoded.sub, roles: decoded.roles.map(r => r.authority) });
      } catch (e) { logout(); }
    }
    setLoading(false);
  }, [authToken]);
  const login = (token) => { localStorage.setItem('token', token); setAuthToken(token); };
  const value = { authToken, user, login, logout, isAdmin: () => user?.roles?.includes('ROLE_ADMIN') };
  return <AuthContext.Provider value={value}>{!loading && children}</AuthContext.Provider>;
};

// --- 3. Page Components ---

const LandingPage = () => (
    <div className="bg-gray-900 text-white"><div className="text-center py-20 px-4"><h1 className="text-5xl md:text-6xl font-extrabold mb-4">Virtual Stock Trading, Perfected.</h1><p className="text-xl md:text-2xl text-gray-300 mb-8 max-w-3xl mx-auto">Learn the markets, hone your strategy, and compete—all without risking a single rupee.</p><div className="space-x-4"><Link to="/signup" className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-3 px-8 rounded-full text-lg">Get Started</Link><Link to="/login" className="bg-gray-700 hover:bg-gray-600 text-white font-bold py-3 px-8 rounded-full text-lg">Login</Link></div></div><div className="py-20 bg-gray-900"><div className="container mx-auto px-4"><h2 className="text-4xl font-bold text-center mb-12">Everything You Need to Succeed</h2><div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-8"><div className="bg-gray-800 p-6 rounded-lg shadow-lg text-center"><ShieldCheck className="w-12 h-12 text-blue-500 mx-auto mb-4" /> <h3 className="text-xl font-bold">Risk-Free Trading</h3></div><div className="bg-gray-800 p-6 rounded-lg shadow-lg text-center"><BarChart className="w-12 h-12 text-blue-500 mx-auto mb-4" /> <h3 className="text-xl font-bold">Performance Analytics</h3></div><div className="bg-gray-800 p-6 rounded-lg shadow-lg text-center"><BookOpen className="w-12 h-12 text-blue-500 mx-auto mb-4" /> <h3 className="text-xl font-bold">Educational Tutorials</h3></div><div className="bg-gray-800 p-6 rounded-lg shadow-lg text-center"><Trophy className="w-12 h-12 text-blue-500 mx-auto mb-4" /> <h3 className="text-xl font-bold">Trading Competitions</h3></div><div className="bg-gray-800 p-6 rounded-lg shadow-lg text-center"><Newspaper className="w-12 h-12 text-blue-500 mx-auto mb-4" /> <h3 className="text-xl font-bold">Real-Time News</h3></div><div className="bg-gray-800 p-6 rounded-lg shadow-lg text-center"><ShieldCheck className="w-12 h-12 text-blue-500 mx-auto mb-4" /> <h3 className="text-xl font-bold">Secure & Role-Based</h3></div></div></div></div></div>
);

const AuthForm = ({ isLogin }) => {
    const [formData, setFormData] = useState({ username: '', email: '', password: '' });
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(false);
    const { login } = useAuth();
    const navigate = useNavigate();
    const handleChange = (e) => setFormData({ ...formData, [e.target.name]: e.target.value });
    const handleSubmit = async (e) => {
        e.preventDefault(); setLoading(true); setError('');
        try {
            if (isLogin) {
                const response = await api.post('/api/auth/login', { email: formData.email, password: formData.password });
                login(response.data.token);
                navigate('/dashboard');
            } else {
                await api.post('/api/auth/signup', formData);
                alert('Signup successful! Please log in.');
                navigate('/login');
            }
        } catch (err) { 
            console.error('Auth error:', err);
            const apiMessage = err.response?.data?.message || err.response?.data?.error || err.response?.statusText;
            setError(apiMessage || err.message || 'An error occurred.'); 
        } 
        finally { setLoading(false); }
    };
    return (
        <div className="flex items-center justify-center min-h-[80vh]"><div className="w-full max-w-md p-8 space-y-6 bg-gray-800 rounded-lg shadow-lg"><h2 className="text-3xl font-bold text-center text-white">{isLogin ? 'Welcome Back' : 'Create an Account'}</h2><form onSubmit={handleSubmit} className="space-y-6">{!isLogin && (<div><label className="block text-sm font-medium text-gray-300">Username</label><input type="text" name="username" onChange={handleChange} required className="w-full px-3 py-2 mt-1 text-white bg-gray-700 border border-gray-600 rounded-md" /></div>)}<div><label className="block text-sm font-medium text-gray-300">Email</label><input type="email" name="email" onChange={handleChange} required className="w-full px-3 py-2 mt-1 text-white bg-gray-700 border border-gray-600 rounded-md" /></div><div><label className="block text-sm font-medium text-gray-300">Password</label><input type="password" name="password" onChange={handleChange} required className="w-full px-3 py-2 mt-1 text-white bg-gray-700 border border-gray-600 rounded-md" /></div>{error && <p className="text-red-500 text-sm text-center">{error}</p>}<button type="submit" disabled={loading} className="w-full py-2 font-bold text-white bg-blue-600 rounded-md hover:bg-blue-700 disabled:bg-gray-500">{loading ? 'Processing...' : (isLogin ? 'Login' : 'Sign Up')}</button></form><p className="text-sm text-center text-gray-400">{isLogin ? "Don't have an account? " : "Already have an account? "}<Link to={isLogin ? '/signup' : '/login'} className="font-medium text-blue-400 hover:underline">{isLogin ? 'Sign Up' : 'Login'}</Link></p></div></div>
    );
};

const DashboardPage = () => {
    const { user, isAdmin } = useAuth();
    return ( <div className="space-y-8"><h1 className="text-4xl font-bold">Welcome, {user?.email}!</h1><p className="text-xl text-gray-300">This is your central hub. Use the navigation to view your portfolio, explore the market, and more.</p>{isAdmin() && <div className="p-6 bg-yellow-900 text-yellow-200 rounded-lg"><h2 className="text-2xl font-bold mb-2">Admin Access</h2><p>You are logged in as an Administrator. Access the <Link to="/admin" className="font-bold underline hover:text-white">Admin Panel</Link> to manage content.</p></div>}</div> );
};

const PortfolioPage = () => {
    const [data, setData] = useState(null);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);
    const [busy, setBusy] = useState(false);
    useEffect(() => {
        (async () => {
            try {
                const res = await api.get('/api/portfolio');
                setData(res.data);
            } catch (err) {
                setError(err.response?.data?.message || 'Failed to load portfolio');
            } finally { setLoading(false); }
        })();
    }, []);
    const refresh = async () => {
        try {
            const res = await api.get('/api/portfolio');
            setData(res.data);
        } catch {}
    };
    const sell = async (symbol, quantity) => {
        if (!quantity || quantity <= 0) return;
        setBusy(true);
        try {
            await api.post('/api/portfolio/sell', { symbol, quantity });
            await refresh();
            alert('Sell order placed');
        } catch (err) { alert(err.response?.data?.message || 'Sell failed'); }
        finally { setBusy(false); }
    };
    if (loading) return <p>Loading portfolio...</p>;
    if (error) return <p className="text-red-400">{error}</p>;
    return (
        <div className="space-y-4">
            <h1 className="text-3xl font-bold">Your Portfolio</h1>
            {!data || !data.holdings?.length ? (
                <p className="text-gray-400">No holdings yet. Buy your first stock from the Market.</p>
            ) : (
                <div className="overflow-x-auto">
                    <table className="min-w-full text-left">
                        <thead className="text-gray-300">
                            <tr><th className="py-2 pr-6">Symbol</th><th className="py-2 pr-6">Quantity</th><th className="py-2 pr-6">Avg Price</th><th className="py-2 pr-6"></th></tr>
                        </thead>
                        <tbody>
                            {data.holdings.map((h, idx) => (
                                <tr key={idx} className="border-t border-gray-700">
                                    <td className="py-2 pr-6">{h.symbol}</td>
                                    <td className="py-2 pr-6">{h.quantity}</td>
                                    <td className="py-2 pr-6">{h.averagePrice}</td>
                                    <td className="py-2 pr-6">
                                        <div className="flex items-center gap-2">
                                            <input type="number" min="1" max={h.quantity} defaultValue={1} id={`sell-${idx}`} className="w-24 px-2 py-1 bg-gray-800 border border-gray-700 rounded" />
                                            <button disabled={busy} onClick={() => sell(h.symbol, Number(document.getElementById(`sell-${idx}`).value))} className="bg-red-600 hover:bg-red-700 px-3 py-1 rounded disabled:bg-gray-600">Sell</button>
                                        </div>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

const MarketPage = () => {
	const [input, setInput] = useState('IBM');
	const [symbols, setSymbols] = useState(['IBM', 'AAPL', 'MSFT']);
	const [quotesBySymbol, setQuotesBySymbol] = useState({});
	const [loading, setLoading] = useState(false);
	const [error, setError] = useState('');

	const normalize = (raw) => {
		const q = raw?.['Global Quote'] || raw;
		if (!q) return null;
		return {
			symbol: q['01. symbol'] || q.symbol,
			open: Number(q['02. open'] || q.open),
			high: Number(q['03. high'] || q.high),
			low: Number(q['04. low'] || q.low),
			price: Number(q['05. price'] || q.price),
			volume: Number(q['06. volume'] || q.volume),
			latestTradingDay: q['07. latest trading day'] || q.latestTradingDay,
			previousClose: Number(q['08. previous close'] || q.previousClose),
			change: Number(q['09. change'] || q.change),
			changePercent: (q['10. change percent'] || q.changePercent || '').toString(),
		};
	};

	const fetchQuotes = async (list) => {
		if (!list.length) return;
		setLoading(true); setError('');
		try {
			const results = await Promise.allSettled(list.map(s => api.get(`/api/market/stocks/${s}`)));
			const next = {};
			results.forEach((r, idx) => {
				const s = list[idx];
				if (r.status === 'fulfilled') next[s] = normalize(r.value.data);
			});
			setQuotesBySymbol(prev => ({ ...prev, ...next }));
		} catch (err) {
			setError('Failed to load market data');
		} finally { setLoading(false); }
	};

	useEffect(() => { fetchQuotes(symbols); }, []);
	useEffect(() => {
		const id = setInterval(() => { fetchQuotes(symbols); }, 15000);
		return () => clearInterval(id);
	}, [symbols]);

	const add = (s) => {
		const sym = (s || input).trim().toUpperCase();
		if (!sym || symbols.includes(sym)) return;
		const next = symbols.concat(sym).slice(0, 8);
		setSymbols(next);
		fetchQuotes([sym]);
		if (!s) setInput('');
	};
	const remove = (s) => setSymbols(symbols.filter(x => x !== s));

	const quickAdds = ['TSLA', 'GOOGL', 'AMZN', 'NVDA'];

	return (
		<div className="space-y-6">
			<h1 className="text-3xl font-bold">Market</h1>

			<div className="flex flex-wrap items-center gap-2">
				<input
					value={input}
					onChange={e => setInput(e.target.value.toUpperCase())}
					className="px-3 py-2 bg-gray-800 border border-gray-700 rounded"
					placeholder="Add symbol (e.g., AAPL)"
				/>
				<button onClick={() => add()} className="bg-blue-600 hover:bg-blue-700 px-4 py-2 rounded">Add</button>
				<span className="text-gray-400 mx-2">Quick add:</span>
				{quickAdds.map(s => (
					<button key={s} onClick={() => add(s)} className="bg-gray-800 hover:bg-gray-700 px-3 py-1 rounded border border-gray-700">{s}</button>
				))}
			</div>

			{error && <p className="text-red-400">{error}</p>}
			{loading && <p>Refreshing data...</p>}

            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-4">
				{symbols.map((s) => {
					const q = quotesBySymbol[s];
					const price = typeof q?.price === 'number' ? q.price : 0;
					const changeNum = typeof q?.change === 'number' ? q.change : 0;
					const isUp = changeNum >= 0;
					return (
                        <div key={s} className="p-4 rounded-lg bg-gray-800 border border-gray-700">
							<div className="flex items-center justify-between">
								<h3 className="text-xl font-semibold">{s}</h3>
								<button onClick={() => remove(s)} className="text-gray-400 hover:text-red-400">Remove</button>
							</div>
							{!q ? (
								<p className="text-gray-400 mt-4">No data yet.</p>
							) : (
								<div className="mt-3 space-y-1">
									<div className="text-3xl font-bold">{price.toFixed(2)}</div>
									<div className={isUp ? 'text-green-400' : 'text-red-400'}>
										{isUp ? '+' : ''}{changeNum.toFixed(2)} ({q.changePercent})
									</div>
									<div className="text-gray-400 text-sm">Prev close: {typeof q.previousClose === 'number' ? q.previousClose.toFixed(2) : q.previousClose}</div>
									<div className="text-gray-400 text-sm">High: {typeof q.high === 'number' ? q.high.toFixed(2) : q.high} • Low: {typeof q.low === 'number' ? q.low.toFixed(2) : q.low}</div>
									<div className="text-gray-500 text-xs">Updated: {q.latestTradingDay || '—'}</div>
                                    <div className="pt-3 flex items-center gap-2">
                                        <input type="number" min="1" defaultValue={1} id={`qty-${s}`} className="w-24 px-2 py-1 bg-gray-900 border border-gray-700 rounded" />
                                        <button onClick={async () => { try { await api.post('/api/portfolio/buy', { symbol: s, quantity: Number(document.getElementById(`qty-${s}`).value) }); alert('Buy order placed'); } catch (err) { alert(err.response?.data?.message || 'Buy failed'); } }} className="bg-blue-600 hover:bg-blue-700 px-3 py-1 rounded">Buy</button>
                                        <button onClick={async () => { try { await api.post('/api/portfolio/sell', { symbol: s, quantity: Number(document.getElementById(`qty-${s}`).value) }); alert('Sell order placed'); } catch (err) { alert(err.response?.data?.message || 'Sell failed'); } }} className="bg-red-600 hover:bg-red-700 px-3 py-1 rounded">Sell</button>
                                    </div>
								</div>
							)}
						</div>
					);
				})}
			</div>
		</div>
	);
};

const TutorialsPage = () => {
    const [tutorials, setTutorials] = useState([]);
    const [error, setError] = useState('');
    useEffect(() => {
        (async () => {
            try {
                const res = await api.get('/api/tutorials');
                setTutorials(Array.isArray(res.data) ? res.data : []);
            } catch (err) {
                // If GET is not implemented, show empty state instead of blank page
                setError('');
            }
        })();
    }, []);
    return (
        <div className="space-y-4">
            <h1 className="text-3xl font-bold">Tutorials</h1>
            {error && <p className="text-red-400">{error}</p>}
            {!tutorials.length ? (
                <p className="text-gray-400">No tutorials yet. Check back later.</p>
            ) : (
                <div className="space-y-4">
                    {tutorials.map((t, i) => (
                        <div key={i} className="p-4 bg-gray-800 rounded">
                            <h3 className="text-xl font-semibold">{t.title}</h3>
                            <p className="text-gray-300">{t.content}</p>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

const CompetitionsPage = () => {
    const [comps, setComps] = useState([]);
    const [error, setError] = useState('');
    const [loading, setLoading] = useState(true);
    const load = async () => {
        setLoading(true); setError('');
        try {
            const res = await api.get('/api/competitions');
            setComps(Array.isArray(res.data) ? res.data : []);
        } catch (err) { setError(err.response?.data?.message || 'Failed to load competitions'); }
        finally { setLoading(false); }
    };
    useEffect(() => { load(); }, []);
    const join = async (id) => {
        try {
            await api.post(`/api/competitions/join/${id}`);
            await load();
            alert('Joined competition');
        } catch (err) { alert(err.response?.data?.message || 'Join failed'); }
    };
    if (loading) return <p>Loading competitions...</p>;
    return (
        <div className="space-y-4">
            <h1 className="text-3xl font-bold">Competitions</h1>
            {error && <p className="text-red-400">{error}</p>}
            {!comps.length ? (
                <p className="text-gray-400">No competitions available.</p>
            ) : (
                <div className="space-y-3">
                    {comps.map((c) => (
                        <div key={c.id ?? c.name} className="p-4 bg-gray-800 rounded flex items-center justify-between">
                            <div>
                                <h3 className="text-xl font-semibold">{c.name}</h3>
                                <p className="text-gray-300">{c.description}</p>
                            </div>
                            <button onClick={() => join(c.id)} className="bg-blue-600 hover:bg-blue-700 px-4 py-2 rounded">Join</button>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

const AdminPage = () => {
    const [tutorial, setTutorial] = useState({ title: '', content: '' });
    const [competition, setCompetition] = useState({ name: '', description: '', startDate: '', endDate: '', startingBalance: 50000 });
    const [message, setMessage] = useState('');
    const { user } = useAuth();

    const handleTutorialSubmit = async (e) => {
        e.preventDefault();
        try {
            await api.post('/api/tutorials', tutorial);
            setMessage('Tutorial created successfully!');
            setTutorial({ title: '', content: '' });
        } catch (err) { setMessage('Error creating tutorial.'); }
    };

    const handleCompetitionSubmit = async (e) => {
        e.preventDefault();
        try {
            await api.post('/api/competitions', {...competition, startDate: `${competition.startDate}T00:00:00`, endDate: `${competition.endDate}T23:59:59`});
            setMessage('Competition created successfully!');
            setCompetition({ name: '', description: '', startDate: '', endDate: '', startingBalance: 50000 });
        } catch (err) { setMessage('Error creating competition.'); }
    };

    return (
        <div className="space-y-12">
            <h1 className="text-4xl font-bold">Admin Panel</h1>
            <div className="p-4 bg-yellow-900 text-yellow-200 rounded-lg">You are logged in as <span className="font-semibold">{user?.email}</span> with <span className="font-semibold">Administrator</span> rights.</div>
            {message && <div className="p-4 bg-green-900 text-green-200 rounded-lg">{message}</div>}
            
            <div className="p-6 bg-gray-800 rounded-lg"><h2 className="text-2xl font-bold mb-4">Create New Tutorial</h2><form onSubmit={handleTutorialSubmit} className="space-y-4">
                <input type="text" placeholder="Tutorial Title" value={tutorial.title} onChange={e => setTutorial({...tutorial, title: e.target.value})} required className="w-full p-2 bg-gray-700 rounded-md" />
                <textarea placeholder="Tutorial Content" value={tutorial.content} onChange={e => setTutorial({...tutorial, content: e.target.value})} required rows="5" className="w-full p-2 bg-gray-700 rounded-md"></textarea>
                <button type="submit" className="py-2 px-6 font-bold text-white bg-blue-600 rounded-md hover:bg-blue-700">Create Tutorial</button>
            </form></div>

            <div className="p-6 bg-gray-800 rounded-lg"><h2 className="text-2xl font-bold mb-4">Create New Competition</h2><form onSubmit={handleCompetitionSubmit} className="space-y-4">
                <input type="text" placeholder="Competition Name" value={competition.name} onChange={e => setCompetition({...competition, name: e.target.value})} required className="w-full p-2 bg-gray-700 rounded-md"/>
                <textarea placeholder="Description" value={competition.description} onChange={e => setCompetition({...competition, description: e.target.value})} className="w-full p-2 bg-gray-700 rounded-md"></textarea>
                <div className="grid grid-cols-2 gap-4">
                    <input type="date" value={competition.startDate} onChange={e => setCompetition({...competition, startDate: e.target.value})} required className="w-full p-2 bg-gray-700 rounded-md"/>
                    <input type="date" value={competition.endDate} onChange={e => setCompetition({...competition, endDate: e.target.value})} required className="w-full p-2 bg-gray-700 rounded-md"/>
                </div>
                <button type="submit" className="py-2 px-6 font-bold text-white bg-blue-600 rounded-md hover:bg-blue-700">Create Competition</button>
            </form></div>
        </div>
    );
};


// --- 4. Layout & Main App Router ---
const Navbar = () => {
    const { authToken, user, logout, isAdmin } = useAuth();
    const navigate = useNavigate();
    const handleLogout = () => { logout(); navigate('/login'); };
    const navLinkClasses = "text-gray-300 hover:text-white transition-colors duration-300 px-3 py-2 rounded-md font-medium flex items-center gap-2";
    const activeLinkClasses = "bg-gray-900 text-white";

    return (
        <nav className="bg-gray-800 p-4 shadow-lg sticky top-0 z-50"><div className="container mx-auto flex justify-between items-center">
            <Link to={authToken ? "/dashboard" : "/"} className="text-2xl font-bold text-white hover:text-blue-400">VSTrader</Link>
            <div className="flex items-center space-x-2">
                {authToken ? (
                    <>
                        <NavLink to="/dashboard" className={({isActive}) => isActive ? `${navLinkClasses} ${activeLinkClasses}`: navLinkClasses}><TrendingUp size={18}/> Dashboard</NavLink>
                        <NavLink to="/portfolio" className={({isActive}) => isActive ? `${navLinkClasses} ${activeLinkClasses}`: navLinkClasses}><Briefcase size={18}/>Portfolio</NavLink>
                        <NavLink to="/market" className={({isActive}) => isActive ? `${navLinkClasses} ${activeLinkClasses}`: navLinkClasses}><BarChart size={18}/>Market</NavLink>
                        <NavLink to="/tutorials" className={({isActive}) => isActive ? `${navLinkClasses} ${activeLinkClasses}`: navLinkClasses}><GraduationCap size={18}/>Tutorials</NavLink>
                        <NavLink to="/competitions" className={({isActive}) => isActive ? `${navLinkClasses} ${activeLinkClasses}`: navLinkClasses}><Gamepad2 size={18}/>Competitions</NavLink>
                        {isAdmin() && <NavLink to="/admin" className={({isActive}) => isActive ? `${navLinkClasses} ${activeLinkClasses}`: navLinkClasses}><UserCog size={18}/>Admin Panel</NavLink>}
                        <span className="text-gray-500">|</span>
                        <span className="text-blue-300 px-3">{user?.email}</span>
                        <button onClick={handleLogout} className="bg-red-600 hover:bg-red-700 text-white font-bold py-2 px-4 rounded">Logout</button>
                    </>
                ) : ( <div className="space-x-4"><Link to="/login" className="text-gray-300 hover:text-white">Login</Link><Link to="/signup" className="bg-blue-600 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">Sign Up</Link></div> )}
            </div>
        </div></nav>
    );
};

const ProtectedRoute = ({ children }) => {
    const { authToken } = useAuth();
    if (!authToken) return <Navigate to="/login" replace />;
    return children;
};

const AdminRoute = ({ children }) => {
    const { isAdmin } = useAuth();
    if (!isAdmin()) return <Navigate to="/dashboard" replace />;
    return children;
};

// This is the single, main component for your entire frontend.
export default function App() {
    return (
        <AuthProvider>
            <BrowserRouter>
                <div className="bg-gray-900 text-gray-100 min-h-screen font-sans">
                    <Navbar />
                    <main className="container mx-auto p-4 md:p-8">
                        <Routes>
                            <Route path="/" element={<LandingPage />} />
                            <Route path="/login" element={<AuthForm isLogin />} />
                            <Route path="/signup" element={<AuthForm />} />
                            <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
                            <Route path="/portfolio" element={<ProtectedRoute><PortfolioPage /></ProtectedRoute>} />
                            <Route path="/market" element={<ProtectedRoute><MarketPage /></ProtectedRoute>} />
                            <Route path="/tutorials" element={<ProtectedRoute><TutorialsPage /></ProtectedRoute>} />
                            <Route path="/competitions" element={<ProtectedRoute><CompetitionsPage /></ProtectedRoute>} />
                            <Route path="/admin" element={<AdminRoute><AdminPage/></AdminRoute>} />
                        </Routes>
                    </main>
                </div>
            </BrowserRouter>
        </AuthProvider>
    );
}
