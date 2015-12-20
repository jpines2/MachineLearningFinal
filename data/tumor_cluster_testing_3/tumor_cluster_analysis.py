


output = open("tumor_cluster_analysis.txt","w")

files = []
results = []
for i in xrange(0,8):
    results.append([])
for j in xrange(1, 101):
    file_name = "Hm4_cluster_10_test_"+str(j)+"_tumor.txt"
    files.append(open(file_name,"r"))

sums = []
for i in xrange(0, 8):
    sums.append([])
    for j in xrange(0,10):
        sums[i].append([])
for f in files:
    line_num = 0
    for line in f.readlines():
	l = line.rstrip().split("\t")
        for i in xrange(0,len(l)):
            sums[line_num][i].append(float(l[i]))
        line_num = line_num + 1
    f.close()

print len(sums[1][3])
print sums[1][3]

for i in xrange(0,8):
    averages = []
    variances = []
    for j in xrange(0,10):
        averages.append(sum(sums[i][j])/float(len(sums[i][j])))
        variance = float(0.0)
        for k in xrange(0, len(sums[i][j])):
            variance += (averages[j]-sums[i][j][k])**2
        variance = variance / len(sums[i][j])
        variances.append(variance)
    
    output.write(str(averages) + "\n")
    output.write(str(variances) + "\n")
    output.write("\n")
output.close()

         
