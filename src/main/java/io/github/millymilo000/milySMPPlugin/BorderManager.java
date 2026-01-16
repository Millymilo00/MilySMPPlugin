package io.github.millymilo000.milySMPPlugin;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.lang.reflect.Type;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static java.util.Map.entry;

public class BorderManager {

    private HashMap<UUID, LocalTime> payingPlayers;
    private HashMap<UUID, Integer> prices;
    final private MilySMPPlugin plugin;

    public BorderManager(MilySMPPlugin plugin) {
        this.plugin = plugin;

        // load data
        try {
            readData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        // Start a task that checks every 10 minutes if the people in payingPlayers time is up.
        // TODO: test this
        plugin.getServer().getScheduler().runTaskTimer(plugin, new CheckPayingPlayersRunnable(plugin, payingPlayers), 20, 12000);
    }

    public void addUser(UUID uuid, int hours) {
        if (payingPlayers.containsKey(uuid)) {
            //increase time
            payingPlayers.replace(uuid, payingPlayers.get(uuid).plusHours(hours));
        } else {
            payingPlayers.put(uuid, LocalTime.now().plusHours(hours));
        }
        int increaseAmt = plugin.getConfig().getInt("border.cross-price-increase");

        // Increase price
        if (prices.containsKey(uuid)) {
            prices.replace(uuid, prices.get(uuid) + increaseAmt * hours);
            return;
        }
        prices.put(uuid, increaseAmt * hours + plugin.getConfig().getInt("border.initial-cross-price"));
    }

    public boolean checkUser(UUID uuid) {
        return payingPlayers.containsKey(uuid);
    }

    public int getPrice(UUID uuid) {
        if (prices.containsKey(uuid)) {
            return prices.get(uuid);
        }
        return plugin.getConfig().getInt("border.initial-cross-price");
    }

    public void readData() throws IOException {
        File borderData = new File(plugin.getDataFolder(), "borderData.json");

        if (!borderData.exists()) {
            payingPlayers = new HashMap<>();
            prices = new HashMap<>();
            return;
        }

        Gson gson = new Gson();
        Type type = new TypeToken<HashMap<String, HashMap<String, String>>>(){}.getType();
        HashMap<String, HashMap<String, String>> jsonMap = gson.fromJson(new FileReader(borderData), type);

        HashMap<UUID, LocalTime> payingPlayers = new HashMap<>();
        HashMap<UUID, Integer> prices = new HashMap<>();

        // Convert for payingPlayers
        for (Map.Entry<String, String> entry : jsonMap.get("payingPlayers").entrySet()) {
            payingPlayers.put(UUID.fromString(entry.getKey()), LocalTime.parse(entry.getValue()));
        }
        // Convert for prices
        for (Map.Entry<String, String> entry : jsonMap.get("prices").entrySet()) {
            prices.put(UUID.fromString(entry.getKey()), Integer.parseInt(entry.getValue()));
        }

        this.payingPlayers = payingPlayers;
        this.prices = prices;
    }

    public void saveData() throws IOException {
        File borderData = new File(plugin.getDataFolder(), "borderData.json");

        if (!borderData.exists()) {
            borderData.createNewFile();
        }

        Map<String, HashMap<String, String>> jsonMap = Map.ofEntries(
                entry("payingPlayers", new HashMap<String, String>()),
                entry("prices", new HashMap<String, String>())
        );

        // Convert maps to strings
        for (Map.Entry<UUID, LocalTime> entry : payingPlayers.entrySet()) {
            jsonMap.get("payingPlayers").put(entry.getKey().toString(), entry.getValue().toString());
        }
        for (Map.Entry<UUID, Integer> entry : prices.entrySet()) {
            jsonMap.get("prices").put(entry.getKey().toString(), entry.getValue().toString());
        }

        try (Writer writer = new FileWriter(borderData)) {
            Gson gson = new GsonBuilder().create();
            gson.toJson(jsonMap, writer);
        }
    }
}
