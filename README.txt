How the installer works WINDOWS ONLY
    connect with github and scrape the list of all tags, the first being the latest
    the string is trimmed so we only get the latest tags version number as a string like v0.0.5
    trim this again down to just a number like 0.5 to be able to compare numerically
    then 0.5 gets compared to the file name of the local version of the program if there is one
    if theres not one or the 0.5 is > the local version name run the install
    the install downloads the latest releases assets which are bundled in the a zip
    the program then deletes the old executable that was used to compare version numbers
    then it unzips the directory in the users downloads and makes a new folder with the mech executable
    
How the releases work
    each github release has a tag in the form of vX.X.X
    this list of tags is used to find the latest releases version number
    each release has an accompanying zip file named mech.zip
    inside this zip is a mechvX.X.X.exe that has the file name match the version number of the tag

ignore testprog.c and the workflows section
the releases tab is meant to simulate mech releases, code for the installer is not included in the releases
