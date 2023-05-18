package com.machopiggies.gameloaderapi.load;

import com.machopiggies.gameloaderapi.excep.InvalidGameException;
import com.machopiggies.gameloaderapi.game.Game;
import com.machopiggies.gameloaderapi.game.GameInfo;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;

public class GameClassLoader extends URLClassLoader {
    private final Game game;

    public GameClassLoader(ClassLoader parent, GameInfo description, File file) throws IOException, InvalidGameException {
        super(new URL[]{file.toURI().toURL()}, parent);

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName(description.getMainClass(), true, this);
            } catch (ClassNotFoundException e) {
                throw new InvalidGameException("Could not find main class " + description.getMainClass() + " from " + file.getName(), e);
            }

            if (description.getInternalName() == null) {
                throw new InvalidGameException("games internal name cannot be null");
            }

            Class<? extends Game> gameStage;
            try {
                gameStage = jarClass.asSubclass(Game.class);
            } catch (ClassCastException e) {
                throw new InvalidGameException("Main class " + description.getMainClass() + " in " + file.getName() + " does not appear to extend Game", e);
            }

            this.game = gameStage.getDeclaredConstructor().newInstance();
            this.game.setInfo(description);

            if (getConfigFile() != null) {
                File mapsFile = new File(getConfigFile().getParentFile(), "maps");
                if (!mapsFile.exists()) {
                    mapsFile.mkdir();
                }
                File mapsConfig = new File(mapsFile, "maps.yml");
                if (!mapsConfig.exists()) {
                    mapsConfig.createNewFile();
                }

                File[] subs = getConfigFile().listFiles();
//                List<GameMap> maps = new ArrayList<>();
//                if (subs != null) {
//                    Map<String, File> mapFiles = new HashMap<>();
//                    for (File map : subs) {
//                        if (!map.getName().toLowerCase().endsWith(".schem") && !map.getName().toLowerCase().endsWith(".schematic")) continue;
//                        mapFiles.put(map.getName(), map);
//                    }
//
//                    YamlConfiguration mapYml = YamlConfiguration.loadConfiguration(mapsConfig);
//                    for (Map.Entry<String, File> entry : mapFiles.entrySet()) {
//                        String internal = entry.getKey().replace(" ", "_");
//                        if (mapYml.contains("maps." + internal)) {
//                            maps.add(new GameMap(
//                                    mapYml.getString("maps." + internal + ".name", "A Map"),
//                                    mapYml.getString("maps." + internal + ".internalName", "a_map"),
//                                    mapYml.getStringList("maps." + internal + ".creators"),
//                                    entry.getValue()
//                            ));
//                        } else {
//                            mapYml.set("maps." + internal + ".name", "A Map");
//                            mapYml.set("maps." + internal + ".internalName", "a_map");
//                            mapYml.set("maps." + internal + ".creators", new ArrayList<>(Collections.singletonList("The Server")));
//                            mapYml.set("maps." + internal + ".name", "A Map");
//                            maps.add(new GameMap(
//                                    "A Map",
//                                    "a_map",
//                                    new ArrayList<>(Collections.singletonList("The Server")),
//                                    entry.getValue()
//                            ));
//                        }
//                    }
//
//                    mapYml.save(mapsConfig);
//                }
//
//                game.setMaps(maps);
            }
        } catch (IllegalAccessException ex) {
            throw new InvalidGameException("Main constructor of " +  description.getMainClass() + " in " + file.getName() + " is not public", ex);
        } catch (InstantiationException ex) {
            throw new InvalidGameException("Unexpected Game type " + description.getMainClass() + " in " + file.getName(), ex);
        } catch (NoSuchMethodException | InvocationTargetException ex) {
            throw new InvalidGameException("Exception thrown whilst trying to instantiate main class " + description.getMainClass() + " in " + file.getName(), ex);
        }
    }

    /**
     * Gets the {@link Game} that this {@link GameClassLoader} has been able to load.
     *
     * @return the loaded {@link Game}
     */
    public Game getGame() {
        return game;
    }

    /**
     * Gets the configuration file (config.yml) of a {@link Game}.
     *
     * @return the configuration file of a {@link Game}
     */
    public File getConfigFile() {
        File configFile = new File(game.getInfo().getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                if (!configFile.createNewFile()) {
                    throw new IOException("unable to create config.yml for game " + (game.getInfo().getName() != null ? "'" + game.getInfo().getName() + "' (" + game.getInfo().getInternalName() + ")" : game.getInfo().getInternalName()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return configFile;
    }
}

