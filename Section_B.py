results = {}
for i in range(5000, 105000, 5000):
    results[i] = genNEvidence(i)
print 1.0 * sum(results.values()) / len(results.values())