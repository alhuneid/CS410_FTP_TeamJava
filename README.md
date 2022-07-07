# CS410_FTP

# Install maven 
Maven is downloadable as a zip file at https://maven.apache.org/download.cgi. Only the binaries are required, so look for the link to apache-maven-{version}-bin.zip or apache-maven-{version}-bin.tar.gz.

Once you have downloaded the zip file, unzip it to your computer. Then add the bin folder to your path.

To test the Maven installation, run mvn from the command-line:

`mvn -v`
# Set path for Maven in your bash_profile
export PATH="$HOME/Documents/apache-maven-3.8.6/bin:$PATH" 
# Compile project
`mvn compile`
# Run the unit test
`mvn test`
# Package project
This will create a jar file to run the app. 
`mvn package` 

