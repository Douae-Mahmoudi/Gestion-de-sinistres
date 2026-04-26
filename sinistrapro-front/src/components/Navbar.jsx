import React, { useState, useEffect, useRef } from "react";
import {
  LogOut, LayoutDashboard, FileText, User, Bell,
  Check, CheckCheck, X, ClipboardList, ShieldCheck,
  CheckCircle, Search, Lock, RefreshCw, FilePlus
} from "lucide-react";
import { sinistreService } from "../services/sinistreService";
 
export function Navbar({ page, setPage, user, onLogout }) {
  const [notifOpen, setNotifOpen] = useState(false);
  const [notifications, setNotifications] = useState([]);
  const [count, setCount] = useState(0);
  const notifRef = useRef(null);
 
  useEffect(() => {
    const handler = (e) => {
      if (notifRef.current && !notifRef.current.contains(e.target))
        setNotifOpen(false);
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);
 
  useEffect(() => {
    if (!user) return;
    const fetchCount = async () => {
      try { setCount(await sinistreService.notifications.countNonLues()); } catch {}
    };
    fetchCount();
    const interval = setInterval(fetchCount, 30000);
    return () => clearInterval(interval);
  }, [user]);
 
  const handleOpenNotif = async () => {
    const next = !notifOpen;
    setNotifOpen(next);
    if (next) {
      try { setNotifications(await sinistreService.notifications.getAll()); } catch {}
    }
  };
 
  const marquerLue = async (id) => {
    try {
      await sinistreService.notifications.marquerLue(id);
      setNotifications(prev => prev.map(n => n.id === id ? { ...n, lue: true } : n));
      setCount(c => Math.max(0, c - 1));
    } catch {}
  };
 
  const marquerToutesLues = async () => {
    try {
      await sinistreService.notifications.marquerToutesLues();
      setNotifications(prev => prev.map(n => ({ ...n, lue: true })));
      setCount(0);
    } catch {}
  };
 
  const homePage = () => {
    if (!user) return "Services";
    switch (user.role) {
      case "AGENT":       return "AgentDashboard";
      case "EXPERT":      return "ExpertDashboard";
      case "SUPERVISEUR": return "DossiersSuperviseur";
      default:            return "ClientDashboard";
    }
  };
 
  const roleBadgeStyle = () => {
    switch (user?.role) {
      case "AGENT":       return "bg-orange-500/10 text-orange-400";
      case "EXPERT":      return "bg-purple-500/10 text-purple-400";
      case "SUPERVISEUR": return "bg-yellow-500/10 text-yellow-400";
      default:            return "bg-cyan-500/10 text-cyan-400";
    }
  };
 
  const linkStyle = (targetPage) =>
    `cursor-pointer flex items-center gap-2 transition-all duration-300 py-2 px-1 text-lg ${
      page === targetPage
        ? "text-cyan-400 border-b-2 border-cyan-400 font-bold"
        : "text-slate-400 hover:text-white hover:bg-white/5 rounded-lg px-3"
    }`;
 
  return (
    <nav className="fixed top-0 left-0 right-0 z-50 bg-[#060c1c]/90 backdrop-blur-xl border-b border-white/5 shadow-2xl">
      <div className="w-full px-4 h-20 flex items-center justify-between">
 
        <div onClick={() => setPage(homePage())}
          className="text-2xl font-black cursor-pointer tracking-tighter text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-blue-500">
          SINISTRA<span className="text-white">PRO</span>
        </div>
 
        <div className="hidden md:flex items-center gap-12 font-medium">
 
          {!user && (
            <>
              <span onClick={() => setPage("Services")}       className={linkStyle("Services")}>Services</span>
              <span onClick={() => setPage("Fonctionnement")} className={linkStyle("Fonctionnement")}>Fonctionnement</span>
            </>
          )}
 
          {user?.role === "CLIENT" && (
            <>
              <span onClick={() => setPage("ClientDashboard")} className={linkStyle("ClientDashboard")}>
                <LayoutDashboard size={18}/> Dashboard
              </span>
              <span onClick={() => setPage("MesSinistres")} className={linkStyle("MesSinistres")}>
                <FileText size={18}/> Mes Sinistres
              </span>
              <span onClick={() => setPage("Profil")} className={linkStyle("Profil")}>
                <User size={18}/> Profil
              </span>
            </>
          )}
 
          {user?.role === "AGENT" && (
            <>
              <span onClick={() => setPage("AgentDashboard")} className={linkStyle("AgentDashboard")}>
                <LayoutDashboard size={18}/> Dashboard
              </span>
              <span onClick={() => setPage("GestionSinistres")} className={linkStyle("GestionSinistres")}>
                <FileText size={18}/> Sinistres
              </span>
              <span onClick={() => setPage("Profil")} className={linkStyle("Profil")}>
                <User size={18}/> Profil
              </span>
            </>
          )}
 
          {user?.role === "EXPERT" && (
            <>
              <span onClick={() => setPage("ExpertDashboard")} className={linkStyle("ExpertDashboard")}>
                <LayoutDashboard size={18}/> Dashboard
              </span>
              <span onClick={() => setPage("MissionsExpert")} className={linkStyle("MissionsExpert")}>
                <ClipboardList size={18}/> Missions
              </span>
              <span onClick={() => setPage("Profil")} className={linkStyle("Profil")}>
                <User size={18}/> Profil
              </span>
            </>
          )}
 
          {user?.role === "SUPERVISEUR" && (
            <>
              <span onClick={() => setPage("DossiersSuperviseur")} className={linkStyle("DossiersSuperviseur")}>
                <ShieldCheck size={18}/> Dossiers
              </span>
              <span onClick={() => setPage("Profil")} className={linkStyle("Profil")}>
                <User size={18}/> Profil
              </span>
            </>
          )}
        </div>
 
        <div className="flex items-center gap-4">
          {!user ? (
            <button onClick={() => setPage("Login")}
              className="px-6 py-2.5 bg-cyan-600 hover:bg-cyan-500 text-white rounded-xl font-bold transition-all shadow-lg shadow-cyan-900/30 active:scale-95">
              Se connecter
            </button>
          ) : (
            <div className="flex items-center gap-4 border-l border-white/10 pl-5">
 
              <div className="relative" ref={notifRef}>
                <button onClick={handleOpenNotif}
                  className={`p-2.5 rounded-xl transition-all ${notifOpen ? "bg-white/10" : "bg-white/5 hover:bg-white/10"}`}>
                  <Bell size={20} className={count > 0 ? "text-cyan-400" : "text-slate-400"} />
                  {count > 0 && (
                    <span className="absolute -top-1 -right-1 w-5 h-5 bg-orange-500 rounded-full text-[10px] font-black flex items-center justify-center text-white border-2 border-[#060c1c] animate-bounce">
                      {count > 9 ? "9+" : count}
                    </span>
                  )}
                </button>
 
                {notifOpen && (
                  <div className="absolute right-0 mt-4 w-96 bg-[#0f172a] border border-white/10 rounded-2xl shadow-2xl z-50 overflow-hidden ring-1 ring-black">
                    <div className="flex items-center justify-between px-5 py-4 bg-white/5 border-b border-white/5">
                      <h3 className="font-bold text-white text-sm tracking-wide">NOTIFICATIONS</h3>
                      <div className="flex gap-4 items-center">
                        {count > 0 && (
                          <button onClick={marquerToutesLues}
                            className="text-[10px] text-cyan-400 hover:text-cyan-300 font-bold flex items-center gap-1">
                            <CheckCheck size={12}/> TOUT LIRE
                          </button>
                        )}
                        <X size={16} className="text-slate-500 cursor-pointer hover:text-white"
                          onClick={() => setNotifOpen(false)} />
                      </div>
                    </div>
                    <div className="max-h-[450px] overflow-y-auto">
                      {notifications.length === 0 ? (
                        <div className="py-12 text-center">
                          <Bell size={32} className="text-slate-800 mx-auto mb-3 opacity-20" />
                          <p className="text-slate-500 text-sm">Rien à signaler pour le moment</p>
                        </div>
                      ) : notifications.map((n) => (
                        <div key={n.id}
                          className={`flex gap-4 px-5 py-4 border-b border-white/5 transition-all hover:bg-white/5 ${!n.lue ? "bg-cyan-500/5" : "opacity-60"}`}>
                          <div className={`mt-1 w-9 h-9 rounded-full flex items-center justify-center shrink-0 border border-white/5 ${getTypeStyle(n.type)}`}>
                            {getTypeIcon(n.type)}
                          </div>
                          <div className="flex-1 min-w-0">
                            <p className={`text-xs leading-relaxed ${!n.lue ? "text-slate-100 font-semibold" : "text-slate-400"}`}>
                              {n.message}
                            </p>
                            <span className="text-[10px] text-slate-600 font-medium block mt-1.5">
                              {formatDate(n.dateCreation)}
                            </span>
                          </div>
                          {!n.lue && (
                            <button onClick={() => marquerLue(n.id)}
                              className="p-2 hover:bg-cyan-400/20 rounded-lg text-cyan-400 transition-colors shrink-0">
                              <Check size={14} />
                            </button>
                          )}
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
 
              <div className="hidden sm:flex flex-col items-end">
                <span className="text-xs font-bold text-white tracking-wide uppercase">{user.prenom}</span>
                <span className={`text-[9px] px-1.5 py-0.5 rounded font-black mt-0.5 ${roleBadgeStyle()}`}>
                  {user.role}
                </span>
              </div>
 
              <button onClick={onLogout}
                className="p-2.5 bg-red-500/5 hover:bg-red-500/20 text-red-400 rounded-xl transition-all border border-red-500/10 active:scale-90"
                title="Déconnexion">
                <LogOut size={20} />
              </button>
            </div>
          )}
        </div>
      </div>
    </nav>
  );
}
 
function getTypeStyle(type) {
  const map = {
    DECISION:            "bg-emerald-500/10 text-emerald-400",
    EXPERTISE:           "bg-purple-500/10  text-purple-400",
    CLOTURE:             "bg-slate-500/10   text-slate-400",
    STATUT_CHANGE:       "bg-blue-500/10    text-blue-400",
    NOUVELLE_DECLARATION:"bg-orange-500/10  text-orange-400",
  };
  return map[type] || "bg-white/5 text-slate-300";
}
 
function getTypeIcon(type) {
  const size = 14;
  switch (type) {
    case "DECISION":            return <CheckCircle size={size} />;
    case "EXPERTISE":           return <Search size={size} />;
    case "CLOTURE":             return <Lock size={size} />;
    case "STATUT_CHANGE":       return <RefreshCw size={size} />;
    case "NOUVELLE_DECLARATION":return <FilePlus size={size} />;
    default:                    return <Bell size={size} />;
  }
}
 
function formatDate(date) {
  if (!date) return "";
  const d    = new Date(date);
  const diff = Math.floor((Date.now() - d) / 1000);
  if (diff < 60)    return "À l'instant";
  if (diff < 3600)  return `Il y a ${Math.floor(diff / 60)}m`;
  if (diff < 86400) return `Il y a ${Math.floor(diff / 3600)}h`;
  return d.toLocaleDateString("fr-FR", { day: "2-digit", month: "short", hour: "2-digit", minute: "2-digit" });
}