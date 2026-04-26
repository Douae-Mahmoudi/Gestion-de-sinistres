import React from "react";
import { FEATURES } from "../constants/data";
import { IconCheck, IconArrow } from "../components/Icons";

export function ServicesPage({ setPage }) {
  const featuresList = FEATURES || [];

  return (
    <div className="min-h-screen w-full pt-16 bg-[#060c1c] relative overflow-hidden flex items-center">
      
      <div
        className="absolute inset-0 z-0 pointer-events-none opacity-20" 
        style={{ 
          backgroundImage: 'linear-gradient(#0ea5e9 1px, transparent 1px), linear-gradient(90deg, #0ea5e9 1px, transparent 1px)', 
          backgroundSize: '50px 50px',
          width: '200vw',
          left: '-50vw'
        }}
      >
      </div>
      
      <div className="max-w-7xl mx-auto px-6 grid md:grid-cols-2 gap-12 items-center relative z-10 w-full">
        
        <div className="md:-ml-12 lg:-ml-20 transition-all duration-500">
          <h1 className="text-5xl md:text-6xl font-extrabold text-white leading-tight mb-6">
            La gestion de votre <span className="text-cyan-400">sinistre auto</span>, <br />
            <span className="text-transparent bg-clip-text bg-gradient-to-r from-cyan-400 to-indigo-500 italic">
              en toute sérénité
            </span>
          </h1>
          
          <div className="grid grid-cols-2 gap-4 mb-8">
            {featuresList.map((item, index) => (
              <div key={index} className="flex items-center gap-2 group">
                <div className="w-5 h-5 rounded-full bg-cyan-500/20 flex items-center justify-center text-cyan-400 shrink-0">
                  <IconCheck />
                </div>
                <span className="text-slate-400 text-sm whitespace-nowrap">{item}</span>
              </div>
            ))}
          </div>

          <button 
            onClick={() => setPage("Login")}
            className="group px-10 py-4 bg-gradient-to-r from-orange-500 to-red-600 text-white font-bold rounded-xl shadow-lg hover:scale-105 transition-all flex items-center gap-3"
          >
            Commencer dès maintenant
          </button>
        </div>

        <div className="hidden md:block relative">
          <div className="absolute -inset-10 bg-cyan-500/10 blur-[100px] rounded-full"></div>
          
          <div className="relative border border-white/10 p-2 rounded-[2.5rem] bg-slate-900/50 backdrop-blur-sm shadow-2xl">
            <img 
              src="/Home.jpg" 
              alt="SinistraPro Home" 
              className="rounded-[2rem] w-full h-auto object-cover shadow-inner border border-white/5"
            />
            
            <div className="absolute -bottom-4 -right-4 bg-[#060c1c] border border-white/10 p-4 rounded-2xl shadow-xl">
               <div className="flex items-center gap-3">
                  <div className="w-2 h-2 rounded-full bg-green-500 animate-pulse"></div>
                  <p className="text-[10px] font-mono text-slate-300 tracking-wider uppercase">Système de gestion actif</p>
               </div>
            </div>
          </div>
        </div>

      </div>
    </div>
  );
}