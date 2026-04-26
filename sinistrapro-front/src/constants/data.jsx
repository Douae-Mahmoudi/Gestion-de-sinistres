import React from "react";
import { IconCamera, IconUsers, IconShield, IconFile } from "../components/Icons";
export const STEPS = [
  {
    num: "01",
    icon: <IconCamera className="w-8 h-8 text-cyan-400" />,
    title: "Constat Amiable",
    desc: "Remplissez votre constat directement sur l'application avec les informations du sinistre.",
    activeClass: "border-cyan-500 bg-cyan-500/10",
  },
  {
    num: "02",
    icon: <IconUsers className="w-8 h-8 text-indigo-400" />,
    title: "Expertise technique",
    desc: "Analyse instantanée des chiffrages par nos experts agréés.",
    activeClass: "border-indigo-500 bg-indigo-500/10",
  },
  {
    num: "03",
    icon: <IconShield className="w-8 h-8 text-emerald-400" />,
    title: "Validation Superviseur",
    desc: "Contrôle qualité final pour garantir la conformité du dossier.",
    activeClass: "border-emerald-500 bg-emerald-500/10",
  },
 {
    num: "04",
    icon: <IconFile className="w-8 h-8 text-orange-400" />, // Nouvelle icône
    title: "Réception Rapport",
    desc: "Consultation et téléchargement de votre rapport d'expertise détaillé.",
    activeClass: "border-orange-500 bg-orange-500/10",
  },
];

export const FEATURES = [
  "Déclaration 100% en ligne",
  "Suivi en temps réel",
  "Experts certifiés",
  "Paiement sous 24h",
  "Données chiffrées",
  "Support 7j/7",
];