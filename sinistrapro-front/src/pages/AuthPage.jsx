import React, { useState, useEffect } from "react";
import { authService } from "../services/authService";
import { Eye, EyeOff, ArrowLeft } from "lucide-react";

export function AuthPage({ onLoginSuccess }) {
  const [mode, setMode] = useState("login");
  const [formData, setFormData] = useState({
    email: "", password: "", nom: "", prenom: "", telephone: ""
  });
  const [resetEmail, setResetEmail]     = useState("");
  const [code,       setCode]           = useState(["", "", "", "", "", ""]);
  const [newPassword, setNewPassword]   = useState("");
  const [showPw,     setShowPw]         = useState(false);
  const [error,      setError]          = useState("");
  const [message,    setMessage]        = useState("");
  const [isLoading,  setIsLoading]      = useState(false);

  useEffect(() => {
    setError("");
    setMessage("");
  }, [mode]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  // ── Gestion du code à 6 chiffres ─────────────────────
  const handleCodeChange = (index, value) => {
    if (!/^\d?$/.test(value)) return; // chiffres seulement
    const newCode = [...code];
    newCode[index] = value;
    setCode(newCode);
    // Auto-focus suivant
    if (value && index < 5) {
      document.getElementById(`code-${index + 1}`)?.focus();
    }
  };

  const handleCodeKeyDown = (index, e) => {
    if (e.key === "Backspace" && !code[index] && index > 0) {
      document.getElementById(`code-${index - 1}`)?.focus();
    }
  };

  const handleCodePaste = (e) => {
    const pasted = e.clipboardData.getData("text").replace(/\D/g, "").slice(0, 6);
    if (pasted.length === 6) {
      setCode(pasted.split(""));
      document.getElementById("code-5")?.focus();
    }
    e.preventDefault();
  };

  // ── Submit login / register ───────────────────────────
  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setMessage("");
    setIsLoading(true);
    try {
      if (mode === "login") {
        const data = await authService.login(formData.email, formData.password);
        localStorage.setItem("token", data.token);
        onLoginSuccess(data);
      } else if (mode === "register") {
        await authService.register({
          nom:        formData.nom,
          prenom:     formData.prenom,
          email:      formData.email,
          motDePasse: formData.password,
          telephone:  formData.telephone,
        });
        setMode("login");
        setMessage("Compte créé avec succès ! Vous pouvez vous connecter.");
      }
    } catch (err) {
      setError(err.response?.data || "Une erreur est survenue. Vérifiez vos informations.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleForgot = async (e) => {
    e.preventDefault();
    setError("");
    setIsLoading(true);
    try {
      await authService.forgotPassword(resetEmail);
      setMode("verify");
      setMessage(`Un code à 6 chiffres a été envoyé à ${resetEmail}`);
    } catch (err) {
      setError(err.response?.data || "Email introuvable.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleVerify = async (e) => {
    e.preventDefault();
    setError("");
    const fullCode = code.join("");
    if (fullCode.length < 6) {
      setError("Veuillez entrer le code complet à 6 chiffres.");
      return;
    }
    setIsLoading(true);
    try {
      await authService.verifyCode(resetEmail, fullCode);
      setMode("reset");
      setMessage("");
    } catch (err) {
      setError(err.response?.data || "Code incorrect ou expiré.");
    } finally {
      setIsLoading(false);
    }
  };

  const handleReset = async (e) => {
    e.preventDefault();
    setError("");
    if (newPassword.length < 6) {
      setError("Le mot de passe doit contenir au moins 6 caractères.");
      return;
    }
    setIsLoading(true);
    try {
      await authService.resetPassword(resetEmail, code.join(""), newPassword);
      setMode("login");
      setMessage("Mot de passe réinitialisé avec succès ! Connectez-vous.");
    } catch (err) {
      setError(err.response?.data || "Erreur lors de la réinitialisation.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex items-center justify-center px-4 py-10">
      <div className="w-full max-w-md bg-slate-900/80 border border-white/10 p-8 rounded-3xl backdrop-blur-xl shadow-2xl">

        {/* Header */}
        <div className="mb-8 text-center">
          {(mode === "forgot" || mode === "verify" || mode === "reset") && (
            <button
              onClick={() => { setMode("login"); setCode(["","","","","",""]); setResetEmail(""); }}
              className="flex items-center gap-1 text-slate-400 hover:text-white text-xs mb-4 transition-colors mx-auto"
            >
              <ArrowLeft size={14} /> Retour à la connexion
            </button>
          )}

          <h2 className="text-3xl font-bold text-white mb-2">
            {mode === "login"    ? "Bon retour"              :
             mode === "register" ? "Créer un compte"         :
             mode === "forgot"   ? "Mot de passe oublié"     :
             mode === "verify"   ? "Vérification du code"    :
                                   "Nouveau mot de passe"}
          </h2>
          <p className="text-slate-400 text-sm italic">
            {mode === "login"    ? "Accédez à votre espace SinistraPro"    :
             mode === "register" ? "Rejoignez-nous pour gérer vos sinistres" :
             mode === "forgot"   ? "Entrez votre email pour recevoir un code" :
             mode === "verify"   ? "Entrez le code reçu par email"           :
                                   "Choisissez un nouveau mot de passe"}
          </p>

          {(mode === "forgot" || mode === "verify" || mode === "reset") && (
            <div className="flex items-center justify-center gap-2 mt-4">
              {["forgot", "verify", "reset"].map((step, i) => (
                <React.Fragment key={step}>
                  <div className={`w-7 h-7 rounded-full flex items-center justify-center text-[10px] font-black transition-all
                    ${mode === step
                      ? "bg-cyan-500 text-black"
                      : ["forgot","verify","reset"].indexOf(mode) > i
                        ? "bg-emerald-500 text-black"
                        : "bg-white/10 text-slate-500"}`}>
                    {["forgot","verify","reset"].indexOf(mode) > i ? "✓" : i + 1}
                  </div>
                  {i < 2 && (
                    <div className={`h-px w-8 transition-all
                      ${["forgot","verify","reset"].indexOf(mode) > i
                        ? "bg-emerald-500" : "bg-white/10"}`}
                    />
                  )}
                </React.Fragment>
              ))}
            </div>
          )}
        </div>

        {/* Messages */}
        {error && (
          <div className="bg-red-500/10 border border-red-500/30 text-red-400 p-3 rounded-xl mb-5 text-xs text-center">
            {error}
          </div>
        )}
        {message && (
          <div className="bg-emerald-500/10 border border-emerald-500/30 text-emerald-400 p-3 rounded-xl mb-5 text-xs text-center">
            {message}
          </div>
        )}

        {mode === "forgot" && (
          <form onSubmit={handleForgot} className="space-y-4">
            <input
              type="email"
              value={resetEmail}
              onChange={e => setResetEmail(e.target.value)}
              placeholder="Votre adresse email"
              required
              className="w-full bg-slate-950 border border-white/10 text-white p-4 rounded-xl focus:ring-2 focus:ring-cyan-500 outline-none transition-all"
            />
            <button
              type="submit"
              disabled={isLoading}
              className="w-full py-4 bg-gradient-to-r from-cyan-500 to-blue-600 text-white font-bold rounded-xl hover:brightness-110 active:scale-95 transition-all disabled:opacity-50"
            >
              {isLoading ? "Envoi en cours..." : "Envoyer le code"}
            </button>
          </form>
        )}

        {mode === "verify" && (
          <form onSubmit={handleVerify} className="space-y-6">
            <div className="flex justify-center gap-3">
              {code.map((digit, i) => (
                <input
                  key={i}
                  id={`code-${i}`}
                  type="text"
                  inputMode="numeric"
                  maxLength={1}
                  value={digit}
                  onChange={e => handleCodeChange(i, e.target.value)}
                  onKeyDown={e => handleCodeKeyDown(i, e)}
                  onPaste={i === 0 ? handleCodePaste : undefined}
                  className="w-12 h-14 text-center text-2xl font-black bg-slate-950 border-2 border-white/10 focus:border-cyan-500 text-white rounded-xl outline-none transition-all"
                />
              ))}
            </div>
            <button
              type="submit"
              disabled={isLoading || code.join("").length < 6}
              className="w-full py-4 bg-gradient-to-r from-cyan-500 to-blue-600 text-white font-bold rounded-xl hover:brightness-110 active:scale-95 transition-all disabled:opacity-50"
            >
              {isLoading ? "Vérification..." : "Vérifier le code"}
            </button>
            <button
              type="button"
              onClick={handleForgot}
              className="w-full text-xs text-slate-500 hover:text-cyan-400 transition-colors"
            >
              Renvoyer le code
            </button>
          </form>
        )}

        {mode === "reset" && (
          <form onSubmit={handleReset} className="space-y-4">
            <div className="relative">
              <input
                type={showPw ? "text" : "password"}
                value={newPassword}
                onChange={e => setNewPassword(e.target.value)}
                placeholder="Nouveau mot de passe"
                minLength={6}
                required
                className="w-full bg-slate-950 border border-white/10 text-white p-4 pr-12 rounded-xl focus:ring-2 focus:ring-cyan-500 outline-none transition-all"
              />
              <button
                type="button"
                onClick={() => setShowPw(s => !s)}
                className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500 hover:text-white transition-colors"
              >
                {showPw ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>

            {newPassword && (
              <div>
                <div className="flex gap-1 mb-1">
                  {[1,2,3,4,5].map(i => {
                    const score = [
                      newPassword.length >= 6,
                      newPassword.length >= 10,
                      /[A-Z]/.test(newPassword),
                      /[0-9]/.test(newPassword),
                      /[^A-Za-z0-9]/.test(newPassword),
                    ].filter(Boolean).length;
                    return (
                      <div key={i} className={`h-1 flex-1 rounded-full transition-all
                        ${i <= score
                          ? score <= 2 ? "bg-red-500"
                          : score <= 3 ? "bg-yellow-500"
                          : "bg-emerald-500"
                          : "bg-white/10"}`}
                      />
                    );
                  })}
                </div>
              </div>
            )}

            <button
              type="submit"
              disabled={isLoading}
              className="w-full py-4 bg-gradient-to-r from-orange-500 to-red-600 text-white font-bold rounded-xl hover:brightness-110 active:scale-95 transition-all disabled:opacity-50"
            >
              {isLoading ? "Réinitialisation..." : "Réinitialiser le mot de passe"}
            </button>
          </form>
        )}

        {mode === "login" && (
          <form onSubmit={handleSubmit} className="space-y-4">
            <input
              name="email" type="email" placeholder="Email" required
              className="w-full bg-slate-950 border border-white/10 text-white p-4 rounded-xl focus:ring-2 focus:ring-orange-500 outline-none transition-all"
              onChange={handleChange}
            />
            <div className="relative">
              <input
                name="password" type={showPw ? "text" : "password"}
                placeholder="Mot de passe" required minLength={6}
                className="w-full bg-slate-950 border border-white/10 text-white p-4 pr-12 rounded-xl focus:ring-2 focus:ring-orange-500 outline-none transition-all"
                onChange={handleChange}
              />
              <button type="button" onClick={() => setShowPw(s => !s)}
                className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500 hover:text-white transition-colors">
                {showPw ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
            <button
              type="button"
              onClick={() => setMode("forgot")}
              className="text-xs text-cyan-400 hover:underline block ml-auto"
            >
              Mot de passe oublié ?
            </button>
            <button
              type="submit" disabled={isLoading}
              className="w-full py-4 bg-gradient-to-r from-orange-500 to-red-600 text-white font-bold rounded-xl hover:brightness-110 active:scale-95 transition-all disabled:opacity-50"
            >
              {isLoading ? "Connexion..." : "Se connecter"}
            </button>
          </form>
        )}

        {mode === "register" && (
          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="grid grid-cols-2 gap-4">
              <input
                name="nom" type="text" placeholder="Nom" required
                className="bg-slate-950 border border-white/10 text-white p-3 rounded-xl focus:ring-2 focus:ring-orange-500 outline-none"
                onChange={handleChange}
              />
              <input
                name="prenom" type="text" placeholder="Prénom" required
                className="bg-slate-950 border border-white/10 text-white p-3 rounded-xl focus:ring-2 focus:ring-orange-500 outline-none"
                onChange={handleChange}
              />
            </div>
            <input
              name="telephone" type="tel" placeholder="Téléphone" required
              className="w-full bg-slate-950 border border-white/10 text-white p-3 rounded-xl focus:ring-2 focus:ring-orange-500 outline-none"
              onChange={handleChange}
            />
            <input
              name="email" type="email" placeholder="Email" required
              className="w-full bg-slate-950 border border-white/10 text-white p-4 rounded-xl focus:ring-2 focus:ring-orange-500 outline-none transition-all"
              onChange={handleChange}
            />
            <div className="relative">
              <input
                name="password" type={showPw ? "text" : "password"}
                placeholder="Mot de passe" required minLength={6}
                className="w-full bg-slate-950 border border-white/10 text-white p-4 pr-12 rounded-xl focus:ring-2 focus:ring-orange-500 outline-none transition-all"
                onChange={handleChange}
              />
              <button type="button" onClick={() => setShowPw(s => !s)}
                className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-500 hover:text-white transition-colors">
                {showPw ? <EyeOff size={18} /> : <Eye size={18} />}
              </button>
            </div>
            <button
              type="submit" disabled={isLoading}
              className="w-full py-4 bg-gradient-to-r from-orange-500 to-red-600 text-white font-bold rounded-xl hover:brightness-110 active:scale-95 transition-all disabled:opacity-50"
            >
              {isLoading ? "Création..." : "S'inscrire"}
            </button>
          </form>
        )}

        {(mode === "login" || mode === "register") && (
          <div className="mt-8 text-center text-sm text-slate-400">
            {mode === "login" ? (
              <p>
                Pas encore membre ?{" "}
                <button onClick={() => setMode("register")} className="text-cyan-400 font-bold hover:underline">
                  Créer un compte
                </button>
              </p>
            ) : (
              <button onClick={() => setMode("login")} className="text-cyan-400 font-bold hover:underline">
                Retour à la connexion
              </button>
            )}
          </div>
        )}
      </div>
    </div>
  );
}