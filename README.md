# Simulazione Consumi Domestici

## Descrizione
Simulatore di consumi energetici domestici sviluppato in Java. L'applicazione permette di simulare il consumo energetico di vari elettrodomestici in una casa virtuale, configurando parametri come durata della simulazione, costo dell'energia e caratteristiche specifiche degli elettrodomestici.

## Struttura del Progetto

### Classi Principali

#### Package: `simulation`
- **Simulator.java**: Classe principale che gestisce l'esecuzione della simulazione
  - Legge la configurazione da file JSON
  - Gestisce il ciclo di simulazione
  - Coordina l'interazione tra Clock e Casa
  - Stampa report periodici e finali

#### Package: `time`
- **Clock.java**: Gestisce il tempo della simulazione
  - Mantiene traccia di ore, minuti e giorni
  - Controlla la durata della simulazione
  - Coordina l'avanzamento temporale

#### Package: `casa`
- **Casa.java**: Rappresenta la casa con i suoi elettrodomestici
  - Gestisce una lista di elettrodomestici
  - Calcola consumi totali
  - Fornisce metodi per l'aggiornamento dei consumi

#### Package: `home_appliances`
- **Elettrodomestico.java**: Classe astratta base per tutti gli elettrodomestici
- **Lavastoviglie.java**: Simula una lavastoviglie con diversi programmi
- **Lavatrice.java**: Simula una lavatrice con velocità centrifuga regolabile
- **Asciugatrice.java**: Simula un'asciugatrice con temperatura regolabile
- **Frigo.java**: Simula un frigorifero con temperatura regolabile

#### Package: `util`
- **JSONHandler.java**: Gestisce la lettura e scrittura di file JSON
  - Creazione configurazione interattiva
  - Lettura/scrittura file di configurazione
  - Configurazione di default

## Configurazione

### File di Configurazione (`config.json`)
Il sistema utilizza un file JSON per la configurazione:

```json
{
  "simulazione": {
    "durata_ore": 24,
    "costo_kwh": 0.20
  },
  "elettrodomestici": [
    {
      "tipo": "Lavastoviglie",
      "programma": 2
    },
    {
      "tipo": "Lavatrice",
      "velocita_centrifuga": 1400
    }
  ]
}
```

### Tipi di Elettrodomestici Supportati
1. **Lavastoviglie**: Programmi disponibili (1: Eco, 2: Intensivo, 3: Rapido)
2. **Lavatrice**: Velocità centrifuga (400-1600 giri/min)
3. **Asciugatrice**: Temperatura asciugatura (40-80°C)
4. **Frigo**: Temperatura (2-8°C)

## Compilazione ed Esecuzione

### Prerequisiti
- Java JDK 11 o superiore
- Libreria JSON (org.json)

### Compilazione
```bash
javac -cp ".:json-20240303.jar" Main.java
```

### Esecuzione
```bash
java -cp ".:json-20240303.jar" Main
```

### Creazione Configurazione Interattiva
Per creare una nuova configurazione:
```bash
java -cp ".:json-20240303.jar" util.JSONHandler
```

## Logica di Simulazione

### Comportamento degli Elettrodomestici
- **Frigo**: Sempre acceso per tutta la simulazione
- **Lavastoviglie/Lavatrice/Asciugatrice**: Si accendono casualmente con probabilità del 60% all'inizio di ogni ora
- **Ciclo Lavaggio-Asciugatura**: Quando una lavatrice si accende, l'asciugatrice si accende automaticamente dopo 2 ore
- **Durata Utilizzo**: Elettrodomestici (escluso frigorifero) rimangono accesi per 3 ore

### Calcolo dei Consumi
- Consumo orario variabile in base alle impostazioni
- Temperatura più alta = consumo più alto
- Velocità centrifuga più alta = consumo più alto
- Temperatura frigorifero più bassa = consumo più alto

## Output della Simulazione

La simulazione produce i seguenti output:
1. **Stato iniziale**: Configurazione degli elettrodomestici
2. **Eventi durante la simulazione**: Accensioni, spegnimenti, cambiamenti di stato
3. **Report periodici**: Ogni 6 ore, dettagli configurazione e stato
4. **Report finale**: Consumo totale e costo stimato

### Esempio di Output
```
SIMULAZIONE CONSUMI DOMESTICI
Durata simulazione: 24 ore (1440 minuti)

INIZIO SIMULAZIONE
Inizio simulazione: Giorno 0 - 00:00

Ora 8:00 - Lavastoviglie acceso per 3 ore (Programma: Intensivo, 65.0°C)

FINE SIMULAZIONE
Tempo finale: Giorno 1 - 00:00
RIEPILOGO FINALE
Durata totale simulazione: 24 ore
Consumo totale: 15.23 kWh
Costo stimato (0.20 €/kWh): 3.05 €
```

## Personalizzazione

### Aggiunta Nuovi Elettrodomestici
Per aggiungere un nuovo tipo di elettrodomestico:
1. Creare una nuova classe che estende `Elettrodomestico`
2. Implementare i metodi specifici
3. Modificare `Casa.java` per supportare il nuovo tipo
4. Aggiornare `JSONHandler.java` per la configurazione interattiva

### Modifica Comportamento Simulazione
- Probabilità di accensione: Modificare `random.nextDouble() < 0.6` in `Simulator.java`
- Durata accensione: Modificare `tempoRimanenteAccensione[i] = 180` in `Simulator.java`
- Ritardo lavaggio-asciugatura: Modificare `clock.getHour() == (oraInizioLavaggio + 2)`

## Note Tecniche

### Unità di Misura
- Consumo: kW (chilowatt)
- Tempo: minuti (simulazione)
- Costo: €/kWh

### Gestione Errori
- File di configurazione mancante: usa configurazione di default
- Tipo elettrodomestico non valido: ignorato
- Errore di lettura JSON: configurazione di default

## Estensioni Future Possibili
1. Aggiunta di pannelli solari e batterie
2. Supporto per fasce orarie (biorarie/multiorarie)
3. Interfaccia grafica
4. Esportazione risultati in CSV/PDF
5. Simulazione di più case simultaneamente
