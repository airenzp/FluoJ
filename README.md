Fluorescence microscopy became a common tool in cell and molecular biology. Image analysis of fluorescent molecules provides key 
information about the location, interactions, and dynamic behavior of target molecules. Usually user needs to recognize, characterize 
and classify particles, and in many cases to do statistical inferences from data collected. 
FluoJ is a software tool to automate many of these steps. We used it to measure sperm quality, cells movement, virus infection in plants
 and apoptosis in cancer cells. This results were achieved providing a customizable environment for particles detection, measurement and
 classification.
A wide number of predefined features can be used to characterize fluorescent specimens (e.g. area, circularity, brightness, etc), 
and new descriptors can be easily added. Once configured, software can be trained to automatically classify particles or used directly
 for automatic image processing.

To run this plugin you need to add the project jar generated in the target folder to the ImageJ plugins directory and renamed it to 
FluoJ_.jar. The project dependencies also need to be added to the /Library/Java/Extensions folder, except for the imagej library, that is
 already present in the ImageJ installation. These jars can be obtained running the maven-assembly-plugin during compilation.




