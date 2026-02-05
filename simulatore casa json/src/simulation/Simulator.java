package simulation;

import casa.Casa;
import org.json.JSONArray;
import org.json.JSONObject;
import time.Clock;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

public class Simulator {
    public void esegui(){
        try {
            String configContent = new String(Files.readAllBytes(Paths.get("config.json")));
            JSONObject config = new JSONObject(configContent);

            var datisimulazione = config.getJSONObject("simulazione");
            int oreSimulazione = datisimulazione.getInt("durata_ore");
            int durataMinuti = oreSimulazione * 60;

            System.out.println("SIMULAZIONE CONSUMI DOMESTICI");
            System.out.println("Durata simulazione: " + oreSimulazione + " ore (" + durataMinuti + " minuti)");

            Clock clock = new Clock(config);
            Casa casa = clock.getCasa();

            JSONArray elettrodomesticiArray = config.getJSONArray("elettrodomestici");

            for (int i = 0; i < elettrodomesticiArray.length(); i++) {
                JSONObject elettrodomestico = elettrodomesticiArray.getJSONObject(i);
                String tipo = elettrodomestico.getString("tipo");

                if (i < casa.getElettrodomestici().size()) {
                    switch (tipo) {
                        case "Lavastoviglie":
                            if (elettrodomestico.has("programma")) {
                                int programma = elettrodomestico.getInt("programma");
                                ((home_appliances.Lavastoviglie) casa.getElettrodomestici().get(i)).setProgramma(programma);
                                System.out.println("\n" + (i+1) + ". Configurazione Lavastoviglie:");
                                System.out.println("   Programma impostato: " + programma);
                            }
                            break;
                        case "Lavatrice":
                            if (elettrodomestico.has("velocita_centrifuga")) {
                                int velocitaCentrifuga = elettrodomestico.getInt("velocita_centrifuga");
                                ((home_appliances.Lavatrice) casa.getElettrodomestici().get(i)).setVelocitaCentrifuga(velocitaCentrifuga);
                                System.out.println("\n" + (i+1) + ". Configurazione Lavatrice:");
                                System.out.println("   Velocità centrifuga impostata: " + velocitaCentrifuga + " giri/min");
                            }
                            break;
                        case "Asciugatrice":
                            if (elettrodomestico.has("temperatura_asciugatura")) {
                                int tempAsciugatrice = elettrodomestico.getInt("temperatura_asciugatura");
                                ((home_appliances.Asciugatrice) casa.getElettrodomestici().get(i)).setTemperaturaAsciugatura(tempAsciugatrice);
                                System.out.println("\n" + (i+1) + ". Configurazione Asciugatrice:");
                                System.out.println("   Temperatura asciugatura impostata: " + tempAsciugatrice + "°C");
                            }
                            break;
                        case "Frigo":
                            if (elettrodomestico.has("temperatura")) {
                                double tempFrigo = elettrodomestico.getDouble("temperatura");
                                ((home_appliances.Frigo) casa.getElettrodomestici().get(i)).setTemperatura(tempFrigo);
                                System.out.println("\n" + (i+1) + ". Configurazione Frigo:");
                                System.out.println("   Temperatura impostata: " + tempFrigo + "°C");
                            }
                            break;
                    }
                }
            }

            int numElettrodomestici = casa.getElettrodomestici().size();
            int[] tempoRimanenteAccensione = new int[numElettrodomestici];
            boolean[] accensioneProgrammata = new boolean[numElettrodomestici];

            System.out.println("\nINIZIO SIMULAZIONE");
            System.out.println("Inizio simulazione: " + clock);
            casa.stampaStatoElettrodomestici();

            for (int i = 0; i < numElettrodomestici; i++) {
                if (casa.getElettrodomestici().get(i).getNome().equals("Frigo")) {
                    tempoRimanenteAccensione[i] = Integer.MAX_VALUE;
                    casa.getElettrodomestici().get(i).accendi();
                    break;
                }
            }

            int oraInizioLavaggio = -1;
            boolean lavaggioAvviato = false;
            Random random = new Random();

            while (!clock.isSimulationFinished()) {
                clock.tick();

                if (clock.getMinute() == 0) {
                    for (int i = 0; i < numElettrodomestici; i++) {
                        String nome = casa.getElettrodomestici().get(i).getNome();
                        if (!nome.equals("Frigo") && !casa.getElettrodomestici().get(i).isAcceso() && tempoRimanenteAccensione[i] <= 0) {
                            if (random.nextDouble() < 0.6) {
                                casa.getElettrodomestici().get(i).accendi();
                                tempoRimanenteAccensione[i] = 180;
                                accensioneProgrammata[i] = false;

                                switch (nome) {
                                    case "Lavastoviglie":
                                        int prog = ((home_appliances.Lavastoviglie) casa.getElettrodomestici().get(i)).getProgramma();
                                        String[] nomiProgrammi = {"Eco", "Intensivo", "Rapido"};
                                        System.out.println("Ora " + clock.getHour() + ":00 - Lavastoviglie acceso per 3 ore (Programma: " +
                                                nomiProgrammi[prog - 1] + ", " +
                                                ((home_appliances.Lavastoviglie) casa.getElettrodomestici().get(i)).getTemperatura() + "°C)");
                                        break;
                                    case "Lavatrice":
                                        System.out.println("Ora " + clock.getHour() + ":00 - Lavatrice acceso per 3 ore (" +
                                                ((home_appliances.Lavatrice) casa.getElettrodomestici().get(i)).getVelocitaCentrifuga() + " giri/min)");
                                        break;
                                    case "Asciugatrice":
                                        System.out.println("Ora " + clock.getHour() + ":00 - Asciugatrice acceso per 3 ore (" +
                                                ((home_appliances.Asciugatrice) casa.getElettrodomestici().get(i)).getTemperaturaAsciugatura() + "°C)");
                                        break;
                                }

                                if (nome.equals("Lavatrice") && !lavaggioAvviato) {
                                    oraInizioLavaggio = clock.getHour();
                                    lavaggioAvviato = true;
                                    System.out.println("   >> Ciclo di lavaggio iniziato. Asciugatura tra 2 ore.");
                                }
                            }
                        }
                    }

                    if (lavaggioAvviato && clock.getHour() == (oraInizioLavaggio + 2) % 24) {
                        for (int i = 0; i < numElettrodomestici; i++) {
                            if (casa.getElettrodomestici().get(i).getNome().equals("Asciugatrice") &&
                                    !casa.getElettrodomestici().get(i).isAcceso() && tempoRimanenteAccensione[i] <= 0) {
                                casa.getElettrodomestici().get(i).accendi();
                                tempoRimanenteAccensione[i] = 120;
                                System.out.println("Ora " + clock.getHour() + ":00 - Asciugatrice acceso automaticamente dopo lavaggio (" +
                                        ((home_appliances.Asciugatrice) casa.getElettrodomestici().get(i)).getDurataCiclo() + " min di ciclo)");
                                break;
                            }
                        }
                    }
                }

                for (int i = 0; i < numElettrodomestici; i++) {
                    if (tempoRimanenteAccensione[i] > 0) {
                        tempoRimanenteAccensione[i]--;
                        if (tempoRimanenteAccensione[i] == 0 && casa.getElettrodomestici().get(i).isAcceso()) {
                            casa.getElettrodomestici().get(i).spegni();
                            String nomeElettrodomestico = casa.getElettrodomestici().get(i).getNome();

                            if (nomeElettrodomestico.equals("Asciugatrice")) {
                                lavaggioAvviato = false;
                                System.out.println(nomeElettrodomestico + " spento dopo ciclo completo di asciugatura");
                            } else if (!nomeElettrodomestico.equals("Frigo")) {
                                System.out.println(nomeElettrodomestico + " spento dopo 3 ore di utilizzo");
                            }
                        }
                    }
                }

                if (clock.getElapsedTime() % 60 == 0 && clock.getElapsedTime() > 0) {
                    int oraSimulazione = clock.getElapsedTime() / 60;
                    System.out.println("\nOra di simulazione: " + oraSimulazione + " - Tempo: " + clock);

                    if (oraSimulazione % 6 == 0) {
                        System.out.println("DETTAGLI CONFIGURAZIONE");
                        for (int i = 0; i < numElettrodomestici; i++) {
                            String nome = casa.getElettrodomestici().get(i).getNome();
                            switch (nome) {
                                case "Frigo":
                                    System.out.println("Frigo: " + ((home_appliances.Frigo) casa.getElettrodomestici().get(i)).getTemperatura() + "°C" +
                                            (((home_appliances.Frigo) casa.getElettrodomestici().get(i)).hasCongelatore() ? " (con congelatore)" : ""));
                                    break;
                                case "Lavatrice":
                                    System.out.println("Lavatrice: " + ((home_appliances.Lavatrice) casa.getElettrodomestici().get(i)).getCapacita() + " kg, " +
                                            ((home_appliances.Lavatrice) casa.getElettrodomestici().get(i)).getVelocitaCentrifuga() + " giri/min");
                                    break;
                            }
                        }
                    }

                    casa.stampaStatoElettrodomestici();
                }
            }

            System.out.println("\nFINE SIMULAZIONE");
            System.out.println("Tempo finale: " + clock);
            casa.stampaStatoElettrodomestici();

            double consumoKWh = casa.getConsumoTotale() / 60.0;
            double costo = consumoKWh * 0.20;

            System.out.println("RIEPILOGO FINALE");
            System.out.println("Durata totale simulazione: " + oreSimulazione + " ore");
            System.out.println("Consumo totale: " + String.format("%.2f", consumoKWh) + " kWh");
            System.out.println("Costo stimato (" + String.format("%.2f", 0.20) + " €/kWh): " + String.format("%.2f", costo) + " €");

            for (int i = 0; i < casa.getElettrodomestici().size(); i++) {
                casa.getElettrodomestici().get(i).resetConsumo();
            }

        } catch (IOException e) {
            System.err.println("Errore nella lettura del file config.json: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Errore durante la simulazione: " + e.getMessage());
            e.printStackTrace();
        }
    }
}