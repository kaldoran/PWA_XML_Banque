PWA_XML_Banque
==============

PWA &amp; XML project ( bank project ) 


### PWA Part 1:
[Spring Security Tutorial](http://www.mkyong.com/tutorials/spring-security-tutorials/)

### Pour avoir le programme qui marche sur votre pc faire :
```bash 
sudo mkdir -p /media/Public/{Work,Archive,Reject,Xsd,Out,In}
sudo chown -R kaldoran:users /media/Public/
```
Remplacer "kaldoran" par votre nom d'utilisateur linux.

Vous pouvez ensuite crée un lien symbolique vers le repertoire pour vous y faciliter l'acces.
```bash 
ln -s /media/Public/ ProjetXML
```
Ceci créra un lien symbolique dans le fichier ou je suis, ce lien aura pour nom "ProjetXML".
Un ls sur "ProjetXML" affichera le contenu de "/media/Public".


Vous pouvez également modifier les constantes de : BankSimulationConstants présent dans le package 
"com.ujm.xmltech.utils"

### Tuer Tomcat
Parfois tomcat ne s'arrete pas ( et ne veut pas se couper proprement ).
Plutôt que de faire un killall java, voici la commande

```bash 
lsof -i -P | grep "*:8084" | kill -9 $(awk {'print $2'})
```
Dépendances : lsof, grep, awk.

Cette commande tuera le processus java associé au serveur lancé sur le port 8084.
Si vous avez lancé votre tomcat sur un port différent changer le numéro de port.