package casa;

import home_appliances.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class Casa {
    private final List<Elettrodomestico> elettrodomestici;

    public Casa() {
        elettrodomestici = new ArrayList<>();
    }

    public Casa(JSONArray configElettrodomestici) {
        elettrodomestici = new ArrayList<>();
        caricaElettrodomesticiDaConfig(configElettrodomestici);
    }

    private void caricaElettrodomesticiDaConfig(JSONArray configElettrodomestici) {
        for (int i = 0; i < configElettrodomestici.length(); i++) {
            JSONObject config = configElettrodomestici.getJSONObject(i);
            String tipo = config.getString("tipo");

            Elettrodomestico elettrodomestico = null;

            switch (tipo.toLowerCase()) {
                case "lavastoviglie":
                    Lavastoviglie lavastoviglie = new Lavastoviglie();
                    if (config.has("programma")) {
                        lavastoviglie.setProgramma(config.getInt("programma"));
                    }
                    elettrodomestico = lavastoviglie;
                    break;

                case "lavatrice":
                    Lavatrice lavatrice = new Lavatrice();
                    if (config.has("velocita_centrifuga")) {
                        lavatrice.setVelocitaCentrifuga(config.getInt("velocita_centrifuga"));
                    }
                    elettrodomestico = lavatrice;
                    break;

                case "asciugatrice":
                    Asciugatrice asciugatrice = new Asciugatrice();
                    if (config.has("temperatura_asciugatura")) {
                        asciugatrice.setTemperaturaAsciugatura(config.getInt("temperatura_asciugatura"));
                    }
                    elettrodomestico = asciugatrice;
                    break;

                case "frigo":
                    Frigo frigo = new Frigo();
                    if (config.has("temperatura")) {
                        frigo.setTemperatura(config.getDouble("temperatura"));
                    }
                    elettrodomestico = frigo;
                    break;

                case "pannellifotovoltaici":
                    PannelliFotovoltaici pannelliFotovoltaici = new PannelliFotovoltaici();
                    if (config.getInt("n_pannelli") != 0) {
                        pannelliFotovoltaici.setNPannelli(config.getInt("n_pannelli"));
                        pannelliFotovoltaici.setPotenzaSole(config.getInt("potenza_sole"));
                        pannelliFotovoltaici.setArea(config.getDouble("area"));
                        elettrodomestico = pannelliFotovoltaici;
                    }
                    break;

                default:
                    System.err.println("Tipo elettrodomestico non riconosciuto: " + tipo);
                    continue;
            }

            if (elettrodomestico != null) {
                elettrodomestici.add(elettrodomestico);
            }
        }

        // Se non ci sono elettrodomestici, usa quelli di default
        if (elettrodomestici.isEmpty()) {
            elettrodomestici.add(new Lavastoviglie());
            elettrodomestici.add(new Lavatrice());
            elettrodomestici.add(new Asciugatrice());
            elettrodomestici.add(new Frigo());
        }
    }

    public void aggiungiElettrodomestico(Elettrodomestico elettrodomestico) {
        elettrodomestici.add(elettrodomestico);
    }

    public void aggiornaConsumi() {
        for (Elettrodomestico e : elettrodomestici) {
            e.aggiornaConsumo();
        }
    }

    public double getConsumoTotale() {
        double totale = 0;
        for (Elettrodomestico e : elettrodomestici) {
            totale += e.getConsumoAccumulato();
        }
        return totale;
    }

    public void stampaStatoElettrodomestici() {
        System.out.println("\nSTATO ELETTRODOMESTICI");
        for (int i = 0; i < elettrodomestici.size(); i++) {
            System.out.println((i + 1) + ". " + elettrodomestici.get(i));
        }
        System.out.println("Consumo totale casa: " + String.format("%.2f", getConsumoTotale()) + " kW");
        System.out.println();
    }

    public List<Elettrodomestico> getElettrodomestici() {
        return elettrodomestici;
    }

    public Elettrodomestico getElettrodomestico(int index) {
        if (index >= 0 && index < elettrodomestici.size()) {
            return elettrodomestici.get(index);
        }
        return null;
    }

    public int getNumeroElettrodomestici() {
        return elettrodomestici.size();
    }
}