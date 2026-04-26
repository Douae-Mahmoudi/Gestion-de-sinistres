import React, { useState, useEffect } from 'react';
import { sinistreService } from '../services/sinistreService';
import {
  ArrowLeft, FileText, CheckCircle2, XCircle,
  Clock, MapPin, AlertCircle, Download, Loader2
} from 'lucide-react';

export function DetailSinistre({ sinistreId, onBack }) {
  const [sinistre, setSinistre] = useState(null);
  const [rapport, setRapport] = useState(null);
  const [decision, setDecision] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const id = parseInt(sinistreId, 10);

  useEffect(() => {
    const loadData = async () => {
      if (isNaN(id)) {
        setError("ID de sinistre invalide");
        setLoading(false);
        return;
      }

      setLoading(true);
      setError(null);

      try {
        const s = await sinistreService.getById(id);
        setSinistre(s);


        try {
          const r = await sinistreService.getRapport(id);
          setRapport(r);
        } catch (e) {
          console.log("Info: Aucun rapport pour le moment.");
        }

        try {
          const d = await sinistreService.getDecision(id);
          setDecision(d);
        } catch (e) {
          console.log("Info: Aucune décision pour le moment.");
        }

      } catch (err) {
        console.error("Erreur critique:", err);
        if (err.response?.status === 403) {
          setError("Accès refusé. Vous n'avez pas les droits pour voir ce dossier.");
        } else {
          setError("Impossible de charger les détails du sinistre.");
        }
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [id]);

  const downloadPdf = (path, filename) => {
    sinistreService.downloadPdf(path, filename);
  };

  if (loading) return (
    <div className="flex flex-col justify-center items-center h-64 gap-4">
      <Loader2 className="animate-spin text-cyan-500" size={40} />
      <p className="text-slate-400 text-sm animate-pulse">Chargement du dossier...</p>
    </div>
  );

  if (error) return (
    <div className="text-center py-20">
       <AlertCircle className="mx-auto text-red-500 mb-4" size={48} />
       <p className="text-white font-bold">{error}</p>
       <button onClick={onBack} className="mt-4 text-cyan-500 hover:underline">Retour au tableau de bord</button>
    </div>
  );

  if (!sinistre) return (
    <div className="text-center py-20 text-slate-500">Dossier introuvable.</div>
  );

  return (
    <div className="min-h-screen bg-[#0a0f1a] text-white p-8">
      <div className="max-w-4xl mx-auto space-y-8">

        <div className="flex flex-wrap items-center gap-4">
          <button
            onClick={onBack}
            className="p-2 bg-white/5 hover:bg-white/10 rounded-xl transition-all"
          >
            <ArrowLeft size={20} />
          </button>
          <div>
            <p className="text-cyan-500 text-xs font-bold uppercase tracking-widest">
              Dossier #SP-{id}
            </p>
            <h1 className="text-3xl font-extrabold">
              {sinistre.typeSinistre?.replace(/_/g, ' ')}
            </h1>
          </div>
          <div className="ml-auto">
            <span className={`text-xs px-4 py-2 rounded-full font-bold uppercase tracking-widest border ${getStatutStyle(sinistre.statut)}`}>
              • {sinistre.statut?.replace(/_/g, ' ')}
            </span>
          </div>
        </div>

        {/* Infos principales */}
        <Section title="Informations du sinistre" icon={<AlertCircle size={18} className="text-cyan-400" />}>
          <Grid>
            <InfoItem label="Référence" value={`#SP-${id}`} />
            <InfoItem label="Numéro police" value={sinistre.numeroPolicAssurance} />
            <InfoItem label="Date d'incident" value={formatDate(sinistre.dateIncident)} />
            <InfoItem label="Statut" value={sinistre.statut?.replace(/_/g, ' ')} />
          </Grid>
          <div className="mt-4 flex items-start gap-2 text-slate-400 text-sm">
            <MapPin size={16} className="mt-0.5 shrink-0 text-slate-500" />
            <span>{sinistre.lieuIncident || 'Lieu non spécifié'}</span>
          </div>
          {sinistre.description && (
            <p className="mt-3 text-sm text-slate-300 bg-white/5 rounded-xl p-4 leading-relaxed italic border border-white/5">
              "{sinistre.description}"
            </p>
          )}
        </Section>

        {/* Timeline */}
        <Section title="Progression du dossier" icon={<Clock size={18} className="text-orange-400" />}>
          <Timeline statut={sinistre.statut} hasRapport={!!rapport} hasDecision={!!decision} />
        </Section>

        {/* Rapport expert */}
        {rapport ? (
          <Section title="Rapport d'expertise" icon={<FileText size={18} className="text-purple-400" />}>
            <Grid>
              <InfoItem label="Expert" value={rapport.expertNom || rapport.expert?.nom} />
              <InfoItem label="Montant estimé" value={rapport.montantEstime ? `${rapport.montantEstime} MAD` : null} />
            </Grid>
            {rapport.descriptionDommages && (
              <div className="mt-4">
                <p className="text-[10px] text-slate-500 uppercase tracking-widest mb-2">Description des dommages</p>
                <p className="text-sm text-slate-300 bg-white/5 rounded-xl p-4 leading-relaxed">{rapport.descriptionDommages}</p>
              </div>
            )}
            <button
              onClick={() => downloadPdf(`/api/rapports/${id}/pdf`, `rapport-${id}.pdf`)}
              className="mt-4 flex items-center gap-2 px-4 py-2 bg-purple-500/10 hover:bg-purple-500/20 text-purple-400 rounded-xl text-xs font-bold uppercase tracking-widest transition-all"
            >
              <Download size={14} /> Télécharger le rapport PDF
            </button>
          </Section>
        ) : (
          <PendingCard 
            title="Rapport d'expertise" 
            message={sinistre.statut === 'DECLARE' ? "En attente d'affectation d'un expert." : "Expertise en cours..."} 
          />
        )}

        {/* Décision */}
        {decision ? (
          <Section 
            title="Décision finale" 
            icon={decision.statut === 'REJETE' ? <XCircle size={18} className="text-red-400" /> : <CheckCircle2 size={18} className="text-emerald-400" />}
          >
            <Grid>
              <InfoItem label="Montant accordé" value={decision.montantFinal ? `${decision.montantFinal} MAD` : null} />
              <InfoItem label="Date de paiement" value={formatDate(decision.datePaiement)} />
              <InfoItem label="N° virement" value={decision.numeroVirement} />
            </Grid>
            {decision.motif && (
              <div className="mt-4">
                <p className="text-[10px] text-slate-500 uppercase tracking-widest mb-2">Commentaire de la compagnie</p>
                <p className="text-sm text-slate-300 bg-white/5 rounded-xl p-4 leading-relaxed">{decision.motif}</p>
              </div>
            )}
            <button
              onClick={() => downloadPdf(`/api/decisions/${id}/pdf`, `decision-${id}.pdf`)}
              className="mt-4 flex items-center gap-2 px-4 py-2 bg-emerald-500/10 hover:bg-emerald-500/20 text-emerald-400 rounded-xl text-xs font-bold uppercase tracking-widest transition-all"
            >
              <Download size={14} /> Lettre de décision PDF
            </button>
          </Section>
        ) : (
          <PendingCard title="Décision finale" message="En attente de traitement par nos services." />
        )}

      </div>
    </div>
  );
}


function Section({ title, icon, children }) {
  return (
    <div className="bg-[#111827]/60 border border-white/5 rounded-2xl p-6 backdrop-blur-sm">
      <div className="flex items-center gap-2 mb-5">
        {icon}
        <h2 className="text-sm font-bold uppercase tracking-widest text-slate-300">{title}</h2>
      </div>
      {children}
    </div>
  );
}

function Grid({ children }) {
  return <div className="grid grid-cols-2 md:grid-cols-4 gap-6">{children}</div>;
}

function InfoItem({ label, value }) {
  return (
    <div>
      <p className="text-[10px] text-slate-500 uppercase tracking-widest mb-1">{label}</p>
      <p className="text-sm font-semibold text-white">{value || '—'}</p>
    </div>
  );
}

function PendingCard({ title, message }) {
  return (
    <div className="border-2 border-dashed border-white/5 rounded-2xl p-8 text-center bg-white/[0.01]">
      <Clock size={24} className="text-slate-600 mx-auto mb-2" />
      <p className="text-sm font-bold text-slate-400">{title}</p>
      <p className="text-xs text-slate-600 mt-1 uppercase tracking-tighter">{message}</p>
    </div>
  );
}

function Timeline({ statut, hasRapport, hasDecision }) {
  const steps = [
    { label: 'Déclaré', done: true },
    { label: 'Affecté', done: ['AFFECTE','EN_EXPERTISE','EVALUE','APPROUVE','REJETE','CLOTURE'].includes(statut) },
    { label: 'Expertise', done: hasRapport || ['EVALUE','APPROUVE','REJETE','CLOTURE'].includes(statut) },
    { label: 'Décision', done: hasDecision || ['APPROUVE','REJETE','CLOTURE'].includes(statut) },
    { label: 'Clôturé', done: statut === 'CLOTURE' },
  ];

  return (
    <div className="flex items-center justify-between gap-2 overflow-x-auto pb-2">
      {steps.map((step, i) => (
        <React.Fragment key={i}>
          <div className="flex flex-col items-center gap-2 min-w-[70px]">
            <div className={`w-8 h-8 rounded-full flex items-center justify-center text-xs font-bold transition-all duration-500
              ${step.done ? 'bg-cyan-500 text-black shadow-[0_0_15px_rgba(6,182,212,0.4)]' : 'bg-white/5 text-slate-600'}`}>
              {step.done ? '✓' : i + 1}
            </div>
            <p className={`text-[10px] uppercase tracking-widest font-bold text-center ${step.done ? 'text-cyan-400' : 'text-slate-600'}`}>
              {step.label}
            </p>
          </div>
          {i < steps.length - 1 && (
            <div className={`h-[2px] flex-1 mb-4 min-w-[20px] rounded-full ${step.done && steps[i+1].done ? 'bg-cyan-500/60' : 'bg-white/5'}`} />
          )}
        </React.Fragment>
      ))}
    </div>
  );
}

function formatDate(date) {
  if (!date) return '—';
  return new Date(date).toLocaleDateString('fr-FR', {
    day: '2-digit', month: 'short', year: 'numeric'
  });
}

function getStatutStyle(statut) {
  switch (statut) {
    case 'DECLARE':      return 'border-cyan-500/20 bg-cyan-500/10 text-cyan-400';
    case 'AFFECTE':      return 'border-blue-500/20 bg-blue-500/10 text-blue-400';
    case 'EN_EXPERTISE': return 'border-purple-500/20 bg-purple-500/10 text-purple-400';
    case 'EVALUE':       return 'border-yellow-500/20 bg-yellow-500/10 text-yellow-400';
    case 'APPROUVE':     return 'border-emerald-500/20 bg-emerald-500/10 text-emerald-400';
    case 'REJETE':       return 'border-red-500/20 bg-red-500/10 text-red-400';
    case 'CLOTURE':      return 'border-slate-500/20 bg-slate-500/10 text-slate-400';
    default:             return 'border-white/10 bg-white/5 text-white';
  }
}