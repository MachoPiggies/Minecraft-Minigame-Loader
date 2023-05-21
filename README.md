Minecraft Minigame Loader 1.8
===========

Hey there, you've found one of the projects I've been doing for fun! Not much to see here yet as I'm still working on the more intricate parts of this system. This little past-time project I've taken up is here to help Java beginners figure out how to start using Spigot, and give them some utils that can make your life easier.

If I ever end up completing this project, I'll put a Maven import tutorial here so you can clone this repo for yourself, use the api and maybe get a head start on making your own minigames to show off!
***

Disclaimer
-----------
This mini project is for reference only, this project has **not been developed to be put to use in a production environment**.

Proper, well-made Game Loaders are custom-made for servers, by developers who have catered the resources, style, paradigm, configuration and features all to their specific server and loader. This game loader has been created independently, and cannot interact with outside elements without that functionality being deliberately added through modification of the plugin. Some correct uses of this projects source are:
 * Taking code for your own projects
 * Loading on development servers to make small mini-games for your friends OR show off to potential employers
 * Learning from the way code here is set out and how you can improve your own

If you are looking for a loader that can cater to players on your network, please reach out to a qualified developer on SpigotMC or another source who will be able to advise and help you on creating a loader and games that best suit you and your servers needs.

**I will not provide any support for 'incorrect' uses of this projects code**

Usage
-----------
1. **Installing a game**
You can do this either by loading the game from inside the API by extending the Game abstract class, or by putting an independent jar into the plugins 'games' directory.
2. **Installing a map**
You can do this by loading a game on the server, a data folder for the game will be created in the 'games' folder with a 'maps' folder inside, your map goes in there. The game will add a map.yml
3. **Setting spawn locations**
Edit the map.yml and add spawns in the format `locations.spawn.teamname.spawnname.x/y/z`, neutral team is teamless
Example:
```yml
locations:
  spawn:
    neutral:
      a:
        x: 0.5
        y: 64
        z: 0.5
      b:
        x: 10.5
        y: 64
        z: -10.5
  
```

Pre-requisites
-----------
1. [Maven](https://maven.apache.org/download.cgi?.) (Maven 3)
2. [Spigot BuildTools 1.8.8](https://www.spigotmc.org/wiki/buildtools/)
3. [Git](https://git-scm.com/downloads) (Obviously)

Installing
-----------

Whilst I don't really recommend you try installing this yet, since I'm still actively working on it, I'm not going to stop you, but at least let me help you do it right haha.

1. Git clone this repository using `git clone https://github.com/MachoPiggies/Minecraft-Minigame-Loader.git` or the CLI if you're weird 
2. Open the repository preferably in [Intellij](https://www.jetbrains.com/idea/download/?fromIDE=#section=windows), IntelliJ should automatically load the Maven project, if it doesn't a message with the button "Load Maven Build" will appear in the bottom right
3. Once Maven has built successfully, go to the Maven tab on the far right, find 'game-loader', open 'Lifecycle' under it and click 'install', if you can't find it, run `maven clean install`
4. This will install this repository into your m2 folder since I don't have my own Nexus repository
5. Once this is done, you should see some of the errors start clearing up. Go to the pom.xml of gameloader, any red dependencies means that they haven't installed properly, send me a DM if this is happening and I can help
6. Once you want to export the plugin, go to the same UI where you installed the loader, but go to 'game-loader-plugin' instead and hit 'package'
7. Once you've done that, your plugin jar should appear in the 'target' file inside the game loader plugin folder

Requiring the API
-----------

1. To require the API with or without the plugin to use in your own games, follow steps 1-5
2. After that, you can add the API as a normal Maven dependency
3. The API **MUST** be shaded if the game loader plugin is not in your server

#### Maven Dependency
```xml
<dependency>
    <groupId>com.machopiggies</groupId>
    <artifactId>game-loader-api</artifactId>
    <version>0.0.1</version>
</dependency>
```

#### Maven Shade Example
```xml
<plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-shade-plugin</artifactId>
    <version>3.1.0</version>
    <configuration>
        <filters>
            <filter>
                <artifact>*:*</artifact>
                <excludes>
                    <exclude>META-INF/*.SF</exclude>
                    <exclude>META-INF/*.DSA</exclude>
                    <exclude>META-INF/*.RSA</exclude>
                </excludes>
            </filter>
        </filters>
    </configuration>
    <executions>
        <execution>
            <phase>package</phase>
            <goals>
                <goal>shade</goal>
            </goals>
            <configuration>
                <artifactSet>
                    <includes>
                        <include>com.machopiggies:game-loader-api</include>
                    </includes>
                </artifactSet>
                <minimizeJar>true</minimizeJar>
                <filters>
                    <filter>
                        <artifact>com.machopiggies:game-loader-api</artifact>
                        <includes>
                            <include>com/machopiggies/gameloaderapi/**</include>
                        </includes>
                    </filter>
                </filters>
            </configuration>
        </execution>
    </executions>
</plugin>
```