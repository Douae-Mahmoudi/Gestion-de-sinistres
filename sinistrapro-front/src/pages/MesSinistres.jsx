import React, { useState, useEffect } from 'react';
import { sinistreService } from '../services/sinistreService';
import { Loader2, MapPin, ChevronRight, AlertCircle } from 'lucide-react';

export function MesSinistres({ setPage, setSelectedSinistreId }) {
    const [sinistres, setSinistres] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const loadSinistres = async () => {
            try {
                const data = await sinistreService.getMesSinistres();
                setSinistres(data);
            } catch (error) {
                console.error("Erreur lors de la récupération des sinistres:", error);
            } finally {
                setLoading(false);
            }
        };
        loadSinistres();
    }, []);

    const dossiersActifs = sinistres.filter(s =>
        s.statut !== 'CLOTURE' && s.statut !== 'REJETE'
    ).length;

    if (loading) {
        return (
            <div className="flex justify-center items-center h-64">
                <Loader2 className="animate-spin text-cyan-500" size={48} />
            </div>
        );
    }

    return (
        <div className="w-full text-white">
            <div className="max-w-7xl mx-auto">

                {/* Header */}
                <div className="flex justify-between items-end mb-12">
                    <div>
                        <p className="text-cyan-500 text-xs font-bold uppercase tracking-widest mb-2">
                            Archive de Gestion
                        </p>
                        <h1 className="text-5xl font-extrabold mb-4">Mes Sinistres</h1>
                        <p className="text-slate-400 max-w-md">
                            Suivi en temps réel de vos dossiers d'indemnisation. Une transparence totale pour une résolution accélérée.
                        </p>
                    </div>
                    <div className="flex gap-4">
                        <div className="bg-slate-900/50 border border-white/5 p-6 rounded-2xl text-center min-w-[140px]">
                            <p className="text-slate-500 text-[10px] uppercase font-bold mb-1">Dossiers Actifs</p>
                            <p className="text-4xl font-black text-cyan-400">
                                {dossiersActifs < 10 ? `0${dossiersActifs}` : dossiersActifs}
                            </p>
                        </div>
                    </div>
                </div>

                {/* Table Header */}
                <div className="grid grid-cols-5 px-6 mb-4 text-[10px] uppercase tracking-widest font-bold text-slate-500">
                    <div>Date du sinistre</div>
                    <div>Type d'incident</div>
                    <div>Localisation</div>
                    <div>État du dossier</div>
                    <div className="text-right">Actions</div>
                </div>

                {/* Liste */}
                <div className="space-y-4">
                    {sinistres.map((s) => (
                        <div
                            key={s.id}
                            className="grid grid-cols-5 items-center p-6 bg-slate-900/40 border border-white/5 rounded-2xl hover:bg-slate-900/60 transition-all group"
                        >
                            {/* Date */}
                            <div>
                                <p className="font-bold text-sm text-slate-200">
                                    {new Date(s.dateIncident).toLocaleDateString('fr-FR', {
                                        day: '2-digit', month: 'short', year: 'numeric'
                                    })}
                                </p>
                                <p className="text-[10px] text-slate-500 font-mono">RÉF: #SP-{s.id}</p>
                            </div>

                            {/* Type */}
                            <div className="flex items-center gap-3">
                                <div className="p-2 bg-slate-800 rounded-lg text-cyan-400 group-hover:bg-cyan-500/10 transition-colors">
                                    <AlertCircle size={18} />
                                </div>
                                <p className="font-semibold text-sm">
                                    {s.typeSinistre?.replace(/_/g, ' ')}
                                </p>
                            </div>

                            {/* Localisation */}
                            <div className="flex items-center gap-2 text-slate-400">
                                <MapPin size={14} className="text-slate-500" />
                                <p className="text-xs truncate pr-4">{s.lieuIncident || "Non spécifié"}</p>
                            </div>

                            <div>
                                <span className={`text-[10px] px-3 py-1 rounded-full font-bold uppercase tracking-tighter border border-transparent ${getStatutStyle(s.statut)}`}>
                                    • {s.statut?.replace(/_/g, ' ')}
                                </span>
                            </div>

                            <div className="flex justify-end">
                                <button
                                    onClick={() => {
                                        setSelectedSinistreId(Number(s.id));
                                        setPage("DetailSinistre");
                                    }}
                                    className="flex items-center gap-1 text-[10px] font-black uppercase tracking-widest text-white group-hover:text-cyan-400 transition-colors"
                                >
                                    Détails <ChevronRight size={14} />
                                </button>
                            </div>
                        </div>
                    ))}

                    {sinistres.length === 0 && (
                        <div className="text-center py-20 bg-slate-900/20 border-2 border-dashed border-white/5 rounded-3xl">
                            <p className="text-slate-500">Aucun dossier trouvé dans votre historique.</p>
                        </div>
                    )}
                </div>
            </div>
        </div>
    );
}

function getStatutStyle(statut) {
    switch (statut) {
        case 'DECLARE':      return 'bg-cyan-500/10 text-cyan-400 border-cyan-500/20';
        case 'AFFECTE':      return 'bg-blue-500/10 text-blue-400 border-blue-500/20';
        case 'EN_EXPERTISE': return 'bg-purple-500/10 text-purple-400 border-purple-500/20';
        case 'EVALUE':       return 'bg-yellow-500/10 text-yellow-400 border-yellow-500/20';
        case 'APPROUVE':     return 'bg-emerald-500/10 text-emerald-400 border-emerald-500/20';
        case 'REJETE':       return 'bg-red-500/10 text-red-400 border-red-500/20';
        case 'CLOTURE':      return 'bg-slate-500/10 text-slate-400 border-slate-500/20';
        default:             return 'bg-white/5 text-white';
    }
}