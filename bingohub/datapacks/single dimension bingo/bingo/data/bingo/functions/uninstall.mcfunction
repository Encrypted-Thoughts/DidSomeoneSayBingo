# Removes all scores required by the datapack

schedule clear bingo:tick

scoreboard objectives remove bingo_update_pending
scoreboard objectives remove bingo_winning_team
scoreboard objectives remove bingo_games_played
scoreboard objectives remove bingo_win
scoreboard objectives remove bingo_loss
scoreboard objectives remove bingo_percentage