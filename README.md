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