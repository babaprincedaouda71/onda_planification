# UML Class Diagram — Système de Planification des Contrôleurs Aériens

---

## Entités (Classes)

### 1. `Controleur`

| Attribut | Type | Contrainte |
|---|---|---|
| idControleur | int | Clé primaire `<>` |
| nom | string | |
| niveauVigilance | enum | `FAIBLE`, `MODERE`, `ELEVE` |

---

### 2. `Alerte`

| Attribut | Type | Contrainte |
|---|---|---|
| idAlerte | int | |
| typeAlerte | string | |
| niveau | enum | `INFO`, `ALERTE`, `CRITIQUE` |

---

### 3. `Equipe`

| Attribut | Type | Contrainte |
|---|---|---|
| IdEquipe | int | Clé primaire `<>` |
| nomEquipe | string | |
| maxMembres | int | |
| membresEquipe | list | |

---

### 4. `Shift`

| Attribut | Type | Contrainte |
|---|---|---|
| idShift | int | |
| date | date | |
| typeShift | enum | `MATIN`, `APRES_MIDI`, `NUIT` |

---

### 5. `Secteur`

| Attribut | Type | Contrainte |
|---|---|---|
| idSecteur | int | |
| complexite | enum | `FAIBLE`, `MOYENNE`, `FORTE` |

---

### 6. `Affectation`

| Attribut | Type | Contrainte |
|---|---|---|
| idAffectation | int | |
| role | enum | `EXECUTANT`, `ASSISTANT` |

---

## Relations

| Source | Relation | Cible | Type |
|---|---|---|---|
| Controleur | génère | Alerte | Agrégation |
| Alerte | concerne | Controleur | Association |
| Alerte | concerne | Equipe | Association |
| Controleur | appartient / contient | Equipe | Agrégation |
| Equipe | possède | Shift | Agrégation |
| Shift | contient | Secteur | Agrégation |
| Secteur | compose | Affectation | Composition |
| Controleur | assigne | Affectation | Association |
