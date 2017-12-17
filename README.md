# dPRM: a stochastic model for detecting post-transcriptional regulatory modules from  heterogeneous biological networks 

Requirements
------------
  1. Install Java JVM.
  2. Add the JVM directory to the $PATH environment variable.

Installation:
------------
  The source code can be directly called from Java.

Usage:
--------------------------------

    $ python mcg_scanning.py -n num_feature -w window_size_list -r ratio_testing -i input_data
    $ ptyhon boosting_cascade_forest.py -f num_foresrt -k topk -n num_jobs -o output_file_name 
    
The examples of input files are available with the zipped source code. The input format is detailed in the Example section.

Example:
--------------------------------

    $ python mcg_scanning.py -n 5597 -w [5582,5590] -r 0.2 -i brain_test.txt
    $ ptyhon boosting_cascade_forest.py -f 2 -k 5 -n 1 -o output_result.txt

Input: in the input data, each line is a sample, and each column is a feature of each sample. 

  - Label: the known label of each sample in training data;
  - Sample_ID: Ids of samples.
  - G.1: the first feature
  - G.2: the secod feature

Output: the labels of testing samples
