import React, { useState, useEffect } from 'react';
import { sinistreService } from '../services/sinistreService';
import {
  User, Mail, Phone, MapPin, Lock, Save,
  ArrowLeft, Loader2, CheckCircle2, XCircle, Eye, EyeOff
} from 'lucide-react';

export function ProfilPage({ user, setPage, onUserUpdate }) {
  const [profil,   setProfil]   = useState(null);
  const [loading,  setLoading]  = useState(true);
  const [activeTab, setActiveTab] = useState('infos');

  const [form, setForm] = useState({
    nom: '', prenom: '', telephone: '', adresse: ''
  });

  const [pwForm, setPwForm] = useState({
    ancienMotDePasse: '', nouveauMotDePasse: '', confirmation: ''
  });
  const [showPw, setShowPw] = useState({
    ancien: false, nouveau: false, confirm: false
  });

  // Feedback
  const [infoMsg,  setInfoMsg]  = useState(null);
  const [pwMsg,    setPwMsg]    = useState(null);
  const [saving,   setSaving]   = useState(false);

  useEffect(() => {
    const load = async () => {
      try {
        const data = await sinistreService.getProfil();
        setProfil(data);
        setForm({
          nom:       data.nom       || '',
          prenom:    data.prenom    || '',
          telephone: data.telephone || '',
          adresse:   data.adresse   || '',
        });
      } catch (err) {
        console.error("Erreur chargement profil:", err);
      } finally {
        setLoading(false);
      }
    };
    load();
  }, []);

  const handleSaveInfos = async (e) => {
    e.preventDefault();
    setSaving(true);
    setInfoMsg(null);
    try {
      const updated = await sinistreService.updateProfil(form);
      setProfil(updated);
      // Met à jour le nom dans la navbar si callback fourni
      if (onUserUpdate) onUserUpdate({ ...user, prenom: updated.prenom });
      setInfoMsg({ type: 'success', text: 'Profil mis à jour avec succès !' });
    } catch (err) {
      setInfoMsg({ type: 'error', text: 'Erreur lors de la mise à jour.' });
    } finally {
      setSaving(false);
    }
  };

  const handleChangePassword = async (e) => {
    e.preventDefault();
    setPwMsg(null);

    if (pwForm.nouveauMotDePasse !== pwForm.confirmation) {
      setPwMsg({ type: 'error', text: 'Les mots de passe ne correspondent pas.' });
      return;
    }
    if (pwForm.nouveauMotDePasse.length < 6) {
      setPwMsg({ type: 'error', text: 'Le mot de passe doit contenir au moins 6 caractères.' });
      return;
    }

    setSaving(true);
    try {
      await sinistreService.changePassword({
        ancienMotDePasse:  pwForm.ancienMotDePasse,
        nouveauMotDePasse: pwForm.nouveauMotDePasse,
      });
      setPwMsg({ type: 'success', text: 'Mot de passe modifié avec succès !' });
      setPwForm({ ancienMotDePasse: '', nouveauMotDePasse: '', confirmation: '' });
    } catch (err) {
      const msg = err.response?.data || 'Ancien mot de passe incorrect.';
      setPwMsg({ type: 'error', text: msg });
    } finally {
      setSaving(false);
    }
  };

  if (loading) return (
    <div className="flex justify-center items-center h-64">
      <Loader2 className="animate-spin text-cyan-500" size={40} />
    </div>
  );

  return (
    <div className="min-h-screen bg-[#0a0f1a] text-white p-8">
      <div className="max-w-3xl mx-auto">

        {/* Header */}
        <div className="flex items-center gap-4 mb-10">
         
          <div>
            <p className="text-cyan-500 text-xs font-bold uppercase tracking-widest mb-1">
              Mon compte
            </p>
            <h1 className="text-4xl font-extrabold">Mon Profil</h1>
          </div>
        </div>

        <div className="flex items-center gap-6 p-6 bg-[#111827]/60 border border-white/5 rounded-2xl mb-8">
          <div className="w-20 h-20 rounded-2xl bg-gradient-to-br from-cyan-500 to-blue-600 flex items-center justify-center text-3xl font-black text-white shrink-0">
            {profil?.prenom?.[0]?.toUpperCase()}{profil?.nom?.[0]?.toUpperCase()}
          </div>
          <div>
            <h2 className="text-2xl font-bold text-white">
              {profil?.prenom} {profil?.nom}
            </h2>
            <p className="text-slate-400 text-sm mt-1">{profil?.email}</p>
            <span className="mt-2 inline-block text-[10px] bg-cyan-500/10 text-cyan-400 px-3 py-1 rounded-full font-bold uppercase tracking-widest">
              {profil?.role}
            </span>
          </div>
          <div className="ml-auto text-right">
            <p className="text-[10px] text-slate-500 uppercase tracking-widest mb-1">Membre depuis</p>
            <p className="text-sm font-semibold text-white">
              {profil?.dateCreation
                ? new Date(profil.dateCreation).toLocaleDateString('fr-FR', {
                    month: 'long', year: 'numeric'
                  })
                : '—'}
            </p>
          </div>
        </div>

        {/* Tabs */}
        <div className="flex gap-2 mb-6">
          {[
            { key: 'infos',    label: 'Informations personnelles', icon: <User size={14} /> },
            { key: 'password', label: 'Mot de passe',              icon: <Lock size={14} /> },
          ].map(tab => (
            <button
              key={tab.key}
              onClick={() => { setActiveTab(tab.key); setInfoMsg(null); setPwMsg(null); }}
              className={`flex items-center gap-2 px-5 py-2.5 rounded-xl text-xs font-bold uppercase tracking-widest transition-all
                ${activeTab === tab.key
                  ? 'bg-cyan-500 text-black'
                  : 'bg-white/5 text-slate-400 hover:bg-white/10'}`}
            >
              {tab.icon} {tab.label}
            </button>
          ))}
        </div>

        {activeTab === 'infos' && (
          <form onSubmit={handleSaveInfos}>
            <div className="bg-[#111827]/60 border border-white/5 rounded-2xl p-6 space-y-5">

              <div className="grid grid-cols-2 gap-4">
                <InputField
                  label="Prénom"
                  icon={<User size={16} />}
                  value={form.prenom}
                  onChange={v => setForm(f => ({ ...f, prenom: v }))}
                  required
                />
                <InputField
                  label="Nom"
                  icon={<User size={16} />}
                  value={form.nom}
                  onChange={v => setForm(f => ({ ...f, nom: v }))}
                  required
                />
              </div>

              <InputField
                label="Email"
                icon={<Mail size={16} />}
                value={profil?.email || ''}
                disabled
                hint="L'email ne peut pas être modifié"
              />

              <InputField
                label="Téléphone"
                icon={<Phone size={16} />}
                value={form.telephone}
                onChange={v => setForm(f => ({ ...f, telephone: v }))}
                placeholder="+212 6XX XXX XXX"
              />

              <InputField
                label="Adresse"
                icon={<MapPin size={16} />}
                value={form.adresse}
                onChange={v => setForm(f => ({ ...f, adresse: v }))}
                placeholder="Votre adresse complète"
              />

              {/* Message feedback */}
              {infoMsg && <FeedbackMsg msg={infoMsg} />}

              <button
                type="submit"
                disabled={saving}
                className="w-full flex items-center justify-center gap-2 py-3 bg-cyan-600 hover:bg-cyan-500 disabled:bg-cyan-900 disabled:text-cyan-700 text-white font-bold rounded-xl transition-all"
              >
                {saving
                  ? <Loader2 size={16} className="animate-spin" />
                  : <Save size={16} />}
                {saving ? 'Sauvegarde...' : 'Sauvegarder les modifications'}
              </button>
            </div>
          </form>
        )}

        {activeTab === 'password' && (
          <form onSubmit={handleChangePassword}>
            <div className="bg-[#111827]/60 border border-white/5 rounded-2xl p-6 space-y-5">

              <PasswordField
                label="Ancien mot de passe"
                value={pwForm.ancienMotDePasse}
                show={showPw.ancien}
                onToggle={() => setShowPw(s => ({ ...s, ancien: !s.ancien }))}
                onChange={v => setPwForm(f => ({ ...f, ancienMotDePasse: v }))}
                required
              />

              <PasswordField
                label="Nouveau mot de passe"
                value={pwForm.nouveauMotDePasse}
                show={showPw.nouveau}
                onToggle={() => setShowPw(s => ({ ...s, nouveau: !s.nouveau }))}
                onChange={v => setPwForm(f => ({ ...f, nouveauMotDePasse: v }))}
                required
              />

              <PasswordField
                label="Confirmer le nouveau mot de passe"
                value={pwForm.confirmation}
                show={showPw.confirm}
                onToggle={() => setShowPw(s => ({ ...s, confirm: !s.confirm }))}
                onChange={v => setPwForm(f => ({ ...f, confirmation: v }))}
                required
              />

              {pwForm.nouveauMotDePasse && (
                <PasswordStrength password={pwForm.nouveauMotDePasse} />
              )}

              {pwMsg && <FeedbackMsg msg={pwMsg} />}

              <button
                type="submit"
                disabled={saving}
                className="w-full flex items-center justify-center gap-2 py-3 bg-orange-600 hover:bg-orange-500 disabled:bg-orange-900 disabled:text-orange-700 text-white font-bold rounded-xl transition-all"
              >
                {saving
                  ? <Loader2 size={16} className="animate-spin" />
                  : <Lock size={16} />}
                {saving ? 'Modification...' : 'Changer le mot de passe'}
              </button>
            </div>
          </form>
        )}

      </div>
    </div>
  );
}


function InputField({ label, icon, value, onChange, disabled, hint, placeholder, required }) {
  return (
    <div>
      <label className="text-[10px] text-slate-500 uppercase tracking-widest font-bold mb-2 block">
        {label}
      </label>
      <div className={`flex items-center gap-3 px-4 py-3 rounded-xl border transition-all
        ${disabled
          ? 'bg-white/3 border-white/5 cursor-not-allowed'
          : 'bg-white/5 border-white/10 hover:border-white/20 focus-within:border-cyan-500/50'}`}>
        <span className="text-slate-500 shrink-0">{icon}</span>
        <input
          type="text"
          value={value}
          onChange={e => onChange?.(e.target.value)}
          disabled={disabled}
          placeholder={placeholder}
          required={required}
          className="bg-transparent flex-1 text-sm text-white placeholder-slate-600 outline-none disabled:text-slate-500"
        />
      </div>
      {hint && <p className="text-[10px] text-slate-600 mt-1.5 ml-1">{hint}</p>}
    </div>
  );
}

function PasswordField({ label, value, show, onToggle, onChange, required }) {
  return (
    <div>
      <label className="text-[10px] text-slate-500 uppercase tracking-widest font-bold mb-2 block">
        {label}
      </label>
      <div className="flex items-center gap-3 px-4 py-3 bg-white/5 border border-white/10 hover:border-white/20 focus-within:border-cyan-500/50 rounded-xl transition-all">
        <span className="text-slate-500 shrink-0"><Lock size={16} /></span>
        <input
          type={show ? 'text' : 'password'}
          value={value}
          onChange={e => onChange(e.target.value)}
          required={required}
          className="bg-transparent flex-1 text-sm text-white placeholder-slate-600 outline-none"
        />
        <button
          type="button"
          onClick={onToggle}
          className="text-slate-500 hover:text-white transition-colors shrink-0"
        >
          {show ? <EyeOff size={16} /> : <Eye size={16} />}
        </button>
      </div>
    </div>
  );
}

function PasswordStrength({ password }) {
  const getStrength = (pw) => {
    let score = 0;
    if (pw.length >= 6)  score++;
    if (pw.length >= 10) score++;
    if (/[A-Z]/.test(pw)) score++;
    if (/[0-9]/.test(pw)) score++;
    if (/[^A-Za-z0-9]/.test(pw)) score++;
    return score;
  };

  const score = getStrength(password);
  const levels = [
    { label: 'Très faible', color: 'bg-red-500' },
    { label: 'Faible',      color: 'bg-orange-500' },
    { label: 'Moyen',       color: 'bg-yellow-500' },
    { label: 'Fort',        color: 'bg-cyan-500' },
    { label: 'Très fort',   color: 'bg-emerald-500' },
  ];
  const level = levels[Math.min(score - 1, 4)] || levels[0];

  return (
    <div>
      <div className="flex gap-1 mb-1">
        {[1,2,3,4,5].map(i => (
          <div
            key={i}
            className={`h-1 flex-1 rounded-full transition-all ${i <= score ? level.color : 'bg-white/10'}`}
          />
        ))}
      </div>
      <p className={`text-[10px] font-bold ${
        score <= 1 ? 'text-red-500' :
        score <= 2 ? 'text-orange-500' :
        score <= 3 ? 'text-yellow-500' :
        score <= 4 ? 'text-cyan-400' : 'text-emerald-400'
      }`}>
        Force : {level.label}
      </p>
    </div>
  );
}

function FeedbackMsg({ msg }) {
  return (
    <div className={`flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium
      ${msg.type === 'success'
        ? 'bg-emerald-500/10 text-emerald-400 border border-emerald-500/20'
        : 'bg-red-500/10 text-red-400 border border-red-500/20'}`}>
      {msg.type === 'success'
        ? <CheckCircle2 size={16} />
        : <XCircle size={16} />}
      {msg.text}
    </div>
  );
}