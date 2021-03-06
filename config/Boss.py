import sys
import math
import random

table = []
for i in range(9):
    table.append([0] * 9)

# game loop
while True:
    # opponent_row: The coordinates of your opponent's last move
    opponent_row, opponent_col = [int(i) for i in input().split()]
    if opponent_row != -1:
        table[opponent_row][opponent_row] = 2
    valid_action_count = int(input())  # the number of possible actions for your next move
    valid_actions = []
    for i in range(valid_action_count):
        # row: The coordinates of a possible next move
        row, col = [int(j) for j in input().split()]
        valid_actions.append((row, col))

    # Write an action using print
    # To debug: print("Debug messages...", file=sys.stderr, flush=True)

    # <row> <column>
    random_move = random.choice(valid_actions)
    table[random_move[0]][random_move[1]] = 1
    print(f"{random_move[0]} {random_move[1]}")
