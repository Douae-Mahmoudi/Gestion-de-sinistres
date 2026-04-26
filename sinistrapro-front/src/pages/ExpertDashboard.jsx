import React, { useState, useEffect, useCallback } from "react";
import { sinistreService } from "../services/sinistreService";
import {
  Clock, CheckCircle2, ClipboardList, ChevronRight,
  Loader2, MapPin, AlertCircle
} from "lucide-react";

export function ExpertDashboard({ user, setPage, setSelectedSinistreId }) {
  const [stats,    setStats]    = useState({ missionsAttente: 0, expertisesEnCours: 0, dossiersClotures: 0 });
  const [missions, setMissions] = useState([]);
  const [loading,  setLoading]  = useState(true);

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      const [statsData, missionsData] = await Promise.all([
        sinistreService.getResumeStatsParRole(),
        sinistreService.getMissionsExpert(),
      ]);

      setStats({
        missionsAttente:   statsData.missionsAttente   || 0,
        expertisesEnCours: statsData.expertisesEnCours || 0,
        dossiersClotures:  statsData.dossiersClotures  || 0,
      });
      setMissions(missionsData);
    } catch (err) {
      console.error("Erreur ExpertDashboard:", err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchData(); }, [fetchData]);

  if (loading) return (
    <div className="flex flex-col items-center justify-center h-64 text-slate-500">
      <Loader2 className="animate-spin mb-2" size={32} />
      <p>Chargement de vos missions...</p>
    </div>
  );

  return (
    <div className="max-w-6xl mx-auto space-y-8 animate-in fade-in duration-700">

      <div className="flex flex-col md:flex-row md:items-center justify-between gap-6">
        <div>
          <h1 className="text-4xl font-bold text-white tracking-tight">
            Espace Expert,{" "}
            <span className="text-purple-400">{user?.prenom}</span> 
          </h1>
          <p className="text-slate-400 mt-2 italic">
            Gérez vos missions d'expertise assignées.
          </p>
        </div>
        <button
          onClick={() => setPage("MissionsExpert")}
          className="flex items-center justify-center gap-2 px-6 py-4 bg-purple-600 hover:bg-purple-500 text-white font-bold rounded-2xl transition-all hover:scale-105 active:scale-95 shadow-lg shadow-purple-900/20"
        >
          <ClipboardList size={20} />
          Voir toutes mes missions
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
        <StatCard
          title="Missions en attente"
          value={stats.missionsAttente}
          icon={<Clock className="text-orange-400" />}
          color="bg-orange-500/5"
          border="border-orange-500/20"
          textColor="text-orange-400"
        />
        <StatCard
          title="Expertises en cours"
          value={stats.expertisesEnCours}
          icon={<ClipboardList className="text-purple-400" />}
          color="bg-purple-500/5"
          border="border-purple-500/20"
          textColor="text-purple-400"
        />
        <StatCard
          title="Dossiers clôturés"
          value={stats.dossiersClotures}
          icon={<CheckCircle2 className="text-emerald-400" />}
          color="bg-emerald-500/5"
          border="border-emerald-500/20"
          textColor="text-emerald-400"
        />
      </div>

      <div className="bg-slate-900/30 border border-white/5 rounded-3xl p-6">
        <div className="flex justify-between items-center mb-6">
          <div>
            <p className="text-[10px] uppercase tracking-widest text-purple-400 font-bold mb-1">
              Prioritaires
            </p>
            <h3 className="text-xl font-bold text-white">Mes missions assignées</h3>
          </div>
          <button
            onClick={() => setPage("MissionsExpert")}
            className="text-xs text-slate-500 hover:text-white transition-colors flex items-center gap-1 uppercase tracking-widest font-bold"
          >
            Voir tout <ChevronRight size={14} />
          </button>
        </div>

        <div className="grid grid-cols-5 px-4 mb-4 text-[10px] uppercase tracking-widest font-bold text-slate-500">
          <div>Référence</div>
          <div>Assuré</div>
          <div>Type</div>
          <div>Localisation</div>
          <div className="text-right">Action</div>
        </div>

        <div className="space-y-3">
          {missions.length === 0 ? (
            <div className="text-center py-12 border-2 border-dashed border-white/5 rounded-2xl">
              <ClipboardList size={28} className="text-slate-700 mx-auto mb-2" />
              <p className="text-slate-500 text-sm">Aucune mission assignée.</p>
            </div>
          ) : (
            missions.slice(0, 5).map((m) => (
              <div
                key={m.id}
                className="grid grid-cols-5 items-center p-4 bg-white/5 hover:bg-white/10 border border-transparent hover:border-white/10 rounded-2xl transition-all group cursor-pointer"
                onClick={() => {
                  setSelectedSinistreId(Number(m.id));
                  setPage("DetailSinistreExpert");
                }}
              >
                <div>
                  <p className="text-[10px] text-slate-500 font-mono">#SP-{m.id}</p>
                  <p className="text-[10px] text-slate-600">
                    {m.dateIncident
                      ? new Date(m.dateIncident).toLocaleDateString('fr-FR')
                      : '—'}
                  </p>
                </div>

                <div>
                  <p className="text-sm font-semibold text-white truncate">
                    {m.nomAssure || m.client?.nomComplet || '—'}
                  </p>
                </div>

                <div className="flex items-center gap-2">
                  <div className="p-1.5 bg-slate-800 rounded-lg text-purple-400 shrink-0">
                    <AlertCircle size={14} />
                  </div>
                  <p className="text-xs font-medium truncate">
                    {m.typeSinistre?.replace(/_/g, ' ')}
                  </p>
                </div>

                <div className="flex items-center gap-2 text-slate-400">
                  <MapPin size={13} className="text-slate-500 shrink-0" />
                  <p className="text-xs truncate">
                    {m.lieuIncident || m.localisation || 'Non spécifié'}
                  </p>
                </div>

                <div className="flex justify-end">
                  <button className="flex items-center gap-1 px-3 py-1.5 bg-purple-500/10 hover:bg-purple-500/20 text-purple-400 rounded-lg text-[10px] font-black uppercase tracking-widest transition-all">
                    Expertiser <ChevronRight size={12} />
                  </button>
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