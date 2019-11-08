# OpenVXML Dev Guide - Internal use only

## Setting up for your workspace

### Prerequisites

**JDK-1.8**

> Currently JDK 1.8 is at https://www.oracle.com/java/technologies/jdk8-downloads.html
>

**Eclipse Kepler** 

​	Version : SR2 with Java8 Patches

​	Package : RCAP and RAP Developers

> Current location is at https://archive.eclipse.org/technology/epp/downloads/release/kepler/SR2-with-Java8-patches/eclipse-rcp-kepler-SR2-Java8-win32-x86_64.zip



### Workspace Setup 

#### Clone GitHub repositories

[^Suggestion]: Have all git repos under one root folder called "GitHub"

1. OpenVXML:  https://github.com/VHT/OpenVXML

   [^Branch]: 6.0.0

2. VHT Toolkit:   https://github.com/VHT/VXML-IVR

   [^Branch]: master



#### Eclipse configuration setup

Start Eclipse and select your workspace

> **From the Eclipse menu:**
>
> Window > Preferences > Java > Compiler > Set Compiler Compliance Level to 1.8

Install below listed plugins and enter the details given as per the request text-box

From the Eclipse menu: Help > Install New Software > Add

***<u>Plugins:</u>***

> **Name:** Elements
>
> **Location:**  https://build.vhtcx.com/downloads/Elements/repository/ 
>
> **Selection:** select all 

> **Name:** Orbit 2013
>
> **Location:**  https://archive.eclipse.org/tools/orbit/downloads/drops/R20130517111416/repository/ 
>
> **Selection:** All Orbit Bundles

> **Name:** Kepler R4
>
> **Location:**   https://build.vhtcx.com/downloads/kepler-equinox/repository/ 
>
> **Selection:** select all  (uncheck "Group items by category" under details  section)



#### Importing projects

From the Eclipse Menu: File > Import

From the Import Dialog: General>Existing Projects into Workspace

##### For OpenVXML

Browse to the location where the OpenVXML (GitHub\OpenVXML) repository was cloned: 

> Check **"Search for nested projects"** under **options**
>
> **Selection:** select all projects shown in the obtained list
>
> **Optional:** Place the imported projects into a working set called 'OpenVXML'

##### For VHT Toolkit

Browse to the location where the VXML-IVR (GitHub\VXML-IVR\com.vht.toolkit.root) repository was cloned:

> Check **"Search for nested projects"** under **options**
>
> **Selection:** select ***only*** <u>"com."</u> projects in the obtained list
>
> **Optional:** Place the imported projects into a working set called 'VHT Toolkit'



## Finally, to Run VXML-IVR

From the Eclipse Menu: Run > Run Configurations

In Run Configurations Window 

1. Create a new Eclipse Application

2. Name it as VXML-IVR

3. On the **Arguments tab** set the VM arguments to: 

   > -Dosgi.requiredJavaVersion=1.8 -XX:MaxPermSize=256m -Xmx4096m

6. Click Run

7. After the child eclipse launches go to **File > Import > General > "Existing Projects into Workspace"**

8. Import all the VXML-IVR projects **except "com."** projects

7. Change the Perspective (Window -> Open Perspective -> Other... -> OpenVXML -> Open)

8. Let child eclipse (VXML-IVR) build all the projects 

9. After completing build, uncheck automatic build option (Project -> Build Automatically)

You are now ready to build a war file

> Note: Setting of child eclipse is performed on the initial setup only. Once complete setup is up and running, you can just follow below steps to launch VXML-IVR
>
> From the Parent Eclipse Menu: Run History > VXML-IVR



## Development Support

**<u>Note: This is strictly for OpenVXML developers only</u>**

To fix bugs or to make any changes to OpenVXML Plugins, follow below steps to get them reflected on the VXML-IVR

To perform the build process, Maven need to be installed on your local machine

### Maven Installation

To install [Apache Maven](http://maven.apache.org/) on Windows, you just need to download the Maven’s zip file, unzip it to a folder, and configure the Windows environment variables. 

> **Mandatory:** Use ***Maven 3.5*** version to build the Tycho projects in VeX Framework 6.0
>
> **URL:** https://archive.apache.org/dist/maven/maven-3/3.5.0/binaries/  (Windows machine: bin.zip)
>
> **Unzip:** C:\apache-maven-3.5.0

1. Make sure JDK is installed, and `JAVA_HOME` environment variable is configured. 

> **JAVA_HOME:** C:\Program Files\Java\jdk1.8.0_221

2. Add a `MAVEN_HOME` system variables, and point it to the Maven folder 

> **MAVEN_HOME:** C:\apache-maven-3.5.0

3. Add %MAVEN_HOME%\bin To PATH
4.  Installation completed, to verify start a new command prompt, type `mvn –version`



### Development on OpenVXML

1. Close Child Eclipse (VXML-IVR)

2. Implement your planned changes to the required plugins and save your changes

3. Open command prompt and browse to "\OpenVXML" folder and run `mvn clean verify`

4. Wait for **<u>BUILD SUCCESS</u>** and then continue

5. You can find the new build at below specified path

   > \OpenVXML\\com.vht.openvxml.releng\com.vht.openvxml.update\target\repository\

6. Now, browse for pom.xml file located in

   > \VXML-IVR\com.vht.toolkit.root\com.vht.openvxml.releng\com.vht.openvxml.configuration\

7. Look for <repository> tag set with below details

   > <id>openvxml</id> and <url> http://build.vhtcx.com/vht/openvxml/6.0.0/repository/</url>

8. Replace <url> tag content with the new generated .p2 repository location with below specified syntax

   > For me, new OpenVXML build is generated at
   >
   > D:\GitHub\OpenVXML\com.vht.openvxml.releng\com.vht.openvxml.update\target\repository

   > So, my syntax will be as follows
   >
   > <url>file:/D:/GitHub/OpenVXML/com.vht.openvxml.releng/com.vht.openvxml.update/target/repository/</url>

9. Open command prompt browse for "GitHub\VXML-IVR\com.vht.toolkit.root\" folder

10. Run `mvn clean verify`

11. Wait for **<u>BUILD SUCCESS</u>** and then continue

12. Open child eclipse (VXML-IVR) created in <u>Finally, to run VXML-IVR</u> and clean build all the projects then changes made on OpenVXML will be reflected