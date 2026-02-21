# JellyDrink — Documentazione Tecnica

> Versione DB: **6** · Stack: Kotlin · Jetpack Compose · Room · Hilt · DataStore · AlarmManager · WorkManager

---

## Indice

1. [Panoramica Generale](#1-panoramica-generale)
2. [Architettura](#2-architettura)
3. [Database — Entità e Migrazioni](#3-database--entità-e-migrazioni)
4. [DAO — Data Access Objects](#4-dao--data-access-objects)
5. [Repository — Business Logic](#5-repository--business-logic)
6. [ViewModel](#6-viewmodel)
7. [Schermate UI](#7-schermate-ui)
8. [Componenti UI Riutilizzabili](#8-componenti-ui-riutilizzabili)
9. [Navigazione](#9-navigazione)
10. [Sistema di Notifiche e Widget](#10-sistema-di-notifiche-e-widget)
11. [Background Workers e Receivers](#11-background-workers-e-receivers)
12. [Dependency Injection](#12-dependency-injection)
13. [Tema e Stile](#13-tema-e-stile)
14. [Formule e Costanti di Business](#14-formule-e-costanti-di-business)
15. [Struttura File del Progetto](#15-struttura-file-del-progetto)

---

## 1. Panoramica Generale

**JellyDrink** è un'app Android di gamification per il monitoraggio dell'assunzione giornaliera d'acqua. L'utente registra ogni consumo d'acqua, accumulando XP e salendo di livello. Una medusa animata si riempie proporzionalmente all'obiettivo giornaliero. L'app gamifica l'idratazione attraverso:

- **Sistema XP e Livelli** — ogni ml bevuto genera XP, con bonus streak e bonus goal
- **26 Badge** in 6 categorie, sbloccati automaticamente al raggiungimento di soglie
- **Sfide giornaliere** — una challenge diversa ogni giorno, 5 tipologie
- **Acquario personalizzabile** — 8 decorazioni acquistabili con XP spendibili
- **Storico visivo** — grafico settimanale e dettaglio 30 giorni
- **Notifica lock screen persistente** — mostra il progresso sempre visibile
- **Widget home screen** — percentuale obiettivo raggiunto

---

## 2. Architettura

L'app segue una clean architecture a tre layer:

```
UI Layer          →  Screens + Composables (Jetpack Compose)
ViewModel Layer   →  State management (StateFlow, collectAsStateWithLifecycle)
Data Layer        →  Repository → DAOs → Room Database + DataStore
```

**Tecnologie principali:**

| Tecnologia | Utilizzo |
|---|---|
| Jetpack Compose | UI dichiarativa |
| Room | Database SQLite tipizzato |
| Hilt | Dependency Injection |
| DataStore (Preferences) | Impostazioni utente (goal, bicchieri, notifiche) |
| Kotlin Coroutines + Flow | Async e reactive state |
| AlarmManager | Reset preciso a mezzanotte |
| WorkManager | Worker periodici (reminder, streak danger) |
| AppWidgetProvider | Widget home screen |

---

## 3. Database — Entità e Migrazioni

**File:** `data/db/AppDatabase.kt`
**Versione corrente:** 6
**Tabelle:** 7

---

### 3.1 Tabelle

#### `water_intake` — Registrazioni acqua
**Entity:** `data/db/entity/WaterIntakeEntity.kt`

| Campo | Tipo | Note |
|---|---|---|
| `id` | `Long` | PrimaryKey, autoGenerate |
| `date` | `String` | Formato `yyyy-MM-dd` |
| `amountMl` | `Int` | Quantità in millilitri |
| `timestamp` | `Long` | `System.currentTimeMillis()` |

---

#### `user_profile` — Profilo utente (singleton)
**Entity:** `data/db/entity/UserProfileEntity.kt`

| Campo | Tipo | Note |
|---|---|---|
| `id` | `Int` | PrimaryKey fisso = 1 |
| `xp` | `Int` | XP totali storici (usati per calcolare il livello) |
| `spendableXp` | `Int` | XP spendibili nel negozio (si decrementano agli acquisti) |
| `level` | `Int` | Livello corrente (ricalcolato a ogni addWater) |
| `totalMlAllTime` | `Int` | Millilitri totali da sempre |
| `bestStreak` | `Int` | Miglior streak giornaliero consecutivo |
| `activeDays` | `Int` | Giorni in cui è stato registrato almeno un intake |
| `dailyRecord` | `Int` | Massimo ml in un singolo giorno |
| `lastActiveDate` | `String` | Data ultimo giorno attivo (`yyyy-MM-dd`) |

---

#### `badges` — Badge sbloccati
**Entity:** `data/db/entity/BadgeEntity.kt`

| Campo | Tipo | Note |
|---|---|---|
| `id` | `Long` | PrimaryKey, autoGenerate |
| `type` | `String` | Identificativo univoco del badge (es. `"first_sip"`) |
| `dateEarned` | `String` | Data sblocco (`yyyy-MM-dd`) |
| `description` | `String` | Testo descrittivo |

---

#### `daily_challenges` — Sfida giornaliera
**Entity:** `data/db/entity/DailyChallengeEntity.kt`

| Campo | Tipo | Note |
|---|---|---|
| `date` | `String` | PrimaryKey (`yyyy-MM-dd`) |
| `type` | `String` | Tipo challenge: `early_bird`, `consistent`, `big_gulp`, `afternoon_goal`, `full_tank` |
| `targetValue` | `Int` | Soglia numerica (ml, count, percentuale) |
| `currentProgress` | `Int` | Progresso corrente |
| `completed` | `Boolean` | `true` se challenge completata |
| `xpReward` | `Int` | XP guadagnati al completamento |

---

#### `jellyfish_collection` — Meduse (e pesce palla futuro)
**Entity:** `data/db/entity/JellyfishEntity.kt`

| Campo | Tipo | Note |
|---|---|---|
| `id` | `String` | PrimaryKey — valore fisso `"rosa"` |
| `nameIt` | `String` | Nome in italiano |
| `unlocked` | `Boolean` | Se sbloccata |
| `selected` | `Boolean` | Se attualmente selezionata |
| `unlockCondition` | `String` | Descrizione della condizione di sblocco |
| `dateUnlocked` | `String?` | Data sblocco (nullable) |
| `cost` | `Int` | Costo in XP spendibili |

---

#### `decorations` — Decorazioni acquario
**Entity:** `data/db/entity/DecorationEntity.kt`

| Campo | Tipo | Note |
|---|---|---|
| `id` | `String` | PrimaryKey (es. `"fish_blue"`, `"treasure"`) |
| `nameIt` | `String` | Nome in italiano |
| `cost` | `Int` | Costo in XP spendibili |
| `owned` | `Boolean` | Se l'utente l'ha acquistata |
| `placed` | `Boolean` | Se è attivamente visualizzata nell'acquario |

---

#### `daily_goal` — Snapshot goal giornaliero
**Entity:** `data/db/entity/DailyGoalEntity.kt`

| Campo | Tipo | Note |
|---|---|---|
| `date` | `String` | PrimaryKey (`yyyy-MM-dd`) |
| `goalMl` | `Int` | Goal in ml per quel giorno (snapshot al momento del primo intake) |

> Questa tabella permette allo storico di mostrare la percentuale corretta anche se l'utente cambia il goal nel tempo. Ogni giorno viene registrato il goal attivo al momento.

---

### 3.2 Migrazioni

| Migrazione | Cambiamenti |
|---|---|
| **1 → 2** | Creazione tabelle `user_profile`, `daily_challenges`, `jellyfish_collection`, `decorations` |
| **2 → 3** | Rimozione colonne obsolete da `user_profile` (`jellyfishStage`, `consecutiveActiveDays`) tramite ricreazione tabella |
| **3 → 4** | Aggiunta colonna `cost` a `jellyfish_collection` |
| **4 → 5** | Aggiunta colonna `spendableXp` a `user_profile`, inizializzata con il valore di `xp` esistente |
| **5 → 6** | Creazione tabella `daily_goal` per snapshot storico dei goal giornalieri |

---

## 4. DAO — Data Access Objects

Tutti i DAO si trovano in `data/db/dao/`.

---

### 4.1 `WaterIntakeDao.kt`

| Metodo | Tipo | Descrizione |
|---|---|---|
| `insert(intake)` | `suspend` | Inserisce un nuovo intake |
| `getTotalForDate(date)` | `Flow<Int>` | Somma ml per data (reactive) |
| `getIntakesForDate(date)` | `Flow<List<...>>` | Lista intakes del giorno, desc per timestamp |
| `getDatesWithGoalMet(fallback)` | `suspend List<String>` | Date in cui il goal è stato raggiunto (LEFT JOIN con `daily_goal`) |
| `getDailySummary(start, end)` | `suspend List<DailySummary>` | Riepilogo per range: `SELECT date, SUM(amountMl) GROUP BY date` |
| `getTotalEntries()` | `suspend Int` | Conteggio totale righe |
| `deleteAll()` | `suspend` | Cancella tutto (usato in reset) |

**DTO associato:**
```kotlin
data class DailySummary(val date: String, val totalMl: Int)
```

La query `getDatesWithGoalMet` usa un **LEFT JOIN** con `daily_goal` per confrontare ogni giorno con il goal effettivo di quel giorno (non quello attuale), con fallback al goal globale:
```sql
SELECT wi.date FROM (
    SELECT date, SUM(amountMl) as totalMl FROM water_intake GROUP BY date
) wi LEFT JOIN daily_goal dg ON wi.date = dg.date
WHERE wi.totalMl >= COALESCE(dg.goalMl, :fallbackGoal)
ORDER BY wi.date DESC
```

---

### 4.2 `UserProfileDao.kt`

| Metodo | Tipo | Descrizione |
|---|---|---|
| `getProfile()` | `Flow<UserProfileEntity?>` | Profilo reattivo (id=1) |
| `getProfileSync()` | `suspend UserProfileEntity?` | Profilo in modo sospeso |
| `upsert(profile)` | `suspend` | Insert or Replace (OnConflict.REPLACE) |
| `deleteAll()` | `suspend` | Cancella profilo (usato in reset) |

---

### 4.3 `BadgeDao.kt`

| Metodo | Tipo | Descrizione |
|---|---|---|
| `getAllBadges()` | `Flow<List<BadgeEntity>>` | Tutti i badge sbloccati (reactive) |
| `getBadgeByType(type)` | `suspend BadgeEntity?` | Badge specifico per tipo |
| `insert(badge)` | `suspend` | Inserisce nuovo badge |
| `deleteAll()` | `suspend` | Cancella tutti i badge (usato in reset) |

---

### 4.4 `DailyChallengeDao.kt`

| Metodo | Tipo | Descrizione |
|---|---|---|
| `getChallengeForDate(date)` | `Flow<DailyChallengeEntity?>` | Challenge del giorno (reactive) |
| `getChallengeForDateSync(date)` | `suspend DailyChallengeEntity?` | Challenge del giorno (sospeso) |
| `insert(challenge)` | `suspend` | Inserisce nuova challenge (IGNORE conflict) |
| `update(challenge)` | `suspend` | Aggiorna progresso/completamento |
| `getCompletedCount()` | `suspend Int` | Totale challenge completate (per badge) |
| `deleteAll()` | `suspend` | Cancella tutto (usato in reset) |

---

### 4.5 `DecorationDao.kt`

| Metodo | Tipo | Descrizione |
|---|---|---|
| `getAllDecorations()` | `Flow<List<DecorationEntity>>` | Tutte le decorazioni (reactive) |
| `getPlacedDecorations()` | `Flow<List<DecorationEntity>>` | Solo decorazioni con `placed=true` |
| `insert(decoration)` | `suspend` | Inserisce decorazione (IGNORE conflict) |
| `update(decoration)` | `suspend` | Aggiorna stato (owned/placed) |
| `deleteAll()` | `suspend` | Cancella tutto (usato in reset) |

---

### 4.6 `JellyfishDao.kt`

| Metodo | Tipo | Descrizione |
|---|---|---|
| `getSelectedJellyfish()` | `Flow<JellyfishEntity?>` | Medusa attualmente selezionata |
| `getJellyfishById(id)` | `Flow<JellyfishEntity?>` | Medusa per ID (reactive) |
| `getJellyfishByIdSync(id)` | `suspend JellyfishEntity?` | Medusa per ID (sospeso) |
| `insert(jellyfish)` | `suspend` | Inserisce medusa (IGNORE conflict) |
| `unlock(id, date)` | `suspend` | Imposta `unlocked=1` e `dateUnlocked` |
| `getCount()` | `suspend Int` | Numero meduse in collezione |

---

### 4.7 `DailyGoalDao.kt`

| Metodo | Tipo | Descrizione |
|---|---|---|
| `insert(goal)` | `suspend` | Inserisce snapshot goal (IGNORE conflict — primo del giorno vince) |
| `getGoalsForRange(start, end)` | `suspend List<DailyGoalEntity>` | Goal per range di date |
| `deleteAll()` | `suspend` | Cancella tutto (usato in reset) |

---

## 5. Repository — Business Logic

**File:** `data/repository/WaterRepository.kt`
**Singleton via Hilt.** Unico punto di accesso alla logica di business. Orchestrà tutti i DAO, gestisce DataStore per le preferenze, e implementa il sistema XP/livelli/badge/challenge.

---

### 5.1 Inizializzazione

```
initializeData()
  ├── initProfile()               — Inserisce profilo singleton (id=1) se non esiste
  ├── initJellyfishCollection()   — Inserisce medusa "rosa" come default selezionata
  ├── initDecorations()           — Inserisce le 8 decorazioni con IGNORE conflict
  ├── generateDailyChallenge()    — Crea challenge del giorno se non esiste
  └── seedDailyGoals()            — Retroattivamente popola daily_goal per giorni storici senza snapshot
```

---

### 5.2 Registrazione Acqua

**Metodo:** `suspend fun addWaterIntake(amountMl: Int)`

Flusso completo:

```
1. Insert WaterIntakeEntity(date=oggi, amountMl, timestamp)
2. Insert DailyGoalEntity(date=oggi, goal) con IGNORE conflict
   → Il goal viene snapshotato solo al PRIMO intake del giorno
3. Calcola totalMl aggiornato per oggi
4. Calcola XP guadagnati:
     streak = calculateStreak(goal)
     multiplier = 1.0 + (min(streak, 5) * 0.1)     ← max 1.5x al 5° giorno streak
     baseXp = (amountMl / 100) * 1
     xpEarned = (baseXp * multiplier).toInt()
     if (previousTotal < goal && newTotal >= goal):
         xpEarned += 50                              ← Bonus goal, una volta al giorno
5. newXp = profile.xp + xpEarned
6. newLevel = floor(sqrt(newXp / 100.0)) + 1
7. Aggiorna UserProfileEntity:
     xp, spendableXp, level, totalMlAllTime,
     bestStreak, activeDays, dailyRecord, lastActiveDate
8. updateChallengeProgress(amountMl, newTotal, goal)
9. checkAndAwardBadges(newTotal, goal)
10. JellyfishWidget.updateAllWidgets(context)
11. WaterNotificationHelper.showWaterProgressNotification(context, newTotal, goal)
```

---

### 5.3 Sistema XP e Livelli

**Formule:**

```
Level = floor(sqrt(xp / 100)) + 1

XP richiesti per raggiungere il livello N:
  xpRequired(N) = (N - 1)² × 100

Esempi:
  Level 1  →    0 XP  (xp 0-99)
  Level 2  →  100 XP  (xp 100-399)
  Level 3  →  400 XP  (xp 400-899)
  Level 4  →  900 XP  (xp 900-1599)
  Level 5  → 1600 XP
  Level 10 → 8100 XP
```

**Metodi:**
- `fun calculateLevel(xp: Int): Int`
- `fun xpForLevel(level: Int): Int`
- `fun xpForNextLevel(currentXp: Int): Int`

---

### 5.4 Sistema Streak

**Metodo:** `suspend fun calculateStreak(goal: Int): Int`

Logica:
1. Ottiene tutte le date in cui il goal è stato raggiunto (ordinate desc)
2. Se la prima data non è oggi né ieri → streak = 0 (rotto)
3. Conta i giorni consecutivi con diff = 1 giorno tra ciascuno

**Streak Multiplier XP:**

| Streak | Moltiplicatore |
|---|---|
| 0 giorni | 1.0× |
| 1 giorno | 1.1× |
| 2 giorni | 1.2× |
| 3 giorni | 1.3× |
| 4 giorni | 1.4× |
| ≥5 giorni | 1.5× (massimo) |

---

### 5.5 Sfide Giornaliere

**Metodo:** `suspend fun generateDailyChallenge()`
Genera una challenge casuale dalla lista `CHALLENGE_TYPES` se non ne esiste già una per oggi.

**Metodo:** `private suspend fun updateChallengeProgress(amountMl, currentTotal, goal)`
Aggiorna il progresso della challenge del giorno ad ogni intake.

**5 tipi di challenge:**

| ID | Nome | Condizione | XP |
|---|---|---|---|
| `early_bird` | Mattiniero | Bevi almeno un sorso prima delle 9:00 | +30 |
| `consistent` | Costante | Effettua almeno 5 intakes in giornata | +30 |
| `big_gulp` | Gran Sorso | Un singolo intake ≥ 500ml | +35 |
| `afternoon_goal` | Efficiente | Raggiungi il goal giornaliero prima delle 15:00 | +40 |
| `full_tank` | Pieno | Raggiungi il 120% del goal giornaliero | +50 |

Quando una challenge passa da non completata a completata, gli XP vengono aggiunti al profilo (`xp` + `spendableXp`).

---

### 5.6 Sistema Badge

**Metodo:** `suspend fun checkAndAwardBadges(currentTotalMl, goal): BadgeEntity?`

Controlla in ordine le condizioni di tutti i 26 badge. Al primo badge non ancora posseduto che soddisfa la condizione, inserisce il `BadgeEntity` nel DB e lo restituisce. Solo un badge alla volta per ogni chiamata.

**Metodo:** `suspend fun getAllBadgesWithStatus(): List<BadgeWithStatus>`

Restituisce tutti i 26 badge con flag `isEarned` e `dateEarned`.

```kotlin
data class BadgeWithStatus(
    val definition: BadgeDefinition,
    val isEarned: Boolean,
    val dateEarned: String?
)

data class BadgeDefinition(
    val type: String,
    val nameIt: String,
    val descriptionIt: String,
    val emoji: String,
    val order: Int,
    val category: String
)
```

**26 Badge distribuiti in 6 categorie:**

| Categoria | Badge | Condizione |
|---|---|---|
| **Primi Passi** | Primo Sorso | Primo intake registrato |
| **Primi Passi** | Obiettivo Raggiunto | Goal raggiunto per la prima volta |
| **Streak** | Serie di 3 | Streak ≥ 3 giorni |
| **Streak** | Serie di 7 | Streak ≥ 7 giorni |
| **Streak** | Serie di 14 | Streak ≥ 14 giorni |
| **Streak** | Serie di 30 | Streak ≥ 30 giorni |
| **Streak** | Centenario | Streak ≥ 100 giorni |
| **Litri Totali** | 10 Litri | totalMlAllTime ≥ 10.000 ml |
| **Litri Totali** | 50 Litri | totalMlAllTime ≥ 50.000 ml |
| **Litri Totali** | 100 Litri | totalMlAllTime ≥ 100.000 ml |
| **Litri Totali** | 500 Litri | totalMlAllTime ≥ 500.000 ml |
| **Litri Totali** | 1000 Litri | totalMlAllTime ≥ 1.000.000 ml |
| **Giorni Attivi** | 7 Giorni | activeDays ≥ 7 |
| **Giorni Attivi** | 30 Giorni | activeDays ≥ 30 |
| **Giorni Attivi** | 100 Giorni | activeDays ≥ 100 |
| **Giorni Attivi** | 365 Giorni | activeDays ≥ 365 |
| **Livelli** | Livello 5 | level ≥ 5 |
| **Livelli** | Livello 10 | level ≥ 10 |
| **Livelli** | Livello 20 | level ≥ 20 |
| **Livelli** | Livello 50 | level ≥ 50 |
| **Sfide e Record** | 10 Sfide | challengeCompleted ≥ 10 |
| **Sfide e Record** | 50 Sfide | challengeCompleted ≥ 50 |
| **Sfide e Record** | 100 Sfide | challengeCompleted ≥ 100 |
| **Sfide e Record** | 150 Sfide | challengeCompleted ≥ 150 |
| **Sfide e Record** | 200 Sfide | challengeCompleted ≥ 200 |
| *(+1 ulteriore)* | *(varia)* | *(varia)* |

---

### 5.7 Decorazioni

**8 decorazioni acquistabili:**

| ID | Nome | Costo XP | Comportamento in acquario |
|---|---|---|---|
| `fish_blue` | Pesciolino Blu | 100 | Nuota orizzontalmente, animazione ultra-fluida 5 fasi (67-127s) |
| `fish_orange` | Pesce Pagliaccio | 200 | Come il pesce blu, colori arancione/bianco |
| `starfish` | Stella Marina | 80 | Statica sulla roccia |
| `coral_pink` | Corallo Rosa | 150 | 3 coralli biforcuti con rami |
| `treasure` | Forziere | 300 | Apertura/chiusura ciclica (4s) |
| `turtle` | Tartaruga | 500 | Occupa tutta la schermata, zampe animate |
| `seahorse` | Cavalluccio | 250 | Drift verticale gentile, 3 fasi (37-67s) |
| `crab` | Granchio | 120 | Camminata orizzontale a scalini (`atan(k)` clamp) |

**Metodi:**
- `fun getAllDecorations(): Flow<List<DecorationEntity>>`
- `fun getPlacedDecorations(): Flow<List<DecorationEntity>>`
- `suspend fun purchaseDecoration(id: String): Boolean`
- `suspend fun toggleDecorationPlaced(id: String)`

---

### 5.8 Preferenze (DataStore)

**Chiavi DataStore:**

| Chiave | Tipo | Default | Descrizione |
|---|---|---|---|
| `"daily_goal"` | `Int` | 2000 | Goal giornaliero in ml |
| `"custom_glasses"` | `String` (JSON) | `[200,500,1000]` | Bicchieri personalizzati in ml |
| `"notifications_enabled"` | `Boolean` | `true` | Notifiche reminder attive |

**Metodi:**
- `fun getDailyGoal(): Flow<Int>`
- `suspend fun setDailyGoal(goal: Int)`
- `fun getCustomGlasses(): Flow<List<Int>>`
- `suspend fun setCustomGlasses(glasses: List<Int>)`
- `fun getNotificationsEnabled(): Flow<Boolean>`
- `suspend fun setNotificationsEnabled(enabled: Boolean)`

---

### 5.9 Reset Completo

**Metodo:** `suspend fun resetAllData()`

Cancella in ordine:
1. `waterIntakeDao.deleteAll()`
2. `userProfileDao.deleteAll()`
3. `badgeDao.deleteAll()`
4. `dailyChallengeDao.deleteAll()`
5. `jellyfishDao.deleteAll()`
6. `decorationDao.deleteAll()`
7. `dailyGoalDao.deleteAll()`
8. DataStore: cancella tutte le preferenze
9. Chiama `initializeData()` per reinizializzare

---

## 6. ViewModel

Tutti i ViewModel si trovano in `viewmodel/`. Usano `@HiltViewModel` e ricevono `WaterRepository` tramite injection.

---

### 6.1 `HomeViewModel.kt`

**Scopo:** Stato principale della home screen.

```kotlin
data class HomeUiState(
    val currentMl: Int = 0,
    val goalMl: Int = 2000,
    val glasses: List<Int> = listOf(200, 500, 1000),
    val streak: Int = 0,
    val badges: List<BadgeEntity> = emptyList(),
    val newBadge: BadgeEntity? = null,       // Badge appena sbloccato
    val xp: Int = 0,
    val level: Int = 1,
    val xpForCurrentLevel: Int = 0,
    val xpForNextLevel: Int = 100,
    val todayChallenge: DailyChallengeEntity? = null,
    val placedDecorations: List<DecorationEntity> = emptyList()
) {
    val percentage: Float                    // currentMl / goalMl, clamped 0f-1f
}
```

**Metodi:**
- `fun addWater(amountMl: Int)` — chiama `repository.addWaterIntake()`, poi controlla badge e aggiorna `newBadge`
- `fun dismissBadge()` — imposta `newBadge = null`

**Fonti dati:** Combina 6 Flow con `combine()` in un unico `StateFlow<HomeUiState>`.

---

### 6.2 `ProfileViewModel.kt`

**Scopo:** Dati del profilo e lista badge con status.

```kotlin
data class ProfileUiState(
    val profile: UserProfileEntity? = null,
    val badges: List<BadgeWithStatus> = emptyList(),
    val xpForCurrentLevel: Int = 0,
    val xpForNextLevel: Int = 100
) {
    val level: Int          // profile?.level ?: 1
    val xp: Int             // profile?.xp ?: 0
    val totalLiters: Float  // totalMlAllTime / 1000f
    val bestStreak: Int
    val activeDays: Int
    val dailyRecord: Int    // in ml
}
```

**Init:** Carica `getAllBadgesWithStatus()` dal repository (operazione suspend).

---

### 6.3 `HistoryViewModel.kt`

**Scopo:** Dati storici per lo schermo storico.

```kotlin
data class HistoryUiState(
    val dailySummaries: List<DailySummary> = emptyList(),   // Ultimi 30 giorni (reversed)
    val goalMl: Int = 2000,
    val goalPerDay: Map<String, Int> = emptyMap(),           // Goal snapshot per data
    val weekSummaries: List<DailySummary> = emptyList(),     // 7 giorni, tutti riempiti (0 se mancanti)
    val isLoading: Boolean = true
)
```

**Metodi:**
- `fun loadHistory()` — carica in coroutine: goal corrente, riepilogo 30 giorni, riepilogo 7 giorni, goal storici per range. Riempie i giorni mancanti nella settimana con `DailySummary(date, 0)`.

---

### 6.4 `SettingsViewModel.kt`

**Scopo:** Gestione impostazioni e reset dati.

```kotlin
data class SettingsUiState(
    val dailyGoal: Int = 2000,
    val customGlasses: List<Int> = listOf(200, 500, 1000),
    val notificationsEnabled: Boolean = true,
    val showResetConfirm: Boolean = false,
    val resetDone: Boolean = false
)
```

**Metodi:**
- `fun updateDailyGoal(goal: Int)`
- `fun updateGlasses(glasses: List<Int>)`
- `fun updateGlassAt(index: Int, newAmountMl: Int)`
- `fun toggleNotifications(enabled: Boolean)`
- `fun showResetConfirm()` / `fun dismissResetConfirm()`
- `fun resetAllData()`
- `fun dismissResetDone()`

---

### 6.5 `ShopViewModel.kt`

**Scopo:** Acquisto e gestione decorazioni.

```kotlin
data class ShopUiState(
    val profile: UserProfileEntity? = null,
    val decorations: List<DecorationEntity> = emptyList()
) {
    val currentXp: Int   // profile?.spendableXp ?: 0
}

sealed class PurchaseResult {
    object Success : PurchaseResult()
    object InsufficientXp : PurchaseResult()
}
```

**Metodi:**
- `fun purchaseDecoration(id: String)` — chiama repository, imposta `purchaseResult`
- `fun toggleDecorationPlaced(id: String)`
- `fun dismissPurchaseResult()` — auto-dismiss dopo 2s dalla LaunchedEffect in ShopScreen

---

## 7. Schermate UI

Tutte le schermate si trovano in `ui/screens/`.

---

### 7.1 `HomeScreen.kt`

La schermata principale. Usa `HomeViewModel`.

**Elementi visivi:**
- **`XpBar`** — Livello attuale, XP correnti, barra dorata di progresso verso il livello successivo
- **`ChallengeCard`** — Card con la sfida del giorno, progress bar animata, icona del tipo di challenge
- **`JellyFishView`** — Medusa centrale animata, draggable verticalmente/orizzontalmente, si riempie con il consumo
- **`WaterProgressBar`** — Barra verticale destra: litri bevuti / goal
- **`AquariumBackground`** — Sfondo oceano completo con decorazioni interattive
- **FAB menu espandibile** — Bottone principale che espande i bicchieri configurati + bottone Negozio
- **Burst particelle** — 18 particelle animate (800ms) al tap di un bicchiere
- **Badge popup** — Card scura animata (scaleIn/fadeIn) che mostra il badge appena sbloccato

**Logica UI:**
- I bicchieri nel FAB vengono da `uiState.glasses` (configurabili nelle impostazioni)
- Il badge popup appare quando `uiState.newBadge != null` e si dismette al tap
- Il fill della medusa si anima con `animateFloatAsState(targetValue = uiState.percentage, animationSpec = tween(900))`

---

### 7.2 `ProfileSettingsScreen.kt`

Schermata unificata Profilo + Impostazioni. Usa `ProfileViewModel` + `SettingsViewModel`.

**Sezioni (LazyColumn):**

1. **Profile Header** — Livello (badge numerico dorato), XpBar, spendableXp disponibile
2. **Statistics Grid** — 4 card: Litri Totali, Streak corrente, Giorni Attivi, Record Giornaliero
3. **Obiettivo Giornaliero** — Slider 500-5000ml con step 100ml (18 step), aggiornamento in tempo reale
4. **Bicchieri Predefiniti** — 3 InputChip editabili (tap apre dialog di modifica), formato in ml/L
5. **Promemoria** — Switch per abilitare/disabilitare le notifiche
6. **Cancella tutti i dati** — Bottone rosso con confirmation dialog

---

### 7.3 `BadgesScreen.kt`

Schermata dedicata ai badge. Usa `ProfileViewModel`.

**Layout:**
- **Header** — Contatore sbloccati/totali + progress bar circolare 72dp
- **Sezioni per categoria** — Per ciascuna delle 6 categorie:
  - Header categoria: icona emoji + nome + contatore (es. "2/5") + mini progress bar
  - Lista badge della categoria (componente `BadgeCard`)

**Comportamento badge:**
- Badge sbloccato: mostra data sblocco
- Badge bloccato: mostra `"🔒 Da sbloccare"` con descrizione condizione

---

### 7.4 `HistoryScreen.kt`

Storico consumi. Usa `HistoryViewModel`.

**Layout (LazyColumn):**
1. **WeeklyChart** — Grafico a barre degli ultimi 7 giorni
   - Barre blu (`JellyBlue`) se sotto il goal, verdi (`SuccessGreen`) se goal raggiunto
   - Linea tratteggiata verde del goal (media dei goal del periodo)
   - Label giorno (prime 2 lettere del giorno della settimana)
2. **Lista giornaliera** — `DaySummaryCard` per ognuno degli ultimi 30 giorni
   - Data formattata in italiano
   - Litri bevuti / Litri goal
   - Percentuale + icona checkmark se goal raggiunto
   - Background verde se goal raggiunto, neutro altrimenti

---

### 7.5 `ShopScreen.kt`

Negozio decorazioni. Usa `ShopViewModel`. Accessibile tramite FAB nella HomeScreen (route `"shop"`).

**Layout:**
- **TopBar** — Titolo "Negozio" + XP spendibili attivi (badge dorato)
- **Lista decorazioni** (`DecorationsTab`) — Per ogni decorazione:
  - Preview grafica (componente `DecorationPreview`)
  - Nome + costo XP (se non posseduta) o "Acquistato" con checkmark verde
  - Bottone "Compra" (disabilitato se XP insufficienti) oppure Switch Visibile/Nascosto
- **Feedback acquisto** — Card animata (scaleIn/fadeIn) verde (successo) o rossa (XP insufficienti), auto-dismiss 2s

---

## 8. Componenti UI Riutilizzabili

Tutti i componenti si trovano in `ui/components/`.

---

### 8.1 `JellyFishView.kt` — Il cuore grafico dell'app

**Firma:**
```kotlin
@Composable
fun JellyFishView(
    fillPercentage: Float,          // 0f-1f
    modifier: Modifier = Modifier
)
```

La medusa è interamente disegnata su `Canvas` con **40+ stati di animazione simultanei**.

**Animazioni infinite (smooth phases, senza moltiplicatori di frequenza):**

| Fase | Durata | Effetto |
|---|---|---|
| `driftPhase` | 40s | Drift organico orizzontale del corpo |
| `wavePhase1/2/3` | 30-50s | 3 componenti di drift verticale |
| `breathe` | 3.5s | Respiro corpo ±3% |
| `tentaclePhaseA/B/C` | 7-14s | Oscillazione tentacoli |
| `blinkCycle` | 5s | Ammiccamento degli occhi |
| `bubblePhase` | 4s | Bolle che salgono |

**Layers di rendering (ordine dal basso):**
1. Ombra morbida (doppio layer semi-trasparente)
2. Tentacoli (dietro il corpo)
3. Corpo bell-shaped (gradiente verticale + radiale per il volume 3D)
4. Macchie organiche (6 spot fissi)
5. Riempimento acqua wavy (superficie sinusoidale, 2 riflessi, gradiente verticale)
6. Highlights speculari (principale + secondario)
7. Rim light (controluce bordo destro)
8. Contorno doppio (alpha 0.15 × 4px + 0.45 × 2.2px)
9. Scallop highlight (bordo inferiore smerlato)
10. Guance (2 ovali radiali rosa sfumate)
11. Occhi multi-layer (pupilla + iride + cornea + riflessi)
12. Bocca espressiva (forma dinamica basata su `fillPercentage`)

**Dragging:**
- Hit test ellittico (non rettangolare)
- All'avvio del drag: salva `dragOffset` iniziale
- Al rilascio: compensa il drift animato con `releaseDrift` per evitare salti

**Palette colori (`JellyFishPalettes.kt`):**

L'app usa un'unica palette fissa `PaletteRosa`, hardcodata direttamente nella `JellyFishView`:

```kotlin
internal val PaletteRosa = JellyfishPalette(
    bodyHL1    = Color(0xFFFFF0F6),  // quasi bianco-rosa
    bodyHL2    = Color(0xFFFFD8EA),  // rosa pallido
    bodyMain   = Color(0xFFFFACD0),  // rosa principale
    bodyDeep   = Color(0xFFEE80B0),  // rosa intenso
    bodyShadow = Color(0xFFD06090),  // ombra rosa scuro
    bodyDark   = Color(0xFFB04878),  // ombra profonda
    outerGlow  = Color(0xFFFFE0F0),  // glow esterno
    glowGold   = Color(0xFFFFD060)   // glow dorato
)
```

---

### 8.2 `AquariumBackground.kt`

**Firma:**
```kotlin
@Composable
fun AquariumBackground(
    placedDecorations: List<DecorationEntity>,
    modifier: Modifier = Modifier
)
```

File di circa 1500 linee. Disegna l'intero sfondo acquario su `Canvas`.

**Palette colori (`AquariumColors.kt`):**

```kotlin
val OceanTop  = Color(0xFF1A4B6B)
val OceanMid1 = Color(0xFF2A5A7A)
...
val OceanBot2 = Color(0xFF062D50)
```

**Elementi statici:**
- Gradiente oceano a 7 colori (top → bottom)
- 4 layer di nebbia sottomarina a profondità diverse
- 6 god rays animati con sway (fase 25s)
- 10 dune di sabbia sinusoidale sul fondo
- 40+ granelli di sabbia (circoli con alpha variabile)
- 3 conchiglie di dimensioni diverse con righe
- 1 roccia semplice + 1 roccia realistica 95×70
- 6+ piante marine corte sul fondale
- 10 alghe alte ricorsive con sway organico
- 10 caustics animate sul fondo (ovali)
- 25 particelle fluttuanti (plankton/detriti)
- 22 bolle con gradiente radiale, dimensioni variabili

**Decorazioni dinamiche (da `placedDecorations`):**

| ID | Rendering |
|---|---|
| `fish_blue` / `fish_orange` | Nuoto orizzontale ultra-fluido, 5 fasi (67-127s) |
| `starfish` | Statica sulla roccia |
| `coral_pink` | 3 coralli biforcuti con rami disegnati |
| `treasure` | Apertura/chiusura ciclica ogni 4s |
| `turtle` | Full-screen, nuota con zampe animate (`fishPhase2`) |
| `seahorse` | Drift verticale gentile, 3 fasi (37-67s) |
| `crab` | Camminata orizzontale a scalini (`atan(k)` clamp) |

---

### 8.3 `BadgeCard.kt`

**Firma:**
```kotlin
@Composable
fun BadgeCard(badge: BadgeWithStatus, modifier: Modifier = Modifier)
```

Disegna una medaglia su Canvas 52dp. Elementi:
- Nastri rossi dietro la medaglia
- Corpo circolare con gradiente radiale (colore dipende dalla categoria)
- Anelli concentrici e bordi
- Emoji/icona badge centrata
- Simbolo sfumato con `saveLayerAlpha` se bloccata

---

### 8.4 `ChallengeCard.kt`

Card per la sfida giornaliera. Mostra:
- Tipo di challenge con icona colorata
- Barra di progresso animata (`tween(500ms)`)
- Progress/target testuale (es. "3 / 5 intakes")
- Checkmark celebrativo se completata

---

### 8.5 `WaterProgressBar.kt`

Barra verticale (22dp × 140dp) sul lato destro della HomeScreen.
- Riempimento dal basso verso l'alto
- Litri bevuti sopra, goal sotto
- Riflesso laterale per effetto tridimensionale

---

### 8.6 `XpBar.kt`

Barra XP orizzontale con gradiente dorato (`FFD700 → FFC107 → FF9800`).
- Badge numerico del livello a sinistra
- Progress bar con percentuale XP verso il prossimo livello
- Testo XP overlay

---

### 8.7 `DecorationPreview.kt`

Anteprima miniaturizzata di una decorazione in un Box circolare.
Usata nello ShopScreen per mostrare ogni item in un cerchio colorato.

---

### 8.8 `AnimationUtils.kt`

Contiene utility per le animazioni:
- `InfiniteTransition.smoothPhase(durationMs)` — estensione che restituisce un `Float` 0f→1f in loop con `LinearEasing`, usata in tutta la JellyFishView e AquariumBackground per evitare jank nei cambi di fase.

---

## 9. Navigazione

**File:** `ui/navigation/NavGraph.kt`

4 tab nella bottom navigation bar + 1 schermata modale (Shop):

```kotlin
sealed class Screen(val route: String, val label: String) {
    object Home    : Screen("home",    "Home")
    object Profile : Screen("profile", "Profilo")
    object History : Screen("history", "Storico")
    object Badges  : Screen("badges",  "Badge")
}

object Routes {
    const val SHOP = "shop"
}
```

| Route | Schermata | Icona |
|---|---|---|
| `"home"` | `HomeScreen` | Water Drop |
| `"profile"` | `ProfileSettingsScreen` | Person |
| `"history"` | `HistoryScreen` | History / BarChart |
| `"badges"` | `BadgesScreen` | EmojiEvents / WorkspacePremium |
| `"shop"` | `ShopScreen` | *(no bottom bar)* |

Lo Shop non ha la bottom navigation bar: viene navigato da `navController.navigate(Routes.SHOP)` tramite il FAB in HomeScreen.

---

## 10. Sistema di Notifiche e Widget

---

### 10.1 `WaterNotificationHelper.kt`

**File:** `notification/WaterNotificationHelper.kt`

**Notifica lock screen persistente** che mostra il progresso d'acqua sempre visibile.

| Caratteristica | Valore |
|---|---|
| Channel ID | `"water_progress_channel"` |
| Notification ID | `1001` |
| Importanza | `IMPORTANCE_LOW` (nessun suono/vibrazione) |
| Ongoing | `true` (non dismissable dall'utente) |
| Layout | `RemoteViews` custom (`R.layout.notification_water_progress`) |
| Contenuto | Percentuale + litri attuali / goal |
| Click | `PendingIntent` → apre `MainActivity` |

**Dark mode:** i `RemoteViews` non adattano i colori automaticamente. Gestito con resource qualifier:
- `res/layout/notification_water_progress.xml` — light mode (testo nero/grigio scuro)
- `res/layout-night/notification_water_progress.xml` — dark mode (testo bianco/grigio chiaro)
- `res/drawable/widget_jellyfish_outline.xml` — icona medusa light mode (tratti `#6B6B6B`)
- `res/drawable-night/widget_jellyfish_outline.xml` — icona medusa dark mode (tratti `#E0E0E0`)

**Metodi statici:**
- `fun createNotificationChannel(context)` — chiamato in `JellyDrinkApp.onCreate()`
- `fun showWaterProgressNotification(context, currentMl, goalMl)` — aggiorna o crea la notifica

---

### 10.2 `JellyfishWidget.kt`

**File:** `widget/JellyfishWidget.kt`
**Classe:** `class JellyfishWidget : AppWidgetProvider()`

Widget home screen che mostra la percentuale dell'obiettivo giornaliero.

**Update flow:**
1. `updateAllWidgets(context)` — metodo statico companion
   - Invia broadcast `ACTION_APPWIDGET_UPDATE` con tutti gli ID attivi
   - Chiama manualmente `updateAppWidget()` per ogni widget (ridondanza sicura)
2. `updateAppWidget()` — funzione privata
   - Delay 100ms per assicurare coerenza del DB
   - Apre una propria istanza Room con tutte le migrazioni
   - Legge `waterIntakeDao.getTotalForDate(oggi)`
   - Calcola percentuale, aggiorna `RemoteViews`
3. `updateWidgetView()` — aggiorna effettivamente le `RemoteViews`
   - `widget_percentage`: testo percentuale
   - `widget_container`: click → apre MainActivity

**Dark mode:** gestito con resource qualifier:
- `res/drawable/widget_background.xml` — sfondo azzurro chiaro (light mode)
- `res/drawable-night/widget_background.xml` — sfondo blu navy scuro (dark mode)
- `res/drawable-night/widget_jellyfish_outline.xml` — tratti bianchi/chiari per contrasto sul fondo scuro

> **Nota:** Il widget crea la propria connessione Room separata dall'app principale. Deve sempre includere **tutte** le migrazioni aggiornate.

---

## 11. Background Workers e Receivers

---

### 11.1 `MidnightResetReceiver.kt`

**File:** `receiver/MidnightResetReceiver.kt`
**Tipo:** `BroadcastReceiver`, schedulato via `AlarmManager`

**Metodo statico:**
```kotlin
fun scheduleMidnightAlarm(context: Context)
```

Schedula con:
```kotlin
AlarmManager.setExactAndAllowWhileIdle(
    AlarmManager.RTC_WAKEUP,
    midnight.timeInMillis,   // 00:00:05 del giorno successivo
    pendingIntent
)
```

- `RTC_WAKEUP` — sveglia il dispositivo
- `setExactAndAllowWhileIdle` — esecuzione **esatta** anche in Doze mode
- Target: **00:00:05** (5 secondi dopo mezzanotte, buffer per sicurezza date)
- Auto-reschedula per la notte successiva ad ogni `onReceive()`

**Azioni in `onReceive()`:**
1. Legge goal attuale dal DataStore
2. Chiama `WaterNotificationHelper.showWaterProgressNotification(0, goal)` → reset a 0
3. Chiama `JellyfishWidget.updateAllWidgets()` → aggiornamento widget
4. Ri-schedula per la prossima mezzanotte

---

### 11.2 `BootReceiver.kt`

**File:** `receiver/BootReceiver.kt`
**Trigger:** `Intent.ACTION_BOOT_COMPLETED`

Al riavvio del dispositivo:
1. Chiama `MidnightResetReceiver.scheduleMidnightAlarm()` — ripristina l'alarm perso al boot
2. Apre Room con tutte le migrazioni
3. Legge `waterIntakeDao.getTotalForDate(oggi)` e il goal da DataStore
4. Chiama `WaterNotificationHelper.showWaterProgressNotification(currentMl, goalMl)`

---

### 11.3 `WaterReminderWorker.kt` e `StreakDangerWorker.kt`

Schedulati in `MainActivity` via WorkManager ma attualmente **disabilitati** (restituiscono `Result.success()` senza eseguire operazioni).

- `WaterReminderWorker` — schedulato ogni 2 ore
- `StreakDangerWorker` — schedulato giornalmente alle 21:00

---

## 12. Dependency Injection

**File:** `di/AppModule.kt`
**Classe app:** `JellyDrinkApp.kt` (`@HiltAndroidApp`)

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides @Singleton
    fun provideDatabase(context: Context): AppDatabase
        // Room.databaseBuilder con tutte le migrazioni
        // addMigrations(MIGRATION_1_2, ..., MIGRATION_5_6)

    @Provides fun provideWaterIntakeDao(db: AppDatabase): WaterIntakeDao
    @Provides fun provideBadgeDao(db: AppDatabase): BadgeDao
    @Provides fun provideUserProfileDao(db: AppDatabase): UserProfileDao
    @Provides fun provideDailyChallengeDao(db: AppDatabase): DailyChallengeDao
    @Provides fun provideDecorationDao(db: AppDatabase): DecorationDao
    @Provides fun provideJellyfishDao(db: AppDatabase): JellyfishDao
    @Provides fun provideDailyGoalDao(db: AppDatabase): DailyGoalDao

    @Provides @Singleton
    fun provideWaterRepository(
        waterIntakeDao, badgeDao, userProfileDao,
        dailyChallengeDao, decorationDao, jellyfishDao,
        dailyGoalDao, context
    ): WaterRepository
}
```

`JellyDrinkApp.onCreate()`:
- Crea i canali di notifica (`NotificationHelper`, `WaterNotificationHelper`)
- Configura `WorkManager` con `HiltWorkerFactory`

`MainActivity.onCreate()`:
- Richiede permesso `POST_NOTIFICATIONS` (Android 13+)
- Chiama `scheduleNotificationWorkers()` → schedula WorkManager + MidnightAlarm
- Chiama `initializeWaterProgressNotification()` → mostra notifica lock screen aggiornata

---

## 13. Tema e Stile

**File:** `ui/theme/Color.kt`, `Theme.kt`, `Type.kt`

**Palette principale:**

```kotlin
val JellyBlue    = Color(0xFF4FC3F7)
val JellyBlueDark = Color(0xFF0288D1)
val JellyCyan    = Color(0xFF00BCD4)
val JellyPurple  = Color(0xFF7C4DFF)
val DeepOcean    = Color(0xFF0D1B2A)
val GoldBadge    = Color(0xFFFFD54F)
val SuccessGreen = Color(0xFF66BB6A)
val TextOnDark   = Color(0xFFE3F2FD)
```

**Temi:**
- **Dark scheme** — Primary: `JellyBlue`, Background: `DeepOcean`
- **Light scheme** — Primary: `JellyBlueDark`, Background: chiaro
- Supporto Dynamic Colors (Android 12+)

---

## 14. Formule e Costanti di Business

### Sistema XP

```
XP per 100ml bevuti:        1 XP
Bonus goal giornaliero:     50 XP (massimo una volta al giorno)

Streak Multiplier:
  multiplier = 1.0 + min(streak, 5) × 0.1
  → da 1.0× (nessuno streak) a 1.5× (≥5 giorni consecutivi)

XP finali per intake:
  xp = floor((amountMl / 100) × multiplier)
  + 50 se in questo intake si supera il goal per la prima volta oggi
```

### Sistema Livelli

```
Livello corrente dato XP:
  level = floor(√(xp / 100)) + 1

XP necessari per arrivare al livello N:
  xpRequired(N) = (N − 1)² × 100

Tabella esempi:
  Level  1 →      0 XP
  Level  2 →    100 XP
  Level  3 →    400 XP
  Level  4 →    900 XP
  Level  5 →  1.600 XP
  Level 10 →  8.100 XP
  Level 20 → 36.100 XP
  Level 50 → 240.100 XP
```

### Goal e Impostazioni

```
Goal giornaliero default:   2.000 ml
Range slider goal:          500 ml – 5.000 ml (step 100ml, 45 step)
Bicchieri default:          [200ml, 500ml, 1.000ml]
Scala grafico storico:      massimo tra goal e record del periodo
```

---

## 15. Struttura File del Progetto

```
app/src/main/java/com/jellydrink/app/
│
├── JellyDrinkApp.kt                        @HiltAndroidApp, NotificationChannels
├── MainActivity.kt                         Permessi, WorkManager scheduling, alarm midnight
│
├── data/
│   ├── db/
│   │   ├── AppDatabase.kt                  Room DB v6, 7 entità, 5 migrazioni
│   │   ├── dao/
│   │   │   ├── WaterIntakeDao.kt
│   │   │   ├── UserProfileDao.kt
│   │   │   ├── BadgeDao.kt
│   │   │   ├── DailyChallengeDao.kt
│   │   │   ├── DecorationDao.kt
│   │   │   ├── JellyfishDao.kt
│   │   │   ├── DailyGoalDao.kt
│   │   │   └── BeerIntakeDao.kt            (branch birra)
│   │   └── entity/
│   │       ├── WaterIntakeEntity.kt
│   │       ├── UserProfileEntity.kt
│   │       ├── BadgeEntity.kt
│   │       ├── DailyChallengeEntity.kt
│   │       ├── DecorationEntity.kt
│   │       ├── JellyfishEntity.kt
│   │       ├── DailyGoalEntity.kt
│   │       └── BeerIntakeEntity.kt         (branch birra)
│   └── repository/
│       └── WaterRepository.kt              Business logic centrale
│
├── di/
│   └── AppModule.kt                        Hilt Module (DB, DAOs, Repository)
│
├── notification/
│   ├── NotificationHelper.kt               Canali generici
│   └── WaterNotificationHelper.kt          Notifica lock screen progress
│
├── receiver/
│   ├── BootReceiver.kt                     BOOT_COMPLETED → alarm + notifica
│   └── MidnightResetReceiver.kt            AlarmManager → reset + widget
│
├── worker/
│   ├── WaterReminderWorker.kt              (disabilitato)
│   ├── StreakDangerWorker.kt               (disabilitato)
│   └── MidnightResetWorker.kt
│
├── viewmodel/
│   ├── HomeViewModel.kt
│   ├── ProfileViewModel.kt
│   ├── HistoryViewModel.kt
│   ├── SettingsViewModel.kt
│   ├── ShopViewModel.kt
│   └── BeerViewModel.kt                    (branch birra)
│
├── ui/
│   ├── components/
│   │   ├── JellyFishView.kt               Medusa animata (componente principale)
│   │   ├── JellyFishPalettes.kt           Palette PaletteRosa e costanti colore medusa
│   │   ├── JellyFishParts.kt              Sub-funzioni disegno medusa
│   │   ├── AquariumBackground.kt          Sfondo oceano completo
│   │   ├── AquariumColors.kt              Palette oceano
│   │   ├── AquariumDecorations.kt         Rendering decorazioni
│   │   ├── AquariumFish.kt                Rendering pesci animati
│   │   ├── BadgeCard.kt                   Medaglia disegnata
│   │   ├── ChallengeCard.kt               Card sfida giornaliera
│   │   ├── WaterProgressBar.kt            Barra verticale progresso
│   │   ├── XpBar.kt                       Barra XP dorata
│   │   ├── DecorationPreview.kt           Anteprima decorazione (negozio)
│   │   ├── AnimationUtils.kt              smoothPhase extension
│   │   └── PufferfishView.kt              (branch birra)
│   ├── screens/
│   │   ├── HomeScreen.kt
│   │   ├── ProfileSettingsScreen.kt       Profilo + Impostazioni unificati
│   │   ├── BadgesScreen.kt                26 badge in 6 categorie
│   │   ├── HistoryScreen.kt
│   │   ├── ShopScreen.kt
│   │   └── BeerScreen.kt                  (branch birra)
│   ├── navigation/
│   │   └── NavGraph.kt                    4 tab + route Shop
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
│
└── widget/
    └── JellyfishWidget.kt                 AppWidgetProvider
```

---

*Documentazione generata il 21/02/2026 — JellyDrink Android App*
