import React, { useState, useEffect } from "react";
import { Navbar }               from "./components/Navbar";
import { ServicesPage }         from "./pages/ServicesPage";
import { FonctionnementPage }   from "./pages/FonctionnementPage";
import { AuthPage }             from "./pages/AuthPage";
import { ClientDashboard }      from "./pages/ClientDashboard";
import { MesSinistres }         from "./pages/MesSinistres";
import { DetailSinistre }       from "./pages/DetailSinistre";
import { NotificationsPage }    from "./pages/NotificationsPage";
import { ProfilPage }           from "./pages/ProfilPage";
import { AgentDashboard }       from "./pages/AgentDashboard";
import { GestionSinistres }     from "./pages/GestionSinistres";
import { DetailSinistreAgent }  from "./pages/Detailsinistreagent";
import { ExpertDashboard }      from "./pages/ExpertDashboard";
import { MissionsExpert }       from "./pages/Missionsexpert";
import { DetailSinistreExpert } from "./pages/DetailSinistreExpert";
import {
  DossiersSuperviseur,
  DetailSinistreSuperviseur,
}                               from "./pages/Superviseurpages";

export default function App() {

  const [page, setPage] = useState(() =>
    localStorage.getItem("lastPage") || "Services"
  );
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem("user");
    return saved ? JSON.parse(saved) : null;
  });
  const [selectedSinistreId, setSelectedSinistreId] = useState(null);

  useEffect(() => {
    localStorage.setItem("lastPage", page);
  }, [page]);

  const handleLoginSuccess = (userData) => {
    setUser(userData);
    localStorage.setItem("user", JSON.stringify(userData));
    switch (userData.role) {
      case "CLIENT":      setPage("ClientDashboard");     break;
      case "AGENT":       setPage("AgentDashboard");      break;
      case "EXPERT":      setPage("ExpertDashboard");     break;
      case "SUPERVISEUR": setPage("DossiersSuperviseur"); break;
      default:            setPage("Services");
    }
  };

  const handleLogout = () => {
    localStorage.clear();
    setUser(null);
    setPage("Services");
  };

  const handleSelectSinistre = (id) => setSelectedSinistreId(Number(id));

  const handleUserUpdate = (updatedUser) => {
    setUser(updatedUser);
    localStorage.setItem("user", JSON.stringify(updatedUser));
  };

  const homePage = () => {
    switch (user?.role) {
      case "AGENT":       return "AgentDashboard";
      case "EXPERT":      return "ExpertDashboard";
      case "SUPERVISEUR": return "DossiersSuperviseur";
      default:            return "ClientDashboard";
    }
  };

  return (
    <div className="bg-[#060c1c] min-h-screen text-white font-sans selection:bg-orange-500/30">
      <Navbar page={page} setPage={setPage} user={user} onLogout={handleLogout} />

      {page === "Services" && <ServicesPage setPage={setPage} />}

      <main className={`${page === "Services" ? "hidden" : "pt-24 pb-10 container mx-auto px-4"}`}>

        {page === "Fonctionnement" && <FonctionnementPage />}
        {page === "Login" && !user && <AuthPage onLoginSuccess={handleLoginSuccess} />}

        {user?.role === "CLIENT" && (
          <>
            {page === "ClientDashboard" && (
              <ClientDashboard user={user} setPage={setPage} />
            )}
            {page === "MesSinistres" && (
              <MesSinistres setPage={setPage} setSelectedSinistreId={handleSelectSinistre} />
            )}
            {page === "DetailSinistre" && selectedSinistreId && !isNaN(selectedSinistreId) && (
              <DetailSinistre
                sinistreId={Number(selectedSinistreId)}
                onBack={() => setPage("MesSinistres")}
              />
            )}
            {page === "Notifications" && (
              <NotificationsPage setPage={setPage} backPage="ClientDashboard" />
            )}
            {page === "Profil" && (
              <ProfilPage user={user} setPage={setPage} onUserUpdate={handleUserUpdate} />
            )}
            {page === "DeclarerSinistre" && (
              <div className="p-8 bg-slate-900/50 rounded-3xl border border-white/10 backdrop-blur-md">
                <h2 className="text-2xl font-bold text-orange-500 mb-6">Nouvelle Déclaration</h2>
                <p className="text-slate-400 italic">Formulaire en cours de développement…</p>
                <button onClick={() => setPage("ClientDashboard")}
                  className="mt-4 text-sm text-cyan-400 underline">
                  Retour au tableau de bord
                </button>
              </div>
            )}
          </>
        )}

        {user?.role === "AGENT" && (
          <>
            {page === "AgentDashboard" && (
              <AgentDashboard user={user} setPage={setPage} setSelectedSinistreId={handleSelectSinistre} />
            )}
            {page === "GestionSinistres" && (
              <GestionSinistres setPage={setPage} setSelectedSinistreId={handleSelectSinistre} />
            )}
            {page === "DetailSinistreAgent" && selectedSinistreId && !isNaN(selectedSinistreId) && (
              <DetailSinistreAgent selectedSinistreId={Number(selectedSinistreId)} setPage={setPage} />
            )}
            {page === "Notifications" && (
              <NotificationsPage setPage={setPage} backPage="AgentDashboard" />
            )}
            {page === "Profil" && (
              <ProfilPage user={user} setPage={setPage} onUserUpdate={handleUserUpdate} />
            )}
          </>
        )}

        {user?.role === "EXPERT" && (
          <>
            {page === "ExpertDashboard" && (
              <ExpertDashboard user={user} setPage={setPage} setSelectedSinistreId={handleSelectSinistre} />
            )}
            {page === "MissionsExpert" && (
              <MissionsExpert setPage={setPage} setSelectedSinistreId={handleSelectSinistre} />
            )}
            {page === "DetailSinistreExpert" && selectedSinistreId && !isNaN(selectedSinistreId) && (
              <DetailSinistreExpert selectedSinistreId={Number(selectedSinistreId)} setPage={setPage} />
            )}
            {page === "Notifications" && (
              <NotificationsPage setPage={setPage} backPage="ExpertDashboard" />
            )}
            {page === "Profil" && (
              <ProfilPage user={user} setPage={setPage} onUserUpdate={handleUserUpdate} />
            )}
          </>
        )}

        {user?.role === "SUPERVISEUR" && (
          <>
            {page === "DossiersSuperviseur" && (
              <DossiersSuperviseur setPage={setPage} setSelectedSinistreId={handleSelectSinistre} />
            )}
            {page === "DetailSinistreSuperviseur" && selectedSinistreId && !isNaN(selectedSinistreId) && (
              <DetailSinistreSuperviseur selectedSinistreId={Number(selectedSinistreId)} setPage={setPage} />
            )}
            {page === "Notifications" && (
              <NotificationsPage setPage={setPage} backPage="DossiersSuperviseur" />
            )}
            {page === "Profil" && (
              <ProfilPage user={user} setPage={setPage} onUserUpdate={handleUserUpdate} />
            )}
          </>
        )}

        {user && page === "Login" && (
          <div className="text-center p-20 bg-slate-900/20 rounded-3xl border border-white/5">
            <p className="text-slate-400">
              Vous êtes déjà authentifié en tant que{" "}
              <span className="text-orange-500 font-bold">{user.role}</span>.
            </p>
            <button onClick={() => setPage(homePage())}
              className="mt-6 px-8 py-3 bg-white/5 hover:bg-white/10 rounded-xl transition-all border border-white/10">
              Accéder à mon espace
            </button>
          </div>
        )}
      </main>
    </div>
  );
}