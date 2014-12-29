PWA_XML_Banque
==============

PWA &amp; XML project ( bank project ) 


## PWA
### Part 1:
[Spring Security Tutorial](http://www.mkyong.com/tutorials/spring-security-tutorials/)


## Appendices

### To run the program
#### Method 1 (on linux)
```bash 
sudo mkdir -p /media/Public/{Work,Archive,Reject,Xsd,Out,In}
sudo chown -R <user name>:users /media/Public/
```

You can create a symbolic link to simplify the access to the directory:
```bash 
ln -s /media/Public/ ProjetXML
```
This will create a symbolic link, named "ProjetXML", in the current directory.
Use ls on "ProjetXML" will display the content of "/media/Public".

#### Method 2
You can also modify the directories constants in the java class "BankSimulationConstants" in the package "com.ujm.xmltech.utils".

### Kill Tomcat
Sometimes tomcat don't stop (et cannot be closed properly).
Rather than do a "killall java", which will also close Netbean (and all running java applications), you can use the command:

```bash 
lsof -i -P | grep "*:<tomcat port>" | kill -9 $(awk {'print $2'})
```
Dependencies : lsof, grep, awk.


This command will kill the java process associated to the server lauched on the port  ```<tomcat port>```.
