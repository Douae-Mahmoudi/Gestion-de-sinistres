import { useEffect, useState, useCallback } from "react";
import { sinistreService } from "../services/sinistreService";
import {
  ArrowLeft, User, MapPin, Calendar, FileText,
  ClipboardList, CheckCircle, Download,
  Loader2, AlertCircle, UserCheck, Banknote,
  ShieldCheck, Clock, Upload
} from "lucide-react";
 
const fmt = (d) =>
  d ? new Date(d).toLocaleDateString("fr-FR", { day: "2-digit", month: "long", year: "numeric" }) : "—";
 
const fmtMoney = (n) =>
  n != null ? new Intl.NumberFormat("fr-MA", { style: "currency", currency: "MAD" }).format(n) : "—";
 
const STATUT_META = {
  DECLARE:      { label: "Déclaré",      color: "text-cyan-400",    bg: "bg-cyan-500/10",    dot: "bg-cyan-400"    },
  AFFECTE:      { label: "Affecté",      color: "text-blue-400",    bg: "bg-blue-500/10",    dot: "bg-blue-400"    },
  EN_EXPERTISE: { label: "En expertise", color: "text-purple-400",  bg: "bg-purple-500/10",  dot: "bg-purple-400"  },
  EVALUE:       { label: "Évalué",       color: "text-yellow-400",  bg: "bg-yellow-500/10",  dot: "bg-yellow-400"  },
  APPROUVE:     { label: "Approuvé",     color: "text-emerald-400", bg: "bg-emerald-500/10", dot: "bg-emerald-400" },
  REJETE:       { label: "Rejeté",       color: "text-red-400",     bg: "bg-red-500/10",     dot: "bg-red-400"     },
  CLOTURE:      { label: "Clôturé",      color: "text-slate-400",   bg: "bg-slate-500/10",   dot: "bg-slate-400"   },
};
 
const STEPS = ["DECLARE", "AFFECTE", "EN_EXPERTISE", "EVALUE", "APPROUVE", "CLOTURE"];
 
function StatusBadge({ statut }) {
  const m = STATUT_META[statut] || { label: statut, color: "text-white", bg: "bg-white/10", dot: "bg-white" };
  return (
    <span className={`inline-flex items-center gap-2 px-3 py-1 rounded-full text-[11px] font-black uppercase tracking-widest ${m.color} ${m.bg}`}>
      <span className={`w-1.5 h-1.5 rounded-full ${m.dot}`} />
      {m.label}
    </span>
  );
}
 
function ProgressStepper({ statut }) {
  const isRejete = statut === "REJETE";
  const currentIdx = STEPS.indexOf(statut);
 
  return (
    <div className="flex items-center gap-0">
      {STEPS.map((step, i) => {
        const meta = STATUT_META[step];
        const done = currentIdx > i;
        const active = currentIdx === i && !isRejete;
        return (
          <div key={step} className="flex items-center flex-1 last:flex-none">
            <div className="flex flex-col items-center gap-1 min-w-[60px]">
              <div className={`w-7 h-7 rounded-full flex items-center justify-center text-[10px] font-black border-2 transition-all
                ${done   ? "border-orange-500 bg-orange-500 text-black"    : ""}
                ${active ? "border-orange-400 bg-transparent text-orange-400 animate-pulse" : ""}
                ${!done && !active ? "border-white/10 bg-white/5 text-slate-600" : ""}`}>
                {done ? "✓" : i + 1}
              </div>
              <span className={`text-[9px] font-bold uppercase tracking-wide text-center leading-tight
                ${done || active ? "text-orange-400" : "text-slate-600"}`}>
                {meta?.label}
              </span>
            </div>
            {i < STEPS.length - 1 && (
              <div className={`h-px flex-1 mx-1 mb-4 transition-all ${done ? "bg-orange-500" : "bg-white/10"}`} />
            )}
          </div>
        );
      })}
      {isRejete && (
        <div className="ml-4 flex flex-col items-center gap-1">
          <div className="w-7 h-7 rounded-full flex items-center justify-center border-2 border-red-500 bg-red-500/20 text-red-400 text-[10px] font-black">✕</div>
          <span className="text-[9px] font-bold text-red-400 uppercase">Rejeté</span>
        </div>
      )}
    </div>
  );
}
 
function AffecterModal({ sinistre, onClose, onSuccess }) {
  const [expertId,    setExpertId]    = useState("");
  const [commentaire, setCommentaire] = useState("");
  const [experts,     setExperts]     = useState([]);
  const [loading,     setLoading]     = useState(false);
  const [error,       setError]       = useState(null);
 
  useEffect(() => {
    sinistreService.getExperts()
      .then(setExperts)
      .catch(() => setExperts([]));
  }, []);
 
  const handleSubmit = async () => {
    if (!expertId) { setError("Veuillez sélectionner un expert."); return; }
    setLoading(true); setError(null);
    try {
      await sinistreService.affecterExpert(sinistre.id, Number(expertId), commentaire);
      onSuccess();
      onClose();
    } catch (e) {
      setError(e.response?.data?.message || "Erreur lors de l'affectation.");
    } finally {
      setLoading(false);
    }
  };
 
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm"
      onClick={onClose}>
      <div className="bg-[#111827] border border-white/10 rounded-2xl w-full max-w-md p-6 shadow-2xl animate-[slideUp_0.2s_ease]"
        onClick={e => e.stopPropagation()}>
        <style>{`@keyframes slideUp { from{opacity:0;transform:translateY(14px)} to{opacity:1;transform:translateY(0)} }`}</style>
 
        <div className="flex justify-between items-start mb-6">
          <div>
            <h3 className="text-lg font-bold text-white">Affecter un expert</h3>
            <p className="text-xs text-slate-500 mt-0.5">Dossier #SP-{sinistre.id}</p>
          </div>
          <button onClick={onClose} className="text-slate-500 hover:text-white text-xl leading-none transition-colors">×</button>
        </div>
 
        <label className="block text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-2">Expert *</label>
        {experts.length > 0 ? (
          <select value={expertId} onChange={e => setExpertId(e.target.value)}
            className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white outline-none focus:border-orange-500/60 mb-4 transition-all">
            <option value="" className="bg-[#111827]">— Sélectionner un expert —</option>
            {experts.map(ex => (
              <option key={ex.id} value={ex.id} className="bg-[#111827]">
                {ex.prenom} {ex.nom} — {ex.email}
              </option>
            ))}
          </select>
        ) : (
          <input type="number" placeholder="ID de l'expert" value={expertId}
            onChange={e => setExpertId(e.target.value)} min={1}
            className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white outline-none focus:border-orange-500/60 mb-4 transition-all placeholder-slate-600" />
        )}
 
        <label className="block text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-2">Commentaire agent</label>
        <textarea rows={3} placeholder="Instructions ou remarques pour l'expert..."
          value={commentaire} onChange={e => setCommentaire(e.target.value)}
          className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white outline-none focus:border-orange-500/60 mb-4 resize-none transition-all placeholder-slate-600" />
 
        {error && (
          <div className="bg-red-500/10 border border-red-500/20 rounded-xl px-4 py-3 mb-4 text-xs text-red-400">{error}</div>
        )}
 
        <div className="flex gap-3 justify-end">
          <button onClick={onClose} disabled={loading}
            className="px-5 py-2.5 rounded-xl text-xs font-bold text-slate-400 bg-white/5 hover:bg-white/10 transition-all">
            Annuler
          </button>
          <button onClick={handleSubmit} disabled={loading}
            className="px-5 py-2.5 rounded-xl text-xs font-black bg-orange-500 hover:bg-orange-400 text-black transition-all flex items-center gap-2 disabled:opacity-60">
            {loading ? <Loader2 size={14} className="animate-spin" /> : <UserCheck size={14} />}
            {loading ? "Affectation…" : "Confirmer"}
          </button>
        </div>
      </div>
    </div>
  );
}
 
function CloturerModal({ sinistre, onClose, onSuccess }) {
  const [numeroVirement, setNumeroVirement] = useState("");
  const [datePaiement,   setDatePaiement]   = useState("");
  const [loading,        setLoading]        = useState(false);
  const [error,          setError]          = useState(null);
 
  const handleSubmit = async () => {
    if (!numeroVirement || !datePaiement) { setError("Tous les champs sont obligatoires."); return; }
    setLoading(true); setError(null);
    try {
      await sinistreService.cloturerSinistre(sinistre.id, { numeroVirement, datePaiement });
      onSuccess();
      onClose();
    } catch (e) {
      setError(e.response?.data?.message || "Erreur lors de la clôture.");
    } finally {
      setLoading(false);
    }
  };
 
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm"
      onClick={onClose}>
      <div className="bg-[#111827] border border-white/10 rounded-2xl w-full max-w-md p-6 shadow-2xl animate-[slideUp_0.2s_ease]"
        onClick={e => e.stopPropagation()}>
 
        <div className="flex justify-between items-start mb-6">
          <div>
            <h3 className="text-lg font-bold text-white">Clôturer le dossier</h3>
            <p className="text-xs text-slate-500 mt-0.5">Dossier #SP-{sinistre.id} — Confirmation du virement</p>
          </div>
          <button onClick={onClose} className="text-slate-500 hover:text-white text-xl leading-none transition-colors">×</button>
        </div>
 
        <label className="block text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-2">N° de virement *</label>
        <input type="text" placeholder="VIR-2024-XXXXXX" value={numeroVirement}
          onChange={e => setNumeroVirement(e.target.value)}
          className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white outline-none focus:border-orange-500/60 mb-4 transition-all placeholder-slate-600 font-mono" />
 
        <label className="block text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-2">Date de paiement *</label>
        <input type="date" value={datePaiement} onChange={e => setDatePaiement(e.target.value)}
          className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white outline-none focus:border-orange-500/60 mb-4 transition-all" />
 
        {error && (
          <div className="bg-red-500/10 border border-red-500/20 rounded-xl px-4 py-3 mb-4 text-xs text-red-400">{error}</div>
        )}
 
        <div className="flex gap-3 justify-end">
          <button onClick={onClose} disabled={loading}
            className="px-5 py-2.5 rounded-xl text-xs font-bold text-slate-400 bg-white/5 hover:bg-white/10 transition-all">
            Annuler
          </button>
          <button onClick={handleSubmit} disabled={loading}
            className="px-5 py-2.5 rounded-xl text-xs font-black bg-emerald-500 hover:bg-emerald-400 text-black transition-all flex items-center gap-2 disabled:opacity-60">
            {loading ? <Loader2 size={14} className="animate-spin" /> : <CheckCircle size={14} />}
            {loading ? "Clôture…" : "Confirmer la clôture"}
          </button>
        </div>
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
      <p className={`text-sm font-semibold leading-snug
        ${highlight ? "text-orange-400" : "text-slate-200"}
        ${mono ? "font-mono text-xs" : ""}`}>
        {value || "—"}
      </p>
    </div>
  );
}
 
function Section({ title, icon: Icon, accentClass = "text-orange-400", borderClass = "border-orange-500/20", children }) {
  return (
    <div className={`bg-[#111827]/60 border ${borderClass} rounded-2xl overflow-hidden mb-4`}>
      <div className={`flex items-center gap-2 px-5 py-3.5 border-b ${borderClass} bg-white/2`}>
        <Icon size={15} className={accentClass} />
        <span className={`text-[11px] font-black uppercase tracking-widest ${accentClass}`}>{title}</span>
      </div>
      <div className="p-5">{children}</div>
    </div>
  );
}
 
export function DetailSinistreAgent({ selectedSinistreId, setPage }) {
  const [sinistre,  setSinistre]  = useState(null);
  const [rapport,   setRapport]   = useState(null);
  const [decision,  setDecision]  = useState(null);
  const [documents, setDocuments] = useState([]);
  const [loading,   setLoading]   = useState(true);
  const [error,     setError]     = useState(null);
 
  const [showAffecter, setShowAffecter] = useState(false);
  const [showCloturer, setShowCloturer] = useState(false);
 
  const load = useCallback(async () => {
    if (!selectedSinistreId) return;
    setLoading(true); setError(null);
    try {
      const [s, r, d, docs] = await Promise.all([
        sinistreService.getById(selectedSinistreId),
        sinistreService.getRapport(selectedSinistreId),
        sinistreService.getDecision(selectedSinistreId),
        sinistreService.getDocumentsBySinistre(selectedSinistreId).catch(() => []),
      ]);
      setSinistre(s);
      setRapport(r);
      setDecision(d);
      setDocuments(docs || []);
    } catch (e) {
      setError("Impossible de charger le dossier.");
    } finally {
      setLoading(false);
    }
  }, [selectedSinistreId]);
 
  useEffect(() => { load(); }, [load]);
 
  if (loading) return (
    <div className="min-h-screen bg-[#0a0f1a] flex items-center justify-center">
      <div className="flex flex-col items-center gap-4">
        <Loader2 size={36} className="text-orange-400 animate-spin" />
        <p className="text-slate-500 text-sm uppercase tracking-widest font-bold">Chargement du dossier…</p>
      </div>
    </div>
  );
 
  if (error || !sinistre) return (
    <div className="min-h-screen bg-[#0a0f1a] flex items-center justify-center">
      <div className="text-center">
        <AlertCircle size={40} className="text-red-400 mx-auto mb-3" />
        <p className="text-slate-400 mb-4">{error || "Dossier introuvable."}</p>
        <button onClick={() => setPage("GestionSinistres")}
          className="px-5 py-2.5 bg-white/5 hover:bg-white/10 rounded-xl text-sm text-white transition-all">
          ← Retour
        </button>
      </div>
    </div>
  );
 
  const client = sinistre.client || {};
  const expert = sinistre.expert || {};
 
  const canAffecter = sinistre.statut === "DECLARE";
  const canCloturer = sinistre.statut === "APPROUVE";
 
  const canDownloadRapportPdf = !!decision;
 
  return (
    <div className="min-h-screen bg-[#0a0f1a] text-white">
      {showAffecter && (
        <AffecterModal sinistre={sinistre} onClose={() => setShowAffecter(false)} onSuccess={load} />
      )}
      {showCloturer && (
        <CloturerModal sinistre={sinistre} onClose={() => setShowCloturer(false)} onSuccess={load} />
      )}
 
      <div className="sticky top-0 z-40 bg-[#0a0f1a]/90 backdrop-blur border-b border-white/5 px-8 py-4">
        <div className="max-w-6xl mx-auto flex items-center gap-4">
          <button onClick={() => setPage("GestionSinistres")}
            className="p-2 bg-white/5 hover:bg-white/10 rounded-xl transition-all">
            <ArrowLeft size={18} />
          </button>
          <div className="flex-1">
            <p className="text-[10px] font-bold uppercase tracking-widest text-orange-400 mb-0.5">Détail du dossier</p>
            <h1 className="text-lg font-black text-white">
              #SP-{sinistre.id}
              <span className="ml-2 text-slate-500 font-mono text-sm">— {sinistre.typeSinistre?.replace(/_/g, " ")}</span>
            </h1>
          </div>
          <StatusBadge statut={sinistre.statut} />
        </div>
      </div>
 
      <div className="max-w-6xl mx-auto px-8 py-8">
 
        <div className="bg-[#111827]/60 border border-white/5 rounded-2xl p-6 mb-6">
          <p className="text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-5">Avancement du dossier</p>
          <ProgressStepper statut={sinistre.statut} />
        </div>
 
        {(canAffecter || canCloturer) && (
          <div className="flex gap-3 mb-6">
            {canAffecter && (
              <button onClick={() => setShowAffecter(true)}
                className="flex items-center gap-2 px-5 py-3 bg-blue-500/10 hover:bg-blue-500/20 border border-blue-500/20 hover:border-blue-500/40 rounded-xl text-xs font-black text-blue-400 transition-all uppercase tracking-widest">
                <UserCheck size={15} />
                Affecter un expert
              </button>
            )}
            {canCloturer && (
              <button onClick={() => setShowCloturer(true)}
                className="flex items-center gap-2 px-5 py-3 bg-emerald-500/10 hover:bg-emerald-500/20 border border-emerald-500/20 hover:border-emerald-500/40 rounded-xl text-xs font-black text-emerald-400 transition-all uppercase tracking-widest">
                <CheckCircle size={15} />
                Clôturer le dossier
              </button>
            )}
          </div>
        )}
 
        <div className="grid grid-cols-3 gap-6">
          <div className="col-span-2 space-y-4">
 
            <Section title="Informations du sinistre" icon={FileText}>
              <div className="grid grid-cols-2 gap-3 mb-4">
                <InfoCard icon={AlertCircle} label="Type de sinistre" value={sinistre.typeSinistre?.replace(/_/g, " ")} highlight />
                <InfoCard icon={Calendar}    label="Date de l'incident" value={fmt(sinistre.dateIncident)} />
                <InfoCard icon={MapPin}      label="Lieu de l'incident" value={sinistre.lieuIncident} />
                <InfoCard icon={ShieldCheck} label="N° Police d'assurance" value={sinistre.numeroPolicAssurance} mono />
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
                  <InfoCard icon={Calendar} label="Date du rapport" value={fmt(rapport.dateSoumission)} />
                </div>
                {rapport.descriptionDommages && (
                  <div className="bg-white/3 border border-white/5 rounded-xl p-4 mb-3">
                    <p className="text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-2">Description des dommages</p>
                    <p className="text-sm text-slate-300 leading-relaxed">{rapport.descriptionDommages}</p>
                  </div>
                )}
                {rapport.observations && (
                  <div className="bg-white/3 border border-white/5 rounded-xl p-4">
                    <p className="text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-2">Observations</p>
                    <p className="text-sm text-slate-300 leading-relaxed">{rapport.observations}</p>
                  </div>
                )}
 
                {canDownloadRapportPdf ? (
                  <button
                    onClick={() => sinistreService.downloadPdf(`/rapports/${sinistre.id}/pdf`, `rapport-SP-${sinistre.id}.pdf`)}
                    className="mt-4 flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs font-bold text-purple-400 bg-purple-500/10 hover:bg-purple-500/20 border border-purple-500/20 transition-all">
                    <Download size={13} />
                    Télécharger le rapport PDF
                  </button>
                ) : (
                  <div className="mt-4 flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs text-slate-600 bg-white/3 border border-white/5">
                    <Clock size={13} />
                    PDF disponible après décision du superviseur
                  </div>
                )}
              </Section>
            ) : (
              <Section title="Rapport d'expertise" icon={ClipboardList} accentClass="text-slate-500" borderClass="border-white/5">
                <div className="flex items-center gap-3 text-slate-600">
                  <Clock size={16} />
                  <p className="text-sm">Aucun rapport disponible pour le moment.</p>
                </div>
              </Section>
            )}
 
            {decision ? (
              <Section title="Décision du superviseur" icon={ShieldCheck}
                accentClass={decision.statut === "APPROUVE" || sinistre.statut === "APPROUVE" || sinistre.statut === "CLOTURE" ? "text-emerald-400" : "text-red-400"}
                borderClass={decision.statut === "APPROUVE" || sinistre.statut === "APPROUVE" || sinistre.statut === "CLOTURE" ? "border-emerald-500/20" : "border-red-500/20"}>
                <div className="grid grid-cols-2 gap-3 mb-4">
                  <InfoCard icon={Calendar} label="Date de décision" value={fmt(decision.dateDecision)} />
                  {decision.montantFinal != null && (
                    <InfoCard icon={Banknote} label="Montant final accordé" value={fmtMoney(decision.montantFinal)} highlight />
                  )}
                </div>
                {decision.motif && (
                  <div className="bg-white/3 border border-white/5 rounded-xl p-4">
                    <p className="text-[10px] font-bold uppercase tracking-widest text-slate-500 mb-2">Motif / Commentaire</p>
                    <p className="text-sm text-slate-300 leading-relaxed">{decision.motif}</p>
                  </div>
                )}
                <button
                  onClick={() => sinistreService.downloadPdf(`/decisions/${sinistre.id}/pdf`, `decision-SP-${sinistre.id}.pdf`)}
                  className="mt-4 flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs font-bold text-emerald-400 bg-emerald-500/10 hover:bg-emerald-500/20 border border-emerald-500/20 transition-all">
                  <Download size={13} />
                  Télécharger la lettre de décision PDF
                </button>
              </Section>
            ) : (
              <Section title="Décision du superviseur" icon={ShieldCheck} accentClass="text-slate-500" borderClass="border-white/5">
                <div className="flex items-center gap-3 text-slate-600">
                  <Clock size={16} />
                  <p className="text-sm">Aucune décision rendue pour le moment.</p>
                </div>
              </Section>
            )}
 
            {sinistre.statut === "CLOTURE" && decision?.numeroVirement && (
              <Section title="Virement effectué" icon={Banknote} accentClass="text-emerald-400" borderClass="border-emerald-500/20">
                <div className="grid grid-cols-2 gap-3">
                  <InfoCard icon={Banknote} label="N° de virement" value={decision.numeroVirement} mono highlight />
                  <InfoCard icon={Calendar} label="Date de paiement" value={fmt(decision.datePaiement)} />
                </div>
              </Section>
            )}
          </div>
 
          <div className="space-y-4">
 
            <Section title="Client" icon={User} accentClass="text-orange-400" borderClass="border-orange-500/20">
              <div className="flex flex-col items-center text-center mb-4">
                <div className="w-14 h-14 rounded-2xl bg-orange-500/10 border border-orange-500/20 flex items-center justify-center text-xl font-black text-orange-400 mb-3">
                  {(client.prenom?.[0] || "") + (client.nom?.[0] || "") || "?"}
                </div>
                <p className="font-bold text-sm text-white">
                  {client.prenom || ""} {client.nom || ""}
                </p>
                <p className="text-xs text-slate-500 mt-0.5">{client.email || "—"}</p>
              </div>
              <div className="space-y-2">
                <div className="flex justify-between text-xs">
                  <span className="text-slate-600">Téléphone</span>
                  <span className="text-slate-300 font-mono">{client.telephone || "—"}</span>
                </div>
                <div className="flex justify-between text-xs">
                  <span className="text-slate-600">Déclaré le</span>
                  <span className="text-slate-300">{fmt(sinistre.dateDeclaration)}</span>
                </div>
              </div>
            </Section>
 
            {expert.nom && (
              <Section title="Expert assigné" icon={UserCheck} accentClass="text-blue-400" borderClass="border-blue-500/20">
                <div className="flex items-center gap-3 mb-3">
                  <div className="w-10 h-10 rounded-xl bg-blue-500/10 border border-blue-500/20 flex items-center justify-center text-sm font-black text-blue-400">
                    {(expert.prenom?.[0] || "") + (expert.nom?.[0] || "")}
                  </div>
                  <div>
                    <p className="text-sm font-bold text-white">{expert.prenom} {expert.nom}</p>
                    <p className="text-[10px] text-slate-500">{expert.email || ""}</p>
                  </div>
                </div>
                {sinistre.commentaireAgent && (
                  <div className="bg-white/3 border border-white/5 rounded-xl p-3">
                    <p className="text-[10px] font-bold uppercase tracking-widest text-slate-600 mb-1">Note agent</p>
                    <p className="text-xs text-slate-400 leading-relaxed">{sinistre.commentaireAgent}</p>
                  </div>
                )}
              </Section>
            )}
 
            <Section title={`Documents (${documents.length})`} icon={Upload} accentClass="text-slate-400" borderClass="border-white/5">
              {documents.length === 0 ? (
                <p className="text-xs text-slate-600 text-center py-3">Aucun document joint.</p>
              ) : (
                <div className="space-y-2">
                  {documents.map(doc => (
                    <div key={doc.id}
                      className="flex items-center justify-between p-3 bg-white/3 border border-white/5 rounded-xl hover:bg-white/5 transition-all group">
                      <div className="min-w-0 mr-2">
                        <p className="text-xs font-semibold text-slate-300 truncate">{doc.nomFichier}</p>
                        <p className="text-[10px] text-slate-600">{doc.typeDocument} · {doc.taille ? `${(doc.taille / 1024).toFixed(1)} Ko` : ""}</p>
                      </div>
                      <button
                        onClick={() => sinistreService.downloadDocument(doc.id, doc.nomFichier)}
                        className="shrink-0 p-1.5 rounded-lg text-slate-600 hover:text-orange-400 hover:bg-orange-500/10 transition-all">
                        <Download size={13} />
                      </button>
                    </div>
                  ))}
                </div>
              )}
            </Section>
          </div>
        </div>
      </div>
    </div>
  );
}