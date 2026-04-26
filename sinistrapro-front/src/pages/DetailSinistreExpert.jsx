import { useEffect, useState, useCallback } from "react";
import { sinistreService } from "../services/sinistreService";
import {
  ArrowLeft, User, MapPin, Calendar, FileText,
  ClipboardList, CheckCircle, Download,
  Loader2, AlertCircle, Banknote,
  ShieldCheck, Clock, Send
} from "lucide-react";

const fmt = (d) =>
  d ? new Date(d).toLocaleDateString("fr-FR", { day: "2-digit", month: "long", year: "numeric" }) : "—";

const fmtMoney = (n) =>
  n != null ? new Intl.NumberFormat("fr-MA", { style: "currency", currency: "MAD" }).format(n) : "—";

const STATUT_META = {
  DECLARE:      { label: "Déclaré",      color: "text-cyan-400",    bg: "bg-cyan-500/10"    },
  AFFECTE:      { label: "Affecté",      color: "text-blue-400",    bg: "bg-blue-500/10"    },
  EN_EXPERTISE: { label: "En expertise", color: "text-purple-400",  bg: "bg-purple-500/10"  },
  EVALUE:       { label: "Évalué",       color: "text-yellow-400",  bg: "bg-yellow-500/10"  },
  APPROUVE:     { label: "Approuvé",     color: "text-emerald-400", bg: "bg-emerald-500/10" },
  REJETE:       { label: "Rejeté",       color: "text-red-400",     bg: "bg-red-500/10"     },
  CLOTURE:      { label: "Clôturé",      color: "text-slate-400",   bg: "bg-slate-500/10"   },
};

const STEPS = ["DECLARE", "AFFECTE", "EN_EXPERTISE", "EVALUE", "APPROUVE", "CLOTURE"];

function StatusBadge({ statut }) {
  const m = STATUT_META[statut] || { label: statut, color: "text-white", bg: "bg-white/10" };
  return (
    <span className={`inline-flex items-center gap-2 px-3 py-1 rounded-full text-[11px] font-black uppercase tracking-widest ${m.color} ${m.bg}`}>
      <span className="w-1.5 h-1.5 rounded-full bg-current" />
      {m.label}
    </span>
  );
}

function ProgressStepper({ statut }) {
  const currentIdx = STEPS.indexOf(statut);
  return (
    <div className="flex items-center gap-0">
      {STEPS.map((step, i) => {
        const meta = STATUT_META[step];
        const done   = currentIdx > i;
        const active = currentIdx === i;
        return (
          <div key={step} className="flex items-center flex-1 last:flex-none">
            <div className="flex flex-col items-center gap-1 min-w-[60px]">
              <div className={`w-7 h-7 rounded-full flex items-center justify-center text-[10px] font-black border-2 transition-all
                ${done   ? "border-purple-500 bg-purple-500 text-black" : ""}
                ${active ? "border-purple-400 bg-transparent text-purple-400 animate-pulse" : ""}
                ${!done && !active ? "border-white/10 bg-white/5 text-slate-600" : ""}`}>
                {done ? "✓" : i + 1}
              </div>
              <span className={`text-[9px] font-bold uppercase tracking-wide text-center leading-tight
                ${done || active ? "text-purple-400" : "text-slate-600"}`}>
                {meta?.label}
              </span>
            </div>
            {i < STEPS.length - 1 && (
              <div className={`h-px flex-1 mx-1 mb-4 ${done ? "bg-purple-500" : "bg-white/10"}`} />
            )}
          </div>
        );
      })}
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
        ${highlight ? "text-purple-400" : "text-slate-200"}
        ${mono ? "font-mono text-xs" : ""}`}>
        {value || "—"}
      </p>
    </div>
  );
}

function Section({ title, icon: Icon, accentClass = "text-purple-400", borderClass = "border-purple-500/20", children }) {
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

function SoumettreRapportModal({ sinistre, onClose, onSuccess }) {
  const [form, setForm] = useState({ descriptionDommages: "", montantEstime: "", observations: "" });
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  const handleSubmit = async () => {
    if (!form.descriptionDommages || !form.montantEstime) {
      setError("Description et montant estimé sont obligatoires.");
      return;
    }
    setLoading(true); setError(null);
    try {
      await sinistreService.soumettreRapport(sinistre.id, {
        descriptionDommages: form.descriptionDommages,
        montantEstime: parseFloat(form.montantEstime),
        observations: form.observations,
      });
      onSuccess();
      onClose();
    } catch (e) {
      setError(e.response?.data?.message || "Erreur lors de la soumission.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/60 backdrop-blur-sm" onClick={onClose}>
      <div className="bg-[#111827] border border-white/10 rounded-2xl w-full max-w-lg p-6 shadow-2xl animate-[slideUp_0.2s_ease]" onClick={e => e.stopPropagation()}>
        <div className="flex justify-between items-start mb-6">
          <div>
            <h3 className="text-lg font-bold text-white">Soumettre le rapport</h3>
            <p className="text-xs text-slate-500 mt-0.5">Dossier #SP-{sinistre.id}</p>
          </div>
          <button onClick={onClose} className="text-slate-500 hover:text-white text-xl">×</button>
        </div>

        <label className="block text-[10px] font-bold uppercase text-slate-500 mb-2">Description des dommages *</label>
        <textarea rows={4} value={form.descriptionDommages} onChange={e => setForm(f => ({ ...f, descriptionDommages: e.target.value }))} className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white mb-4 outline-none focus:border-purple-500/60" />

        <label className="block text-[10px] font-bold uppercase text-slate-500 mb-2">Montant estimé (MAD) *</label>
        <input type="number" value={form.montantEstime} onChange={e => setForm(f => ({ ...f, montantEstime: e.target.value }))} className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white mb-4 outline-none focus:border-purple-500/60 font-mono" />

        <label className="block text-[10px] font-bold uppercase text-slate-500 mb-2">Observations (optionnel)</label>
        <textarea rows={3} value={form.observations} onChange={e => setForm(f => ({ ...f, observations: e.target.value }))} className="w-full bg-white/5 border border-white/10 rounded-xl px-4 py-3 text-sm text-white mb-4 outline-none focus:border-purple-500/60" />

        {error && <div className="bg-red-500/10 border border-red-500/20 rounded-xl px-4 py-3 mb-4 text-xs text-red-400">{error}</div>}

        <div className="flex gap-3 justify-end">
          <button onClick={onClose} className="px-5 py-2.5 rounded-xl text-xs text-slate-400 bg-white/5">Annuler</button>
          <button onClick={handleSubmit} disabled={loading} className="px-5 py-2.5 rounded-xl text-xs font-black bg-purple-500 text-black flex items-center gap-2 disabled:opacity-60">
            {loading ? <Loader2 size={14} className="animate-spin" /> : <Send size={14} />}
            {loading ? "Envoi…" : "Soumettre"}
          </button>
        </div>
      </div>
    </div>
  );
}

export function DetailSinistreExpert({ selectedSinistreId, setPage }) {
  const [sinistre, setSinistre] = useState(null);
  const [rapport, setRapport] = useState(null);
  const [decision, setDecision] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [showRapport, setShowRapport] = useState(false);
  const [downloading, setDownloading] = useState(false);

  const load = useCallback(async () => {
    if (!selectedSinistreId) return;
    setLoading(true); setError(null);
    try {
      const [s, r, d] = await Promise.allSettled([
        sinistreService.getById(selectedSinistreId),
        sinistreService.getRapport(selectedSinistreId),
        sinistreService.getDecision(selectedSinistreId),
      ]);
      if (s.status === "fulfilled") setSinistre(s.value);
      if (r.status === "fulfilled") setRapport(r.value);
      if (d.status === "fulfilled") setDecision(d.value);
    } catch {
      setError("Impossible de charger le dossier.");
    } finally {
      setLoading(false);
    }
  }, [selectedSinistreId]);

  useEffect(() => { load(); }, [load]);

  const handleDownloadPdf = async () => {
    setDownloading(true);
    try {
      await sinistreService.downloadPdf(
        `/api/rapports/${sinistre.id}/pdf`,
        `Rapport-Expertise-SP-${sinistre.id}.pdf`
      );
    } catch (err) {
      alert("Erreur lors du téléchargement du PDF");
    } finally {
      setDownloading(false);
    }
  };

  if (loading) return (
    <div className="min-h-screen bg-[#0a0f1a] flex items-center justify-center">
      <Loader2 size={36} className="text-purple-400 animate-spin" />
    </div>
  );

  if (error || !sinistre) return (
    <div className="min-h-screen bg-[#0a0f1a] flex items-center justify-center">
      <div className="text-center">
        <AlertCircle size={40} className="text-red-400 mx-auto mb-3" />
        <button onClick={() => setPage("ExpertDashboard")} className="text-white underline">← Retour</button>
      </div>
    </div>
  );

  const client = sinistre.client || {};
  const canSoumettreRapport = (sinistre.statut === "AFFECTE" || sinistre.statut === "EN_EXPERTISE") && !rapport;

  return (
    <div className="min-h-screen bg-[#0a0f1a] text-white">
      {showRapport && <SoumettreRapportModal sinistre={sinistre} onClose={() => setShowRapport(false)} onSuccess={load} />}

      <div className="sticky top-0 z-40 bg-[#0a0f1a]/90 backdrop-blur border-b border-white/5 px-8 py-4">
        <div className="max-w-6xl mx-auto flex items-center gap-4">
          <button onClick={() => setPage("ExpertDashboard")} className="p-2 bg-white/5 rounded-xl hover:bg-white/10"><ArrowLeft size={18} /></button>
          <div className="flex-1">
            <h1 className="text-lg font-black text-white">#SP-{sinistre.id} — {sinistre.typeSinistre?.replace(/_/g, " ")}</h1>
          </div>
          <StatusBadge statut={sinistre.statut} />
        </div>
      </div>

      <div className="max-w-6xl mx-auto px-8 py-8">
        <div className="bg-[#111827]/60 border border-white/5 rounded-2xl p-6 mb-6">
          <ProgressStepper statut={sinistre.statut} />
        </div>

        {canSoumettreRapport && (
          <button onClick={() => setShowRapport(true)} className="mb-6 flex items-center gap-2 px-5 py-3 bg-purple-500/10 border border-purple-500/20 rounded-xl text-xs font-black text-purple-400 uppercase tracking-widest hover:bg-purple-500/20">
            <Send size={15} /> Soumettre mon rapport
          </button>
        )}

        <div className="grid grid-cols-3 gap-6">
          <div className="col-span-2 space-y-4">
            <Section title="Infos Sinistre" icon={FileText}>
              <div className="grid grid-cols-2 gap-3">
                <InfoCard icon={AlertCircle} label="Type" value={sinistre.typeSinistre} highlight />
                <InfoCard icon={Calendar} label="Date" value={fmt(sinistre.dateIncident)} />
                <InfoCard icon={MapPin} label="Lieu" value={sinistre.lieuIncident} />
                <InfoCard icon={ShieldCheck} label="Police" value={sinistre.numeroPolicAssurance} mono />
              </div>
            </Section>

            {rapport && (
              <Section title="Votre rapport" icon={ClipboardList} accentClass="text-emerald-400" borderClass="border-emerald-500/20">
                <div className="grid grid-cols-2 gap-3 mb-4">
                  <InfoCard icon={Banknote} label="Estimé" value={fmtMoney(rapport.montantEstime)} highlight />
                  <InfoCard icon={Calendar} label="Soumis le" value={fmt(rapport.dateSoumission)} />
                </div>
                <button 
                  onClick={handleDownloadPdf}
                  disabled={downloading}
                  className="flex items-center gap-2 px-4 py-2.5 rounded-xl text-xs font-bold text-emerald-400 bg-emerald-500/10 hover:bg-emerald-500/20 border border-emerald-500/20 disabled:opacity-50"
                >
                  {downloading ? <Loader2 size={13} className="animate-spin" /> : <Download size={13} />}
                  {downloading ? "Téléchargement..." : "Télécharger PDF"}
                </button>
              </Section>
            )}
          </div>

          <div className="space-y-4">
            <Section title="Client" icon={User}>
              <div className="text-center">
                <div className="w-14 h-14 rounded-2xl bg-purple-500/10 mx-auto flex items-center justify-center text-xl font-black text-purple-400 mb-2">
                  {client.nom?.[0]}
                </div>
                <p className="font-bold text-sm">{client.prenom} {client.nom}</p>
                <p className="text-xs text-slate-500">{client.email}</p>
              </div>
            </Section>
          </div>
        </div>
      </div>
    </div>
  );
}