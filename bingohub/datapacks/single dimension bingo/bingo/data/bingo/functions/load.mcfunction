# Creates all scores required by the datapack

scoreboard objectives add bingo_update_pending dummy
scoreboard objectives add bingo_winning_team dummy
scoreboard objectives add bingo_games_played dummy
scoreboard objectives add bingo_win dummy
scoreboard objectives add bingo_loss dummy
scoreboard objectives add bingo_percentage dummy

scoreboard players set #bingo bingo_update_pending 0
scoreboard players set #bingo bingo_winning_team 0
scoreboard players set #bingo bingo_games_played 0
scoreboard players set #bingo bingo_win 0
scoreboard players set #bingo bingo_loss 0
scoreboard players set #bingo bingo_percentage 0

scoreboard players set #bingo_red bingo_win 0
scoreboard players set #bingo_red bingo_loss 0
scoreboard players set #bingo_orange bingo_win 0
scoreboard players set #bingo_orange bingo_loss 0
scoreboard players set #bingo_yellow bingo_win 0
scoreboard players set #bingo_yellow bingo_loss 0
scoreboard players set #bingo_green bingo_win 0
scoreboard players set #bingo_green bingo_loss 0
scoreboard players set #bingo_cyan bingo_win 0
scoreboard players set #bingo_cyan bingo_loss 0
scoreboard players set #bingo_blue bingo_win 0
scoreboard players set #bingo_blue bingo_loss 0
scoreboard players set #bingo_purple bingo_win 0
scoreboard players set #bingo_purple bingo_loss 0
scoreboard players set #bingo_pink bingo_win 0
scoreboard players set #bingo_pink bingo_loss 0


