import React, { useState } from "react";
import { STEPS } from "../constants/data";

export function FonctionnementPage() {
  const [hovered, setHovered] = useState(null);
  const stepsList = STEPS || [];

  return (
    <div className="min-h-screen pt-24 bg-[#060c1c] px-6 pb-12">
      <div className="max-w-6xl mx-auto">
        <div className="mb-12 text-center md:text-left">
          <h2 className="text-3xl font-bold text-white mb-2">Comment ça marche ?</h2>
          <p className="text-slate-400">Quatre étapes simples pour une indemnisation rapide.</p>
        </div>
        
        <div className="grid md:grid-cols-4 gap-6">
          {stepsList.map((step, idx) => (
            <div 
              key={step.num || idx}
              onMouseEnter={() => setHovered(idx)}
              onMouseLeave={() => setHovered(null)}
              className={`relative p-8 rounded-2xl border transition-all duration-500 cursor-default ${
                hovered === idx 
                ? step.activeClass 
                : "border-white/5 bg-slate-900/40"
              }`}
            >
              <span className="absolute top-4 right-6 text-4xl font-black opacity-10 text-white">
                {step.num}
              </span>
              <div className="mb-6 inline-block p-3 rounded-xl bg-slate-800/50">
                {step.icon}
              </div>
              <h3 className="text-white font-bold text-lg mb-2">{step.title}</h3>
              <p className="text-slate-500 text-sm leading-relaxed">{step.desc}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}