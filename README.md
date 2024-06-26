![Did Someone Say Bingo?](https://github.com/Encrypted-Thoughts/DidSomeoneSayBingo/assets/50642352/8c5cac65-c16f-4b22-ad1c-bb06ff9e50e5)
---
![License](https://img.shields.io/github/license/Encrypted-Thoughts/DidSomeoneSayBingo)
![Issues](https://img.shields.io/github/issues/Encrypted-Thoughts/DidSomeoneSayBingo?label=Issues)
![Version](https://img.shields.io/github/v/tag/Encrypted-Thoughts/DidSomeoneSayBingo)
![Downloads](https://img.shields.io/github/downloads/Encrypted-Thoughts/DidSomeoneSayBingo/total)

DSSBingo is a fabric version of my interpretation of minecraft bingo intended to be able to run as a server side only mod where vanilla clients can join and play.

## Installation
The below steps assume you have already setup a fabric 1.20.6 server. See [fabricmc.net/use/installer](https://fabricmc.net/use/installer) to download the fabric installer and for installation instructions.
1. Download the latest release file [here](https://github.com/Encrypted-Thoughts/DidSomeoneSayBingo/releases).
2. Extract/unzip it in the same directory as your server.jar file and overwrite the existing `world`, `config`, and `mods` folders. If your world is named something other than `world` you'll need to open the world folder in the zip and copy the content into the equivalent folder on your server.
3. If you want to play with smaller randomized biomes then rename the `tinybiomes.zip.disabled` file in `world/datapacks` to be `tinybiomes.zip`. 
4. Install any other mods you might want to use. The below are recommended:
  * [Lithium](https://www.curseforge.com/minecraft/mc-mods/lithium) - General purpose optimization mod that improves a variety of systems in minecraft.
  * [Servercore](https://www.curseforge.com/minecraft/mc-mods/servercore) - A variety of extra optimizations on top of lithium that alter vanilla gameplay slightly. Allows for things like dynamic changing of server simulation distance, render distance, and mobcaps based on server performance.
  * [Spark](https://www.curseforge.com/minecraft/mc-mods/spark) - Allows running profiles to be able to troubleshoot server performance issues or share with someone who might be able to.

For more advanced configuration like editing/creating your own item pools and game presets see the [wiki](https://github.com/Encrypted-Thoughts/DidSomeoneSayBingo/wiki). 

## Commands
Everything that happens in the mod can be controlled through the commands. For a [list of all of the commands see the wiki page](https://github.com/Encrypted-Thoughts/DidSomeoneSayBingo/wiki/Commands).

![bingo card full](https://github.com/Encrypted-Thoughts/DidSomeoneSayBingo/assets/50642352/79738922-af13-44e1-8340-6b5127a47b55)
