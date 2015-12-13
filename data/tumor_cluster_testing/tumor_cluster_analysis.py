


output = open("tumor_cluster_analysis.txt","w")

files = []
for j in xrange(1, 101):
    clusters = []
    for i in xrange(0,8):
        clusters.append([])
    file_name = "Hm4_cluster_10_test_"+str(j)+"_tumor.txt"
    files.append(open(file_name,"r"))
for f in files:
    line_num = 0
    for line in f.readlines():
        clusters[line_num].append(line)
        line_num = line_num + 1
    f.close()
for cluster in clusters:
    sums = []
    for i in xrange(0, 10):
        sums.append([])
    for clus in cluster:
        cl = clus.split("\t")
        for c in xrange(0,10):
            sums[c].append(float(cl[c]))
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

         
