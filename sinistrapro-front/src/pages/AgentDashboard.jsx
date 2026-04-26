import React, { useState, useEffect, useCallback } from "react";
import { sinistreService } from "../services/sinistreService";
import {
  ClipboardList, CheckCircle2, XCircle, Archive,
  ChevronRight, Loader2, BarChart3, Users, TrendingUp
} from "lucide-react";

export function AgentDashboard({ user, setPage, setSelectedSinistreId }) {
  const [stats,           setStats]           = useState({ total: 0, enCours: 0, approuves: 0, rejetes: 0, clotures: 0 });
  const [recentSinistres, setRecentSinistres] = useState([]);
  const [parStatut,       setParStatut]       = useState({});
  const [loading,         setLoading]         = useState(true);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      const [resumeData, tousLesSinistres, parStatutData] = await Promise.all([
        sinistreService.getResumeStats(),
        sinistreService.getAllSinistres(),
        sinistreService.getParStatut(),
      ]);

      setStats(resumeData);
      setParStatut(parStatutData);

      // Les 5 derniers sinistres
      const sorted = tousLesSinistres.sort(
        (a, b) => new Date(b.dateIncident) - new Date(a.dateIncident)
      );
      setRecentSinistres(sorted.slice(0, 5));
    } catch (err) {
      console.error("Erreur AgentDashboard:", err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchData(); }, [fetchData]);

  if (loading) return (
    <div className="flex flex-col items-center justify-center h-64 text-slate-500">
      <Loader2 className="animate-spin mb-2" size={32} />
      <p>Chargement du tableau de bord...</p>
    </div>
  );

  const total = stats.total || 1;

  return (
    <div className="max-w-6xl mx-auto space-y-8 animate-in fade-in duration-700">

      {/* ── Header ── */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
          <h1 className="text-4xl font-bold text-white tracking-tight">
            Espace Agent,{" "}
            <span className="text-orange-400">{user.prenom}</span> 
          </h1>
          <p className="text-slate-400 mt-2 italic">
            Gérez et suivez l'ensemble des dossiers de sinistres.
          </p>
        </div>
       
      </div>

      <div className="grid grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Total dossiers"
          value={stats.total}
          icon={<BarChart3 className="text-white" />}
          color="bg-slate-700/50"
          border="border-white/10"
        />
        <StatCard
          title="En cours"
          value={stats.enCours}
          icon={<ClipboardList className="text-cyan-400" />}
          color="bg-cyan-500/5"
          border="border-cyan-500/20"
          textColor="text-cyan-400"
        />
        <StatCard
          title="Approuvés"
          value={stats.approuves}
          icon={<CheckCircle2 className="text-emerald-400" />}
          color="bg-emerald-500/5"
          border="border-emerald-500/20"
          textColor="text-emerald-400"
        />
        <StatCard
          title="Clôturés"
          value={stats.clotures}
          icon={<Archive className="text-slate-400" />}
          color="bg-slate-500/5"
          border="border-slate-500/20"
          textColor="text-slate-400"
        />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">

        <div className="lg:col-span-2 bg-slate-900/50 border border-white/5 rounded-3xl p-6">
          <div className="flex justify-between items-center mb-6">
            <div>
              <p className="text-[10px] uppercase tracking-widest text-orange-400 font-bold">
                Répartition
              </p>
              <h3 className="text-xl font-semibold text-white">
                Dossiers par statut
              </h3>
            </div>
            <TrendingUp size={20} className="text-slate-500" />
          </div>

          <div className="space-y-4">
            {[
              { key: 'DECLARE',      label: 'Déclarés',      color: 'bg-cyan-500' },
              { key: 'AFFECTE',      label: 'Affectés',      color: 'bg-blue-500' },
              { key: 'EN_EXPERTISE', label: 'En expertise',  color: 'bg-purple-500' },
              { key: 'EVALUE',       label: 'Évalués',       color: 'bg-yellow-500' },
              { key: 'APPROUVE',     label: 'Approuvés',     color: 'bg-emerald-500' },
              { key: 'REJETE',       label: 'Rejetés',       color: 'bg-red-500' },
              { key: 'CLOTURE',      label: 'Clôturés',      color: 'bg-slate-500' },
            ].map(({ key, label, color }) => {
              const val = parStatut[key] || 0;
              const pct = Math.round((val / total) * 100);
              return (
                <div key={key}>
                  <div className="flex justify-between items-center mb-1">
                    <span className="text-xs text-slate-400 font-medium">{label}</span>
                    <span className="text-xs font-bold text-white">{val} <span className="text-slate-500">({pct}%)</span></span>
                  </div>
                  <div className="h-2 bg-white/5 rounded-full overflow-hidden">
                    <div
                      className={`h-full ${color} rounded-full transition-all duration-700`}
                      style={{ width: `${pct}%` }}
                    />
                  </div>
                </div>
              );
            })}
          </div>
        </div>

        {/* ── Résumé rapide ── */}
        <div className="space-y-4">
          <div className="bg-slate-900/50 border border-white/5 rounded-3xl p-6">
            <p className="text-[10px] uppercase tracking-widest text-slate-500 font-bold mb-4">
              Taux de résolution
            </p>
            <div className="flex items-end gap-2">
              <p className="text-5xl font-black text-white">
                {stats.total > 0
                  ? Math.round(((stats.approuves + stats.clotures) / stats.total) * 100)
                  : 0}
                <span className="text-2xl text-emerald-400">%</span>
              </p>
            </div>
            <p className="text-xs text-slate-500 mt-2">
              Dossiers approuvés ou clôturés sur le total
            </p>
            <div className="mt-4 h-2 bg-white/5 rounded-full overflow-hidden">
              <div
                className="h-full bg-emerald-500 rounded-full transition-all duration-700"
                style={{
                  width: `${stats.total > 0
                    ? Math.round(((stats.approuves + stats.clotures) / stats.total) * 100)
                    : 0}%`
                }}
              />
            </div>
          </div>

          <div className="bg-slate-900/50 border border-white/5 rounded-3xl p-6">
            <p className="text-[10px] uppercase tracking-widest text-slate-500 font-bold mb-4">
              Dossiers rejetés
            </p>
            <div className="flex items-end gap-2">
              <p className="text-5xl font-black text-red-400">
                {stats.rejetes}
              </p>
            </div>
            <div className="mt-4 h-2 bg-white/5 rounded-full overflow-hidden">
              <div
                className="h-full bg-red-500 rounded-full"
                style={{
                  width: `${stats.total > 0
                    ? Math.round((stats.rejetes / stats.total) * 100)
                    : 0}%`
                }}
              />
            </div>
          </div>
        </div>
      </div>

      <div className="bg-slate-900/30 border border-white/5 rounded-3xl p-6">
        <div className="flex justify-between items-center mb-6">
          <h3 className="text-xl font-bold text-white">Derniers dossiers</h3>
          <button
            onClick={() => setPage("GestionSinistres")}
            className="text-xs text-slate-500 hover:text-white transition-colors flex items-center gap-1 uppercase tracking-widest font-bold"
          >
            Voir tous <ChevronRight size={14} />
          </button>
        </div>

        <div className="space-y-3">
          {recentSinistres.length === 0 ? (
            <div className="text-center py-12 border-2 border-dashed border-white/5 rounded-2xl">
              <p className="text-slate-500 text-sm">Aucun dossier trouvé.</p>
            </div>
          ) : (
            recentSinistres.map((s) => (
              <div
                key={s.id}
                onClick={() => {
                  setSelectedSinistreId(Number(s.id));
                  setPage("DetailSinistreAgent");
                }}
                className="group flex items-center justify-between p-4 bg-white/5 hover:bg-white/10 border border-transparent hover:border-white/10 rounded-2xl transition-all cursor-pointer"
              >
                <div className="flex items-center gap-4">
                  <div className="w-12 h-12 bg-slate-800 rounded-xl flex items-center justify-center text-slate-400 group-hover:text-orange-400 transition-colors">
                    <BarChart3 size={20} />
                  </div>
                  <div>
                    <h4 className="font-semibold text-white text-sm">
                      {s.typeSinistre?.replace(/_/g, ' ')}
                    </h4>
                    <p className="text-[10px] text-slate-500 font-mono">
                      REF: #SP-{s.id} • {new Date(s.dateIncident).toLocaleDateString('fr-FR')}
                      {s.clientNom && ` • ${s.clientNom}`}
                    </p>
                  </div>
                </div>
                <div className="flex items-center gap-4">
                  <span className={`text-[10px] px-3 py-1 rounded-full font-bold tracking-tighter uppercase ${getStatutStyle(s.statut)}`}>
                    • {s.statut?.replace(/_/g, ' ')}
                  </span>
                  <ChevronRight size={18} className="text-slate-700 group-hover:text-white transition-colors" />
                </div>
              </div>
            ))
          )}
        </div>
      </div>

    </div>
  );
}


function StatCard({ title, value, icon, color, border, textColor = "text-white" }) {
  return (
    <div className={`${color} border ${border} p-6 rounded-3xl flex items-center justify-between hover:border-white/20 transition-all`}>
      <div>
        <p className="text-[10px] text-slate-500 uppercase tracking-widest font-bold mb-1">{title}</p>
        <p className={`text-3xl font-black ${textColor}`}>
          {value < 10 ? `0${value}` : value}
        </p>
      </div>
      <div className="w-12 h-12 bg-white/5 rounded-2xl flex items-center justify-center">
        {icon}
      </div>
    </div>
  );
}

function getStatutStyle(statut) {
  switch (statut) {
    case 'DECLARE':      return 'bg-cyan-500/10 text-cyan-400';
    case 'AFFECTE':      return 'bg-blue-500/10 text-blue-400';
    case 'EN_EXPERTISE': return 'bg-purple-500/10 text-purple-400';
    case 'EVALUE':       return 'bg-yellow-500/10 text-yellow-400';
    case 'APPROUVE':     return 'bg-emerald-500/10 text-emerald-400';
    case 'REJETE':       return 'bg-red-500/10 text-red-400';
    case 'CLOTURE':      return 'bg-slate-500/10 text-slate-400';
    default:             return 'bg-white/5 text-white';
  }
}