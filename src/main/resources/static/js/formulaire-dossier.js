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

function goToNextStep() {
    if (validateCurrentStep()) {
        if (formState.currentStep === 1) {
            saveDemandeur();
        } else if (formState.currentStep === 2) {
            savePasseportAndVisa();
        }
        moveToStep(formState.currentStep + 1);
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

        formState.formData.demandeur = demandeurData;

        const response = await fetch(API_BASE_URL + '/demandeurs', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(demandeurData)
        });

        if (response.ok) {
            const demandeur = await response.json();
            formState.demandeurId = demandeur.id;
            showAlert('Informations d\'état civil enregistrées.', 'success');
        } else {
            const error = await response.text();
            throw new Error(error);
        }
    } catch (error) {
        console.error('Erreur:', error);
        showAlert('Erreur lors de l\'enregistrement du demandeur: ' + error.message, 'danger');
        throw error;
    }
}

// ============================================
// SAUVEGARDE DES DONNÉES - ÉTAPE 2
// ============================================

async function savePasseportAndVisa() {
    try {
        // Sauvegarder le passeport
        const passeportData = {
            demandeurId: formState.demandeurId,
            numero: document.getElementById('numeroPasseport').value,
            dateEmission: document.getElementById('dateEmissionPasseport').value,
            dateExpiration: document.getElementById('dateExpirationPasseport').value,
            lieuEmission: document.getElementById('lieuEmissionPasseport').value
        };

        const passeportResponse = await fetch(API_BASE_URL + '/passeports', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(passeportData)
        });

        if (passeportResponse.ok) {
            const passeport = await passeportResponse.json();
            formState.passeportId = passeport.id;
        } else {
            throw new Error('Erreur lors de l\'enregistrement du passeport');
        }

        // Sauvegarder le visa transformable
        const visaData = {
            passeportId: formState.passeportId,
            typeVisa: document.getElementById('typeVisa').value,
            dateDebut: document.getElementById('dateDebutVisa').value,
            dateFin: document.getElementById('dateFinVisa').value,
            typeIdentiteId: document.getElementById('typeIdentite').value
        };

        const visaResponse = await fetch(API_BASE_URL + '/visas', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(visaData)
        });

        if (visaResponse.ok) {
            const visa = await visaResponse.json();
            formState.visaTransformableId = visa.id;
            showAlert('Passeport et Visa enregistrés.', 'success');
        } else {
            throw new Error('Erreur lors de l\'enregistrement du visa');
        }
    } catch (error) {
        console.error('Erreur:', error);
        showAlert('Erreur lors de l\'enregistrement: ' + error.message, 'danger');
        throw error;
    }
}

// ============================================
// CHARGEMENT DES PIÈCES - ÉTAPE 3
// ============================================

async function loadPieces() {
    try {
        // D'abord, créer une demande
        const demandeData = {
            demandeurId: formState.demandeurId,
            typeDemandeId: 1 // Valeur par défaut, adapter selon vos données
        };

        const demandeResponse = await fetch(API_BASE_URL + '/demandes', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(demandeData)
        });

        const demande = await demandeResponse.json();

        // Créer le dossier
        const dossierData = {
            demandeId: demande.id,
            demandeurId: formState.demandeurId,
            visaTransformableId: formState.visaTransformableId
        };

        const dossierResponse = await fetch(API_BASE_URL + '/dossiers', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(dossierData)
        });

        const dossier = await dossierResponse.json();
        formState.dossierId = dossier.id;

        // Charger les pièces du dossier
        const piecesResponse = await fetch(API_BASE_URL + '/dossiers/' + dossier.id + '/pieces');
        const pieces = await piecesResponse.json();

        displayPieces(pieces);
    } catch (error) {
        console.error('Erreur lors du chargement des pièces:', error);
        showAlert('Erreur lors du chargement des pièces: ' + error.message, 'danger');
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
    const input = document.createElement('input');
    input.type = 'file';
    input.onchange = async function(e) {
        const file = e.target.files[0];
        const formData = new FormData();
        formData.append('file', file);

        try {
            const response = await fetch(
                API_BASE_URL + '/dossiers/' + dossierId + '/pieces/communes/' + pieceId + '/upload',
                {
                    method: 'POST',
                    body: formData
                }
            );

            if (response.ok) {
                showAlert('Fichier uploadé avec succès.', 'success');
                loadPieces(); // Recharger les pièces
            } else {
                throw new Error('Erreur lors de l\'upload');
            }
        } catch (error) {
            console.error('Erreur:', error);
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
        // Vérifier que toutes les pièces sont fournies
        const completudeResponse = await fetch(API_BASE_URL + '/dossiers/' + formState.dossierId + '/completude');
        const completudeData = await completudeResponse.json();

        if (!completudeData.complet) {
            showAlert('Toutes les pièces justificatives doivent être fournies avant de valider le dossier.', 'warning');
            return;
        }

        // Mettre à jour le statut du dossier
        const updateResponse = await fetch(
            API_BASE_URL + '/dossiers/' + formState.dossierId + '/statut',
            {
                method: 'PUT',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ statut: 'APPROUVE' })
            }
        );

        if (updateResponse.ok) {
            showSuccessModal(formState.dossierId);
        } else {
            throw new Error('Erreur lors de la validation du dossier');
        }
    } catch (error) {
        console.error('Erreur:', error);
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
