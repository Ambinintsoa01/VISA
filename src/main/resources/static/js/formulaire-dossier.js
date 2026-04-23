// Configuration de l'API
const API_BASE_URL = '/api';

// État global du formulaire
let formState = {
    currentStep: 1,
    demandeurId: null,
    passeportId: null,
    visaTransformableId: null,
    dossierId: null,
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
        const [nationalites, situations, typeIdentites] = await Promise.all([
            fetch(API_BASE_URL + '/ref/nationalites').then(r => r.json()),
            fetch(API_BASE_URL + '/ref/situations-familiales').then(r => r.json()),
            fetch(API_BASE_URL + '/ref/types-identite').then(r => r.json())
        ]);

        populateSelect('nationalite', nationalites);
        populateSelect('situationFamiliale', situations);
        populateSelect('typeIdentite', typeIdentites);
    } catch (error) {
        console.error('Erreur lors du chargement des données:', error);
        showAlert('Erreur lors du chargement des listes déroulantes.', 'danger');
    }
}

function populateSelect(selectId, data) {
    const select = document.getElementById(selectId);
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
            typeVisa: document.getElementById('typeVisa').value,
            dateDebut: document.getElementById('dateDebutVisa').value,
            dateFin: document.getElementById('dateFinVisa').value,
            typeIdentiteId: document.getElementById('typeIdentite').value
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
        console.log('formState:', formState);
        
        // Vérifier les IDs nécessaires
        if (!formState.demandeurId) {
            throw new Error('ID demandeur manquant');
        }
        console.log('✓ Demandeur ID:', formState.demandeurId);
        
        if (!formState.passeportId) {
            throw new Error('ID passeport manquant');
        }
        console.log('✓ Passeport ID:', formState.passeportId);
        
        if (!formState.visaTransformableId) {
            throw new Error('ID visa manquant');
        }
        console.log('✓ Visa ID:', formState.visaTransformableId);

        // D'abord, créer une demande
        console.log('📝 Création de la demande...');
        const demandeData = {
            demandeurId: formState.demandeurId,
            typeDemandeId: 1 // Valeur par défaut, adapter selon vos données
        };

        const demandeResponse = await fetch(API_BASE_URL + '/demandes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(demandeData)
        });

        if (!demandeResponse.ok) {
            const errText = await demandeResponse.text();
            console.error('❌ Erreur création demande HTTP', demandeResponse.status, ':', errText);
            throw new Error('Erreur création demande: ' + errText);
        }

        const demande = await demandeResponse.json();
        console.log('✓ Demande créée, ID:', demande.id);
        
        if (!demande.id) {
            throw new Error('Demande créée mais sans ID');
        }

        // Créer le dossier
        console.log('📋 Création du dossier...');
        const dossierData = {
            demandeId: demande.id,
            createdBy: 'frontend'
        };

        if (!dossierData.demandeId) {
            throw new Error('Demande ID invalide');
        }
        console.log('Envoi au serveur:', dossierData);

        const dossierResponse = await fetch(API_BASE_URL + '/dossiers', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dossierData)
        });

        if (!dossierResponse.ok) {
            const errText = await dossierResponse.text();
            console.error('❌ Erreur création dossier HTTP', dossierResponse.status, ':', errText);
            throw new Error('Erreur création dossier: ' + errText);
        }

        const dossierRespBody = await dossierResponse.json();
        console.log('Réponse serveur:', dossierRespBody);
        
        const dossier = dossierRespBody.dossier || dossierRespBody;
        
        if (!dossier || !dossier.id) {
            console.error('❌ Réponse dossier invalide:', dossierRespBody);
            throw new Error('Réponse invalide du serveur: ID dossier manquant');
        }
        
        formState.dossierId = dossier.id;
        console.log('✓ Dossier créé avec ID:', formState.dossierId);

        // Charger les pièces du dossier
        console.log('🗂️ Chargement des pièces...');
        if (!formState.dossierId || formState.dossierId === undefined) {
            throw new Error('dossierId invalide: ' + formState.dossierId);
        }

        const piecesResponse = await fetch(API_BASE_URL + '/dossiers/' + formState.dossierId + '/pieces');
        
        if (!piecesResponse.ok) {
            const errText = await piecesResponse.text();
            console.error('❌ Erreur chargement pièces HTTP', piecesResponse.status, ':', errText);
            throw new Error('Erreur chargement pièces: ' + errText);
        }

        const pieces = await piecesResponse.json();
        console.log('✓ Pièces chargées:', pieces);

        displayPieces(pieces);
        showAlert('Étape 3: Pièces chargées avec succès!', 'success');
        
    } catch (error) {
        console.error('❌ ERREUR ÉTAPE 3:', error);
        showAlert('Erreur Étape 3: ' + error.message, 'danger');
    }
}

function displayPieces(pieces) {
    const piecesList = document.getElementById('piecesList');
    piecesList.innerHTML = '';

    if (!pieces || pieces.length === 0) {
        piecesList.innerHTML = '<p class="text-muted">Aucune pièce requise.</p>';
        return;
    }

    const table = document.createElement('div');
    table.className = 'table-responsive';

    let html = '<table class="table table-striped">';
    html += '<thead>';
    html += '<tr><th>Pièce</th><th>Statut</th><th>Action</th></tr>';
    html += '</thead><tbody>';

    pieces.forEach(piece => {
        const statusBadgeClass = piece.statut === 'FOURNI' ? 'bg-success' : 'bg-warning';
        const statusLabel = piece.statut === 'FOURNI' ? '✓ Fourni' : '✗ Non fourni';
        
        html += '<tr>';
        html += '<td>' + (piece.nom || piece.libelle || 'Pièce ' + piece.id) + '</td>';
        html += '<td><span class="badge ' + statusBadgeClass + '">' + statusLabel + '</span></td>';
        html += '<td>';
        if (piece.statut !== 'FOURNI') {
            html += '<button type="button" class="btn btn-sm btn-outline-primary" onclick="uploadPiece(' + piece.id + ', ' + formState.dossierId + ')">Télécharger</button>';
        }
        html += '</td>';
        html += '</tr>';
    });

    html += '</tbody></table>';
    table.innerHTML = html;
    piecesList.appendChild(table);

    updatePiecesProgress(pieces);
}

function updatePiecesProgress(pieces) {
    const totalPieces = pieces.length;
    const piecesAffournies = pieces.filter(p => p.statut === 'FOURNI').length;
    const progressPercent = totalPieces > 0 ? (piecesAffournies / totalPieces) * 100 : 0;

    document.getElementById('progressPieces').style.width = progressPercent + '%';
    document.getElementById('progressPiecesText').textContent = Math.round(progressPercent) + '%';
    document.getElementById('piecesFournies').textContent = piecesAffournies;
    document.getElementById('piecesTotal').textContent = totalPieces;
}

function uploadPiece(pieceId, dossierId) {
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
            const url = API_BASE_URL + '/dossiers/' + dossierId + '/pieces/communes/' + pieceId + '/upload';
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
                throw new Error('Erreur HTTP ' + response.status + ': ' + errText);
            }
        } catch (error) {
            console.error('Erreur upload:', error);
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
        // Valider dossierId
        if (!formState.dossierId || formState.dossierId === undefined) {
            showAlert('Erreur: ID dossier invalide. Veuillez créer le dossier d\'abord.', 'danger');
            return;
        }

        // Vérifier que toutes les pièces sont fournies
        const completudeResponse = await fetch(API_BASE_URL + '/dossiers/' + formState.dossierId + '/completude');
        
        if (!completudeResponse.ok) {
            const errText = await completudeResponse.text();
            throw new Error('Erreur vérification complétude: ' + errText);
        }

        const completudeData = await completudeResponse.json();
        const isComplete = completudeData.completude === true;

        if (!isComplete) {
            showAlert('Toutes les pièces justificatives doivent être fournies avant de valider le dossier.', 'warning');
            return;
        }

        // Mettre à jour le statut du dossier
        const updateResponse = await fetch(
            API_BASE_URL + '/dossiers/' + formState.dossierId + '/statut',
            {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ code: 'APPROUVE' })
            }
        );

        if (updateResponse.ok) {
            showSuccessModal(formState.dossierId);
        } else {
            const errText = await updateResponse.text();
            throw new Error('Erreur lors de la validation du dossier: ' + errText);
        }
    } catch (error) {
        console.error('Erreur submitForm:', error);
        showAlert('Erreur: ' + error.message, 'danger');
    }
}

function resetForm() {
    document.getElementById('mainForm').reset();
    formState = {
        currentStep: 1,
        demandeurId: null,
        passeportId: null,
        visaTransformableId: null,
        dossierId: null,
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
