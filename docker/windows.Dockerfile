FROM mcr.microsoft.com/windows:ltsc2019

# Install Java 17 Development Kit (JDK17) to support Gradle 8.4, which Kotlin Multiplatform supports
RUN powershell (new-object System.Net.WebClient).Downloadfile('https://download.oracle.com/java/17/latest/jdk-17_windows-x64_bin.exe', 'C:\jdk-17_windows-x64_bin.exe')
RUN powershell start-process -filepath C:\jdk-17_windows-x64_bin.exe -passthru -wait -argumentlist "/s,INSTALLDIR=c:\Java\jre17,/L,install64.log"
RUN del C:\jdk-17_windows-x64_bin.exe

# Copy the app over and set the current directory to it
COPY . "C:\app"
WORKDIR "C:\app"

# Run Gradle, which installs Gradle and then executes the package command
RUN ["gradlew.bat", "packageReleaseMsi"]

# TODO - Export the file into a GitHub release
# File is by default located under composeApp -> build -> compose -> binaries -> main-release -> msi