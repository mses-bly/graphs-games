import random as rnd
def genNEvidence(N, epsilon = 0.005):
    evidence = []
    for i in range(N):
        evidence.append(round(rnd.random() + epsilon))
    if sum(evidence) > 0.5*N:
        return 1
    else:
        return 0
