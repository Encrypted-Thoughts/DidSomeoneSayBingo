# Creates all scores required by the datapack

scoreboard objectives add bingo_update_pending dummy
scoreboard objectives add bingo_winning_team dummy

scoreboard players set #bingo bingo_update_pending 0
scoreboard players set #bingo bingo_winning_team 0
