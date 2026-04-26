import { useState, useEffect, useCallback } from "react";
import { sinistreService } from "../services/sinistreService";
import {
  Loader2, AlertCircle, ClipboardList, MapPin,
  Calendar, ChevronRight, Search, ArrowLeft,
  Clock, CheckCircle, Zap, Filter
} from "lucide-react";
 
const fmt = (d) =>
  d ? new Date(d).toLocaleDateString("fr-FR", { day: "2-digit", month: "short", year: "numeric" }) : "—";
 
const STATUT_META = {
  AFFECTE:      { label: "En attente",   color: "text-blue-400",    bg: "bg-blue-500/10",    dot: "bg-blue-400"    },
  EN_EXPERTISE: { label: "En cours",     color: "text-purple-400",  bg: "bg-purple-500/10",  dot: "bg-purple-400"  },
  EVALUE:       { label: "Évalué",       color: "text-yellow-400",  bg: "bg-yellow-500/10",  dot: "bg-yellow-400"  },
  APPROUVE:     { label: "Approuvé",     color: "text-emerald-400", bg: "bg-emerald-500/10", dot: "bg-emerald-400" },
  REJETE:       { label: "Rejeté",       color: "text-red-400",     bg: "bg-red-500/10",     dot: "bg-red-400"     },
  CLOTURE:      { label: "Clôturé",      color: "text-slate-400",   bg: "bg-slate-500/10",   dot: "bg-slate-400"   },
};
 
function StatusBadge({ statut }) {
  const m = STATUT_META[statut] || { label: statut, color: "text-white", bg: "bg-white/10", dot: "bg-white" };
  return (
    <span className={`inline-flex items-center gap-1.5 px-2.5 py-1 rounded-full text-[10px] font-black uppercase tracking-widest ${m.color} ${m.bg}`}>
      <span className={`w-1.5 h-1.5 rounded-full ${m.dot}`} />
      {m.label}
    </span>
  );
}
 
function StatCard({ icon: Icon, label, value, color, bg }) {
  return (
    <div className={`flex items-center gap-4 p-5 rounded-2xl bg-[#111827]/60 border ${bg}`}>
      <div className={`p-3 rounded-xl`}>
        <Icon size={20} className={color} />
      </div>
      <div>
        <p className="text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-0.5">{label}</p>
        <p className={`text-2xl font-black ${color}`}>{String(value).padStart(2, "0")}</p>
      </div>
    </div>
  );
}
 
export function MissionsExpert({ setPage, setSelectedSinistreId }) {
  const [missions, setMissions] = useState([]);
  const [filtered, setFiltered] = useState([]);
  const [loading,  setLoading]  = useState(true);
  const [error,    setError]    = useState(null);
  const [search,   setSearch]   = useState("");
  const [statut,   setStatut]   = useState("TOUS");
 
  const load = useCallback(async () => {
    setLoading(true); setError(null);
    try {
      const data = await sinistreService.getMissionsExpert();
      const sorted = (data || []).sort(
        (a, b) => new Date(b.dateIncident) - new Date(a.dateIncident)
      );
      setMissions(sorted);
      setFiltered(sorted);
    } catch {
      setError("Impossible de charger vos missions.");
    } finally {
      setLoading(false);
    }
  }, []);
 
  useEffect(() => { load(); }, [load]);
 
  useEffect(() => {
    let result = missions;
    if (statut !== "TOUS") result = result.filter(m => m.statut === statut);
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(m =>
        m.id?.toString().includes(q) ||
        m.typeSinistre?.toLowerCase().includes(q) ||
        m.lieuIncident?.toLowerCase().includes(q)
      );
    }
    setFiltered(result);
  }, [search, statut, missions]);
 
  const count = (s) => missions.filter(m => m.statut === s).length;
 
  const FILTERS = [
    { key: "TOUS",         label: "Toutes",     count: missions.length },
    { key: "AFFECTE",      label: "En attente", count: count("AFFECTE")      },
    { key: "EN_EXPERTISE", label: "En cours",   count: count("EN_EXPERTISE") },
    { key: "EVALUE",       label: "Évalués",    count: count("EVALUE")       },
    { key: "CLOTURE",      label: "Clôturés",   count: count("CLOTURE")      },
  ];
 
  if (loading) return (
    <div className="min-h-[60vh] flex items-center justify-center">
      <div className="flex flex-col items-center gap-4">
        <Loader2 size={36} className="text-purple-400 animate-spin" />
        <p className="text-slate-500 text-sm uppercase tracking-widest font-bold">Chargement des missions…</p>
      </div>
    </div>
  );
 
  if (error) return (
    <div className="min-h-[60vh] flex items-center justify-center">
      <div className="text-center">
        <AlertCircle size={40} className="text-red-400 mx-auto mb-3" />
        <p className="text-slate-400 mb-4">{error}</p>
        <button onClick={load} className="px-5 py-2.5 bg-white/5 hover:bg-white/10 rounded-xl text-sm text-white transition-all">Réessayer</button>
      </div>
    </div>
  );
 
  return (
    <div className="min-h-screen bg-[#0a0f1a] text-white p-8">
      <div className="max-w-6xl mx-auto">
 
        {/* Header */}
        <div className="flex items-center gap-4 mb-10">
         
          <div className="flex-1">
            <p className="text-purple-400 text-xs font-bold uppercase tracking-widest mb-1">Espace expert</p>
            <h1 className="text-4xl font-extrabold">Mes missions</h1>
          </div>
          <div className="bg-slate-900/50 border border-white/5 px-6 py-3 rounded-2xl text-center">
            <p className="text-slate-500 text-[10px] uppercase font-bold mb-1">Total</p>
            <p className="text-3xl font-black text-purple-400">
              {String(missions.length).padStart(2, "0")}
            </p>
          </div>
        </div>
 
        {/* Stats */}
        <div className="grid grid-cols-4 gap-4 mb-8">
          <StatCard icon={Clock}       label="En attente" value={count("AFFECTE")}      color="text-blue-400"    bg="border-blue-500/10"    />
          <StatCard icon={Zap}         label="En cours"   value={count("EN_EXPERTISE")} color="text-purple-400"  bg="border-purple-500/10"  />
          <StatCard icon={Filter}      label="Évalués"    value={count("EVALUE")}       color="text-yellow-400"  bg="border-yellow-500/10"  />
          <StatCard icon={CheckCircle} label="Clôturés"   value={count("CLOTURE")}      color="text-emerald-400" bg="border-emerald-500/10" />
        </div>
 
        {/* Search */}
        <div className="flex items-center gap-3 mb-5 bg-white/5 border border-white/10 rounded-2xl px-4 py-3 focus-within:border-purple-500/50 transition-all">
          <Search size={16} className="text-slate-500 shrink-0" />
          <input type="text" placeholder="Rechercher par ID, type, lieu…" value={search}
            onChange={e => setSearch(e.target.value)}
            className="bg-transparent flex-1 text-sm text-white placeholder-slate-600 outline-none" />
          {search && (
            <button onClick={() => setSearch("")} className="text-slate-500 hover:text-white text-xs transition-colors">✕</button>
          )}
        </div>
 
        <div className="flex flex-wrap gap-2 mb-8">
          {FILTERS.map(f => (
            <button key={f.key} onClick={() => setStatut(f.key)}
              className={`flex items-center gap-2 px-4 py-2 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all
                ${statut === f.key
                  ? "bg-purple-500 text-black"
                  : "bg-white/5 text-slate-400 hover:bg-white/10"}`}>
              {f.label}
              <span className={`px-1.5 py-0.5 rounded-full text-[9px] font-black
                ${statut === f.key ? "bg-black/20" : "bg-white/10 text-slate-500"}`}>
                {f.count}
              </span>
            </button>
          ))}
        </div>
 
        {filtered.length > 0 && (
          <div className="grid grid-cols-5 px-6 mb-3 text-[10px] uppercase tracking-widest font-bold text-slate-500">
            <div>Référence</div>
            <div>Type d'incident</div>
            <div>Localisation</div>
            <div>Date incident</div>
            <div className="text-right">Statut / Action</div>
          </div>
        )}
 
        {filtered.length === 0 ? (
          <div className="text-center py-24 bg-slate-900/20 border-2 border-dashed border-white/5 rounded-3xl">
            <ClipboardList size={36} className="text-slate-700 mx-auto mb-3" />
            <p className="text-slate-500 text-sm">
              {search || statut !== "TOUS"
                ? "Aucune mission ne correspond à votre recherche."
                : "Aucune mission assignée pour le moment."}
            </p>
          </div>
        ) : (
          <div className="space-y-3">
            {filtered.map(mission => {
              const canAct = mission.statut === "AFFECTE" || mission.statut === "EN_EXPERTISE";
              return (
                <div key={mission.id}
                  onClick={() => {
                    setSelectedSinistreId(Number(mission.id));
                    setPage("DetailSinistreExpert");
                  }}
                  className={`grid grid-cols-5 items-center p-5 border rounded-2xl cursor-pointer transition-all group
                    ${canAct
                      ? "bg-purple-500/5 border-purple-500/20 hover:bg-purple-500/10 hover:border-purple-500/40"
                      : "bg-[#111827]/50 border-white/5 hover:bg-[#111827]/80 hover:border-white/10"}`}>
 
                  <div>
                    <p className="font-bold text-sm text-slate-200 font-mono">#SP-{mission.id}</p>
                    <p className="text-[10px] text-slate-600 mt-0.5">{fmt(mission.dateDeclaration || mission.dateCreation)}</p>
                  </div>
 
                  <div className="flex items-center gap-2">
                    <div className={`p-1.5 rounded-lg ${canAct ? "bg-purple-500/10" : "bg-slate-800"}`}>
                      <AlertCircle size={14} className={canAct ? "text-purple-400" : "text-slate-500"} />
                    </div>
                    <p className="text-sm font-semibold text-slate-300 truncate">
                      {mission.typeSinistre?.replace(/_/g, " ")}
                    </p>
                  </div>
 
                  <div className="flex items-center gap-2">
                    <MapPin size={13} className="text-slate-600 shrink-0" />
                    <p className="text-xs text-slate-400 truncate">{mission.lieuIncident || "—"}</p>
                  </div>
 
                  <div className="flex items-center gap-2">
                    <Calendar size={13} className="text-slate-600 shrink-0" />
                    <p className="text-xs text-slate-400">{fmt(mission.dateIncident)}</p>
                  </div>
 
                  <div className="flex items-center justify-end gap-3">
                    <StatusBadge statut={mission.statut} />
                    <ChevronRight size={16} className={`transition-colors ${canAct ? "text-purple-400" : "text-slate-600 group-hover:text-white"}`} />
                  </div>
                </div>
              );
            })}
          </div>
        )}
 
        {filtered.length > 0 && (
          <p className="text-center text-[10px] text-slate-600 uppercase tracking-widest mt-6">
            {filtered.length} mission{filtered.length > 1 ? "s" : ""} affichée{filtered.length > 1 ? "s" : ""}
            {statut !== "TOUS" || search ? ` sur ${missions.length} au total` : ""}
          </p>
        )}
      </div>
    </div>
  );
}