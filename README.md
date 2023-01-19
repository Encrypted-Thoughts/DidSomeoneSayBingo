# Did Someone Say Bingo?
![License](https://img.shields.io/github/license/Encrypted-Thoughts/DidSomeoneSayBingo)
![Issues](https://img.shields.io/github/issues/Encrypted-Thoughts/DidSomeoneSayBingo?label=Issues)
![Version](https://img.shields.io/github/v/tag/Encrypted-Thoughts/DidSomeoneSayBingo)
![Downloads](https://img.shields.io/github/downloads/Encrypted-Thoughts/DidSomeoneSayBingo/total)


DSSBingo is a fabric version of my interpretation of minecraft bingo intended to be able to run as a server side only mod where vanilla clients can join and play.

## Installation
For installation instructions see: [Server]() or [Single Player]() 

## Commands
Everything that happens in the mod can be controlled through the commands listed below.

| Command                                                                         | Op Only | Descriptions |
| :---                                                                            |  :---:  | :---         |
| `/teamtp`                                                                       | no      | Teleport to a random teammate that's more than 50 blocks away |
| `/clarify <row> <column>`                                                       | no      | Get the name of the item at the specified row and column on the bingo card |
| `/bingo`                                                                        | no      | Gets this list of commands and their descriptions |
| `/bingo generate <start>`                                                       | no      | Generates a new bingo card and optionally starts a game at the same time |
| `/bingo start`                                                                  | no      | Starts a game of bingo with the current generated card |
| `/bingo end`                                                                    | yes     | Command to forcefully end the current game of bingo |
| `/bingo preset <preset>`                                                        | no      | Set the game settings to a predefined preset |
| `/bingo settings`                                                               | no      | View current bingo settings |
| `/bingo settings gamemode <mode>`                                               | no      | Set the game mode type to play. Bingo: just normal bingo. Lockout: Getting an item locks it out from other teams. Win on bingo or getting the majority of items. Blackout: Get all the items to win. |
| `/bingo settings dimension <dimension>`                                         | no      | Set the dimension the game will start in |
| `/bingo settings timer <minutes>`                                               | no      | Set a time limit in minutes for the game. Set to 0 minutes for no timer. |
| <code>/bingo settings radius <small&#124;medium&#124;large></code>              | no      | Set the size of the area that teams will be spawned in. This won't stop teams from spawning within 100 blocks of each other but reduces the chance they will with a larger area. |
| `/bingo settings randomize`                                                     | no      | Set game settings to a random game preset |
| `/bingo settings ySpawnOffset <integer>`                                        | no      | The offset to spawn above the ground. Defaults to 50 for overworld and 0 for nether. |
| `/bingo settings maxYLevel <integer>`                                           | no      | The max height at which to find a spawn point. Defaults to 200 for overworld and 110 for nether. |
| `/bingo settings effects add <effect> <duration> <amplifier> <ambient> <particles> <icon> <respawn>` | no      | Add a status effect to play the game with and whether you only get them at the beginning of the game or at every respawn |
| `/bingo settings effects remove <effect>`                                       | no      | Remove a status effect |
| `/bingo settings effects clear`                                                 | no      | Remove all status effects |
| `/bingo settings equipment add <item> <amount> <respawn> <equip>`               | no      | Add starting equipment to begin the game with. Enchantments can be added but currently this must be done as part of a preset config file. |
| `/bingo settings equipment remove <item>`                                       | no      | Remove starting equipment |
| `/bingo settings equipment clear`                                               | no      | Remove all starting equipment |
| <code>/bingo settings equipment <stone&#124;iron&#124;diamond&#124;food></code> | no      | Add sets of tools or bread as starting equipment |
| `/bingo settings items add <itempool`                                           | no      | Add an item pool. New Item pools can be defined as config files. |
| `/bingo settings items remove <itempool>`                                       | no      | Remove an item pool |
| `/bingo settings items clear`                                                   | no      | Remove all item pools |
| `/bingo getmap`                                                                 | no      | Get a new a bingo card in case you toss yours away somehow during a game |
| `/bingo pvp <enabled>`                                                          | no      | Enable or disable pvp |
| `/bingo team join <team>`                                                       | no      | Join one of the 8 available teams |
| `/bingo team set <player> <team>`                                               | yes     | Set a player to a team |
| `/bingo team randomize <teams>`                                                 | no      | Randomize all current bingo players equally to different teams if possible. Optionally can specify an amount of teams to randomize players into. |
| `/bingo team voteend`                                                           | no      | Start a vote to end the current game |
| `/bingo team spawn spawnPoint <coords>`                                         | yes     | Set the spawn point for the bingo spawn hub |
| `/bingo team spawn displayPoint <coords>`                                       | yes     | Set the bottom left corner where the bingo board will generate |
| <code>/bingo team spawn concrete <start&#124;stop&#124;coords></code>           | yes     | Set coordinates to change into the winning team colors in concrete on game end. |
| <code>/bingo team spawn glass <start&#124;stop&#124;coords></code>              | yes     | Set coordinates to change into the winning team colors in glass on game end. |



![bingo card full](https://user-images.githubusercontent.com/50642352/213332281-6ba61f83-348b-4bb1-a0ee-d78bf36de9fd.png)
