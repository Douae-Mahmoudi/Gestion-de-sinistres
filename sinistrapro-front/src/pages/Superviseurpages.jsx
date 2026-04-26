
import { useState, useEffect, useCallback } from "react";
import { sinistreService } from "../services/sinistreService";
import axios from "axios";
import {
  Loader2, AlertCircle, ShieldCheck, Clock,
  CheckCircle, XCircle, ArrowLeft, Banknote,
  Calendar, MapPin, FileText, ClipboardList,
  ChevronRight, Search, Filter, User, Download
} from "lucide-react";
 
const api = axios.create({ baseURL: "http://localhost:8100/api" });
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("token");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
 
const fmt = (d) =>
  d ? new Date(d).toLocaleDateString("fr-FR", { day: "2-digit", month: "short", year: "numeric" }) : "—";
 
const fmtMoney = (n) =>
  n != null ? new Intl.NumberFormat("fr-MA", { style: "currency", currency: "MAD" }).format(n) : "—";
 
const STATUT_META = {
  DECLARE:      { label: "Déclaré",    color: "text-cyan-400",    bg: "bg-cyan-500/10",    dot: "bg-cyan-400"    },
  AFFECTE:      { label: "Affecté",    color: "text-blue-400",    bg: "bg-blue-500/10",    dot: "bg-blue-400"    },
  EN_EXPERTISE: { label: "Expertise",  color: "text-purple-400",  bg: "bg-purple-500/10",  dot: "bg-purple-400"  },
  EVALUE:       { label: "À décider",  color: "text-yellow-400",  bg: "bg-yellow-500/10",  dot: "bg-yellow-400"  },
  APPROUVE:     { label: "Approuvé",   color: "text-emerald-400", bg: "bg-emerald-500/10", dot: "bg-emerald-400" },
  REJETE:       { label: "Rejeté",     color: "text-red-400",     bg: "bg-red-500/10",     dot: "bg-red-400"     },
  CLOTURE:      { label: "Clôturé",    color: "text-slate-400",   bg: "bg-slate-500/10",   dot: "bg-slate-400"   },
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
 
function StatCard({ icon: Icon, label, value, color, borderColor }) {
  return (
    <div className={`flex items-center gap-4 p-5 rounded-2xl bg-[#111827]/60 border ${borderColor}`}>
      <Icon size={20} className={color} />
      <div>
        <p className="text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-0.5">{label}</p>
        <p className={`text-2xl font-black ${color}`}>{String(value).padStart(2, "0")}</p>
      </div>
    </div>
  );
}
 
function InfoCard({ icon: Icon, label, value, mono = false, highlight = false }) {
  return (
    <div className="bg-white/3 border border-white/5 rounded-xl p-4">
      <div className="flex items-center gap-2 mb-2">
        <Icon size={13} className="text-slate-500" />
        <span className="text-[10px] font-bold uppercase tracking-widest text-slate-500">{label}</span>
      </div>
      <p className={`text-sm font-semibold leading-snug break-all
        ${highlight ? "text-yellow-400" : "text-slate-200"}
        ${mono ? "font-mono text-xs" : ""}`}>
        {value || "—"}
      </p>
    </div>
  );
}
 
function Section({ title, icon: Icon, accentClass = "text-yellow-400", borderClass = "border-yellow-500/20", children }) {
  return (
    <div className={`bg-[#111827]/60 border ${borderClass} rounded-2xl overflow-hidden mb-4`}>
      <div className={`flex items-center gap-2 px-5 py-3.5 border-b ${borderClass}`}>
        <Icon size={15} className={accentClass} />
        <span className={`text-[11px] font-black uppercase tracking-widest ${accentClass}`}>{title}</span>
      </div>
      <div className="p-5">{children}</div>
    </div>
  );
}
 
function DecisionModal({ sinistre, type, onClose, onSuccess }) {
  const [montant, setMontant] = useState("");
  const [motif,   setMotif]   = useState("");
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState(null);
 
  const isApprouver = type === "APPROUVER";
 
  const handleSubmit = async () => {
    if (!motif.trim()) { setError("Le motif est obligatoire."); return; }
    if (isApprouver && (!montant || isNaN(montant) || Number(montant) <= 0)) {
      setError("Veuillez saisir un montant valide."); return;
    }
    setLoading(true); setError(null);
    try {
      if (isApprouver) {
        await api.put(`/sinistres/${sinistre.id}/approuver`, { montantFinal: parseFloat(montant), motif });
      } else {
        await api.put(`/sinistres/${sinistre.id}/rejeter`, { motif });
      }
      onSuccess();
      onClose();
    } catch (e) {
      setError(e.response?.data?.message || "Erreur lors de la décision.");
    } finally {
      setLoading(false);
    }
  };
 
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm"
      onClick={onClose}>
      <div className="bg-[#111827] border border-white/10 rounded-2xl w-full max-w-md p-6 shadow-2xl animate-[slideUp_0.2s_ease]"
        onClick={e => e.stopPropagation()}>
        <style>{`@keyframes slideUp{from{opacity:0;transform:translateY(14px)}to{opacity:1;transform:translateY(0)}}`}</style>
 
        <div className="flex justify-between items-start mb-6">
          <div>
            <h3 className={`text-lg font-bold ${isApprouver ? "text-emerald-400" : "text-red-400"}`}>
              {isApprouver ? "✓ Approuver le dossier" : "✕ Rejeter le dossier"}
            </h3>
            <p className="text-xs text-slate-500 mt-0.5">Dossier #SP-{sinistre.id}</p>
          </div>
          <button onClick={onClose} className="text-slate-500 hover:text-white text-xl leading-none">×</button>
        </div>
 
        {isApprouver && (
          <>
            <label className="block text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-2">
              Montant final accordé (MAD) *
            </label>
            <input type="number" placeholder="ex: 12500" min={0} step={0.01}
              value={montant} onChange={e => setMontant(e.target.value)}
              className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white outline-none focus:border-emerald-500/60 mb-4 transition-all placeholder-slate-600 font-mono" />
          </>
        )}
 
        <label className="block text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-2">
          Motif / Commentaire *
        </label>
        <textarea rows={4} placeholder={isApprouver ? "Motif de l'approbation…" : "Raison du rejet…"}
          value={motif} onChange={e => setMotif(e.target.value)}
          className={`w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white outline-none mb-4 resize-none transition-all placeholder-slate-600
            ${isApprouver ? "focus:border-emerald-500/60" : "focus:border-red-500/60"}`} />
 
        {error && (
          <div className="bg-red-500/10 border border-red-500/20 rounded-xl px-4 py-3 mb-4 text-xs text-red-400">{error}</div>
        )}
 
        <div className="flex gap-3 justify-end">
          <button onClick={onClose} disabled={loading}
            className="px-5 py-2.5 rounded-xl text-xs font-bold text-slate-400 bg-white/5 hover:bg-white/10 transition-all">
            Annuler
          </button>
          <button onClick={handleSubmit} disabled={loading}
            className={`px-5 py-2.5 rounded-xl text-xs font-black transition-all flex items-center gap-2 disabled:opacity-60
              ${isApprouver ? "bg-emerald-500 hover:bg-emerald-400 text-black" : "bg-red-500 hover:bg-red-400 text-white"}`}>
            {loading ? <Loader2 size={14} className="animate-spin" /> : isApprouver ? <CheckCircle size={14} /> : <XCircle size={14} />}
            {loading ? "Envoi…" : isApprouver ? "Confirmer l'approbation" : "Confirmer le rejet"}
          </button>
        </div>
      </div>
    </div>
  );
}
 

export function DetailSinistreSuperviseur({ selectedSinistreId, setPage }) {
  const [sinistre, setSinistre] = useState(null);
  const [rapport,  setRapport]  = useState(null);
  const [decision, setDecision] = useState(null);
  const [loading,  setLoading]  = useState(true);
  const [error,    setError]    = useState(null);
  const [modal,    setModal]    = useState(null);
 
  const load = useCallback(async () => {
    if (!selectedSinistreId) return;
    setLoading(true); setError(null);
    try {
      const [s, r, d] = await Promise.all([
        sinistreService.getById(selectedSinistreId),
        sinistreService.getRapport(selectedSinistreId).catch(() => null),
        sinistreService.getDecision(selectedSinistreId).catch(() => null),
      ]);
      setSinistre(s); setRapport(r); setDecision(d);
    } catch {
      setError("Impossible de charger le dossier.");
    } finally {
      setLoading(false);
    }
  }, [selectedSinistreId]);
 
  useEffect(() => { load(); }, [load]);
 
  if (loading) return (
    <div className="min-h-screen bg-[#0a0f1a] flex items-center justify-center">
      <Loader2 size={36} className="text-yellow-400 animate-spin" />
    </div>
  );
 
  if (error || !sinistre) return (
    <div className="min-h-screen bg-[#0a0f1a] flex items-center justify-center text-center">
      <div>
        <AlertCircle size={40} className="text-red-400 mx-auto mb-3" />
        <p className="text-slate-400 mb-4">{error || "Dossier introuvable."}</p>
        <button onClick={() => setPage("DossiersSuperviseur")}
          className="px-5 py-2.5 bg-white/5 hover:bg-white/10 rounded-xl text-sm text-white transition-all">← Retour</button>
      </div>
    </div>
  );
 
  const client = sinistre.client || {};
  const clientNom = `${client.prenom || sinistre.clientPrenom || ""} ${client.nom || sinistre.clientNom || ""}`.trim();
  const clientEmail = client.email || sinistre.clientEmail || "—";
  const canDecider = sinistre.statut === "EVALUE";
 
  return (
    <div className="min-h-screen bg-[#0a0f1a] text-white">
      {modal && (
        <DecisionModal sinistre={sinistre} type={modal} onClose={() => setModal(null)} onSuccess={load} />
      )}
 
      <div className="sticky top-0 z-40 bg-[#0a0f1a]/90 backdrop-blur border-b border-white/5 px-8 py-4">
        <div className="max-w-6xl mx-auto flex items-center gap-4">
          <button onClick={() => setPage("DossiersSuperviseur")}
            className="p-2 bg-white/5 hover:bg-white/10 rounded-xl transition-all">
            <ArrowLeft size={18} />
          </button>
          <div className="flex-1">
            <p className="text-[10px] font-bold uppercase tracking-widest text-yellow-400 mb-0.5">Révision dossier</p>
            <h1 className="text-lg font-black text-white">
              #SP-{sinistre.id}
              <span className="ml-2 text-slate-500 font-mono text-sm">— {sinistre.typeSinistre?.replace(/_/g, " ")}</span>
            </h1>
          </div>
          <StatusBadge statut={sinistre.statut} />
        </div>
      </div>
 
      <div className="max-w-6xl mx-auto px-8 py-8">
 
        {/* Action requise */}
        {canDecider && (
          <div className="flex items-center gap-4 mb-6 p-4 bg-yellow-500/5 border border-yellow-500/20 rounded-2xl">
            <div className="flex-1">
              <p className="text-xs font-bold text-yellow-400 mb-0.5">Action requise</p>
              <p className="text-[11px] text-slate-500">Ce dossier est évalué et attend votre décision.</p>
            </div>
            <div className="flex gap-3">
              <button onClick={() => setModal("APPROUVER")}
                className="flex items-center gap-2 px-5 py-2.5 bg-emerald-500/10 hover:bg-emerald-500/20 border border-emerald-500/30 rounded-xl text-xs font-black text-emerald-400 transition-all uppercase tracking-widest">
                <CheckCircle size={14} /> Approuver
              </button>
              <button onClick={() => setModal("REJETER")}
                className="flex items-center gap-2 px-5 py-2.5 bg-red-500/10 hover:bg-red-500/20 border border-red-500/30 rounded-xl text-xs font-black text-red-400 transition-all uppercase tracking-widest">
                <XCircle size={14} /> Rejeter
              </button>
            </div>
          </div>
        )}
 
        {decision && !canDecider && (
          <div className={`flex items-center gap-4 mb-6 p-4 rounded-2xl border
            ${sinistre.statut === "APPROUVE" ? "bg-emerald-500/5 border-emerald-500/20" : "bg-red-500/5 border-red-500/20"}`}>
            {sinistre.statut === "APPROUVE"
              ? <CheckCircle size={20} className="text-emerald-400 shrink-0" />
              : <XCircle    size={20} className="text-red-400 shrink-0" />}
            <div>
              <p className={`text-sm font-bold ${sinistre.statut === "APPROUVE" ? "text-emerald-400" : "text-red-400"}`}>
                Décision rendue le {fmt(decision.dateDecision)}
              </p>
              {decision.motif && <p className="text-xs text-slate-500 mt-0.5">{decision.motif}</p>}
            </div>
            {decision.montantFinal != null && (
              <div className="ml-auto text-right">
                <p className="text-[10px] text-slate-600 uppercase font-bold">Montant accordé</p>
                <p className="text-sm font-black text-emerald-400">{fmtMoney(decision.montantFinal)}</p>
              </div>
            )}
          </div>
        )}
 
        <div className="grid grid-cols-3 gap-6">
          <div className="col-span-2 space-y-4">
 
            <Section title="Informations du sinistre" icon={FileText}>
              <div className="grid grid-cols-2 gap-3 mb-4">
                <InfoCard icon={AlertCircle} label="Type"        value={sinistre.typeSinistre?.replace(/_/g, " ")} highlight />
                <InfoCard icon={Calendar}    label="Date"        value={fmt(sinistre.dateIncident)} />
                <InfoCard icon={MapPin}      label="Lieu"        value={sinistre.lieuIncident} />
                <InfoCard icon={ShieldCheck} label="N° Police"   value={sinistre.numeroPolicAssurance} mono />
              </div>
              {sinistre.description && (
                <div className="bg-white/3 border border-white/5 rounded-xl p-4">
                  <p className="text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-2">Description</p>
                  <p className="text-sm text-slate-300 leading-relaxed">{sinistre.description}</p>
                </div>
              )}
            </Section>
 
            {rapport ? (
              <Section title="Rapport d'expertise" icon={ClipboardList} accentClass="text-purple-400" borderClass="border-purple-500/20">
                <div className="grid grid-cols-2 gap-3 mb-4">
                    <InfoCard icon={User} label="Expert"
  value={rapport.expert
    ? `${rapport.expert.prenom || ""} ${rapport.expert.nom || ""}`.trim()
    : "—"} />
                  <InfoCard icon={Banknote} label="Montant estimé" value={fmtMoney(rapport.montantEstime)} highlight />
                  <InfoCard icon={Calendar} label="Date rapport" value={fmt(rapport.dateSoumission)} />
                </div>
                {rapport.descriptionDommages && (
                  <div className="bg-white/3 border border-white/5 rounded-xl p-4 mb-3">
                    <p className="text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-2">Dommages constatés</p>
                    <p className="text-sm text-slate-300 leading-relaxed">{rapport.descriptionDommages}</p>
                  </div>
                )}
                {rapport.observations && (
                  <div className="bg-white/3 border border-white/5 rounded-xl p-4">
                    <p className="text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-2">Observations</p>
                    <p className="text-sm text-slate-300 leading-relaxed">{rapport.observations}</p>
                  </div>
                )}
                <button
                  onClick={() => sinistreService.downloadPdf(`/rapports/${sinistre.id}/pdf`, `rapport-SP-${sinistre.id}.pdf`)}
                  className="mt-4 flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs font-bold text-purple-400 bg-purple-500/10 hover:bg-purple-500/20 border border-purple-500/20 transition-all">
                  <Download size={13} /> Télécharger le rapport PDF
                </button>
              </Section>
            ) : (
              <Section title="Rapport d'expertise" icon={ClipboardList} accentClass="text-slate-500" borderClass="border-white/5">
                <div className="flex items-center gap-3 text-slate-600">
                  <Clock size={16} />
                  <p className="text-sm">Aucun rapport disponible.</p>
                </div>
              </Section>
            )}
          </div>
 
          <div className="space-y-4">
            <Section title="Client assuré" icon={User} accentClass="text-yellow-400" borderClass="border-yellow-500/20">
              <div className="flex flex-col items-center text-center mb-4">
                <div className="w-14 h-14 rounded-2xl bg-yellow-500/10 border border-yellow-500/20 flex items-center justify-center text-xl font-black text-yellow-400 mb-3">
                  {(clientNom?.[0] || "?").toUpperCase()}
                </div>
                <p className="font-bold text-sm text-white">{clientNom || "—"}</p>
                <p className="text-xs text-slate-500 mt-0.5">{clientEmail}</p>
              </div>
              <div className="space-y-2 text-xs">
                <div className="flex justify-between">
                  <span className="text-slate-600">Téléphone</span>
                  <span className="text-slate-300 font-mono">{client.telephone || sinistre.clientTelephone || "—"}</span>
                </div>
                <div className="flex justify-between">
                  <span className="text-slate-600">Déclaré le</span>
                  <span className="text-slate-300">{fmt(sinistre.dateDeclaration || sinistre.dateCreation)}</span>
                </div>
              </div>
            </Section>
 
            <Section title="Statut dossier" icon={ShieldCheck} accentClass="text-slate-400" borderClass="border-white/5">
              <div className="space-y-3">
                <StatusBadge statut={sinistre.statut} />
                <div className="flex justify-between text-xs mt-3">
                  <span className="text-slate-600">Rapport expert</span>
                  <span className={rapport ? "text-emerald-400 font-bold" : "text-red-400"}>
                    {rapport ? "Reçu" : "Absent"}
                  </span>
                </div>
                <div className="flex justify-between text-xs">
                  <span className="text-slate-600">Décision rendue</span>
                  <span className={decision ? "text-emerald-400 font-bold" : "text-slate-600"}>
                    {decision ? "Oui" : "En attente"}
                  </span>
                </div>
              </div>
            </Section>
          </div>
        </div>
      </div>
    </div>
  );
}

export function DossiersSuperviseur({ setPage, setSelectedSinistreId }) {
  const [sinistres, setSinistres] = useState([]);
  const [filtered,  setFiltered]  = useState([]);
  const [loading,   setLoading]   = useState(true);
  const [error,     setError]     = useState(null);
  const [search,    setSearch]    = useState("");
  const [statut,    setStatut]    = useState("EVALUE");
 
  const load = useCallback(async () => {
    setLoading(true); setError(null);
    try {
      const data = await sinistreService.getAllSinistres();
      const relevant = (data || [])
        .filter(s => ["EVALUE","APPROUVE","REJETE","CLOTURE"].includes(s.statut))
        .sort((a, b) => new Date(b.dateIncident) - new Date(a.dateIncident));
      setSinistres(relevant);
      setFiltered(relevant.filter(s => s.statut === "EVALUE"));
    } catch {
      setError("Impossible de charger les dossiers.");
    } finally {
      setLoading(false);
    }
  }, []);
 
  useEffect(() => { load(); }, [load]);
 
  useEffect(() => {
    let result = sinistres;
    if (statut !== "TOUS") result = result.filter(s => s.statut === statut);
    if (search.trim()) {
      const q = search.toLowerCase();
      result = result.filter(s =>
        s.id?.toString().includes(q) ||
        s.typeSinistre?.toLowerCase().includes(q) ||
        s.lieuIncident?.toLowerCase().includes(q)
      );
    }
    setFiltered(result);
  }, [search, statut, sinistres]);
 
  const count = (s) => sinistres.filter(d => d.statut === s).length;
 
  const FILTERS = [
    { key: "EVALUE",   label: "À décider",  count: count("EVALUE"),   urgent: true },
    { key: "APPROUVE", label: "Approuvés",  count: count("APPROUVE")               },
    { key: "REJETE",   label: "Rejetés",    count: count("REJETE")                 },
    { key: "CLOTURE",  label: "Clôturés",   count: count("CLOTURE")                },
    { key: "TOUS",     label: "Tous",       count: sinistres.length                },
  ];
 
  if (loading) return (
    <div className="min-h-[60vh] flex items-center justify-center">
      <Loader2 size={36} className="text-yellow-400 animate-spin" />
    </div>
  );
 
  if (error) return (
    <div className="min-h-[60vh] flex items-center justify-center text-center">
      <div>
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
            <p className="text-yellow-400 text-xs font-bold uppercase tracking-widest mb-1">Espace superviseur</p>
            <h1 className="text-4xl font-extrabold">Dossiers à réviser</h1>
          </div>
          {count("EVALUE") > 0 && (
            <div className="bg-yellow-500/10 border border-yellow-500/20 px-5 py-3 rounded-2xl text-center">
              <p className="text-[10px] text-yellow-500 font-bold uppercase mb-1">En attente</p>
              <p className="text-2xl font-black text-yellow-400">{String(count("EVALUE")).padStart(2,"0")}</p>
            </div>
          )}
        </div>
 
        {/* Stats */}
        <div className="grid grid-cols-4 gap-4 mb-8">
          <StatCard icon={Clock}       label="À décider"  value={count("EVALUE")}   color="text-yellow-400"  borderColor="border-yellow-500/20"  />
          <StatCard icon={CheckCircle} label="Approuvés"  value={count("APPROUVE")} color="text-emerald-400" borderColor="border-emerald-500/20" />
          <StatCard icon={XCircle}     label="Rejetés"    value={count("REJETE")}   color="text-red-400"     borderColor="border-red-500/20"     />
          <StatCard icon={ShieldCheck} label="Clôturés"   value={count("CLOTURE")}  color="text-slate-400"   borderColor="border-slate-500/20"   />
        </div>
 
        {/* Search */}
        <div className="flex items-center gap-3 mb-5 bg-white/5 border border-white/10 rounded-2xl px-4 py-3 focus-within:border-yellow-500/50 transition-all">
          <Search size={16} className="text-slate-500 shrink-0" />
          <input type="text" placeholder="Rechercher par ID, type, lieu…"
            value={search} onChange={e => setSearch(e.target.value)}
            className="bg-transparent flex-1 text-sm text-white placeholder-slate-600 outline-none" />
          {search && <button onClick={() => setSearch("")} className="text-slate-500 hover:text-white text-xs">✕</button>}
        </div>
 
        {/* Filtres */}
        <div className="flex flex-wrap gap-2 mb-8">
          {FILTERS.map(f => (
            <button key={f.key} onClick={() => setStatut(f.key)}
              className={`flex items-center gap-2 px-4 py-2 rounded-xl text-[10px] font-black uppercase tracking-widest transition-all
                ${statut === f.key
                  ? f.urgent ? "bg-yellow-500 text-black" : "bg-white/20 text-white"
                  : "bg-white/5 text-slate-400 hover:bg-white/10"}`}>
              {f.label}
              {f.urgent && f.count > 0 && statut !== f.key && (
                <span className="w-2 h-2 rounded-full bg-yellow-400 animate-pulse" />
              )}
              <span className={`px-1.5 py-0.5 rounded-full text-[9px] font-black
                ${statut === f.key ? "bg-black/20" : "bg-white/10 text-slate-500"}`}>
                {f.count}
              </span>
            </button>
          ))}
        </div>
 
        {/* Table header */}
        {filtered.length > 0 && (
          <div className="grid grid-cols-5 px-6 mb-3 text-[10px] uppercase tracking-widest font-bold text-slate-500">
            <div>Référence</div>
            <div>Type</div>
            <div>Lieu</div>
            <div>Date incident</div>
            <div className="text-right">Statut</div>
          </div>
        )}
 
        {/* Liste */}
        {filtered.length === 0 ? (
          <div className="text-center py-24 bg-slate-900/20 border-2 border-dashed border-white/5 rounded-3xl">
            <Filter size={36} className="text-slate-700 mx-auto mb-3" />
            <p className="text-slate-500 text-sm">
              {statut === "EVALUE" ? "Aucun dossier en attente de décision. " : "Aucun dossier trouvé."}
            </p>
          </div>
        ) : (
          <div className="space-y-3">
            {filtered.map(s => {
              const urgent = s.statut === "EVALUE";
              return (
                <div key={s.id}
                  onClick={() => { setSelectedSinistreId(Number(s.id)); setPage("DetailSinistreSuperviseur"); }}
                  className={`grid grid-cols-5 items-center p-5 border rounded-2xl cursor-pointer transition-all group
                    ${urgent
                      ? "bg-yellow-500/5 border-yellow-500/20 hover:bg-yellow-500/10 hover:border-yellow-500/40"
                      : "bg-[#111827]/50 border-white/5 hover:bg-[#111827]/80 hover:border-white/10"}`}>
                  <div>
                    <p className="font-bold text-sm text-slate-200 font-mono">#SP-{s.id}</p>
                    <p className="text-[10px] text-slate-600 mt-0.5">{fmt(s.dateDeclaration || s.dateCreation)}</p>
                  </div>
                  <div className="flex items-center gap-2">
                    <AlertCircle size={14} className={urgent ? "text-yellow-400" : "text-slate-500"} />
                    <p className="text-sm font-semibold text-slate-300 truncate">{s.typeSinistre?.replace(/_/g, " ")}</p>
                  </div>
                  <div className="flex items-center gap-2">
                    <MapPin size={13} className="text-slate-600 shrink-0" />
                    <p className="text-xs text-slate-400 truncate">{s.lieuIncident || "—"}</p>
                  </div>
                  <div className="flex items-center gap-2">
                    <Calendar size={13} className="text-slate-600 shrink-0" />
                    <p className="text-xs text-slate-400">{fmt(s.dateIncident)}</p>
                  </div>
                  <div className="flex items-center justify-end gap-3">
                    <StatusBadge statut={s.statut} />
                    <ChevronRight size={16} className={`transition-colors ${urgent ? "text-yellow-400" : "text-slate-600 group-hover:text-white"}`} />
                  </div>
                </div>
              );
            })}
          </div>
        )}
 
        {filtered.length > 0 && (
          <p className="text-center text-[10px] text-slate-600 uppercase tracking-widest mt-6">
            {filtered.length} dossier{filtered.length > 1 ? "s" : ""} affiché{filtered.length > 1 ? "s" : ""}
          </p>
        )}
      </div>
    </div>
  );
}