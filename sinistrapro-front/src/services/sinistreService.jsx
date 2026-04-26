import axios from 'axios';

const api = axios.create({
  baseURL: "http://localhost:8100/api",
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('token');
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
}, (error) => Promise.reject(error));

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const url    = error.config?.url || '';

    if (status === 401) localStorage.removeItem('token');

    const isExpected404 = status === 404 &&
      (url.includes('/rapports/') || url.includes('/decisions/'));

    if (!isExpected404 && status !== 401) {
      console.error(`Erreur API [${status}] sur ${url}`);
    }

    return Promise.reject(error);
  }
);

const downloadFile = async (endpoint, filename) => {
  try {
    const cleanEndpoint = endpoint.startsWith('/api')
      ? endpoint.replace('/api', '')
      : endpoint;

    const response = await api.get(cleanEndpoint, {
      responseType: 'blob',
      headers: { 'Accept': 'application/pdf, application/octet-stream' }
    });

    const blob = new Blob(
      [response.data],
      { type: response.headers['content-type'] || 'application/pdf' }
    );
    const url  = window.URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href  = url;
    link.setAttribute('download', filename);
    document.body.appendChild(link);
    link.click();
    link.remove();
    window.URL.revokeObjectURL(url);
  } catch (error) {
    console.error("Échec du téléchargement :", error);
    throw error;
  }
};

export const sinistreService = {

  getResumeStats:        async () => (await api.get('/stats/resume')).data,
  getParStatut:          async () => (await api.get('/stats/sinistres-par-statut')).data,
  getResumeStatsParRole: async () => (await api.get('/sinistres/stats/resume')).data,

  getMesSinistres:  async () => (await api.get('/sinistres/mes-sinistres')).data,
  getById:          async (id) => (await api.get(`/sinistres/${id}`)).data,
  declarerSinistre: async (data) => (await api.post('/sinistres', data)).data,

  getAllSinistres: async () => (await api.get('/sinistres')).data,
  getExperts:      async () => (await api.get('/utilisateurs/experts')).data,

  affecterExpert: async (sinistreId, expertId, commentaireAgent) =>
    (await api.put(`/sinistres/${sinistreId}/affecter`, { expertId, commentaireAgent })).data,

  cloturerSinistre: async (sinistreId, data) =>
    (await api.put(`/sinistres/${sinistreId}/cloturer`, {
      ...data,
      motif: data.motif || "Clôture du dossier"
    })).data,

  getMissionsExpert: async () => (await api.get('/sinistres/expert/missions')).data,

  soumettreRapport: async (sinistreId, data) =>
    (await api.put(`/sinistres/${sinistreId}/soumettre-rapport`, data)).data,

  getRapport: async (sinistreId) => {
    try { return (await api.get(`/rapports/${sinistreId}`)).data; }
    catch (err) { if (err.response?.status === 404) return null; throw err; }
  },

  getDecision: async (sinistreId) => {
    try { return (await api.get(`/decisions/${sinistreId}`)).data; }
    catch (err) { if (err.response?.status === 404) return null; throw err; }
  },

  downloadPdf: (path, filename) => {
    const cleanPath = path.startsWith('/api') ? path.replace('/api', '') : path;
    return downloadFile(cleanPath, filename);
  },

  downloadPdfRapport:  (sinistreId) =>
    downloadFile(`/rapports/${sinistreId}/pdf`,  `Rapport_SP_${sinistreId}.pdf`),
  downloadPdfDecision: (sinistreId) =>
    downloadFile(`/decisions/${sinistreId}/pdf`, `Decision_SP_${sinistreId}.pdf`),

  downloadDocument: (docId, fileName) =>
    downloadFile(`/documents/${docId}/download`, fileName || `Doc_${docId}`),

  uploadDocument: async (sinistreId, file, typeDocument) => {
    const formData = new FormData();
    formData.append('fichier', file);
    formData.append('typeDocument', typeDocument);
    return (await api.post(`/documents/upload/${sinistreId}`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    })).data;
  },

  getDocumentsBySinistre: async (sinistreId) =>
    (await api.get(`/documents/sinistre/${sinistreId}`)).data,

  getProfil:      async ()     => (await api.get('/utilisateurs/me')).data,
  updateProfil:   async (data) => (await api.put('/utilisateurs/me', data)).data,
  changePassword: async (data) => (await api.put('/utilisateurs/me/password', data)).data,

  notifications: {
    getAll: async () => (await api.get('/notifications')).data,
    countNonLues: async () => {
      const r = await api.get('/notifications/non-lues/count');
      return r.data?.count ?? r.data;
    },
    marquerLue:       async (id) => api.put(`/notifications/${id}/lire`),
    marquerToutesLues: async ()  => api.put('/notifications/lire-tout'),
  },
};