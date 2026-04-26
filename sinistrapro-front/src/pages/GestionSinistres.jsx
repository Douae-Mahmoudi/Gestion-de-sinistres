import React, { useState, useEffect, useCallback } from "react";
import { sinistreService } from "../services/sinistreService";
import {
  Search, Filter, ChevronRight, Loader2,
  MapPin, AlertCircle, ArrowLeft
} from "lucide-react";

export function GestionSinistres({ setPage, setSelectedSinistreId }) {
  const [sinistres, setSinistres] = useState([]);
  const [filtered,  setFiltered]  = useState([]);
  const [loading,   setLoading]   = useState(true);
  const [search,    setSearch]    = useState("");
  const [statut,    setStatut]    = useState("TOUS");

  const fetchData = useCallback(async () => {
    try {
      setLoading(true);
      const data = await sinistreService.getAllSinistres();
      const sorted = data.sort(
        (a, b) => new Date(b.dateIncident) - new Date(a.dateIncident)
      );
      setSinistres(sorted);
      setFiltered(sorted);
    } catch (err) {
      console.error("Erreur GestionSinistres:", err);
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => { fetchData(); }, [fetchData]);

  useEffect(() => {
    let result = sinistres;

    if (statut !== "TOUS") {
      result = result.filter(s => s.statut === statut);
    }

    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(s =>
        s.id?.toString().includes(q) ||
        s.typeSinistre?.toLowerCase().includes(q) ||
        s.lieuIncident?.toLowerCase().includes(q) ||
        s.clientNom?.toLowerCase().includes(q) ||
        s.clientEmail?.toLowerCase().includes(q)
      );
    }

    setFiltered(result);
  }, [search, statut, sinistres]);

  const statuts = [
    { key: "TOUS",         label: "Tous",         count: sinistres.length },
    { key: "DECLARE",      label: "Déclarés",     count: sinistres.filter(s => s.statut === "DECLARE").length },
    { key: "AFFECTE",      label: "Affectés",     count: sinistres.filter(s => s.statut === "AFFECTE").length },
    { key: "EN_EXPERTISE", label: "En expertise", count: sinistres.filter(s => s.statut === "EN_EXPERTISE").length },
    { key: "EVALUE",       label: "Évalués",      count: sinistres.filter(s => s.statut === "EVALUE").length },
    { key: "APPROUVE",     label: "Approuvés",    count: sinistres.filter(s => s.statut === "APPROUVE").length },
    { key: "REJETE",       label: "Rejetés",      count: sinistres.filter(s => s.statut === "REJETE").length },
    { key: "CLOTURE",      label: "Clôturés",     count: sinistres.filter(s => s.statut === "CLOTURE").length },
  ];

  if (loading) return (
    <div className="flex justify-center items-center h-64">
      <Loader2 className="animate-spin text-orange-400" size={40} />
    </div>
  );

  return (
    <div className="min-h-screen bg-[#0a0f1a] text-white p-8">
      <div className="max-w-7xl mx-auto">

        <div className="flex items-center gap-4 mb-10">
        
          <div className="flex-1">
            <p className="text-orange-400 text-xs font-bold uppercase tracking-widest mb-1">
              Gestion des dossiers
            </p>
            <h1 className="text-4xl font-extrabold">Tous les sinistres</h1>
          </div>
          <div className="bg-slate-900/50 border border-white/5 px-6 py-3 rounded-2xl text-center">
            <p className="text-slate-500 text-[10px] uppercase font-bold mb-1">Total</p>
            <p className="text-3xl font-black text-orange-400">
              {sinistres.length < 10 ? `0${sinistres.length}` : sinistres.length}
            </p>
          </div>
        </div>

        <div className="flex items-center gap-3 mb-6 bg-white/5 border border-white/10 rounded-2xl px-4 py-3 focus-within:border-orange-500/50 transition-all">
          <Search size={18} className="text-slate-500 shrink-0" />
          <input
            type="text"
            placeholder="Rechercher par ID, type, lieu, client..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            className="bg-transparent flex-1 text-sm text-white placeholder-slate-600 outline-none"
          />
          {search && (
            <button
              onClick={() => setSearch("")}
              className="text-slate-500 hover:text-white transition-colors text-xs"
            >
              ✕
            </button>
          )}
        </div>

        <div className="flex flex-wrap gap-2 mb-8">
          {statuts.map(s => (
            <button
              key={s.key}
              onClick={() => setStatut(s.key)}
              className={`flex items-center gap-2 px-4 py-2 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all
                ${statut === s.key
                  ? 'bg-orange-500 text-black'
                  : 'bg-white/5 text-slate-400 hover:bg-white/10'}`}
            >
              {s.label}
              <span className={`px-1.5 py-0.5 rounded-full text-[9px] font-black
                ${statut === s.key ? 'bg-black/20 text-black' : 'bg-white/10 text-slate-500'}`}>
                {s.count}
              </span>
            </button>
          ))}
        </div>

        <div className="grid grid-cols-6 px-6 mb-4 text-[10px] uppercase tracking-widest font-bold text-slate-500">
          <div>Référence</div>
          <div>Type d'incident</div>
          <div>Client</div>
          <div>Localisation</div>
          <div>État</div>
          <div className="text-right">Actions</div>
        </div>

        <div className="space-y-3">
          {filtered.length === 0 ? (
            <div className="text-center py-20 bg-slate-900/20 border-2 border-dashed border-white/5 rounded-3xl">
              <Filter size={32} className="text-slate-700 mx-auto mb-3" />
              <p className="text-slate-500">
                {search || statut !== "TOUS"
                  ? "Aucun dossier ne correspond à votre recherche."
                  : "Aucun dossier trouvé."}
              </p>
            </div>
          ) : (
            filtered.map((s) => (
              <div
                key={s.id}
                className="grid grid-cols-6 items-center p-5 bg-[#111827]/50 border border-white/5 rounded-2xl hover:bg-[#111827]/80 hover:border-white/10 transition-all group cursor-pointer"
                onClick={() => {
                  setSelectedSinistreId(Number(s.id));
                  setPage("DetailSinistreAgent");
                }}
              >
                <div>
                  <p className="font-bold text-sm text-slate-200">
                    {new Date(s.dateIncident).toLocaleDateString('fr-FR', {
                      day: '2-digit', month: 'short', year: 'numeric'
                    })}
                  </p>
                  <p className="text-[10px] text-slate-500 font-mono">
                    RÉF: #SP-{s.id}
                  </p>
                </div>

                <div className="flex items-center gap-3">
                  <div className="p-2 bg-slate-800 rounded-lg text-orange-400 shrink-0">
                    <AlertCircle size={16} />
                  </div>
                  <p className="font-semibold text-sm truncate">
                    {s.typeSinistre?.replace(/_/g, ' ')}
                  </p>
                </div>

                <div>
                  <p className="text-sm text-slate-300 font-medium truncate">
                    {s.clientNom || s.clientPrenom
                      ? `${s.clientPrenom || ''} ${s.clientNom || ''}`.trim()
                      : '—'}
                  </p>
                  <p className="text-[10px] text-slate-600 truncate">
                    {s.clientEmail || ''}
                  </p>
                </div>

                {/* Localisation */}
                <div className="flex items-center gap-2 text-slate-400">
                  <MapPin size={13} className="text-slate-500 shrink-0" />
                  <p className="text-xs truncate">
                    {s.lieuIncident || "Non spécifié"}
                  </p>
                </div>

                {/* Statut */}
                <div>
                  <span className={`text-[10px] px-3 py-1 rounded-full font-bold uppercase tracking-tighter ${getStatutStyle(s.statut)}`}>
                    • {s.statut?.replace(/_/g, ' ')}
                  </span>
                </div>

                {/* Action */}
                <div className="flex justify-end">
                  <button className="flex items-center gap-1 text-[10px] font-black uppercase tracking-widest text-slate-500 group-hover:text-orange-400 transition-colors">
                    Voir <ChevronRight size={14} />
                  </button>
                </div>
              </div>
            ))
          )}
        </div>

        {filtered.length > 0 && (
          <p className="text-center text-[10px] text-slate-600 uppercase tracking-widest mt-6">
            {filtered.length} dossier{filtered.length > 1 ? 's' : ''} affiché{filtered.length > 1 ? 's' : ''}
            {statut !== "TOUS" || search ? ` sur ${sinistres.length} total` : ''}
          </p>
        )}
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