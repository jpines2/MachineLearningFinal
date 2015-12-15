import sys

def file_to_list(file_name):
    filey = open(file_name,"r")
    content = filey.readlines()
    filey.close()
    return content

def correlation_acquisition(line):
    line_strip = line.rstrip()
    line_split = line_strip.split(' ')
    return line_split[3]

def main(): 
    LHIPPO_content = file_to_list("LHIPPO_raw_dump.txt")
    RHIPPO_content = file_to_list("RHIPPO_raw_dump.txt")
    LNEO_content = file_to_list("LNEO_raw_dump.txt")
    RNEO_content = file_to_list("RNEO_raw_dump.txt")
    BRAINSTEM_content = file_to_list("BRAINSTEM_raw_dump.txt")
    LOLF_content = file_to_list("LOLF_raw_dump.txt")
    ROLF_content = file_to_list("ROLF_raw_dump.txt")
    LTHAL_content = file_to_list("LTHAL_raw_dump.txt")
    RTHAL_content = file_to_list("RTHAL_raw_dump.txt")
    LSTRIATUM_content = file_to_list("LSTRIATUM_raw_dump.txt")
    RSTRIATUM_content = file_to_list("RSTRIATUM_raw_dump.txt")
    LHYPO_content = file_to_list("LHYPO_raw_dump.txt")
    RHYPO_content = file_to_list("RHYPO_raw_dump.txt")

    m5_dump = open("Hm5_supervised_dump.txt","w")
    for i in xrange(0, len(LHIPPO_content)):
        LHIPPO_corr = correlation_acquisition(LHIPPO_content[i])
        RHIPPO_corr = correlation_acquisition(RHIPPO_content[i])
        LNEO_corr = correlation_acquisition(LNEO_content[i])
        RNEO_corr = correlation_acquisition(RNEO_content[i])
        BRAINSTEM_corr = correlation_acquisition(BRAINSTEM_content[i])
        LOLF_corr = correlation_acquisition(LOLF_content[i])
        ROLF_corr = correlation_acquisition(ROLF_content[i])
        LTHAL_corr = correlation_acquisition(LTHAL_content[i])
        RTHAL_corr = correlation_acquisition(RTHAL_content[i])
        LSTRIATUM_corr = correlation_acquisition(LSTRIATUM_content[i])
        RSTRIATUM_corr = correlation_acquisition(RSTRIATUM_content[i])
        LHYPO_corr = correlation_acquisition(LHYPO_content[i])
        RHYPO_corr = correlation_acquisition(RHYPO_content[i])
        
        output_line = '0 ' + '1:'+LHIPPO_corr + ' 2:'+RHIPPO_corr + ' 3:'+LNEO_corr + ' 4:'+RNEO_corr + ' 5:' + BRAINSTEM_corr + ' 6:' + LOLF_corr + ' 7:' + ROLF_corr + ' 8:' + LTHAL_corr + ' 9:'+ RTHAL_corr + ' 10:' + LSTRIATUM_corr + ' 11:' + RSTRIATUM_corr + ' 12:' + LHYPO_corr + ' 13:' + RHYPO_corr
        m5_dump.write(output_line+'\n')
    m5_dump.close()
    

if __name__ == '__main__':
    main()




