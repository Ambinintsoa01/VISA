// Configuration de l'API
const API_BASE_URL = '/api';

// État global du formulaire
let formState = {
    currentStep: 1,
    demandeurId: null,
    passeportId: null,
    visaTransformableId: null,
    demandeId: null,
    dossierId: null,
    selectedCommunes: [],
    selectedComplementaires: [],
    apiError: false,
    formData: {
        demandeur: {},
        passeport: {},
        visaTransformable: {},
        demande: {}
    }
};

// Initialisation
document.addEventListener('DOMContentLoaded', function() {
    console.log('Formulaire chargé');
    loadRefData();
    setupEventListeners();
});

// ============================================
// GESTION DES ÉTAPES
// ============================================

function setupEventListeners() {
    document.getElementById('btnSuivant').addEventListener('click', goToNextStep);
    document.getElementById('btnPrecedent').addEventListener('click', goToPreviousStep);
    document.getElementById('btnValider').addEventListener('click', submitForm);
    document.getElementById('btnCloseSuccess').addEventListener('click', resetForm);
}

async function goToNextStep() {
    if (validateCurrentStep()) {
        try {
            if (formState.currentStep === 1) {
                console.log('💾 Sauvegarde du demandeur...');
                await saveDemandeur();
                console.log('✓ Demandeur sauvegardé');
            } else if (formState.currentStep === 2) {
                console.log('💾 Sauvegarde du passeport et visa...');
                await savePasseportAndVisa();
                console.log('✓ Passeport et visa sauvegardés');
            }
            moveToStep(formState.currentStep + 1);
        } catch (error) {
            console.error('❌ Erreur dans goToNextStep:', error);
            showAlert('Erreur: ' + error.message, 'danger');
        }
    }
}

function goToPreviousStep() {
    moveToStep(formState.currentStep - 1);
}

function moveToStep(stepNumber) {
    if (stepNumber < 1 || stepNumber > 3) return;

    // Masquer toutes les étapes
    document.querySelectorAll('.form-step').forEach(step => {
        step.classList.remove('active');
    });

    // Afficher l'étape actuelle
    document.getElementById('step' + stepNumber).classList.add('active');

    // Mettre à jour les indicateurs de progression
    updateProgressIndicators(stepNumber);

    // Mettre à jour les boutons
    updateFormButtons(stepNumber);

    // Charger les pièces si étape 3
    if (stepNumber === 3) {
        loadPieces();
    }

    formState.currentStep = stepNumber;
    scrollToTop();
}

function updateProgressIndicators(stepNumber) {
    for (let i = 1; i <= 3; i++) {
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

    // Mettre à jour la barre de progression
    const progressBar = document.getElementById('progressBar');
    const progressPercent = (stepNumber / 3) * 100;
    progressBar.style.width = progressPercent + '%';
}

function updateFormButtons(stepNumber) {
    const btnPrecedent = document.getElementById('btnPrecedent');
    const btnSuivant = document.getElementById('btnSuivant');
    const btnValider = document.getElementById('btnValider');

    if (stepNumber === 1) {
        btnPrecedent.style.display = 'none';
        btnSuivant.style.display = 'inline-block';
        btnValider.style.display = 'none';
    } else if (stepNumber === 2) {
        btnPrecedent.style.display = 'inline-block';
        btnSuivant.style.display = 'inline-block';
        btnValider.style.display = 'none';
    } else if (stepNumber === 3) {
        btnPrecedent.style.display = 'inline-block';
        btnSuivant.style.display = 'none';
        btnValider.style.display = 'inline-block';
    }
}

// ============================================
// VALIDATION
// ============================================

function validateCurrentStep() {
    const currentStepDiv = document.getElementById('step' + formState.currentStep);
    const inputs = currentStepDiv.querySelectorAll('input, select');
    let isValid = true;
    const errors = [];

    inputs.forEach(input => {
        if (input.hasAttribute('required')) {
            input.classList.remove('is-invalid');
            if (!input.value.trim()) {
                input.classList.add('is-invalid');
                isValid = false;
                errors.push(input.labels[0]?.textContent || 'Champ requis');
            }
        }
    });

    if (!isValid) {
        showAlert('Veuillez remplir tous les champs requis.', 'danger');
    }

    return isValid;
}

// ============================================
// CHARGEMENT DES DONNÉES DE RÉFÉRENCE
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
    if (!select) {
        console.warn('populateSelect: élément introuvable pour id =', selectId);
        return;
    }
    select.innerHTML = '<option value="">-- Sélectionner --</option>';

    if (Array.isArray(data)) {
        data.forEach(item => {
            const option = document.createElement('option');
            option.value = item.id || item.code || item.value;
            option.textContent = item.name || item.libelle || item.label || item;
            select.appendChild(option);
        });
    }
}

// ============================================
// SAUVEGARDE DES DONNÉES - ÉTAPE 1
// ============================================

async function saveDemandeur() {
    try {
        console.log('📋 Collecte des données Étape 1...');
        const demandeurData = {
            nom: document.getElementById('nom').value,
            prenom: document.getElementById('prenom').value,
            sexe: document.getElementById('sexe').value,
            dateNaissance: document.getElementById('dateNaissance').value,
            nationaliteId: document.getElementById('nationalite').value,
            situationFamilialeId: document.getElementById('situationFamiliale').value,
            lieuNaissance: document.getElementById('lieuNaissance').value,
            telephoneContact: document.getElementById('telephoneContact').value,
            adresse: document.getElementById('adresse').value
        };
        console.log('Données à envoyer:', demandeurData);

        formState.formData.demandeur = demandeurData;

        console.log('📤 POST /api/demandeurs...');
        const response = await fetch(API_BASE_URL + '/demandeurs', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(demandeurData)
        });

        if (response.ok) {
            const demandeur = await response.json();
            console.log('✓ Réponse serveur:', demandeur);
            formState.demandeurId = demandeur.id;
            console.log('✓ DemandeurID définit à:', formState.demandeurId);
            showAlert('Informations d\'état civil enregistrées.', 'success');
        } else {
            const error = await response.text();
            console.error('❌ Erreur HTTP:', response.status, error);
            throw new Error(error);
        }
    } catch (error) {
        console.error('❌ Erreur saveDemandeur:', error);
        formState.apiError = true;
        showAlert('Erreur lors de l\'enregistrement du demandeur: ' + error.message, 'danger');
        throw error;
    }
}

// ============================================
// SAUVEGARDE DES DONNÉES - ÉTAPE 2
// ============================================

async function savePasseportAndVisa() {
    try {
        console.log('=== ÉTAPE 2: PASSEPORT & VISA ===');
        console.log('formState au départ:', formState);
        
        // Vérifier que demandeurId existe
        if (!formState.demandeurId) {
            throw new Error('ID demandeur manquant. Assurez-vous que l\'étape 1 a été complétée.');
        }
        console.log('✓ Demandeur ID trouvé:', formState.demandeurId);

        // Sauvegarder le passeport
        console.log('📝 Collecte données passeport...');
        const passeportData = {
            demandeurId: formState.demandeurId,
            numero: document.getElementById('numeroPasseport').value,
            dateEmission: document.getElementById('dateEmissionPasseport').value,
            dateExpiration: document.getElementById('dateExpirationPasseport').value,
            paysEmission: document.getElementById('lieuEmissionPasseport').value
        };
        console.log('Données passeport:', passeportData);

        if (!passeportData.numero || !passeportData.demandeurId) {
            throw new Error('Numéro passeport et demandeur requis');
        }

        console.log('📤 POST /api/passeports...');
        const passeportResponse = await fetch(API_BASE_URL + '/passeports', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(passeportData)
        });

        if (!passeportResponse.ok) {
            const errText = await passeportResponse.text();
            console.error('❌ Erreur HTTP passeport:', passeportResponse.status, errText);
            throw new Error('Erreur passeport: ' + errText);
        }

        const passeport = await passeportResponse.json();
        console.log('✓ Réponse passeport:', passeport);
        
        if (!passeport.id) {
            throw new Error('Passeport créé mais sans ID');
        }
        formState.passeportId = passeport.id;
        console.log('✓ PasseportID défini à:', formState.passeportId);

        // Sauvegarder le visa transformable
        console.log('📝 Collecte données visa...');
        const visaData = {
            passeportId: formState.passeportId,
            typeVisaId: document.getElementById('typeVisa').value,
            infos: {
                dateDebut: document.getElementById('dateDebutVisa').value,
                dateFin: document.getElementById('dateFinVisa').value,
                typeIdentiteId: document.getElementById('typeIdentite').value
            }
        };
        console.log('Données visa:', visaData);

        if (!visaData.passeportId) {
            throw new Error('Passeport ID invalide');
        }

        const visaResponse = await fetch(API_BASE_URL + '/visas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(visaData)
        });

        if (!visaResponse.ok) {
            const errText = await visaResponse.text();
            console.error('❌ Erreur HTTP visa:', visaResponse.status, errText);
            throw new Error('Erreur visa: ' + errText);
        }

        const visa = await visaResponse.json();
        console.log('✓ Réponse visa:', visa);
        
        if (!visa.id) {
            throw new Error('Visa créé mais sans ID');
        }
        formState.visaTransformableId = visa.id;
        console.log('✓ VisaTransformableID défini à:', formState.visaTransformableId);
        console.log('✓ État final formState:', formState);
        showAlert('Passeport et Visa enregistrés.', 'success');
    } catch (error) {
        console.error('❌ Erreur savePasseportAndVisa:', error);
        formState.apiError = true;
        showAlert('Erreur lors de l\'enregistrement: ' + error.message, 'danger');
        throw error;
    }
}

// ============================================
// CHARGEMENT DES PIÈCES - ÉTAPE 3
// ============================================

async function loadPieces() {
    try {
        console.log('=== CHARGEMENT PIÈCES ÉTAPE 3 ===');

        // 1) Charger le catalogue (communes + complémentaires)
        try {
            const typeDemandeSelected = document.getElementById('typeDemande')?.value || null;
            await loadCatalogueByTypeVisa(typeDemandeSelected);
        } catch (e) {
            console.warn('Impossible de charger le catalogue:', e);
        }

        // 2) Si un dossier existe déjà, charger ses pièces
        if (formState.dossierId) {
            const piecesResponse = await fetch(API_BASE_URL + '/dossiers/' + formState.dossierId + '/pieces');
            if (!piecesResponse.ok) {
                const errText = await piecesResponse.text();
                throw new Error('Erreur chargement pièces: ' + errText);
            }
            return;
        }

        // 3) Sinon afficher un message neutre
        const piecesList = document.getElementById('piecesList');
        if (piecesList) {
            piecesList.innerHTML = '<p class="text-muted">Sélectionnez les pièces puis cliquez sur « Créer le Dossier ».</p>';
        }
        updatePiecesProgress([]);
    } catch (error) {
        console.error('❌ ERREUR ÉTAPE 3:', error);
        formState.apiError = true;
        showAlert('Erreur Étape 3: ' + error.message, 'danger');
    }
}

function normalizePiecesResponse(piecesResp) {
    if (Array.isArray(piecesResp)) {
        return piecesResp;
    }
    if (piecesResp && (piecesResp.communes || piecesResp.complementaires)) {
        return []
            .concat(piecesResp.communes || [])
            .concat(piecesResp.complementaires || []);
    }
    return [];
}

async function fetchJsonOrThrow(url, options) {
    const resp = await fetch(url, options);
    if (!resp.ok) {
        const body = await resp.text();
        throw new Error(body || ('Erreur HTTP ' + resp.status));
    }
    const text = await resp.text();
    if (!text) return null;
    try {
        return JSON.parse(text);
    } catch {
        return text;
    }
}

async function loadCatalogueByTypeVisa(typeDemandeId) {
    try {
        const communesPromise = fetch(API_BASE_URL + '/catalogue/communes').then(r => r.ok ? r.json() : []);
        const complementairesPromise = fetch(API_BASE_URL + '/catalogue/complementaires' + (typeDemandeId ? ('?typeDemandeId=' + encodeURIComponent(typeDemandeId)) : '')).then(r => r.ok ? r.json() : []);

        const [communes, complementaires] = await Promise.all([communesPromise, complementairesPromise]);
        displayCatalogue(communes, complementaires);
    } catch (error) {
        console.error('Erreur chargement catalogue:', error);
    }
}

function displayCatalogue(communes, complementaires) {
    const containerId = 'catalogueContainer';
    let container = document.getElementById(containerId);
    if (!container) {
        // insérer au-dessus de la liste des pièces si possible
        const piecesList = document.getElementById('piecesList');
        container = document.createElement('div');
        container.id = containerId;
        if (piecesList && piecesList.parentNode) {
            piecesList.parentNode.insertBefore(container, piecesList);
        } else {
            document.body.appendChild(container);
        }
    }

    let html = '<div class="card mb-3"><div class="card-header">Catalogue - Pièces communes</div><div class="card-body">';
    if (!communes || communes.length === 0) html += '<p class="text-muted">Aucune pièce commune.</p>';
    else {
        html += '<ul class="list-group list-group-flush">';
        communes.forEach(c => {
            const label = (c.nom || c.libelle || c.code || ('ID ' + c.id));
            const requiredAttr = c.obligatoire ? ' checked' : '';
            html += '<li class="list-group-item"><input type="checkbox" class="catalogue-checkbox me-2"' + requiredAttr + ' data-id="' + (c.id || '') + '" data-label="' + (label.replace(/"/g, '\\"')) + '" data-type="commune"/>' + label + '</li>';
        });
        html += '</ul>';
    }
    html += '</div></div>';

    html += '<div class="card mb-3"><div class="card-header">Catalogue - Pièces complémentaires</div><div class="card-body">';
    if (!complementaires || complementaires.length === 0) html += '<p class="text-muted">Aucune pièce complémentaire pour ce type.</p>';
    else {
        html += '<ul class="list-group list-group-flush">';
        complementaires.forEach(c => {
            const label = (c.nom || c.libelle || c.code || ('ID ' + c.id));
            const requiredAttr = c.obligatoire ? ' checked' : '';
            html += '<li class="list-group-item"><input type="checkbox" class="catalogue-checkbox me-2"' + requiredAttr + ' data-id="' + (c.id || '') + '" data-label="' + (label.replace(/"/g, '\\"')) + '" data-type="complementaire"/>' + label + '</li>';
        });
        html += '</ul>';
    }
    html += '</div></div>';

    container.innerHTML = html;
    // attach a delegated listener to catalogue container to log checked items
    setupCatalogueCheckboxListeners();

    // Fallback: attach direct listeners to checkboxes in case delegation misses (ensures immediate logging)
    try {
        const boxes = container.querySelectorAll('.catalogue-checkbox');
        boxes.forEach(cb => {
            cb.removeEventListener('change', onCatalogueChange);
            cb.addEventListener('change', function(e) {
                try { console.log('direct checkbox change, id=', cb.getAttribute('data-id'), 'checked=', cb.checked); } catch (err) {}
                onCatalogueChange();
            });
        });
    } catch (err) {
        console.warn('Could not attach direct checkbox listeners', err);
    }

    // Synchroniser formState avec les cases pré-cochées (obligatoires)
    onCatalogueChange();
}

function setupCatalogueCheckboxListeners() {
    const container = document.getElementById('catalogueContainer');
    if (!container) return;
    // avoid adding duplicate delegated listener
    if (!container._catalogueListenerAdded) {
        const handler = function (e) {
            const cb = e.target && e.target.closest ? e.target.closest('.catalogue-checkbox') : (e.target && e.target.matches && e.target.matches('.catalogue-checkbox') ? e.target : null);
            if (cb) {
                onCatalogueChange();
            }
        };
        container.addEventListener('change', handler);
        container.addEventListener('click', handler);
        container._catalogueListenerAdded = true;
    }
}

function onCatalogueChange() {
    const checked = Array.from(document.querySelectorAll('.catalogue-checkbox:checked')).map(cb => ({
        id: cb.getAttribute('data-id'),
        label: cb.getAttribute('data-label'),
        type: cb.getAttribute('data-type')
    }));
    console.log('Catalogue checked items:', checked);

    // split by type
    const communes = checked.filter(i => i.type === 'commune').map(i => Number(i.id));
    const complementaires = checked.filter(i => i.type === 'complementaire').map(i => Number(i.id));

    // Store selections in formState; actual save will occur when creating the dossier
    formState.selectedCommunes = communes;
    formState.selectedComplementaires = complementaires;
    console.log('Selections stored in formState.selectedCommunes / selectedComplementaires');
}

function uploadPiece(pieceId, dossierId, type) {
    // Validation des paramètres
    if (!pieceId || pieceId === undefined || pieceId === 'undefined') {
        showAlert('Erreur: ID pièce invalide', 'danger');
        return;
    }
    if (!dossierId || dossierId === undefined || dossierId === 'undefined') {
        showAlert('Erreur: ID dossier invalide. Veuillez créer le dossier d\'abord.', 'danger');
        return;
    }

    const input = document.createElement('input');
    input.type = 'file';
    input.onchange = async function(e) {
        const file = e.target.files[0];
        if (!file) return;

        const formData = new FormData();
        formData.append('file', file);

        try {
            const safeType = (type === 'complementaires') ? 'complementaires' : 'communes';
            const url = API_BASE_URL + '/dossiers/' + dossierId + '/pieces/' + safeType + '/' + pieceId + '/upload';
            console.log('Upload vers:', url);

            const response = await fetch(url, {
                method: 'POST',
                body: formData
            });

            if (response.ok) {
                showAlert('Fichier uploadé avec succès.', 'success');
                loadPieces(); // Recharger les pièces
            } else {
                const errText = await response.text();
                formState.apiError = true;
                throw new Error('Erreur HTTP ' + response.status + ': ' + errText);
            }
        } catch (error) {
            console.error('Erreur upload:', error);
            formState.apiError = true;
            showAlert('Erreur lors de l\'upload: ' + error.message, 'danger');
        }
    };
    input.click();
}

// ============================================
// SOUMISSION DU FORMULAIRE
// ============================================

async function submitForm() {
    try {
        formState.apiError = false;

        // Toujours synchroniser les sélections avant envoi
        onCatalogueChange();

        // Vérifier les IDs nécessaires
        if (!formState.demandeurId) throw new Error('ID demandeur manquant');
        if (!formState.passeportId) throw new Error('ID passeport manquant');
        if (!formState.visaTransformableId) throw new Error('ID visa manquant');

        // 1) Créer une demande
        const demandeData = {
            demandeurId: formState.demandeurId,
            passeportId: formState.passeportId,
            idVisaTransformable: formState.visaTransformableId,
            typeDemandeId: document.getElementById('typeDemande').value
        };

        const demandeResponse = await fetch(API_BASE_URL + '/demandes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(demandeData)
        });
        if (!demandeResponse.ok) {
            const errText = await demandeResponse.text();
            throw new Error('Erreur création demande: ' + errText);
        }
        const demande = await demandeResponse.json();
        if (!demande || !demande.id) throw new Error('Demande créée mais sans ID');
        formState.demandeId = demande.id;

        // 2) Créer le dossier
        const dossierResponse = await fetch(API_BASE_URL + '/dossiers', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ demandeId: formState.demandeId, createdBy: 'frontend' })
        });
        if (!dossierResponse.ok) {
            const errText = await dossierResponse.text();
            throw new Error('Erreur création dossier: ' + errText);
        }
        const dossierRespBody = await dossierResponse.json();
        const dossier = dossierRespBody && dossierRespBody.dossier ? dossierRespBody.dossier : dossierRespBody;
        if (!dossier || !dossier.id) throw new Error('Réponse invalide du serveur: ID dossier manquant');
        formState.dossierId = dossier.id;

        // 3) Insérer les pièces via les endpoints dédiés (même si liste vide pour forcer la validation backend)
        const communes = Array.isArray(formState.selectedCommunes) ? formState.selectedCommunes : [];
        const complementaires = Array.isArray(formState.selectedComplementaires) ? formState.selectedComplementaires : [];

        const communesResp = await fetch(API_BASE_URL + '/dossiers/' + formState.dossierId + '/pieces/communes/create', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(communes)
        });
        if (!communesResp.ok) {
            const errText = await communesResp.text();
            throw new Error('Erreur création pièces communes: ' + errText);
        }

        const compResp = await fetch(API_BASE_URL + '/dossiers/' + formState.dossierId + '/pieces/complementaires/create', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(complementaires)
        });
        if (!compResp.ok) {
            const errText = await compResp.text();
            throw new Error('Erreur création pièces complémentaires: ' + errText);
        }

        // 4) Charger les pièces du dossier et afficher le succès
        await loadPieces();
        showSuccessModal(formState.dossierId);
    } catch (error) {
        formState.apiError = true;
        console.error('❌ Erreur submitForm:', error);
        showAlert(error.message || 'Erreur lors de la création du dossier', 'danger');
    }
}

function updatePiecesProgress(pieces) {
    const totalPieces = Array.isArray(pieces) ? pieces.length : 0;
    const piecesFournies = Array.isArray(pieces)
        ? pieces.filter(p => p && p.statutPiece && String(p.statutPiece.code || '').toUpperCase() === 'FOURNI').length
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

function resetForm() {
    document.getElementById('mainForm').reset();
    formState = {
        currentStep: 1,
        demandeurId: null,
        passeportId: null,
        visaTransformableId: null,
        demandeId: null,
        dossierId: null,
        selectedCommunes: [],
        selectedComplementaires: [],
        apiError: false,
        formData: {
            demandeur: {},
            passeport: {},
            visaTransformable: {},
            demande: {}
        }
    };
    moveToStep(1);
    hideSuccessModal();
}

// ============================================
// UTILITAIRES
// ============================================

function showAlert(message, type) {
    const alertContainer = document.getElementById('alertContainer');
    const alertDiv = document.createElement('div');
    alertDiv.className = 'alert alert-' + type + ' alert-dismissible fade show';
    alertDiv.role = 'alert';
    alertDiv.innerHTML = message + '<button type="button" class="btn-close" data-bs-dismiss="alert"></button>';
    
    alertContainer.appendChild(alertDiv);

    // Auto-close après 5 secondes
    setTimeout(() => {
        alertDiv.remove();
    }, 5000);
}

function showSuccessModal(dossierId) {
    document.getElementById('dossierId').textContent = dossierId;
    const modal = new bootstrap.Modal(document.getElementById('successModal'));
    modal.show();
}

function hideSuccessModal() {
    const modal = bootstrap.Modal.getInstance(document.getElementById('successModal'));
    if (modal) {
        modal.hide();
    }
}

function scrollToTop() {
    document.querySelector('.container').scrollIntoView({ behavior: 'smooth', block: 'start' });
}
