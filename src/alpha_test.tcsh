#!/bin/tcsh

set iterations = $1
set clusters = $2
set alpha = $3

echo "Beginning"
    foreach y (`seq 1 1 $iterations`)
        java cs475.KCluster.KClusterTester -clusters {$clusters} -gd_iterations -1 -init_random 1 -data ../data/unsupervised/Hm4_unsupervised_dump.txt > Hm4_alpha_{$alpha}_test_{$y}.txt
    end
echo "Complete" 

