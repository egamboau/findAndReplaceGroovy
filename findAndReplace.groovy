#! /usr/bin/env groovy

import groovy.io.FileType
import groovy.util.logging.Log


@Log
class FindAndReplace {

    //the script arguments
    String[] arguments

    //variables to hold the required parameters, to make the code easier to read
    File directoryPath
    String patternToSearch
    String textToReplace

    //optional parameters
    def logFilePath

    FindAndReplace(String[] args) {
        arguments = args
    }

    /**
    * Function that will print the script usage on screen, in case of that the script is misused
    */
    def printHelp() {
        //we get the path and name of the current script dynamically, and then use it to print the error
        String scriptFile = getClass().protectionDomain.codeSource.location.path
        log.info("Usage: ${scriptFile} <directory to search> <pattern to search> <text to replace> [path for log]")
        System.exit(1)
    }

    /**
     * Function that will validate the parameters provided to the script.
     * If any parameter is invalid, then we print the corresponded error
     */
     def validateParameters() {
        //first, we need a path to scan for files. We check for the parameter, and if the provided directory exists
        if(arguments.size() == 0)  {
            // no parameters provided Print the usage help
            printHelp()
            } else {
                boolean allParametersDefined = false
                //we have some parameters, so we can iterate over them, based on the index, and
                arguments.eachWithIndex { argument, index ->
                    switch(index) {
                        case 0:
                            //this is the path for files
                            directoryPath = new File(argument)
                            if(!directoryPath.exists()) {
                                log.info "The directory at ${argument} does not exists"
                                System.exit(2)
                            } else if (!directoryPath.isDirectory()) {
                                log.info "The path ${arguments[index]} is not a directory"
                                System.exit(3)
                            }
                            break
                        case 1:
                            //the pattern
                            patternToSearch = argument
                            break
                        case 2:
                            //the text that will replace the pattern. With this, we will complete the command and we are ready
                            //to run
                            textToReplace = argument
                            allParametersDefined = true
                            break
                        case 3:
                            //this is the optional log file, check for existence. It the path does not exists, we create it
                            logFilePath = new File(argument)
                            break
                        default:
                            //if we reach here, it means that we have extra parameters, print the help
                            printHelp()
                        }
                    }
                    if(!allParametersDefined) {
                    //command incomplete
                    printHelp()
            }
        }
    }

    /**
     * Function that will search for the files on the current path, search for the given pattern, and
     * replace it with the current text. All of the current parameters are read from the global variables,
     * previously validated
     */
     def scanDirectoryForTextReplacements() {
        def modifiedFiles = new HashSet()
        directoryPath.eachFileRecurse (FileType.FILES) { file ->
            //grab only the files ending on txt
            if(file.name.endsWith('.txt')) {
                log.info "searching on ${file.getCanonicalPath()}" 
                //we found a file, for security, we create a backup, and write on the original one
                def originalFileName = file.getCanonicalPath()
                def backupFileName = "${originalFileName}.backup"
                file.renameTo backupFileName

                //for performance, create a BufferedWriter, to prevent a lot of I/O operations
                new File(originalFileName).withWriter { bufferWriter ->
                    //also, for the replacement, we choose the buffered Reader approach, for performance reasons
                    new File(backupFileName).withReader { bufferedReader ->
                        bufferedReader.eachLine { line, count ->
                            log.info "Searching on line ${count}"
                            def replacedLine = line.replaceAll(patternToSearch) {
                                // if we got a match, this means that the file is modified, we need to log it
                                modifiedFiles << originalFileName
                                log.info "replacing pattern on line ${count}"
                                textToReplace
                            }
                            bufferWriter << replacedLine + System.getProperty("line.separator")
                        }
                        bufferWriter.flush()
                    }
                }
            }
        }
        //write the log, if we have it configured
        if(logFilePath) {
            logFilePath.delete()
            modifiedFiles.each {
                logFilePath << "${it}\n"
            }
        }
    }

    def execute() {
        log.info "Starting script"
        //first, we validate the parameters for the current execution
        validateParameters()
        //then, we just run the directory scan
        scanDirectoryForTextReplacements()
        log.info "Script Finished"
    }
}


def findAndReplace = new FindAndReplace(args)
findAndReplace.execute()