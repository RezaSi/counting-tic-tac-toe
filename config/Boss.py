import sys
import math
import random

# Get 3 cells in a row!

table = []
for i in range(9):
    table.append([0] * 9)

def dist(a, b):
    return (a[0] - b[0]) ** 2 + (a[1] - b[1]) ** 2

def best_move(valid_actions):
    bm = (-1, -1)
    bm_score = 10000000
    for action in valid_actions:
        res = 0
        for i in range(9):
            for j in range(9):
                if table[i][j] == 1:
                    res += dist((i, j), action)
        if res < bm_score:
            bm_score = res
            bm = action
    return bm
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
    bm = best_move(valid_actions)
    table[bm[0]][bm[1]] = 1
    print(f"{bm[0]} {bm[1]}")
