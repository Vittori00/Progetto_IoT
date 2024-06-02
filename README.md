# Progetto IoT

## Descrizione
Questo progetto è un sistema IoT che include vari componenti per la gestione e il monitoraggio dell'irrigazione di piante. Include sensori, attuatori e applicazioni per la gestione remota.

## Componenti del Progetto
- **Illumination**: Gestione dell'illuminazione.
- **Plant Sensor**: Monitoraggio delle condizioni delle piante.
- **Registration Server**: Server per la registrazione dei dispositivi.
- **Remote Control Application**: Applicazione per il controllo remoto.
- **Soil Sensor**: Monitoraggio dell'umidità del suolo.
- **Sprinkler**: Controllo degli irrigatori.

## Struttura del Repository
- `illumination/`: Codice e configurazioni per la gestione dell'illuminazione.
- `plant_sensor/`: Codice per i sensori delle piante.
- `registration-server/`: Codice del server per la registrazione dei dispositivi.
- `remote-control-application/`: Codice dell'applicazione di controllo remoto.
- `soil_sensor/`: Codice per i sensori del suolo.
- `sprinkler/`: Codice per il controllo degli irrigatori.

## Prerequisiti
- Compilatore C
- Ambiente Java
- Make

## Installazione
1. Clonare il repository:
    ```bash
    git clone https://github.com/Vittori00/Progetto_IoT.git
    ```
2. Navigare nella directory del progetto:
    ```bash
    cd Progetto_IoT
    ```
3. Compilare i vari componenti usando Makefile:
    ```bash
    make
    ```

## Utilizzo
- **Registrazione Dispositivi**:
  ```bash
  ./registration-server/start.sh
