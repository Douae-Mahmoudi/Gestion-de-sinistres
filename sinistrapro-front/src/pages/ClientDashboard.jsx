import React, { useState, useEffect, useCallback } from "react";
import { sinistreService } from "../services/sinistreService";
import { DeclarerSinistreModal } from "./DeclarerSinistreModal";
import { 
  PlusCircle, 
  ClipboardList, 
  CheckCircle2, 
  ChevronRight, 
  BarChart3,
  Loader2 
} from "lucide-react";

export function ClientDashboard({ user, setPage }) {
  const [stats, setStats] = useState({ total: 0, enCours: 0, approuves: 0, rejetes: 0, clotures: 0 });
  const [recentSinistres, setRecentSinistres] = useState([]);
  const [loading, setLoading] = useState(true);
  const [isModalOpen, setIsModalOpen] = useState(false);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      const resStats = await sinistreService.getResumeStatsParRole();
      setStats(resStats || { total: 0, enCours: 0, approuves: 0, rejetes: 0, clotures: 0 });

      const data = await sinistreService.getMesSinistres();
      if (Array.isArray(data)) {
        const sorted = data.sort((a, b) => new Date(b.dateIncident) - new Date(a.dateIncident));
        setRecentSinistres(sorted.slice(0, 5));
      }
    } catch (err) {
      console.error("Erreur dashboard:", err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData]);

  if (loading && recentSinistres.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center h-64 text-slate-500">
        <Loader2 className="animate-spin mb-2" size={32} />
        <p>Chargement de votre espace...</p>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto space-y-8 animate-in fade-in duration-700">
      
      {/* Header */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
          <h1 className="text-4xl font-bold text-white tracking-tight">
            Bienvenue, <span className="text-cyan-400">{user?.prenom || 'Cher Client'}</span> 
          </h1>
          <p className="text-slate-400 mt-2 italic">
            Suivez l'état de vos dossiers de sinistres en temps réel.
          </p>
        </div>
        
        <button 
          onClick={() => setIsModalOpen(true)}
          className="flex items-center justify-center gap-2 px-8 py-4 bg-orange-600 hover:bg-orange-500 text-white font-bold rounded-2xl transition-all hover:scale-105 active:scale-95 shadow-lg shadow-orange-900/20"
        >
          <PlusCircle size={20} />
          Déclarer un nouveau sinistre
        </button>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        
        <div className="space-y-4 lg:order-1">
          <StatCard 
            title="Sinistres en cours" 
            value={stats.enCours || 0} 
            icon={<ClipboardList className="text-cyan-400" />} 
            color="cyan"
          />
          <StatCard 
            title="Dossiers Clôturés" 
            value={(stats.clotures || 0) + (stats.approuves || 0)} 
            icon={<CheckCircle2 className="text-emerald-400" />} 
            color="emerald"
          />
          <div className="p-6 bg-gradient-to-br from-slate-900 to-slate-800 border border-white/5 rounded-3xl">
             <p className="text-xs text-slate-400 mb-2 font-medium">Total des dossiers</p>
             <p className="text-4xl font-black text-white">{stats.total || 0}</p>
          </div>
        </div>

        <div className="lg:col-span-2 lg:order-2 bg-slate-900/50 border border-white/5 p-8 rounded-3xl backdrop-blur-md flex flex-col justify-between">
          <div className="flex justify-between items-center mb-8">
            <div>
              <p className="text-[10px] uppercase tracking-widest text-cyan-500 font-bold">Vue d'ensemble</p>
              <h3 className="text-2xl font-semibold text-white">Activité de vos dossiers</h3>
            </div>
            <span className="text-xs bg-cyan-500/10 px-3 py-1 rounded-full text-cyan-400 font-mono animate-pulse">LIVE MONITOR</span>
          </div>
          
          <div className="h-64 flex items-end justify-between gap-3 px-2">
            {[30, 45, 25, 60, 40, 85, 35, 55, 90, 45].map((h, i) => (
              <div 
                key={i} 
                className={`w-full rounded-t-2xl relative group transition-all duration-700 hover:bg-cyan-400/30 ${i === 8 ? 'bg-cyan-500/40' : 'bg-white/5'}`} 
                style={{ height: `${h}%` }}
              >
                <div className="absolute -top-10 left-1/2 -translate-x-1/2 opacity-0 group-hover:opacity-100 transition-opacity bg-slate-800 text-white text-[10px] py-1 px-2 rounded-lg pointer-events-none">
                  {h}%
                </div>
              </div>
            ))}
          </div>
        </div>
      </div>

      <div className="bg-slate-900/30 border border-white/5 rounded-3xl p-8">
        <div className="flex justify-between items-center mb-8">
          <h3 className="text-2xl font-bold text-white">Historique des dernières déclarations</h3>
          <button 
            onClick={() => setPage("MesSinistres")}
            className="text-xs text-cyan-400 hover:text-white transition-colors flex items-center gap-2 bg-cyan-400/5 px-4 py-2 rounded-full uppercase tracking-widest font-bold"
          >
            Voir tout <ChevronRight size={14} />
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-1 gap-4">
          {recentSinistres.map((s) => (
            <div 
              key={s.id} 
              onClick={() => setPage("MesSinistres")}
              className="group flex items-center justify-between p-5 bg-white/5 hover:bg-white/10 border border-transparent hover:border-white/10 rounded-2xl transition-all cursor-pointer"
            >
              <div className="flex items-center gap-5">
                <div className="w-14 h-14 bg-slate-800 rounded-2xl flex items-center justify-center text-slate-400 group-hover:text-cyan-400 group-hover:bg-slate-700 transition-all">
                  <BarChart3 size={24} />
                </div>
                <div>
                  <h4 className="font-bold text-white text-base">{s.typeSinistre?.replace(/_/g, ' ')}</h4>
                  <p className="text-xs text-slate-500 font-mono mt-1">
                    REFERENCE: #SP-{s.id} • LE {new Date(s.dateIncident).toLocaleDateString('fr-FR')}
                  </p>
                </div>
              </div>
              <div className="flex items-center gap-6">
                <span className={`text-[11px] px-4 py-1.5 rounded-full font-black tracking-tight uppercase shadow-sm ${getStatutStyle(s.statut)}`}>
                  {s.statut?.replace(/_/g, ' ')}
                </span>
                <ChevronRight size={20} className="text-slate-700 group-hover:text-white group-hover:translate-x-1 transition-all" />
              </div>
            </div>
          ))}

          {recentSinistres.length === 0 && !loading && (
            <div className="text-center py-20 border-2 border-dashed border-white/5 rounded-3xl">
              <p className="text-slate-500">Aucun sinistre déclaré pour le moment.</p>
            </div>
          )}
        </div>
      </div>

      <DeclarerSinistreModal 
        isOpen={isModalOpen} 
        onClose={() => setIsModalOpen(false)} 
        onSuccess={fetchData}
      />
    </div>
  );
}

function getStatutStyle(statut) {
    switch (statut) {
        case 'DECLARE':      return 'bg-cyan-500/10 text-cyan-400 border border-cyan-500/20';
        case 'AFFECTE':      return 'bg-blue-500/10 text-blue-400 border border-blue-500/20';
        case 'EN_EXPERTISE': return 'bg-purple-500/10 text-purple-400 border border-purple-500/20';
        case 'EVALUE':       return 'bg-yellow-500/10 text-yellow-400 border border-yellow-500/20';
        case 'APPROUVE':     return 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20';
        case 'REJETE':       return 'bg-red-500/10 text-red-400 border border-red-500/20';
        case 'CLOTURE':      return 'bg-slate-500/10 text-slate-400 border border-slate-500/20';
        default:             return 'bg-white/5 text-white';
    }
}

function StatCard({ title, value, icon, color }) {
  const colorMap = {
    cyan: "hover:border-cyan-500/30",
    emerald: "hover:border-emerald-500/30"
  };

  return (
    <div className={`bg-slate-900/50 border border-white/5 p-7 rounded-3xl flex items-center justify-between transition-all hover:translate-x-2 ${colorMap[color]}`}>
      <div>
        <p className="text-[11px] text-slate-500 uppercase tracking-widest font-bold mb-2">{title}</p>
        <p className="text-4xl font-black text-white">{value < 10 ? `0${value}` : value}</p>
      </div>
      <div className="w-14 h-14 bg-white/5 rounded-2xl flex items-center justify-center">
        {icon}
      </div>
    </div>
  );
}