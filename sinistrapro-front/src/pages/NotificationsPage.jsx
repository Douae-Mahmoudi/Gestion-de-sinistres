import React, { useState, useEffect } from 'react';
import { sinistreService } from '../services/sinistreService';
import { Bell, CheckCheck, Loader2, ArrowLeft, CheckCircle, Search, Lock, ClipboardList, FilePlus } from 'lucide-react';
 
export function NotificationsPage({ setPage, backPage = "ClientDashboard" }) {
  const [notifications, setNotifications] = useState([]);
  const [loading, setLoading] = useState(true);
  const [filter, setFilter] = useState('TOUTES'); // TOUTES | NON_LUES
 
  useEffect(() => {
    const load = async () => {
      try {
        const data = await sinistreService.notifications.getAll();
        setNotifications(data);
      } catch (err) {
        console.error("Erreur chargement notifications:", err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);
 
  const marquerLue = async (id) => {
    try {
      await sinistreService.notifications.marquerLue(id);
      setNotifications(prev => prev.map(n => n.id === id ? { ...n, lue: true } : n));
    } catch (err) {
      console.error("Erreur lors du marquage comme lu:", err);
    }
  };
 
  const marquerToutesLues = async () => {
    try {
      await sinistreService.notifications.marquerToutesLues();
      setNotifications(prev => prev.map(n => ({ ...n, lue: true })));
    } catch (err) {
      console.error("Erreur marquage global:", err);
    }
  };
 
  const filtered = filter === 'NON_LUES'
    ? notifications.filter(n => !n.lue)
    : notifications;
 
  const nonLuesCount = notifications.filter(n => !n.lue).length;
 
  if (loading) return (
    <div className="flex justify-center items-center h-64">
      <Loader2 className="animate-spin text-cyan-500" size={40} />
    </div>
  );
 
  return (
    <div className="min-h-screen bg-[#0a0f1a] text-white p-8">
      <div className="max-w-3xl mx-auto">
 
        {/* Header */}
        <div className="flex items-start justify-between mb-10">
          <div className="flex items-center gap-4">
            <button
              onClick={() => setPage(backPage)}
              className="p-2 bg-white/5 hover:bg-white/10 rounded-xl transition-all"
              title="Retour"
            >
              <ArrowLeft size={20} />
            </button>
            <div>
              <p className="text-cyan-500 text-xs font-bold uppercase tracking-widest mb-1">
                Centre de messages
              </p>
              <h1 className="text-4xl font-extrabold">Notifications</h1>
            </div>
          </div>
 
          <div className="flex flex-col items-end gap-3">
            <div className="bg-slate-900/50 border border-white/5 px-5 py-3 rounded-2xl text-center">
              <p className="text-slate-500 text-[10px] uppercase font-bold mb-1">Non lues</p>
              <p className="text-3xl font-black text-orange-400">
                {nonLuesCount < 10 ? `0${nonLuesCount}` : nonLuesCount}
              </p>
            </div>
            {nonLuesCount > 0 && (
              <button
                onClick={marquerToutesLues}
                className="flex items-center gap-2 text-[10px] text-cyan-400 hover:text-cyan-300 font-bold uppercase tracking-widest transition-colors"
              >
                <CheckCheck size={12} /> Tout marquer comme lu
              </button>
            )}
          </div>
        </div>
 
        {/* Filtres */}
        <div className="flex gap-2 mb-6">
          {['TOUTES', 'NON_LUES'].map(f => (
            <button
              key={f}
              onClick={() => setFilter(f)}
              className={`px-4 py-2 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all
                ${filter === f
                  ? 'bg-cyan-500 text-black'
                  : 'bg-white/5 text-slate-400 hover:bg-white/10'}`}
            >
              {f === 'TOUTES' ? `Toutes (${notifications.length})` : `Non lues (${nonLuesCount})`}
            </button>
          ))}
        </div>
 
        <div className="space-y-3">
          {filtered.length === 0 ? (
            <div className="text-center py-20 bg-slate-900/20 border-2 border-dashed border-white/5 rounded-3xl">
              <Bell size={32} className="text-slate-700 mx-auto mb-3" />
              <p className="text-slate-500">
                {filter === 'NON_LUES' ? 'Aucune notification non lue.' : 'Aucune notification.'}
              </p>
            </div>
          ) : (
            filtered.map((n) => (
              <div
                key={n.id}
                className={`flex items-start gap-4 p-5 rounded-2xl border transition-all
                  ${!n.lue
                    ? 'bg-cyan-500/5 border-cyan-500/10 hover:bg-cyan-500/10'
                    : 'bg-[#111827]/50 border-white/5 hover:bg-[#111827]/80'}`}
              >
                <div className={`mt-0.5 w-10 h-10 rounded-full flex items-center justify-center shrink-0
                  ${getTypeStyle(n.type)}`}>
                  {getTypeIcon(n.type)}
                </div>
 
                <div className="flex-1 min-w-0">
                  <div className="flex items-start justify-between gap-4">
                    <p className={`text-sm leading-relaxed ${!n.lue ? 'text-white font-medium' : 'text-slate-400'}`}>
                      {n.message}
                    </p>
                    {!n.lue && (
                      <span className="shrink-0 w-2 h-2 bg-cyan-400 rounded-full mt-1.5" />
                    )}
                  </div>
                  <div className="flex items-center justify-between mt-2">
                    <div className="flex items-center gap-3">
                      <span className={`text-[10px] px-2 py-0.5 rounded-full font-bold uppercase ${getTypeStyle(n.type)}`}>
                        {n.type?.replace(/_/g, ' ')}
                      </span>
                      <span className="text-[10px] text-slate-600">
                        {formatDate(n.dateCreation)}
                      </span>
                    </div>
                    {!n.lue && (
                      <button
                        onClick={() => marquerLue(n.id)}
                        className="text-[10px] text-slate-500 hover:text-cyan-400 font-bold uppercase tracking-widest transition-colors"
                      >
                        Marquer comme lue
                      </button>
                    )}
                  </div>
                </div>
              </div>
            ))
          )}
        </div>
 
      </div>
    </div>
  );
}
 
function getTypeStyle(type) {
  switch (type) {
    case 'DECISION':           return 'bg-emerald-500/10 text-emerald-400';
    case 'EXPERTISE':          return 'bg-purple-500/10 text-purple-400';
    case 'CLOTURE':            return 'bg-slate-500/10 text-slate-400';
    case 'STATUT_CHANGE':      return 'bg-cyan-500/10 text-cyan-400';
    case 'NOUVELLE_DECLARATION': return 'bg-orange-500/10 text-orange-400';
    default:                   return 'bg-white/5 text-white';
  }
}
 
function getTypeIcon(type) {
  const size = 16;
  switch (type) {
    case 'DECISION':           return <CheckCircle size={size} />;
    case 'EXPERTISE':          return <Search size={size} />;
    case 'CLOTURE':            return <Lock size={size} />;
    case 'STATUT_CHANGE':      return <ClipboardList size={size} />;
    case 'NOUVELLE_DECLARATION': return <FilePlus size={size} />;
    default:                   return <Bell size={size} />;
  }
}
 
function formatDate(date) {
  if (!date) return '';
  const d    = new Date(date);
  const now  = new Date();
  const diff = Math.floor((now - d) / 1000);
  if (diff < 60)    return "À l'instant";
  if (diff < 3600)  return `Il y a ${Math.floor(diff / 60)} min`;
  if (diff < 86400) return `Il y a ${Math.floor(diff / 3600)}h`;
  return d.toLocaleDateString('fr-FR', { day: '2-digit', month: 'long', year: 'numeric' });
}