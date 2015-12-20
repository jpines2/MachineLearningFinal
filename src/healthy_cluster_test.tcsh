#!/bin/tcsh

set iterations = $1

echo "Beginning"
foreach x (`seq 5 1 10`)
    foreach y (`seq 1 1 $iterations`)
        java cs475.KCluster.KClusterTester -init_random 1 -clusters {$x} -gd_iterations -1 -data ../data/unsupervised/Hm4_unsupervised_dump.txt > Hm4_cluster_{$x}_test_{$y}.txt
    end
end 
echo "Complete" 

mkdir healthy_cluster_testing_2/
mv *.txt healthy_cluster_testing_2/
