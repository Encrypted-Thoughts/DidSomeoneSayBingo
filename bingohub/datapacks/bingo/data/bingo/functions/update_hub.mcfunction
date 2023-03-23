#update hub to colors of the winning team

execute if score #bingo bingo_winning_team matches 0 run function bingo:update_hub_black
execute if score #bingo bingo_winning_team matches 1 run function bingo:update_hub_red
execute if score #bingo bingo_winning_team matches 2 run function bingo:update_hub_orange
execute if score #bingo bingo_winning_team matches 3 run function bingo:update_hub_yellow
execute if score #bingo bingo_winning_team matches 4 run function bingo:update_hub_green
execute if score #bingo bingo_winning_team matches 5 run function bingo:update_hub_cyan
execute if score #bingo bingo_winning_team matches 6 run function bingo:update_hub_blue
execute if score #bingo bingo_winning_team matches 7 run function bingo:update_hub_purple
execute if score #bingo bingo_winning_team matches 8 run function bingo:update_hub_pink



scoreboard players set #bingo bingo_update_pending 0