#!/bin/tcsh

set iterations = $1
set clusters = $2
set alpha = $3
set init_random = $4

echo "Beginning"
    foreach y (`seq 1 1 $iterations`)
        java cs475.KCluster.KClusterTester -init_random {$init_random} -alpha {$alpha} -clusters {$clusters} -gd_iterations -1 -data ../data/unsupervised/Hm4_unsupervised_dump.txt -testing 1 > Hm4_cluster_{$clusters}_test_{$y}_tumor.txt
    end
echo "Complete" 

mkdir tumor_cluster_testing/
mv *.txt tumor_cluster_testing/
