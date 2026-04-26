import React, { useState } from "react";
import { X, Send } from "lucide-react";
import { sinistreService } from "../services/sinistreService";

const INITIAL_FORM = {
  typeSinistre: "ACCIDENT",
  description: "",
  dateIncident: "",
  lieuIncident: "",
  numeroPolicAssurance: "",
  numeroConstatAmiable: "",
};

export function DeclarerSinistreModal({ isOpen, onClose, onSuccess }) {
  const [formData, setFormData] = useState(INITIAL_FORM);
  const [loading, setLoading]   = useState(false);

  if (!isOpen) return null;

  const handleChange = (e) =>
    setFormData((prev) => ({ ...prev, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      await sinistreService.declarerSinistre(formData);
      onSuccess();
      onClose();
      setFormData(INITIAL_FORM);
    } catch (error) {
      console.error("Erreur lors de la déclaration:", error);
      const errorMsg =
        error.response?.data?.message || "Erreur lors de l'envoi du dossier.";
      alert(errorMsg);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-[100] flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm">
      <div className="bg-white w-full max-w-lg rounded-3xl shadow-2xl overflow-hidden animate-in zoom-in duration-300">

        <div className="bg-slate-50 px-8 py-6 border-b border-slate-100 flex justify-between items-center">
          <div>
            <h2 className="text-xl font-bold text-slate-900">Déclarer un sinistre</h2>
            <p className="text-xs text-slate-500 uppercase tracking-widest font-semibold">
              Nouveau Dossier
            </p>
          </div>
          <button onClick={onClose} className="p-2 hover:bg-slate-200 rounded-full text-slate-400 transition-colors">
            <X size={20} />
          </button>
        </div>

        {/* Formulaire */}
        <form onSubmit={handleSubmit} className="p-8 space-y-5">

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-1">
              <label className="text-xs font-bold text-slate-700 ml-1">Type de sinistre</label>
              <select
                name="typeSinistre"
                value={formData.typeSinistre}
                onChange={handleChange}
                className="w-full p-3 bg-slate-100 border-none rounded-xl text-slate-900 focus:ring-2 focus:ring-orange-500 outline-none"
              >
                <option value="ACCIDENT">Accident</option>
                <option value="BRIS_DE_GLACE">Bris de glace</option>
                <option value="VOL">Vol</option>
                <option value="INCENDIE">Incendie</option>
              </select>
            </div>

            <div className="space-y-1">
              <label className="text-xs font-bold text-slate-700 ml-1">N° Police d'assurance</label>
              <input
                type="text"
                name="numeroPolicAssurance"
                placeholder="Ex: POL-1234"
                required
                value={formData.numeroPolicAssurance}
                onChange={handleChange}
                className="w-full p-3 bg-slate-100 border-none rounded-xl text-slate-900 focus:ring-2 focus:ring-orange-500 outline-none placeholder:text-slate-400"
              />
            </div>
          </div>

          <div className="space-y-1">
            <label className="text-xs font-bold text-slate-700 ml-1">N° Constat Amiable</label>
            <input
              type="text"
              name="numeroConstatAmiable"
              placeholder="Ex: CNT-9988"
              required
              value={formData.numeroConstatAmiable}
              onChange={handleChange}
              className="w-full p-3 bg-slate-100 border-none rounded-xl text-slate-900 focus:ring-2 focus:ring-orange-500 outline-none placeholder:text-slate-400"
            />
          </div>

          <div className="space-y-1">
            <label className="text-xs font-bold text-slate-700 ml-1">Date de l'incident</label>
            <input
              type="date"
              name="dateIncident"
              required
              value={formData.dateIncident}
              onChange={handleChange}
              className="w-full p-3 bg-slate-100 border-none rounded-xl text-slate-900 focus:ring-2 focus:ring-orange-500 outline-none"
            />
          </div>

          <div className="space-y-1">
            <label className="text-xs font-bold text-slate-700 ml-1">Lieu de l'incident</label>
            <input
              type="text"
              name="lieuIncident"
              placeholder="Ville, quartier..."
              required
              value={formData.lieuIncident}
              onChange={handleChange}
              className="w-full p-3 bg-slate-100 border-none rounded-xl text-slate-900 focus:ring-2 focus:ring-orange-500 outline-none"
            />
          </div>

          <div className="space-y-1">
            <label className="text-xs font-bold text-slate-700 ml-1">Description détaillée</label>
            <textarea
              rows="3"
              name="description"
              placeholder="Décrivez brièvement les circonstances..."
              required
              value={formData.description}
              onChange={handleChange}
              className="w-full p-3 bg-slate-100 border-none rounded-xl text-slate-900 focus:ring-2 focus:ring-orange-500 outline-none resize-none"
            />
          </div>

          <button
            type="submit"
            disabled={loading}
            className="w-full py-4 bg-orange-600 hover:bg-orange-500 text-white font-bold rounded-2xl transition-all shadow-lg shadow-orange-200 flex items-center justify-center gap-2 disabled:opacity-50"
          >
            {loading ? "Envoi en cours..." : <><Send size={18} /> Envoyer la déclaration</>}
          </button>
        </form>
      </div>
    </div>
  );
}