


output = open("alpha5.0_cluster_analysis.txt","w")
files = []
for j in xrange(1, 101):
    sums = []
    for c in xrange(0, 10):
        sums.append([])
    clusters = []
    file_name = "Hm4_alpha_5.0_test_"+str(j)+".txt"
    files.append(open(file_name,"r"))
for f in files:
    clusters.append(f.readline())
    f.close()
for cluster in clusters:
    clus = cluster.split("\t")
    for c in xrange(0,10):
        sums[c].append(float(clus[c]))
averages = []
variances = []
for c in xrange(0,10):
    averages.append(sum(sums[c])/float(len(sums[c])))
    variance = float(0.0)
    for d in xrange(0,len(sums[c])):
        variance += (averages[c]-sums[c][d])**2
    variance = variance / len(sums[c])
    variances.append(variance)
output.write(str(averages) + "\n")
output.write(str(variances) + "\n")
output.write("\n")
output.close()

         
