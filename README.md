# findAndReplaceGroovy
Search and replace implementation using a groovy script.

#To Execute it
##Linux/Unix enviroments
To run on linux and any unix-like enviroments, two options are available

1. Using bash, give execution permission to the file with `chmod +x findAndReplace.groovy`, and then execute it with `./findAndReplace.groovy`
1. Use the `groovy` command to execute the script directly with `groovy findAndReplace`

The script was not tested on Windows enviroments, however it should run with the `groovy` command
##Enviroments
The script was created on MacOS 10.11 El Capitan, with groovy version 2.4.5 and Java SDK version 1.0.7_79, from Oracle

#Syntax
To execute the script, the following format should be used:

`findAndReplace.groovy <directoryToSearch> <patternToSearch> <textToWrite> [logFilePath]` on which
- directoryToSearch: is the path on which the text files are. The script only accepts files ending on .txt. The directory is iterated on a recursive way, so files on subdirectories will be include on the search too
- patternToSearch: is the pattern that the script will try to find on the files on the directory and subdirectories.
- textToWrite: the text that will be written when the script finds any match
- logFilePath: Optional pattern, represents a file where the script will log all the modified files.
