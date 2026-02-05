package util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class JSONHandler {

    public static JSONObject leggiConfigurazione(String filePath) {
        try (FileReader reader = new FileReader(filePath)) {
            JSONTokener tokener = new JSONTokener(reader);
            return new JSONObject(tokener);
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file JSON: " + e.getMessage());
            return creaConfigurazioneDefault();
        }
    }

    public static void scriviConfigurazione(String filePath, JSONObject config) {
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(config.toString(2)); // Indentazione di 2 spazi
            System.out.println("Configurazione salvata in: " + filePath);
        } catch (IOException e) {
            System.err.println("Errore nel salvataggio del file JSON: " + e.getMessage());
        }
    }

    public static JSONObject creaConfigurazioneInterattiva() {
        Scanner scanner = new Scanner(System.in);
        JSONObject config = new JSONObject();

        // Configurazione simulazione
        JSONObject simulazione = new JSONObject();
        System.out.println("CONFIGURAZIONE SIMULAZIONE");
        System.out.print("Durata in ore: ");
        simulazione.put("durata_ore", scanner.nextInt());
        System.out.print("Costo per kWh (€): ");
        simulazione.put("costo_kwh", scanner.nextDouble());
        config.put("simulazione", simulazione);

        // Configurazione elettrodomestici
        JSONArray elettrodomestici = new JSONArray();
        boolean continua = true;
        int contatore = 1;

        System.out.println("\nCONFIGURAZIONE ELETTRODOMESTICI");
        System.out.println("Tipi disponibili: Lavastoviglie, Lavatrice, Asciugatrice, Frigo");

        while (continua) {
            System.out.println("\n--- Elettrodomestico #" + contatore + " ---");
            System.out.print("Tipo elettrodomestico (o 'fine' per terminare): ");
            String tipo = scanner.next();

            if (tipo.equalsIgnoreCase("fine")) {
                continua = false;
                break;
            }

            JSONObject elettrodomestico = new JSONObject();
            elettrodomestico.put("tipo", tipo);

            switch (tipo.toLowerCase()) {
                case "lavastoviglie":
                    System.out.println("Programmi disponibili:");
                    System.out.println("1 - Eco (45°C, 1.0 kW)");
                    System.out.println("2 - Intensivo (65°C, 1.5 kW)");
                    System.out.println("3 - Rapido (40°C, 0.8 kW)");
                    System.out.print("Seleziona programma (1-3): ");
                    elettrodomestico.put("programma", scanner.nextInt());
                    break;

                case "lavatrice":
                    System.out.print("Velocità centrifuga (400-1600 giri/min): ");
                    elettrodomestico.put("velocita_centrifuga", scanner.nextInt());
                    break;

                case "asciugatrice":
                    System.out.print("Temperatura asciugatura (40-80°C): ");
                    elettrodomestico.put("temperatura_asciugatura", scanner.nextInt());
                    break;

                case "frigo":
                    System.out.print("Temperatura frigo (2-8°C): ");
                    elettrodomestico.put("temperatura", scanner.nextDouble());
                    break;

                default:
                    System.out.println("Tipo non riconosciuto. Saltato.");
                    continue;
            }

            elettrodomestici.put(elettrodomestico);
            contatore++;
        }

        config.put("elettrodomestici", elettrodomestici);

        if (elettrodomestici.length() == 0) {
            System.out.println("Nessun elettrodomestico configurato. Uso configurazione default.");
            return creaConfigurazioneDefault();
        }

        return config;
    }

    private static JSONObject creaConfigurazioneDefault() {
        JSONObject config = new JSONObject();

        JSONObject simulazione = new JSONObject();
        simulazione.put("durata_ore", 24);
        simulazione.put("costo_kwh", 0.20);
        config.put("simulazione", simulazione);

        JSONArray elettrodomestici = new JSONArray();

        // Configurazione default
        JSONObject lavastoviglie = new JSONObject();
        lavastoviglie.put("tipo", "Lavastoviglie");
        lavastoviglie.put("programma", 1);
        elettrodomestici.put(lavastoviglie);

        JSONObject lavatrice = new JSONObject();
        lavatrice.put("tipo", "Lavatrice");
        lavatrice.put("velocita_centrifuga", 1200);
        elettrodomestici.put(lavatrice);

        JSONObject asciugatrice = new JSONObject();
        asciugatrice.put("tipo", "Asciugatrice");
        asciugatrice.put("temperatura_asciugatura", 60);
        elettrodomestici.put(asciugatrice);

        JSONObject frigo = new JSONObject();
        frigo.put("tipo", "Frigo");
        frigo.put("temperatura", 4.0);
        elettrodomestici.put(frigo);

        config.put("elettrodomestici", elettrodomestici);

        return config;
    }
}