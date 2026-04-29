// Configuration de l'API
const API_BASE_URL = '/api';

// État global du duplicata
let duplicataState = {
    currentStep: 1,
    demandeurId: null,
    passeportId: null,
    visaTransformableId: null,
    demandeId: null,
    dossierId: null,
    selectedCommunes: [],
    selectedComplementaires: []
};

document.addEventListener('DOMContentLoaded', function () {
    loadRefData();
    setupEventListeners();
    // Charger le catalogue dès l'arrivée (sans filtre) puis réagir au typeDemande
    loadCatalogueByTypeDemande(null);
});

function setupEventListeners() {
    document.getElementById('btnSuivant').addEventListener('click', goToNextStep);
    document.getElementById('btnPrecedent').addEventListener('click', goToPreviousStep);
    document.getElementById('btnCreerDossier').addEventListener('click', createDossier);
    document.getElementById('btnSoumettre').addEventListener('click', submitDuplicata);
    document.getElementById('btnReset').addEventListener('click', resetDuplicata);

    const typeDemande = document.getElementById('typeDemande');
    if (typeDemande) {
        typeDemande.addEventListener('change', function () {
            loadCatalogueByTypeDemande(typeDemande.value || null);
        });
    }
}

// ============================================
// GESTION DES ÉTAPES
// ============================================

async function goToNextStep() {
    if (duplicataState.currentStep !== 1) return;

    // Pour aller à l'étape 2, le dossier doit exister
    if (!duplicataState.dossierId) {
        showAlert('Veuillez créer le dossier à l\'étape 1 avant de continuer.', 'warning');
        return;
    }
    moveToStep(2);
}

function goToPreviousStep() {
    moveToStep(1);
}

function moveToStep(stepNumber) {
    if (stepNumber < 1 || stepNumber > 2) return;

    document.querySelectorAll('.form-step').forEach(step => step.classList.remove('active'));
    document.getElementById('step' + stepNumber).classList.add('active');
    updateProgressIndicators(stepNumber);
    updateFormButtons(stepNumber);
    duplicataState.currentStep = stepNumber;
    scrollToTop();
}

function updateProgressIndicators(stepNumber) {
    for (let i = 1; i <= 2; i++) {
        const indicator = document.getElementById('step' + i + '-indicator');
        if (i < stepNumber) {
            indicator.classList.remove('active');
            indicator.classList.add('completed');
        } else if (i === stepNumber) {
            indicator.classList.remove('completed');
            indicator.classList.add('active');
        } else {
            indicator.classList.remove('active', 'completed');
        }
    }

    const progressBar = document.getElementById('progressBar');
    const progressPercent = (stepNumber / 2) * 100;
    progressBar.style.width = progressPercent + '%';
}

function updateFormButtons(stepNumber) {
    const btnPrecedent = document.getElementById('btnPrecedent');
    const btnSuivant = document.getElementById('btnSuivant');

    if (stepNumber === 1) {
        btnPrecedent.style.display = 'none';
        btnSuivant.style.display = 'inline-flex';
    } else {
        btnPrecedent.style.display = 'inline-flex';
        btnSuivant.style.display = 'none';
    }
}

// ============================================
// DONNÉES DE RÉFÉRENCE
// ============================================

async function loadRefData() {
    try {
        const [nationalites, situations, typeIdentites, typeVisas, typeDemandes] = await Promise.all([
            fetch(API_BASE_URL + '/ref/nationalites').then(r => r.json()),
            fetch(API_BASE_URL + '/ref/situations-familiales').then(r => r.json()),
            fetch(API_BASE_URL + '/ref/types-identite').then(r => r.json()),
            fetch(API_BASE_URL + '/ref/types-visas').then(r => r.json()),
            fetch(API_BASE_URL + '/ref/types-demandes').then(r => r.json())
        ]);

        populateSelect('nationalite', nationalites);
        populateSelect('situationFamiliale', situations);
        populateSelect('typeIdentite', typeIdentites);
        populateSelect('typeVisa', typeVisas);
        populateSelect('typeDemande', typeDemandes);
    } catch (error) {
        console.error('Erreur lors du chargement des données:', error);
        showAlert('Erreur lors du chargement des listes déroulantes.', 'danger');
    }
}

function populateSelect(selectId, data) {
    const select = document.getElementById(selectId);
    if (!select) return;
    select.innerHTML = '<option value="">-- Sélectionner --</option>';

    if (!Array.isArray(data)) return;
    data.forEach(item => {
        const option = document.createElement('option');
        option.value = item.id ?? item.code ?? item.value;
        option.textContent = item.libelle ?? item.name ?? item.label ?? item.code ?? String(item);
        select.appendChild(option);
    });

    // Valeur par défaut: auto-sélection de la première option chargée
    if (select.dataset && String(select.dataset.autoselectFirst || '').toLowerCase() === 'true') {
        // index 0 = placeholder, index 1 = première vraie option
        if (!select.value && select.options && select.options.length > 1) {
            select.value = select.options[1].value;
            // Déclencher les listeners (ex: recharge du catalogue sur typeDemande)
            try {
                select.dispatchEvent(new Event('change', { bubbles: true }));
            } catch (_) {
                // ignore
            }
        }
    }
}

// ============================================
// CATALOGUE + SÉLECTION
// ============================================

async function loadCatalogueByTypeDemande(typeDemandeId) {
    try {
        const communesPromise = fetch(API_BASE_URL + '/catalogue/communes').then(r => r.ok ? r.json() : []);
        const complementairesPromise = fetch(
            API_BASE_URL + '/catalogue/complementaires' + (typeDemandeId ? ('?typeDemandeId=' + encodeURIComponent(typeDemandeId)) : '')
        ).then(r => r.ok ? r.json() : []);

        const [communes, complementaires] = await Promise.all([communesPromise, complementairesPromise]);
        displayCatalogue(communes, complementaires);
    } catch (error) {
        console.error('Erreur chargement catalogue:', error);
        showAlert('Erreur lors du chargement du catalogue des pièces.', 'danger');
    }
}

function displayCatalogue(communes, complementaires) {
    const container = document.getElementById('catalogueContainer');
    if (!container) return;

    const renderList = (items, type, title) => {
        let html = '<div class="card mb-3"><div class="card-header">' + title + '</div><div class="card-body">';
        if (!items || items.length === 0) {
            html += '<p class="text-muted">Aucune pièce.</p>';
        } else {
            html += '<ul class="list-group list-group-flush">';
            items.forEach(item => {
                const label = (item.libelle || item.code || ('ID ' + item.id));
                const checked = item.obligatoire ? ' checked' : '';
                html +=
                    '<li class="list-group-item">' +
                    '<input type="checkbox" class="catalogue-checkbox me-2"' + checked +
                    ' data-id="' + (item.id ?? '') + '"' +
                    ' data-label="' + escapeAttr(label) + '"' +
                    ' data-type="' + type + '" />' +
                    label +
                    (item.obligatoire ? ' <span class="badge bg-warning ms-2">Obligatoire</span>' : '') +
                    '</li>';
            });
            html += '</ul>';
        }
        html += '</div></div>';
        return html;
    };

    container.innerHTML =
        renderList(communes, 'commune', 'Catalogue - Pièces communes') +
        renderList(complementaires, 'complementaire', 'Catalogue - Pièces complémentaires');

    container.addEventListener('change', onCatalogueChange, { passive: true });
    onCatalogueChange();
}

function onCatalogueChange() {
    const checked = Array.from(document.querySelectorAll('.catalogue-checkbox:checked')).map(cb => ({
        id: Number(cb.getAttribute('data-id')),
        type: cb.getAttribute('data-type')
    }));

    duplicataState.selectedCommunes = checked.filter(i => i.type === 'commune').map(i => i.id).filter(Boolean);
    duplicataState.selectedComplementaires = checked.filter(i => i.type === 'complementaire').map(i => i.id).filter(Boolean);
}

function escapeAttr(value) {
    return String(value).replace(/"/g, '\\"');
}

// ============================================
// CRÉATION DOSSIER (Sprint 2 - étape 1)
// ============================================

async function createDossier() {
    if (!validateStep('step1')) return;

    try {
        onCatalogueChange();

        await saveDemandeur();
        await savePasseportAndVisa();
        await createDemandeAndDossier();
        await createPiecesEntries();
        await loadAndRenderPieces();

        document.getElementById('piecesSection').style.display = '';
        document.getElementById('resumeDossier').textContent = duplicataState.dossierId ? ('Dossier #' + duplicataState.dossierId) : '(non créé)';

        showAlert('Dossier créé. Vous pouvez uploader les photocopies.', 'success');
    } catch (error) {
        console.error('Erreur createDossier:', error);
        showAlert(error.message || 'Erreur lors de la création du dossier', 'danger');
    }
}

async function saveDemandeur() {
    const demandeurData = {
        nom: document.getElementById('nom').value,
        prenom: document.getElementById('prenom').value,
        dateNaissance: document.getElementById('dateNaissance').value,
        nationaliteId: document.getElementById('nationalite').value || null,
        situationFamilialeId: document.getElementById('situationFamiliale').value || null,
        email: document.getElementById('email').value || null,
        telephone: document.getElementById('telephone').value || null
        // NOTE: sexeId non géré côté UI (pas d'endpoint ref), on n'envoie pas.
    };

    const response = await fetch(API_BASE_URL + '/demandeurs', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(demandeurData)
    });
    if (!response.ok) {
        const err = await response.text();
        throw new Error(err || 'Erreur enregistrement demandeur');
    }
    const demandeur = await response.json();
    duplicataState.demandeurId = demandeur.id;
}

async function savePasseportAndVisa() {
    if (!duplicataState.demandeurId) throw new Error('ID demandeur manquant');

    const passeportData = {
        demandeurId: duplicataState.demandeurId,
        numero: document.getElementById('numeroPasseport').value,
        dateEmission: document.getElementById('dateEmissionPasseport').value,
        dateExpiration: document.getElementById('dateExpirationPasseport').value,
        paysEmission: document.getElementById('lieuEmissionPasseport').value
    };

    const passeportResponse = await fetch(API_BASE_URL + '/passeports', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(passeportData)
    });
    if (!passeportResponse.ok) {
        const err = await passeportResponse.text();
        throw new Error(err || 'Erreur création passeport');
    }
    const passeport = await passeportResponse.json();
    duplicataState.passeportId = passeport.id;

    const visaData = {
        passeportId: duplicataState.passeportId,
        typeVisaId: document.getElementById('typeVisa').value,
        infos: {
            dateDebut: document.getElementById('dateDebutVisa').value,
            dateFin: document.getElementById('dateFinVisa').value,
            typeIdentiteId: document.getElementById('typeIdentite').value
        }
    };

    const visaResponse = await fetch(API_BASE_URL + '/visas', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(visaData)
    });
    if (!visaResponse.ok) {
        const err = await visaResponse.text();
        throw new Error(err || 'Erreur création visa transformable');
    }
    const visa = await visaResponse.json();
    duplicataState.visaTransformableId = visa.id;
}

async function createDemandeAndDossier() {
    const demandeData = {
        demandeurId: duplicataState.demandeurId,
        passeportId: duplicataState.passeportId,
        idVisaTransformable: duplicataState.visaTransformableId,
        typeDemandeId: document.getElementById('typeDemande').value
    };

    const demandeResponse = await fetch(API_BASE_URL + '/demandes', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(demandeData)
    });
    if (!demandeResponse.ok) {
        const err = await demandeResponse.text();
        throw new Error(err || 'Erreur création demande');
    }
    const demande = await demandeResponse.json();
    duplicataState.demandeId = demande.id;

    const dossierResponse = await fetch(API_BASE_URL + '/dossiers', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ demandeId: duplicataState.demandeId, createdBy: 'frontend-duplicata' })
    });
    if (!dossierResponse.ok) {
        const err = await dossierResponse.text();
        throw new Error(err || 'Erreur création dossier');
    }
    const dossierBody = await dossierResponse.json();
    const dossier = dossierBody && dossierBody.dossier ? dossierBody.dossier : dossierBody;
    duplicataState.dossierId = dossier.id;
}

async function createPiecesEntries() {
    if (!duplicataState.dossierId) throw new Error('ID dossier manquant');

    const communes = Array.isArray(duplicataState.selectedCommunes) ? duplicataState.selectedCommunes : [];
    const complementaires = Array.isArray(duplicataState.selectedComplementaires) ? duplicataState.selectedComplementaires : [];

    const communesResp = await fetch(API_BASE_URL + '/dossiers/' + duplicataState.dossierId + '/pieces/communes/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(communes)
    });
    if (!communesResp.ok) {
        const err = await communesResp.text();
        throw new Error(err || 'Erreur création pièces communes');
    }

    const compResp = await fetch(API_BASE_URL + '/dossiers/' + duplicataState.dossierId + '/pieces/complementaires/create', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(complementaires)
    });
    if (!compResp.ok) {
        const err = await compResp.text();
        throw new Error(err || 'Erreur création pièces complémentaires');
    }
}

// ============================================
// PIÈCES: LISTE + UPLOAD
// ============================================

async function loadAndRenderPieces() {
    if (!duplicataState.dossierId) return;

    const piecesResponse = await fetch(API_BASE_URL + '/dossiers/' + duplicataState.dossierId + '/pieces');
    if (!piecesResponse.ok) {
        const err = await piecesResponse.text();
        throw new Error(err || 'Erreur chargement pièces');
    }
    const piecesBody = await piecesResponse.json();
    const communes = Array.isArray(piecesBody.communes) ? piecesBody.communes : [];
    const complementaires = Array.isArray(piecesBody.complementaires) ? piecesBody.complementaires : [];

    renderPieces(communes, complementaires);
    updatePiecesProgress(communes.concat(complementaires));
}

function renderPieces(communes, complementaires) {
    const piecesList = document.getElementById('piecesList');
    if (!piecesList) return;

    const renderTable = (rows, kindLabel, kindApiPath) => {
        let html = '<div class="card mb-3">';
        html += '<div class="card-header">' + kindLabel + '</div>';
        html += '<div class="card-body">';
        if (!rows || rows.length === 0) {
            html += '<p class="text-muted">Aucune pièce.</p>';
        } else {
            html += '<div class="table-responsive">';
            html += '<table class="table table-hover">';
            html += '<thead><tr><th>Pièce</th><th>Statut</th><th>Fichier</th><th style="width: 180px;">Action</th></tr></thead><tbody>';
            rows.forEach(row => {
                const label = getPieceLabel(row);
                const statusCode = (row.statutPiece && row.statutPiece.code) ? String(row.statutPiece.code).toUpperCase() : 'NON_FOURNI';
                const filePath = row.fichierPath ? String(row.fichierPath) : '';
                const hasFile = Boolean(filePath);
                const isFourni = statusCode === 'FOURNI' || hasFile;
                const badgeClass = isFourni ? 'bg-success' : 'bg-warning';
                const badgeLabel = isFourni ? 'FOURNI' : 'NON FOURNI';

                html += '<tr>';
                html += '<td>' + escapeHtml(label) + '</td>';
                html += '<td><span class="badge ' + badgeClass + '">' + badgeLabel + '</span></td>';
                html += '<td>' + (filePath ? ('<span class="text-muted">' + escapeHtml(filePath) + '</span>') : '<span class="text-muted">—</span>') + '</td>';
                html += '<td>';
                html += '<button type="button" class="btn btn-outline-primary btn-sm" data-upload-kind="' + kindApiPath + '" data-piece-id="' + row.id + '">';
                html += '<i class="fas fa-upload"></i> Uploader';
                html += '</button>';
                html += '</td>';
                html += '</tr>';
            });
            html += '</tbody></table></div>';
        }
        html += '</div></div>';
        return html;
    };

    piecesList.innerHTML =
        renderTable(communes, 'Pièces communes', 'communes') +
        renderTable(complementaires, 'Pièces complémentaires', 'complementaires');

    // Bind upload buttons
    piecesList.querySelectorAll('button[data-upload-kind]').forEach(btn => {
        btn.addEventListener('click', function () {
            const kind = btn.getAttribute('data-upload-kind');
            const pieceId = btn.getAttribute('data-piece-id');
            uploadPiece(pieceId, duplicataState.dossierId, kind);
        });
    });
}

function getPieceLabel(row) {
    if (row.cataloguePieceCommune) return row.cataloguePieceCommune.libelle || row.cataloguePieceCommune.code || ('Pièce ' + row.id);
    if (row.cataloguePieceComplementaire) return row.cataloguePieceComplementaire.libelle || row.cataloguePieceComplementaire.code || ('Pièce ' + row.id);
    return 'Pièce ' + row.id;
}

function uploadPiece(pieceId, dossierId, kind) {
    if (!pieceId || !dossierId) {
        showAlert('ID pièce/dossier invalide.', 'danger');
        return;
    }

    const input = document.createElement('input');
    input.type = 'file';
    input.onchange = async function (e) {
        const file = e.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        try {
            const url = API_BASE_URL + '/dossiers/' + dossierId + '/pieces/' + kind + '/' + pieceId + '/upload';
            const response = await fetch(url, { method: 'POST', body: formData });
            if (!response.ok) {
                const err = await response.text();
                throw new Error(err || ('Erreur HTTP ' + response.status));
            }
            showAlert('Fichier uploadé.', 'success');
            await loadAndRenderPieces();
        } catch (error) {
            console.error('Erreur upload:', error);
            showAlert('Erreur lors de l\'upload: ' + (error.message || 'inconnue'), 'danger');
        }
    };
    input.click();
}

function updatePiecesProgress(pieces) {
    const totalPieces = Array.isArray(pieces) ? pieces.length : 0;
    const piecesFournies = Array.isArray(pieces)
        ? pieces.filter(p => {
            const status = p && p.statutPiece ? String(p.statutPiece.code || '').toUpperCase() : '';
            const hasFile = p && p.fichierPath;
            return status === 'FOURNI' || Boolean(hasFile);
        }).length
        : 0;
    const progressPercent = totalPieces > 0 ? (piecesFournies / totalPieces) * 100 : 0;

    const progressEl = document.getElementById('progressPieces');
    const progressTextEl = document.getElementById('progressPiecesText');
    const piecesFourniesEl = document.getElementById('piecesFournies');
    const piecesTotalEl = document.getElementById('piecesTotal');

    if (progressEl) progressEl.style.width = progressPercent + '%';
    if (progressTextEl) progressTextEl.textContent = Math.round(progressPercent) + '%';
    if (piecesFourniesEl) piecesFourniesEl.textContent = piecesFournies;
    if (piecesTotalEl) piecesTotalEl.textContent = totalPieces;
}

// ============================================
// SOUMISSION (Sprint 2 - étape 2)
// ============================================

function submitDuplicata() {
    if (!duplicataState.dossierId) {
        showAlert('Aucun dossier créé. Revenez à l\'étape 1.', 'warning');
        return;
    }

    if (!validateStep('step2')) return;

    fetch(API_BASE_URL + '/dossiers/' + duplicataState.dossierId + '/completude')
        .then(resp => resp.ok ? resp.json() : resp.text().then(t => { throw new Error(t || 'Erreur complétude'); }))
        .then(body => {
            if (!body || body.completude !== true) {
                showAlert('Le dossier n\'est pas complet. Uploadez toutes les pièces obligatoires.', 'warning');
                return null;
            }
            return fetch(API_BASE_URL + '/dossiers/' + duplicataState.dossierId + '/statut', {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ code: 'APPROUVE' })
            });
        })
        .then(resp => {
            if (!resp) return;
            if (!resp.ok) {
                return resp.text().then(t => { throw new Error(t || 'Erreur changement statut'); });
            }
        })
        .catch(err => {
            console.error('Erreur statut dossier:', err);
            showAlert('Erreur validation dossier: ' + (err.message || err), 'danger');
        });

    // Frontend uniquement: pas d'endpoint dédié duplicata dans ce projet.
    // On considère la saisie complète côté UI.
    const payload = {
        dossierId: duplicataState.dossierId,
        type: document.getElementById('documentType').value,
        reference: document.getElementById('documentReference').value,
        dateDebut: document.getElementById('documentDateDebut').value,
        dateFin: document.getElementById('documentDateFin').value
    };

    console.log('Duplicata payload (frontend):', payload);
    showAlert('Duplicata saisi. (Enregistrement serveur à implémenter côté backend)', 'success');
}

// ============================================
// VALIDATION + UTILITAIRES
// ============================================

function validateStep(stepId) {
    const stepDiv = document.getElementById(stepId);
    if (!stepDiv) return true;

    const inputs = stepDiv.querySelectorAll('input, select');
    let isValid = true;
    inputs.forEach(input => {
        if (input.hasAttribute('required')) {
            input.classList.remove('is-invalid');
            if (!String(input.value || '').trim()) {
                input.classList.add('is-invalid');
                isValid = false;
            }
        }
    });

    if (!isValid) {
        showAlert('Veuillez remplir tous les champs requis.', 'danger');
    }
    return isValid;
}

function showAlert(message, type) {
    const alertContainer = document.getElementById('alertContainer');
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-' + type + ' alert-dismissible fade show';
    alertDiv.role = 'alert';
    alertDiv.innerHTML = message + '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';
    alertContainer.appendChild(alertDiv);
    setTimeout(() => {
        try { alertDiv.remove(); } catch (_) {}
    }, 5000);
}

function scrollToTop() {
    const container = document.querySelector('.container');
    if (container) container.scrollIntoView({ behavior: 'smooth', block: 'start' });
}

function escapeHtml(value) {
    return String(value)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#039;');
}

function resetDuplicata() {
    duplicataState = {
        currentStep: 1,
        demandeurId: null,
        passeportId: null,
        visaTransformableId: null,
        demandeId: null,
        dossierId: null,
        selectedCommunes: [],
        selectedComplementaires: []
    };
    document.getElementById('piecesSection').style.display = 'none';
    document.getElementById('piecesList').innerHTML = '';
    document.getElementById('resumeDossier').textContent = '(non créé)';
    moveToStep(1);
}
