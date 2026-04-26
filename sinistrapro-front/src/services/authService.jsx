const API_URL = "http://localhost:8100/api/auth";

export const authService = {

  // Login
  async login(email, motDePasse) {
    const response = await fetch(`${API_URL}/login`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, motDePasse }),
    });
    if (!response.ok) {
      const msg = await response.text();
      throw { response: { data: msg || "Identifiants invalides" } };
    }
    return response.json();
  },

  //  Register
  async register(userData) {
    const response = await fetch(`${API_URL}/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(userData),
    });
    if (!response.ok) {
      const msg = await response.text();
      throw { response: { data: msg || "Erreur lors de l'inscription" } };
    }
    return response.json();
  },

  async forgotPassword(email) {
    const response = await fetch(`${API_URL}/forgot-password`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email }),
    });
    const msg = await response.text();
    if (!response.ok) {
      throw { response: { data: msg || "Email introuvable." } };
    }
    return msg;
  },

  async verifyCode(email, code) {
    const response = await fetch(`${API_URL}/verify-code`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, code }),
    });
    const msg = await response.text();
    if (!response.ok) {
      throw { response: { data: msg || "Code incorrect ou expiré." } };
    }
    return msg;
  },

  async resetPassword(email, code, nouveauMotDePasse) {
    const response = await fetch(`${API_URL}/reset-password`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email, code, nouveauMotDePasse }),
    });
    const msg = await response.text();
    if (!response.ok) {
      throw { response: { data: msg || "Erreur lors de la réinitialisation." } };
    }
    return msg;
  },
};